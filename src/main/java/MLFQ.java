import java.util.ArrayDeque;

/**
 * Multilevel Feedback Queue. Doesn't incorporate techniques to avoid monopolizing the highest queue(s).
 * <p>
 * -Kyle
 */
public class MLFQ extends Multilevel {

    //Round-robin queue with quantum = 5
    private final ArrayDeque<Job> priority0;

    //Round-robin queue with quantum = 10
    private final ArrayDeque<Job> priority1;

    //Lowest-level queue using First Come, First Served.
    private final ArrayDeque<Job> priority2;

    /**
     * Basic constructor, calls the super constructor before setting up local specifics and driving all the computations.
     */
    public MLFQ(boolean toOutput) {
        super();
        output = toOutput;
        priority0 = new ArrayDeque<Job>();
        priority1 = new ArrayDeque<Job>();
        priority2 = new ArrayDeque<Job>();

        while (!readyQueue.isEmpty()) {
            priority0.add(readyQueue.remove());
        }

        tick();
        finalReport();
        report(text, "MLFQ.txt");
        if (output) {
            System.out.println(text);
        }
    }

    public MLFQ(){
        this(true);
    }

    /**
     * Local Main method, largely for testing.
     *
     * @param args Unused.
     */
    public static void main(String[] args) {
        new MLFQ();
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
     * Calls the parent method to update wait times on all three queues.
     *
     * @param currentJob The current job.
     */
    private void updateWaitTimes(Job currentJob) {
        updateWaitTimes(currentJob, priority0);
        updateWaitTimes(currentJob, priority1);
        updateWaitTimes(currentJob, priority2);
    }

    /**
     * While there are jobs remaining, updates IO, then checks queues in order of priority to determine the next job.
     * This could probably be refactored if there was a need for it.
     */
    private void tick() {
        int clock = 0;
        byte lastQueue = 0;

        while (!(priority0.isEmpty() && priority1.isEmpty() && priority2.isEmpty() && ioJobs.isEmpty())) {
            updateIO();

            if (!priority0.isEmpty()) {
                if (lastQueue != 0) {
                    clock = 0;
                    lastQueue = 0;
                    if (output) {
                        displayStatus(priority0.peek());
                    }
                }
                administrivia(priority0.peek());
                if (tock(priority0)) {
                    lastQueue = -1;
                    timeElapsed++;
                    continue;
                }
                if (++clock == 5) {
                    priority1.add(priority0.remove());
                    lastQueue = -1;
                }
            } else if (!priority1.isEmpty()) {
                if (lastQueue != 1) {
                    clock = 0;
                    lastQueue = 1;
                    if (output) {
                        displayStatus(priority1.peek());
                    }
                }
                administrivia(priority1.peek());
                if (tock(priority1)) {
                    lastQueue = -1;
                    timeElapsed++;
                    continue;
                }
                if (++clock == 10) {
                    priority2.add(priority1.remove());
                    lastQueue = -1;
                }
            } else if (!priority2.isEmpty()) {
                if (lastQueue != 2) {
                    clock = 0;
                    lastQueue = 2;
                    if (output) {
                        displayStatus(priority2.peek());
                    }
                }
                administrivia(priority2.peek());
                if (tock(priority2)) {
                    lastQueue = -1;
                }
            } else {
                ioOnly++;
            }
            timeElapsed++;
        }
    }

}
