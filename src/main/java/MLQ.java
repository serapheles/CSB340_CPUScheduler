import com.bears.utility.PQueueManager;
import com.bears.utility.Process;


import java.util.*;


public class MLQ {
    private PQueueManager readyQueue;
    private int currentTime;

    private Queue<Process> IOQueue;

    private Process CPUProcess;

    public MLQ () {
        readyQueue = new PQueueManager("src/main/resources/input", new int[]{1, 1, 1, 1, 2, 2, 2, 2});
        currentTime = 0;
        IOQueue = new LinkedList<>();
        CPUProcess = null;
    }


    public void process(){
        if (readyQueue.size() == 0){
            throw new IllegalStateException("Queue is empty");
        }

        while (!isCompleted()){
            if (CPUProcess == null){
                if (!readyQueue.isEmpty()){
                    CPUProcess = readyQueue.pop();
                }

            }else if (CPUProcess.getBurstTme() == 0){
                if (!CPUProcess.isFinished()) {
                    IOQueue.add(CPUProcess);
                    CPUProcess = null;
                }else{
                    CPUProcess = null;
                }
                if (!readyQueue.isEmpty()){
                    CPUProcess = readyQueue.pop();
                }
            }else if (!readyQueue.isEmpty() && readyQueue.peek().getPriority() < CPUProcess.getPriority()){
                Process newProcess = readyQueue.pop();
                readyQueue.push(CPUProcess);
                CPUProcess = newProcess;
            }
            System.out.println(snapshot());
            processingIOQueue();
            if (CPUProcess != null) {
                CPUProcess.setBurstTme(CPUProcess.getBurstTme() - 1);
            }

            currentTime++;
        }

    }

    public boolean isCompleted(){
        return readyQueue.isEmpty() && IOQueue.isEmpty() && CPUProcess == null;
    }

    public void processingIOQueue(){
        Iterator<Process> iterator = IOQueue.iterator();
        while(iterator.hasNext()){
            Process process = iterator.next();
            process.setIOTme(process.getIOTime() -1);
            if (process.getIOTime() <= 0){
                process.advanceIterator();
                process.setReadyQueueArrivalTime(currentTime++);
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
            sb.append("\nNext Process on CPU: ").append(CPUProcess.getStrName() + "\t" + CPUProcess.getBurstTme() +"\n");
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
                sb.append(ioProcess.getStrName() + "\t" + ioProcess.getIOTime() + "\n");
            }
        }

        sb.append("\n" + " ::::::::::::::::::::::::::::::::::::::::::::::::::\n\n");

        return sb.toString();
    }




}
