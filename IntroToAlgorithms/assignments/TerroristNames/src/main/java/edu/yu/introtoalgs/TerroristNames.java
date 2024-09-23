package edu.yu.introtoalgs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;

public class TerroristNames extends TerroristNamesBase{

    public final static int MAX_ID_LENGTH = 9;
    private HashSet<String> members;
    private HashMap<String, Integer> membersSub;

    public TerroristNames () {
        super();
        members = new HashSet<>();
        membersSub = new HashMap<>();
    }
    @Override
    public void add(String id) {
        if (members.contains(id)) {
            throw new IllegalArgumentException("already here");
        } else if(id.isEmpty()){
            throw new IllegalArgumentException("id can't be empty");
        } else if(id.contains(" ")){
            throw new IllegalArgumentException("can't contain spaces");
        } else if (id.length() > MAX_ID_LENGTH){
            throw new IllegalArgumentException("id can't be that long");
        }
        this.cutItUp(id);
        members.add(id);
    }
    private void cutItUp(String s){
        HashSet<String> added = new HashSet<>();
        for(int i = 1; i < s.length(); i++){
            for(int j = 0; j < (s.length() - i) + 1; j++){
                String substring = s.substring(j, j + i);
                if(!added.contains(substring)){
                    added.add(substring);
                    this.membersSub.merge(substring, 1, Integer::sum);
                }
            }
        }
    }
    @Override
    public int search(String id) {
        //whitespace include Tab?
        if(id.isEmpty() || id.contains(" ") || id.length() > MAX_ID_LENGTH){
            throw new IllegalArgumentException("ya broke the rules");
        }
        Integer i = membersSub.get(id);
        if(members.contains(id)){
            return 1;
        }
        return Objects.requireNonNullElse(i, 0);
    }
}
