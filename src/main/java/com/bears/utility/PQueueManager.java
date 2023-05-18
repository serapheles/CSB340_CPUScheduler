package com.bears.utility;

import java.io.File;
import java.util.*;

public class PQueueManager {
    private PriorityQueue<Process>  pq = new PriorityQueue<>((p1, p2) -> p1.getPriority() - p2.getPriority());

    private static final int[] priorityArray = new int[]{3, 6, 5, 4, 1, 2, 8, 7};

    public PQueueManager(String fileName){

        try{
            File file  = new File(fileName);
            Scanner scanner = new Scanner(file);
            int i = 0;
            while (scanner.hasNextLine()){
                Process process = new Process(scanner.nextLine());
                process.setPriority(priorityArray[i++]);
                pq.offer(process);
                if (scanner.hasNextLine()){
                    scanner.nextLine();
                }
            }


        }catch (Exception e){
            System.out.println(e.getMessage() + "something went wrong");
        }
    }

    public boolean isEmpty(){
        return pq.isEmpty();
    }

    public void push(Process p){
        pq.offer(p);
    }

    public Process pop(){
        return pq.poll();
    }










}
