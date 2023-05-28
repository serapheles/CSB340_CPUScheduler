import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Scanner;

public abstract class Multilevel {

    protected int timeElapsed;
    protected int ioOnly;

    public Multilevel(){

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

    protected void administrivia(Job job) {
        if (job.getResponseTime() == -1) {
            job.setResponseTime(timeElapsed);
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
