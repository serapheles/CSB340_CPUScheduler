package com.bears.utility;

public class Process {
    private int burstTime;
    private int responseTime;
    private int waitingTime;
    private int turnaroundTime;

    public Process(int burstTime){
        this.burstTime = burstTime;
    }

    public int getBurstTime() {
        return burstTime;
    }

    public int getResponseTime() {
        return responseTime;
    }

    public int getWaitingTime() {
        return waitingTime;
    }

    public int getTurnaroundTime() {
        return turnaroundTime;
    }

    public void decreaseBurstTime(int num){
        burstTime = burstTime - num;
    }

    @Override
    public String toString() {
        return "Process{" +
                "burstTime=" + burstTime +
                ", responseTime=" + responseTime +
                ", waitingTime=" + waitingTime +
                ", turnaroundTime=" + turnaroundTime +
                '}';
    }
}
