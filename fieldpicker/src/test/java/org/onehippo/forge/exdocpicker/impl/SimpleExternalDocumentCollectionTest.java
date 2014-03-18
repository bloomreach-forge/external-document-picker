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
        assertEquals(10, docCollection.getSize());

        docCollection.remove(docCollection.iterator().next());
        assertEquals(9, docCollection.getSize());

        docCollection.add(createDoc(11, "Document 11"));
        assertEquals(10, docCollection.getSize());

        docCollection.add(createDoc(12, "Document 12"));
        assertEquals(11, docCollection.getSize());

        docCollection.addAll(Arrays.asList(createDoc(13, "Document 13"), createDoc(14, "Document 14"), createDoc(15, "Document 15")));
        assertEquals(14, docCollection.getSize());

        docCollection.clear();
        assertEquals(0, docCollection.getSize());
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

        JSONObject [] array = docCollection.toArray(new JSONObject[docCollection.getSize()]);
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
