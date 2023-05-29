#Introduction
Scheduling processes is an important part of an Operating System. Since there are many considerations that must be made, there are many algorithms that aim to prioritize or balance specific facets of the task. Presented here are some of the more basic and common scheduling algorithms as examples of how it is done and the tradeoffs that are made as part of this task.

##FCFS
First Come First Serve process scheduling algorithm like the name it implies is simply First Come First Serve. This take a very simple appraoch and simply put whichever process is first on the ready queue to be CPU.

##SJF
Shortest Job First process scheduling algorithms, particularly the preemptive version implemented in our assignment, take the greedy algorithm approach to minimizing the turnaround by ensuring the shortest jobs are given priority, thus able to finish quickly (which in turn frees up the queue for slower processes). However, the corollary to this is that the slowest processes take even longer. Indeed, if there are a steady stream of short processes, the longest processes may never get a chance to run at all.

##Priority Scheduling

##Round Robin

##Multilevel Queue
The Multilevel Queue process scheduling algorithm combines multiple approaches in order to try and balance the pros and cons of different methods. In particular, it attempts to balance the fairness of methods like Round Robin with an increased responsiveness for higher priority processes, such as kernel and real time processes. However, the particular implementation here uses the Fixed Priority Preemptive Scheduling Method, which can lead to starvation of lower priority processes.

##Multilevel Feedback Queue
The Multilevel Feedback Queue takes the idea used in the Multilevel Queue of balancing approaches, but focuses on run time to prioritize shorter processes for Shortest Job First style efficiency, with any process that takes too long being given a lower priority. In the implementation here, this priority drop is permanent, though this certainly is not the universal implementation. Whether or not the higher priority queues actually use Round Robin is at some level a matter of perspective, but by downgrading the priority of a process after a set time, they functionally do. The lowest level queue is First Come, First Serve, though as with other scheduling methods that allow preemption, it risks starvation if there are too many quick processes or if a process periodically called IO to stay at a higher priority.


##Averages

|						|FCFS	|SJF	| Priority	|RR		|MLQ	|MLFQ	|
|---					|---	|---	|---		|---	|---	|---	|
|CPU Utilization		|85.34%	|100%	|			|		|82%	|91.4%	|
|Avg Waiting Time		|185.25	|124.88	|			|		|261.38	|156.88	|
|Avg Turnaround Time	|521.37	|461	|			|		|597.5	|493	|
|AVG Response Time		|24.37	|29.5	|			|		|39.25	|15.75	|


|						|FCFS	 CPU utilization: |SJF	CPU utilization: | Priority CPU utilization: 	|RR CPU utilization: |MLQ	CPU utilization: |MLFQ CPU utilization: 	|
|---					|---	|---	|---		|---	|---	|---	|
|CPU Utilization		|85.34%	|100%	|			|		|82%	|91.4%	|
|Avg Waiting Time		|185.25	|124.88	|			|		|261.38	|156.88	|
|Avg Turnaround Time	|521.37	|461	|			|		|597.5	|493	|
|AVG Response Time		|24.37	|29.5	|			|		|39.25	|15.75	|
