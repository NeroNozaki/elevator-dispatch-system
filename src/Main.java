public class Main {
    public static void main(String[] args) {
        
        ElevatorSystem system = new ElevatorSystem(5, 20);
        system.callElevator(system.floors.get(1));

        System.out.println("Program executed successfully ^_^");
    }
}
