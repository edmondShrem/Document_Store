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
        MatrixAddFJ m = new MatrixAddFJ(2);
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
    void speeeeed(){
        double[][] a = give(8192);
        MatrixAddFJ m = new MatrixAddFJ(40000);
        //double[][] c = new double[8192][8192];
        long startTime = System.currentTimeMillis();
        m.add(a,a);
        long endTime = System.currentTimeMillis();
        System.out.println(endTime - startTime);
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
