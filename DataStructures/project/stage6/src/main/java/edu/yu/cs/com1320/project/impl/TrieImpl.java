//figured out the problem: the comparator should implicitly do decending.
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
            return;
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
        if(key == null){
            throw new IllegalArgumentException("null key");
        }
        Set<Value> s = this.get(key);
        List<Value> l = this.getSortedList(s, comparator);
        return l;
    }

    //literally trash but wtvr
    private List<Value> getSortedList(Set<Value> s, Comparator<Value> comparator) {
        Value largest = null;
        int largeI = 0;
        Value[] arr = (Value[]) s.toArray();
        List<Value> l;
        for(int i = 0; i < arr.length; i ++){
            largest = arr[i];
            largeI = i;
            for(int j = i; j < arr.length; j++){
                if(comparator.compare(largest, arr[j]) > 0){
                    largeI = j;
                }
            }
            arr[i] = arr[largeI];
            arr[largeI] = largest;
        }
        l = Arrays.stream(arr).toList();
        return l;
    }


    @Override
    public Set<Value> get(String key) {
        if(key == null){
            throw new IllegalArgumentException("null key");
        }
        //shallow copy?
        Node<Value> n = this.get(this.root, key, 0);
        if(n == null || n.vals == null){
            return new HashSet<Value>();
        }
        Set<Value> s = n.vals;
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
        if(prefix == null || comparator == null){
            throw new IllegalArgumentException("null prefix or comparator");
        }
        if(prefix.isEmpty()){
            return new ArrayList<>();
        }
        Node<Value> subRoot = this.get(this.root, prefix, 0);
        Set<Value> s = new HashSet<Value>();
        walkDownAndAdd(subRoot, s);
        List<Value> l = getSortedList(s, comparator);
        return l;
    }

    @Override
    public Set<Value> deleteAllWithPrefix(String prefix) {
        if(prefix == null){
            throw new IllegalArgumentException("null prefix");
        }
        if(prefix.isEmpty()){
            return new HashSet<>();
        }
        Node<Value> subRoot = this.get(this.root, prefix, 0);
        Set<Value> s = new HashSet<Value>();
        walkDownAndAdd(subRoot, s);
        subRoot = this.get(this.root, prefix, 0);
        subRoot.vals = new HashSet<Value>();
         for(int i = 0; i < alphabetSize; i++){
             subRoot.links[i] = null;
         }
        return s;
    }
    private void walkDownAndAdd(Node<Value> n, Set<Value>s){
        if(n == null){
            return;
        }
        s.addAll(n.vals);
        for(Node<Value> k : n.links){
            walkDownAndAdd(k, s);
        }
    }


    @Override
    public Set<Value> deleteAll(String key) {
        if(key == null){
            throw new IllegalArgumentException("null key");
        }
        Node<Value> n = this.get(this.root, key, 0);
        Set<Value> s = this.get(key);
        n.vals = new HashSet<Value>();
        this.deleteAll(this.root, key, 0);
        return s;
    }

    private Node deleteAll(Node x, String key, int d) {
        if (x == null)
        {
            return null;
        }
        if (d == key.length())
        {
            x.vals = new HashSet<Value>();
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
        for (int c = 0; c <alphabetSize; c++)
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
        if(key == null){
            throw new IllegalArgumentException("null key");
        }
        if(val == null){
            throw new IllegalArgumentException("null val");
        }
        Node<Value> n = this.get(this.root, key, 0);
        if(n != null && n.vals.remove(val)){
            if(n.vals.isEmpty()){
                this.deleteAll(this.root, key, 0);
            }
            return val;
        }
        return null;
    }
//check permission
    private static class Node<Value>
    {
        public Set<Value> vals = new HashSet<>();
        public Node[] links = new Node[256];
        }
}
