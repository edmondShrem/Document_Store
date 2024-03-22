package edu.yu.cs.com1320.project.stage4.impl;

import edu.yu.cs.com1320.project.HashTable;
import edu.yu.cs.com1320.project.impl.HashTableImpl;

import java.net.URI;
import java.util.*;


public class DocumentImpl implements edu.yu.cs.com1320.project.stage4.Document {
    private HashTable<String, String> metaData;
    private HashTable<String, String> copy;
    private URI uri;
    private String text;
    private byte[] binaryData;
    private HashTable<String, Integer> wordCounts;
    public DocumentImpl(URI uri, String txt) {
        if (uri == null || uri.getPath() == null || uri.getPath().isEmpty() || txt == null || txt.equals("")) {
            throw new IllegalArgumentException("One or more arguments were blank or null");
        }
        this.uri = uri;
        this.text = txt;
        this.binaryData = null;
        this.metaData = new HashTableImpl<>();
        this.wordCounts = new HashTableImpl<>();
        this.setWordTable();
    }
    //48-57 = nums
    //65-90 = caps
    //97-122 = lowers
    private void setWordTable(){
        String[] arr = this.text.split(" ");
        char[] chars;
        String alphanumeric = "";
        for(String str : arr){
            alphanumeric = getAlphanumeric(str);
            assert !alphanumeric.equals("");
            //the word already appears in the table
            if(wordCounts.containsKey(alphanumeric)){
                int prev = wordCounts.get(alphanumeric);
                wordCounts.put(alphanumeric, prev + 1);
            } else {
                //this is a new word
                wordCounts.put(alphanumeric, 1);
            }
            alphanumeric = "";
        }
    }

    private String getAlphanumeric(String str) {
        StringBuilder s = new StringBuilder();
        char[] chars;
        chars = str.toCharArray();
        for(char c : chars){
            if(isAlphanumeric(c)){
                s.append(c);
            }
        }
        return s.toString();
    }

    private boolean isAlphanumeric(char c){
        if (c >= 48 && c <= 57){
            //is number
            return true;
        }
        if (c >= 65 && c <= 90){
            //is capital letter
            return true;
        }
        if (c >= 97 && c <= 122){
            //is lowercase letter
            return true;
        }
        return false;
    }
    public DocumentImpl(URI uri, byte[] binaryData){
        if (uri == null || uri.getPath() == null || uri.getPath().equals("") || binaryData == null || binaryData.length == 0){
            throw new IllegalArgumentException("One or more arguments were blank or null");
        }
        this.uri = uri;
        this.text = null;
        this.wordCounts = new HashTableImpl<>();
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
    public int wordCount(String word) {
        //holds the alphanumeric version of the term
        String ANSearchTerm;
        ANSearchTerm = getAlphanumeric(word);
        assert !ANSearchTerm.isEmpty();
       if(this.binaryData != null || this.wordCounts.get(ANSearchTerm) == null){
           return 0;
       } else {
           assert this.wordCounts.get(ANSearchTerm) != 0;
           return this.wordCounts.get(ANSearchTerm);
       }
    }
    @Override
    public Set<String> getWords() {
        return wordCounts.keySet();
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
