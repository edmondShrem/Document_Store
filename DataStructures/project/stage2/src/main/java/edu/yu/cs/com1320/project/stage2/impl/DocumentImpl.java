package edu.yu.cs.com1320.project.stage2.impl;

import edu.yu.cs.com1320.project.HashTable;
import edu.yu.cs.com1320.project.impl.HashTableImpl;

import java.net.URI;
import java.util.Arrays;
import java.util.Set;


public class DocumentImpl implements edu.yu.cs.com1320.project.stage2.Document {
    private HashTable<String, String> metaData;
    private HashTable<String, String> copy;
    private URI uri;
    private String text;
    private byte[] binaryData;
    public DocumentImpl(URI uri, String txt) {
        if (uri == null || uri.getPath() == null || uri.getPath().isEmpty() || txt == null || txt.equals("")){
            throw new IllegalArgumentException("One or more arguments were blank or null");
        }
        this.uri = uri;
        this.text = txt;
        this.binaryData = null;
        this.metaData = new HashTableImpl<>();

    }
    public DocumentImpl(URI uri, byte[] binaryData){
        if (uri == null || uri.getPath() == null || uri.getPath().equals("") || binaryData == null || binaryData.length == 0){
            throw new IllegalArgumentException("One or more arguments were blank or null");
        }
        this.uri = uri;
        this.text = null;
        this.binaryData = binaryData;
        this.metaData = new HashTableImpl<>();
    }
    @Override
    public String setMetadataValue(String key, String value) {
        if (key == null || key.equals("")){
            throw new IllegalArgumentException("key is null or blank");
        }
        return this.metaData.put(key, value);
    }

    @Override
    public String getMetadataValue(String key) {
        if (key == null || key.equals("")){
            throw new IllegalArgumentException("key is null or blank");
        }
        return this.metaData.get(key);
    }

    @Override
    public HashTable<String, String> getMetadata() {
        copy = new HashTableImpl<String, String>();
        Set<String> keys = metaData.keySet();
        for(String k : keys){
            copy.put(k, metaData.get(k));
        }
        return copy;
    }

    public String getDocumentTxt() {
        return this.text;
    }

    @Override
    public URI getKey() {
        return this.uri;
    }
    @Override
    public byte[] getDocumentBinaryData(){
        return this.binaryData;
    }

    @Override
    public int hashCode() {
        int result = uri.hashCode();
        result = 31 * result + (text != null ? text.hashCode() : 0);
        result = 31 * result + Arrays.hashCode(binaryData);
        return Math.abs(result);
    }
    @Override
    public boolean equals(Object o){
        if (!(o instanceof DocumentImpl)){
            return false;
        }
        if (o == this){
            return true;
        }
        if(o.hashCode() == this.hashCode()){
            return true;
        }
        return false;
    }
}
