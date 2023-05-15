package com.bears.utility;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Scanner;

public class QueueManager{
    private List<Process> list = new LinkedList<>();
    private ListIterator<Process> iter = list.listIterator();



    public QueueManager(String fileName){
        try{
            File file  = new File(fileName);
            Scanner scanner = new Scanner(file);
            while (scanner.hasNext()){
                Process process = new Process(scanner.nextLine());
                list.add(process);
            }


        }catch (Exception e){
            System.out.println(e.getMessage());
        }

    }

    public void add(Process process){
        if (iter.hasNext()){
            throw new IllegalStateException();
        }
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




}
