package com.bears.utility;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Scanner;

public class QueueManager{
    private List<Process> list = new LinkedList<>();
    private ListIterator<Process> iter;



    public QueueManager(String fileName){
        try{
            File file  = new File(fileName);
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()){
                Process process = new Process(scanner.nextLine());
                if (scanner.hasNextLine()){
                    scanner.nextLine();
                }
                list.add(process);
            }


        }catch (Exception e){
            System.out.println(e.getMessage() + "something went wrong");
        }
        iter = list.listIterator();
    }

    public void add(Process process){

        list.add(process);
        iter = list.listIterator();

    }

    public void remove(){
        iter.remove();
    }

    public Process next(){
        return iter.next();
    }

    public boolean hasNext(){
        return iter.hasNext();
    }

    @Override
    public String toString() {
        return "QueueManager{" +
                "list=" + list +
                '}';
    }

    public int getSize(){
        return list.size();
    }

}
