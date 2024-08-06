/**
 * Copyright 2014-2024 Bloomreach B.V. (<a href="http://www.bloomreach.com">http://www.bloomreach.com</a>)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         <a href="http://www.apache.org/licenses/LICENSE-2.0">http://www.apache.org/licenses/LICENSE-2.0</a>
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.onehippo.forge.exdocpicker.impl;

import java.io.Serializable;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.onehippo.forge.exdocpicker.api.ExternalDocumentCollection;
import org.onehippo.forge.exdocpicker.api.ExternalDocumentServiceContext;
import org.onehippo.forge.exdocpicker.api.ExternalDocumentServiceFacade;

/**
 *
 */
public class ServiceDelegatingExternalDocumentServiceFacade implements ExternalDocumentServiceFacade<Serializable> {

    private static final long serialVersionUID = 1L;

    public static final String DELEGATE_EXTERNAL_DOCUMENT_SERVICE_ID = "delegate.external.document.service.id";

    private String serviceId;

    @Override
    public ExternalDocumentCollection<Serializable> searchExternalDocuments(ExternalDocumentServiceContext context,
            String queryString) {
        return getDelegatingExternalDocumentServiceFacadeService(context).searchExternalDocuments(context, queryString);
    }

    @Override
    public ExternalDocumentCollection<Serializable> getFieldExternalDocuments(ExternalDocumentServiceContext context) {
        return getDelegatingExternalDocumentServiceFacadeService(context).getFieldExternalDocuments(context);
    }

    @Override
    public void setFieldExternalDocuments(ExternalDocumentServiceContext context,
            ExternalDocumentCollection<Serializable> exdocs) {
        getDelegatingExternalDocumentServiceFacadeService(context).setFieldExternalDocuments(context, exdocs);
    }

    @Override
    public String getDocumentTitle(ExternalDocumentServiceContext context, Serializable doc, Locale preferredLocale) {
        return getDelegatingExternalDocumentServiceFacadeService(context).getDocumentTitle(context, doc, preferredLocale);
    }

    @Override
    public String getDocumentDescription(ExternalDocumentServiceContext context, Serializable doc,
            Locale preferredLocale) {
        return getDelegatingExternalDocumentServiceFacadeService(context).getDocumentDescription(context, doc, preferredLocale);
    }

    @Override
    public String getDocumentIconLink(ExternalDocumentServiceContext context, Serializable doc, Locale preferredLocale) {
        return getDelegatingExternalDocumentServiceFacadeService(context).getDocumentIconLink(context, doc, preferredLocale);
    }

    @SuppressWarnings("unchecked")
    protected ExternalDocumentServiceFacade<Serializable> getDelegatingExternalDocumentServiceFacadeService(ExternalDocumentServiceContext context) {
        if (serviceId == null) {
            serviceId = context.getPluginConfig().getString(DELEGATE_EXTERNAL_DOCUMENT_SERVICE_ID);

            if (StringUtils.isBlank(serviceId)) {
                throw new IllegalStateException("Invalid delegate.external.document.service.id: '" + serviceId + "'.");
            }
        }

        return context.getPluginContext().getService(serviceId, ExternalDocumentServiceFacade.class);
    }

}
