package edu.yu.introtoalgs;

import edu.yu.introtoalgs.DHashMapBase;
import edu.yu.introtoalgs.SizedHashMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

public class DHashMap<Key, Value> extends DHashMapBase<Key, Value>{
    private final int maxServerCapacity;
    private HashMap<Integer, SizedHashMap<Key,Value>> mapMap;
    private ArrayList<Integer> ids;
    private int counter;

    /**
     * Constructor: client specifies the per-server capacity of participating
     * servers (hash maps) in the distributed hash map.  (For simplicity, each
     * server has the same capacity.)  The system must throw an
     * IllegalArgumentException if clients attempt to store more than this amount
     * of data.
     *
     * @param perServerMaxCapacity per server maximum capacity, must be greater
     *                             than 0.
     * @throws IllegalArgumentException as appropriate.
     */
    public DHashMap(int perServerMaxCapacity) {
        super(perServerMaxCapacity);
        if (perServerMaxCapacity < 1){
            throw new IllegalArgumentException("Max must be positive");
        }
        this.maxServerCapacity = perServerMaxCapacity;
        this.mapMap = new HashMap<>();
        this.ids = new ArrayList<>();
        this.counter = 0;
    }

    private void advanceCounter(){
        counter = (counter + 1) % ids.size();
    }

    @Override
    public int getPerServerMaxCapacity() {
        return this.maxServerCapacity;
    }

    @Override
    public void addServer(int id, SizedHashMap<Key, Value> map) {
        if(id < 0){
            throw new IllegalArgumentException("id can't be negative");
        }
        if(map == null){
            throw new IllegalArgumentException("map can't be null");
        }
        if (ids.contains(id)){
            throw new IllegalArgumentException("id is already represented in the DHM");
        }
        this.counter = 0;
        this.mapMap.put(id,map);
        this.ids.add(id);
        this.balanceADD(id);
    }
    //need to look closer at this. the whole "exit b4 u blow urself up thang
    @Override
    public void removeServer(int id) {
        if (id < 0){
            throw new IllegalArgumentException("id can't be negative");
        }
        if (mapMap.get(id) == null){
            throw new IllegalArgumentException("Id is not represented in the Dhasmap");
        }
        int totalStored = this.totalStored();
        if(totalStored / (double)(this.ids.size() - 1) > this.maxServerCapacity){
            throw new IllegalArgumentException("There is not enough room to redistribute the contents of that particular server");
        }
        this.counter = 0;
        this.balanceDELETE(id);
        this.mapMap.remove(id);
    }
    private int totalStored(){
        int total = 0;
        for(int eyeDee : this.ids){
            total += this.mapMap.get(eyeDee).size();
        }
        return total;
    }
    private void balanceADD(int id) {
        Set<Entry<Key, Value>> relocate;
        for (int eyeDee : ids) {
            if (eyeDee == id) {
                continue;
            }
            HashMap<Key, Value> map = mapMap.get(eyeDee);
            relocate = new HashSet<>(map.entrySet());
            int amtToSend = relocate.size()/ ids.size();
            int totalSent = 0;
            for(Entry<Key, Value> e : relocate){
                if(!map.isEmpty() && totalSent < amtToSend){
                    this.mapMap.get(id).put(e.getKey(),e.getValue());
                    map.remove(e.getKey());
                    totalSent++;
                }
            }
        }
    }

    //maybe handle error and leave things the way they are, meaning repopulate the one ur deleting
    //in order to fix it back the way it was before the unsucsessfl call shrug
    private void balanceDELETE(int id){
        Set<Entry<Key,Value>> relocate = mapMap.get(id).entrySet();
        this.ids.remove((Object)id);
        int allowedErrors = this.ids.size();
        int totalErrors = 0;
        for (Entry<Key,Value> e : relocate){
            while(this.mapMap.get(ids.get(counter)).size() == this.maxServerCapacity){
                totalErrors++;
                this.advanceCounter();
                //under normal circumstances we should NEVER get here. this is a failsafe to prevent the code from exploding
                if (totalErrors == allowedErrors){
                    return;
                }
            }
            this.mapMap.get(ids.get(counter)).put(e.getKey(), e.getValue());
            this.advanceCounter();
        }
    }
//maybe add some balance to put and remove?
    @Override
    public Value put(Key key, Value value) {
        if (key == null) {
            throw new IllegalArgumentException("key can't be null");
        }
        if (ids.isEmpty()){
            throw new IllegalStateException("There are no servers. Add some to add data");
        }
        int idIfHere = -1;
        Value result;
        int exceptionCounter = 0;
        for(int eyeDee:ids){
            result = mapMap.get(eyeDee).get(key);
            if (result != null){
                idIfHere = eyeDee;
                return mapMap.get(idIfHere).put(key,value);
            }
        }
        //bro just catch the error, dib
        while (exceptionCounter < mapMap.size()) {
            if (mapMap.get(ids.get(counter)).size() >= maxServerCapacity) {
                exceptionCounter++;
                this.advanceCounter();
            } else {
                result = mapMap.get(ids.get(counter)).put(key, value);
                this.advanceCounter();
                return result;
            }
        }
        if (exceptionCounter == mapMap.size()) {
            throw new IllegalArgumentException("no room");
        }
        return null;
    }
    @Override
    public Value get(Object key) {
        if (key == null) {
            throw new IllegalArgumentException("key can't be null");
        }
        Value result;
         for(int eyeDee:ids){
             result = mapMap.get(eyeDee).get(key);
             if (result != null){
                 return result;
             }
         }
         return null;
    }

    @Override
    public Value remove(Object key) {
        if (key == null) {
            throw new IllegalArgumentException("key can't be null");
        }
        Value result;
        for(int eyeDee:ids){
            result = mapMap.get(eyeDee).remove(key);
            if (result != null){
                return result;
            }
        }
        return null;
    }
}