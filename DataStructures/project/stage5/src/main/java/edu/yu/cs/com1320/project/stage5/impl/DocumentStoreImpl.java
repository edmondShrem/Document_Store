package edu.yu.cs.com1320.project.stage5.impl;

import edu.yu.cs.com1320.project.HashTable;
//asl ant this
import edu.yu.cs.com1320.project.MinHeap;
import edu.yu.cs.com1320.project.Stack;
import edu.yu.cs.com1320.project.impl.HashTableImpl;
import edu.yu.cs.com1320.project.impl.MinHeapImpl;
import edu.yu.cs.com1320.project.impl.StackImpl;
import edu.yu.cs.com1320.project.impl.TrieImpl;
import edu.yu.cs.com1320.project.stage5.Document;
import edu.yu.cs.com1320.project.undo.CommandSet;
import edu.yu.cs.com1320.project.undo.GenericCommand;
import edu.yu.cs.com1320.project.undo.Undoable;
import org.ietf.jgss.GSSName;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.*;

public class DocumentStoreImpl implements edu.yu.cs.com1320.project.stage5.DocumentStore {
    private HashTable<URI, DocumentImpl> docs;
    private TrieImpl<Document> wordTrie;
    private StackImpl<Undoable> commandStack;
    private MinHeap<Document> timeHeap;
    private int trueStackSize;
    private int maxDocs;
    private int maxBytes;

    public DocumentStoreImpl() {
        this.docs = new HashTableImpl<>();
        this.commandStack = new StackImpl<>();
        this.wordTrie = new TrieImpl<>();
        this.timeHeap = new MinHeapImpl();
        this.trueStackSize = 0;
        maxBytes = Integer.MAX_VALUE;
        maxDocs = Integer.MAX_VALUE;
    }
    private int getTrueStackSize(){
        int x = commandStack.size();
        StackImpl<Undoable> temp = new StackImpl<>();
        int count = 0;
        for(int i = 0; i < x; i++){
            if(commandStack.peek().getClass().equals(CommandSet.class)){
                count += ((CommandSet<URI>)(commandStack.peek())).size();
            } else {
                count ++;
            }
            temp.push(commandStack.pop());
        }
        while(temp.size() != 0){
            commandStack.push(temp.pop());
        }
        return count;
    }
    @Override
    public String setMetadata(URI uri, String key, String value) {
        if (uri == null || uri.getPath() == null || uri.getPath().equals("") || key == null || key.equals("") || docs.get(uri) == null) {
            throw new IllegalArgumentException("the uri is null or blank, if there is no document stored at that uri, or the key is null or blank");
        }
        if (trueStackSize <= this.getTrueStackSize()) {
            String old = this.getMetadata(uri, key);
            commandStack.push(new GenericCommand<URI>(uri, (uri1) -> {if(docs.get(uri1)!= null) docs.get(uri1).setMetadataValue(key, old);}));
        }
        trueStackSize = this.getTrueStackSize();
        String old = this.docs.get(uri).setMetadataValue(key, value);
        this.docs.get(uri).setLastUseTime(System.nanoTime());
        this.timeHeap.reHeapify(this.docs.get(uri));
        return old;
    }

    @Override
    public String getMetadata(URI uri, String key) {
        if (uri == null || uri.getPath() == null || uri.getPath().equals("") || key == null || key.equals("") || docs.get(uri) == null) {
            throw new IllegalArgumentException("the uri is null or blank, if there is no document stored at that uri, or the key is null or blank");
        }
        String val = this.docs.get(uri).getMetadataValue(key);
        if(val != null) {
            this.docs.get(uri).setLastUseTime(System.nanoTime());
            this.timeHeap.reHeapify(this.docs.get(uri));
        }
        return val;
    }

