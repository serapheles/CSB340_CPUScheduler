import java.io.*;
import java.util.*;

/**
 * Abstract parent class for other classes to inherit from. With the approach I used to ensure preemption, I realized
 * there's a lot of overlap so this made a lot of sense. I thought of everything as seconds on a clock, hence why there
 * are methods named "tick" and "tock"; tick is local (to the algorithm) updates/checks, while tock updates the current
 * process (which is the same across algorithms).
 * <p>
 * This was named when I thought I was doing MLFQ and MLQ, and thought Rob was going to do SJF.
 * <p>
 * -Kyle
 */
public abstract class Multilevel {

    protected final ArrayDeque<Job> readyQueue;
    //Technically this is a sorted collection, but the optimizations that take advantage of that aren't worth it for
    //this assignment.
    protected final TreeMap<Job, Queue<Job>> ioJobs;
    protected final ArrayList<Job> allJobs;

    //If true, print results to stdout.
    protected boolean output = true;
    protected int timeElapsed;
    protected int ioOnly;
    protected StringBuilder text;

    /**
     * Basic constructor.
     */
    public Multilevel() {
        readyQueue = open_processes();
        allJobs = new ArrayList<>(readyQueue);
        ioJobs = new TreeMap<Job, Queue<Job>>(Comparator.comparingInt(Job::checkNextIOBurst).thenComparingInt(Job::getPriority));
        text = new StringBuilder();
    }

    /**
     * Calls the argument version with the default process file.
     *
     * @return Job queue.
     */
    protected static ArrayDeque<Job> open_processes() {
        return open_processes("src/main/resources/processes.txt");
    }

