package edu.yu.introtoalgs;

import java.util.*;

public class LearningPrizes extends LearningPrizesBase{
    private double pwc;
    private boolean afterAward;
    private int currentDay;
    private PriorityQueue<double[]> minQueue;
    private PriorityQueue<double[]> maxQueue;
    private HashMap<Integer, Set<Integer>> daysToTicket;
    private final int DAY = 0;
    private  int ID = 1;
    private final int HRS = 2;
    int totalTickets;
    private ArrayList<Double> prizes;
    public LearningPrizes(double prizeWeightingConstant) {
        super(prizeWeightingConstant);
        this.pwc = prizeWeightingConstant;
        this.minQueue = new PriorityQueue<>(new arrCompMax());
        this.maxQueue = new PriorityQueue<>(new arrCompMin());
        this.currentDay = 0;
        this.daysToTicket = new HashMap<>();
        this.prizes = new ArrayList<>();
        this.afterAward = false;
        this.totalTickets = 0;
    }

    @Override
    public void addTicket(int day, int childId, double hoursLearned) {
        if(day < this.currentDay){
            throw new IllegalArgumentException("that day has passed you silly goose.");
        } else if (childId < 0){
            throw new IllegalArgumentException("Child ID cannot be negative, silly.");
        } else if (hoursLearned <= 0){
            throw new IllegalArgumentException("Gotta work on that hasmada, can't not learn");
        }
        //advance day
        if(!this.daysToTicket.containsKey(day)){
            //this mean we are on a new day, check to see if we can get some new munee (unclear abt zero/1 ticket for a spec day
            if(this.totalTickets >= 2){
                //get the prize
                this.prizes.add(this.getNewPrize());
            }
            this.currentDay = day;
            this.daysToTicket.put(this.currentDay, new HashSet<>());
        }
        //need to do some exception null stuff
        if(this.daysToTicket.get(day).contains(childId)){
            throw new IllegalArgumentException("Only one ticket per child per day");
        }
        this.daysToTicket.get(day).add(childId);
        this.maxQueue.add(new double[]{day, childId, hoursLearned});
        this.minQueue.add(new double[]{day, childId, hoursLearned});
        this.totalTickets++;
    }

    private Double getNewPrize() {
        double[] MAX;
        double[] min;
        double munee;
        MAX = this.maxQueue.remove();
        min = this.minQueue.remove();
        munee = this.pwc * (MAX[HRS] - min[HRS]);
        this.daysToTicket.get((int)MAX[DAY]).remove((int)MAX[ID]);
        this.daysToTicket.get((int)min[DAY]).remove((int)min[ID]);
        this.minQueue.remove(MAX);
        this.maxQueue.remove(min);
        this.totalTickets-=2;
        return munee;
    }

    //this is just wrong; supposed to be each day individually. gotta do it when i add the tickets. **ig whenever the day changes**
//probably kedai to make a "day change" method, which calcs the munee
    @Override
    public Iterator<Double> awardedPrizeMoney() {
        if(this.totalTickets >= 2){
            prizes.add(getNewPrize());
        }//prepare for next time, iterator will need that many
        Iterator<Double> prizeIterator = new Iterator<>() {
            private ArrayList<Double> munz = prizes;
            private int pointer = 0;
            @Override
            public boolean hasNext() {
                return !(pointer == munz.size());
            }

            @Override
            public Double next() {
                if(!hasNext()){
                    throw new NoSuchElementException("The iterator is Empty");
                }
                pointer++;
                return munz.get(pointer-1);
            }
        };
        this.currentDay++;
        return prizeIterator;
    }




    private class arrCompMax implements Comparator<double[]> {

        @Override
        public int compare(double[] o1, double[] o2) {
            return Double.compare(o1[HRS], o2[HRS]);
        }
    }
    private class arrCompMin implements Comparator<double[]> {

        @Override
        public int compare(double[] o1, double[] o2) {
            return -Double.compare(o1[HRS], o2[HRS]);
        }
    }
}
