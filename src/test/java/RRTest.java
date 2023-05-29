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
        //order of completion: P1, P2
        String inputFile = "src/main/resources/testinput";
        RR r1 = new RR(inputFile, "test", false);
        r1.process();
        LinkedList<Process> list = r1.getProcesses();
        Assertions.assertEquals(2, list.size());
        Assertions.assertEquals("P1", list.get(0).getStrName());
        Assertions.assertEquals("P2", list.get(1).getStrName());
        //...
    }
}