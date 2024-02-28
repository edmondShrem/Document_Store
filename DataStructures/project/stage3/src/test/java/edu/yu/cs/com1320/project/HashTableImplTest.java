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
        h.put("Ea", "yoy");
        h.put("FB", "b");
        h.put("uygFB", "3");
        h.put("FgyuguB", "4");
        h.put("Fgi8ygi8B", "5");
        h.put("FByi8y", "6");
        h.put("hyiouyi9yFB", "7");
        h.put("uiohyiuyFB", "8");
        h.put("986gyugyFB", "9");
        h.put("iuhohFB", "10");
        h.put("uygyugFB", "11");
        h.put("F97097B", "12");
        h.put("F567746B", "13");
        h.put("F9760969B", "14");
        h.put("F58778B", "15");
        h.put("785FB", "16");
        h.put("875FB", "17");
        h.put("F888B", "18");
        h.put("F5555B", "19b");
        h.put("FB587578", "b20");
        h.put("F76585B", "b21");
        h.put("FiyiyB", "b22");
        h.put("ihbihykbFB", "b23");
        h.put("ibFB", "b24");
        h.put("FedlkvbnwpijnespoibjneipB", "b25");
        assertEquals(25, h.size());
        assertEquals("b", h.get("FB"));
        assertEquals("yoy", h.get("Ea"));
        h.put("FB", null);
        assertEquals(24, h.size());
        assertEquals("yoy", h.get("Ea"));
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