    @Override
    public int put(InputStream input, URI uri, DocumentFormat format) {
        if (uri == null || uri.getPath() == null || uri.getPath().equals("") || format == null) {
            throw new IllegalArgumentException("uri is null or empty, or format is null");
        }
        if (input == null) {
            DocumentImpl prev = docs.get(uri);
            this.delete(uri);
            if (trueStackSize <= this.getTrueStackSize() && prev != null) {
                if(prev.getDocumentTxt() != null){
                    commandStack.push(new GenericCommand<URI>(uri, (uri1) -> { this.putBasedOnFormat(uri1, DocumentFormat.TXT,prev.getDocumentTxt().getBytes()); if(prev.getDocumentBinaryData() != null) throwItIntoTheTrie(prev);}));
                }else{
                    commandStack.push(new GenericCommand<URI>(uri, (uri1) -> { this.putBasedOnFormat(uri1, DocumentFormat.BINARY,prev.getDocumentBinaryData()); if(prev.getDocumentBinaryData() != null) throwItIntoTheTrie(prev);}));
                }
            }
            trueStackSize = this.getTrueStackSize();
            return (prev == null ? 0 : prev.hashCode());
        }
        byte[] bytes;
        bytes = getBytes(input);
        if(bytes.length > maxBytes){
            throw new IllegalArgumentException("This document is over the memory limit: " + bytes.length + " > " + maxBytes);
        }
        DocumentImpl prev;
        prev = putBasedOnFormat(uri, format, bytes);
        if (prev == null) {
            if (trueStackSize <= this.getTrueStackSize()) {
                commandStack.push(new GenericCommand<URI>(uri, (uri1) -> delete(uri1)));
            }
            trueStackSize = this.getTrueStackSize();
            if(getTotalBytes() > maxBytes || docs.size() > maxDocs) {
                while (getTotalBytes() > maxBytes || docs.size() > maxDocs) {
                    cull();
                }
            }
            return 0;
        } else {
            if (trueStackSize <= this.getTrueStackSize()) {
                if(prev.getDocumentTxt() != null){
                    commandStack.push(new GenericCommand<URI>(uri, (uri1) -> { this.putBasedOnFormat(uri1, DocumentFormat.TXT,prev.getDocumentTxt().getBytes()); if(prev.getDocumentBinaryData() != null) throwItIntoTheTrie(prev);}));
                }else{
                    commandStack.push(new GenericCommand<URI>(uri, (uri1) -> { this.putBasedOnFormat(uri1, DocumentFormat.BINARY,prev.getDocumentBinaryData()); if(prev.getDocumentBinaryData() != null) throwItIntoTheTrie(prev);}));
                }            }
            trueStackSize = this.getTrueStackSize();
            if(getTotalBytes() > maxBytes || docs.size() > maxDocs) {
                while (getTotalBytes() > maxBytes || docs.size() > maxDocs) {
                    cull();
                }
            }
            return prev.hashCode();
        }
    }

    private void throwItIntoTheTrie(Document d) {
        Set<String> set = d.getWords();
        for (String s : set) {
            wordTrie.put(s, d);
        }
    }

    private DocumentImpl putBasedOnFormat(URI uri, DocumentFormat format, byte[] bytes) {
        DocumentImpl prev;
        if(docs.size() >= maxDocs){
            cull();
        }
        if (format == DocumentFormat.TXT) {
            DocumentImpl current = new DocumentImpl(uri, new String(bytes));
            current.setLastUseTime(System.nanoTime());
            prev = docs.put(uri, null);
            //this.delete(uri);
            docs.put(uri, current);
            this.throwItIntoTheTrie(current);
        } else {
            prev = docs.put(uri, new DocumentImpl(uri, bytes));
        }
        if(prev != null){
            prev.setLastUseTime(Integer.MIN_VALUE);
            this.timeHeap.reHeapify(prev);
            this.timeHeap.remove();
        }
        this.timeHeap.insert(this.docs.get(uri));
        this.timeHeap.reHeapify(this.docs.get(uri));
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
        if(this.docs.get(url) != null){
            this.docs.get(url).setLastUseTime(System.nanoTime());
            this.timeHeap.reHeapify(this.docs.get(url));
        }
        return docs.get(url);
    }

