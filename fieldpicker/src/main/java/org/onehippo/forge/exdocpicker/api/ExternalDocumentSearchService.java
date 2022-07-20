/**
 * Copyright 2014-2022 Bloomreach B.V. (<a href="https://www.bloomreach.com">https://www.bloomreach.com</a>)
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
package org.onehippo.forge.exdocpicker.api;

import java.io.Serializable;

/**
 * The implementation of this interface is responsible for searching external documents and returning the searched result in {@link ExternalDocumentCollection}.
 * 
 * @param <T> Domain specific external document POJO type which must be serializable.
 */
public interface ExternalDocumentSearchService<T extends Serializable> {

    /**
     * Searches external documents by the <code>queryString</code> and returns the result in {@link ExternalDocumentCollection}.
     * @param context ExternalDocumentServiceContext instance
     * @param queryString any implementation specific query string
     * @return document collection
     */
    ExternalDocumentCollection<T> searchExternalDocuments(ExternalDocumentServiceContext context, String queryString);

}
