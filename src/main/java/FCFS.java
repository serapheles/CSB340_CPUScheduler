import com.bears.utility.Process;
import com.bears.utility.QueueManager;


import java.util.*;

public class FCFS {
    private QueueManager readyQueue;
    private int currentTime;

    private Queue<Process> IOQueue;

    private Process CPUProcess = null;

    public FCFS() {
        readyQueue = new QueueManager("src/main/resources/input");
        currentTime = 0;
        IOQueue = new LinkedList<>();

    }

    public Process getProcessOnCPU(){
        return CPUProcess;
    }

    public void process(){
        System.out.println("ready queue" + readyQueue);
        CPUProcess = readyQueue.hasNext() ? readyQueue.next(): null;
        System.out.println(CPUProcess);
        if (CPUProcess == null){
            throw new IllegalStateException("can't even start");
        }
        int burstBegin = 0;
        int duration = CPUProcess.next();
        int burstEnd = burstBegin + duration;
        for (;currentTime < 300 && !isCompleted() ; currentTime++){
            System.out.println(currentTime);
            if (currentTime == burstEnd) {

                if (CPUProcess.hasNext()){
                    CPUProcess.setIOEndTime(currentTime + CPUProcess.next());
                    IOQueue.add(CPUProcess);
                    readyQueue.remove();
                }else{
                    readyQueue.remove();
                }

                if (readyQueue.hasNext()){
                    CPUProcess = readyQueue.next();
                    System.out.println(CPUProcess);
                    burstBegin = currentTime;
                    duration = CPUProcess.next();
                    burstEnd = burstBegin + duration;
                }else{
                    processingIOQueue();
                }

            }
        }
    }

    public boolean isCompleted(){
        return IOQueue.size() == 0 && readyQueue.getSize() == 0;
    }

    public boolean processingIOQueue(){
        if (readyQueue.hasNext() == false){
            Iterator<Process> iter = IOQueue.iterator();
            List<Process> tempList = new ArrayList<>();
            while (iter.hasNext()){
                Process p = iter.next();
                int endTime = p.getIOEndTime();
                if (currentTime >= endTime){
                    iter.remove();
                    tempList.add(p);
                    System.out.println("added back to readyQUeue");
                }
            }

            tempList.sort(Comparator.comparingInt(Process::getIOEndTime));

            for (Process p : tempList){
                readyQueue.add(p);
            }

        }
        return false;
    }

    public String snapshot(){
        StringBuilder sb = new StringBuilder();
        sb.append("Current Time: " + currentTime);
        sb.append("\nNext Process on CPU: " + getProcessOnCPU() + "\n");
        sb.append("---------------------------------------------------------\n");
        sb.append("List of processes in the ready queue: \n");
        sb.append("\t\tProcess\tBurst\n");


        return sb.toString();
    }



    }



