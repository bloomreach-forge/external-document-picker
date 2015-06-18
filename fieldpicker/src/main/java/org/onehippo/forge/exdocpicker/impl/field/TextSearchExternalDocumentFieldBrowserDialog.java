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

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;
import org.onehippo.forge.exdocpicker.api.ExternalDocumentCollection;
import org.onehippo.forge.exdocpicker.api.ExternalDocumentServiceContext;
import org.onehippo.forge.exdocpicker.api.ExternalDocumentServiceFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TextSearchExternalDocumentFieldBrowserDialog extends ExternalDocumentFieldBrowserDialog {

    private static final long serialVersionUID = 1L;

    private static Logger log = LoggerFactory.getLogger(TextSearchExternalDocumentFieldBrowserDialog.class);

    public TextSearchExternalDocumentFieldBrowserDialog(IModel<String> titleModel,
                                                        final ExternalDocumentServiceContext extDocServiceContext,
                                                        final ExternalDocumentServiceFacade<Serializable> exdocService,
                                                        IModel<ExternalDocumentCollection<Serializable>> model) {
        super(titleModel, extDocServiceContext, exdocService, model);

        final TextField<String> searchText = new TextField<String>("search-input", new PropertyModel<String>(this, "searchQuery"));
        searchText.setOutputMarkupId(true);
        add(setFocus(searchText));

        //Search button
        AjaxButton searchButton = new AjaxButton("search-button", new StringResourceModel("search-label", this, null)) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onSubmit(AjaxRequestTarget ajaxRequestTarget, Form<?> form) {
                searchExternalDocumentsBySearchQuery();
                ajaxRequestTarget.add(TextSearchExternalDocumentFieldBrowserDialog.this);
            }
        };

        add(searchButton);
    }
}

