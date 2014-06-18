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

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import javax.jcr.Node;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.RefreshingView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.request.resource.ResourceReference;
import org.hippoecm.frontend.dialog.AbstractDialog;
import org.hippoecm.frontend.dialog.DialogLink;
import org.hippoecm.frontend.dialog.IDialogFactory;
import org.hippoecm.frontend.model.IModelReference;
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
import org.onehippo.forge.exdocpicker.api.ExternalDocumentServiceContext;
import org.onehippo.forge.exdocpicker.api.ExternalDocumentServiceFacade;
import org.onehippo.forge.exdocpicker.api.PluginConstants;
import org.onehippo.forge.exdocpicker.impl.SimpleExternalDocumentCollectionDataProvider;
import org.onehippo.forge.exdocpicker.impl.SimpleExternalDocumentServiceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExternalDocumentFieldSelectorPlugin extends RenderPlugin<Node> implements IObserver {

    private static final long serialVersionUID = 1L;

    private static final Logger log = LoggerFactory.getLogger(ExternalDocumentFieldSelectorPlugin.class);

    private static final ResourceReference DELETE_ICON_REF = new PackageResourceReference(ExternalDocumentFieldSelectorPlugin.class, "delete-small-16.png");

    private JcrNodeModel documentModel;
    private ExternalDocumentServiceFacade<Serializable> exdocService;
    private ExternalDocumentCollection<Serializable> curDocCollection;

    private final ExternalDocumentServiceContext extDocServiceContext;

    public ExternalDocumentFieldSelectorPlugin(final IPluginContext context, IPluginConfig config) {
        super(context, config);

        setOutputMarkupId(true);

        documentModel = (JcrNodeModel) getModel();

        exdocService = (ExternalDocumentServiceFacade<Serializable>) getExternalDocumentService();

        extDocServiceContext = new SimpleExternalDocumentServiceContext(config, context, documentModel);

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

        curDocCollection = exdocService.getFieldExternalDocuments(extDocServiceContext);

        if (!isCompareMode()) {
            add(createRefreshingView(curDocCollection));
        } else {
            if (!getPluginConfig().containsKey("model.compareTo")) {
                log.warn("no base model service configured");
            } else {
                JcrNodeModel compareBaseDocumentModel = null;
                IModelReference compareBaseRef = getPluginContext().getService(getPluginConfig().getString("model.compareTo"), IModelReference.class);

                if (compareBaseRef == null) {
                    log.warn("no base model service available");
                } else {
                    compareBaseDocumentModel = new JcrNodeModel(new StringBuilder()
                            .append(((JcrNodeModel) compareBaseRef.getModel()).getItemModel().getPath())
                            .toString());
                    ExternalDocumentServiceContext comparingContext = new SimpleExternalDocumentServiceContext(getPluginConfig(), getPluginContext(), compareBaseDocumentModel);
                    ExternalDocumentCollection<Serializable> baseDocCollection = exdocService.getFieldExternalDocuments(comparingContext);
                    add(createCompareView(curDocCollection, baseDocCollection));
                }
            }
        }

        // Browse button
        DialogLink browseButton = new DialogLink("browse-button", new StringResourceModel("picker.browse", this, null),
                createDialogFactory(), getDialogService());

        browseButton.setVisible(isEditMode());
        add(browseButton);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(CssHeaderItem.forReference(new PackageResourceReference(ExternalDocumentFieldSelectorPlugin.class, ExternalDocumentFieldSelectorPlugin.class.getSimpleName() + ".css")));
    }

    protected boolean isEditMode() {
        return IEditor.Mode.EDIT.equals(IEditor.Mode.fromString(getPluginConfig().getString("mode", "view")));
    }

    protected boolean isCompareMode() {
        return IEditor.Mode.COMPARE.equals(IEditor.Mode.fromString(getPluginConfig().getString("mode", "view")));
    }

    protected ExternalDocumentServiceFacade<? extends Serializable> getExternalDocumentService() {
        ExternalDocumentServiceFacade<? extends Serializable> service = null;
        String serviceFacadeClassName = null;

        try {
            serviceFacadeClassName = getPluginConfig().getString(PluginConstants.PARAM_EXTERNAL_DOCUMENT_SERVICE_FACADE);
            Class<? extends ExternalDocumentServiceFacade> serviceClass = 
                    (Class<? extends ExternalDocumentServiceFacade>) Class.forName(serviceFacadeClassName);
            service = serviceClass.newInstance();
        } catch (Exception e) {
            log.error("Failed to create external document service facade from class name, '{}'.", serviceFacadeClassName, e);
        }

        return service;
    }

    protected IModel<String> getCaptionModel() {
        final String defaultCaption = new StringResourceModel("exdocfield.caption", this, null, PluginConstants.DEFAULT_FIELD_CAPTION).getString();
        String caption = getPluginConfig().getString("caption", defaultCaption);
        String captionKey = caption;
        return new StringResourceModel(captionKey, this, null, caption);
    }

    private RefreshingView<? extends Serializable> createRefreshingView(final ExternalDocumentCollection<Serializable> docCollection) {

        return new RefreshingView<Serializable>("view") {

            private static final long serialVersionUID = 1L;

            private IDataProvider<Serializable> dataProvider = new SimpleExternalDocumentCollectionDataProvider(docCollection);

            @Override
            protected Iterator getItemModels() {

                final Iterator<? extends Serializable> baseIt = dataProvider.iterator(0, docCollection.getSize());

                return new Iterator<IModel<? extends Serializable>>() {
                    public boolean hasNext() {
                        return baseIt.hasNext();
                    }

                    public IModel<? extends Serializable> next() {
                        return dataProvider.model(baseIt.next());
                    }

                    public void remove() {
                        baseIt.remove();
                    }
                };
            }

            @Override
            protected void populateItem(Item item) {
                final Serializable doc = (Serializable) item.getModelObject();

                item.add(new Label("link-text", new Model<String>() {
                    private static final long serialVersionUID = 1L;
                    @Override
                    public String getObject() {
                        return exdocService.getDocumentTitle(extDocServiceContext, doc, getRequest().getLocale());
                    }
                }));

                if (item.getIndex() == docCollection.getSize() - 1) {
                    item.add(new AttributeAppender("class", new Model("last"), " "));
                }

                AjaxLink deleteLink = new AjaxLink("delete") {

                    private static final long serialVersionUID = 1L;

                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        boolean removed = docCollection.remove(doc);

                        if (removed) {
                            exdocService.setFieldExternalDocuments(extDocServiceContext, docCollection);
                            target.add(ExternalDocumentFieldSelectorPlugin.this);
                        }
                    }
                };

                final Image deleteImage = new Image("delete-image") {
                    private static final long serialVersionUID = 1L;
                };
                deleteImage.setImageResourceReference(DELETE_ICON_REF, null);
                deleteLink.add(deleteImage);

                if (!isEditMode()) {
                    deleteLink.setVisible(false);
                }

                item.add(deleteLink);
            }
        };
    }

    private RefreshingView createCompareView(final ExternalDocumentCollection<Serializable> docCollection, final ExternalDocumentCollection<Serializable> baseDocCollection) {

        return new RefreshingView("view") {

            @Override
            protected Iterator getItemModels() {
                Serializable [] baseDocs = baseDocCollection.toArray(new Serializable[baseDocCollection.getSize()]);
                Serializable [] currentDocs = docCollection.toArray(new Serializable[docCollection.getSize()]);

                List<Change<Serializable>> changeSet = LCS.getChangeSet(baseDocs, currentDocs);
                final Change<Serializable> [] changes = changeSet.toArray(new Change[changeSet.size()]);

                return new Iterator<IModel<Change<? extends Serializable>>>() {

                    private int changeSetIndex = 0;

                    public boolean hasNext() {
                        return changeSetIndex < changes.length;
                    }

                    public IModel<Change<? extends Serializable>> next() {

                        if (changeSetIndex >= changes.length) {
                            throw new NoSuchElementException();
                        }

                        final Change<? extends Serializable> change = changes[changeSetIndex++];

                        return new Model<Change<? extends Serializable>>(change);
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }

            @Override
            protected void populateItem(Item item) {
                Change<? extends Serializable> change = (Change<? extends Serializable>) item.getModelObject();
                final Serializable searchDoc = change.getValue();

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

            public AbstractDialog<ExternalDocumentCollection<Serializable>> createDialog() {
                return new ExternalDocumentFieldBrowserDialog(getCaptionModel(), extDocServiceContext, exdocService, new Model(curDocCollection));
            }
        };
    }

}

