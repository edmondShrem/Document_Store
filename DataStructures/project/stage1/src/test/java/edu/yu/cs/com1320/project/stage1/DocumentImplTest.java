package edu.yu.cs.com1320.project.stage1;

import edu.yu.cs.com1320.project.stage1.impl.DocumentImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.*;
public class DocumentImplTest{
    DocumentImpl d;
    @BeforeEach
    void beforeEach() throws URISyntaxException {
        d = new DocumentImpl(new URI("file:///cheese/grapeSoda"), "hello");
    }
    @Test
    void equalsTest() throws URISyntaxException {
        Integer sameCode = Integer.valueOf(d.hashCode());
        assertFalse(d.equals(sameCode));
        assertEquals(d,d);
        assertNotEquals(d, new DocumentImpl(new URI("file:///cheese/grapeSoda"), "wfew"));
    }
    @Test
    void constructorTXTTestEXP()  {
        assertThrows(IllegalArgumentException.class, () -> {
            new DocumentImpl(null, "");
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new DocumentImpl(new URI("bruhmoment6://192.0.2.16:80"), "yo");
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new DocumentImpl(new URI("hello", "hello", "hello"), "");
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new DocumentImpl(new URI("hello", "hello", "hello"), (String)null);
        });
    }
    @Test
    void constructorARRTestEXp(){
        assertThrows(IllegalArgumentException.class, () -> {
            new DocumentImpl(null, "hi".getBytes());
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new DocumentImpl(new URI("bruhmoment://192.0.2.16:80"), "hi".getBytes());
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new DocumentImpl(new URI("hello", "hello", "hello"), (byte[]) null);
        });
    }
    @Test
    void setterTestBadArgs(){
        assertThrows(IllegalArgumentException.class, () -> {
            d.setMetadataValue(null, "yo");
        });
        assertThrows(IllegalArgumentException.class, () -> {
            d.setMetadataValue("", "yo");
        });
    }
    @Test
    void setterTestGoodArgs(){
        assertNull(d.setMetadataValue("yo", "sup"));
        assertEquals(d.setMetadataValue("yo", "bruh"), "sup");
    }
    @Test
    void getterTestBadArgs(){
        assertThrows(IllegalArgumentException.class, () -> {
            d.getMetadataValue("");
        });
        assertThrows(IllegalArgumentException.class, () -> {
            d.getMetadataValue(null);
        });
    }
    @Test
    void getterTestGoodArgs(){
        assertNull(d.getMetadataValue("yo"));
        d.setMetadataValue("yo", "bro");
        assertEquals(d.getMetadataValue("yo"), "bro");
    }
    @Test
    void getMetaDataTest(){
        d.setMetadataValue("yo", "bro");
        d.setMetadataValue("so", "bro");
        d.setMetadataValue("woe", "bro");
        assertEquals(d.getMetadata().get("so"), d.getMetadataValue("so"));
        assertFalse(d.getMetadata() == d.getMetadata());
    }

    @Test
    void basicGettersTest() throws URISyntaxException {
        assertEquals(d.getDocumentTxt(), "hello");
        assertEquals(d.getKey(), new URI("file:///cheese/grapeSoda"));
        assertNull(d.getDocumentBinaryData());
        DocumentImpl ce = new DocumentImpl(new URI("file:///cheese/grapeSoda"), "hello");
        assertTrue(d.equals(ce));
    }
}