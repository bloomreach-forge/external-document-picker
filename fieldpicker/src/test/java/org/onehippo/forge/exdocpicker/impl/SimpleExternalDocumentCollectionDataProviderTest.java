/**
 * Copyright 2014-2026 Bloomreach B.V. (<a href="https://www.bloomreach.com">https://www.bloomreach.com</a>)
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.when;

import java.util.Iterator;

import org.apache.wicket.model.IModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.onehippo.forge.exdocpicker.api.ExternalDocumentCollection;

@ExtendWith(MockitoExtension.class)
public class SimpleExternalDocumentCollectionDataProviderTest {

    @Mock
    private ExternalDocumentCollection<DocumentObject> docCollection;

    private SimpleExternalDocumentCollectionDataProvider<DocumentObject> dataProvider;

    @BeforeEach
    public void before() {
        dataProvider = new SimpleExternalDocumentCollectionDataProvider<>(docCollection);
    }

    @Test
    public void iterator_delegatesToCollectionWithFirstAndCount() {
        @SuppressWarnings("unchecked")
        Iterator<DocumentObject> expected = org.mockito.Mockito.mock(Iterator.class);
        when(docCollection.iterator(2L, 5L)).thenReturn(expected);

        Iterator<? extends DocumentObject> result = dataProvider.iterator(2L, 5L);

        assertSame(expected, result);
    }

    @Test
    public void size_delegatesToCollectionGetSize() {
        when(docCollection.getSize()).thenReturn(42);

        assertEquals(42L, dataProvider.size());
    }

    @Test
    public void model_wrapsObjectInModel() {
        DocumentObject doc = new DocumentObject();

        IModel<DocumentObject> model = dataProvider.model(doc);

        assertSame(doc, model.getObject());
    }

    @Test
    public void detach_doesNotThrow() {
        dataProvider.detach();
    }
}
