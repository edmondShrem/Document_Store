package edu.yu.introtoalgs;

public class MatrixAddFJ extends MatrixAddFJBase{
    /**
     * Constructor: client specifies the threshold value "n" (in a "n by n"
     * matrix) at which a Fork-Join implementation of add should switch over to a
     * serial implementation.
     *
     * @param addThreshold specifies that matrix addition for "n" greater than or
     *                     equal to the threshold must be processed using a serial algorithm rather
     *                     than via FJ decomposition, must be greater than 0.
     */
    public MatrixAddFJ(int addThreshold) {
        super(addThreshold);
    }

    @Override
    public double[][] add(double[][] a, double[][] b) {
        return new double[0][];
    }
}
