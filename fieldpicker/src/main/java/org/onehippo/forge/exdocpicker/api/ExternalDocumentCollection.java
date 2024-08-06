/**
 * Copyright 2014-2024 Bloomreach B.V. (<a href="https://www.bloomreach.com">https://www.bloomreach.com</a>)
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
public interface ExternalDocumentCollection<T extends Serializable> extends Serializable, Cloneable {

    /**
     * Returns an iterator of all the items.
     * @return an iterator of all the items
     */
    Iterator<T> iterator();

    /**
     * Returns an iterator of items starting from <code>first</code> index up to <code>count</code> items.
     * @param first first index
     * @param count iterating count
     * @return an iterator of items starting from <code>first</code> index up to <code>count</code> items
     */
    Iterator<T> iterator(long first, final long count);

    /**
     * Returns true if this collection contains the <code>doc</code>.
     * @param doc document
     * @return true if this collection contains the <code>doc</code>
     */
    boolean contains(T doc);

    /**
     * Returns the index of {@code doc} in this collection if found.
     * Otherwise returns -1.
     * @param doc document
     * @return the index of {@code doc} in this collection if found. Otherwise returns -1
     */
    int indexOf(T doc);

    /**
     * Adds the <code>doc</code> to this collection.
     * @param doc document
     * @return true if added
     */
    boolean add(T doc);

    /**
     * Inserts the <code>doc</code> at the {@code index} position of this collection.
     * @param index index at which the document should be inserted
     * @param doc document
     * @return true if inserted
     */
    boolean add(int index, T doc);

    /**
     * Adds all the <code>docs</code> to this collection.
     * @param docs documents
     * @return all the <code>docs</code> to this collection
     */
    boolean addAll(Collection<T> docs);

    /**
     * Removes the <code>doc</code> from this collection.
     * @param doc document
     * @return the <code>doc</code> from this collection
     */
    boolean remove(T doc);

    /**
     * Removes all the document items from this collection.
     */
    void clear();

    /**
     * Returns the size of this collection.
     * @return the size of this collection
     */
    int getSize();

    /**
     * Copy all the items into an array and returns the array.
     * @param a array
     * @return all the items into an array and returns the array
     */
    T[] toArray(T[] a);

    /**
     * Creates and returns a copy of this object.
     * @return a copy of this object
     */
    Object clone();

}
