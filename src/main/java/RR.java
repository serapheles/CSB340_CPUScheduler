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

    private LinkedList<Process> completedList;

    private int idleTime = 0;

    private boolean flag = true; //toggle for output;

    private StringBuilder output;
    private String outputFile;


    private int quanta;

    public RR(String filename, String outputFile, boolean outputFlag) {
        readyQueue = new QueueManager(filename);
        currentTime = 0;
        IOQueue = new LinkedList<>();
        idleTime = 0;
        completedList = new LinkedList<>();
        output = new StringBuilder();
        quanta =5;
        this.flag = outputFlag;
        this.outputFile = outputFile;
    }



    public void process(){

        if (readyQueue.isEmpty()){
            throw new IllegalStateException("Queue is empty");
        }

        while (!isCompleted()){
            if (CPUProcess == null){
                if (!readyQueue.isEmpty()){
                    CPUProcess = readyQueue.pop();
                    if (CPUProcess.getInitialCPUTime() == -1) {
                        CPUProcess.setInitialCPUTime(currentTime);
                    }
                    System.out.println(snapshot());
                }

            }else if (CPUProcess.getBurstTme() == 0){
                if (!CPUProcess.isFinished()) {
                    IOQueue.add(CPUProcess);
                    CPUProcess = null;
                    quanta = 0;
                }else{
                    CPUProcess.setExitTime(currentTime);
                    completedList.add(CPUProcess);
                    CPUProcess = null;
                    quanta = 0;
                }
                if (!readyQueue.isEmpty()){
                    CPUProcess = readyQueue.pop();
                    quanta = 5;
                    if (CPUProcess.getInitialCPUTime() == -1) {
                        CPUProcess.setInitialCPUTime(currentTime);
                    }
                    System.out.println(snapshot());
                }
            }else if (quanta <= 0){
                readyQueue.push(CPUProcess);
                CPUProcess = null;
                quanta = 0;
                if (!readyQueue.isEmpty()){
                    CPUProcess = readyQueue.pop();
                    quanta = 5;
                    if (CPUProcess.getInitialCPUTime() == -1) {
                        CPUProcess.setInitialCPUTime(currentTime);
                    }
                }

                System.out.println(snapshot());

            }
            //System.out.println(snapshot());
            processingIOQueue();
            if (CPUProcess != null) {
                CPUProcess.setBurstTme(CPUProcess.getBurstTme() - 1);
                quanta--;
            }else{
                idleTime++;
            }
            currentTime++;
        }

        String str = snapshot();
        System.out.println(str);
        System.out.println(getMetrics());


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

        sb.append("\n---------------------------------------------------------\n");
        for (Process completedProcess : completedList){
            sb.append(completedProcess.getStrName() + "\t");
        }

        sb.append("\n\n" + " ::::::::::::::::::::::::::::::::::::::::::::::::::\n\n");
        outputToFile(outputFile, sb);
        return sb.toString();
    }

    public void outputToFile(String filename, StringBuilder sb) {
        if (!flag) {
            return;
        }
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

    public String getMetrics(){
        StringBuilder sb = new StringBuilder();
        sb.append("\nFinished\n\n");
        sb.append("Total Time:\t" + currentTime + "\n");
        sb.append("CPU Utilization:\t" + ((currentTime - idleTime) / ((double)currentTime)) * 100).append("%") ;

        List<Process> list = new LinkedList<>(completedList);
        list.sort(Comparator.comparingInt(Process::getProcess_id));

        sb.append("\n\nWaiting Time\t\t");

        for (Process p : list){
            sb.append(p.getStrName() + "\t\t");
        }
        sb.append("\n\t\t\t\t\t");
        int totalWait = 0;
        for (Process p1 : list ){
            sb.append(p1.getWaitingTime() + "\t\t");
            totalWait += p1.getWaitingTime();
        }
        sb.append("\nAverage Wait:\t\t" + ((double)totalWait) / list.size());

        sb.append("\n\nTurnaround Time\t\t");
        for (Process p1 : list){
            sb.append(p1.getStrName() + "\t\t");
        }
        sb.append("\n\t\t\t\t\t");
        int totalTurnaround = 0;
        for (Process p : list){
            sb.append(p.getTurnAroundTime() + "\t\t");
            totalTurnaround += p.getTurnAroundTime();
        }

        sb.append("\nAverage Turnaround:\t" + ((double)totalTurnaround) / list.size());

        sb.append("\n\nResponse Time\t\t");
        for (Process p : list){
            sb.append(p.getStrName() + "\t\t");
        }
        sb.append("\n\t\t\t\t\t");
        int totalResponse = 0;
        for (Process p : list){
            sb.append(p.getResponseTime() + "\t\t");
            totalResponse += p.getResponseTime();
        }

        sb.append("\nAverage Response:\t" + ((double)totalResponse) / list.size());

        outputToFile(outputFile, sb);
        return sb.toString();
    }

    public LinkedList<Process> getProcesses(){
        return completedList;
    }

}







