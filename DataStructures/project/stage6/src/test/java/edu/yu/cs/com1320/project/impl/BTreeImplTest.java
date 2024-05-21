package edu.yu.cs.com1320.project.impl;

import com.google.gson.*;
import edu.yu.cs.com1320.project.stage6.impl.DocumentImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BTreeImplTest {
    @Test
    void eorngoieg(){
        BTreeImpl<String, String>  b = new BTreeImpl<String, String>();
        b.put("1" , "1");
        b.put("2" , "2");
        b.put("3" , "3");
        b.put("4" , "4");
        b.put("5" , "5");
        b.put("6" , "6");
        b.put("7" , "7");
        b.put("8" , "8");
        assertEquals("1", b.get("1"));
        assertEquals("2", b.get("2"));
        assertEquals("3", b.get("3"));
        assertEquals("4", b.get("4"));
        assertEquals("5", b.get("5"));
        assertEquals("6", b.get("6"));
        assertEquals("7", b.get("7"));
        assertEquals("8", b.get("8"));
        Gson g = new Gson();
        System.out.println(g.toJson(b.get("1")));
        b.put("1" , null);
        assertNull(b.get("1"));
        b.put("2" , null);
        assertNull(b.get("2"));
        b.put("3" , null);
        assertNull(b.get("3"));
        b.put("4" , null);
        assertNull(b.get("4"));
        b.put("5" , null);
        assertNull(b.get("5"));
        b.put("6" , null);
        assertNull(b.get("6"));
        b.put("7" , null);
        assertNull(b.get("7"));
        b.put("8" , null);
        assertNull(b.get("8"));

    }
}
