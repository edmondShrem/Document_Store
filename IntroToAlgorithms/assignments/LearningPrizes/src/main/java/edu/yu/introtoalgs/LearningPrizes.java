package edu.yu.introtoalgs;

import java.util.Comparator;
import java.util.Iterator;
import java.util.PriorityQueue;

public class LearningPrizes extends LearningPrizesBase{
    private double pwc;
    private int currentDay;
    private PriorityQueue<double[]> minQueue;
    private PriorityQueue<double[]> maxQueue;
    private final int DAY = 0;
    private  int ID = 1;
    private final int HRS = 2;
    public LearningPrizes(double prizeWeightingConstant) {
        super(prizeWeightingConstant);
        this.pwc = prizeWeightingConstant;
        this.maxQueue = new PriorityQueue<double[]>(new arrCompMax());
        this.minQueue = new PriorityQueue<double[]>(new arrCompMin());
        this.currentDay = 1;
    }

    @Override
    public void addTicket(int day, int childId, double hoursLearned) {

    }

    @Override
    public Iterator<Double> awardedPrizeMoney() {
        return null;
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
