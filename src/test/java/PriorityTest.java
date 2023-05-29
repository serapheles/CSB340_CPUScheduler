import com.bears.utility.Process;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;

class PriorityTest {
    private Priority p1;

    @Test
    void checkOrderofCompletion() {
        //order of completion: P1, P2
        String inputFile = "src/main/resources/testinput";
        Priority p1 = new Priority(inputFile, "test", false, new int[]{1,2});
        p1.process();
        LinkedList<Process> list = p1.getProcesses();
        Assertions.assertEquals(2, list.size());
        Assertions.assertEquals("P1", list.get(0).getStrName());
        Assertions.assertEquals("P2", list.get(1).getStrName());
        //...
    }

    @Test
    void checkWaitingTime() {
//        //order of completion: P1	P6	P8	P7	P5	P3	P2	P4
//        String inputFile = "src/main/resources/input";
//        FCFS f1 = new FCFS(inputFile, "FCFS_output", false);
//        f1.process();
//        LinkedList<Process> list = f1.getProcesses();
//        Assertions.assertEquals(8, list.size());
//        Assertions.assertEquals(170, list.get(0).getWaitingTime());
    }


}