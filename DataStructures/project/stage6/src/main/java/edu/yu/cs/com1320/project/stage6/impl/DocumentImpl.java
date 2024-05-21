package edu.yu.cs.com1320.project.stage6.impl;

import edu.yu.cs.com1320.project.stage6.Document;

import java.util.*;

import java.net.URI;


public class DocumentImpl implements edu.yu.cs.com1320.project.stage6.Document {
    private HashMap<String, String> metaData;
    private HashMap<String, String> copy;
    private URI uri;
    private String text;
    private byte[] binaryData;
    private Map<String, Integer> wordCounts;
    private long lastUsedTime;
    public DocumentImpl(URI uri, String txt, Map<String, Integer> wordCountMap) {
        if (uri == null || uri.getPath() == null || uri.getPath().isEmpty() || txt == null || txt.equals("")) {
            throw new IllegalArgumentException("One or more arguments were blank or null");
        }
        this.uri = uri;
        this.text = txt;
        this.binaryData = null;
        this.metaData = new HashMap<>();
        if(wordCountMap == null) {
            this.wordCounts = new HashMap<>();
        } else {
            wordCounts = (HashMap<String, Integer>) wordCountMap;
        }
        this.setWordTable();
        this.setLastUseTime(System.nanoTime());
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
        this.wordCounts = new HashMap<>();
        this.binaryData = binaryData;
        this.metaData = new HashMap<>();
        this.setLastUseTime(System.nanoTime());
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
    public HashMap<String, String> getMetadata() {
        copy = new HashMap<>();
        Set<String> keys = metaData.keySet();
        for(String k : keys){
            copy.put(k, metaData.get(k));
        }
        return copy;
    }

    @Override
    public void setMetadata(HashMap<String, String> metadata) {
        this.metaData = metadata;
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
       if(this.binaryData != null || this.wordCounts.get(ANSearchTerm) == null){
           return 0;
       } else {
           return this.wordCounts.get(ANSearchTerm);
       }
    }
    @Override
    public Set<String> getWords() {
        if(this.binaryData != null){
            return new HashSet<>();
        }
        return wordCounts.keySet();
    }

    @Override
    public long getLastUseTime() {
        return lastUsedTime;
    }

    @Override
    public void setLastUseTime(long timeInNanoseconds) {
        this.lastUsedTime = timeInNanoseconds;
    }

    @Override
    public HashMap<String, Integer> getWordMap() {
        return (HashMap<String, Integer>) wordCounts;
    }

    @Override
    public void setWordMap(HashMap<String, Integer> wordMap) {
        this.wordCounts = wordMap;
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
        if(o == null){
            return false;
        }
        if (!(o instanceof DocumentImpl)){
            return false;
        }
        if (o == this){
            return true;
        }
        return o.hashCode() == this.hashCode();
    }

    @Override
    public int compareTo(Document o) {
        if(this.getLastUseTime() == o.getLastUseTime()){
            return 0;
        } else if (this.getLastUseTime() > o.getLastUseTime()){
            return 1;
        }
        return -1;
    }
}
