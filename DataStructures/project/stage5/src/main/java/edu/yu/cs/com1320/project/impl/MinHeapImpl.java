package edu.yu.cs.com1320.project.impl;

import edu.yu.cs.com1320.project.MinHeap;

import java.util.NoSuchElementException;

public class MinHeapImpl extends MinHeap{

    public MinHeapImpl(){
        this.elements = new Comparable[4];
    }
    @Override
    public void reHeapify(Comparable element) {
        if(getParent(element) != null && element.compareTo(getParent(element)) < 0){
            this.upHeap(getArrayIndex(element));
        } else if ((getRight(element) != null && element.compareTo(getRight(element)) > 0) || ( getLeft(element) != null && element.compareTo(getLeft(element)) > 0)){
            this.downHeap(getArrayIndex(element));
        }
    }
    /**
     *  returns the parent. if it's the root, return null.
     */
    private Comparable getParent(Comparable element){
        return this.elements[(getArrayIndex(element) - 1)/2];
    }
    /**
     *  returns the right child. if it's a leaf, return null.
     */
    private Comparable getRight (Comparable element){
        if(2*getArrayIndex(element) + 2 >= this.elements.length){
            return null;
        }
        return this.elements[2*getArrayIndex(element) + 2];
    }
    /**
     *  returns the left child. if it's a leaf, return null.
     */
    private Comparable getLeft(Comparable element){
        if(2*getArrayIndex(element) + 1 >= this.elements.length){
            return null;
        }
        return this.elements[2*getArrayIndex(element) + 1];
    }
    /**
     *  returns the index of the element. if it's not there, throw a tantrum i mean exception
     */
    @Override
    protected int getArrayIndex(Comparable element) {
        for(int i = 1; i < this.elements.length; i++){
            if(this.elements[i].equals(element)){
                return i;
            }
        }
        throw new NoSuchElementException("no such element exists in the heap");
        //correct?
    }
    /**
     *  does what it says on the tin.
     */
    @Override
    protected void doubleArraySize() {
        Comparable[] arr = new Comparable[this.elements.length * 2];
        for(int i = 0; i < elements.length; i ++){
            arr[i] = this.elements[i];
        }
        this.elements = arr;
    }
}
