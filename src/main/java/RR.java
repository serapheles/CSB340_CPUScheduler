import com.bears.utility.Process;
import com.bears.utility.QueueManager;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class RR {
    private QueueManager readyQueue;
    private int currentTime;

    private Queue<Process> IOQueue;

    private Process CPUProcess;

    private List<Process> completedList;

    private int idleTime = 0;

    private boolean flag = true; //toggle for output;

    private StringBuilder output;

    private int quanta;

    public RR() {
        readyQueue = new QueueManager("src/main/resources/input");
        currentTime = 0;
        IOQueue = new LinkedList<>();
        idleTime = 0;
        completedList = new LinkedList<>();
        output = new StringBuilder();
        quanta =5;
    }



    public void process(){

        if (readyQueue.isEmpty()){
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
                    quanta = 0;
                }else{
                    CPUProcess = null;
                    quanta = 0;
                }
                if (!readyQueue.isEmpty()){
                    CPUProcess = readyQueue.pop();
                    quanta = 5;
                }
            }else if (quanta <= 0){
                readyQueue.push(CPUProcess);
                CPUProcess = null;
                quanta = 0;
                if (!readyQueue.isEmpty()){
                    CPUProcess = readyQueue.pop();
                    quanta = 5;
                }
            }
            System.out.println(snapshot());
            processingIOQueue();
            if (CPUProcess != null) {
                CPUProcess.setBurstTme(CPUProcess.getBurstTme() - 1);
                quanta--;
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
            sb.append("\nNext Process on CPU: ").append(CPUProcess.getStrName() + "\t(" + CPUProcess.getBurstTme() + ")\tquanta: " +quanta +"\n");
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
                sb.append("\t\t" + ioProcess.getStrName() + "\t" + ioProcess.getIOTime() + "\n");
            }
        }

        sb.append("\n" + " ::::::::::::::::::::::::::::::::::::::::::::::::::\n\n");

        return sb.toString();
    }

    public void outputToFile(String filename, StringBuilder sb) {
        FileWriter fileWriter = null;
        try {
            File file = new File(filename);
            if (!file.exists()){
                file.createNewFile();
            }
            fileWriter = new FileWriter(filename, true);
            fileWriter.write(sb.toString());
            fileWriter.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }


    }



}



