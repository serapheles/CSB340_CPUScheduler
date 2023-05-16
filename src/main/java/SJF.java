import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Scanner;

public class SJF {

    PriorityQueue<Job> cpu_queue;

    //Only considering total cpu time for such an early attempt (with priority and finally total IO time for tiebreakers).
    public SJF() {
        cpu_queue = new PriorityQueue<>((o1, o2) -> {
            int res = Integer.compare(o1.neededTime, o2.neededTime);
            if (res == 0) {
                res = Integer.compare(o1.priority, o2.priority);
            }
            if (res == 0) {
                int v1 = 0;
                int v2 = 0;
                for (int i : o1.io_times) {
                    v1 += i;
                }
                for (int i : o2.io_times) {
                    v2 += i;
                }
                res = Integer.compare(v1, v2);
            }
            return res;
        });
    }

    private static ArrayDeque<Job> open_processes() throws FileNotFoundException {
        return open_processes("src/main/resources/processes.txt");
    }

    //These are here because figuring out whatever the hell Rob was doing with his Process class would take longer than
    //writing my own basic one.
    private static ArrayDeque<Job> open_processes(String text) throws FileNotFoundException {
        Scanner scanner = new Scanner(new BufferedReader(new FileReader(text)));
        ArrayDeque<Job> jobs = new ArrayDeque<>();
        int process_id, priority;
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            //That second part is just me, isn't currently in the file.
            if (line.length() == 0 || line.charAt(0) == '#') {
                continue;
            }
            //Not safe enough for "real" code, but this is speed running.
            //This section is icky.
            process_id = Character.getNumericValue(line.charAt(1));
            priority = Character.getNumericValue(line.charAt(line.length() - 1));
            ArrayList<Integer> cpu_times = new ArrayList<>(), io_times = new ArrayList<>();
            boolean cpu = true;
            int start = line.indexOf(123) + 1;  //Should always be 3
            int end = line.lastIndexOf(125);

            String[] vals = line.substring(start, end).split(", ");

            for (String s : vals) {
                if (cpu) {
                    cpu_times.add(Integer.parseInt(s));
                } else {
                    io_times.add(Integer.parseInt(s));
                }
                cpu = !cpu;
            }
            jobs.add(new Job(process_id, cpu_times, io_times, priority));
        }
        return jobs;
    }

    public static void main(String[] args) throws FileNotFoundException {
        SJF preAlpha = new SJF();
        preAlpha.cycle();
    }

    //Adds a new process to the queue. Currently trivial.
    public void addProcess(Job newProcess) {
        int neededTime = 0;
        for (int i : newProcess.cpu_times) {
            neededTime += i;
        }
        newProcess.neededTime = neededTime;
        cpu_queue.add(newProcess);
    }

    //Driver function, adds the processes to a queue and checks each unit of time if anything is done.
    //Currently only using total cpu time and ignoring IO, so just pops items in turn.
    public void cycle() throws FileNotFoundException {
        ArrayDeque<Job> jobs = open_processes();
        while (!jobs.isEmpty()) {
            addProcess(jobs.pop());
        }
        int timeSlice = 0;
        int runTime = 0;
        while (!cpu_queue.isEmpty()) {
            runTime++;
            if (++timeSlice > cpu_queue.peek().neededTime) {
                Job j = cpu_queue.remove();
                System.out.println(j.process_id);
                timeSlice = 0;
//                continue;
            }
        }
    }

    private static class Job {
        ArrayList<Integer> cpu_times;

        ArrayList<Integer> io_times;

        int priority;

        int process_id;

        int neededTime;

        Job(int process_id, ArrayList<Integer> cpu_times, ArrayList<Integer> io_times, int priority) {
            this.process_id = process_id;
            this.cpu_times = cpu_times;
            this.io_times = io_times;
            this.priority = priority;
            this.neededTime = 0;
        }

        public int getProcess_id() {
            return process_id;
        }

        public int getPriority() {
            return priority;
        }

        public ArrayList<Integer> getCpu_times() {
            return cpu_times;
        }

        public ArrayList<Integer> getIo_times() {
            return io_times;
        }

        public int getNeededTime() {
            return neededTime;
        }

        public void setNeededTime(int neededTime) {
            this.neededTime = neededTime;
        }
    }
}
