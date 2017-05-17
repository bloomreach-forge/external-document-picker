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
package org.onehippo.forge.exdocpicker.impl.field.tree;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Tree item expansion state data set.
 */
class TreeItemExpansionSet implements Set<Serializable>, Serializable {

    private static final long serialVersionUID = 1L;

    private Set<Serializable> items = new HashSet<>();
    private boolean inversed;

    public void expandAll() {
        items.clear();
        inversed = true;
    }

    public void collapseAll() {
        items.clear();
        inversed = false;
    }

    @Override
    public boolean add(Serializable item) {
        if (inversed) {
            return items.remove(item);
        } else {
            return items.add(item);
        }
    }

    @Override
    public boolean remove(Object o) {
        if (inversed) {
            return items.add((Serializable) o);
        } else {
            return items.remove(o);
        }
    }

    @Override
    public boolean contains(Object o) {
        if (inversed) {
            return !items.contains(o);
        } else {
            return items.contains(o);
        }
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int size() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isEmpty() {
        throw new UnsupportedOperationException();
    }

    @Override
    public <A> A[] toArray(A[] a) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<Serializable> iterator() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object[] toArray() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(Collection<? extends Serializable> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }
}