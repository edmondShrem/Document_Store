package edu.yu.cs.com1320.project.stage4.impl;

import edu.yu.cs.com1320.project.HashTable;
import edu.yu.cs.com1320.project.impl.HashTableImpl;
import edu.yu.cs.com1320.project.impl.StackImpl;
import edu.yu.cs.com1320.project.impl.TrieImpl;
import edu.yu.cs.com1320.project.stage4.Document;
import edu.yu.cs.com1320.project.undo.Command;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.*;

public class DocumentStoreImpl implements edu.yu.cs.com1320.project.stage4.DocumentStore {
    private HashTable<URI, DocumentImpl> docs;
    private TrieImpl<Document> wordTrie;
    private StackImpl<Command> commandStack;
    private int trueStackSize;

    public DocumentStoreImpl() {
        this.docs = new HashTableImpl<>();
        this.commandStack = new StackImpl<>();
        this.wordTrie = new TrieImpl<>();
        this.trueStackSize = 0;
    }

    @Override
    public String setMetadata(URI uri, String key, String value) {
        if (uri == null || uri.getPath() == null || uri.getPath().equals("") || key == null || key.equals("") || docs.get(uri) == null) {
            throw new IllegalArgumentException("the uri is null or blank, if there is no document stored at that uri, or the key is null or blank");
        }
        if (trueStackSize <= commandStack.size()) {
            String old = this.getMetadata(uri, key);
            commandStack.push(new Command(uri, (uri1) -> setMetadata(uri1, key, old)));
        }
        trueStackSize = commandStack.size();
        return this.docs.get(uri).setMetadataValue(key, value);
    }

    @Override
    public String getMetadata(URI uri, String key) {
        if (uri == null || uri.getPath() == null || uri.getPath().equals("") || key == null || key.equals("") || docs.get(uri) == null) {
            throw new IllegalArgumentException("the uri is null or blank, if there is no document stored at that uri, or the key is null or blank");
        }
        return this.docs.get(uri).getMetadataValue(key);
    }

    @Override
    public int put(InputStream input, URI uri, DocumentFormat format) {
        if (uri == null || uri.getPath() == null || uri.getPath().equals("") || format == null) {
            throw new IllegalArgumentException("uri is null or empty, or format is null");
        }
        if (input == null) {
            DocumentImpl prev = docs.get(uri);
            this.delete(uri);
            if (trueStackSize <= commandStack.size()) {
                commandStack.push(new Command(uri, (uri1) -> docs.put(uri1, prev)));
            }
            trueStackSize = commandStack.size();
            return (prev == null ? 0 : prev.hashCode());
        }
        byte[] bytes;
        bytes = getBytes(input);
        DocumentImpl prev;
        prev = putBasedOnFormat(uri, format, bytes);
        if (prev == null) {
            if (trueStackSize <= commandStack.size()) {
                commandStack.push(new Command(uri, (uri1) -> delete(uri1)));
            }
            trueStackSize = commandStack.size();
            return 0;
        } else {
            if (trueStackSize <= commandStack.size()) {
                commandStack.push(new Command(uri, (uri1) -> docs.put(uri1, prev)));
            }
            trueStackSize = commandStack.size();
            return prev.hashCode();
        }
    }

    private void throwItIntoTheTrie(Document d) {
        Set<String> set = d.getWords();
        for (String s : set) {
            assert set != null;
            assert d != null;
            wordTrie.put(s, d);
        }
    }

    private DocumentImpl putBasedOnFormat(URI uri, DocumentFormat format, byte[] bytes) {
        DocumentImpl prev;
        if (format == DocumentFormat.TXT) {
            DocumentImpl current = new DocumentImpl(uri, new String(bytes));
            prev = docs.put(uri, null);
            this.delete(uri);
            docs.put(uri, current);
            this.throwItIntoTheTrie(current);
        } else {
            prev = docs.put(uri, new DocumentImpl(uri, bytes));
        }
        return prev;
    }

    private static byte[] getBytes(InputStream input) {
        byte[] bytes;
        while (true) {
            try {
                bytes = input.readAllBytes();
                input.close();
                break;
            } catch (IOException e) {
                continue;
            }
        }
        return bytes;
    }

    @Override
    public Document get(URI url) {
        return docs.get(url);
    }

    //i think i dealt properly with the trie?
    @Override
    public boolean delete(URI url) {
        DocumentImpl prev = docs.put(url, null);
        if (prev != null) {
            Set<String> words = prev.getWords();
            for (String s : words) {
                wordTrie.delete(s, prev);
            }
        }
        if (trueStackSize <= commandStack.size() && prev != null) {
            commandStack.push(new Command(url, (uri1) -> docs.put(uri1, prev)));
        }
        trueStackSize = commandStack.size();
        return prev != null;
    }

    @Override
    public void undo() throws IllegalStateException {
        if (commandStack.size() == 0) {
            throw new IllegalStateException("Nothing to undo");
        }
        Command c = commandStack.pop();
        c.undo();
    }

