public class Client {
    public static void main(String[] args){
        System.out.println("hello world");
        String inputFile = "src/main/resources/input";
        boolean outputYes = true;

//        FCFS f1 = new FCFS(inputFile, "FCFS_output", outputYes);
//        f1.process();

//        Priority p1 = new Priority("src/main/resources/testinput", "Priority_output", false, new int[]{3, 6, 5, 4, 1, 2, 8, 7});
//        p1.process();

        RR r1 = new RR();
        r1.process();




    }
}
