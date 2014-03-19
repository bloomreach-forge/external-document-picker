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
 * External document collection representation.
 * {@link ExternalDocumentSearchService} is supposed to return an instance of this type.
 * 
 * <P>
 * Because it is used in a CMS/Wicket application, each item must be serializable and
 * this collection itself must be serializable, too.
 * </P>
 * 
 * @param <T> Domain specific external document POJO type which must be serializable.
 */
public interface ExternalDocumentCollection<T extends Serializable> extends Serializable {

    /**
     * Returns an iterator of all the items.
     * @return
     */
    public Iterator<? extends T> iterator();

    /**
     * Returns an iterator of items starting from <code>first</code> index up to <code>count</code> items.
     * @param first
     * @param count
     * @return
     */
    public Iterator<? extends T> iterator(long first, final long count);

    /**
     * Returns true if this collection contains the <code>doc</code>.
     * @param docs
     * @return
     */
    public boolean contains(T doc);

    /**
     * Adds the <code>doc</code> to this collection.
     * @param doc
     * @return
     */
    public boolean add(T doc);

    /**
     * Adds all the <code>docs</code> to this collection.
     * @param docs
     * @return
     */
    public boolean addAll(Collection<T> docs);

    /**
     * Removes the <code>doc</code> from this collection.
     * @param doc
     * @return
     */
    public boolean remove(T doc);

    /**
     * Removes all the document items from this collection.
     */
    public void clear();

    /**
     * Returns the size of this collection.
     * @return
     */
    public int getSize();

    /**
     * Copy all the items into an array and returns the array.
     * @param a
     * @return
     */
    public T[] toArray(T[] a);

}
