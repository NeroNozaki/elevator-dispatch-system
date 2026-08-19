public class Main {
    public static void main(String[] args) {
        
        ElevatorSystem system = new ElevatorSystem(5, 20);
        for (Elevator elevator : system.elevators) {
            System.out.println(elevator);
        }
        System.out.println("Program executed successfully ^_^");
    }
}