    //i think i dealt properly with the trie?
    @Override
    public boolean delete(URI url) {
        DocumentImpl prev = docs.put(url, null);
        if (prev != null) {
            prev.setLastUseTime(Integer.MIN_VALUE);
            this.timeHeap.reHeapify(prev);
            Set<String> words = prev.getWords();
            for (String s : words) {
                wordTrie.delete(s, prev);
            }
            this.timeHeap.remove();
        }
        if (trueStackSize <= this.getTrueStackSize() && prev != null) {
            if(prev.getDocumentTxt() != null){
            commandStack.push(new GenericCommand<URI>(url, (uri1) -> { this.putBasedOnFormat(uri1, DocumentFormat.TXT,prev.getDocumentTxt().getBytes()); if(prev.getDocumentBinaryData() != null) throwItIntoTheTrie(prev);}));
            }else{
                commandStack.push(new GenericCommand<URI>(url, (uri1) -> { this.putBasedOnFormat(uri1, DocumentFormat.BINARY,prev.getDocumentBinaryData()); if(prev.getDocumentBinaryData() != null) throwItIntoTheTrie(prev);}));
            }
        }
        trueStackSize = this.getTrueStackSize();
        return prev != null;
    }
//deal with these later
    @Override
    public void undo() throws IllegalStateException {
        if (commandStack.size() == 0) {
            throw new IllegalStateException("Nothing to undo");
        }
        Undoable c = commandStack.pop();
        if(c.getClass().equals(GenericCommand.class)){
            if(docs.get((URI)((GenericCommand)c).getTarget()) != null) {
                docs.get((URI) ((GenericCommand) c).getTarget()).setLastUseTime(System.nanoTime());
                timeHeap.reHeapify(docs.get((URI) ((GenericCommand) c).getTarget()));
            }
        } else {
            for(Object g:((CommandSet)c)){
                if(docs.get((URI)((GenericCommand)g).getTarget()) != null) {
                    docs.get((URI) ((GenericCommand) g).getTarget()).setLastUseTime(System.nanoTime());
                    timeHeap.reHeapify(docs.get((URI) ((GenericCommand) g).getTarget()));
                }
            }
        }
        c.undo();
    }

