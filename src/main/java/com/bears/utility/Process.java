package com.bears.utility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

public class Process {
    private String strName;
    private int process_id;

    int priority;
    private List<Integer> io_times;
    private List<Integer> burst_times;
    private int currentIndex;
    private int evictionTime;

    private int readyQueueArrivalTime;


    public Process(String input){
        if (input == null || input.length() == 0){
            throw new IllegalStateException("input string is empty");
        }
        int idx = input.indexOf('{');
        strName = input.substring(0, idx).replace(" ", "");
        process_id = Integer.parseInt(strName.replaceAll("[^0-9]", ""));
        String[] arr = input.substring(idx + 1).split("[^0-9]");
        int[] nums = Arrays.stream(arr).filter(Process::isParsable).mapToInt(Integer::parseInt).toArray();

        burst_times = new ArrayList<>();
        io_times = new ArrayList<>();
        evictionTime = 0;
        readyQueueArrivalTime = 0;

        for (int i = 0; i+1 < nums.length; i=i+2){
                burst_times.add(nums[i]);
                io_times.add(nums[i+1]);
        }

        burst_times.add(nums[nums.length -1]);
        this.priority = -1;
    }


    public String getStrName() {
        return strName;
    }


    public int getProcess_id() {
        return process_id;
    }

    public int getIOTime(){
        if (currentIndex < io_times.size()){
            return io_times.get(currentIndex);
        }

        return -1; //signal end
    }

    public void setIOTme(int ioTime){
        io_times.set(currentIndex, ioTime);
    }

    public int getBurstTme(){
        return burst_times.get(currentIndex);
    }

    public void setBurstTme(int burstTime){
        burst_times.set(currentIndex, burstTime);
    }


    public int getReadyQueueArrivalTime() {
        return readyQueueArrivalTime;
    }

    public void setReadyQueueArrivalTime(int readyQueueArrivalTime) {
        this.readyQueueArrivalTime = readyQueueArrivalTime;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }


    //
    public void advanceIterator(){
        currentIndex++;
    }


    public boolean isFinished(){
        return currentIndex == burst_times.size() -1 && burst_times.get(currentIndex) == 0;
    }


    public boolean isOnLastBurst(){
        return currentIndex == burst_times.size() -1;
    }


    public int getEvictionTime() {
        return evictionTime;
    }

    public void setEvictionTime(int time) {
        this.evictionTime = time;
    }

    public static boolean isParsable(String input) {
        try {
            Integer.parseInt(input);
            return true;
        } catch (final NumberFormatException e) {
            return false;
        }
    }


    @Override
    public String toString() {
        return "\t\t" + strName + "\t" + getBurstTme();
    }

    public boolean equals(Process process){
        return this.priority == process.priority && this.strName.equals(process.strName);
    }

}
