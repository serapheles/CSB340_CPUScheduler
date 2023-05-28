import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

public abstract class Multilevel {

    protected final ArrayDeque<Job> readyQueue;
    //Technically this is a sorted collection, but the optimizations that take advantage of that aren't worth it for
    //this assignment.
    protected final TreeMap<Job, ArrayDeque<Job>> ioJobs;
    protected final ArrayList<Job> allJobs;
    protected final boolean output = true;
    protected int timeElapsed;
    protected int ioOnly;

    public Multilevel() {
        readyQueue = open_processes();
        allJobs = new ArrayList<>(readyQueue);
        ioJobs = new TreeMap<Job, ArrayDeque<Job>>(Comparator.comparingInt(Job::checkNextIOBurst).thenComparingInt(Job::getPriority));

    }

    protected void updateIO(){
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

    protected double getRatio() {
        return (1.0 - ((double) ioOnly / (double) timeElapsed)) * 100.0;
    }

    protected void checkIfFirstResponse(Job job){
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
        StringBuilder text = new StringBuilder();
        text.append("Total elapsed time: ").append(timeElapsed).append("\n");
        text.append("Currently running process: ").append(currentJob.getProcessId()).append("\n");
        text.append("Processes in I/O:\n");
        for (Job tempJob : ioJobs.keySet()) {
            text.append(tempJob.getProcessId()).append(", remaining time: ").append(tempJob.checkNextIOBurst()).append("\n\n");
        }
        System.out.println(text);
    }

    protected void updateWaitTimes(Job currentJob, ArrayDeque<Job> queue){
        for (Job j : queue) {
            if (j.equals(currentJob)) {
                continue;
            }
            j.updateWaitTime();
        }
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
