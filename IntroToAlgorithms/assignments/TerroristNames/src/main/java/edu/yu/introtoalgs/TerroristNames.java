package edu.yu.introtoalgs;

import java.util.*;
//try using tree stuff
public class TerroristNames extends TerroristNamesBase{
    //the idea:::hold all of the 2 char sequences, which sends you to a set containing all the words with those in them. then call contains on  a small set of those? saves time potentially???? ur gonna run outta heap spae again -_-
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
        if(id == null){
            throw new IllegalArgumentException("Can't be null");//i think?
        }
        if (members.contains(id)) {
            throw new IllegalArgumentException("already here");
        } else if(id.isEmpty()){
            throw new IllegalArgumentException("id can't be empty");
        } else if(containsWhite(id)){
            throw new IllegalArgumentException("can't contain spaces");
        } else if (id.length() > MAX_ID_LENGTH){
            throw new IllegalArgumentException("id can't be that long");
        }
        this.cutItUp(id);
    }
    private boolean containsWhite(String s){
        boolean isThere = false;
        for(char c:s.toCharArray()){
            if(Character.isWhitespace(c)){
                isThere = true;
            }
        }
        return isThere;
    }
    private void cutItUp(String s){
        HashSet<String> added = new HashSet<>();
        for(int i = 1; i <= s.length(); i++){
            for(int j = 0; j < (s.length() - i) + 1; j++){
                String substring = s.substring(j, j + i);
                if(!added.contains(substring)){
                    //how do i track whats been added more efficienly>
                    added.add(substring);
                    this.membersSub.merge(substring, 1, Integer::sum);
                }
            }
        }
    }
    @Override
    public int search(String id) {
        //whitespace include Tab?
        if(id == null || id.isEmpty() ||containsWhite(id) || id.length() > MAX_ID_LENGTH){
            throw new IllegalArgumentException("ya broke da rules");
        }
        Integer i = membersSub.get(id);
        return Objects.requireNonNullElse(i, 0);
    }
}


