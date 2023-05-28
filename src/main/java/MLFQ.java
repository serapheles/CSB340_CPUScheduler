import java.util.*;

public class MLFQ extends Multilevel {
    private final ArrayDeque<Job> readyQueue;

    //Quantum 5
    private final ArrayDeque<Job> priority0;

    //Quantum 10
    private final ArrayDeque<Job> priority1;

    //FCFS
    private final ArrayDeque<Job> priority2;

    //Technically this is a sorted collection, but the optimizations that take advantage of that aren't worth it for
    //this assignment.
    private final TreeMap<Job, ArrayDeque<Job>> ioJobs;

    private final ArrayList<Job> allJobs;
    private final boolean output = true;


    public MLFQ() {
        readyQueue = open_processes();
        allJobs = new ArrayList<>(readyQueue);
        priority0 = new ArrayDeque<Job>();
        priority1 = new ArrayDeque<Job>();
        priority2 = new ArrayDeque<Job>();
        ioJobs = new TreeMap<Job, ArrayDeque<Job>>(Comparator.comparingInt(Job::checkNextIOBurst).thenComparingInt(Job::getPriority));

        while (!readyQueue.isEmpty()) {
            priority0.add(readyQueue.remove());
        }

        tick();
        System.out.println(readyQueue);
        System.out.println(allJobs);

        System.out.println("Total time elapsed: " + timeElapsed + "\n");
        System.out.printf("Cpu utilization: %.2f\n", getRatio());
        int totalWait = 0;
        int totalTurnaround = 0;
        int totalResponse = 0;
        for (Job j : readyQueue) {
            totalWait += j.getWaitTime();
            totalTurnaround += (j.getWaitTime() + j.getNeededCPUTime() + j.getNeededIOTime());
            totalResponse += j.getResponseTime();
            System.out.println("Process " + j.getProcessId() + ":");
            System.out.println("Wait time: " + j.getWaitTime());
            System.out.println("Turnaround: " + (j.getWaitTime() + j.getNeededCPUTime() + j.getNeededIOTime()));
            System.out.println("Response: " + j.getResponseTime() + "\n");
        }
        System.out.printf("Average wait time: %.2f\n", (double) totalWait / (double) readyQueue.size());
        System.out.printf("Average turnaround: %.2f\n", (double) totalTurnaround / (double) readyQueue.size());
        System.out.printf("Average response time: %.2f\n", (double) totalResponse / (double) readyQueue.size());
    }

    public static void main(String[] args) {
        new MLFQ();
    }

    private void updateWaitTimes(Job currentJob) {
        System.out.println(currentJob.getProcessId());
        System.out.println(timeElapsed);
        for (Job j : priority0) {
            if (j.equals(currentJob)) {
                continue;
            }
            j.updateWaitTime();
        }
        for (Job j : priority1) {
            if (j.equals(currentJob)) {
                continue;
            }
            j.updateWaitTime();
        }
        for (Job j : priority2) {
            if (j.equals(currentJob)) {
                continue;
            }
            j.updateWaitTime();
        }
    }

    /**
     * Loops until every queue is empty.
     */
    private void tick() {
        int clock = 0;
        byte lastQueue = 0;

        while (!(priority0.isEmpty() && priority1.isEmpty() && priority2.isEmpty() && ioJobs.isEmpty())) {
//            System.out.println(timeElapsed);

            if (!ioJobs.isEmpty()) {
                Job tempJob;
                Iterator<Map.Entry<Job, ArrayDeque<Job>>> it = ioJobs.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry<Job, ArrayDeque<Job>> mapping = it.next();
                    tempJob = mapping.getKey();
                    Integer timeRemaining = tempJob.decrementIOBurst();
                    if (timeRemaining == null || timeRemaining < 0) {
                        throw new RuntimeException();
                    }
                    if (timeRemaining == 0) {
                        mapping.getKey().getNextIOBurst();
                        mapping.getValue().add(mapping.getKey());
                        it.remove();

                    }
                }
            }

            if (!priority0.isEmpty()) {
                if (lastQueue != 0) {
                    clock = 0;
                    lastQueue = 0;
                    if (output) {
                        displayStatus(priority0.peek());
                    }
                }
                administrivia(priority0.peek());
                updateWaitTimes(priority0.peek());
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
                updateWaitTimes(priority1.peek());
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
                updateWaitTimes(priority2.peek());
                if (tock(priority2)) {
                    lastQueue = -1;
                }
            } else {
                ioOnly++;
            }
            timeElapsed++;
        }
    }

    /**
     * Updates the process that currently has priority.
     *
     * @param queue The current queue.
     * @return Effectively, if the process yields priority.
     */
    private boolean tock(ArrayDeque<Job> queue) {
        Integer time = queue.peek().decrementCpuBurst();
        if (time == null || time < 0) {
            System.out.println("Process id: " + queue.peek().getProcessId());
            System.out.println("Remaining burst time: " + queue.peek().checkNextCpuBurst());
            System.out.println(time);
            throw new RuntimeException();
        }
        //Refactor this later, in the magical world where there is time.
        if (time == 0) {
            System.out.println("Burst complete for process: " + queue.peek().getProcessId());
            queue.peek().getNextCpuBurst();
            //need to remove the value from the job's time
            if (queue.peek().checkNextIOBurst() == null) {
                //Stop updating wait time
                Job tempJob = queue.remove();
                //
                allJobs.remove(tempJob);
                readyQueue.add(tempJob);
                if (output) {
                    System.out.println("Process complete: " + tempJob.getProcessId());
                }
            } else {
                System.out.println("Adding to IO: " + queue.peek().getProcessId());
                ioJobs.put(queue.remove(), queue);
            }
            return true;
        }
        return false;
    }

    /**
     * Outputs status information.
     *
     * @param currentJob The currently running process.
     */
    private void displayStatus(Job currentJob) {
        StringBuilder text = new StringBuilder();
        text.append("Total elapsed time: ").append(timeElapsed).append("\n");
        text.append("Currently running process: ").append(currentJob.getProcessId()).append("\n");
        text.append("Processes in I/O:\n");
        for (Job tempJob : ioJobs.keySet()) {
            text.append(tempJob.getProcessId()).append(", remaining time: ").append(tempJob.checkNextIOBurst()).append("\n\n");
        }
        System.out.println(text);
    }
}
