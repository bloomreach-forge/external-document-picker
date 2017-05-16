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

public interface ExternalDocumentTreeService<T extends Serializable> {

    default public boolean hasChildren(T doc) {
        return false;
    }

    default public Iterator<T> getChildren(T doc) {
        List<T> emptyList = Collections.emptyList();
        return emptyList.iterator();
    }

    default public T getParent(T doc) {
        return null;
    }

}
