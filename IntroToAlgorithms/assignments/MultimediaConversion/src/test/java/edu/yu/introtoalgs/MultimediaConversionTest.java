package edu.yu.introtoalgs;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
public class MultimediaConversionTest {
    @Test
    void shmestOnlyOneShortestPath(){
        MultimediaConversion m = new MultimediaConversion("a");
        m.add("a", "b", 10);
        m.add("b", "c", 10);
        m.add("a", "c", 20);
        Map<String, Double> h = m.convert("b", "c");
        assertEquals(10, h.get("b"));
        assertEquals(20, h.get("c"));

    }
    @Test
    void theOneWhereThereAreMultipleAndBFSWillPickTheWrongOneMaybe(){
        MultimediaConversion m = new MultimediaConversion("a");
        m.add("a", "b", 1);
        m.add("b", "c", 1);
        m.add("a", "c", 1);
        m.add("b", "d", 5);
        m.add("c", "d", 1);
        Map<String, Double> h = m.convert("d");
        assertEquals(2, h.get("d"));

    }
    @Test
    void anothaoneathose(){
        MultimediaConversion m = new MultimediaConversion("a");
        m.add("a", "b", 1);
        m.add("b", "c", 1);
        m.add("a", "c", 1);
        m.add("b", "d", 5);
        m.add("c", "d", 1);
        m.add("d","e", 5);
        Map<String, Double> h = m.convert("e");
        assertEquals(7, h.get("e"));
    }
    @Test
    void minNodesCompleteGraph(){
        MultimediaConversion m = new MultimediaConversion("1");
        int total = 0;

        for(int s = 0; s < 90; s++){
            for(int i = s; i < 90; i++){
                try {
                    m.add(s + "",i + "",Math.random());
                    total++;
                } catch (Exception e) {
                }
            }

        }
        Map<String, Double> h = m.convert("12");
        System.out.println(total);

        }
    @Test
    void tooBigInCase(){
        MultimediaConversion m = new MultimediaConversion("1");
        int total = 0;
        long l = System.currentTimeMillis();
        for(int s = 0; s < 4000; s++){
            for(int i = s; i < 4000; i++){
                try {
                    m.add(s + "",i + "",Math.random());
                    total++;
                } catch (Exception e) {
                }
            }

        }
        Map<String, Double> h = m.convert("12", "32");
        System.out.println(System.currentTimeMillis() - l);
        System.out.println(total);
    }

    @Test
    void maxNodes(){
        MultimediaConversion m = new MultimediaConversion("1");
        int total = 0;

        for(int s = 1; s < 4000; s++){
                try {
                    m.add(s + "",(s+1) + "",Math.random());
                    total++;
                } catch (Exception e) {
                }
            }
        Map<String, Double> h = m.convert("400");
        System.out.println(total);

        }

    }

