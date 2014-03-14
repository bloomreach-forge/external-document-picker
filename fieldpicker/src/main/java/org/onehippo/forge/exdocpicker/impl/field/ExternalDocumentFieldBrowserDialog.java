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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sf.json.JSONObject;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.request.resource.ResourceReference;
import org.apache.wicket.util.value.IValueMap;
import org.apache.wicket.util.value.ValueMap;
import org.hippoecm.frontend.dialog.AbstractDialog;
import org.hippoecm.frontend.plugin.IPluginContext;
import org.hippoecm.frontend.plugin.config.IPluginConfig;
import org.onehippo.forge.exdocpicker.api.ExternalDocumentSearchResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExternalDocumentFieldBrowserDialog extends AbstractDialog<ExternalDocumentFieldListModel> {

    private static final long serialVersionUID = 1L;

    private static Logger log = LoggerFactory.getLogger(ExternalDocumentFieldBrowserDialog.class);

    private String searchTerm = "";

    private final IPluginConfig pluginConfig;
    private final IPluginContext pluginContext;
    private List<JSONObject> selectedExternalItems = new ArrayList<JSONObject>();
    private long pageIndex;
    private long total;

    private final static IValueMap CUSTOM_DIALOG_CONSTANTS = new ValueMap("width=835,height=650").makeImmutable();

    private int searchPageSize;

    public ExternalDocumentFieldBrowserDialog(IPluginContext context, IPluginConfig config, IModel<ExternalDocumentFieldListModel> model) {
        super(model);
        setOutputMarkupId(true);

        pluginConfig = config;
        pluginContext = context;

        searchPageSize = getPluginConfig().getInt("page.size", 10);

        //initialize already selected external docs
        //this.selectedExternalItems.addAll(model.getObject());

        //Search input
        searchTerm = "*";
        final TextField<String> searchText = new TextField<String>("search-input", new PropertyModel<String>(this, "searchTerm"));
        searchText.setOutputMarkupId(true);
        add(setFocus(searchText));

        //Paging information
        Label firstItemIndexLabel = new Label("first-item-index", new AbstractReadOnlyModel<Long>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Long getObject() {
                return ((pageIndex - 1) * searchPageSize) + 1;
            }
        });
        Label lastItemIndexLabel = new Label("last-item-index", new AbstractReadOnlyModel<Long>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Long getObject() {
                long lastItemIndex = (pageIndex - 1) * searchPageSize + searchPageSize;
                return total < lastItemIndex ? total : lastItemIndex;
            }
        });
        Label countLabel = new Label("total", new PropertyModel(this, "total"));
        Label searchTermLabel = new Label("search-term", new PropertyModel(this, "searchTerm"));

        add(firstItemIndexLabel);
        add(lastItemIndexLabel);
        add(countLabel);
        add(searchTermLabel);

        //Search button
        AjaxButton searchButton = new AjaxButton("search-button", new StringResourceModel("search-label", this, null)) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onSubmit(AjaxRequestTarget ajaxRequestTarget, Form<?> form) {
                ajaxRequestTarget.add(ExternalDocumentFieldBrowserDialog.this);
            }
        };
        add(searchButton);

        if (getModel().getObject() == null) {
            setOkVisible(false);
            setOkEnabled(false);
        }

        IDataProvider<JSONObject> provider = new IDataProvider<JSONObject>() {

            private static final long serialVersionUID = 1L;

            private List<JSONObject> searchDocumentList = new ArrayList<JSONObject>();

            public Iterator<? extends JSONObject> iterator(long first, long count) {
                pageIndex = ((int) first) / ((int) searchPageSize) + 1;
                searchDocumentList.clear();
                CollectionUtils.addAll(searchDocumentList, searchDocuments(searchTerm, pageIndex).documentIterator());
                return searchDocumentList.iterator();
            }

            public long size() {
                ExternalDocumentSearchResult searchResult = searchDocuments(searchTerm, 1);
                return Math.max(searchResult.getTotalSize(), searchResult.getSize());
            }

            public IModel<JSONObject> model(JSONObject model) {
                return new Model<JSONObject>(model);
            }

            public void detach() {
            }

        };

        DataView<JSONObject> resultsDataView = new DataView<JSONObject>("item", provider, searchPageSize) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(final Item<JSONObject> listItem) {
                final JSONObject searchDocument = listItem.getModelObject();
                listItem.setOutputMarkupId(true);

                AjaxCheckBox selectCheckbox = new AjaxCheckBox("select-button", new Model<Boolean>()) {
                    private static final long serialVersionUID = 1L;

                    @Override
                    protected void onUpdate(AjaxRequestTarget ajaxRequestTarget) {
                        if (getModelObject()) {
                            addSelectedExternalItem(searchDocument);

                            if (isSingleSelectionModel()) {
                                ExternalDocumentFieldBrowserDialog.this.handleSubmit();
                            }
                        } else {
                            removeSelectedExternalItem(searchDocument);
                        }
                    }
                };

                if (selectedExternalItems.contains(searchDocument)) {
                    selectCheckbox.getModel().setObject(true);
                }

                final String thumbNailLink = searchDocument.getString("thumbnail");
                final SearchDocumentThumbnailImage thumbnailImage = new SearchDocumentThumbnailImage("image", thumbNailLink);
                listItem.add(thumbnailImage);
                listItem.add(selectCheckbox);
                listItem.add(new Label("title-label", String.valueOf(searchDocument.getString("title"))));
                listItem.add(new Label("summary-label", String.valueOf(searchDocument.getString("summary"))));

                final String description = searchDocument.getString("description");

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


    public void addSelectedExternalItem(JSONObject selectedExternalItem) {
        this.selectedExternalItems.add(selectedExternalItem);
    }

    public void removeSelectedExternalItem(JSONObject selectedExternalItem) {
        this.selectedExternalItems.remove(selectedExternalItem);
    }

    @Override
    protected void onOk() {
        if (selectedExternalItems != null) {
            getModel().getObject().addAll(selectedExternalItems);
        }
    }


    public IModel<String> getTitle() {
        return new StringResourceModel("search-browser-title", this, null);
    }


    @Override
    public IValueMap getProperties() {
        return CUSTOM_DIALOG_CONSTANTS;
    }

    protected ExternalDocumentSearchResult searchDocuments(String searchTerm, long pageIndex) {
        return null;
    }

    protected IPluginConfig getPluginConfig() {
        return pluginConfig;
    }

    protected IPluginContext getPluginContext() {
        return pluginContext;
    }

    protected boolean isSingleSelectionModel() {
        return StringUtils.equalsIgnoreCase("single", getPluginConfig().getString("selection.mode"));
    }

    public static class SearchDocumentThumbnailImage extends Image {

        private static final ResourceReference NO_THUMB = new PackageResourceReference(ExternalDocumentFieldBrowserDialog.class, "no-thumb.jpg");

        private static final long serialVersionUID = 1L;

        public SearchDocumentThumbnailImage(String id, String imageUrl) {
            super(id);

            if ((imageUrl == null || imageUrl.isEmpty())) {
                this.setImageResourceReference(NO_THUMB, null);
            } else {
                add(new AttributeModifier("src", true, new Model(imageUrl)));
            }
        }
    }
}

