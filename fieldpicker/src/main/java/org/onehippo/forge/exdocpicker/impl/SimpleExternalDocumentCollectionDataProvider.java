/**
 * Copyright 2014-2024 Bloomreach B.V. (<a href="http://www.bloomreach.com">http://www.bloomreach.com</a>)
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
package org.onehippo.forge.exdocpicker.impl;

import java.io.Serializable;
import java.util.Iterator;

import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.onehippo.forge.exdocpicker.api.ExternalDocumentCollection;

/**
 *
 */
public class SimpleExternalDocumentCollectionDataProvider<T extends Serializable> implements IDataProvider<T> {

    private static final long serialVersionUID = 1L;

    private final ExternalDocumentCollection<T> docCollection;

    public SimpleExternalDocumentCollectionDataProvider(ExternalDocumentCollection<T> docCollection) {
        this.docCollection = docCollection;
    }

    @Override
    public void detach() {
    }

    @Override
    public Iterator<? extends T> iterator(long first, long count) {
        return docCollection.iterator(first, count);
    }

    @Override
    public long size() {
        return docCollection.getSize();
    }

    @Override
    public IModel<T> model(T object) {
        return new Model<T>(object);
    }

}
