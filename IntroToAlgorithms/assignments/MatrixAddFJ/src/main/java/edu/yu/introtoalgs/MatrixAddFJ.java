package edu.yu.introtoalgs;

import java.util.Arrays;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

public class MatrixAddFJ extends MatrixAddFJBase {
    private final int threshold;
    static ForkJoinPool pool= new ForkJoinPool(Runtime.getRuntime().availableProcessors());


    public MatrixAddFJ(int addThreshold) {
        super(addThreshold);
        if (addThreshold <= 0) {
            throw new IllegalArgumentException("Threshold must be greater than 0 silly!");
        }
        this.threshold = addThreshold;

    }

    @Override
    public double[][] add(double[][] a, double[][] b) {
        int n = a.length;
        double[][] result = new double[n][n];

        if (n <= threshold) {
            serialCompute(a, b, result, n, n);
        } else {

            pool.invoke(new MatrixAddTask(a, b, result, 0, 0, n, n, threshold));
        }

        return result;
    }

    private void serialCompute(double[][] a, double[][] b, double[][] result, int rowEnd, int colEnd) {
        for (int i = 0; i < rowEnd; i++) {
            for (int j = 0; j < colEnd; j++) {
                result[i][j] = a[i][j] + b[i][j];
            }
        }
    }
//copy one of the arrays and add to it; few acceses?
    private static class MatrixAddTask extends RecursiveAction {
        private final double[][] a;
        private final double[][] b;
        private final double[][] result;
        private final int rowStart, colStart, rowEnd, colEnd;
        private final int threshold;

        MatrixAddTask(double[][] a, double[][] b, double[][] result, int rowStart, int colStart, int rowEnd, int colEnd, int threshold) {
            this.a = a;
            this.b = b;
            this.result = result;
            this.rowStart = rowStart;
            this.colStart = colStart;
            this.rowEnd = rowEnd;
            this.colEnd = colEnd;
            this.threshold = threshold;
        }

        @Override
        protected void compute() {
            int numRows = rowEnd - rowStart;
            int numCols = colEnd - colStart;
            double[] resultI;
            double[] aI;
            double[] bI;
            if (numRows <= threshold && numCols <= threshold) {
                for (int i = rowStart; i < rowEnd; i++) {
                     resultI = result[i];
                     aI = a[i];
                     bI = b[i];
                    for (int j = colStart; j < colEnd; j++) {
                        resultI[j] = aI[j] + bI[j];
                    }
                }
            } else {
                int rowMid = rowStart + numRows / 2;
                int colMid = colStart + numCols / 2;

                invokeAll(
                        new MatrixAddTask(a, b, result, rowStart, colStart, rowMid, colMid, threshold), // Top-left
                        new MatrixAddTask(a, b, result, rowStart, colMid, rowMid, colEnd, threshold),  // Top-right
                        new MatrixAddTask(a, b, result, rowMid, colStart, rowEnd, colMid, threshold),  // Bottom-left
                        new MatrixAddTask(a, b, result, rowMid, colMid, rowEnd, colEnd, threshold)    // Bottom-right
                );
            }
        }
    }
}
