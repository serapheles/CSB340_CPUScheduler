import java.util.ArrayDeque;

/**
 * Multilevel Queue using Fixed Priority Preemptive Scheduling. I'm not sure why my values are off (per your feedback);
 * current guesses are either I'm updating (or not) something when I shouldn't be which is leading to a cumulative error,
 * or I'm processing things wrong (that is, in a way other than expected, though with internal consistency).
 * <p>
 * -Kyle
 */
public class MLQ extends Multilevel {

    //Higher priority Round-robin queue with quantum = 4
    private final ArrayDeque<Job> priorityHigh;

    //Lowest-level queue using First Come, First Served.
    private final ArrayDeque<Job> priorityLow;

    /**
     * Basic constructor, calls the super constructor before setting up local specifics and driving all the computations.
     */
    public MLQ(boolean toOutput) {
        super();
        output = toOutput;
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
        report(text, "MLQ.txt");
        if (output) {
            System.out.println(text);
        }
    }

    public MLQ(){
        this(true);
    }

    /**
     * Local Main method, largely for testing.
     *
     * @param args Unused.
     */
    public static void main(String[] args) {
        new MLQ();
    }

    /**
     * While there are jobs remaining, updates IO, then checks queues in order of priority to determine the next job.
     * This could probably be refactored if there was a need for it.
     */
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
                if (++clock == 4) {
                    priorityHigh.add(priorityHigh.remove());
                    clock = 0;
                    lastQueue = -1;
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

    /**
     * Calls the local methods to set response time and update wait times.
     *
     * @param job The current job.
     */
    private void administrivia(Job job) {
        checkIfFirstResponse(job);
        updateWaitTimes(job);
    }

    /**
     * Calls the parent method to update wait times on both queues.
     *
     * @param currentJob The current job.
     */
    private void updateWaitTimes(Job currentJob) {
        updateWaitTimes(currentJob, priorityHigh);
        updateWaitTimes(currentJob, priorityLow);
    }

}