    @Override
    public void undo(URI url) throws IllegalStateException {
        if (commandStack.size() == 0) {
            throw new IllegalStateException("Nothing to undo");
        }
        StackImpl<Command> temp = new StackImpl<>();
        boolean found = false;
        while (!found) {
            //ensures that if its not found everything gets put back first before throwing, dont wanna break the system
            if (commandStack.size() == 0) {
                while (temp.size() != 0) {
                    commandStack.push(temp.pop());
                }
                throw new IllegalStateException("URI is not represented in the command stack");
            }
            if (commandStack.peek().getUri().equals(url)) {
                commandStack.pop().undo();
                found = true;
            } else {
                temp.push(commandStack.pop());
            }
        }
        while (temp.size() > 0) {
            commandStack.push(temp.pop());
        }
    }

    @Override
    public List<Document> search(String keyword) {
        Comparator<Document> c = new docComp(keyword);
        return wordTrie.getSorted(keyword, c);
    }

    @Override
    public List<Document> searchByPrefix(String keywordPrefix) {
        Comparator<Document> c = new preComp(keywordPrefix);
        return wordTrie.getAllWithPrefixSorted(keywordPrefix, c);
    }

    @Override
    public Set<URI> deleteAll(String keyword) {
        Comparator<Document> c = new docComp(keyword);
        List<Document> list = wordTrie.getSorted(keyword, c);
        return deleteAllAndGetUris(list);
    }

    @Override
    public Set<URI> deleteAllWithPrefix(String keywordPrefix) {
        Comparator<Document> c = new preComp(keywordPrefix);
        List<Document> list = wordTrie.getAllWithPrefixSorted(keywordPrefix, c);
        return deleteAllAndGetUris(list);
    }

    private Set<URI> deleteAllAndGetUris(List<Document> list) {
        Set<URI> uris = new HashSet<>();
        Set<String> words;
        for (Document d : list) {
            uris.add(d.getKey());
            //delete from hashtable
            docs.put(d.getKey(), null);
            //delete from wordtrie
            words = d.getWords();
            for (String s : words) {
                wordTrie.delete(s, d);
            }
            //gonna have to delete from metatrie if that becomes a thing
        }
        return uris;
    }

    //there's a better way to do this but i also need to finish. wooo ;-;
    @Override
    public List<Document> searchByMetadata(Map<String, String> keysValues) {
        List<Document> list = new ArrayList<>();
        for (Document d : docs.values()) {
            if (d.getMetadata().keySet().equals(keysValues.keySet()) && d.getMetadata().values().equals(keysValues.values())) {
                list.add(d);
            }
        }
        return list;
    }

    @Override
    public List<Document> searchByKeywordAndMetadata(String keyword, Map<String, String> keysValues) {
        List<Document> byKeyWord = this.search(keyword);
        return getMetaIntersections(keysValues, byKeyWord);
    }

    @Override
    public List<Document> searchByPrefixAndMetadata(String keywordPrefix, Map<String, String> keysValues) {
        List<Document> byPrefix = this.searchByPrefix(keywordPrefix);
        return getMetaIntersections(keysValues, byPrefix);
    }

    private List<Document> getMetaIntersections(Map<String, String> keysValues, List<Document> byPrefix) {
        List<Document> byMetaData = this.searchByMetadata(keysValues);
        List<Document> toReturn = new ArrayList<Document>();
        for (Document d : byPrefix) {
            if (byMetaData.contains(d)) {
                toReturn.add(d);
            }
        }
        return toReturn;
    }

    @Override
    public Set<URI> deleteAllWithMetadata(Map<String, String> keysValues) {
        List<Document> toDelete = this.searchByMetadata(keysValues);
        return deleteAllAndGetUris(toDelete);
    }

    @Override
    public Set<URI> deleteAllWithKeywordAndMetadata(String keyword, Map<String, String> keysValues) {
        List<Document> toDeleteKeyword = this.search(keyword);
        List<Document> toDelete = getMetaIntersections(keysValues, toDeleteKeyword);
        return deleteAllAndGetUris(toDelete);
    }

    @Override
    public Set<URI> deleteAllWithPrefixAndMetadata(String keywordPrefix, Map<String, String> keysValues) {
        List<Document> toDeletePrefix = this.search(keywordPrefix);
        List<Document> toDelete = getMetaIntersections(keysValues, toDeletePrefix);
        return deleteAllAndGetUris(toDelete);
    }

    private class docComp implements Comparator<Document> {
        String comper;

        public docComp(String s) {
            this.comper = s;
        }

        @Override
        public int compare(Document o1, Document o2) {
            if (o1.wordCount(comper) > o2.wordCount(comper)) {
                return 1;
            } else if (o1.wordCount(comper) < o2.wordCount(comper)) {
                return -1;
            } else {
                return 0;
            }
        }
    }
    private class preComp implements Comparator<Document>{
        String comper;

        public preComp(String s) {
            this.comper = s;
        }
        @Override
        public int compare(Document o1, Document o2){
            int d1C = 0;
            int d2C = 0;
            for(String s : o1.getWords()){
                if(s.startsWith(comper)){
                    d1C += o1.wordCount(s);
                }
            }
            for(String s : o2.getWords()){
                if(s.startsWith(comper)){
                    d2C += o2.wordCount(s);
                }
            }
            return  d1C - d2C;
        }
    }
}