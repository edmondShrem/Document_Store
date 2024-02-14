package edu.yu.cs.com1320.project.stage1;

import edu.yu.cs.com1320.project.stage1.impl.DocumentStoreImpl;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

public class DocumentStoreImplTest {
    DocumentStoreImpl ds;
    InputStream i;
    @BeforeEach
    void beforeEach(){
        ds = new DocumentStoreImpl();
        //might need to make this a byte[] input stream? זה אזה כמות
        i = new InputStream() {
            @Override
            public int read() throws IOException {
                return 0;
            }

            @Override
            public byte[] readAllBytes() throws IOException {
                return "shamalamadingdong".getBytes();
            }
        };
    }
    @AfterEach
    void afterEach() throws IOException {
        i.close();
    }
    @Test
    void badInputGetMDTest(){
        assertThrows(IllegalArgumentException.class, () -> {
            
        });
    }
    @Test
    void putBadTest(){
        assertThrows(IllegalArgumentException.class, () -> {
            ds.put(i, new URI("file:///cheese/grapeSoda"), null);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            ds.put(i, null, DocumentStore.DocumentFormat.TXT);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            ds.put(i, new URI("bruhmoment://192.0.2.16:80"), DocumentStore.DocumentFormat.BINARY);
        });
    }
    @Test
    void deletePutTest() throws URISyntaxException, IOException {
       assertEquals(ds.put(null, new URI("file:///cheese/grapeSoda"), DocumentStore.DocumentFormat.TXT), 0);
       ds.put(i, new URI("file:///cheese/grapeSoda"), DocumentStore.DocumentFormat.BINARY);
       int hash = ds.get(new URI("file:///cheese/grapeSoda")).hashCode();
       assertEquals(ds.put(null, new URI("file:///cheese/grapeSoda"), DocumentStore.DocumentFormat.TXT), hash);
    }
    @Test
    void putAndGetTest() throws URISyntaxException, IOException {
        assertEquals(ds.put(i, new URI("file:///cheese/grapeSoda"), DocumentStore.DocumentFormat.TXT), 0);
        ds.put(i, new URI("file:///squid/grapeSoda"), DocumentStore.DocumentFormat.BINARY);
        assertEquals(ds.get(new URI("file:///cheese/grapeSoda")).getDocumentTxt(), "shamalamadingdong");
        assertArrayEquals(ds.get(new URI("file:///squid/grapeSoda")).getDocumentBinaryData(), "shamalamadingdong".getBytes());
    }

}

