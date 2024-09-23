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
        assertEquals(1,t.search("f"));
        assertEquals(1,t.search("a"));
        assertEquals(1,t.search("r"));
        assertEquals(1,t.search("t"));
        assertEquals(1,t.search("fa"));
        assertEquals(1,t.search("ar"));
        assertEquals(1,t.search("rt"));
        assertEquals(1,t.search("far"));
        assertEquals(1,t.search("art"));
        assertEquals(1,t.search("fart"));
    }
    @Test
    void lottaExceptions(){
        //later
    }
    @Test
    void lottaAdds(){
        TerroristNames t = new TerroristNames();
        for(int i = 1000000; i < 2000000; i ++){
            t.add(i + "");
        }
    }
}

