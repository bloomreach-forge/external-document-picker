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

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.request.resource.ResourceReference;
import org.onehippo.forge.exdocpicker.api.ExternalDocumentCollection;
import org.onehippo.forge.exdocpicker.api.ExternalDocumentServiceContext;
import org.onehippo.forge.exdocpicker.api.ExternalDocumentServiceFacade;
import org.onehippo.forge.exdocpicker.api.PluginConstants;
import org.onehippo.forge.exdocpicker.impl.SimpleExternalDocumentCollectionDataProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExternalDocumentFieldBrowserDialog extends AbstractExternalDocumentFieldBrowserDialog {

    private static final long serialVersionUID = 1L;

    private static Logger log = LoggerFactory.getLogger(ExternalDocumentFieldBrowserDialog.class);

    private String searchQuery;

    private boolean initialSearchEnabled;

    private final AjaxButton selectAllButton;
    private final AjaxButton clearAllButton;

    public ExternalDocumentFieldBrowserDialog(IModel<String> titleModel,
                                              final ExternalDocumentServiceContext extDocServiceContext,
                                              final ExternalDocumentServiceFacade<Serializable> exdocService,
                                              IModel<ExternalDocumentCollection<Serializable>> model) {
        super(titleModel, extDocServiceContext, exdocService, model);

        selectAllButton = new AjaxButton("select-all-button") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                selectedExtDocs.clear();
                for (Iterator<? extends Serializable> it = searchedDocCollection.iterator(); it.hasNext(); ) {
                    selectedExtDocs.add(it.next());
                }
                target.add(ExternalDocumentFieldBrowserDialog.this);
            }
        };
        selectAllButton.setEnabled(false);
        add(selectAllButton);

        clearAllButton = new AjaxButton("clear-all-button") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                selectedExtDocs.clear();
                target.add(ExternalDocumentFieldBrowserDialog.this);
            }
        };
        clearAllButton.setEnabled(false);
        add(clearAllButton);

        if (isSingleSelectionMode()) {
            selectAllButton.setVisible(false);
            clearAllButton.setVisible(false);
        }
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(CssHeaderItem.forReference(new PackageResourceReference(ExternalDocumentFieldBrowserDialog.class, ExternalDocumentFieldBrowserDialog.class.getSimpleName() + ".css")));
    }

    public String getSearchQuery() {
        return searchQuery;
    }

    public void setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
    }

    @Override
    protected void doInitialSearchOnExternalDocuments() {
        initialSearchEnabled = getPluginConfig().getAsBoolean(PluginConstants.PARAM_INITIAL_SEARCH_ENABLED, PluginConstants.DEFAULT_INITIAL_SEARCH_ENABLED);
        searchQuery = getPluginConfig().getString(PluginConstants.PARAM_INITIAL_SEARCH_QUERY, "");

        // initially search all
        if (initialSearchEnabled) {
            searchExternalDocumentsBySearchQuery();
        }
    }

    @Override
    protected void initDataListViewUI() {
        final IDataProvider<Serializable> provider =
                new SimpleExternalDocumentCollectionDataProvider<Serializable>(searchedDocCollection);

        final DataView<Serializable> resultsDataView = new DataView<Serializable>("item", provider, pageSize) {
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

                selectCheckbox.getModel().setObject(selectedExtDocs.contains(doc));

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

        resultsDataView.setOutputMarkupId(true);

        add(resultsDataView);
        add(new ExternalDocumentFieldBrowserPageNavigator("navigator", resultsDataView));
    }

    protected void searchExternalDocumentsBySearchQuery() {
        try {
            ExternalDocumentCollection<? extends Serializable> searchedDocs = exdocService.searchExternalDocuments(extDocServiceContext, getSearchQuery());

            searchedDocCollection.clear();

            if (searchedDocs != null && searchedDocs.getSize() > 0) {
                for (Iterator<? extends Serializable> it = searchedDocs.iterator(); it.hasNext(); ) {
                    searchedDocCollection.add(it.next());
                }

                selectAllButton.setEnabled(true);
                clearAllButton.setEnabled(true);
            } else {
                selectAllButton.setEnabled(false);
                clearAllButton.setEnabled(false);
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

