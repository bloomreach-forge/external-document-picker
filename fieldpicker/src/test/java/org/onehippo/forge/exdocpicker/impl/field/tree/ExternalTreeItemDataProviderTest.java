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
package org.onehippo.forge.exdocpicker.impl.field.tree;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Iterator;

import org.apache.wicket.model.IModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.onehippo.forge.exdocpicker.api.ExternalDocumentCollection;
import org.onehippo.forge.exdocpicker.api.ExternalDocumentTreeService;

@ExtendWith(MockitoExtension.class)
public class ExternalTreeItemDataProviderTest {

    @Mock
    private ExternalDocumentCollection<String> rootTreeItems;

    @Mock
    private ExternalDocumentTreeService<String> treeService;

    private ExternalTreeItemDataProvider<String> dataProvider;

    @BeforeEach
    public void before() {
        dataProvider = new ExternalTreeItemDataProvider<>(rootTreeItems, treeService);
    }

    @Test
    public void getRoots_delegatesToRootTreeItemsIterator() {
        @SuppressWarnings("unchecked")
        Iterator<String> expected = mock(Iterator.class);
        when(rootTreeItems.iterator()).thenReturn(expected);

        Iterator<? extends String> result = dataProvider.getRoots();

        assertSame(expected, result);
    }

    @Test
    public void hasChildren_delegatesToTreeService() {
        when(treeService.hasChildren("item")).thenReturn(true);

        assertTrue(dataProvider.hasChildren("item"));
    }

    @Test
    public void getChildren_delegatesToTreeService() {
        @SuppressWarnings("unchecked")
        Iterator<String> expected = mock(Iterator.class);
        when(treeService.getChildren("item")).thenReturn(expected);

        Iterator<String> result = dataProvider.getChildren("item");

        assertSame(expected, result);
    }

    @Test
    public void getParent_delegatesToTreeService() {
        when(treeService.getParent("item")).thenReturn("parent");

        assertEquals("parent", dataProvider.getParent("item"));
    }

    @Test
    public void model_wrapsItemInModel() {
        IModel<String> model = dataProvider.model("item");

        assertEquals("item", model.getObject());
    }

    @Test
    public void detach_doesNotThrow() {
        dataProvider.detach();
    }
}
