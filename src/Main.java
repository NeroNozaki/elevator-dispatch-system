public class Main {
    public static void main(String[] args) {
        ElevatorSystem system = new ElevatorSystem(5, 20);
        Simulation sim = new Simulation(system);

        if (args.length == 0) {
            sim.run();
        } else if (args.length == 2 && args[0].equals("--demo")) {
            int demoNumber;
            try {
                demoNumber = Integer.parseInt(args[1].trim());
            } catch (NumberFormatException e) {
                System.err.println("Demo number must be an integer");
                return;
            }
            try {
                sim.runDemo(demoNumber);
            } catch (IllegalArgumentException e) {
                System.err.println(e.getMessage());
                return;
            }
        } else if (args.length == 1 && args[0].equals("--help")) {
            System.out.println("""
                Interactive mode:
                java Main

                Demo mode:
                java Main --demo <number>

                Available demos: 1-5 (see README.md)
                """);
            return;
        } else {
            System.err.println("Usage: java Main [--demo <number>]");
            return;
        }
        System.out.println("Program executed successfully ^_^");
    }
}
