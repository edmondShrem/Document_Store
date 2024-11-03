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
public class TerrorTests {
    @Test
    void doesItWork(){
        TerroristNames t = new TerroristNames();
        t.add("fartfart");
        t.add("fart");
        assertEquals(2,t.search("f"));
        assertEquals(2,t.search("a"));
        assertEquals(2,t.search("r"));
        assertEquals(2,t.search("t"));
        assertEquals(2,t.search("fa"));
        assertEquals(2,t.search("ar"));
        assertEquals(2,t.search("rt"));
        assertEquals(2,t.search("far"));
        assertEquals(2,t.search("art"));
        assertEquals(2,t.search("fart"));
    }
    @Test
    void bohl(){
        TerroristNames t = new TerroristNames();
        for(int i = 100; i < 200; i++){
            t.add(i+"");
        }
        assertEquals(100, t.search(1+""));
        assertEquals(0, t.search("fart"));
    }
    @Test
    void lottaExceptions(){
        //later
    }
    @Test
    void lottaAdds(){
        TerroristNames t = new TerroristNames();
        for(int i = 0; i < 500000; i ++){
            t.add("ab" + i);
        }
        for(int i = 0; i < 1000000; i ++){
            t.search("ab" + i);
        }
    }
}

