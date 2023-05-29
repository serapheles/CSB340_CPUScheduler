# Introduction
Scheduling processes is an important part of an Operating System. Since there are many considerations that must be made, there are many algorithms that aim to prioritize or balance specific facets of the task. Presented here are some of the more basic and common scheduling algorithms as examples of how it is done and the tradeoffs that are made as part of this task.

## FCFS
First Come First Serve process scheduling algorithm like the name it implies is simply First Come First Serve. FCFS is a basic and straightforward algorithm for process scheduling. In this algorithm, the process are executed in the order the arrive, with the first process in the que being girst to receive the CPU. This approach ensures that tasks are executed in a predictable and sequential way. However, FCFS may lead to poor utilization of system resources if long running processes occupy the CPU, causing to wait for extended periods. Moreover, this algorithm is not suitable for time-sensitive or interactive tasks that require quick response times.  

##SJF
Shortest Job First process scheduling algorithms, particularly the preemptive version implemented in our assignment, take the greedy algorithm approach to minimizing the turnaround by ensuring the shortest jobs are given priority, thus able to finish quickly (which in turn frees up the queue for slower processes). However, the corollary to this is that the slowest processes take even longer. Indeed, if there are a steady stream of short processes, the longest processes may never get a chance to run at all.

## Priority Scheduling
Priority scheduling algorithm approach were each process is assigned a priority values, and the CPU is allocated to the process with the highest priority first. This approach ensures that critical and high priority tasks are executed promptly, optimizing the system performanse and responsivenss. This algorithm effectively balances the needs of different processes by giving precedence to thos with higher priority, enabling efficient task managment in various computing enviroments. 

## Round Robin
Round Robin provides process an equal amount of CPU time. It operates on the principale of preemption, where process are temporarly interrupted and moved to the back of the queue after executing for a fixed time quantum. Round Robin is suitable ofr time-sharing systems and interactive enviroments, as it reduces latency and allows for quick context switching between processes. Round Robin can introduce overhead and inefficiencies when dealing with tasks of varying execution times, leading to potential performance degradation. 


## Multilevel Queue
The Multilevel Queue process scheduling algorithm combines multiple approaches in order to try and balance the pros and cons of different methods. In particular, it attempts to balance the fairness of methods like Round Robin with an increased responsiveness for higher priority processes, such as kernel and real time processes. However, the particular implementation here uses the Fixed Priority Preemptive Scheduling Method, which can lead to starvation of lower priority processes.

## Multilevel Feedback Queue
The Multilevel Feedback Queue takes the idea used in the Multilevel Queue of balancing approaches, but focuses on run time to prioritize shorter processes for Shortest Job First style efficiency, with any process that takes too long being given a lower priority. In the implementation here, this priority drop is permanent, though this certainly is not the universal implementation. Whether or not the higher priority queues actually use Round Robin is at some level a matter of perspective, but by downgrading the priority of a process after a set time, they functionally do. The lowest level queue is First Come, First Serve, though as with other scheduling methods that allow preemption, it risks starvation if there are too many quick processes or if a process periodically called IO to stay at a higher priority.

## UML Diagram
![image](https://github.com/robtai29/CSB340_CPUScheduler/assets/61960571/f08de938-7233-44a1-99a0-7efc66ea04b6)


## Averages

|						|FCFS	|SJF	| Priority	|RR		|MLQ	|MLFQ	|
|---					|---	|---	|---		|---	|---	|---	|
|CPU Utilization		|85.34%	|100%	|		78.01%	|	93.87%	|82%	|91.4%	|
|Avg Waiting Time		|185.25	|124.88	|	197.63		|	227.75	|261.38	|156.88	|
|Avg Turnaround Time	|521.37	|461	|	533.80		|	563.88	|597.5	|493	|
|AVG Response Time		|24.37	|29.5	|	72.0		|	45.88	|39.25	|15.75	|

## Individual Process Performance

|    | FCFS |     |    | Priority |     |    | Round Robin |     |    | SJF  |     |    | MLQ  |     |    | MLFQ |     |    |
|----|------|-----|----|----------|-----|----|-------------|-----|----|------|-----|----|------|-----|----|------|-----|----|
|    | CPU  |     |    | CPU:     |     |    | CPU:        |     |    | CPU: |     |    | CPU: |     |    | CPU: |     |    |
|    | Tw   | Ttr | Tr | Tw       | Ttr | Tr | Tw          | Ttr | Tr | Tw   | Ttr | Tr | Tw   | Ttr | Tr | Tw   | Ttr | Tr |
| P1 |  170 | 395 | 0  |   106    | 331 | 27 |      144    | 369 | 0  |  26  | 251 | 11 |  68  | 293 |  0 |  63  | 288 | 0  |
| P2 |  164 | 591 | 5  |   233    | 660 | 70 |      187    | 614 | 5  |  46  | 473 | 3  |  409 | 836 | 35 |  145 | 572 | 5  |
| P3 |  165 | 557 | 9  |   187    | 579 | 35 |      260    | 652 | 9  |  255 | 647 | 16 |  401 | 401 | 39 |  221 | 613 | 9  |
| P4 |  164 | 648 | 17 |   152    | 636 | 32 |      145    | 629 | 14 |  8   | 492 | 0  |  56  | 540 | 4  |  17  | 501 | 14 |
| P5 |  221 | 530 | 20 |   36     | 345 | 0  |      309    | 618 | 17 |  231 | 540 | 123|  132 | 441 | 7  |  253 | 562 | 17 |
| P6 |  230 | 445 | 36 |   78     | 293 | 16 |      250    | 465 | 22 |  61  | 276 | 29 |  132 | 347 | 11 |  191 | 406 | 22 |
| P7 |  184 | 512 | 47 |   456    | 784 | 298|      306    | 634 | 27 |  277 | 605 | 47 |  435 | 763 | 96 |  228 | 556 | 27 |
| P8 |  184 | 493 | 61 |   333    | 642 | 98 |      221    | 530 | 33 |   95 |  404| 7   |  458 | 767 | 122|  137 | 446 | 32 |

## Discussion
Discussion should be spent comparing algorithm performance and deciding on the best solution to implement. Why its the best solution and why not should also be discussed.
