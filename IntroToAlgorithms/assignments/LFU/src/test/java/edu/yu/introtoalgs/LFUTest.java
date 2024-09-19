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

public class LFUTest {
    LFU<Integer, Integer> map;
    @BeforeEach
    void beforeEach(){
        map = new LFU<>(50);
    }

    @Test
    void regSetAndGet(){
        for (int i = 0; i < 25; i++){
            map.set(i, i+1);
        }
        for(int i =0; i < 25; i++){
            assertEquals(map.get(i).get(), i+1);
        }
    }
    @Test
    void iceCreamMachineIsBroken(){
        for (int i = 0; i < 50; i++){
            map.set(i, i+1);
        }
        for (int i = 0; i < 49; i++){
            map.get(i);
        }
        map.set(100,100);
        assertTrue(map.get(49).isEmpty());
        for (int i = 0; i < 48; i++){
            assertEquals(map.get(i).get(), i+1);
        }
    }
    @Test
    void exceptionTest(){
        assertThrows(IllegalArgumentException.class, () -> {
            new LFU<Integer, Integer>( -1);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            map.set(null, 1);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            map.set(1, null);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            map.get(null);
        });
    }
    @Test
    void sizeOfTest(){
        for (int i = 0; i < 25; i++){
            map.set(i, i+1);
        }
        assertEquals(25, map.size());
        for(int i = 100; i < 200; i++){
            map.set(i,i);
        }
        assertEquals(50, map.size());
    }
    @Test
    void clearAndIsEmptyTest(){
        assertTrue(map.isEmpty());
        for(int i = 100; i < 150; i++){
            map.set(i,i);
        }
        assertFalse(map.isEmpty());
        for (int i = 100; i < 150; i++){
            assertEquals(map.get(i).get(), i);
        }
        map.clear();
        for (int i = 100; i < 150; i++){
            assertTrue(map.get(i).isEmpty());
        }
        assertTrue(map.isEmpty());

    }
    @Test
    void runOuttaRoom(){
        LFU<Integer, Integer> cache = new LFU<>(2);
        cache.set(1,1);
        cache.set(2,2);
        cache.get(2);
        cache.get(1);
        cache.set(3,3);
        assertTrue(cache.get(1).isEmpty());

    }

    @Test
    void beegBeegInput(){
        LFU<Integer, Integer> cache = new LFU<>(10000);
        for(int i = 0; i < 8000000; i ++){
            cache.set(i, i);
        }
        for(int i = 8000000; i > 0; i--){
            cache.get(i-1);

        }
        for(int i = 0; i < 8000000; i ++){
            cache.set(i, i);
        }
        for(int i = 7999999; i >= 0; i--){
            cache.get(i);
        }
    }
    @Test
    void fleeeeeen(){
        LFU<Integer, Integer> cache = new LFU<>(5);
        cache.set(1,1);
        cache.set(2,2);
        cache.set(3,3);
        cache.set(4,4);
        cache.set(5,5);
        cache.get(5);
        cache.get(5);
        cache.get(5);
        cache.get(5);
        cache.get(5);
        cache.get(4);
        cache.get(4);
        cache.get(4);
        cache.get(4);
        cache.get(3);
        cache.get(3);
        cache.get(3);
        cache.get(2);
        cache.get(2);
        cache.get(1);
        cache.set(6,6);
        cache.get(6);
        cache.get(6);
        cache.get(6);
        cache.get(6);
        cache.get(6);
        cache.get(6);
        cache.get(6);
        cache.get(6);
        cache.get(6);
        cache.get(6);
        assertTrue(cache.get(1).isEmpty());
        cache.set(7,7);
        cache.get(7);
        cache.get(7);
        cache.get(7);
        cache.get(7);
        cache.get(7);
        cache.get(7);
        cache.get(7);
        cache.get(7);
        cache.get(7);
        cache.get(7);
        assertTrue(cache.get(2).isEmpty());
        cache.set(8,8);
        cache.get(8);
        cache.get(8);
        cache.get(8);
        cache.get(8);
        cache.get(8);
        cache.get(8);
        cache.get(8);
        cache.get(8);
        cache.get(8);
        cache.get(8);
        assertTrue(cache.get(3).isEmpty());
        cache.set(9,9);
        cache.get(6);
        cache.get(9);
        cache.get(9);
        cache.get(9);
        cache.get(9);
        cache.get(9);
        cache.get(9);
        cache.get(9);
        cache.get(9);
        cache.get(9);
        assertTrue(cache.get(4).isEmpty());
        cache.set(10,10);
        cache.get(10);
        cache.get(10);
        cache.get(10);
        cache.get(10);
        cache.get(10);
        assertTrue(cache.get(5).isEmpty());
    }
}