    @Override
    public void undo(URI url) throws IllegalStateException {
        if (commandStack.size() == 0) {
            throw new IllegalStateException("Nothing to undo");
        }
        StackImpl<Undoable> temp = new StackImpl<>();
        boolean found = false;
        while (!found) {
            //ensures that if its not found everything gets put back first before throwing, dont wanna break the system
            if (commandStack.size() == 0) {
                while (temp.size() != 0) {
                    commandStack.push(temp.pop());
                }
                throw new IllegalStateException("URI is not represented in the command stack");
            }
            //if its a single command check the uri, if not check if the uri is represented in the set. undo either way
            if (commandStack.peek().getClass().equals(GenericCommand.class) && ((GenericCommand<URI>)commandStack.peek()).getTarget().equals(url)){
                commandStack.pop().undo();
                if(docs.get(url) != null) {
                    docs.get(url).setLastUseTime(System.nanoTime());
                    timeHeap.reHeapify(docs.get(url));
                }
                /////
                found = true;
            } else if (commandStack.peek().getClass().equals(CommandSet.class) && ((CommandSet<URI>)(commandStack.peek())).containsTarget(url)){
                found = true;
                ((CommandSet<URI>)(commandStack.peek())).undo(url);
                if(docs.get(url) != null) {
                    docs.get(url).setLastUseTime(System.nanoTime());
                    timeHeap.reHeapify(docs.get(url));
                }
               ////
                if(((CommandSet<URI>)commandStack.peek()).isEmpty()){
                    commandStack.pop();
                }
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
        List<Document> l = wordTrie.getSorted(keyword, c);
        for(Document d : l){
            d.setLastUseTime(System.nanoTime());
            this.timeHeap.reHeapify(d);
        }
        return l;
    }

    @Override
    public List<Document> searchByPrefix(String keywordPrefix) {
        Comparator<Document> c = new preComp(keywordPrefix);
        List<Document> l = wordTrie.getAllWithPrefixSorted(keywordPrefix, c);
        for(Document d : l){
            d.setLastUseTime(System.nanoTime());
            this.timeHeap.reHeapify(d);
        }
        return l;
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
            if (trueStackSize <= this.getTrueStackSize()) {
                CommandSet<URI> c = new CommandSet<URI>();
                for(Document doc: list){
                    c.addCommand(new GenericCommand<URI>(doc.getKey(), (uri1) -> {
                        if(((Document)doc).getDocumentTxt() != null) {
                            if(doc.getDocumentTxt().getBytes().length > maxBytes){
                                throw new IllegalArgumentException();
                            }
                            if (docs.size() >= maxDocs){
                                cull();
                            }
                            this.putBasedOnFormat(uri1, DocumentFormat.TXT, doc.getDocumentTxt().getBytes());
                            throwItIntoTheTrie(doc);
                        } else {
                            this.putBasedOnFormat(uri1, DocumentFormat.BINARY, doc.getDocumentBinaryData());
                        }
                    }));
                }
                commandStack.push(c);
            }
            trueStackSize = this.getTrueStackSize();
            d.setLastUseTime(Integer.MIN_VALUE);
            timeHeap.reHeapify(d);
            timeHeap.remove();//gonna have to delete from metatrie if that becomes a thing
        }
        return uris;
    }

    //there's a better way to do this but i also need to finish. wooo ;-;
    @Override
    public List<Document> searchByMetadata(Map<String, String> keysValues) {
        List<Document> list = new ArrayList<>();
        if(keysValues.isEmpty()){
            return list;
        }
        for (Document d : docs.values()) {
            if (d.getMetadata().keySet().containsAll(keysValues.keySet()) ){
                    if(d.getMetadata().values().containsAll(keysValues.values())){
                        d.setLastUseTime(System.nanoTime());
                        this.timeHeap.reHeapify(d);
                        list.add(d);
                    }
                }
            }
        return list;
    }

    @Override
    public List<Document> searchByKeywordAndMetadata(String keyword, Map<String, String> keysValues) {
        Comparator<Document> c = new docComp(keyword);
        List<Document> l = wordTrie.getSorted(keyword, c);
        return getMetaIntersections(keysValues, l);
    }

    @Override
    public List<Document> searchByPrefixAndMetadata(String keywordPrefix, Map<String, String> keysValues) {
        Comparator<Document> c = new preComp(keywordPrefix);
        List<Document> byPrefix = wordTrie.getAllWithPrefixSorted(keywordPrefix, c);
        return getMetaIntersections(keysValues, byPrefix);
    }

    private List<Document> getMetaIntersections(Map<String, String> keysValues, List<Document> byPrefix) {
        List<Document> byMetadata = new ArrayList<>();
        for (Document d : docs.values()) {
            if (d.getMetadata().keySet().containsAll(keysValues.keySet()) ){
                if(d.getMetadata().values().containsAll(keysValues.values())){
                    byMetadata.add(d);
                }
            }
        }
        List<Document> toReturn = new ArrayList<Document>();
        for (Document d : byPrefix) {
            if (byMetadata.contains(d)) {
                d.setLastUseTime(System.nanoTime());
                this.timeHeap.reHeapify(d);
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
        Comparator<Document> c = new docComp(keyword);
        List<Document> toDeleteKeyword = wordTrie.getSorted(keyword, c);
        List<Document> toDelete = getMetaIntersections(keysValues, toDeleteKeyword);

        return deleteAllAndGetUris(toDelete);
    }

    @Override
    public Set<URI> deleteAllWithPrefixAndMetadata(String keywordPrefix, Map<String, String> keysValues) {
        Comparator<Document> c = new preComp(keywordPrefix);
        List<Document> byPrefix = wordTrie.getAllWithPrefixSorted(keywordPrefix, c);
        List<Document> toDelete = getMetaIntersections(keysValues, byPrefix);

        return deleteAllAndGetUris(toDelete);
    }

    @Override
    public void setMaxDocumentCount(int limit) {
        int totalDocs = this.docs.size();
        this.maxDocs = limit;
        Document prev;
        if(totalDocs > this.maxDocs){
            while(totalDocs > this.maxDocs) {
                cull();
                totalDocs--;
            }
        }
    }
    private void removeFromUndos(Document d){
        URI uri = d.getKey();
        this.docs.put(uri, null);
        Stack<Undoable> temp = new StackImpl<>();
        Undoable next;
        while(commandStack.size() > 0){
        next = commandStack.pop();
        if(next.getClass().equals(GenericCommand.class)){
            if (!((GenericCommand)next).getTarget().equals(uri)){
                temp.push(next);
            } else {
                next.undo();
            }
        }
        else {
            ((CommandSet)next).undo(uri);

           if(!((CommandSet)next).isEmpty()){
               temp.push(next);
        }
        }
        }
        while(temp.size() > 0){
            commandStack.push(temp.pop());
        }
        this.trueStackSize = getTrueStackSize();
    }
    @Override
    public void setMaxDocumentBytes(int limit) {
        this.maxBytes = limit;
        int currentBytes = this.getTotalBytes();
        Document prev;
        if(currentBytes > maxBytes){
            while(currentBytes > maxBytes){
                prev = cull();
                if(prev.getDocumentTxt() != null){
                    currentBytes -= prev.getDocumentTxt().getBytes().length;
                } else {
                    currentBytes -= prev.getDocumentBinaryData().length;
                }
            }
        }
    }

    private Document cull() {
        Document prev;

        prev = timeHeap.peek();
        //this.timeHeap.reHeapify(prev);
        this.timeHeap.remove();
        removeFromUndos(prev);
        return prev;
    }

    private int getTotalBytes(){
        int total = 0;
        for(Document d:docs.values()){
            if(d.getDocumentTxt() != null){
                total += d.getDocumentTxt().getBytes().length;
            } else {
                total += d.getDocumentBinaryData().length;
            }
        }
        return total;
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