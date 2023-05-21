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

    }



    public void process(){
        if (readyQueue.getSize() == 0){
            throw new IllegalStateException("Queue is empty");
        }
        CPUProcess = readyQueue.pop();
        CPUProcess.setEvictionTime(currentTime + CPUProcess.getBurstTme());
     //   System.out.printf(snapshot());
        while (!isCompleted()){
        //    System.out.println(snapshot());
            if (CPUProcess != null && currentTime >= CPUProcess.getEvictionTime()){
                if (!CPUProcess.isOnLastBurst()) {
                    int IO = CPUProcess.getIOTime();
                    CPUProcess.setEvictionTime(IO + currentTime);
                    CPUProcess.advanceIterator();
                    IOQueue.add(CPUProcess);
                }
                CPUProcess = null;
            }
            processingIOQueue();
            if (!readyQueue.isEmpty() && CPUProcess == null) {
                CPUProcess = readyQueue.pop();
                CPUProcess.setEvictionTime(currentTime + CPUProcess.getBurstTme());
            }
            System.out.println(snapshot());
            currentTime++;
        }
     //   System.out.println(snapshot());
    }

    public boolean isCompleted(){
        return readyQueue.isEmpty() && IOQueue.isEmpty() && CPUProcess == null;
    }

    public void processingIOQueue(){
        Iterator<Process> iterator = IOQueue.iterator();
        while(iterator.hasNext()){
            Process process = iterator.next();
            if (process.getEvictionTime() <= currentTime){
                readyQueue.push(process);
                iterator.remove();
            }
        }
    }

    public String snapshot(){
        StringBuilder sb = new StringBuilder();
        sb.append("Current Time: " + currentTime);
        if (CPUProcess == null){
            sb.append("\nNext Process on CPU: " + "NA" + "\n");
        }else {
            sb.append("\nNext Process on CPU: ").append(CPUProcess.getStrName()).append(String.format(" (%d) ", CPUProcess.getEvictionTime() -currentTime)).append("\n");
        }
        sb.append("---------------------------------------------------------\n");
        sb.append("List of processes in the ready queue: \n");
        sb.append("\t\tProcess\tBurst\n");
        sb.append(readyQueue);
        sb.append("\n---------------------------------------------------------\n");
        sb.append("List of processes in I/O:");
        sb.append("\t\tProcess\tRemaining I/O time\n");
        if (IOQueue.isEmpty()){
            sb.append("\t\t[empty]\n");
        }else{
            for (Process ioProcess : IOQueue){
                sb.append(ioProcess.getStrName() + "\t" + String.valueOf(ioProcess.getEvictionTime() - currentTime) + "\n");
            }
        }

        sb.append("\n" + " ::::::::::::::::::::::::::::::::::::::::::::::::::\n\n");

        return sb.toString();
    }



    }



