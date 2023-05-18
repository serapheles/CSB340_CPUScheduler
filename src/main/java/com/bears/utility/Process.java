package com.bears.utility;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class Process {
    private String strName;
    private int process_id;

    int priority;
    private List<Integer> io_times;
    private List<Integer> burst_times;
    private int currentIndex;
    private int neededTime;


    public Process(String input){
        if (input == null || input.length() == 0){
            throw new IllegalStateException("input string is empty");
        }
        int idx = input.indexOf('{');
        strName = input.substring(0, idx).replace(" ", "");
        process_id = Integer.parseInt(strName.replaceAll("[^0-9]", ""));
        String[] arr = input.substring(idx + 1).split("[^0-9]");
        burst_times = new ArrayList<>();
        io_times = new ArrayList<>();
        neededTime = 0;

        for (int i = 0; i < arr.length-1; i=i+2){
            if (isParsable(arr[i]) && isParsable(arr[i+1])){
                burst_times.add(Integer.parseInt(arr[i]));
                io_times.add(Integer.parseInt(arr[i+1]));
            }
        }

        burst_times.add(Integer.parseInt(arr[arr.length -1]));
        this.priority = -1;
    }


    public String getStrName() {
        return strName;
    }

    public void setStrName(String strName) {
        this.strName = strName;
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

    public int getBurstTme(){
        return burst_times.get(currentIndex);
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

    public boolean hasEnded(){
        return currentIndex >= io_times.size();
    }



    public int getNeededTime() {
        return neededTime;
    }

    public void setNeededTime(int neededTime) {
        this.neededTime = neededTime;
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
}
