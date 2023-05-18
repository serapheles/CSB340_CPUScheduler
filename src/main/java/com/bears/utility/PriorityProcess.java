package com.bears.utility;

public class PriorityProcess extends Process {
    private int priority;
    public PriorityProcess(String input) {
        super(input);
        priority = Integer.MAX_VALUE;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
}
