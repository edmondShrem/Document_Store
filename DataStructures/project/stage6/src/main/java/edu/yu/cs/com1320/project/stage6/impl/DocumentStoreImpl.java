package edu.yu.cs.com1320.project.stage6.impl;

import edu.yu.cs.com1320.project.MinHeap;
import edu.yu.cs.com1320.project.Stack;
import edu.yu.cs.com1320.project.impl.MinHeapImpl;
import edu.yu.cs.com1320.project.impl.StackImpl;
import edu.yu.cs.com1320.project.impl.TrieImpl;
import edu.yu.cs.com1320.project.stage6.Document;
import edu.yu.cs.com1320.project.undo.CommandSet;
import edu.yu.cs.com1320.project.undo.GenericCommand;
import edu.yu.cs.com1320.project.undo.Undoable;
import edu.yu.cs.com1320.project.impl.BTreeImpl;

import javax.print.Doc;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.*;

public class DocumentStoreImpl implements edu.yu.cs.com1320.project.stage6.DocumentStore {
    //to replace all uses of size, simply keep a running tally of documents. I say "simply but it will probably involve lots and lonts of shenanigans and testing.
    //what do i even do about values like bruh.
    //WAIT HEAR ME OUT. we give trieImpl some stupid value for metadata or whatver that auto returns what i need. this is awful and needs something better. maybe hyst replace with a simple list or set of uris and just go thru those< that would probaby be a better idea come to think of it.
    private BTreeImpl<URI, DocumentImpl> docTree;
    private TrieImpl<URISafonUboiTeman> wordTrie;
    private TrieImpl<URISafonUboiTeman> metaTrie;
    private StackImpl<Undoable> commandStack;
    private MinHeap<URISafonUboiTeman> timeHeap;
    private Set<URISafonUboiTeman> heapSet;
    private int trueStackSize;
    private int maxDocs;
    private int maxBytes;

    private int totalBytes;

    public DocumentStoreImpl() {
        this.docTree = new BTreeImpl<>();
        this.docTree.setPersistenceManager(new DocumentPersistenceManager());
        this.commandStack = new StackImpl<>();
        this.wordTrie = new TrieImpl<>();
        this.metaTrie = new TrieImpl<>();
        this.timeHeap = new MinHeapImpl();
        heapSet = new HashSet<>();
        this.trueStackSize = 0;
        maxBytes = Integer.MAX_VALUE;
        maxDocs = Integer.MAX_VALUE;
    }
    public DocumentStoreImpl(File baseDir) {
        this.docTree = new BTreeImpl<>();
        this.docTree.setPersistenceManager(new DocumentPersistenceManager(baseDir));
        this.commandStack = new StackImpl<>();
        this.wordTrie = new TrieImpl<>();
        this.metaTrie = new TrieImpl<>();
        this.timeHeap = new MinHeapImpl();
        heapSet = new HashSet<>();
        this.trueStackSize = 0;
        maxBytes = Integer.MAX_VALUE;
        maxDocs = Integer.MAX_VALUE;
    }
    private void addToHeapIfNotInSet(URISafonUboiTeman u){
        if (!heapSet.contains(u)){
            timeHeap.insert(u);
            heapSet.add(u);
            if(u.getDocumentTxt() != null){
                totalBytes += u.getDocumentTxt().getBytes().length;
            } else {
                totalBytes += u.getDocumentBinaryData().length;
            }
        }
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
        if (uri == null || uri.getPath() == null || uri.getPath().equals("") || key == null || key.equals("") || docTree.get(uri) == null) {
            throw new IllegalArgumentException("the uri is null or blank, if there is no document stored at that uri, or the key is null or blank");
        }
        if (trueStackSize <= this.getTrueStackSize()) {
            String old = this.getMetadata(uri, key);
            commandStack.push(new GenericCommand<URI>(uri, (uri1) -> {if(docTree.get(uri1)!= null){metaTrie.delete(key+"-"+value, new URISafonUboiTeman(uri)); docTree.get(uri1).setMetadataValue(key, old); metaTrie.put(key+"-"+old, new URISafonUboiTeman(uri1));}}));
        }
        trueStackSize = this.getTrueStackSize();
        String old = this.docTree.get(uri).setMetadataValue(key, value);
        URISafonUboiTeman u = new URISafonUboiTeman(uri);
        if (old != null){
            metaTrie.delete(key +"-"+old, u);
        }
        //ah
        metaTrie.put(key+"-"+value, u);
        //put back maybe
        //this.docTree.get(uri).setLastUseTime(System.nanoTime());
        //???
        //URISafonUboiTeman u = new URISafonUboiTeman(uri);
        u.setLastUseTime(System.nanoTime());
        this.addToHeapIfNotInSet(u);
        this.timeHeap.reHeapify(u);
        this.cleanUpMemory();
        return old;

    }
    @Override
    public String getMetadata(URI uri, String key) {
        if (uri == null || uri.getPath() == null || uri.getPath().equals("") || key == null || key.equals("") || docTree.get(uri) == null) {
            throw new IllegalArgumentException("the uri is null or blank, if there is no document stored at that uri, or the key is null or blank");
        }
        String val = this.docTree.get(uri).getMetadataValue(key);
        if(val != null) {
            this.docTree.get(uri).setLastUseTime(System.nanoTime());
            URISafonUboiTeman u = new URISafonUboiTeman(uri);
            this.addToHeapIfNotInSet(u);
            this.timeHeap.reHeapify(u);
        }
        this.cleanUpMemory();
        return val;
    }

