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
import java.util.Iterator;

import org.apache.wicket.extensions.markup.html.repeater.tree.ITreeProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.onehippo.forge.exdocpicker.api.ExternalDocumentCollection;
import org.onehippo.forge.exdocpicker.api.ExternalDocumentTreeService;

/**
 * {@link ITreeProvider} implementation for {@link ExternalTreeItemFieldBrowserDialog}.
 * @param <T> Domain specific external document POJO type which must be serializable.
 */
class ExternalTreeItemDataProvider<T extends Serializable> implements ITreeProvider<T> {

    private static final long serialVersionUID = 1L;

    private final ExternalDocumentCollection<T> rootTreeItems;
    private final ExternalDocumentTreeService<T> treeService;

    public ExternalTreeItemDataProvider(final ExternalDocumentCollection<T> rootTreeItems, ExternalDocumentTreeService<T> treeService) {
        this.rootTreeItems = rootTreeItems;
        this.treeService = treeService;
    }

    @Override
    public Iterator<? extends T> getRoots() {
        return rootTreeItems.iterator();
    }

    @Override
    public boolean hasChildren(T item) {
        return treeService.hasChildren(item);
    }

    @Override
    public Iterator<T> getChildren(T item) {
        return treeService.getChildren(item);
    }

    @Override
    public IModel<T> model(Serializable item) {
        return new Model(item);
    }

    @Override
    public void detach() {
    }

    public T getParent(T item) {
        return treeService.getParent(item);
    }
}
