import java.util.ArrayDeque;

public class MLFQ extends Multilevel {

    //Quantum 5
    private final ArrayDeque<Job> priority0;

    //Quantum 10
    private final ArrayDeque<Job> priority1;

    //FCFS
    private final ArrayDeque<Job> priority2;

    public MLFQ() {
        super();
        priority0 = new ArrayDeque<Job>();
        priority1 = new ArrayDeque<Job>();
        priority2 = new ArrayDeque<Job>();

        while (!readyQueue.isEmpty()) {
            priority0.add(readyQueue.remove());
        }

        tick();
        finalReport();
        report(text);
    }

    public static void main(String[] args) {
        new MLFQ();
    }

    private void administrivia(Job job) {
        checkIfFirstResponse(job);
        updateWaitTimes(job);
    }

    private void updateWaitTimes(Job currentJob) {
        updateWaitTimes(currentJob, priority0);
        updateWaitTimes(currentJob, priority1);
        updateWaitTimes(currentJob, priority2);
    }

    /**
     * Loops until every queue is empty.
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
