/**
 * Copyright 2014-2017 Hippo B.V. (http://www.onehippo.com)
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

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.RefreshingView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.resource.PackageResourceReference;
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
import org.hippoecm.frontend.plugins.standards.icon.HippoIcon;
import org.hippoecm.frontend.service.IEditor;
import org.hippoecm.frontend.service.render.RenderPlugin;
import org.hippoecm.frontend.skin.Icon;
import org.onehippo.forge.exdocpicker.api.ExternalDocumentCollection;
import org.onehippo.forge.exdocpicker.api.ExternalDocumentServiceContext;
import org.onehippo.forge.exdocpicker.api.ExternalDocumentServiceFacade;
import org.onehippo.forge.exdocpicker.api.PluginConstants;
import org.onehippo.forge.exdocpicker.impl.SimpleExternalDocumentCollectionDataProvider;
import org.onehippo.forge.exdocpicker.impl.SimpleExternalDocumentServiceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * External document(s) selector field plugin.
 */
public class ExternalDocumentFieldSelectorPlugin extends RenderPlugin<Node> implements IObserver {

    private static final long serialVersionUID = 1L;

    private static final Logger log = LoggerFactory.getLogger(ExternalDocumentFieldSelectorPlugin.class);

    private JcrNodeModel documentModel;

    private ExternalDocumentServiceFacade<Serializable> exdocService;
    private ExternalDocumentCollection<Serializable> curDocCollection;
    private ExternalDocumentServiceContext extDocServiceContext;

    /**
     * Constructs external document(s) selector field plugin.
     * @param context plugin context
     * @param config plugin config
     */
    public ExternalDocumentFieldSelectorPlugin(final IPluginContext context, IPluginConfig config) {
        super(context, config);

        setOutputMarkupId(true);

        documentModel = (JcrNodeModel)getModel();

        setExternalDocumentServiceFacade((ExternalDocumentServiceFacade<Serializable>) createExternalDocumentService());
        setExternalDocumentServiceContext(new SimpleExternalDocumentServiceContext(this, config, context,
                                                                                   documentModel));

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

        MarkupContainer exdocsContainer = new WebMarkupContainer("exdocs-container");

        boolean exdocsContainerVisible = getPluginConfig()
            .getAsBoolean(PluginConstants.PARAM_EXTERNAL_DOCUMENTS_CONTAINER_VISIBLE, true);

        exdocsContainer.setVisible(exdocsContainerVisible);

        if (exdocsContainerVisible) {
            if (!isCompareMode()) {
                exdocsContainer.add(createRefreshingView(getCurrentExternalDocumentCollection()));
            } else {
                if (!getPluginConfig().containsKey("model.compareTo")) {
                    log.warn("no base model service configured");
                } else {
                    JcrNodeModel compareBaseDocumentModel = null;
                    IModelReference compareBaseRef = getPluginContext()
                        .getService(getPluginConfig().getString("model.compareTo"), IModelReference.class);

                    if (compareBaseRef == null) {
                        log.warn("no base model service available");
                    } else {
                        compareBaseDocumentModel = new JcrNodeModel(new StringBuilder()
                            .append(((JcrNodeModel)compareBaseRef.getModel()).getItemModel().getPath())
                            .toString());
                        ExternalDocumentServiceContext comparingContext =
                            new SimpleExternalDocumentServiceContext(this,
                                                                     getPluginConfig(),
                                                                     getPluginContext(),
                                                                     compareBaseDocumentModel);
                        ExternalDocumentCollection<Serializable> baseDocCollection = getExternalDocumentServiceFacade()
                            .getFieldExternalDocuments(comparingContext);
                        exdocsContainer.add(createCompareView(getCurrentExternalDocumentCollection(),
                                                              baseDocCollection));
                    }
                }
            }
        }

        add(exdocsContainer);

        WebMarkupContainer actionButtonsContainer = new WebMarkupContainer("action-buttons");
        // Browse button
        DialogLink browseButton = new DialogLink("browse-button", new StringResourceModel("picker.browse",
                                                                                          this, null),
                                                 createDialogFactory(), getDialogService());
        actionButtonsContainer.add(browseButton);

        actionButtonsContainer.setVisible(isEditMode());
        add(actionButtonsContainer);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(CssHeaderItem
            .forReference(new PackageResourceReference(ExternalDocumentFieldSelectorPlugin.class,
                                                       ExternalDocumentFieldSelectorPlugin.class
                                                           .getSimpleName() + ".css")));
    }

    /**
     * Returns true if it is currently in edit mode in the document editor.
     * @return true if it is currently in edit mode in the document editor
     */
    protected boolean isEditMode() {
        return IEditor.Mode.EDIT.equals(IEditor.Mode.fromString(getPluginConfig().getString("mode", "view")));
    }

    /**
     * Returns true if it is currently in compare mode in the document editor.
     * @return true if it is currently in compare mode in the document editor
     */
    protected boolean isCompareMode() {
        return IEditor.Mode.COMPARE.equals(IEditor.Mode.fromString(getPluginConfig()
            .getString("mode", "view")));
    }

