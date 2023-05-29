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
//order of completeion: P6	P1	P5	P3	P4	P8	P2	P7
        String inputFile = "src/main/resources/input";
        Priority p1 = new Priority(inputFile, "Priority_output", false, new int[]{3, 6, 5, 4, 1, 2, 8, 7});
        p1.process();
        LinkedList<Process> list = p1.getProcesses();
        Assertions.assertEquals(8, list.size());
        Assertions.assertEquals("P6", list.get(0).getStrName());
        Assertions.assertEquals("P1", list.get(1).getStrName());
        Assertions.assertEquals("P5", list.get(2).getStrName());
        Assertions.assertEquals("P3", list.get(3).getStrName());
        Assertions.assertEquals("P4", list.get(4).getStrName());
        Assertions.assertEquals("P8", list.get(5).getStrName());
        Assertions.assertEquals("P2", list.get(6).getStrName());
        Assertions.assertEquals("P7", list.get(7).getStrName());
    }

    @Test
    void checkWaitingTime() {
        String inputFile = "src/main/resources/input";
        Priority p1 = new Priority(inputFile, "Priority_output", false, new int[]{3, 6, 5, 4, 1, 2, 8, 7});
        p1.process();
        LinkedList<Process> list = p1.getProcesses();
        Assertions.assertEquals(8, list.size());
        Assertions.assertEquals(78, list.get(0).getWaitingTime());
        Assertions.assertEquals(106, list.get(1).getWaitingTime());
        Assertions.assertEquals(36, list.get(2).getWaitingTime());

    }


}