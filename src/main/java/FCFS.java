import com.bears.utility.Process;
import com.bears.utility.QueueManager;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class FCFS {
    private QueueManager readyQueue;
    private int currentTime;

    private Queue<Process> IOQueue;

    private Process CPUProcess;

    private LinkedList<Process> completedList;

    private int idleTime = 0;

    private int CPUTime = 0;

    private boolean flag; //toggle for output;

    private StringBuilder output;

    private String outputFile;

    public FCFS(String filename, String outputFile, boolean outputFlag) {
        readyQueue = new QueueManager(filename);
        currentTime = 0;
        IOQueue = new LinkedList<>();
        idleTime = 0;
        completedList = new LinkedList<>();
        output = new StringBuilder();
        this.outputFile = outputFile;
        flag = outputFlag;
        CPUProcess = null;
    }


    public void process(){
        if (readyQueue.getSize() == 0){
            throw new IllegalStateException("Queue is empty");
        }
        //  CPUProcess = readyQueue.pop();
        //  CPUProcess.setEvictionTime(currentTime + CPUProcess.getBurstTme());
        //   System.out.printf(snapshot());
        while (!isCompleted()){
            //    System.out.println(snapshot());

            if (CPUProcess != null && currentTime >= CPUProcess.getEvictionTime()){
                if (!CPUProcess.isOnLastBurst()) {
                    int IO = CPUProcess.getIOTime();
                    CPUProcess.setEvictionTime(IO + currentTime);
                    CPUProcess.advanceIterator();
                    IOQueue.add(CPUProcess);
                }else{
                    CPUProcess.setExitTime(currentTime);
                    completedList.add(CPUProcess);
                }



                CPUProcess = null;
            }
            processingIOQueue();

            if (!readyQueue.isEmpty() && CPUProcess == null) {
                CPUProcess = readyQueue.pop();
                if (CPUProcess.getInitialCPUTime() == -1){
                    CPUProcess.setInitialCPUTime(currentTime);
                }
                CPUProcess.setEvictionTime(currentTime + CPUProcess.getBurstTme());

                if (flag) {
                    String str = snapshot();
                    System.out.println(str);
                    output.append(str);
                }
            }

            if (CPUProcess == null){
                idleTime++;
            }

            currentTime++;
        }

        currentTime--;
        if (flag) {

            String str = snapshot();
            System.out.println(str);
            output.append(str);
            System.out.println(getMetrics());
        }


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
        sb.append("\n---------------------------------------------------------\n");
        for (Process completedProcess : completedList){
            sb.append(completedProcess.getStrName() + "\t");
        }

        sb.append("\n\n" + " ::::::::::::::::::::::::::::::::::::::::::::::::::\n\n");
        outputToFile("FCFS_output", sb);
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



