import com.bears.utility.PQueueManager;
import com.bears.utility.Process;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;


public class Priority {
    private PQueueManager readyQueue;
    private int currentTime;

    private Queue<Process> IOQueue;

    private Process CPUProcess;
    private boolean flag;
    private String outputFile;
    private int idleTime;
    private LinkedList<Process> completedList;


    /**+
     * Constructor for Priority
     * @param filename file to read from
     * @param outputFile file to write to
     * @param outputFlag  flag to toggle output
     * @param priorities array of priorities
     */
    public Priority (String filename, String outputFile, boolean outputFlag, int[] priorities) {
        readyQueue = new PQueueManager(filename, priorities);
        this.outputFile = outputFile;
        flag = outputFlag;

        currentTime = 0;
        IOQueue = new LinkedList<>();
        CPUProcess = null;
        idleTime = 0;
        completedList = new LinkedList<>();
    }


    /**
     * Checks if all processes are completed
     *  true if all processes are completed
     */
    public void process(){
        if (readyQueue.size() == 0){
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
                }else{
                    CPUProcess.setExitTime(currentTime);
                    completedList.add(CPUProcess);
                    CPUProcess = null;
                }
                if (!readyQueue.isEmpty()){
                    CPUProcess = readyQueue.pop();
                    if (CPUProcess.getInitialCPUTime() == -1) {
                        CPUProcess.setInitialCPUTime(currentTime);
                    }
                    System.out.println(snapshot());

                }
            }else if (!readyQueue.isEmpty() && readyQueue.peek().getPriority() < CPUProcess.getPriority()){
                Process newProcess = readyQueue.pop();
                readyQueue.push(CPUProcess);
                CPUProcess = newProcess;
                if (CPUProcess.getInitialCPUTime() == -1) {
                    CPUProcess.setInitialCPUTime(currentTime);
                }
                System.out.println(snapshot());
            }
            //System.out.println(snapshot());
            processingIOQueue();
            if (CPUProcess != null) {
                CPUProcess.setBurstTme(CPUProcess.getBurstTme() - 1);
            }else{
                idleTime++;
            }

            currentTime++;
        }

            String str = snapshot();
            System.out.println(str);
            System.out.println(getMetrics());


    }

    /**
     * isCompleted checks if all processes are completed
     * @return true if all processes are completed false otherwise
     */

    public boolean isCompleted(){
        return readyQueue.isEmpty() && IOQueue.isEmpty() && CPUProcess == null;
    }


    /**
     * processingIOQueue checks if any processes in the IO queue are ready to be moved to the ready queue
     * if so, it moves them to the ready queue
     */
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

    /**
     * snapshot creates a snapshot of the current state of the system
     * and displays it to the console
     * @return a string representation of the current state of the system
     */
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
        sb.append("\n---------------------------------------------------------\n");
        for (Process completedProcess : completedList){
            sb.append(completedProcess.getStrName() + "\t");
        }

        sb.append("\n" + " ::::::::::::::::::::::::::::::::::::::::::::::::::\n\n");
        outputToFile(outputFile, sb);
        return sb.toString();
    }

    /**
     * outputToFile outputs the string representation of the current state of the system to a file
     * @param filename name of the file to write to
     * @param sb string representation of the current state of the system
     */
    public void outputToFile(String filename, StringBuilder sb) {
        if (flag == false){
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

    /**
     * getMetrics method calculates the metrics of the RR and
     * prints all values to the console
     * @return metrics of the Priority algorithm
     */
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

    /**
     * getProcesses method returns the list of processes
     * @return list of processes
     */

    public LinkedList<Process> getProcesses(){
        return completedList;
    }

}




