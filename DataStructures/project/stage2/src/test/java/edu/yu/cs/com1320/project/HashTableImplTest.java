package edu.yu.cs.com1320.project;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import edu.yu.cs.com1320.project.impl.HashTableImpl;

import static org.junit.jupiter.api.Assertions.*;
public class HashTableImplTest {
    HashTableImpl<String, String> h;
    @BeforeEach
    void BeforeEach(){
        h = new HashTableImpl<>();
    }
    @Test
    void putTest(){
        h.put("FB", "b");
        h.put("Ea", "yo");
        assertEquals("b", h.get("FB"));
        assertEquals("yo", h.get("Ea"));
        h.put("FB", null);
        assertEquals("yo", h.get("Ea"));
    }
}
