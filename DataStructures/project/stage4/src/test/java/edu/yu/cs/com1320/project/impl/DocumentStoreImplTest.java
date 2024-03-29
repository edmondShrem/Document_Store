package edu.yu.cs.com1320.project.impl;

import edu.yu.cs.com1320.project.stage4.Document;
import edu.yu.cs.com1320.project.stage4.DocumentStore;
import edu.yu.cs.com1320.project.stage4.impl.DocumentImpl;
import edu.yu.cs.com1320.project.stage4.impl.DocumentStoreImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        ds.put(new ByteArrayInputStream("3 b''''oi 3".getBytes()), new URI("file:///wee/grapeSoda"), DocumentStore.DocumentFormat.TXT);
        ds.put(new ByteArrayInputStream("bo''''i boi 3 7".getBytes()), new URI("file:///cheese/grapeSoda"), DocumentStore.DocumentFormat.TXT);
        List<Document> list = ds.search("boi");
        assertEquals(new URI("file:///cheese/grapeSoda"), list.get(0).getKey());
    }
    @Test
    void searchPrefixTest() throws URISyntaxException {
        ds.put(new ByteArrayInputStream("%&^%#$&#%$*3 b'oi b''''''''oi bo'''''i bo''''''i 3".getBytes()), new URI("file:///wee/grapeSoda"), DocumentStore.DocumentFormat.TXT);
        ds.put(new ByteArrayInputStream("b'''''''''''''''''''''oing bo''iefvbosuvo b'oiwoufvbwipvefpsivbd b'oingegeg b'oiweofivbepovbdupob 3 7".getBytes()), new URI("file:///cheese/grapeSoda"), DocumentStore.DocumentFormat.TXT);
        List<Document> list = ds.searchByPrefix("boi");
        assertEquals(2, list.size());
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
        ds.put(new ByteArrayInputStream("3 bo''''innnnnnnnnnnn 3".getBytes()), uris[0], DocumentStore.DocumentFormat.TXT);
        ds.put(new ByteArrayInputStream("boiedfjiov boisvnfoseuvnsfepibnsofn 3 7".getBytes()), uris[1], DocumentStore.DocumentFormat.TXT);
        ds.put(new ByteArrayInputStream("3 shomie 3".getBytes()), uris[2], DocumentStore.DocumentFormat.TXT);
        ds.put(new ByteArrayInputStream("crones 3 7".getBytes()), uris[3], DocumentStore.DocumentFormat.TXT);
        ds.put(new ByteArrayInputStream("crones 3 7".getBytes()), uris[4], DocumentStore.DocumentFormat.BINARY);
        ds.setMetadata(uris[4], "cactus", "juice");
        ds.get(uris[4]).setMetadataValue("1" , "2");
        ds.setMetadata(uris[0], "cactus", "juice");
        ds.get(uris[0]).setMetadataValue("1" , "2");
        ds.setMetadata(uris[1], "cacti", "juices");
        ds.setMetadata(uris[2], "cactusy", "juicey");
        ds.setMetadata(uris[3], "cactus", "juice");
        Map<String, String> md = new HashMap<>();
        md.put("cactus", "juice");
        md.put("1", "2");
        List<Document> l = ds.searchByMetadata(md);
        assertEquals(2, l.size());
        assertTrue(l.contains(ds.get(uris[0])));
        assertTrue(l.contains(ds.get(uris[4])));
        md = new HashMap<>();
        l = ds.searchByMetadata(md);
        assertEquals(0, l.size());
    }
    @Test
    void keywordmdsearchTest(){
        ds.put(new ByteArrayInputStream("3 boinnnnnnnnnnnn 3".getBytes()), uris[0], DocumentStore.DocumentFormat.TXT);
        ds.put(new ByteArrayInputStream("boiedfjiov boisvnfoseuvnsfepibnsofn 3 7".getBytes()), uris[1], DocumentStore.DocumentFormat.TXT);
        ds.put(new ByteArrayInputStream("3 shomie 3 3".getBytes()), uris[2], DocumentStore.DocumentFormat.TXT);
        ds.put(new ByteArrayInputStream("crones 7".getBytes()), uris[3], DocumentStore.DocumentFormat.TXT);
        ds.setMetadata(uris[0], "cactus", "juice");
        ds.get(uris[0]).setMetadataValue("cactus" , "juice");
        ds.setMetadata(uris[1], "cacti", "juices");
        ds.setMetadata(uris[2], "cactusy", "juicey");
        ds.setMetadata(uris[3], "cactus", "juice");
        Map<String, String> md = new HashMap<>();
        md.put("cactus", "juice");
        List<Document> l = ds.searchByKeywordAndMetadata("3", md);
        assertEquals(1, l.size());
        assertTrue(l.contains(ds.get(uris[0])));
    }
    @Test
    void preMdSearchTest(){
        ds.put(new ByteArrayInputStream("3 boinnnnnnnnnnnn 3".getBytes()), uris[0], DocumentStore.DocumentFormat.TXT);
        ds.put(new ByteArrayInputStream("boiedfjiov boisvnfoseuvnsfepibnsofn boid 3 7".getBytes()), uris[1], DocumentStore.DocumentFormat.TXT);
        ds.put(new ByteArrayInputStream("3 shomie 3 3".getBytes()), uris[2], DocumentStore.DocumentFormat.TXT);
        ds.put(new ByteArrayInputStream("crones 7".getBytes()), uris[3], DocumentStore.DocumentFormat.TXT);
        ds.setMetadata(uris[0], "cactus", "juice");
        ds.get(uris[0]).setMetadataValue("1", "2");
        ds.setMetadata(uris[1], "cacti", "juices");
        ds.setMetadata(uris[2], "cactusy", "juicey");
        ds.setMetadata(uris[3], "cactus", "juice");
        Map<String, String> md = new HashMap<>();
        md.put("cactus", "juice");
        List<Document> l = ds.searchByPrefixAndMetadata("boi", md);
        assertEquals(1, l.size());
        assertTrue(l.contains(ds.get(uris[0])));
    }
    @Test
    void drDelete(){
        ds.put(new ByteArrayInputStream("3 boinnnnnnnnnnnn 3".getBytes()), uris[0], DocumentStore.DocumentFormat.TXT);
        ds.put(new ByteArrayInputStream("boiedfjiov boisvnfoseuvnsfepibnsofn boid 3 7".getBytes()), uris[1], DocumentStore.DocumentFormat.TXT);
        ds.put(new ByteArrayInputStream("3 shomie 3 3".getBytes()), uris[2], DocumentStore.DocumentFormat.TXT);
        ds.put(new ByteArrayInputStream("crones 7".getBytes()), uris[3], DocumentStore.DocumentFormat.TXT);
        ds.setMetadata(uris[0], "cactus", "juice");
        ds.get(uris[0]).setMetadataValue("1", "2");
        ds.setMetadata(uris[1], "cacti", "juices");
        ds.setMetadata(uris[2], "cactusy", "juicey");
        ds.setMetadata(uris[3], "cactus", "juice");
        Map<String, String> md = new HashMap<>();
        md.put("cactus", "juice");
        Set<URI> s = ds.deleteAllWithMetadata(md);
        assertEquals(2, s.size());
        assertNotNull(ds.get(uris[1]));
        assertNotNull(ds.get(uris[2]));
        assertNull(ds.get(uris[0]));
        assertNull(ds.get(uris[3]));
    }
    @Test
    void deleteKeywordMD(){
        ds.put(new ByteArrayInputStream("3 boinnnnnnnnnnnn 3".getBytes()), uris[0], DocumentStore.DocumentFormat.TXT);
        ds.put(new ByteArrayInputStream("boiedfjiov boisvnfoseuvnsfepibnsofn boid 3 7".getBytes()), uris[1], DocumentStore.DocumentFormat.TXT);
        ds.put(new ByteArrayInputStream("3 shomie 3 3".getBytes()), uris[2], DocumentStore.DocumentFormat.TXT);
        ds.put(new ByteArrayInputStream("crones 7".getBytes()), uris[3], DocumentStore.DocumentFormat.TXT);
        ds.setMetadata(uris[0], "cactus", "juice");
        ds.get(uris[0]).setMetadataValue("1", "2");
        ds.setMetadata(uris[1], "cacti", "juices");
        ds.setMetadata(uris[2], "cactusy", "juicey");
        ds.setMetadata(uris[3], "cactus", "juice");
        Map<String, String> md = new HashMap<>();
        md.put("cactus", "juice");
        Set<URI> s = ds.deleteAllWithKeywordAndMetadata("3", md);
        assertEquals(1, s.size());
        assertNotNull(ds.get(uris[1]));
        assertNotNull(ds.get(uris[2]));
        assertNotNull(ds.get(uris[3]));
        assertNull(ds.get(uris[0]));
    }
    @Test
    void deletePrefMdTest(){
        ds.put(new ByteArrayInputStream("3 boinnnnnnnnnnnn 3".getBytes()), uris[0], DocumentStore.DocumentFormat.TXT);
        ds.put(new ByteArrayInputStream("boiedfjiov boisvnfoseuvnsfepibnsofn boid 3 7".getBytes()), uris[1], DocumentStore.DocumentFormat.TXT);
        ds.put(new ByteArrayInputStream("3 shomie 3 3".getBytes()), uris[2], DocumentStore.DocumentFormat.TXT);
        ds.put(new ByteArrayInputStream("crones 7".getBytes()), uris[3], DocumentStore.DocumentFormat.TXT);
        ds.setMetadata(uris[0], "cactus", "juice");
        ds.get(uris[0]).setMetadataValue("1", "2");
        ds.setMetadata(uris[1], "cacti", "juices");
        ds.setMetadata(uris[2], "cactusy", "juicey");
        ds.setMetadata(uris[3], "cactus", "juice");
        Map<String, String> md = new HashMap<>();
        md.put("cactus", "juice");
        Set<URI> s = ds.deleteAllWithPrefixAndMetadata("boi", md);
        assertEquals(1, s.size());
        assertNotNull(ds.get(uris[1]));
        assertNotNull(ds.get(uris[2]));
        assertNotNull(ds.get(uris[3]));
        assertNull(ds.get(uris[0]));
    }
    //undos -_-
    @Test
    void putUndoTestExtremeTrieEdition(){
        ds.put(new ByteArrayInputStream("3 boinnnnnnnnnnnn 3".getBytes()), uris[0], DocumentStore.DocumentFormat.TXT);
        ds.put(new ByteArrayInputStream("boiedfjiov boisvnfoseuvnsfepibnsofn boid 3 7".getBytes()), uris[1], DocumentStore.DocumentFormat.TXT);
        ds.put(new ByteArrayInputStream("33333 shomie 3 3".getBytes()), uris[2], DocumentStore.DocumentFormat.TXT);
        ds.put(new ByteArrayInputStream("33333 shomie 3 3 crones 7".getBytes()), uris[3], DocumentStore.DocumentFormat.TXT);
        ds.undo(uris[2]);
        assertEquals(3, ds.search("3").size());
        ds.undo();
        assertEquals(2, ds.search("3").size());
        assertNotNull(ds.get(uris[0]));
        assertNotNull(ds.get(uris[1]));
        assertNotNull(ds.get(uris[1]));
    }
    @Test
    void DeleteUndoTestExtremeTrieEdition() throws URISyntaxException {
        ds.put(new ByteArrayInputStream("3 boinnnnnnnnnnnn 3".getBytes()), uris[0], DocumentStore.DocumentFormat.TXT);
        ds.put(new ByteArrayInputStream("boiedfjiov boisvnfoseuvnsfepibnsofn boid 3 7".getBytes()), uris[1], DocumentStore.DocumentFormat.TXT);
        ds.put(new ByteArrayInputStream("33333 shomie 3 3".getBytes()), uris[2], DocumentStore.DocumentFormat.TXT);
        ds.put(new ByteArrayInputStream("33333 shomie 3 3 crones 7".getBytes()), uris[3], DocumentStore.DocumentFormat.TXT);
        ds.deleteAllWithPrefix("boi");
        assertEquals(2, ds.search("3").size());
        ds.undo(uris[1]);
        assertEquals(3, ds.search("3").size());
        beforeEach();
        ds.put(new ByteArrayInputStream("3 boinnnnnnnnnnnn 3".getBytes()), uris[0], DocumentStore.DocumentFormat.TXT);
        ds.put(new ByteArrayInputStream("boiedfjiov boisvnfoseuvnsfepibnsofn boid 3 7".getBytes()), uris[1], DocumentStore.DocumentFormat.TXT);
        ds.put(new ByteArrayInputStream("33333 shomie 3 3".getBytes()), uris[2], DocumentStore.DocumentFormat.TXT);
        ds.put(new ByteArrayInputStream("33333 shomie 3 3 crones 7".getBytes()), uris[3], DocumentStore.DocumentFormat.TXT);
        ds.deleteAllWithPrefix("boi");
        assertEquals(2, ds.search("3").size());
        ds.undo();
        assertEquals(4, ds.search("3").size());
        beforeEach();
        ds.put(new ByteArrayInputStream("3 boinnnnnnnnnnnn 3".getBytes()), uris[0], DocumentStore.DocumentFormat.TXT);
        ds.put(new ByteArrayInputStream("boiedfjiov boisvnfoseuvnsfepibnsofn boid 3 7".getBytes()), uris[1], DocumentStore.DocumentFormat.TXT);
        ds.put(new ByteArrayInputStream("33333 shomie 3 3".getBytes()), uris[2], DocumentStore.DocumentFormat.TXT);
        ds.put(new ByteArrayInputStream("33333 shomie 3 3 crones 7".getBytes()), uris[3], DocumentStore.DocumentFormat.TXT);
        ds.deleteAll("3");
        assertEquals(0, ds.search("3").size());
        ds.undo();
        assertEquals(4, ds.search("3").size());
        assertEquals(0, ds.deleteAllWithPrefix("").size());
    }
    @Test
    void piazzaMadness() throws URISyntaxException {
        ds.put(new ByteArrayInputStream("app 3 boinnnnnnnnnnnn 3".getBytes()), uris[0], DocumentStore.DocumentFormat.TXT);
        ds.put(new ByteArrayInputStream("apple boiedfjiov boisvnfoseuvnsfepibnsofn boid 3 7".getBytes()), uris[1], DocumentStore.DocumentFormat.TXT);
        ds.put(new ByteArrayInputStream("33333 shomie 3 3".getBytes()), uris[2], DocumentStore.DocumentFormat.TXT);
        ds.put(new ByteArrayInputStream("33333 shomie 3 3 crones 7".getBytes()), uris[3], DocumentStore.DocumentFormat.TXT);
        ds.deleteAllWithPrefix("app");
        ds.undo(uris[1]);
        assertNotNull(ds.get(uris[1]));
        assertNull(ds.get(uris[0]));
        beforeEach();
        ds.put(new ByteArrayInputStream("app 3 boinnnnnnnnnnnn 3".getBytes()), uris[0], DocumentStore.DocumentFormat.TXT);
        ds.put(new ByteArrayInputStream("apple boiedfjiov boisvnfoseuvnsfepibnsofn boid 3 7".getBytes()), uris[1], DocumentStore.DocumentFormat.TXT);
        ds.put(new ByteArrayInputStream("33333 shomie 3 3".getBytes()), uris[2], DocumentStore.DocumentFormat.TXT);
        ds.put(new ByteArrayInputStream("33333 shomie 3 3 crones 7".getBytes()), uris[3], DocumentStore.DocumentFormat.TXT);
        ds.put(new ByteArrayInputStream("crones&".getBytes()), uris[3], DocumentStore.DocumentFormat.TXT);
        assertEquals("crones&", ds.get(uris[3]).getDocumentTxt());
        //comment so i can commit again :)
    }
}