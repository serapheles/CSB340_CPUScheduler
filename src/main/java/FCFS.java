import com.bears.utility.Process;
import com.bears.utility.QueueManager;

public class FCFS {
    private QueueManager readyQueue;
    private int currentTime;

    public FCFS() {
        readyQueue = new QueueManager("src/main/resources/input");
        currentTime = 0;

    }

    public Process getProcessOnCPU(){
        return readyQueue.getCurrentProcess();
    }





    }



