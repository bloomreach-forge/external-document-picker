package org.onehippo.forge.exdocpicker.demo.jpa.model;

import org.junit.Test;

import static org.junit.Assert.*;

public class InformantTest {

    @Test
    public void testEquals() {
        Informant first = new Informant();
        Informant second = new Informant();

        // instanceof
        Object object = new Object();
        assertNotEquals(first, object);

        // null, null
        first.setId(null);
        second.setId(null);
        assertNotEquals(first, second);

        // non null, null
        first.setId(10L);
        second.setId(null);
        assertNotEquals(first, second);

        // null, non null
        first.setId(null);
        second.setId(10L);
        assertNotEquals(first, second);

        //equals
        first.setId(10L);
        second.setId(10L);
        assertEquals(first, second);

        // !equals
        first.setId(5L);
        second.setId(10L);
        assertNotEquals(first, second);
    }

    @Test
    public void testHashCode() {
        Informant informant = new Informant();

        informant.setId(null);
        assertEquals(-1 ,informant.hashCode());

        informant.setId(10L);
        assertNotEquals(-1, informant.hashCode());
    }
}
