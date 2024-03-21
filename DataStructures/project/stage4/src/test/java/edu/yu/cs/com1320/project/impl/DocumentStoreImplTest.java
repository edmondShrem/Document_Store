package edu.yu.cs.com1320.project.impl;

import edu.yu.cs.com1320.project.stage4.Document;
import edu.yu.cs.com1320.project.stage4.DocumentStore;
import edu.yu.cs.com1320.project.stage4.impl.DocumentStoreImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class DocumentStoreImplTest {
    DocumentStoreImpl ds;
    InputStream i;
    InputStream[] inputStreams;
    URI[] uris;
    @BeforeEach
    void beforeEach() throws URISyntaxException {
        ds = new DocumentStoreImpl();
        //might need to make this a byte[] input stream? זה אזה כמות
        i = new ByteArrayInputStream("shamalamadingdong".getBytes());
        uris = new URI[100];
        inputStreams = new InputStream[100];
        for(int i = 0; i < 100; i ++){
            uris[i] = new URI("file:///che" + i + "ese/grapeSoda");
            inputStreams[i] = new ByteArrayInputStream(("THE the tHe thE They're " + i + " " + i%10).getBytes());
        }
    }

    @AfterEach
    void afterEach() throws IOException {
        i.close();
    }
    @Test
    void metaUndoTest() throws URISyntaxException {
        ds.put(i, new URI("file:///cheese/grapeSoda"), DocumentStore.DocumentFormat.TXT);
        ds.setMetadata(new URI("file:///cheese/grapeSoda"), "file:///cheese/grapeSoda", "bro");
        ds.setMetadata(new URI("file:///cheese/grapeSoda"), "file:///cheese/grapeSoda", "show");
        ds.setMetadata(new URI("file:///cheese/grapeSoda"), "file:///cheese/grapeSoda", "sow");
        ds.undo(new URI("file:///cheese/grapeSoda"));
        assertEquals("show", ds.getMetadata(new URI("file:///cheese/grapeSoda"), "file:///cheese/grapeSoda"));
        ds.undo();
        assertEquals("bro", ds.getMetadata(new URI("file:///cheese/grapeSoda"), "file:///cheese/grapeSoda"));
    }
    @Test
    void putUndoTest() throws URISyntaxException {
        ds.put(new ByteArrayInputStream("1".getBytes()), new URI("file:///wee/grapeSoda"), DocumentStore.DocumentFormat.TXT);
        ds.put(new ByteArrayInputStream("2".getBytes()), new URI("file:///wee/grapeSoda"), DocumentStore.DocumentFormat.TXT);
        ds.put(new ByteArrayInputStream("3".getBytes()), new URI("file:///wee/grapeSoda"), DocumentStore.DocumentFormat.TXT);
        ds.put(new ByteArrayInputStream("boi".getBytes()), new URI("file:///cheese/grapeSoda"), DocumentStore.DocumentFormat.TXT);
        ds.put(new ByteArrayInputStream("4".getBytes()), new URI("file:///wee/grapeSoda"), DocumentStore.DocumentFormat.TXT);
        ds.put(new ByteArrayInputStream("5".getBytes()), new URI("file:///wee/grapeSoda"), DocumentStore.DocumentFormat.TXT);
        ds.put(new ByteArrayInputStream("6".getBytes()), new URI("file:///wee/grapeSoda"), DocumentStore.DocumentFormat.TXT);
        ds.put(new ByteArrayInputStream("7".getBytes()), new URI("file:///wee/grapeSoda"), DocumentStore.DocumentFormat.TXT);
        ds.undo();
        assertEquals("6", ds.get(new URI("file:///wee/grapeSoda")).getDocumentTxt());
        ds.undo(new URI("file:///wee/grapeSoda"));
        //found the isue. when calling this way, we remove multiple items
        assertEquals("5",ds.get(new URI("file:///wee/grapeSoda")).getDocumentTxt());
        ds.undo(new URI("file:///wee/grapeSoda"));
        assertEquals("4", ds.get(new URI("file:///wee/grapeSoda")).getDocumentTxt());
        ds.undo(new URI("file:///wee/grapeSoda"));
        assertEquals("3", ds.get(new URI("file:///wee/grapeSoda")).getDocumentTxt());
        ds.undo(new URI("file:///wee/grapeSoda"));
        assertEquals("2", ds.get(new URI("file:///wee/grapeSoda")).getDocumentTxt());
        ds.undo(new URI("file:///wee/grapeSoda"));
        assertEquals("1", ds.get(new URI("file:///wee/grapeSoda")).getDocumentTxt());
        assertEquals("boi", ds.get(new URI("file:///cheese/grapeSoda")).getDocumentTxt());
        ds.undo();
        assertNull(ds.get(new URI("file:///cheese/grapeSoda")));
    }
    @Test
    void undoDeleteTest() throws URISyntaxException {
        ds.put(new ByteArrayInputStream("1".getBytes()), new URI("file:///wee/grapeSoda"), DocumentStore.DocumentFormat.TXT);
        ds.put(new ByteArrayInputStream("2".getBytes()), new URI("file:///wee/grapeSoda"), DocumentStore.DocumentFormat.TXT);
        ds.put(new ByteArrayInputStream("3".getBytes()), new URI("file:///wee/grapeSoda"), DocumentStore.DocumentFormat.TXT);
        ds.put(new ByteArrayInputStream("boi".getBytes()), new URI("file:///cheese/grapeSoda"), DocumentStore.DocumentFormat.TXT);
        ds.put(new ByteArrayInputStream("4".getBytes()), new URI("file:///wee/grapeSoda"), DocumentStore.DocumentFormat.TXT);
        ds.put(new ByteArrayInputStream("5".getBytes()), new URI("file:///wee/grapeSoda"), DocumentStore.DocumentFormat.TXT);
        ds.put(new ByteArrayInputStream("6".getBytes()), new URI("file:///wee/grapeSoda"), DocumentStore.DocumentFormat.TXT);
        ds.put(new ByteArrayInputStream("7".getBytes()), new URI("file:///wee/grapeSoda"), DocumentStore.DocumentFormat.TXT);
        ds.delete(new URI("file:///wee/grapeSoda"));
        ds.undo();
        assertEquals("7", ds.get(new URI("file:///wee/grapeSoda")).getDocumentTxt());
        ds.undo(new URI("file:///wee/grapeSoda"));
        assertEquals("6", ds.get(new URI("file:///wee/grapeSoda")).getDocumentTxt());
        ds.undo(new URI("file:///wee/grapeSoda"));
        //found the isue. when calling this way, we remove multiple items
        assertEquals("5",ds.get(new URI("file:///wee/grapeSoda")).getDocumentTxt());
        ds.undo(new URI("file:///wee/grapeSoda"));
        assertEquals("4", ds.get(new URI("file:///wee/grapeSoda")).getDocumentTxt());
        ds.undo(new URI("file:///wee/grapeSoda"));
        assertEquals("3", ds.get(new URI("file:///wee/grapeSoda")).getDocumentTxt());
        ds.undo(new URI("file:///wee/grapeSoda"));
        assertEquals("2", ds.get(new URI("file:///wee/grapeSoda")).getDocumentTxt());
        ds.undo(new URI("file:///wee/grapeSoda"));
        assertEquals("1", ds.get(new URI("file:///wee/grapeSoda")).getDocumentTxt());
        ds.put(new ByteArrayInputStream("quack".getBytes()), new URI("file:///quack/grapeSoda"), DocumentStore.DocumentFormat.TXT);
        ds.undo(new URI("file:///wee/grapeSoda"));
        assertEquals("quack", ds.get(new URI("file:///quack/grapeSoda")).getDocumentTxt());
    }

    @Test
    void everythingEverywhereAllAtOnce() throws URISyntaxException {
        ds.put(new ByteArrayInputStream("3".getBytes()), new URI("file:///wee/grapeSoda"), DocumentStore.DocumentFormat.TXT);
        ds.put(new ByteArrayInputStream("boi".getBytes()), new URI("file:///cheese/grapeSoda"), DocumentStore.DocumentFormat.TXT);
        ds.setMetadata(new URI("file:///wee/grapeSoda"), "file:///wee/grapeSoda", "jabroni");
        ds.delete(new URI("file:///cheese/grapeSoda"));
        ds.put(new ByteArrayInputStream("fleeb".getBytes()), new URI("file:///slow/grapeSoda"), DocumentStore.DocumentFormat.TXT);
        ds.undo();
        assertNull(ds.get(new URI("file:///slow/grapeSoda")));
        ds.undo(new URI("file:///wee/grapeSoda"));
        assertEquals("3", ds.get(new URI("file:///wee/grapeSoda")).getDocumentTxt());
        ds.undo();
        assertNotNull(ds.get(new URI("file:///cheese/grapeSoda")));
    }

    @Test
    void stackTest(){
        StackImpl<String> s = new StackImpl<>();
        assertNull(s.pop());
        assertNull(s.peek());
        s.push(null);
        assertEquals(0, s.size());
        s.push("ji");
        s.push(null);
        s.push("wee");
        assertEquals(2, s.size());
        assertEquals("wee", s.pop());
        assertEquals("ji", s.pop());
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
            ds.put(i, new URI("https://192.0.2.16:80"), DocumentStore.DocumentFormat.BINARY);
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
        i = new ByteArrayInputStream("shamalamadingdong".getBytes());
        ds.put(i, new URI("file:///squid/grapeSoda"), DocumentStore.DocumentFormat.BINARY);
        assertEquals(ds.get(new URI("file:///cheese/grapeSoda")).getDocumentTxt(), "shamalamadingdong");
        assertArrayEquals(ds.get(new URI("file:///squid/grapeSoda")).getDocumentBinaryData(), "shamalamadingdong".getBytes());
    }
    //trie time :cool:
    @Test
    void searchTest() throws URISyntaxException {
        ds.put(new ByteArrayInputStream("3 boi 3".getBytes()), new URI("file:///wee/grapeSoda"), DocumentStore.DocumentFormat.TXT);
        ds.put(new ByteArrayInputStream("boi boi 3 7".getBytes()), new URI("file:///cheese/grapeSoda"), DocumentStore.DocumentFormat.TXT);
        List<Document> list = ds.search("boi");
        assertEquals(new URI("file:///cheese/grapeSoda"), list.get(0).getKey());
    }
    @Test
    void searchPrefixTest() throws URISyntaxException {
        ds.put(new ByteArrayInputStream("3 boi boi boi boi 3".getBytes()), new URI("file:///wee/grapeSoda"), DocumentStore.DocumentFormat.TXT);
        ds.put(new ByteArrayInputStream("boing boiefvbosuvo boiwoufvbwipvefpsivbd boingegeg boiweofivbepovbdupob 3 7".getBytes()), new URI("file:///cheese/grapeSoda"), DocumentStore.DocumentFormat.TXT);
        List<Document> list = ds.searchByPrefix("boi");
        assertEquals(new URI("file:///cheese/grapeSoda"), list.get(0).getKey());
    }
    @Test
    void deleteAll() throws URISyntaxException {
        ds.put(new ByteArrayInputStream("3 boi 3".getBytes()), new URI("file:///wee/grapeSoda"), DocumentStore.DocumentFormat.TXT);
        ds.put(new ByteArrayInputStream("boi boi 3 7".getBytes()), new URI("file:///cheese/grapeSoda"), DocumentStore.DocumentFormat.TXT);
        ds.put(new ByteArrayInputStream("3 shomie 3".getBytes()), new URI("file:///shlee/grapeSoda"), DocumentStore.DocumentFormat.TXT);
        ds.put(new ByteArrayInputStream("crones 3 7".getBytes()), new URI("file:///please/grapeSoda"), DocumentStore.DocumentFormat.TXT);
        Set<URI> s = ds.deleteAll("boi");
        assertTrue(s.contains(new URI("file:///wee/grapeSoda")));
        assertTrue(s.contains(new URI("file:///cheese/grapeSoda")));
        assertFalse(s.contains(new URI("file:///shlee/grapeSoda")));
        assertFalse(s.contains(new URI("file:///please/grapeSoda")));
        assertNull(ds.get(new URI("file:///wee/grapeSoda")));
        assertNull(ds.get(new URI("file:///cheese/grapeSoda")));
        assertNotNull(ds.get(new URI("file:///shlee/grapeSoda")));
        assertNotNull(ds.get(new URI("file:///please/grapeSoda")));
    }
    @Test
    void deleteAllWithPrefixTest() throws URISyntaxException {
        ds.put(new ByteArrayInputStream("3 boinnnnnnnnnnnn 3".getBytes()), new URI("file:///wee/grapeSoda"), DocumentStore.DocumentFormat.TXT);
        ds.put(new ByteArrayInputStream("boiedfjiov boisvnfoseuvnsfepibnsofn 3 7".getBytes()), new URI("file:///cheese/grapeSoda"), DocumentStore.DocumentFormat.TXT);
        ds.put(new ByteArrayInputStream("3 shomie 3".getBytes()), new URI("file:///shlee/grapeSoda"), DocumentStore.DocumentFormat.TXT);
        ds.put(new ByteArrayInputStream("crones 3 7".getBytes()), new URI("file:///please/grapeSoda"), DocumentStore.DocumentFormat.TXT);
        Set<URI> s = ds.deleteAllWithPrefix("boi");
        assertTrue(s.contains(new URI("file:///wee/grapeSoda")));
        assertTrue(s.contains(new URI("file:///cheese/grapeSoda")));
        assertFalse(s.contains(new URI("file:///shlee/grapeSoda")));
        assertFalse(s.contains(new URI("file:///please/grapeSoda")));
        assertNull(ds.get(new URI("file:///wee/grapeSoda")));
        assertNull(ds.get(new URI("file:///cheese/grapeSoda")));
        assertNotNull(ds.get(new URI("file:///shlee/grapeSoda")));
        assertNotNull(ds.get(new URI("file:///please/grapeSoda")));
    }
    //the joke is meta data = md = doctorb i is smort ^u^
    @Test
    void drSearch(){
        
    }
}

