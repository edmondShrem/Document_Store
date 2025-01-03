package edu.yu.cs.com1320.project.impl;

import edu.yu.cs.com1320.project.MinHeap;

import java.util.NoSuchElementException;

public class MinHeapImpl<E extends Comparable<E>> extends MinHeap<E> {

    public MinHeapImpl(){
        Comparable<?>[] c =(new Comparable[4]);
        this.elements = (E[])(Comparable<E>[])(c);
    }
    @Override
    public void reHeapify(E element) {
        upHeap(getArrayIndex(element));
        downHeap(getArrayIndex(element));
    }
    /**
     *  returns the parent. if it's the root, return null.
     */
    private E getParent(E element){
        if(getArrayIndex(element)  <= 1 /*|| this.elements[(getArrayIndex(element) - 1)/2] == null*/){

            return null;
        }
        return this.elements[(getArrayIndex(element) - 1)/2];
    }
    /**
     *  returns the right child. if it's a leaf, return null.
     */
    private E getRight (E element){
        if(2*getArrayIndex(element) + 2 >= this.elements.length){
            return null;
        }
        return this.elements[2*getArrayIndex(element) + 2];
    }
    /**
     *  returns the left child. if it's a leaf, return null.
     */
    private E getLeft(E element){
        if(2*getArrayIndex(element) + 1 >= this.elements.length){
            return null;
        }
        return this.elements[2*getArrayIndex(element) + 1];
    }
    /**
     *  returns the index of the element. if it's not there, throw a tantrum i mean exception
     */
    @Override
    protected int getArrayIndex(E element) {
        for(int i = 0; i < this.elements.length; i++){
            if(this.elements[i] != null && this.elements[i].equals(element)){
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
        Comparable<?>[] c =(new Comparable[this.elements.length * 2]);
        E[] arr = (E[]) c;
        for(int i = 0; i < elements.length; i ++){
            arr[i] = this.elements[i];
        }
        this.elements = arr;
    }
}
