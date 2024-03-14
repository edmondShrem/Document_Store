package edu.yu.cs.com1320.project.impl;

import edu.yu.cs.com1320.project.Trie;

import java.util.*;

public class TrieImpl<Value> implements Trie<Value>{
    private static final int alphabetSize = 256; // extended ASCII
    private Node<Value> root;// root of trie

    public TrieImpl(){
        this.root = new Node<Value>();
    }

    @Override
    public void put(String key, Value val) {
        //deleteAll the value from this key
        if (val == null)
        {
            this.deleteAll(key);
        }
        else
        {
            this.root = put(this.root, key, val, 0);
        }
    }
    private Node put(Node x, String key, Value val, int d)
    {
        //create a new node
        if (x == null)
        {
            x = new Node();
        }
        //we've reached the last node in the key,
        //set the value for the key and return the node
        if (d == key.length())
        {
            x.vals.add(val);
            return x;
        }
        //proceed to the next node in the chain of nodes that
        //forms the desired key
        char c = key.charAt(d);
        x.links[c] = this.put(x.links[c], key, val, d + 1);
        return x;
    }

    @Override
    public List<Value> getSorted(String key, Comparator<Value> comparator) {
        Set<Value> s = this.get(key);
        List<Value> l = this.getSortedList(s, comparator);
        return l;
    }

    //literally trash but wtvr
    private List<Value> getSortedList(Set<Value> s, Comparator<Value> comparator) {
        Value largest = null;
        int largeI = 0;
        Value[] arr = (Value[]) s.toArray();
        List<Value> l = new ArrayList<>();
        for (Value v : arr) {
            for (int i = 0; i < arr.length; i++) {
                if (arr[i] != null) {
                    if (comparator.compare(largest, arr[i]) < 0) {
                        largest = arr[i];
                        largeI = i;
                    }
                }
            }
            l.add(largest);
            arr[largeI] = null;
        }
        return l;
    }


    @Override
    public Set<Value> get(String key) {
        //shallow copy?
        Set<Value> s = this.get(this.root, key, 0).vals;
        Set<Value> copy = new HashSet<>(s);
        return copy;
    }
    private Node get(Node x, String key, int d)
    {
        //link was null - return null, indicating a miss
        if (x == null)
        {
            return null;
        }
        //we've reached the last node in the key,
        //return the node
        if (d == key.length())
        {
            return x;
        }
        //proceed to the next node in the chain of nodes that
        //forms the desired key
        char c = key.charAt(d);
        return this.get(x.links[c], key, d + 1);
    }

    @Override
    public List<Value> getAllWithPrefixSorted(String prefix, Comparator<Value> comparator) {
        return null;
    }

    @Override
    public Set<Value> deleteAllWithPrefix(String prefix) {
        return null;
    }

    @Override
    public Set<Value> deleteAll(String key) {
        Node<Value> n = this.get(this.root, key, 0);
        Set<Value> s = this.get(key);
        n.vals = new HashSet<Value>();
        assert s != null;
        return s;
    }

    private Node deleteAll(Node x, String key, int d) {
        if (x == null)
        {
            return null;
        }
        if (d == key.length())
        {
            x.vals = null;
        }
        else
        {
            char c = key.charAt(d);
            x.links[c] = this.deleteAll(x.links[c], key, d + 1);
        }
        if (x.vals != null)
        {
            return x;
        }
        for (int c = 0; c <TrieImpl.alphabetSize; c++)
        {
            if (x.links[c] != null)
            {
                return x; //not empty
            }
        }
        return null;
    }

    @Override
    public Value delete(String key, Value val) {
        Node<Value> n = this.get(this.root, key, 0);
        if(n.vals.remove(val)){
            return val;
        }
        return null;
    }

    public static class Node<Value>
    {
        protected Set<Value> vals = new HashSet<>();
        protected Node[] links = new Node[TrieImpl.alphabetSize];

        }
    

}
