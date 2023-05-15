import com.bears.utility.Process;
import com.bears.utility.QueueManager;


import java.util.LinkedList;
import java.util.Queue;

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

        CPUProcess = readyQueue.hasNext() ? readyQueue.next(): null;
        if (CPUProcess == null){
            throw new IllegalStateException("can't even start");
        }
        int burstBegin = 0;
        int duration = CPUProcess.next();
        int burstEnd = burstBegin + duration;
        for (; ; currentTime++){
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
                    burstBegin = currentTime;
                    duration = CPUProcess.next();
                    burstEnd = burstBegin + duration;
                }

            }
        }
    }



    }



