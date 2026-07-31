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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TreeItemExpansionSetTest {

    private static final String ITEM = "item-1";

    private TreeItemExpansionSet expansionSet;

    @BeforeEach
    public void before() {
        expansionSet = new TreeItemExpansionSet();
    }

    @Test
    public void newSet_notInversedByDefault_containsReturnsFalseForAnyItem() {
        assertFalse(expansionSet.contains(ITEM));
    }

    @Test
    public void add_whenNotInversed_addsItemAndMakesItContained() {
        boolean added = expansionSet.add(ITEM);

        assertTrue(added);
        assertTrue(expansionSet.contains(ITEM));
    }

    @Test
    public void add_whenNotInversed_duplicateItem_returnsFalse() {
        expansionSet.add(ITEM);

        boolean addedAgain = expansionSet.add(ITEM);

        assertFalse(addedAgain);
    }

    @Test
    public void remove_whenNotInversed_presentItem_removesItAndReturnsTrue() {
        expansionSet.add(ITEM);

        boolean removed = expansionSet.remove(ITEM);

        assertTrue(removed);
        assertFalse(expansionSet.contains(ITEM));
    }

    @Test
    public void remove_whenNotInversed_absentItem_returnsFalse() {
        boolean removed = expansionSet.remove(ITEM);

        assertFalse(removed);
    }

    @Test
    public void expandAll_setsInversedAndClearsItems_soEveryItemIsContained() {
        expansionSet.add(ITEM);

        expansionSet.expandAll();

        assertTrue(expansionSet.contains(ITEM));
        assertTrue(expansionSet.contains("any-other-item"));
    }

    @Test
    public void collapseAll_clearsItemsAndUninverts_soNoItemIsContained() {
        expansionSet.expandAll();

        expansionSet.collapseAll();

        assertFalse(expansionSet.contains(ITEM));
        assertFalse(expansionSet.contains("any-other-item"));
    }

    @Test
    public void add_whenInversed_itemNotYetExcepted_returnsFalseAndLeavesItContained() {
        expansionSet.expandAll();

        boolean added = expansionSet.add(ITEM);

        assertFalse(added);
        assertTrue(expansionSet.contains(ITEM));
    }

    @Test
    public void remove_whenInversed_marksItemAsExceptionAndReturnsTrue() {
        expansionSet.expandAll();

        boolean removed = expansionSet.remove(ITEM);

        assertTrue(removed);
        assertFalse(expansionSet.contains(ITEM));
    }

    @Test
    public void remove_whenInversed_duplicateException_returnsFalse() {
        expansionSet.expandAll();
        expansionSet.remove(ITEM);

        boolean removedAgain = expansionSet.remove(ITEM);

        assertFalse(removedAgain);
    }

    @Test
    public void add_whenInversed_reAddsPreviouslyExceptedItem_returnsTrueAndRestoresContainment() {
        expansionSet.expandAll();
        expansionSet.remove(ITEM);

        boolean added = expansionSet.add(ITEM);

        assertTrue(added);
        assertTrue(expansionSet.contains(ITEM));
    }

    @Test
    public void clear_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> expansionSet.clear());
    }

    @Test
    public void size_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> expansionSet.size());
    }

    @Test
    public void isEmpty_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> expansionSet.isEmpty());
    }

    @Test
    public void toArrayWithArgument_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> expansionSet.toArray(new Object[0]));
    }

    @Test
    public void toArray_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> expansionSet.toArray());
    }

    @Test
    public void iterator_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> expansionSet.iterator());
    }

    @Test
    public void containsAll_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> expansionSet.containsAll(Collections.emptyList()));
    }

    @Test
    public void addAll_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> expansionSet.addAll(Collections.emptyList()));
    }

    @Test
    public void retainAll_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> expansionSet.retainAll(Collections.emptyList()));
    }

    @Test
    public void removeAll_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> expansionSet.removeAll(Collections.emptyList()));
    }
}
