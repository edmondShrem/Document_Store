package edu.yu.cs.com1320.project.stage1.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.*;
public class DocumentImplTest{
    DocumentImpl d;
    @BeforeEach
    void beforeEach() throws URISyntaxException {
        d = new DocumentImpl(new URI("file:///foo/bar"), "hello");
    }
    @Test
    void constructorTXTTest() throws URISyntaxException {
        assertThrows(IllegalArgumentException.class, () -> {
            new DocumentImpl(null, "");
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new DocumentImpl(new URI(""), "yo");
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new DocumentImpl(new URI("hello", "hello", "hello"), "");
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new DocumentImpl(new URI("hello", "hello", "hello"), (String)null);
        });
    }
    @Test
    void constructorARRTest(){
        assertThrows(IllegalArgumentException.class, () -> {
            new DocumentImpl(null, "hi".getBytes());
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new DocumentImpl(new URI(""), "hi".getBytes());
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new DocumentImpl(new URI("hello", "hello", "hello"), (byte[]) null);
        });
    }

}