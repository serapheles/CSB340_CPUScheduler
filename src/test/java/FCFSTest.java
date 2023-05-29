import com.bears.utility.Process;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;



class FCFSTest {
    private FCFS f1;


    @Test
    void checkOrderOfCompletion() {
        //order of completion: P1	P6	P8	P7	P5	P3	P2	P4
        String inputFile = "src/main/resources/input";
        FCFS f1 = new FCFS(inputFile, "FCFS_output", false);
        f1.process();
        LinkedList<Process> list = f1.getProcesses();
        Assertions.assertEquals(8, list.size());
        Assertions.assertEquals("P1", list.get(0).getStrName());
        Assertions.assertEquals("P6", list.get(1).getStrName());
        Assertions.assertEquals("P8", list.get(2).getStrName());
        Assertions.assertEquals("P7", list.get(3).getStrName());
        Assertions.assertEquals("P5", list.get(4).getStrName());
        Assertions.assertEquals("P3", list.get(5).getStrName());
        Assertions.assertEquals("P2", list.get(6).getStrName());
        Assertions.assertEquals("P4", list.get(7).getStrName());

    }

    @Test
    void checkWaitingTime() {
        //order of completion: P1	P6	P8	P7	P5	P3	P2	P4
//        Waiting Time		P1		P2		P3		P4		P5		P6		P7		P8
//                       170		164		165		164		221		230		184		184
        String inputFile = "src/main/resources/input";
        FCFS f1 = new FCFS(inputFile, "FCFS_output", false);
        f1.process();
        LinkedList<Process> list = f1.getProcesses();
        Assertions.assertEquals(8, list.size());
        Assertions.assertEquals(170, list.get(0).getWaitingTime());
        Assertions.assertEquals(230, list.get(1).getWaitingTime());
        Assertions.assertEquals(184, list.get(2).getWaitingTime());
        Assertions.assertEquals(184, list.get(3).getWaitingTime());
        Assertions.assertEquals(221, list.get(4).getWaitingTime());
        Assertions.assertEquals(165, list.get(5).getWaitingTime());
        Assertions.assertEquals(164, list.get(6).getWaitingTime());
        Assertions.assertEquals(164, list.get(7).getWaitingTime());

    }

    @Test
    void checkTurnaroundTime() {
        //order of completion: P1	P6	P8	P7	P5	P3	P2	P4
        //        Waiting Time		P1		P2		P3		P4		P5		P6		P7		P8
        //                         395		591		557		648		530		445		512		493
        String inputFile = "src/main/resources/input";
        FCFS f1 = new FCFS(inputFile, "FCFS_output", false);
        f1.process();
        LinkedList<Process> list = f1.getProcesses();
        Assertions.assertEquals(8, list.size());
        Assertions.assertEquals(395, list.get(0).getTurnAroundTime());
        Assertions.assertEquals(445, list.get(1).getTurnAroundTime());
        Assertions.assertEquals(493, list.get(2).getTurnAroundTime());
        Assertions.assertEquals(512, list.get(3).getTurnAroundTime());
        Assertions.assertEquals(530, list.get(4).getTurnAroundTime());
        Assertions.assertEquals(557, list.get(5).getTurnAroundTime());
        Assertions.assertEquals(591, list.get(6).getTurnAroundTime());
        Assertions.assertEquals(648, list.get(7).getTurnAroundTime());

    }

    @Test
    void checkResponseTime() {
        //order of completion: P1	P6	P8	P7	P5	P3	P2	P4
//        Response Time		P1		P2		P3		P4		P5		P6		P7		P8
//                          0		5		9		17		20		36		47		61
        String inputFile = "src/main/resources/input";
        FCFS f1 = new FCFS(inputFile, "FCFS_output", false);
        f1.process();
        LinkedList<Process> list = f1.getProcesses();
        Assertions.assertEquals(8, list.size());
        Assertions.assertEquals(0, list.get(0).getResponseTime());
        Assertions.assertEquals(36, list.get(1).getResponseTime());
        Assertions.assertEquals(61, list.get(2).getResponseTime());
        Assertions.assertEquals(47, list.get(3).getResponseTime());
        Assertions.assertEquals(20, list.get(4).getResponseTime());
        Assertions.assertEquals(9, list.get(5).getResponseTime());
        Assertions.assertEquals(5, list.get(6).getResponseTime());
        Assertions.assertEquals(17, list.get(7).getResponseTime());

    }

}