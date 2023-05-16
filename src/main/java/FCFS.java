import com.bears.utility.Process;
import com.bears.utility.QueueManager;


import java.util.*;

public class FCFS {
    private QueueManager readyQueue;
    private int currentTime;

    private Queue<Process> IOQueue;

    private Process CPUProcess;

    public FCFS() {
        readyQueue = new QueueManager("src/main/resources/input");
        currentTime = 0;
        IOQueue = new LinkedList<>();
        getProcessOnCPU();

    }

    public Process getProcessOnCPU(){
        CPUProcess = readyQueue.getCurrentProcess();
        readyQueue.remove();
        return CPUProcess;
    }

    public void process(){
        int burstBegin = 0;
        int duration = CPUProcess.next();
        int burstEnd = burstBegin + duration;
        System.out.println(snapshot());
        for (;currentTime < 300 && !isCompleted() ; currentTime++){

            if (currentTime == burstEnd) {
                if (CPUProcess.hasNext()){
                    CPUProcess.setIOEndTime(currentTime + CPUProcess.next());
                    IOQueue.add(CPUProcess);
                    readyQueue.remove();
                }else{
                    readyQueue.remove();
                }




            }
        }
    }

    public boolean isCompleted(){
        return IOQueue.size() == 0 && readyQueue.getSize() == 0;
    }

    public void processingIOQueue(){

            Iterator<Process> iter = IOQueue.iterator();
            while (iter.hasNext()){
                Process p = iter.next();
                int endTime = p.getIOEndTime();
                if (currentTime >= endTime){
                    iter.remove();
                    readyQueue.add(p);
                    System.out.println("added" + p + "back to the quee");
                }
            }


    }

    public String snapshot(){
        StringBuilder sb = new StringBuilder();
        sb.append("Current Time: " + currentTime);
        sb.append("\nNext Process on CPU: " + getProcessOnCPU().getStrName() + "\n");
        sb.append("---------------------------------------------------------\n");
        sb.append("List of processes in the ready queue: \n");
        sb.append("\t\tProcess\tBurst\n");
        sb.append(readyQueue);
        return sb.toString();
    }



    }



