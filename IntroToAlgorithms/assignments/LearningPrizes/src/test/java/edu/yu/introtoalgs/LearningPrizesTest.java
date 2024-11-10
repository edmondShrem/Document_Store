package edu.yu.introtoalgs;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


import java.util.Iterator;

public class LearningPrizesTest {
    LearningPrizes lp;
    @BeforeEach
    void beforeEach(){
        lp = new LearningPrizes(2);
    }
    @Test
    void doesItExplodePlusSomeThingtofeelgoodabout(){
        LearningPrizes l = new LearningPrizes(5);
        l.addTicket(1,1,1.5);
        l.addTicket(1,2,2.5);
        Iterator<Double> i = l.awardedPrizeMoney();
        assertEquals(5, i.next());
    }
    @Test
    void correctness(){
        lp.addTicket(1,1,10);
        lp.addTicket(1,2,13);
        lp.addTicket(1,11,1);
        lp.addTicket(2,1,6);
        lp.addTicket(2,4, 5);
        lp.addTicket(3,1,15);
        lp.addTicket(3,5,7);
        Iterator<Double> i = lp.awardedPrizeMoney();
        assertEquals(24, i.next());
        assertEquals(10, i.next());
        assertEquals(18, i.next());
        lp.addTicket(4,1,15);
        lp.addTicket(4,5,7);
        i = lp.awardedPrizeMoney();
        assertEquals(24, i.next());
        assertEquals(10, i.next());
        assertEquals(18, i.next());
        assertEquals(16, i.next());
        assertFalse(i.hasNext());
    }
    @Test
    void theBadStuffTest(){

    }
    @Test
    void slowTest(){
        for(int i = 1; i < 65000; i ++){
            lp.addTicket(i,i,i);
            lp.addTicket(i,i+1,i+3);
            lp.addTicket(i,i+2,i+5);
        }
        Iterator<Double> p = lp.awardedPrizeMoney();
        while(p.hasNext()){
            p.next();
        }
    }
}
