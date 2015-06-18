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
package org.onehippo.forge.exdocpicker.impl;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.collections.iterators.AbstractIteratorDecorator;
import org.onehippo.forge.exdocpicker.api.ExternalDocumentCollection;

/**
 * 
 */
public class SimpleExternalDocumentCollection<T extends Serializable> implements ExternalDocumentCollection<T> {

    private static final long serialVersionUID = 1L;

    private List<T> list = new LinkedList<T>();

    public SimpleExternalDocumentCollection() {
        this(null);
    }

    public SimpleExternalDocumentCollection(List<T> source) {
        if (source != null) {
            list.addAll(source);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Iterator<? extends T> iterator(long first, final long count) {
        return new AbstractIteratorDecorator(list.listIterator((int) first)) {
            private int iterationCount;
 
            @Override
            public boolean hasNext() {
                return super.hasNext() && (iterationCount < (int) count);
            }

            @Override
            public Object next() {
                try {
                    return super.next();
                } finally {
                    iterationCount += 1;
                }
            }
        };
    }

    @Override
    public Iterator<? extends T> iterator() {
        return list.iterator();
    }

    @Override
    public boolean contains(T doc) {
        return list.contains(doc);
    }

    @Override
    public boolean add(T doc) {
        return list.add(doc);
    }

    @Override
    public boolean addAll(Collection<T> docs) {
        return list.addAll(docs);
    }

    @Override
    public boolean remove(T doc) {
        return list.remove(doc);
    }

    @Override
    public void clear() {
        list.clear();
    }

    @Override
    public int getSize() {
        return list.size();
    }

    @Override
    public T[] toArray(T[] a) {
        return list.toArray(a);
    }

    @Override
    public Object clone() {
        return new SimpleExternalDocumentCollection(list);
    }

}