    /**
     * Creates a new {@link ExternalDocumentServiceFacade} instance.
     * By default, this method reads the plugin configuration by the name, {@link PluginConstants#PARAM_EXTERNAL_DOCUMENT_SERVICE_FACADE},
     * and instantiated an object by the FQCN configuration parameter.
     * @return a new {@link ExternalDocumentServiceFacade} instance
     */
    protected ExternalDocumentServiceFacade<? extends Serializable> createExternalDocumentService() {
        ExternalDocumentServiceFacade<? extends Serializable> service = null;
        String serviceFacadeClassName = null;

        try {
            serviceFacadeClassName = getPluginConfig()
                .getString(PluginConstants.PARAM_EXTERNAL_DOCUMENT_SERVICE_FACADE);
            Class<? extends ExternalDocumentServiceFacade> serviceClass = (Class<? extends ExternalDocumentServiceFacade>)Class
                .forName(serviceFacadeClassName);
            service = serviceClass.newInstance();
        } catch (Exception e) {
            log.error("Failed to create external document service facade from class name, '{}'.",
                      serviceFacadeClassName, e);
        }

        return service;
    }

    /**
     * Returns the field caption label.
     * @return the field caption label
     */
    protected IModel<String> getCaptionModel() {
        final String defaultCaption = new StringResourceModel("exdocfield.caption", this, null)
                .setDefaultValue(PluginConstants.DEFAULT_FIELD_CAPTION).getString();
        String caption = getPluginConfig().getString("caption", defaultCaption);
        String captionKey = caption;
        return new StringResourceModel(captionKey, this, null).setDefaultValue(caption);
    }

    private RefreshingView<? extends Serializable> createRefreshingView(final ExternalDocumentCollection<Serializable> docCollection) {

        return new RefreshingView<Serializable>("view") {

            private static final long serialVersionUID = 1L;

            private IDataProvider<Serializable> dataProvider =
                new SimpleExternalDocumentCollectionDataProvider(docCollection);

            @Override
            protected Iterator getItemModels() {

                final Iterator<? extends Serializable> baseIt = dataProvider
                    .iterator(0, docCollection.getSize());

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
                final Serializable doc = (Serializable)item.getModelObject();

                final Label label = new Label("link-text", new Model<String>() {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public String getObject() {
                        return getExternalDocumentServiceFacade()
                            .getDocumentTitle(getExternalDocumentServiceContext(), doc,
                                              getRequest().getLocale());
                    }
                });
                final String description = getExternalDocumentServiceFacade()
                        .getDocumentDescription(getExternalDocumentServiceContext(), doc, getRequest().getLocale());
                if (StringUtils.isNotBlank(description)) {
                    label.add(new AttributeModifier("title", description));
                }
                item.add(label);

                if (item.getIndex() == docCollection.getSize() - 1) {
                    item.add(new AttributeAppender("class", new Model("last"), " "));
                }

                addControlsToListItem(docCollection, item);
            }
        };
    }

    private RefreshingView createCompareView(final ExternalDocumentCollection<Serializable> docCollection,
                                             final ExternalDocumentCollection<Serializable> baseDocCollection) {

        return new RefreshingView("view") {

            @Override
            protected Iterator getItemModels() {
                Serializable[] baseDocs = baseDocCollection.toArray(new Serializable[baseDocCollection
                    .getSize()]);
                Serializable[] currentDocs = docCollection.toArray(new Serializable[docCollection.getSize()]);

                List<Change<Serializable>> changeSet = LCS.getChangeSet(baseDocs, currentDocs);
                final Change<Serializable>[] changes = changeSet.toArray(new Change[changeSet.size()]);

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
                Change<? extends Serializable> change = (Change<? extends Serializable>)item.getModelObject();
                final Serializable searchDoc = change.getValue();

                final Label label = new Label("link-text", new Model<String>() {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public String getObject() {
                        return getExternalDocumentServiceFacade()
                            .getDocumentTitle(getExternalDocumentServiceContext(), searchDoc,
                                              getRequest().getLocale());
                    }
                });

                final String description = getExternalDocumentServiceFacade().getDocumentDescription(
                        getExternalDocumentServiceContext(), searchDoc, getRequest().getLocale());
                if (StringUtils.isNotBlank(description)) {
                    label.add(new AttributeModifier("title", description));
                }

                if (change.getType() == ChangeType.ADDED) {
                    label.add(new AttributeAppender("class", new Model("hippo-diff-added"), " "));
                } else if (change.getType() == ChangeType.REMOVED) {
                    label.add(new AttributeAppender("class", new Model("hippo-diff-removed"), " "));
                }

                item.add(label);

                addControlsToListItem(docCollection, item);
            }
        };
    }

    /**
     * Returns the {@link ExternalDocumentServiceFacade} instance used by this plugin instance.
     * @return the {@link ExternalDocumentServiceFacade} instance used by this plugin instance
     */
    protected ExternalDocumentServiceFacade<Serializable> getExternalDocumentServiceFacade() {
        return exdocService;
    }

