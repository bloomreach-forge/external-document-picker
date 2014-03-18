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
package org.onehippo.forge.exdocpicker.api;

import java.io.Serializable;

/**
 * The implementation of this interface is responsible for getting currently-selected external documents from the context document node
 * and setting new external documents into the context document node.
 * 
 * @param <T>
 */
public interface ExternalDocumentFieldService<T extends Serializable> {

    /**
     * Gets the currently-selected external documents from the context CMS document node and returns them in {@link ExternalDocumentCollection}.
     * @param context
     * @return
     */
    public ExternalDocumentCollection<T> getFieldExternalDocuments(ExternalDocumentServiceContext context);

    /**
     * Sets the new external documents (<code>exdocs</code>) into the context CMS document.
     * @param context
     * @param exdocs
     */
    public void setFieldExternalDocuments(ExternalDocumentServiceContext context, ExternalDocumentCollection<T> exdocs);

}
