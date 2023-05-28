package com.bears.utility;

import java.io.File;
import java.util.*;

public class PQueueManager {
    private PriorityQueue<Process>  pq = new PriorityQueue<>((p1, p2) -> p1.priority == p2.priority ?
            p1.getReadyQueueArrivalTime()- p2.getReadyQueueArrivalTime() : p1.priority - p2.priority);

    private static final int[] priorityArray = new int[]{3, 6};

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

    public int size(){
        return pq.size();
    }

    public Process peek(){
        return pq.peek();
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        Process[] arr = pq.toArray(new Process[0]);
        Arrays.sort(arr, Comparator.comparingInt(Process::getReadyQueueArrivalTime));
        for (Process p : arr){
            sb.append(p);
            sb.append("\n");
        }

        return sb.toString();
    }







}