    /**
     * Sets the {@link ExternalDocumentServiceFacade} instance used by this plugin instance.
     * @param exdocService the {@link ExternalDocumentServiceFacade} instance used by this plugin instance
     */
    protected void setExternalDocumentServiceFacade(ExternalDocumentServiceFacade<Serializable> exdocService) {
        this.exdocService = exdocService;
    }

    /**
     * Returns the collection of the currently-selected external documents in the document.
     * @return the collection of the currently-selected external documents in the document
     */
    protected ExternalDocumentCollection<Serializable> getCurrentExternalDocumentCollection() {
        if (curDocCollection == null) {
            curDocCollection = getExternalDocumentServiceFacade()
                .getFieldExternalDocuments(getExternalDocumentServiceContext());
        }

        return curDocCollection;
    }

    /**
     * Sets the collection of the currently-selected external documents in the document.
     * @param curDocCollection the collection of the currently-selected external documents in the document
     */
    protected void setCurrentExternalDocumentCollection(ExternalDocumentCollection<Serializable> curDocCollection) {
        this.curDocCollection = curDocCollection;
    }

    /**
     * Returns the {@link ExternalDocumentServiceContext} instance.
     * @return the {@link ExternalDocumentServiceContext} instance
     */
    protected ExternalDocumentServiceContext getExternalDocumentServiceContext() {
        return extDocServiceContext;
    }

    /**
     * Sets the {@link ExternalDocumentServiceContext} instance.
     * @param extDocServiceContext the {@link ExternalDocumentServiceContext} instance
     */
    protected void setExternalDocumentServiceContext(ExternalDocumentServiceContext extDocServiceContext) {
        this.extDocServiceContext = extDocServiceContext;
    }

    /**
     * Creates a picker dialog with which user can select external documents.
     * @return a picker dialog with which user can select external documents
     */
    protected AbstractDialog<ExternalDocumentCollection<Serializable>> createDialogInstance() {
        return new TextSearchExternalDocumentFieldBrowserDialog(
                getCaptionModel(),
                getExternalDocumentServiceContext(),
                getExternalDocumentServiceFacade(),
                new Model<ExternalDocumentCollection<Serializable>>(getCurrentExternalDocumentCollection()));
    }

    /**
     * Creates a dialog factory to create a picker dialog.
     * @return a dialog factory to create a picker dialog
     */
    protected IDialogFactory createDialogFactory() {
        return new IDialogFactory() {
            private static final long serialVersionUID = 1L;

            public AbstractDialog<ExternalDocumentCollection<Serializable>> createDialog() {
                return createDialogInstance();
            }
        };
    }

    private void addControlsToListItem(final ExternalDocumentCollection<Serializable> docCollection, final ListItem<?> item) {
        final Serializable doc = (Serializable)item.getModelObject();

        final boolean isEditMode = isEditMode();
        final int itemCount = docCollection.getSize();
        final int itemIndex = item.getIndex();

        final WebMarkupContainer controls = new WebMarkupContainer("controls");
        controls.setVisible(isEditMode);

        final MarkupContainer upLink = new AjaxLink("up") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                boolean removed = docCollection.remove(doc);
                if (removed) {
                    docCollection.add(itemIndex - 1, doc);
                    exdocService.setFieldExternalDocuments(extDocServiceContext, docCollection);
                    target.add(ExternalDocumentFieldSelectorPlugin.this);
                }
            }
        };
        upLink.setEnabled(isEditMode && itemIndex > 0);
        upLink.setVisible(isEditMode);
        final HippoIcon upIcon = HippoIcon.fromSprite("up-icon", Icon.ARROW_UP);
        upLink.add(upIcon);
        controls.add(upLink);

        final MarkupContainer downLink = new AjaxLink("down") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                boolean removed = docCollection.remove(doc);
                if (removed) {
                    docCollection.add(itemIndex + 1, doc);
                    exdocService.setFieldExternalDocuments(extDocServiceContext, docCollection);
                    target.add(ExternalDocumentFieldSelectorPlugin.this);
                }
            }
        };
        downLink.setEnabled(isEditMode && itemIndex < itemCount - 1);
        downLink.setVisible(isEditMode);
        final HippoIcon downIcon = HippoIcon.fromSprite("down-icon", Icon.ARROW_DOWN);
        downLink.add(downIcon);
        controls.add(downLink);

        final MarkupContainer removeLink = new AjaxLink("remove") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                boolean removed = docCollection.remove(doc);
                if (removed) {
                    exdocService.setFieldExternalDocuments(extDocServiceContext, docCollection);
                    target.add(ExternalDocumentFieldSelectorPlugin.this);
                }
            }
        };
        removeLink.setEnabled(isEditMode);
        removeLink.setVisible(isEditMode);
        final HippoIcon removeIcon = HippoIcon.fromSprite("remove-icon", Icon.TIMES);
        removeLink.add(removeIcon);
        controls.add(removeLink);

        item.add(controls);
    }
}
