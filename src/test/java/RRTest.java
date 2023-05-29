import com.bears.utility.Process;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;

class RRTest {
    private RR r1;

    @Test
    void checkOrderofCompletion() {
        //order of completion: P1	P6	P8	P2	P5	P4	P7	P3
        String inputFile = "src/main/resources/input";
        RR r1 = new RR(inputFile, "RR_output", false);
        r1.process();
        LinkedList<Process> list = r1.getProcesses();
        Assertions.assertEquals(8, list.size());
        Assertions.assertEquals("P1", list.get(0).getStrName());
        Assertions.assertEquals("P6", list.get(1).getStrName());
        Assertions.assertEquals("P8", list.get(2).getStrName());
        Assertions.assertEquals("P2", list.get(3).getStrName());
        Assertions.assertEquals("P5", list.get(4).getStrName());
        Assertions.assertEquals("P4", list.get(5).getStrName());
        Assertions.assertEquals("P7", list.get(6).getStrName());
        Assertions.assertEquals("P3", list.get(7).getStrName());

    }

    @Test
    void checkWaitingTime() {
        String inputFile = "src/main/resources/input";
        RR r1 = new RR(inputFile, "RR_output", false);
        r1.process();
        LinkedList<Process> list = r1.getProcesses();
        Assertions.assertEquals(8, list.size());
        Assertions.assertEquals(144, list.get(0).getWaitingTime());
        Assertions.assertEquals(250, list.get(1).getWaitingTime());
        Assertions.assertEquals(221, list.get(2).getWaitingTime());

    }
}