package com.bears.utility;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class QueueManager{
    private LinkedList<Process> list;
    private int size;


    public QueueManager(String fileName){
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
    public Process pop(){
        return list.pollFirst();

    }

    public Process getCurrentProcess(){
        return list.peek();
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