    /**
     * Not perfect, but I prefer how this is structured to the "group" version.
     *
     * @param text The file to open. Here in case you have, or I come up with, other tests.
     * @return The job queue.
     */
    protected static ArrayDeque<Job> open_processes(String text) {
        try {
            Scanner scanner = new Scanner(new BufferedReader(new FileReader(text)));
            ArrayDeque<Job> jobs = new ArrayDeque<>();
            int processId, priority;
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                //That second part is just me, isn't currently in the file.
                if (line.length() == 0 || line.charAt(0) == '#') {
                    continue;
                }
                //Not safe enough for "real" code, but this is speed running.
                //This section is icky.
                processId = Character.getNumericValue(line.charAt(1));
                priority = Character.getNumericValue(line.charAt(line.length() - 1));
                ArrayList<Integer> cpuTimes = new ArrayList<>(), ioTimes = new ArrayList<>();
                boolean cpu = true;
                int start = line.indexOf(123) + 1;  //Should always be 3
                int end = line.lastIndexOf(125);

                String[] vals = line.substring(start, end).split(", ");

                for (String s : vals) {
                    if (cpu) {
                        cpuTimes.add(Integer.parseInt(s));
                    } else {
                        ioTimes.add(Integer.parseInt(s));
                    }
                    cpu = !cpu;
                }
                jobs.add(new Job(processId, cpuTimes, ioTimes, priority));
            }
            return jobs;
        } catch (Exception err) {
            System.err.println(err.getMessage());
            System.exit(1);
        }
        return null;
    }

    /*
        Adapted from digitalocean.com.
     */
    public void report(StringBuilder text, String filename) {
        File file = new File(filename);
        try {
            FileWriter writer = new FileWriter(file);
            BufferedWriter bufferedWriter = new BufferedWriter(writer);
            bufferedWriter.write(String.valueOf(text));
            bufferedWriter.close();
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Loops through processes that are currently in I/O and updates their I/O times. When a IO burst reaches zero,
     * returns it to the associated queue (relevant particularly for MLFQ).
     */
    protected void updateIO() {
        if (!ioJobs.isEmpty()) {
            Job tempJob;
            Iterator<Map.Entry<Job, Queue<Job>>> it = ioJobs.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<Job, Queue<Job>> mapping = it.next();
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
    }

    /**
     * Calculates the CPU utilization, for cleaner string formatting.
     *
     * @return The cpu utilization as a float.
     */
    protected double getRatio() {
        return (1.0 - ((double) ioOnly / (double) timeElapsed)) * 100.0;
    }

    /**
     * Determines if this is the first time a job has been active, for setting the response times.
     *
     * @param job The active job.
     */
    protected void checkIfFirstResponse(Job job) {
        if (job.getResponseTime() == -1) {
            job.setResponseTime(timeElapsed);
        }
    }

    /**
     * Outputs status information.
     *
     * @param currentJob The currently running process.
     */
    protected void displayStatus(Job currentJob) {
        text.append("Total elapsed time: ").append(timeElapsed).append("\n");
        text.append("Currently running process: ").append(currentJob.getProcessId()).append("\n");
        text.append("Processes in I/O:\n");
        for (Job tempJob : ioJobs.keySet()) {
            text.append(tempJob.getProcessId()).append(", remaining time: ").append(tempJob.checkNextIOBurst()).append("\n\n");
        }
    }

    /**
     * Iterates through the jobs not in IO to update their respective wait times.
     *
     * @param currentJob The currently running job, since it isn't waiting.
     * @param queue      The relevant collection; may not actually be a queue.
     */
    protected void updateWaitTimes(Job currentJob, Collection<Job> queue) {
        for (Job j : queue) {
            if (j.equals(currentJob)) {
                continue;
            }
            j.updateWaitTime();
        }
    }

    /**
     * Reports the statistics of cpu scheduler.
     */
    protected void finalReport() {
        text.append("Total time elapsed: ").append(timeElapsed).append("\n");
        text.append("Cpu utilization: ").append(getRatio()).append("\n");
        int totalWait = 0;
        int totalTurnaround = 0;
        int totalResponse = 0;
        for (Job j : readyQueue) {
            totalWait += j.getWaitTime();
            totalTurnaround += (j.getWaitTime() + j.getNeededCPUTime() + j.getNeededIOTime());
            totalResponse += j.getResponseTime();
            text.append("Process ").append(j.getProcessId()).append(":").append("\n");
            text.append("Wait time: ").append(j.getWaitTime()).append("\n");
            text.append("Turnaround: ").append(j.getWaitTime() + j.getNeededCPUTime() + j.getNeededIOTime()).append("\n");
            text.append("Response: ").append(j.getResponseTime()).append("\n").append("\n");
        }
        text.append("Average wait time: ").append((double) totalWait / (double) readyQueue.size()).append("\n");
        text.append("Average turnaround: ").append((double) totalTurnaround / (double) readyQueue.size()).append("\n");
        text.append("Average response time: ").append((double) totalResponse / (double) readyQueue.size()).append("\n");
    }

    /**
     * Updates the process that currently has priority.
     *
     * @param queue The current queue.
     * @return Effectively, if the process yields priority.
     */
    protected boolean tock(Queue<Job> queue) {
        Integer time = queue.peek().decrementCpuBurst();
        if (time == null || time < 0) {
            System.out.println("Process id: " + queue.peek().getProcessId());
            System.out.println("Remaining burst time: " + queue.peek().checkNextCpuBurst());
            System.out.println(time);
            throw new RuntimeException();
        }
        //Refactor this later, in the magical world where there is time.
        if (time == 0) {
            text.append("Burst complete for process: ").append(queue.peek().getProcessId()).append("\n");
            queue.peek().getNextCpuBurst();
            //need to remove the value from the job's time
            if (queue.peek().checkNextIOBurst() == null) {
                //Stop updating wait time
                Job tempJob = queue.remove();
                //
                allJobs.remove(tempJob);
                readyQueue.add(tempJob);
                if (output) {
                    text.append("Process complete: ").append(tempJob.getProcessId()).append("\n");
                }
            } else {
                text.append("Adding to IO: ").append(queue.peek().getProcessId()).append("\n");
                ioJobs.put(queue.remove(), queue);
            }
            return true;
        }
        return false;
    }

    /**
     * Personal process class because my needs and views don't mesh with the package version.
     */
    protected static class Job {
        private final ArrayDeque<Integer> cpuTimes;

        private final ArrayDeque<Integer> ioTimes;

        private final int priority;

        private final int processId;

        private int neededCPUTime;

        private int neededIOTime;
        private int responseTime;
        private int waitTime;

        /**
         * Job constructor.
         *
         * @param processId The id of the job (effectively it's placement).
         * @param cpuTimes  The cpu burst times, as a collection.
         * @param ioTimes   The IO burst times, as a collection.
         * @param priority  The priority of the job. Not needed for every algorithm, but actually useful for some of
         *                  those, where this is used as a tie-breaker in comparisons.
         */
        public Job(int processId, Collection<Integer> cpuTimes, Collection<Integer> ioTimes, int priority) {
            this.processId = processId;
            this.cpuTimes = new ArrayDeque<>(cpuTimes);
            this.ioTimes = new ArrayDeque<>(ioTimes);
            this.priority = priority;
            for (Integer i : cpuTimes) {
                this.neededCPUTime += i;
            }
            for (Integer i : ioTimes) {
                this.neededIOTime += i;
            }
            this.responseTime = -1;
            this.waitTime = 0;
        }

        /**
         * Response time getter.
         *
         * @return The response time.
         */
        public int getResponseTime() {
            return responseTime;
        }

        /**
         * Response time setter.
         *
         * @param responseTime The response time to set.
         */
        public void setResponseTime(int responseTime) {
            this.responseTime = responseTime;
        }

        /**
         * Wait time getter.
         *
         * @return The wait time.
         */
        public int getWaitTime() {
            return waitTime;
        }

        /**
         * Increments the wait time.
         */
        public void updateWaitTime() {
            this.waitTime++;
        }

        /**
         * Process Id getter.
         *
         * @return The process id.
         */
        public int getProcessId() {
            return processId;
        }

        /**
         * Priority getter.
         *
         * @return The priority.
         */
        public int getPriority() {
            return priority;
        }

       /*
       The following two decrement methods are a bit weird. I don't remember the *exact* reason, but there is a reason,
       revolving around updating properly.
        */

        /**
         * Decreases the current CPU burst.
         *
         * @return The current CPU burst time, or null if the job is complete.
         */
        public Integer decrementCpuBurst() {
            if (!cpuTimes.isEmpty()) {
                int val = cpuTimes.remove();
                cpuTimes.addFirst(--val);
                return cpuTimes.peek();
            }
            return null;
        }

        /**
         * Decrease the current IO burst.
         *
         * @return The current IO burst time, or null if the job is complete.
         */
        public Integer decrementIOBurst() {
            if (!ioTimes.isEmpty()) {
                int val = ioTimes.remove();
                ioTimes.addFirst(--val);
                return ioTimes.peek();
            }
            return null;
        }

        /**
         * Get the current CPU burst time.
         *
         * @return The current CPU burst.
         */
        public Integer getNextCpuBurst() {
            return cpuTimes.poll();
        }

        /**
         * Get the current CPU burst time.
         *
         * @return The current CPU burst.
         */
        public Integer checkNextCpuBurst() {
            return cpuTimes.peek();
        }

        /**
         * Get the current IO burst time.
         *
         * @return The current IO burst.
         */
        public Integer getNextIOBurst() {
            return ioTimes.poll();
        }

        /**
         * Get the current IO burst time.
         *
         * @return The current IO burst.
         */
        public Integer checkNextIOBurst() {
            return ioTimes.peek();
        }

        /**
         * Returns the total needed time needed to complete all CPU bursts.
         *
         * @return The combined CPU burst time.
         */
        public int getNeededCPUTime() {
            return neededCPUTime;
        }

        /**
         * Returns the total needed time needed to complete all IO bursts.
         *
         * @return The combined IO burst time.
         */
        public int getNeededIOTime() {
            return neededIOTime;
        }
    }
}
