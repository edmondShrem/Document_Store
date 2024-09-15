package edu.yu.introtoalgs;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Optional;
import java.util.PriorityQueue;

public class LFU<Key, Value> extends LFUBase<Key, Value> {
//POOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOM
    //POOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOM
    private final int maxSize;
    private boolean isFull;
    private PriorityQueue<Object[]> usageHeap;
    //the arrays are set as follows: 0 = number of accesses. 1 = Key. 2 = Value
    private HashMap<Key, Object[]> dataMap;
    /**
     * Constructor: supplies the maximum size of the cache: when the cache is
     * full, the LFU eviction policy MUST be used to select a cache entry to swap
     * out to make room for the new cache entry.
     *
     * @param maxSize maximum size of the cache, must be greater than 0.
     * @throws IllegalArgumentException as appropriate.
     * @see #set
     */
    public LFU(int maxSize) {
        super(maxSize);
        if (maxSize < 1){
            throw new IllegalArgumentException("Max Size cannot be less than one");
        }
        this.maxSize = maxSize;
        this.isFull = false;
        //is this a good idea?
        this.usageHeap = new PriorityQueue<>(new entryComparator());
        this.dataMap = new HashMap<>();
    }
    @Override
    public boolean set(Key key, Value value) {
        boolean isThere = this.dataMap.get(key) != null;

        return false;
    }

    @Override
    public Optional<Value> get(Key key) {
        return Optional.empty();
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public void clear() {

    }

    static class entryComparator implements Comparator<Object[]> {
        @Override
        public int compare(Object[] o1, Object[] o2) {
            //returns a negative number if o1 is lower priority, positive it is higher, and zero if equal
            return (int)o2[0] - (int)o1[0];
        }
    }
}
