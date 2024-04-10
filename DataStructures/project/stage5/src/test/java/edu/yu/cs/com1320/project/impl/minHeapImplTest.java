package edu.yu.cs.com1320.project.impl;

import edu.yu.cs.com1320.project.MinHeap;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
public class minHeapImplTest {
    MinHeap<String> h;
    @BeforeEach
    void beforeEach(){
        h = new MinHeapImpl();
    }
    @Test
    void bruh(){
       h.insert("a");
        h.insert("b");
        h.insert("c");
        h.insert("d");
    }
}
