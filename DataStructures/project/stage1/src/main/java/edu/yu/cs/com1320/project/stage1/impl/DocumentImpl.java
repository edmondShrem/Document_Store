package edu.yu.cs.com1320.project.stage1.impl;

import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;



public class DocumentImpl implements edu.yu.cs.com1320.project.stage1.Document {
    HashMap<String, String> metaData;
    URI uri;
    String text;
    byte[] binaryData;
    public DocumentImpl(URI uri, String txt) throws IllegalArgumentException{
        if (uri == null || uri.getPath() == null || uri.getPath().isEmpty() || txt == null || txt.equals("")){
            throw new IllegalArgumentException("One or more arguments were blank or null");
        }
        this.uri = uri;
        this.text = txt;
        this.binaryData = null;
        this.metaData = new HashMap<>();
    }
    public DocumentImpl(URI uri, byte[] binaryData){
        if (uri == null || uri.getPath() == null || uri.getPath().equals("") || binaryData == null){
            throw new IllegalArgumentException("One or more arguments were blank or null");
        }
        this.uri = uri;
        this.text = null;
        this.binaryData = binaryData;
        this.metaData = new HashMap<>();
    }
    @Override
    public String setMetadataValue(String key, String value) throws IllegalArgumentException{
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
    public HashMap<String, String> getMetadata() {
        return new HashMap<String, String>(metaData);
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
        return result;
    }
    @Override
    public boolean equals(Object o){
        if (!(o.getClass().equals(this.getClass()))){
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