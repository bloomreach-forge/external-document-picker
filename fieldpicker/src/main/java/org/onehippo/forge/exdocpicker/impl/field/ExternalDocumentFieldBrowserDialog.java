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
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ResourceReference;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.PackageResourceReference;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.resources.CompressedResourceReference;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.util.value.IValueMap;
import org.apache.wicket.util.value.ValueMap;
import org.hippoecm.frontend.dialog.AbstractDialog;
import org.hippoecm.frontend.plugin.IPluginContext;
import org.hippoecm.frontend.plugin.config.IPluginConfig;
import org.onehippo.forge.exdocpicker.api.ExternalDocumentCollection;
import org.onehippo.forge.exdocpicker.api.ExternalDocumentServiceContext;
import org.onehippo.forge.exdocpicker.api.ExternalDocumentServiceFacade;
import org.onehippo.forge.exdocpicker.api.PluginConstants;
import org.onehippo.forge.exdocpicker.impl.SimpleExternalDocumentCollection;
import org.onehippo.forge.exdocpicker.impl.SimpleExternalDocumentCollectionDataProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExternalDocumentFieldBrowserDialog extends AbstractDialog<ExternalDocumentCollection<Serializable>> {

    private static final long serialVersionUID = 1L;

    private static Logger log = LoggerFactory.getLogger(ExternalDocumentFieldBrowserDialog.class);

    private static final ResourceReference CSS_RESOURCE = new CompressedResourceReference(ExternalDocumentFieldBrowserDialog.class, ExternalDocumentFieldBrowserDialog.class.getSimpleName() + ".css");

    private String searchQuery;

    private final IModel<String> titleModel;
    private final ExternalDocumentServiceFacade<Serializable> exdocService;

    private final List<Serializable> selectedExtDocs = new LinkedList<Serializable>();

    private ExternalDocumentCollection<Serializable> currentDocSelection;
    private ExternalDocumentCollection<Serializable> searchedDocCollection = new SimpleExternalDocumentCollection<Serializable>();

    private int pageSize;

    private final IValueMap dialogSize;

    private final boolean initialSearchEnabled;

    private final ExternalDocumentServiceContext extDocServiceContext;

    public ExternalDocumentFieldBrowserDialog(IModel<String> titleModel, final ExternalDocumentServiceContext extDocServiceContext, final ExternalDocumentServiceFacade<Serializable> exdocService, IModel<ExternalDocumentCollection<Serializable>> model) {
        super(model);
        setOutputMarkupId(true);

        this.titleModel = titleModel;
        this.extDocServiceContext = extDocServiceContext;
        this.exdocService = exdocService;

        initialSearchEnabled = getPluginConfig().getAsBoolean(PluginConstants.PARAM_INITIAL_SEARCH_ENABLED, PluginConstants.DEFAULT_INITIAL_SEARCH_ENABLED);

        searchQuery = getPluginConfig().getString(PluginConstants.PARAM_INITIAL_SEARCH_QUERY, "");

        final String dialogSizeParam = getPluginConfig().getString(PluginConstants.PARAM_DIALOG_SIZE, PluginConstants.DEFAULT_DIALOG_SIZE);
        dialogSize = new ValueMap(dialogSizeParam).makeImmutable();

        pageSize = getPluginConfig().getInt(PluginConstants.PARAM_PAGE_SIZE, PluginConstants.DEFAULT_PAGE_SIZE);

        currentDocSelection = getModelObject();

        final TextField<String> searchText = new TextField<String>("search-input", new PropertyModel<String>(this, "searchQuery"));
        searchText.setOutputMarkupId(true);
        add(setFocus(searchText));

        //Search button
        AjaxButton searchButton = new AjaxButton("search-button", new StringResourceModel("search-label", this, null)) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onSubmit(AjaxRequestTarget ajaxRequestTarget, Form<?> form) {
                searchExternalDocumentsBySearchQuery();
                ajaxRequestTarget.addComponent(ExternalDocumentFieldBrowserDialog.this);
            }
        };
        add(searchButton);

        if (getModel().getObject() == null) {
            setOkVisible(false);
            setOkEnabled(false);
        }

        // initially search all
        if (initialSearchEnabled) {
            searchExternalDocumentsBySearchQuery();
        }

        IDataProvider<Serializable> provider = new SimpleExternalDocumentCollectionDataProvider<Serializable>(searchedDocCollection);

        DataView<Serializable> resultsDataView = new DataView<Serializable>("item", provider, pageSize) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(final Item<Serializable> listItem) {
                final Serializable doc = listItem.getModelObject();
                listItem.setOutputMarkupId(true);

                AjaxCheckBox selectCheckbox = new AjaxCheckBox("select-button", new Model<Boolean>()) {
                    private static final long serialVersionUID = 1L;

                    @Override
                    protected void onUpdate(AjaxRequestTarget ajaxRequestTarget) {
                        if (getModelObject()) {
                            selectedExtDocs.add(doc);

                            if (isSingleSelectionMode()) {
                                ExternalDocumentFieldBrowserDialog.this.handleSubmit();
                            }
                        } else {
                            selectedExtDocs.remove(doc);
                        }
                    }
                };

                if (currentDocSelection.contains(doc)) {
                    selectCheckbox.getModel().setObject(true);
                }

                final String iconLink = exdocService.getDocumentIconLink(extDocServiceContext, doc, getRequest().getLocale());
                final ExternalDocumentIconImage iconImage = new ExternalDocumentIconImage("image", iconLink);
                listItem.add(iconImage);
                listItem.add(selectCheckbox);
                listItem.add(new Label("title-label", exdocService.getDocumentTitle(extDocServiceContext, doc, getRequest().getLocale())));
                final String description = exdocService.getDocumentDescription(extDocServiceContext, doc, getRequest().getLocale());

                WebMarkupContainer frame = new WebMarkupContainer("paragraph-label") {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public void onComponentTagBody(final MarkupStream markupStream, final ComponentTag openTag) {
                        replaceComponentTagBody(markupStream, openTag, StringUtils.defaultString(description));
                    }
                };

                listItem.add(frame);

                listItem.add(new AttributeAppender("class", new AbstractReadOnlyModel() {
                    private static final long serialVersionUID = 1L;

                    public Object getObject() {
                        return ((listItem.getIndex() & 1) == 1) ? "even" : "odd";
                    }
                }, " "));
            }
        };

        add(resultsDataView);
        add(new ExternalDocumentFieldBrowserPageNavigator("navigator", resultsDataView));
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.renderCSSReference(CSS_RESOURCE);
    }

    @Override
    protected void onOk() {
        if (selectedExtDocs != null) {
            if (isSingleSelectionMode()) {
                if (!selectedExtDocs.isEmpty()) {
                    currentDocSelection.clear();
                    currentDocSelection.add(selectedExtDocs.iterator().next());
                    exdocService.setFieldExternalDocuments(extDocServiceContext, currentDocSelection);
                }
            } else {
                boolean added = false;

                for (Serializable doc : selectedExtDocs) {
                    if (!currentDocSelection.contains(doc)) {
                        currentDocSelection.add(doc);
                        added = true;
                    }
                }

                if (added) {
                    exdocService.setFieldExternalDocuments(extDocServiceContext, currentDocSelection);
                }
            }
        }
    }

    @Override
    public IModel<String> getTitle() {
        return titleModel;
    }

    @Override
    public IValueMap getProperties() {
        return dialogSize;
    }

    protected IPluginConfig getPluginConfig() {
        return extDocServiceContext.getPluginConfig();
    }

    protected IPluginContext getPluginContext() {
        return extDocServiceContext.getPluginContext();
    }

    protected boolean isSingleSelectionMode() {
        return StringUtils.equalsIgnoreCase(PluginConstants.SELECTION_MODE_SINGLE, getPluginConfig().getString(PluginConstants.PARAM_SELECTION_MODE, PluginConstants.SELECTION_MODE_MULTIPLE));
    }

    public String getSearchQuery() {
        return searchQuery;
    }

    public void setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
    }

    protected void searchExternalDocumentsBySearchQuery() {
        try {
            ExternalDocumentCollection<? extends Serializable> searchedDocs = exdocService.searchExternalDocuments(extDocServiceContext, getSearchQuery());

            searchedDocCollection.clear();

            if (searchedDocs == null || searchedDocs.getSize() == 0) {
                return;
            }

            for (Iterator<? extends Serializable> it = searchedDocs.iterator(); it.hasNext(); ) {
                searchedDocCollection.add(it.next());
            }
        } catch (Exception e) {
            log.error("Failed to execute search external documents by the search query, '" + getSearchQuery() + "'.", e);
        }
    }

    public static class ExternalDocumentIconImage extends Image {

        private static final ResourceReference NO_ICON = new PackageResourceReference(ExternalDocumentFieldBrowserDialog.class, "no-icon.jpg");

        private static final long serialVersionUID = 1L;

        public ExternalDocumentIconImage(String id, String imageUrl) {
            super(id);

            if (StringUtils.isBlank(imageUrl)) {
                this.setImageResourceReference(NO_ICON, null);
            } else {
                add(new AttributeModifier("src", true, new Model(imageUrl)));
            }
        }
    }
}

