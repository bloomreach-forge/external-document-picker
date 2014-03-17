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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Iterator;

import net.sf.json.JSONObject;

import org.junit.Before;
import org.junit.Test;

/**
 * 
 */
public class SimpleExternalDocumentCollectionTest {

    private SimpleExternalDocumentCollection<JSONObject> docCollection;

    @Before
    public void before() throws Exception {
        docCollection = new SimpleExternalDocumentCollection<JSONObject>();

        for (int i = 1; i <= 10; i++) {
            docCollection.add(createDoc(i, "Document " + i));
        }
    }

    @Test
    public void testSize() throws Exception {
        assertEquals(-1, docCollection.getTotalSize());

        try {
            docCollection.setTotalSize(-3);
            fail("Must get an IllegalArgumentException for an integer less than -1.");
        } catch (IllegalArgumentException e) {
            // as expected
        }

        assertEquals(-1, docCollection.getTotalSize());

        docCollection.setTotalSize(20);
        assertEquals(20, docCollection.getTotalSize());
        assertEquals(10, docCollection.size());

        docCollection.remove(docCollection.iterator().next());
        assertEquals(20, docCollection.getTotalSize());
        assertEquals(9, docCollection.size());

        docCollection.add(createDoc(11, "Document 11"));
        assertEquals(20, docCollection.getTotalSize());
        assertEquals(10, docCollection.size());

        docCollection.add(createDoc(12, "Document 12"));
        assertEquals(20, docCollection.getTotalSize());
        assertEquals(11, docCollection.size());

        docCollection.addAll(Arrays.asList(createDoc(13, "Document 13"), createDoc(14, "Document 14"), createDoc(15, "Document 15")));
        assertEquals(20, docCollection.getTotalSize());
        assertEquals(14, docCollection.size());

        docCollection.clear();
        assertEquals(20, docCollection.getTotalSize());
        assertEquals(0, docCollection.size());

        docCollection.setTotalSize(0);
        assertEquals(0, docCollection.getTotalSize());
        assertEquals(0, docCollection.size());
    }

    @Test
    public void testIteration() throws Exception {
        Iterator<? extends JSONObject> it = docCollection.iterator();

        for (int i = 1; i <= 10; i++) {
            assertTrue(it.hasNext());
            JSONObject item = it.next();
            assertEquals(i, item.getInt("id"));
            assertEquals("Document " + i, item.getString("title"));
        }

        assertFalse(it.hasNext());

        it = docCollection.iterator(5, 4);

        for (int i = 6; i <= 9; i++) {
            assertTrue(it.hasNext());
            JSONObject item = it.next();
            assertEquals(i, item.getInt("id"));
            assertEquals("Document " + i, item.getString("title"));
        }

        assertFalse(it.hasNext());

        JSONObject [] array = docCollection.toArray(new JSONObject[docCollection.size()]);
        assertEquals(10, array.length);

        for (int i = 1; i <= 10; i++) {
            JSONObject item = array[i - 1];
            assertEquals(i, item.getInt("id"));
            assertEquals("Document " + i, item.getString("title"));
        }
    }

    private JSONObject createDoc(int id, String title) {
        JSONObject doc = new JSONObject();
        doc.put("id", id);
        doc.put("title", title);
        return doc;
    }
}
