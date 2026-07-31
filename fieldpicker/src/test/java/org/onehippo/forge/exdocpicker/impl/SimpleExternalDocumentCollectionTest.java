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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SimpleExternalDocumentCollectionTest {

    private SimpleExternalDocumentCollection<DocumentObject> docCollection;

    @BeforeEach
    public void before() {
        docCollection = new SimpleExternalDocumentCollection<>();

        for (int i = 1; i <= 10; i++) {
            docCollection.add(createDoc(i, "Document " + i));
        }
    }

    @Test
    public void noArgConstructor_createsEmptyCollection() {
        SimpleExternalDocumentCollection<DocumentObject> collection = new SimpleExternalDocumentCollection<>();

        assertEquals(0, collection.getSize());
    }

    @Test
    public void listConstructor_withNullSource_createsEmptyCollection() {
        SimpleExternalDocumentCollection<DocumentObject> collection = new SimpleExternalDocumentCollection<>(null);

        assertEquals(0, collection.getSize());
    }

    @Test
    public void listConstructor_withSource_copiesEntries() {
        List<DocumentObject> source = Arrays.asList(createDoc(1, "Document 1"), createDoc(2, "Document 2"));

        SimpleExternalDocumentCollection<DocumentObject> collection = new SimpleExternalDocumentCollection<>(source);

        assertEquals(2, collection.getSize());
        assertTrue(collection.contains(source.get(0)));
        assertTrue(collection.contains(source.get(1)));
    }

    @Test
    public void iterator_returnsEntriesInInsertionOrder() {
        Iterator<DocumentObject> it = docCollection.iterator();

        for (int i = 1; i <= 10; i++) {
            assertTrue(it.hasNext());
            DocumentObject item = it.next();
            assertEquals(i, item.getInt("id"));
            assertEquals("Document " + i, item.getString("title"));
        }

        assertFalse(it.hasNext());
    }

    @Test
    public void iteratorWithRange_offsetAndCount_returnsBoundedSubset() {
        Iterator<DocumentObject> it = docCollection.iterator(5, 4);

        for (int i = 6; i <= 9; i++) {
            assertTrue(it.hasNext());
            DocumentObject item = it.next();
            assertEquals(i, item.getInt("id"));
        }

        assertFalse(it.hasNext());
    }

    @Test
    public void iteratorWithRange_countExceedingRemainingEntries_stopsAtLastEntry() {
        Iterator<DocumentObject> it = docCollection.iterator(8, 10);

        int seen = 0;
        while (it.hasNext()) {
            it.next();
            seen++;
        }

        assertEquals(2, seen);
    }

    @Test
    public void iteratorWithRange_zeroCount_returnsNoEntries() {
        Iterator<DocumentObject> it = docCollection.iterator(0, 0);

        assertFalse(it.hasNext());
    }

    @Test
    public void contains_existingEntry_returnsTrue() {
        DocumentObject doc = docCollection.iterator().next();

        assertTrue(docCollection.contains(doc));
    }

    @Test
    public void contains_missingEntry_returnsFalse() {
        DocumentObject doc = createDoc(99, "Missing");

        assertFalse(docCollection.contains(doc));
    }

    @Test
    public void indexOf_existingEntry_returnsItsPosition() {
        DocumentObject doc = createDoc(20, "Document 20");
        docCollection.add(doc);

        assertEquals(10, docCollection.indexOf(doc));
    }

    @Test
    public void indexOf_missingEntry_returnsNegativeOne() {
        DocumentObject doc = createDoc(99, "Missing");

        assertEquals(-1, docCollection.indexOf(doc));
    }

    @Test
    public void add_appendsEntryAndIncreasesSize() {
        DocumentObject doc = createDoc(11, "Document 11");

        docCollection.add(doc);

        assertEquals(11, docCollection.getSize());
        assertEquals(10, docCollection.indexOf(doc));
    }

    @Test
    public void addAtIndex_insertsEntryAtGivenPosition() {
        DocumentObject doc = createDoc(0, "Document 0");

        docCollection.add(0, doc);

        assertEquals(11, docCollection.getSize());
        assertEquals(0, docCollection.indexOf(doc));
    }

    @Test
    public void addAll_appendsEveryEntryFromGivenCollection() {
        List<DocumentObject> extra = Arrays.asList(createDoc(11, "Document 11"), createDoc(12, "Document 12"));

        docCollection.addAll(extra);

        assertEquals(12, docCollection.getSize());
        assertTrue(docCollection.contains(extra.get(0)));
        assertTrue(docCollection.contains(extra.get(1)));
    }

    @Test
    public void remove_existingEntry_removesItAndDecreasesSize() {
        DocumentObject doc = docCollection.iterator().next();

        docCollection.remove(doc);

        assertEquals(9, docCollection.getSize());
        assertFalse(docCollection.contains(doc));
    }

    @Test
    public void remove_missingEntry_leavesCollectionUnchanged() {
        DocumentObject doc = createDoc(99, "Missing");

        docCollection.remove(doc);

        assertEquals(10, docCollection.getSize());
    }

    @Test
    public void clear_removesAllEntries() {
        docCollection.clear();

        assertEquals(0, docCollection.getSize());
    }

    @Test
    public void toArray_returnsEveryEntryInInsertionOrder() {
        DocumentObject[] array = docCollection.toArray(new DocumentObject[docCollection.getSize()]);

        assertEquals(10, array.length);

        for (int i = 1; i <= 10; i++) {
            assertEquals(i, array[i - 1].getInt("id"));
        }
    }

    @Test
    public void clone_returnsIndependentCollectionWithSameEntries() {
        Object clone = docCollection.clone();

        assertTrue(clone instanceof SimpleExternalDocumentCollection);

        @SuppressWarnings("unchecked")
        SimpleExternalDocumentCollection<DocumentObject> clonedCollection = (SimpleExternalDocumentCollection<DocumentObject>) clone;
        assertNotSame(docCollection, clonedCollection);
        assertEquals(docCollection.getSize(), clonedCollection.getSize());

        DocumentObject doc = createDoc(11, "Document 11");
        clonedCollection.add(doc);
        assertFalse(docCollection.contains(doc));
    }

    private DocumentObject createDoc(int id, String title) {
        DocumentObject doc = new DocumentObject();
        doc.put("id", id);
        doc.put("title", title);
        return doc;
    }
}
