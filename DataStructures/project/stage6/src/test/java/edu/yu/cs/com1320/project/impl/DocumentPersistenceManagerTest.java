package edu.yu.cs.com1320.project.impl;

import edu.yu.cs.com1320.project.stage6.Document;
import edu.yu.cs.com1320.project.stage6.impl.DocumentImpl;
import edu.yu.cs.com1320.project.stage6.impl.DocumentPersistenceManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DocumentPersistenceManagerTest {
    @Test
    void idkMan() throws URISyntaxException, IOException {
        DocumentPersistenceManager d = new DocumentPersistenceManager();
        URI u = new URI("http://www.yu.edu/documents/docScratch");
        DocumentImpl doc = new DocumentImpl(u, "big fart", null);
        doc.setMetadataValue("lol", "wee");
        d.serialize(u, doc);
        Document paula = d.deserialize(u);
        assertEquals(doc, paula);
        DocumentPersistenceManager craig = new DocumentPersistenceManager(new File("C:\\documents"));
        craig.serialize(u, doc);
        craig.deserialize(u);
        //assertTrue(d.delete(paula.getKey()));
    }
}
