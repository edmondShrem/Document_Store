package edu.yu.cs.com1320.project.impl;

import edu.yu.cs.com1320.project.stage6.Document;
import edu.yu.cs.com1320.project.stage6.DocumentStore;
import edu.yu.cs.com1320.project.stage6.impl.DocumentStoreImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.File;
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
            uris[i] = new URI("http://www.yu.edu/documents/doc" + i);
            inputStreams[i] = new ByteArrayInputStream(("THE the tHe thE They're " + i + " " + i%10).getBytes());
        }
    }
    private boolean deleteDirectory(File directoryToBeDeleted) {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }
        return directoryToBeDeleted.delete();
    }

    @AfterEach
    void afterEach() throws IOException {
        i.close();
        for(URI u : uris){
            String path = u.toString();
            if(path.startsWith("http://")){
                path = path.substring(7);
            } else if (path.startsWith("https://")){
                path = path.substring(8);
            }
            File f = new File(path + ".json");
            boolean deleted = f.delete();
            if(deleted){
                while(f.getParent() != null) {
                    f = new File(f.getParent());
                    f.delete();
                }
            }
        }
    }
    private File gimmeAFile(URI u){
        String path = u.toString();
        if(path.startsWith("http://")){
            path = path.substring(7);
        } else if (path.startsWith("https://")){
            path = path.substring(8);
        }
        File f = new File(path + ".json");
        return f;
    }

    //welp time to write new tests i guess
    @Test
    void basicOperations(){
        for(int i =0; i < 5; i++){
            ds.put(new ByteArrayInputStream("doc".getBytes()), uris[i], DocumentStore.DocumentFormat.TXT);
        }
        ds.setMaxDocumentCount(2);
        assertNotNull(ds.get(uris[1]));
    }
    @Test
    void metaBasics(){
        for(int i =0; i < 5; i++){
            ds.put(new ByteArrayInputStream("doc".getBytes()), uris[i], DocumentStore.DocumentFormat.TXT);
        }
        ds.setMetadata(uris[0], "big", "burger");
        ds.setMaxDocumentBytes(6);
        assertTrue(gimmeAFile(uris[1]).exists());
        assertTrue(gimmeAFile(uris[2]).exists());
        assertTrue(gimmeAFile(uris[3]).exists());
        assertFalse(gimmeAFile(uris[4]).exists());
        assertFalse(gimmeAFile(uris[0]).exists());
    }
    @Test
    void againButSearch(){
        for(int i =0; i < 5; i++){
            ds.put(new ByteArrayInputStream("doc".getBytes()), uris[i], DocumentStore.DocumentFormat.TXT);
        }
        ds.setMetadata(uris[0], "big", "burger");
        ds.setMetadata(uris[0], "small", "steak");
        ds.setMetadata(uris[4], "big", "burger");
        ds.get(uris[1]);
        ds.get(uris[2]);
        ds.get(uris[3]);
        ds.setMaxDocumentBytes(6);
        assertTrue(gimmeAFile(uris[4]).exists());
        assertTrue(gimmeAFile(uris[1]).exists());
        assertTrue(gimmeAFile(uris[0]).exists());
        assertFalse(gimmeAFile(uris[2]).exists());
        assertFalse(gimmeAFile(uris[3]).exists());
        Map<String, String> m = new HashMap<>();
        m.put("big", "burger");
        ds.searchByMetadata(m);
        assertTrue(gimmeAFile(uris[3]).exists());
        assertTrue(gimmeAFile(uris[1]).exists());
        assertTrue(gimmeAFile(uris[2]).exists());
        assertFalse(gimmeAFile(uris[4]).exists());
        assertFalse(gimmeAFile(uris[0]).exists());
    }
    @Test
    void smolDelete(){
        for(int i =0; i < 5; i++){
            ds.put(new ByteArrayInputStream("doc".getBytes()), uris[i], DocumentStore.DocumentFormat.TXT);
        }
        ds.setMaxDocumentCount(2);
        ds.delete(uris[3]);
        assertNull(ds.get(uris[3]));
        ds.put(new ByteArrayInputStream("doc".getBytes()), uris[3], DocumentStore.DocumentFormat.TXT);
        assertNotNull(ds.get(uris[3]));
        assertFalse(gimmeAFile(uris[3]).exists());
    }
    @Test
    void testing2dot4InTheSpecYkTheOne(){
        for(int i =0; i < 5; i++){
            ds.put(new ByteArrayInputStream("doc".getBytes()), uris[i], DocumentStore.DocumentFormat.TXT);
        }
        ds.setMaxDocumentBytes(6);
        ds.put(new ByteArrayInputStream("doc".getBytes()), uris[3], DocumentStore.DocumentFormat.TXT);
        assertFalse(gimmeAFile(uris[3]).exists());
    }
    @Test
    void wordCountMapAndTime(){
        for(int i =0; i < 5; i++){
            ds.put(new ByteArrayInputStream("doc doc goose".getBytes()), uris[i], DocumentStore.DocumentFormat.TXT);
        }
        HashMap<String,Integer> h = ds.get(uris[0]).getWordMap();
        Long t = ds.get(uris[0]).getLastUseTime();
        ds.setMaxDocumentCount(2);
        assertEquals(h, ds.get(uris[0]).getWordMap());
        assertNotEquals(t, ds.get(uris[0]).getLastUseTime());
    }
}
