import java.util.Comparator;
import java.util.PriorityQueue;

public class SJF extends Multilevel {

    private final PriorityQueue<Job> primaryQueue;

    public SJF() {
        super();
        primaryQueue = new PriorityQueue<>(Comparator.comparingInt(Job::checkNextCpuBurst).thenComparingInt(Job::getPriority));
        while (!readyQueue.isEmpty()) {
            primaryQueue.add(readyQueue.remove());
        }

        tick();
        finalReport();
        report(text);
    }

    public static void main(String[] args) {
        new SJF();
    }

    private void administrivia(Job job) {
        checkIfFirstResponse(job);
        updateWaitTimes(job);
    }

    private void updateWaitTimes(Job currentJob) {
        updateWaitTimes(currentJob, primaryQueue);
    }

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
