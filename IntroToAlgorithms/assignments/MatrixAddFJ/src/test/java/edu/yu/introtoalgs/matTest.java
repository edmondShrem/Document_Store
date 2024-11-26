package edu.yu.introtoalgs;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
public class matTest {
    @Test
    void correctQuestionMark(){
        double[][] a = {{1,1,1,1,1,1,1,1},
                        {2,2,2,2,2,2,2,2},
                        {3,3,3,3,3,3,3,3},
                        {4,4,4,4,4,4,4,4},
                        {5,5,5,5,5,5,5,5},
                        {6,6,6,6,6,6,6,6},
                        {7,7,7,7,7,7,7,7},
                        {8,8,8,8,8,8,8,8}};
        double[][] b = {{1,1,1,1,1,1,1,1},
                {2,2,2,2,2,2,2,2},
                {3,3,3,3,3,3,3,3},
                {4,4,4,4,4,4,4,4},
                {5,5,5,5,5,5,5,5},
                {6,6,6,6,6,6,6,6},
                {7,7,7,7,7,7,7,7},
                {8,8,8,8,8,8,8,8}};
        MatrixAddFJ m = new MatrixAddFJ(1);
        double[][] c = m.add(a,b);
        double[][] testBoi = {{2,2,2,2,2,2,2,2},
                {4,4,4,4,4,4,4,4},
                {6,6,6,6,6,6,6,6},
                {8,8,8,8,8,8,8,8},
                {10,10,10,10,10,10,10,10},
                {12,12,12,12,12,12,12,12},
                {14,14,14,14,14,14,14,14},
                {16,16,16,16,16,16,16,16}};
        assertArrayEquals(c,testBoi);
    }
    @Test
    void bigCorrect() {
        double[][] d = give(8192);
        MatrixAddFJ m = new MatrixAddFJ(123467);
        MatrixAddFJ n = new MatrixAddFJ(64);
        assertArrayEquals(m.add(d,d), n.add(d,d));
    }
    @Test
    void speeeeed(){
        double[][] a = give(8192);
        MatrixAddFJ serial = new MatrixAddFJ(1342567);
        MatrixAddFJ fork = new MatrixAddFJ(2000);
        long serialsec = 0;
        long forksec = 0;
        long startTime;
        long endTime;
        //double[][] c = new double[8192][8192];
        for(int i = 0; i < 100; i++){
             startTime = System.currentTimeMillis();
            serial.add(a,a);
             endTime = System.currentTimeMillis();
            serialsec += (endTime - startTime);
            startTime = System.currentTimeMillis();
            fork.add(a,a);
            endTime = System.currentTimeMillis();
            forksec += endTime - startTime;
        }
        System.out.println("average serial: " + serialsec/100 + "\n average fork: " + forksec/100);
    }

    private double[][] give(int size){
        double[][] d = new double[size][size];
        for(int i = 0; i < size; i ++){
            for(int j = 0; j < size; j ++){
                d[i][j] = i + j;
            }
        }
        return d;
    }
}
