import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * Shortest Job First: Preemptive. This was honestly made very last minute, but since the parent class was made for
 * MLFQ/MLQ, it was very straight forward. A priority queue set to the current CPU burst (with job priority set for
 * tie-breakers (since java's comparison backed data structures don't allow duplicate values)) neatly handles not only
 * the basic job sorting, but any jobs returning from IO.
 * <p>
 * - Kyle
 */
public class SJF extends Multilevel {

    private final PriorityQueue<Job> primaryQueue;

    /**
     * Basic constructor, calls the super constructor before setting up local specifics and driving all the computations.
     */
    public SJF(boolean toOutput) {
        super();
        output = toOutput;
        primaryQueue = new PriorityQueue<>(Comparator.comparingInt(Job::checkNextCpuBurst).thenComparingInt(Job::getPriority));
        while (!readyQueue.isEmpty()) {
            primaryQueue.add(readyQueue.remove());
        }

        tick();
        finalReport();
        report(text, "SJF.txt");
        if (output) {
            System.out.println(text);
        }
    }

    public SJF(){
        this(true);
    }

    /**
     * Local Main method, largely for testing.
     *
     * @param args Unused.
     */
    public static void main(String[] args) {
        new SJF();
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
     * Calls the parent method to update wait times on the jobs in queue.
     *
     * @param currentJob The current job.
     */
    private void updateWaitTimes(Job currentJob) {
        updateWaitTimes(currentJob, primaryQueue);
    }

    /**
     * While there are jobs remaining, updates IO, then checks the queue to determine the next job.
     */
    private void tick() {
        int currentJob = -1;
        while (!(primaryQueue.isEmpty() && ioJobs.isEmpty())) {
            updateIO();
            if (!primaryQueue.isEmpty()) {
                if (primaryQueue.peek().getProcessId() != currentJob) {
                    currentJob = primaryQueue.peek().getProcessId();
                    if (output) {
                        displayStatus(primaryQueue.peek());
                    }
                }
                administrivia(primaryQueue.peek());
                if (tock(primaryQueue)) {
                    timeElapsed++;
                    continue;
                }

            }
            timeElapsed++;
        }
    }
}
