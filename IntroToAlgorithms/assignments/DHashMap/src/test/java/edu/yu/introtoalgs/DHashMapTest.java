package edu.yu.introtoalgs;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions.*;

import java.util.Collection;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DHashMapTest {
    DHashMap<Integer, String> m = new DHashMap<>(5);
    DHashMap<String, String> h = new DHashMap<>(5);
    @BeforeEach
    void beforeEach(){
        m = new DHashMap<>(5);
        h = new DHashMap<>(5);
        m.addServer(0, new SizedHashMap<>(5));
        h.addServer(0, new SizedHashMap<>(5));
        h.addServer(1, new SizedHashMap<>(5));
        h.addServer(2, new SizedHashMap<>(5));
        h.addServer(3, new SizedHashMap<>(5));
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
        Assertions.assertEquals("t",m.put(1, "z"));
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
        assertNotNull(h.get("Ea"));
        assertNotNull(h.get("Wa"));
        assertThrows(
                IllegalArgumentException.class,
                () -> h.get(null)
        );
    }






    @Test
    void sCollide(){
        h.put("336", "0");
        h.put("336", "0");
        h.put("1ot", "1");
        h.put("1ot", "1");
        h.put("1pU", "2");
        h.removeServer(0);
        h.put("1q6", "3");
        h.put("2Pt", "4");
        h.put("2QU", "5");
        h.put("2R6", "6");
        h.put("2QU", null);
        assertNotNull(h.get("336"));
        assertNotNull(h.get("1pU"));
        assertNotNull(h.get("1q6"));
        assertNotNull(h.get("2Pt"));
        assertNull(h.get("2QU"));
        assertNotNull(h.get("2R6"));
    }

    @Test
    void sadPathConstructorAndAddServer(){
       assertThrows(
                IllegalArgumentException.class,
                () -> new DHashMap<>(0)
        );
       //negative
        assertThrows(
                IllegalArgumentException.class,
                () -> m.addServer(-2, new SizedHashMap<Integer, String>(5))
        );
        //already exists in the set
        assertThrows(
                IllegalArgumentException.class,
                () -> m.addServer(0, new SizedHashMap<Integer, String>(5))
        );
        //null map
        assertThrows(
                IllegalArgumentException.class,
                () -> m.addServer(200, null)
        );
    }
    @Test
    void removeServerSadPath(){
        //negative
        assertThrows(
                IllegalArgumentException.class,
                () -> m.removeServer(-2)
        );
        //not there
        assertThrows(
                IllegalArgumentException.class,
                () -> m.removeServer(12)
        );
    }
    @Test
    void putSadPath(){
        //null key
        assertThrows(
                IllegalArgumentException.class,
                () -> m.put(null,"lol")
        );
        m.put(1,"1");
        m.put(2,"1");
        m.put(3,"1");
        m.put(4,"1");
        m.put(5,"1");
        m.addServer(17, new SizedHashMap<>(5));
        m.put(13,"1");
        m.put(23,"1");
        m.put(33,"1");
        m.put(43,"1");
        m.put(53,"1");
        assertThrows(
                IllegalArgumentException.class,
                () -> m.put(6,"too much")
        );
        DHashMap<Integer,Integer> d = new DHashMap<>(5);
        assertThrows(
                IllegalStateException.class,
                () -> d.put(6,6)
        );
    }
    @Test
    void getSadPath(){
        m.put(21,"f");
        assertThrows(
                IllegalArgumentException.class,
                () -> m.get(null)
        );
    }
    @Test
    void removeSadPath(){
        m.put(21,"f");
        assertThrows(
                IllegalArgumentException.class,
                () -> m.remove(null)
        );
    }
    @Test
    void thousandPutsThenGets(){
        DHashMap<Integer, Integer> boi = new DHashMap<>(100);
        for(int i = 0; i < 10; i++){
            boi.addServer(i, new SizedHashMap<>(100));
        }
        for(int i = 0; i < 1000; i ++){
            boi.put(i,i);
        }
        for(int i = 0; i < 10; i++){
            boi.addServer(i+100, new SizedHashMap<>(100));
        }
        for(int i = 0; i < 1000; i ++){
            boi.put(i,i);
        }
        for(int i = 0; i < 1000; i++){
            assertEquals(i, boi.get(i));
        }
    }

    @Test
    void thousandPutsThenGetsButIDeleteSomeExtraServers(){
        DHashMap<Integer, Integer> boi = new DHashMap<>(100);
        for(int i = 0; i < 100; i++){
            boi.addServer(i, new SizedHashMap<>(100));
        }
        for(int i = 0; i < 1000; i ++){
            boi.put(i,i);
        }
        boi.removeServer(50);
        boi.removeServer(60);
        for(int i = 0; i < 1000; i++){
            assertEquals(i, boi.get(i));
        }
    }
    @Test
    void lotsOfInputButIAddServersInTheMiddle(){
        DHashMap<Integer, Integer> boi = new DHashMap<>(10);
        for(int i = 0; i < 1000; i++){
            boi.addServer(i, new SizedHashMap<>(10));
        }
        for(int i = 0; i < 1000; i ++){
            boi.put(i,i);
        }
        boi.removeServer(500);
        boi.removeServer(600);
        boi.addServer(500,new SizedHashMap<>(10));
        boi.addServer(600,new SizedHashMap<>(10));
        for(int i = 0; i < 1000; i++){
            assertEquals(i, boi.get(i));
        }
    }

    @Test
    void removingAServerButTheresNoRoom(){
        DHashMap<Integer, Integer> boi = new DHashMap<>(10);
        for(int i = 0; i < 100; i++){
            boi.addServer(i, new SizedHashMap<>(10));
        }
        for(int i = 0; i < 1000; i ++){
            boi.put(i,i);
        }
        assertThrows(
                IllegalArgumentException.class,
                () -> boi.removeServer(5)
        );

    }
    @Test
    void lotsOfPuts(){
        DHashMap<Integer, Integer> boi = new DHashMap<>(100);
        for(int i = 0; i < 100; i++){
            boi.addServer(i, new SizedHashMap<>(100));
        }
        for(int i = 0; i < 4000; i ++){
            boi.put(i,i);
        }
    }

}
