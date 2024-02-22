package edu.yu.cs.com1320.project.impl;

import edu.yu.cs.com1320.project.HashTable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class HashTableImpl<Key, Value> implements HashTable{
    private class Entry<Key, Value>{
        Key key;
        Value value;
        Entry<Key, Value> next;
        Entry(Key k, Value v){
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
    public Object get(Object k) {
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

    @Override
    public Object put(Object k, Object v) {
        if(v == null){
            return this.delete((Key) k);
        }
        Entry<Key, Value> e = new Entry<Key, Value>((Key)k, (Value)v);
        int i = hashFunction(e.key);
        Entry<Key, Value> old = entries[i];
        if(old == null){
            entries[i] = e;
            return null;
        }
        while (old.next != null){
            if (old.next.key.equals(e.key)){
                Value temp = old.next.value;
                e.next = old.next.next;
                old.next = e;
                old = e;
                return temp;
            }
            if (old.next.next == null){
                old.next.next = e;
                return null;
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
    public boolean containsKey(Object o) {
        if(o == null){
            throw new NullPointerException();
        }
        int i = hashFunction((Key) o);
        Entry<Key, Value> e = entries[i];
        if (e == null){
            return false;
        }
        if(e.key.equals(o)){
            return true;
        }
        while(e.next != null){
            e = e.next;
            if(e.key.equals(o)){
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
        return java.util.Collections.unmodifiableSet(keys);

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
        return java.util.Collections.unmodifiableCollection(values);

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
    private int hashFunction(Key k){
        return(k.hashCode() & 0x7fffffff) % this.entries.length;
    }
}
