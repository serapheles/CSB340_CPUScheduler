package com.bears.utility;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class QueueManager{
    private List<Process> list = new LinkedList<>();
    private int current;
    private int size;


    public QueueManager(String fileName){
        current = 0;
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

    public void add(Process process){
        list.add(process);
    }

    //remove current process and current points at next process
    public void remove(){
        list.remove(current);
        size--;
    }

    public Process getCurrentProcess(){
        if (current >= size){
            return null;
        }

        return list.get(current);
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
