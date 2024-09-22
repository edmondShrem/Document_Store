package edu.yu.introtoalgs;

import java.util.*;

public class LFU<Key, Value> extends LFUBase<Key, Value> {
//POOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOM
    //POOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOM
    private final int maxSize;
    //private PriorityQueue<Object[]> usageHeap;
    private HashMap<Integer, HashMap<Key, Object[]>> usageMap;
    private int lowestUsage;
    private Key lowKey;
    //the arrays are set as follows: 0 = number of accesses. 1 = Key 2 = Value
    private HashMap<Key, Object[]> dataMap;
    private final int ACCESSES = 0;
    private final int KEY = 1;
    private final int VALUE = 2;
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
        this.lowestUsage = 0;
        this.lowKey = null;
        this.usageMap = new HashMap<>();
        this.dataMap = new HashMap<>();
    }
    @Override
    public boolean set(Key key, Value value) {
        if (key == null || value == null){
            throw new IllegalArgumentException("Args cannot be null");
        }
        boolean isThere = this.dataMap.get(key) != null;
        boolean wereFull = this.size() == this.maxSize;
        if (isThere){
            Object[] arr = this.dataMap.get(key);
            arr[VALUE] = value;
            arr[ACCESSES] = (Integer) arr[ACCESSES] + 1;
            this.updateUsageMap(arr);

        } else {
            if (wereFull){
                Object[] toKish = this.usageMap.get(lowestUsage).remove(lowKey);
                this.dataMap.remove(toKish[KEY]);
            }
            Object[] newKidInTown = {1,key,value};
            this.dataMap.put(key, newKidInTown);
            this.updateUsageMap(newKidInTown);
        }
        return isThere;
    }
    private void updateUsageMap(Object[] arr){
        int accesses = (Integer) arr[ACCESSES];
        boolean alreadyThere = usageMap.get(accesses) != null;
        if(!alreadyThere) {
            this.usageMap.put(accesses, new HashMap<>());
        }
        Key keey = (Key) arr[KEY];
        usageMap.get(accesses).put(keey,arr);
        if((int) accesses == 1){
            this.lowestUsage = 1;
            this.lowKey = keey;

        } else {
        this.usageMap.get(accesses - 1).remove(keey);
        //if its empty ==> (if lowest was that, increase lowest. else, do nothing.) else pick a new key
        if(this.usageMap.get( accesses - 1).isEmpty()){
            if( accesses - 1 == lowestUsage) {
                this.lowestUsage++;
                this.lowKey = keey;

            }
        } else if (lowestUsage ==  accesses - 1){
            for (Key k : usageMap.get( accesses - 1).keySet()) {
                this.lowKey = k;
                break;
            }
        }
        }
    }
    @Override
    public Optional<Value> get(Key key) {
        //make sure abt this one
        if(key == null){
            throw new IllegalArgumentException("key cannot be null");
        }
        Object[] bob = this.dataMap.get(key);
        Optional<Value> result;
        if(bob != null){
            result = Optional.ofNullable((Value)bob[VALUE]);
        } else {
            result = Optional.empty();
        }
        if (result.isPresent()) {
            Object[] arr = this.dataMap.get(key);
            arr[ACCESSES] = (Integer) arr[ACCESSES] + 1;
            this.updateUsageMap(arr);
        }
        return result;

    }
    @Override
    public int size() {
        return dataMap.size();
    }
    @Override
    public boolean isEmpty() {
        return this.size() == 0;
    }
    @Override
    public void clear() {
        this.usageMap = new HashMap<>();
        this.dataMap = new HashMap<>();
        this.lowestUsage = 0;
        this.lowKey = null;
    }
}
