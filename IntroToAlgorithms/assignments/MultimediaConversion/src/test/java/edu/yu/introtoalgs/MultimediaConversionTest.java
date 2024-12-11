package edu.yu.introtoalgs;
import org.junit.jupiter.api.Test;

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
}
