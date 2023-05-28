import java.util.ArrayDeque;
import java.util.ArrayList;

public class MLQ extends Multilevel{

    private final ArrayDeque<Job> readyQueue;

    //Quantum 5
    private final ArrayDeque<Job> priorityHigh;

    //Quantum 10
    private final ArrayDeque<Job> priorityLow;

    private final ArrayList<Job> allJobs;

    public MLQ(ArrayDeque<Job> priorityHigh, ArrayDeque<Job> priorityLow){
        this.priorityHigh = priorityHigh;
        this.priorityLow = priorityLow;
        readyQueue = open_processes();
        allJobs = new ArrayList<>(readyQueue);

//        ioJobs = new TreeMap<Job, ArrayDeque<Job>>(Comparator.comparingInt(Job::checkNextIOBurst).thenComparingInt(Job::getPriority));


    }
}
