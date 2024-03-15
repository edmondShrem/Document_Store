package edu.yu.cs.com1320.project.impl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import edu.yu.cs.com1320.project.impl.TrieImpl;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
public class TrieImplTest {
    TrieImpl<String> t;
    Comparator<String> comp;
    @BeforeEach
    void beforeEach(){
        t = new TrieImpl<>();
         comp = new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        };
    }
    @Test
    void constructorTest(){
        TrieImpl<String> t = new TrieImpl<>();
        t.put("k", "v");
        t.put("k", "7");
        assertTrue(t.get("k").contains("v") && t.get("k").contains("7"));
        //it works ahhhhhhhhhhh lets gooooooooooooooooooooo hi judah :)
    }
    @Test
    void getSortedTestNotEmpty(){
        t.put("k", "v");
        t.put("k", "7");
        t.put("k", "Wumbo");
        t.put("k", "zumbo");
        t.put("k", "ZUMBO");
        List<String> l = t.getSorted("k", comp);
        for(int i = 1; i < l.size(); i++) {
            assertTrue(l.get(i-1).compareTo(l.get(i)) > 0);
        }
    }
    @Test
    void getSortedTestEmpty(){
        List<String> l = t.getSorted("k", comp);
        assertTrue(l.isEmpty());
    }

    @Test
    void putTest(){
        t.put("cool", "1");
        t.put("coolio", "2");
        t.put("cooliotoolio", "3");
        t.put("coolcoolmountain", "4");
        assertTrue(t.get("cool").contains("1"));
        assertTrue(t.get("coolio").contains("2"));
        assertTrue(t.get("cooliotoolio").contains("3"));
        assertTrue(t.get("coolcoolmountain").contains("4"));
    }

    @Test
    void getTest(){
        t.put("cool", "1");
        t.put("coolio", "2");
        t.put("cooliotoolio", "3");
        t.put("coolcoolmountain", "4");
        assertTrue(t.get("cool").contains("1"));
        assertTrue(t.get("coolio").contains("2"));
        assertTrue(t.get("cooliotoolio").contains("3"));
        assertTrue(t.get("coolcoolmountain").contains("4"));
        assertEquals(t.get("wumbo"), new HashSet<String>());
    }
    @Test
    void getAllWithPrefixSortedTest(){
        t.put("cool", "1");
        t.put("cool", "6");
        t.put("cool", "12");
        t.put("coolio", "2");
        t.put("coolio", "bohl");
        t.put("cooliotoolio", "3");
        t.put("coolcoolmountain", "4");
        List<String> l = t.getAllWithPrefixSorted("coo", comp);
        assertEquals(7, l.size());
        for(int i = 1; i < l.size(); i++) {
            assertTrue(l.get(i-1).compareTo(l.get(i)) > 0);
        }
        assertTrue(t.getAllWithPrefixSorted("bruh", comp).isEmpty());
    }
    @Test
    void deleteTest(){
        t.put("cool", "1");
        t.put("cool", "6");
        t.put("cool", "12");
        t.put("coolio", "1");
        t.put("coolio", "bohl");
        t.put("cooliotoolio", "3");
        t.put("coolcoolmountain", "4");
        assertEquals("1", t.delete("cool", "1"));
        assertNull(t.delete("cool", "1"));
        assertNull(t.delete("null", "1"));
        assertTrue(t.get("coolio").contains("1"));
    }
    @Test
    void deleteAllTest(){
        t.put("cool", "1");
        t.put("cool", "6");
        t.put("cool", "12");
        t.put("coolio", "1");
        t.put("coolio", "bohl");
        t.put("cooliotoolio", "3");
        t.put("coolcoolmountain", "4");
        assertEquals(3, t.deleteAll("cool").size());
        assertTrue(t.deleteAll("cool").isEmpty());
        assertTrue(t.get("coolio").contains("1"));
    }
    @Test
    void deleteAllWithPrefixTest(){
        t.put("cool", "1");
        t.put("cool", "6");
        t.put("cool", "12");
        t.put("coolio", "1");
        t.put("coolio", "bohl");
        t.put("cooliotoolio", "3");
        t.put("coolcoolmountain", "4");
        assertEquals(3, t.deleteAllWithPrefix("coolio").size());
        assertTrue(t.get("cool").contains("1"));
        assertTrue(t.get("coolcoolmountain").contains("4"));
        assertFalse(t.get("coolio").contains("bohl"));
        assertFalse(t.get("cooliotoolio").contains("3"));
        assertTrue(t.get("cooliotoolio").isEmpty());
    }
}
