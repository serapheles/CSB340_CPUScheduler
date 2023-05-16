package com.bears.utility;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class Process {
    private String strName;
    private int numName;

    private int IOEndTime;

    private int index;
    private List<Integer> numList;
    private ListIterator<Integer> iter;

    public Process(String input){
        int idx = input.indexOf('{');
        strName = input.substring(0, idx).replace(" ", "");
        numName = Integer.parseInt(strName.replaceAll("[^0-9]", ""));
        String[] arr = input.substring(idx + 1).split("[^0-9]");
        numList = new ArrayList<>();

        for (String num : arr){
            if (isParsable(num)) {
                numList.add(Integer.parseInt(num));
            }
        }

        iter = numList.listIterator();

    }

    public boolean hasNext(){
        return iter.hasNext();
    }

    public int next(){
        return iter.next();
    }

    public boolean isOnCPU(){
        return iter.nextIndex()==0;
    }

    public int getIOEndTime() {
        return IOEndTime;
    }

    public void setIOEndTime(int IOEndTime) {
        this.IOEndTime = IOEndTime;
    }

    public String getStrName() {
        return strName;
    }

    public int getNumName() {
        return numName;
    }

    public int getIndex() {
        return index;
    }

    public List<Integer> getNumList() {
        return numList;
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
        return "Process{" +
                "strName='" + strName + '\'' +
                ", numName=" + numName +
                ", numList=" + numList +
                ", IOTime ends =" + IOEndTime +
                '}';
    }
}
