package edu.yu.introtoalgs;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

public class MatrixAddFJ extends MatrixAddFJBase {
    private final int threshold;

    public MatrixAddFJ(int addThreshold) {
        super(addThreshold);
        if (addThreshold <= 0) {
            throw new IllegalArgumentException("Threshold must be greater than 0!");
        }
        this.threshold = addThreshold;
    }

    @Override
    public double[][] add(double[][] a, double[][] b) {
        if (a.length != b.length || a[0].length != b[0].length) {
            throw new IllegalArgumentException("Matrices must have the same dimensions.");
        }

        int n = a.length;
        double[][] result = new double[n][n];

        if (n <= threshold) {
            // Use serial computation for small matrices
            serialCompute(a, b, result, 0, 0, n, n);
        } else {
            ForkJoinPool pool = ForkJoinPool.commonPool();
            pool.invoke(new MatrixAddTask(a, b, result, 0, 0, n, n, threshold));
        }

        return result;
    }

    private void serialCompute(double[][] a, double[][] b, double[][] result, int rowStart, int colStart, int rowEnd, int colEnd) {
        for (int i = rowStart; i < rowEnd; i++) {
            for (int j = colStart; j < colEnd; j++) {
                result[i][j] = a[i][j] + b[i][j];
            }
        }
    }

    private class MatrixAddTask extends RecursiveAction {
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

            if (numRows <= threshold && numCols <= threshold) {
                // Perform serial computation
                serialCompute(a, b, result, rowStart, colStart, rowEnd, colEnd);
            } else {
                // Split into quadrants
                int rowMid = rowStart + numRows / 2;
                int colMid = colStart + numCols / 2;

                invokeAll(
                        new MatrixAddTask(a, b, result, rowStart, colStart, rowMid, colMid, threshold), // Top-left
                        new MatrixAddTask(a, b, result, rowStart, colMid, rowMid, colEnd, threshold),   // Top-right
                        new MatrixAddTask(a, b, result, rowMid, colStart, rowEnd, colMid, threshold),  // Bottom-left
                        new MatrixAddTask(a, b, result, rowMid, colMid, rowEnd, colEnd, threshold)     // Bottom-right
                );
            }
        }
    }
}
