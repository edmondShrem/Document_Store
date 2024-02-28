package edu.yu.cs.com1320.project.impl;

import edu.yu.cs.com1320.project.HashTable;

import java.util.*;

public class HashTableImpl<Key, Value> implements HashTable<Key, Value>{
    private class Entry<K, V>{
        K key;
        V value;
        Entry<K, V> next;
        Entry(K k, V v){
            if(k == null){
                throw new IllegalArgumentException();
            }
            key = k;
            value = v;
            next = null;
        }
    }
    private Entry<Key, Value>[] entries;
    public HashTableImpl(){
        entries = new Entry[5];
    }


    @Override
    public Value get(Key k) {
        int i = hashFunction((Key) k);
        Entry<Key, Value> e = entries[i];
        while(e != null){
            if(e.key.equals(k)){
                return e.value;
            }
            e = e.next;
        }
        return null;
    }
//worry abt length later
    @Override
    public Value put(Key k, Value v) {
        if(v == null){
            return this.delete((Key) k);
        }
        if(gottaDouble()){doubleAndRehash();}
        Entry<Key, Value> e = new Entry<Key, Value>((Key)k, (Value)v);
        int i = hashFunction(e.key);
        Entry<Key, Value> old = entries[i];
        if(old == null){
            entries[i] = e;
            return null;
        } else if (old.key.equals(k)){
            Value temp = old.value;
            e.next = entries[i].next;
            entries[i] = e;
            return temp;
        }
        while (old.next != null){
            if (old.next.key.equals(k)){
                Value temp = old.next.value;
                e.next = old.next.next;
                old.next = e;
                old = e;
                return temp;
            }
            old = old.next;
        }
        old.next = e;
        return null;
    }
    private Value delete(Key k){
        if (!containsKey(k)){
            return null;
        }
        int i = hashFunction(k);
        if(entries[i].key.equals(k)){
            Value v = entries[i].value;
            entries[i] = entries[i].next;
            return v;
        }
        Entry<Key, Value>old = entries[i];
        while(old.next != null){
            if(old.next.key.equals(k)){
                Value v = old.next.value;
                old.next = old.next.next;
                return v;
            }
            old = old.next;
        }
        return null;
    }

    @Override
    public boolean containsKey(Key key) {
        if(key == null){
            throw new NullPointerException();
        }
        int i = hashFunction((Key) key);
        Entry<Key, Value> e = entries[i];
        if (e == null){
            return false;
        }
        if(e.key.equals(key)){
            return true;
        }
        while(e.next != null){
            e = e.next;
            if(e.key.equals(key)){
                return true;
            }
        }
        return false;
    }

    @Override
    public Set<Key> keySet() {
        Set<Key> keys = new HashSet<>();
        for(Entry<Key, Value> e : this.entries){
            while(e != null){
                keys.add(e.key);
                e = e.next;
            }
        }
        return Collections.unmodifiableSet(keys);

    }

    @Override
    public Collection<Value> values() {
        Collection<Value> values = new ArrayList<>();
        for(Entry<Key, Value> e : this.entries){
            while(e != null){
                values.add(e.value);
                e = e.next;
            }
        }
        return Collections.unmodifiableCollection(values);

    }

    @Override
    public int size() {
        int c = 0;
        for(Entry<Key, Value> e : this.entries){
            while(e != null){
                c++;
                e = e.next;
            }
        }
        return c;
    }
    //if the array is full, returns true
    private boolean gottaDouble(){
        int size = this.size();
        return entries.length < size / 4;
    }
    private void doubleAndRehash(){
        Entry<Key, Value>[] n = new Entry[entries.length * 2];
        for(Entry<Key, Value> e : this.entries){
            while(e != null){
                int i = (e.key.hashCode() & 0x7fffffff) % n.length;
                if(n[i] == null){
                    n[i] = new Entry<>(e.key, e.value);
                } else {
                    Entry old = n[i];
                    while(old.next != null){
                        old = old.next;
                    }
                    old.next = new Entry<>(e.key, e.value);
                }
                e = e.next;
            }
        }
        entries = n;
    }

    private int hashFunction(Key k){
        return(k.hashCode() & 0x7fffffff) % this.entries.length;
    }
}
