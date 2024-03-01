package edu.yu.cs.com1320.project.stage3.impl;

import edu.yu.cs.com1320.project.HashTable;
import edu.yu.cs.com1320.project.impl.HashTableImpl;
import edu.yu.cs.com1320.project.stage3.Document;
import edu.yu.cs.com1320.project.impl.StackImpl;
import edu.yu.cs.com1320.project.undo.Command;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

public class DocumentStoreImpl implements edu.yu.cs.com1320.project.stage3.DocumentStore{
    private HashTable<URI, DocumentImpl> docs;
    private StackImpl<Command> commandStack;
    private int stackSize;

    public DocumentStoreImpl(){
        this.docs = new HashTableImpl<>();
        this.commandStack = new StackImpl<>();
        this.stackSize = 0;
    }

    @Override
    public String setMetadata(URI uri, String key, String value) {
        if(uri == null || uri.getPath() == null || uri.getPath().equals("") || key == null || key.equals("") || docs.get(uri) == null){
            throw new IllegalArgumentException("the uri is null or blank, if there is no document stored at that uri, or the key is null or blank");
        }
        if(stackSize <= commandStack.size()) {
            String old = this.getMetadata(uri, key);
            commandStack.push(new Command(uri, (uri1) -> setMetadata(uri1, key, old)));
            stackSize = commandStack.size();
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

    @Override
    public void undo() throws IllegalStateException {
        if(commandStack.size() == 0){
            throw new IllegalStateException("Nothing to undo");
        }
        Command c = commandStack.pop();
        c.undo();
    }

    @Override
    public void undo(URI url) throws IllegalStateException {
        if(commandStack.size() == 0){
            throw new IllegalStateException("Nothing to undo");
        }
        StackImpl<Command> temp = new StackImpl<>();
        boolean found = false;
        while (!found){
            //ensures that if its not found everything gets put back first before throwing, dont wanna break the system
            if(commandStack.size() == 0){
                int size = temp.size();
                for(int i = 0; i < size; i ++){
                    //redo
                    commandStack.push(temp.pop());
                }
                throw new IllegalStateException("URI is not represented in the command stack");
            }
            if (commandStack.peek().getUri().equals(url)){
                undo();
                found = true;
            } else {
                commandStack.peek().undo();
                temp.push(commandStack.pop());
            }
        }
        for(int i = 0; i < temp.size(); i ++){
            //how do i "redo" them?
            //probably gotta figure out howda undo em first, but who knows maaaaaaaaaaaaan
            commandStack.push(temp.pop());
        }
    }
}


