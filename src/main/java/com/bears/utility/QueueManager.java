package com.bears.utility;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class QueueManager{
    private List<Process> list;
    private int currentIndex;
    private int size;


    public QueueManager(String fileName){
        currentIndex = 0;
        list = new LinkedList<>();
        size = 0;
        try{
            File file  = new File(fileName);
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()){
                Process process = new Process(scanner.nextLine());

                if (scanner.hasNextLine()){
                    scanner.nextLine();
                }
                list.add(process);
                size++;
            }


        }catch (Exception e){
            System.out.println(e.getMessage() + "something went wrong");
        }
    }

    public void push(Process process){
        list.add(process);
    }

    //remove current process and current points at next process
    public void pop(){
        list.remove(currentIndex);
        size--;
    }

    public Process getCurrentProcess(){
        if (currentIndex >= size){
            return null;
        }

        return list.get(currentIndex);
    }

    public boolean isEmpty(){
        return list.isEmpty();
    }



    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Process p : list){
            sb.append(p);
            sb.append("\n");

        }

        return sb.toString();
    }

    public int getSize(){
        return list.size();
    }



}
