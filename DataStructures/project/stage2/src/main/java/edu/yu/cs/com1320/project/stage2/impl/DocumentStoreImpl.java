package edu.yu.cs.com1320.project.stage2.impl;

import edu.yu.cs.com1320.project.HashTable;
import edu.yu.cs.com1320.project.impl.HashTableImpl;
import edu.yu.cs.com1320.project.stage2.Document;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
public class DocumentStoreImpl implements edu.yu.cs.com1320.project.stage2.DocumentStore{
    private HashTable<URI, DocumentImpl> docs;

    public DocumentStoreImpl(){
        this.docs = new HashTableImpl<>();
    }

    @Override
    public String setMetadata(URI uri, String key, String value) {
        if(uri == null || uri.getPath() == null || uri.getPath().equals("") || key == null || key.equals("") || docs.get(uri) == null){
            throw new IllegalArgumentException("the uri is null or blank, if there is no document stored at that uri, or the key is null or blank");
        }
        return this.docs.get(uri).setMetadataValue(key, value);
    }

    @Override
    public String getMetadata(URI uri, String key) {
        if(uri == null || uri.getPath() == null || uri.getPath().equals("") || key == null || key.equals("") || docs.get(uri) == null){
            throw new IllegalArgumentException("the uri is null or blank, if there is no document stored at that uri, or the key is null or blank");
        }
        return this.docs.get(uri).getMetadataValue(key);
    }

    @Override
    public int put(InputStream input, URI uri, DocumentFormat format) {
        if(uri == null || uri.getPath() == null || uri.getPath().equals("") || format == null){
            throw new IllegalArgumentException("uri is null or empty, or format is null");
        }
        if(input == null){
            DocumentImpl prev = docs.put(uri, null);
            return (prev == null ? 0 : prev.hashCode());
        }
        byte[] bytes;
        while(true) {
            try {
                bytes = input.readAllBytes();
                input.close();
                break;
            } catch (IOException e){
                continue;
            }
        }
        DocumentImpl prev;
        if(format == DocumentFormat.TXT){
            prev = docs.put(uri, new DocumentImpl(uri, new String(bytes)));
        } else{
            prev = docs.put(uri, new DocumentImpl(uri, bytes));
        }
        if (prev == null){
            return 0;
        } else {
            return prev.hashCode();
        }
    }

    @Override
    public Document get(URI url) {
        return docs.get(url);
    }

    @Override
    public boolean delete(URI url) {
        return docs.put(url, null) != null;
    }
}


