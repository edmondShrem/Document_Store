package edu.yu.introtoalgs;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions.*;
public class DHashMapTest {
    DHashMap<Integer, String> m = new DHashMap<>(5);
    @BeforeEach
    void beforeEach(){
        m.addServer(0, new SizedHashMap<>(5));
    }

    @Test
    void testTestSoICanAlwaysPassOneAndFeelGoodAboutMyself(){
        Assertions.assertEquals(1,1);
    }

    @Test
    void putAndGetWithOneServerTest(){
        m.put(0,"s");
        m.put(1,"t");
        Assertions.assertEquals(m.get(0), "s");
        Assertions.assertEquals(m.get(1), "t");
        Assertions.assertNull(m.get(12));
    }

    @Test
    void putAndGetWithTwoServers(){
        m.addServer(2, new SizedHashMap<>(5));
        m.put(0,"s");
        m.put(1,"t");
        Assertions.assertEquals(m.get(0), "s");
        Assertions.assertEquals(m.get(1), "t");
        Assertions.assertNull(m.get(12));
    }

    @Test
    void leff(){
        final int perServerMaxCapacity = 2;
        final DHashMapBase<String, Integer> dhm = new DHashMap<>(perServerMaxCapacity);
        dhm.addServer(12, new SizedHashMap<String, Integer>(perServerMaxCapacity));
        dhm.addServer(18, new SizedHashMap<String, Integer>(perServerMaxCapacity));
        dhm.put("foo", 1);
        dhm.put("bar", 2);
        final int v = dhm.get("bar");
        Assertions.assertEquals(2,v);
        Assertions.assertEquals(1,dhm.get("foo"));
        dhm.remove("foo");
        Assertions.assertNull(dhm.get("foo"));
        dhm.addServer(5, new SizedHashMap<String, Integer>(perServerMaxCapacity));
        dhm.removeServer(12);
        final int v2 = dhm.get("bar");
        Assertions.assertEquals(2,v2);
    }
}
