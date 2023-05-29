
import java.util.ArrayDeque;

public class MLQ extends Multilevel {

    //Quantum 5
    private final ArrayDeque<Job> priorityHigh;

    //Quantum 10
    private final ArrayDeque<Job> priorityLow;

    public MLQ() {
        super();
        priorityHigh = new ArrayDeque<>();
        priorityLow = new ArrayDeque<>();

        while (!readyQueue.isEmpty()) {
            if (readyQueue.peek().getPriority() < 5) {
                priorityHigh.add(readyQueue.remove());
            } else {
                priorityLow.add(readyQueue.remove());
            }
        }
        tick();
        finalReport();
    }

    private void tick() {
        int clock = 0;
        byte lastQueue = 0;

        while (!(priorityHigh.isEmpty() && priorityLow.isEmpty() && ioJobs.isEmpty())) {
            updateIO();

            if (!priorityHigh.isEmpty()) {
                if (lastQueue != 0) {
                    clock = 0;
                    lastQueue = 0;
                    if (output) {
                        displayStatus(priorityHigh.peek());
                    }
                }
                administrivia(priorityHigh.peek());
                updateWaitTimes(priorityHigh.peek());
                if (tock(priorityHigh)) {
                    lastQueue = -1;
                    timeElapsed++;
                    continue;
                }
            } else if (!priorityLow.isEmpty()) {
                if (lastQueue != 1) {
                    clock = 0;
                    lastQueue = 1;
                    if (output) {
                        displayStatus(priorityLow.peek());
                    }
                }
                administrivia(priorityLow.peek());
                if (tock(priorityLow)) {
                    lastQueue = -1;
                    timeElapsed++;
                    continue;
                }
            } else {
                ioOnly++;
            }
            timeElapsed++;
        }
    }

    private void administrivia(Job job) {
        checkIfFirstResponse(job);
        updateWaitTimes(job);
    }

    private void updateWaitTimes(Job currentJob) {
        updateWaitTimes(currentJob, priorityHigh);
        updateWaitTimes(currentJob, priorityLow);
    }

    public static void main(String[] args) {
        new MLQ();
    }

}
