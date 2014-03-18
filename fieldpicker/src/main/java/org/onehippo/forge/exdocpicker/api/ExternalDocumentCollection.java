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
import java.util.Collection;
import java.util.Iterator;

/**
 * 
 */
public interface ExternalDocumentCollection<T extends Serializable> extends Serializable {

    public Iterator<? extends T> iterator();

    public Iterator<? extends T> iterator(long first, final long count);

    public boolean contains(T docs);

    public boolean add(T doc);

    public boolean addAll(Collection<T> docs);

    public boolean remove(T doc);

    public void clear();

    public int getSize();

    public T[] toArray(T[] a);

}
