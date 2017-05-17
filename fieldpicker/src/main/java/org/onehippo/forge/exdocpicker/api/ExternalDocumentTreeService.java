/**
 * Copyright 2017 Hippo B.V. (http://www.onehippo.com)
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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * The implementation of this interface is responsible for providing children and parent items to render a tree
 * list view instead of a flat list view from the searched external data items by {@link ExternalDocumentSearchService}
 * method implementations.
 * @param <T> Domain specific external document POJO type which must be serializable.
 */
public interface ExternalDocumentTreeService<T extends Serializable> {

    /**
     * Returns true if the {@code doc} has children.
     * @param doc document
     * @return true if the {@code doc} has children
     */
    default public boolean hasChildren(T doc) {
        return false;
    }

    /**
     * Returns an iterator of the children of the {@code doc}.
     * @param doc document
     * @return an iterator of the children of the {@code doc}
     */
    default public Iterator<T> getChildren(T doc) {
        List<T> emptyList = Collections.emptyList();
        return emptyList.iterator();
    }

    /**
     * Returns the parent document of the {@code doc} if any, or null if there's no parent.
     * @param doc document
     * @return the parent document of the {@code doc} if any, or null if there's no parent
     */
    default public T getParent(T doc) {
        return null;
    }

}
