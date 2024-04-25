import java.util.*;
import java.io.*;

public class Project3 {
    static class MyJob {
        private String name; // name of job
        private int arrivalTime; // arrival of job
        private int duration; // duration of job
        private int spaceCount; // tracks amount of spacing for graph

        // Constructor
        MyJob(String n, int arrTime, int durationTime) {
            name = n;
            arrivalTime = arrTime;
            duration = durationTime;
        }

        // Copy constructor
        MyJob(MyJob selected) {
            this(selected.name, selected.arrivalTime, selected.duration);
        }

        public void setSpaceCount(int num) {
            spaceCount = num;
        }

        // Returns name of job
        public String getName() {
            return name;
        }

        // Returns arrival time
        public int getArrivalTime() {
            return arrivalTime;
        }

        // Returns Duration
        public int getDuration() {
            return duration;
        }

        // Sets the duration of the job
        public void setDuration(int duration) {
            this.duration = duration;
        }

        // Prints graph
        public void printMatrix() {
            System.out.print(name); // Prints name of job (e.g. 'A', 'B', etc.)

            // Creates proper spacing
            for (int i = 0; i <= spaceCount; i++) {
                System.out.print(" ");
            }

            // Fills slots with 'X'
            for (int j = 0; j < duration; j++) {
                System.out.print("X");
            }

            System.out.println(); // New line for next job
        }
    }

    public static void main(String[] args) {

        // Check arguments
        if (args.length != 1) {
            System.out.println("Error Incorrect Arguments");
            System.exit(0);
        }

        // Declare job variables
        ArrayList<MyJob> jobList = new ArrayList<>();
        String currJob;

        // Read file
        try {
            FileReader fileReader = new FileReader(args[0]);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            // Parse file and add jobs to job list
            while ((currJob = bufferedReader.readLine()) != null) {
                String[] parseLine = currJob.split("\t", 3);
                jobList.add(new MyJob(parseLine[0], Integer.parseInt(parseLine[1]),
                        Integer.parseInt(parseLine[2])));
            }
        }
        // Error handling
        catch (FileNotFoundException ex) {
            System.out.println("Unable to open file");
        } catch (IOException e) {
            System.out.println("Error reading file");
        }

        // Create new instance of FCFS scheduling algorithm, passing in job list
        FCFS FCFS_scheduler = new FCFS();
        FCFS_scheduler.mySchedule(jobList);

        // Create new instance of RR scheduling algorithm, passing in job list and quantum value
        RR RR_scheduler = new RR();
        int quantum = 1; // Quantum value
        RR_scheduler.mySchedule(jobList, quantum);
    }
}

class FCFS {

    // Main algorithm
    public void mySchedule(ArrayList<Project3.MyJob> list) {
        System.out.println("FCFS"); // Print algorithm name for graph
        Project3.MyJob copy;
        int time = 0;

        // Copy list
        ArrayList<Project3.MyJob> newList = new ArrayList<>();
        for (Project3.MyJob job : list) {
            newList.add(new Project3.MyJob(job));
        }

        while (!(newList.isEmpty())) {
            copy = max(newList); // Find the job with the earliest arrival time
            copy.setSpaceCount(time); // Set spacing for the job's execution time
            copy.printMatrix(); // Print the job execution
            time += copy.getDuration(); // Update time by adding the duration of the current job
        }
    }

    // Helper function to find job with the earliest arrival time
    public Project3.MyJob max(ArrayList<Project3.MyJob> list) {
        Project3.MyJob selected = null;
        int max = 10000;
        int index = 0;

        // Find the job with the earliest arrival time
        for (int i = 0; i < list.size(); i++) {
            if (max >= list.get(i).getArrivalTime()) {
                index = i;
                max = list.get(i).getArrivalTime();
                selected = list.get(i);
            }
        }

        // Remove the selected job from the list
        list.remove(index);

        return selected;
    }
}

class RR {

    // Main algorithm
    public void mySchedule(ArrayList<Project3.MyJob> list, int quantum) {
        System.out.println("Round Robin"); // Print algorithm name for graph

        int time = 0;
        Queue<Project3.MyJob> readyQueue = new LinkedList<>();

        // Iterate until all jobs are completed
        while (!list.isEmpty() || !readyQueue.isEmpty()) {
            // Add arriving jobs to the ready queue
            while (!list.isEmpty() && list.get(0).getArrivalTime() <= time) {
                readyQueue.add(list.remove(0));
            }

            // Execute jobs in the ready queue for one time slice or quantum
            for (Project3.MyJob currentJob : readyQueue) {
                if (currentJob.getDuration() <= 0) {
                    continue; // Skip completed jobs
                }

                currentJob.setSpaceCount(time);
                currentJob.printMatrix();

                // Decrease job duration
                currentJob.setDuration(currentJob.getDuration() - 1);

                time++;

                // Check if quantum or job duration has expired
                if (time % quantum == 0 || currentJob.getDuration() <= 0) {
                    break; // Exit the loop to give the next job a turn
                }
            }

            // Remove completed jobs from the ready queue
            readyQueue.removeIf(job -> job.getDuration() <= 0);

            // If there are no jobs in the ready queue but there are still jobs not arrived yet
            if (!list.isEmpty() && readyQueue.isEmpty()) {
                time = list.get(0).getArrivalTime();
            }
        }
    }
}
