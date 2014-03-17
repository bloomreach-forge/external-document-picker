/**
 * Copyright 2014 Hippo B.V. (http://www.onehippo.com)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *         http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.onehippo.forge.exdocpicker.impl.field;

import java.util.Iterator;
import java.util.List;

import javax.jcr.Node;

import net.sf.json.JSONObject;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.RefreshingView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;
import org.hippoecm.frontend.dialog.AbstractDialog;
import org.hippoecm.frontend.dialog.DialogAction;
import org.hippoecm.frontend.dialog.IDialogFactory;
import org.hippoecm.frontend.model.JcrNodeModel;
import org.hippoecm.frontend.model.event.IObservable;
import org.hippoecm.frontend.model.event.IObserver;
import org.hippoecm.frontend.plugin.IPluginContext;
import org.hippoecm.frontend.plugin.config.IPluginConfig;
import org.hippoecm.frontend.plugins.standards.diff.LCS;
import org.hippoecm.frontend.plugins.standards.diff.LCS.Change;
import org.hippoecm.frontend.plugins.standards.diff.LCS.ChangeType;
import org.hippoecm.frontend.service.IEditor;
import org.hippoecm.frontend.service.render.RenderPlugin;
import org.onehippo.forge.exdocpicker.api.ExternalDocumentCollection;
import org.onehippo.forge.exdocpicker.api.ExternalDocumentService;
import org.onehippo.forge.exdocpicker.impl.SimpleExternalDocumentCollection;
import org.onehippo.forge.exdocpicker.impl.SimpleExternalDocumentCollectionDataProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExternalDocumentFieldSelectorPlugin extends RenderPlugin<Node> implements IObserver {

    private static final long serialVersionUID = 1L;

    private static final Logger log = LoggerFactory.getLogger(ExternalDocumentFieldSelectorPlugin.class);

    private JcrNodeModel documentModel;
    private ExternalDocumentService<JSONObject> exdocService;

    public ExternalDocumentFieldSelectorPlugin(final IPluginContext context, IPluginConfig config) {
        super(context, config);

        documentModel = (JcrNodeModel) getModel();

        exdocService = getExternalDocumentService();

        add(new Label("exdocfield-relateddocs-caption", getCaptionModel()));

        if (isEditMode() && documentModel != null) {
            context.registerService(new IObserver() {
                public IObservable getObservable() {
                    return documentModel;
                }
                public void onEvent(Iterator events) {
                    redraw();
                }
            }, IObserver.class.getName());
        }

        if (!isCompareMode()) {
            ExternalDocumentCollection<JSONObject> docCollection = exdocService.getCurrentDocuments(documentModel);
            add(createRefreshingView(docCollection));
        } else {
            ExternalDocumentCollection<JSONObject> docCollection = exdocService.getCurrentDocuments(documentModel);
            ExternalDocumentCollection<JSONObject> baseDocCollection = new SimpleExternalDocumentCollection<JSONObject>();
            add(createCompareView(docCollection, baseDocCollection));
        }

        IDialogFactory dialogFactory = createDialogFactory();
        final DialogAction action = new DialogAction(dialogFactory, getDialogService());

        AjaxLink<String> dialogLink = new AjaxLink<String>("browser-select") {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(AjaxRequestTarget target) {
                action.execute();
            }
        };

        dialogLink.add(new Label("link-text", new StringResourceModel("picker.browse", this, null)));
        dialogLink.setVisible(isEditMode());
        add(dialogLink);
    }

    protected boolean isEditMode() {
        return IEditor.Mode.EDIT.equals(IEditor.Mode.fromString(getPluginConfig().getString("mode", "view")));
    }

    protected boolean isCompareMode() {
        return IEditor.Mode.COMPARE.equals(IEditor.Mode.fromString(getPluginConfig().getString("mode", "view")));
    }

    protected ExternalDocumentService<JSONObject> getExternalDocumentService() {
        ExternalDocumentService<JSONObject> service = null;
        String serviceClassName = null;

        try {
            serviceClassName = getPluginConfig().getString("external.document.service.class");
            Class<? extends ExternalDocumentService> serviceClass = 
                    (Class<? extends ExternalDocumentService>) Class.forName(serviceClassName);
            service = serviceClass.newInstance();
        } catch (Exception e) {
            log.error("Failed to create external document service from class name, '{}'.", serviceClassName, e);
        }

        return service;
    }

    protected IModel<String> getCaptionModel() {
        final String defaultCaption = new StringResourceModel("exdocfield-docs", this, null, "Related external documents").getString();
        String caption = getPluginConfig().getString("caption", defaultCaption);
        String captionKey = caption;
        return new StringResourceModel(captionKey, this, null, caption);
    }

    protected RefreshingView<JSONObject> createRefreshingView(final ExternalDocumentCollection<JSONObject> docCollection) {

        return new RefreshingView<JSONObject>("view") {

            private static final long serialVersionUID = 1L;

            private IDataProvider<JSONObject> dataProvider = new SimpleExternalDocumentCollectionDataProvider<JSONObject>(docCollection);

            @Override
            protected Iterator getItemModels() {

                final Iterator<? extends JSONObject> baseIt = dataProvider.iterator(0, 0);

                return new Iterator<IModel<JSONObject>>() {
                    public boolean hasNext() {
                        return baseIt.hasNext();
                    }

                    public IModel<JSONObject> next() {
                        return dataProvider.model(baseIt.next());
                    }

                    public void remove() {
                        baseIt.remove();
                    }
                };
            }

            @Override
            protected void populateItem(Item item) {
                final JSONObject doc = (JSONObject) item.getModelObject();

                item.add(new Label("link-text", new PropertyModel(doc, "title")));

                if (item.getIndex() == docCollection.size() - 1) {
                    item.add(new AttributeAppender("class", new Model("last"), " "));
                }

                AjaxLink deleteLink = new AjaxLink("delete") {

                    private static final long serialVersionUID = 1L;

                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        docCollection.remove(doc);
                    }
                };

                if (!isEditMode()) {
                    deleteLink.setVisible(false);
                }

                item.add(deleteLink);
            }
        };
    }

    private RefreshingView createCompareView(final ExternalDocumentCollection<JSONObject> docCollection, final ExternalDocumentCollection<JSONObject> baseDocCollection) {

        return new RefreshingView("view") {

            @Override
            protected Iterator getItemModels() {
                JSONObject [] baseDocs = baseDocCollection.toArray(new JSONObject[baseDocCollection.size()]);
                JSONObject [] currentDocs = docCollection.toArray(new JSONObject[docCollection.size()]);

                List<Change<JSONObject>> changeSet = LCS.getChangeSet(baseDocs, currentDocs);
                final Iterator<Change<JSONObject>> upstream = changeSet.iterator();
                return new Iterator<IModel<?>>() {

                    public boolean hasNext() {
                        return upstream.hasNext();
                    }

                    public IModel<?> next() {
                        final Change<JSONObject> change = upstream.next();
                        return new IModel() {
                            private static final long serialVersionUID = 1L;

                            public Object getObject() {
                                return change;
                            }

                            public void setObject(Object object) {
                                throw new UnsupportedOperationException();
                            }

                            public void detach() {
                            }
                        };
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }

                };
            }

            @Override
            protected void populateItem(Item item) {
                Change<JSONObject> change = (Change<JSONObject>) item.getModelObject();
                final JSONObject searchDoc = change.getValue();

                Label label = new Label("link-text", new PropertyModel(searchDoc, "title"));

                if (change.getType() == ChangeType.ADDED) {
                    label.add(new AttributeAppender("class", new Model("hippo-diff-added"), " "));
                } else if (change.getType() == ChangeType.REMOVED) {
                    label.add(new AttributeAppender("class", new Model("hippo-diff-removed"), " "));
                }

                item.add(label);

                AjaxLink deleteLink = new AjaxLink("delete") {

                    private static final long serialVersionUID = 1L;

                    @Override
                    public void onClick(AjaxRequestTarget target) {
                    }
                };

                deleteLink.setVisible(false);
                item.add(deleteLink);
            }
        };
    }

    protected IDialogFactory createDialogFactory() {
        return new IDialogFactory() {
            private static final long serialVersionUID = 1L;

            public AbstractDialog<ExternalDocumentCollection<JSONObject>> createDialog() {
                ExternalDocumentCollection<JSONObject> docCollection = null;
                return new ExternalDocumentFieldBrowserDialog(getPluginContext(), getPluginConfig(), exdocService, documentModel, new Model(docCollection));
            }
        };
    }

}

