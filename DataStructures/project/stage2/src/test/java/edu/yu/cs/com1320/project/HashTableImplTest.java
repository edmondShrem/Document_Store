package edu.yu.cs.com1320.project;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import edu.yu.cs.com1320.project.impl.HashTableImpl;

import java.util.Collection;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
public class HashTableImplTest {
    HashTableImpl<String, String> h;

    @BeforeEach
    void BeforeEach() {
        h = new HashTableImpl<>();
    }

    @Test
    void putTest() {
        h.put("FB", "b");
        h.put("Ea", "yo");
        h.put("FB", "b");
        h.put("Ea", "yo");
        assertEquals(2, h.size());
        assertEquals("b", h.get("FB"));
        assertEquals("yo", h.get("Ea"));
        h.put("FB", null);
        assertEquals("yo", h.get("Ea"));
        assertNull(h.put("ladeeda", null));
    }

    @Test
    void getTest() {
        h.put("FB", "b");
        h.put("Ea", "wee");
        h.put("Wa", "greyhound");
        h.put("za", "kevin.");
        assertEquals("b", h.get("FB"));
        assertEquals("wee", h.get("Ea"));
        assertEquals("greyhound", h.get("Wa"));
        assertEquals("kevin.", h.get("za"));
        h.put("FB", null);
        assertNull(h.get("FB"));
        assertEquals("wee", h.get("Ea"));
    }

    @Test
    void containsTest() {
        h.put("FB", "b");
        h.put("Ea", "wee");
        h.put("Wa", "greyhound");
        h.put("za", "kevin.");
        assertTrue(h.containsKey("Ea"));
        assertTrue(h.containsKey("Wa"));
        assertFalse(h.containsKey("shamalamadingdong"));
        assertThrows(
                NullPointerException.class,
                () -> h.containsKey(null)
        );
    }

    @Test
    void keySetTest() {
        h.put("FB", "b");
        h.put("Ea", "wee");
        h.put("Wa", "greyhound");
        h.put("za", "kevin.");
        Set<String> s = h.keySet();
        assertEquals(s.size(), h.size());
        assertTrue(s.contains("FB"));
        assertFalse(s.contains("b"));
    }

    @Test
    void colTest() {
        h.put("FB", "b");
        h.put("Ea", "wee");
        h.put("Wa", "greyhound");
        h.put("za", "kevin.");
        Collection<String> c = h.values();
        assertEquals(c.size(), h.size());
        assertTrue(c.contains("b"));
        assertFalse(c.contains("FB"));
    }

    @Test
    void sizeTest() {
        h.put("FB", "b");
        h.put("Ea", "wee");
        h.put("Wa", "greyhound");
        h.put("za", "kevin.");
        assertEquals(4, h.size());
    }
    @Test
    void sCollide(){
        h.put("336", "0");
        h.put("336", "0");
        h.put("1ot", "1");
        h.put("1ot", "1");
        h.put("1pU", "2");
        h.put("1q6", "3");
        h.put("2Pt", "4");
        h.put("2QU", "5");
        h.put("2R6", "6");
        assertEquals(7, h.size());
        h.put("2QU", null);
        assertEquals(6, h.size());
        assertTrue(h.containsKey("336"));
        assertTrue(h.containsKey("1pU"));
        assertTrue(h.containsKey("1q6"));
        assertTrue(h.containsKey("2Pt"));
        assertFalse(h.containsKey("2QU"));
        assertTrue(h.containsKey("2R6"));
    }
}
