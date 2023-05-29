import java.io.*;
import java.util.*;

public abstract class Multilevel {

    protected final ArrayDeque<Job> readyQueue;
    //Technically this is a sorted collection, but the optimizations that take advantage of that aren't worth it for
    //this assignment.
    protected final TreeMap<Job, Queue<Job>> ioJobs;
    protected final ArrayList<Job> allJobs;
    protected final boolean output = true;
    protected int timeElapsed;
    protected int ioOnly;
    protected StringBuilder text;

    public Multilevel() {
        readyQueue = open_processes();
        allJobs = new ArrayList<>(readyQueue);
        ioJobs = new TreeMap<Job, Queue<Job>>(Comparator.comparingInt(Job::checkNextIOBurst).thenComparingInt(Job::getPriority));
        text = new StringBuilder();
    }

    /*
        Adapted from digitalocean.com.
     */
    public void report(StringBuilder text){
        File file = new File("Output.txt");
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

    protected double getRatio() {
        return (1.0 - ((double) ioOnly / (double) timeElapsed)) * 100.0;
    }

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

    protected void updateWaitTimes(Job currentJob, Collection<Job> queue) {
        for (Job j : queue) {
            if (j.equals(currentJob)) {
                continue;
            }
            j.updateWaitTime();
        }
    }

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

        public int getResponseTime() {
            return responseTime;
        }

        public void setResponseTime(int responseTime) {
            this.responseTime = responseTime;
        }

        public int getWaitTime() {
            return waitTime;
        }

        public void updateWaitTime() {
            this.waitTime++;
        }

        public int getProcessId() {
            return processId;
        }

        public int getPriority() {
            return priority;
        }

        public Integer decrementCpuBurst() {
            if (!cpuTimes.isEmpty()) {
                int val = cpuTimes.remove();
                cpuTimes.addFirst(--val);
                return cpuTimes.peek();
            }
            return null;
        }

        public Integer decrementIOBurst() {
            if (!ioTimes.isEmpty()) {
                int val = ioTimes.remove();
                ioTimes.addFirst(--val);
                return ioTimes.peek();
            }
            return null;
        }

        public Integer getNextCpuBurst() {
            return cpuTimes.poll();
        }

        public Integer checkNextCpuBurst() {
            return cpuTimes.peek();
        }

        public Integer getNextIOBurst() {
            return ioTimes.poll();
        }

        public Integer checkNextIOBurst() {
            return ioTimes.peek();
        }

        public int getNeededCPUTime() {
            return neededCPUTime;
        }

        public int getNeededIOTime() {
            return neededIOTime;
        }
    }
}
