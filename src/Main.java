public class Main {
    public static void main(String[] args) {

        run();

        System.out.println("Program executed successfully ^_^");
    }

    public static void run() {
        ElevatorSystem system = new ElevatorSystem(5, 20);
        system.begin();

    }

    public static void test() {
        Test.run();
    }
}
