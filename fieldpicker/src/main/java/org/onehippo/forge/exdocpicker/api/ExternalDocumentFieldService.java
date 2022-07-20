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
 * The implementation of this interface is responsible for getting currently-selected external documents from the context document node
 * and setting new external documents into the context document node.
 * 
 * @param <T> Domain specific external document POJO type which must be serializable.
 */
public interface ExternalDocumentFieldService<T extends Serializable> {

    /**
     * Gets the currently-selected external documents from the context CMS document node and returns them in {@link ExternalDocumentCollection}.
     * @param context ExternalDocumentServiceContext instance
     * @return document collection
     */
    public ExternalDocumentCollection<T> getFieldExternalDocuments(ExternalDocumentServiceContext context);

    /**
     * Sets the new external documents (<code>exdocs</code>) into the context CMS document.
     * @param context ExternalDocumentServiceContext instance
     * @param exdocs document collection
     */
    public void setFieldExternalDocuments(ExternalDocumentServiceContext context, ExternalDocumentCollection<T> exdocs);

}
