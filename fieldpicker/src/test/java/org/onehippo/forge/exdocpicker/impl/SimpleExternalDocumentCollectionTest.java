package org.onehippo.forge.exdocpicker.impl;

import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class SimpleExternalDocumentCollectionTest {
    @Test void defaultConstructor_isEmpty() { assertEquals(0, new SimpleExternalDocumentCollection<>().getSize()); }
    @Test void sourceConstructor_withNull_isEmpty() { assertEquals(0, new SimpleExternalDocumentCollection<>(null).getSize()); }
    @Test void sourceConstructor_withList_populatesCollection() { assertEquals(3, new SimpleExternalDocumentCollection<>(Arrays.asList("a","b","c")).getSize()); }
    @Test void add_increasesSize() { var c = new SimpleExternalDocumentCollection<String>(); c.add("x"); assertEquals(1, c.getSize()); }
    @Test void contains_existingItem_returnsTrue() { var c = new SimpleExternalDocumentCollection<String>(); c.add("x"); assertTrue(c.contains("x")); }
    @Test void contains_missingItem_returnsFalse() { assertFalse(new SimpleExternalDocumentCollection<String>().contains("absent")); }
    @Test void indexOf_returnsCorrectIndex() { var c = new SimpleExternalDocumentCollection<>(List.of("a","b")); assertEquals(1, c.indexOf("b")); }
    @Test void remove_removesItem() { var c = new SimpleExternalDocumentCollection<String>(); c.add("i"); c.remove("i"); assertEquals(0, c.getSize()); }
    @Test void iterator_fullRange_returnsAll() { var c = new SimpleExternalDocumentCollection<>(List.of("a","b","c")); Iterator<String> it = c.iterator(0,10); int n=0; while(it.hasNext()){it.next();n++;} assertEquals(3,n); }
    @Test void iterator_limited_returnsSubset() { var c = new SimpleExternalDocumentCollection<>(List.of("a","b","c")); Iterator<String> it = c.iterator(0,2); int n=0; while(it.hasNext()){it.next();n++;} assertEquals(2,n); }
    @Test void clear_emptiesCollection() { var c = new SimpleExternalDocumentCollection<>(List.of("a","b")); c.clear(); assertEquals(0, c.getSize()); }
    @Test void pluginConstants_valid() { assertNotNull(org.onehippo.forge.exdocpicker.api.PluginConstants.SELECTION_MODE_MULTIPLE); assertTrue(org.onehippo.forge.exdocpicker.api.PluginConstants.DEFAULT_PAGE_SIZE > 0); }
}