    @Override
    public int put(InputStream input, URI uri, DocumentFormat format) {
        if (uri == null || uri.getPath() == null || uri.getPath().equals("") || format == null) {
            throw new IllegalArgumentException("uri is null or empty, or format is null");
        }
        if (input == null) {
            DocumentImpl prev = docTree.get(uri);
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
            if(getTotalBytes() > maxBytes || heapSet.size() > maxDocs) {
                while (getTotalBytes() > maxBytes || heapSet.size() > maxDocs) {
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
            if(getTotalBytes() > maxBytes || heapSet.size() > maxDocs) {
                while (getTotalBytes() > maxBytes || heapSet.size() > maxDocs) {
                    cull();
                }
            }
            return prev.hashCode();
        }
    }

    private void throwItIntoTheTrie(Document d) {
        Set<String> set = d.getWords();
        for (String s : set) {
            wordTrie.put(s, new URISafonUboiTeman(d.getKey()));
        }
    }
    private void throwIntoMetaTrie(Document d){
        HashMap<String, String> h = d.getMetadata();
        Set<String> set = h.keySet();
        for(String s:set){
            metaTrie.put(s+"-"+h.get(s), new URISafonUboiTeman(d.getKey()));
        }
    }

    private DocumentImpl putBasedOnFormat(URI uri, DocumentFormat format, byte[] bytes) {
        DocumentImpl prev;
        if(heapSet.size() > maxDocs || getTotalBytes() > maxBytes){
            cull();
        }
        if (format == DocumentFormat.TXT) {
            DocumentImpl current = new DocumentImpl(uri, new String(bytes), null);
            current.setLastUseTime(System.nanoTime());
            prev = docTree.put(uri, current);;
            //this.delete(uri);
            this.throwItIntoTheTrie(current);
            this.throwIntoMetaTrie(current);
            totalBytes += current.getDocumentTxt().getBytes().length;
            this.cleanUpMemory();

        } else {
            prev = docTree.put(uri, new DocumentImpl(uri, bytes));
            this.throwIntoMetaTrie(docTree.get(uri));
            totalBytes += bytes.length;
            this.cleanUpMemory();

        }
        if(prev != null) {
            if (prev.getDocumentTxt() != null){
                totalBytes -= prev.getDocumentTxt().getBytes().length;
            } else {
                totalBytes -= prev.getDocumentBinaryData().length;
            }
            URISafonUboiTeman u = new URISafonUboiTeman(prev.getKey());
            u.setLastUseTime(System.nanoTime());
            this.addToHeapIfNotInSet(u);
            this.timeHeap.reHeapify(u);
            //this.timeHeap.remove();
            //this else is very new
            this.cleanUpMemory();

        } else {
            URISafonUboiTeman steve = new URISafonUboiTeman(uri);
            this.timeHeap.insert(steve);
            this.timeHeap.reHeapify(steve);
            heapSet.add(steve);
            this.cleanUpMemory();

        }
            /*URISafonUboiTeman steve = new URISafonUboiTeman(uri);
            this.timeHeap.insert(steve);*/
            //this.timeHeap.reHeapify(steve);
        this.cleanUpMemory();
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
        Document d = this.docTree.get(url);
        URISafonUboiTeman u = new URISafonUboiTeman(url);
        if(d != null) {
            addToHeapIfNotInSet(u);
            d.setLastUseTime(System.nanoTime());
            this.timeHeap.reHeapify(u);
        }
        this.cleanUpMemory();
        return d;
    }


    //i think i dealt properly with the trie?
    @Override
    public boolean delete(URI url) {
       // DocumentImpl prev = docTree.put(url, null);
        //this is a change
        DocumentImpl prev = docTree.get(url);
        if (prev != null) {
            prev.setLastUseTime(Integer.MIN_VALUE);
            this.timeHeap.reHeapify(new URISafonUboiTeman(url));
            Set<String> words = prev.getWords();
            for (String s : words) {
                wordTrie.delete(s, new URISafonUboiTeman(url));
            }
            HashMap<String, String> oldMeta = prev.getMetadata();
            Set<String> prevKeys = oldMeta.keySet();
            for(String s : prevKeys){
                metaTrie.delete(s+"-"+oldMeta.get(s), new URISafonUboiTeman(prev.getKey()));
            }
            //debatable
            prev.setLastUseTime(Integer.MIN_VALUE);
            heapSet.remove(this.timeHeap.remove());
            if(prev.getDocumentTxt() != null){
                totalBytes -= prev.getDocumentTxt().getBytes().length;
            } else {
                totalBytes -= prev.getDocumentBinaryData().length;
            }
            docTree.put(url, null);
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
        long time = System.nanoTime();
        if (commandStack.size() == 0) {
            throw new IllegalStateException("Nothing to undo");
        }
        Undoable c = commandStack.pop();
        if(c.getClass().equals(GenericCommand.class)){
            if(docTree.get((URI)((GenericCommand)c).getTarget()) != null) {
                docTree.get((URI) ((GenericCommand) c).getTarget()).setLastUseTime(time);
                timeHeap.reHeapify(new URISafonUboiTeman((URI) ((GenericCommand) c).getTarget()));

            }
        } else {
            for(Object g:((CommandSet)c)){
                if(docTree.get((URI)((GenericCommand)g).getTarget()) != null) {
                    docTree.get((URI) ((GenericCommand) g).getTarget()).setLastUseTime(time);
                    timeHeap.reHeapify(new URISafonUboiTeman((URI) ((GenericCommand) c).getTarget()));
                }
            }
        }
        c.undo();
    }

    @Override
    public void undo(URI url) throws IllegalStateException {
        long time = System.nanoTime();
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
                if(docTree.get(url) != null) {
                    docTree.get(url).setLastUseTime(time);
                    timeHeap.reHeapify(new URISafonUboiTeman(url));
                }
                /////
                found = true;
            } else if (commandStack.peek().getClass().equals(CommandSet.class) && ((CommandSet<URI>)(commandStack.peek())).containsTarget(url)){
                found = true;
                ((CommandSet<URI>)(commandStack.peek())).undo(url);
                if(docTree.get(url) != null) {
                    docTree.get(url).setLastUseTime(time);
                    timeHeap.reHeapify(new URISafonUboiTeman(url));
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
        long time = System.nanoTime();
        Comparator<URISafonUboiTeman> c = new docComp(keyword);
        List<URISafonUboiTeman> l = wordTrie.getSorted(keyword, c);
        for(URISafonUboiTeman d : l){
            d.setLastUseTime(time);
            this.addToHeapIfNotInSet(d);
            this.timeHeap.reHeapify(d);
        }
        List<Document> takeTheL= new ArrayList<>();
        for(URISafonUboiTeman d : l) {
            takeTheL.add(d.gimme());
        }
        this.cleanUpMemory();
        return takeTheL;
    }

    @Override
    public List<Document> searchByPrefix(String keywordPrefix) {
        long time = System.nanoTime();
        Comparator<URISafonUboiTeman> c = new preComp(keywordPrefix);
        List<URISafonUboiTeman> l = wordTrie.getAllWithPrefixSorted(keywordPrefix, c);
        for(URISafonUboiTeman d : l){
            d.setLastUseTime(time);
            this.addToHeapIfNotInSet(d);
            this.timeHeap.reHeapify(d);
        }
        List<Document> takeTheL= new ArrayList<>();
        for(URISafonUboiTeman d : l) {
            takeTheL.add(d.gimme());
        }
        this.cleanUpMemory();
        return takeTheL;
    }

    @Override
    public Set<URI> deleteAll(String keyword) {
        Comparator<URISafonUboiTeman> c = new docComp(keyword);
        List<URISafonUboiTeman> list = wordTrie.getSorted(keyword, c);
        List<Document> takeTheL= new ArrayList<>();
        for(URISafonUboiTeman d : list) {
            takeTheL.add(d.gimme());
        }
        this.cleanUpMemory();
        return deleteAllAndGetUris(takeTheL);
    }

    @Override
    public Set<URI> deleteAllWithPrefix(String keywordPrefix) {
        Comparator<URISafonUboiTeman> c = new preComp(keywordPrefix);
        List<URISafonUboiTeman> list = wordTrie.getAllWithPrefixSorted(keywordPrefix, c);
        List<Document> takeTheL= new ArrayList<>();
        for(URISafonUboiTeman d : list) {
            takeTheL.add(d.gimme());
        }
        this.cleanUpMemory();
        return deleteAllAndGetUris(takeTheL);
    }

    private Set<URI> deleteAllAndGetUris(List<Document> list) {
        Set<URI> uris = new HashSet<>();
        Set<String> words;
        for (Document d : list) {
            uris.add(d.getKey());
            //delete from tree
            //moved to bottom cuz im an idiot
            //delete from wordtrie
            words = d.getWords();
            for (String s : words) {
                wordTrie.delete(s, new URISafonUboiTeman(d.getKey()));
            }
            //delete from metaTrie
            HashMap<String, String> oldMeta = d.getMetadata();
            Set<String> oldKeys = oldMeta.keySet();
            for(String s:oldKeys){
                metaTrie.delete(s+"-"+oldMeta.get(s), new URISafonUboiTeman(d.getKey()));
            }
            if (trueStackSize <= this.getTrueStackSize()) {
                CommandSet<URI> c = new CommandSet<URI>();
                for(Document doc: list){
                    c.addCommand(new GenericCommand<URI>(doc.getKey(), (uri1) -> {
                        if(((Document)doc).getDocumentTxt() != null) {
                            if(doc.getDocumentTxt().getBytes().length > maxBytes){
                                throw new IllegalArgumentException();
                            }
                            if (heapSet.size() >= maxDocs){
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
            if(d.getDocumentTxt() != null){
                totalBytes -= d.getDocumentTxt().getBytes().length;
            } else {
                totalBytes -= d.getDocumentBinaryData().length;
            }
            d.setLastUseTime(Integer.MIN_VALUE);
            timeHeap.reHeapify(new URISafonUboiTeman(d.getKey()));
            heapSet.remove(timeHeap.remove());//gonna have to delete from metatrie if that becomes a thing
            docTree.put(d.getKey(), null);
        }
        this.cleanUpMemory();
        return uris;
    }

    //there's a better way to do this but i also need to finish. wooo ;-;
    @Override
    public List<Document> searchByMetadata(Map<String, String> keysValues) {
        long time = System.nanoTime();
        List<Document> list = new ArrayList<>();
        List<URISafonUboiTeman> l1 = new ArrayList<>();
        List<URISafonUboiTeman> l2 = new ArrayList<>();
        if(keysValues.isEmpty()){
            return list;
        }
        Set<String> keys = keysValues.keySet();
        for(String s:keys){
           l1 = metaTrie.get(s+"-"+keysValues.get(s)).stream().toList();
           if(l2.isEmpty()){
               l2.addAll(l1);
           } else {
               l2 = this.inBoth(l1,l2);
           }
        }
        for (URISafonUboiTeman d : l2) {
                        d.setLastUseTime(time);
                        this.addToHeapIfNotInSet(d);
                        this.timeHeap.reHeapify(d);
                        list.add(d.gimme());
                    }
        this.cleanUpMemory();
        return list;
    }
    private List<URISafonUboiTeman> inBoth(List<URISafonUboiTeman> l1, List<URISafonUboiTeman> l2){
        List <URISafonUboiTeman> takeTheL = new ArrayList<>();
        for(URISafonUboiTeman d : l1){
            if (l2.contains(d)){
                takeTheL.add(d);
            }
        }
        this.cleanUpMemory();
        return takeTheL;
    }
    @Override
    public List<Document> searchByKeywordAndMetadata(String keyword, Map<String, String> keysValues) {
        Comparator<URISafonUboiTeman> c = new docComp(keyword);
        List<URISafonUboiTeman> l = wordTrie.getSorted(keyword, c);
        List<Document> takeTheL= new ArrayList<>();
        for(URISafonUboiTeman d : l) {
            takeTheL.add(d.gimme());
        }
        this.cleanUpMemory();
        return getMetaIntersections(keysValues, takeTheL);
    }

    @Override
    public List<Document> searchByPrefixAndMetadata(String keywordPrefix, Map<String, String> keysValues) {
        Comparator<URISafonUboiTeman> c = new preComp(keywordPrefix);
        List<URISafonUboiTeman> byPrefix = wordTrie.getAllWithPrefixSorted(keywordPrefix, c);
        List<Document> takeTheL= new ArrayList<>();
        for(URISafonUboiTeman d : byPrefix) {
            takeTheL.add(d.gimme());
        }
        this.cleanUpMemory();
        return getMetaIntersections(keysValues, takeTheL);
    }

    private List<Document> getMetaIntersections(Map<String, String> keysValues, List<Document> byPrefix) {
        long time = System.nanoTime();
        List<Document> byMetaData = new ArrayList<>();
        List<URISafonUboiTeman> l1 = new ArrayList<>();
        List<URISafonUboiTeman> l2 = new ArrayList<>();
        Set<String> keys = keysValues.keySet();
        for(String s:keys){
            l1 = metaTrie.get(s+"-"+keysValues.get(s)).stream().toList();
            if(l2.isEmpty()){
                l2 = l1;
            } else {
                l2 = this.inBoth(l1,l2);
            }
        }
        for (URISafonUboiTeman d : l2) {
            d.setLastUseTime(time);
            this.addToHeapIfNotInSet(d);
            this.timeHeap.reHeapify(d);
            byMetaData.add(d.gimme());
        }
        List<Document> toReturn = new ArrayList<Document>();
        for (Document d : byPrefix) {
            if (byMetaData.contains(d)) {
                d.setLastUseTime(time);
                this.addToHeapIfNotInSet(new URISafonUboiTeman(d.getKey()));
                this.timeHeap.reHeapify(new URISafonUboiTeman(d.getKey()));
                toReturn.add(d);
            }
        }
        this.cleanUpMemory();
        return toReturn;
    }

    @Override
    public Set<URI> deleteAllWithMetadata(Map<String, String> keysValues) {
        List<Document> toDelete = this.searchByMetadata(keysValues);
        this.cleanUpMemory();
        return deleteAllAndGetUris(toDelete);
    }

    @Override
    public Set<URI> deleteAllWithKeywordAndMetadata(String keyword, Map<String, String> keysValues) {
        Comparator<URISafonUboiTeman> c = new docComp(keyword);
        List<URISafonUboiTeman> toDeleteKeyword = wordTrie.getSorted(keyword, c);
        List<Document> takeTheL= new ArrayList<>();
        for(URISafonUboiTeman d : toDeleteKeyword) {
            takeTheL.add(d.gimme());
        }
        List<Document> toDelete = getMetaIntersections(keysValues, takeTheL);
        this.cleanUpMemory();
        return deleteAllAndGetUris(toDelete);
    }

    @Override
    public Set<URI> deleteAllWithPrefixAndMetadata(String keywordPrefix, Map<String, String> keysValues) {
        Comparator<URISafonUboiTeman> c = new preComp(keywordPrefix);
        List<URISafonUboiTeman> byPrefix = wordTrie.getAllWithPrefixSorted(keywordPrefix, c);
        List<Document> takeTheL= new ArrayList<>();
        for(URISafonUboiTeman d : byPrefix) {
            takeTheL.add(d.gimme());
        }
        List<Document> toDelete = getMetaIntersections(keysValues, takeTheL);
        this.cleanUpMemory();
        return deleteAllAndGetUris(toDelete);
    }

    @Override
    public void setMaxDocumentCount(int limit) {
       // int heapSet.size() = this.heapSet.size();
        this.maxDocs = limit;
        Document prev;
        if(heapSet.size() > this.maxDocs){
            while(heapSet.size() > this.maxDocs) {
                Document d = cull();
               // heapSet.size()--;
                /*if(d.getDocumentTxt() != null){
                    totalBytes -= d.getDocumentTxt().getBytes().length;
                } else {
                    totalBytes -= d.getDocumentBinaryData().length;
                }*/
            }
        }
    }
    private void removeFromUndos(Document d){
        URI uri = d.getKey();
        this.docTree.put(uri, null);
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
        this.cleanUpMemory();
    }
    @Override
    public void setMaxDocumentBytes(int limit) {
        this.maxBytes = limit;
        Document prev;
        if(totalBytes > maxBytes){
            while(totalBytes > maxBytes){
                prev = cull();
               /* if(prev.getDocumentTxt() != null){
                    totalBytes -= prev.getDocumentTxt().getBytes().length;
                } else {
                    totalBytes -= prev.getDocumentBinaryData().length;
                }*/
            }
        }
    }

    private Document cull() {
        URISafonUboiTeman prev;
        prev = timeHeap.peek();
        //this.timeHeap.reHeapify(prev);
        heapSet.remove(this.timeHeap.remove());
        Document d = prev.gimme();
        try {
            this.docTree.moveToDisk(d.getKey());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //removeFromUndos(prev.gimme());
        //heapSet.size()--;
        if(d.getDocumentTxt() != null){
            totalBytes -= d.getDocumentTxt().getBytes().length;
        } else {
            totalBytes -= d.getDocumentBinaryData().length;
        }
        return d;
    }
    //welp we're doing this now i guess
    private void cleanUpMemory(){
        while(totalBytes > maxBytes || heapSet.size() > maxDocs){
            cull();
        }
    }

    private int getTotalBytes(){
        return totalBytes;
    }

    private class docComp implements Comparator<URISafonUboiTeman> {
        String comper;

        public docComp(String s) {
            this.comper = s;
        }

        @Override
        public int compare(URISafonUboiTeman o1, URISafonUboiTeman o2) {
            if (o1.wordCount(comper) > o2.wordCount(comper)) {
                return -1;
            } else if (o1.wordCount(comper) < o2.wordCount(comper)) {
                return 1;
            } else {
                return 0;
            }
        }
    }
    private class preComp implements Comparator<URISafonUboiTeman>{
        String comper;

        public preComp(String s) {
            this.comper = s;
        }
        @Override
        public int compare(URISafonUboiTeman o1, URISafonUboiTeman o2){
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
            return  d2C - d1C;
        }
    }
    private class URISafonUboiTeman implements Comparable<URISafonUboiTeman> {
        URI uri;
        public URISafonUboiTeman(URI u){
            this.uri = u;
        }
        public String setMetadataValue(String key, String value) {
           return docTree.get(uri).setMetadataValue(key, value);
        }


        public String getMetadataValue(String key) {
            return docTree.get(uri).getMetadataValue(key);
        }


        public HashMap<String, String> getMetadata() {
            return docTree.get(uri).getMetadata();
        }


        public void setMetadata(HashMap<String, String> metadata) {
            docTree.get(uri).setMetadata(metadata);
        }

        public String getDocumentTxt() {
            return docTree.get(uri).getDocumentTxt();
        }


        public URI getKey() {
            return this.uri;
        }


        public int wordCount(String word) {
            return docTree.get(uri).wordCount(word);
        }

        public Set<String> getWords() {
            return docTree.get(uri).getWords();
        }


        public long getLastUseTime() {
            return docTree.get(uri).getLastUseTime();
        }


        public void setLastUseTime(long timeInNanoseconds) {
            docTree.get(uri).setLastUseTime(timeInNanoseconds);
        }


        public HashMap<String, Integer> getWordMap() {
            return docTree.get(uri).getWordMap();
        }


        public void setWordMap(HashMap<String, Integer> wordMap) {
            docTree.get(uri).setWordMap(wordMap);
        }


        public byte[] getDocumentBinaryData(){
            return docTree.get(uri).getDocumentBinaryData();
        }


        public int hashCode() {
            return docTree.get(uri).hashCode();
        }

        public boolean equals(Object o){
            return getKey().equals(((URISafonUboiTeman)o).getKey());
        }

        @Override
        public int compareTo(URISafonUboiTeman o) {
            if(this.getLastUseTime() == o.getLastUseTime()){
                return 0;
            } else if (this.getLastUseTime() > o.getLastUseTime()){
                return 1;
            }
            return -1;
    }
        public Document gimme(){
            return docTree.get(uri);
        }

    }
}