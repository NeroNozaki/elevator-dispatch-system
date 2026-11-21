public class Main {
    public static void main(String[] args) {
        ElevatorSystem system = new ElevatorSystem(5, 20);
        Simulation sim = new Simulation(system);
        sim.run();

        System.out.println("Program executed successfully ^_^");
    }
}
