package com.mak.classportal.utilities;

public class ScreenStack {
    static final int MAX = 4;
    int top;
    String[] a = new String[MAX]; // Maximum size of Stack

    public ScreenStack() {
        top = -1;
    }

    public boolean isEmpty() {
        return (top < 0);
    }

    public boolean push(String x) {
        if (top >= (MAX - 1)) {
            System.out.println("Stack Overflow");
            return false;
        } else {
            a[++top] = x;
            System.out.println(x + " pushed into stack");
            return true;
        }
    }

    public String pop() {
        if (top < 0) {
            System.out.println("Stack Underflow");
            return null;
        } else {
            String x = a[top--];
            return x;
        }
    }

    public String peek() {
        if (top < 0) {
            System.out.println("Stack Underflow");
            return null;
        } else {
            return a[top];
        }
    }
}

