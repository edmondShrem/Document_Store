package edu.yu.cs.com1320.project.impl;

import edu.yu.cs.com1320.project.Stack;
public class StackImpl<T> implements Stack<T>{
    private T[] stack;
    public StackImpl(){
       stack = (T[])(new Object[2]);
    }

    @Override
    public void push(T element) {
        if(this.isFull()){
            this.arrayDouble();
        }
        stack[this.size()] = element;
    }

    @Override
    public T pop() {
        T t = stack[this.size() - 1];
        stack[this.size() - 1] = null;
        return t;
    }

    @Override
    public T peek() {
        return stack[this.size() - 1];
    }

    @Override
    public int size() {
        if(this.isFull()){
            return stack.length;
        }
        for(int i = 0; i < stack.length; i ++){
            if (stack[i] == null){
                return i;
            }
        }
        return 0;
    }
    private boolean isFull(){
        return stack[stack.length - 1] != null;
    }
    private void arrayDouble(){
        T[] steve = (T[])(new Object[stack.length * 2]);
        for(int i = 0; i < stack.length; i ++){
            steve[i] = stack[i];
        }
        stack = steve;
    }
}
