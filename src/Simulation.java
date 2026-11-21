import java.util.NoSuchElementException;
import java.util.Scanner;

public class Simulation {
    ElevatorSystem system;
    Scanner scanner;
    CommandParser parser;

    Simulation(ElevatorSystem system) {
        this(system, new Scanner(System.in));
    }

    Simulation(ElevatorSystem system, Scanner scanner) {
        this.system = system;
        this.scanner = scanner;
        this.parser = new CommandParser();
    }

    void run() {
        printIntro();
        printSystem();

        try {
            while (scanner.hasNextLine()) {
                if (!handleInput(scanner.nextLine())) {
                    break;
                }
            }
        } catch (NoSuchElementException e) {
            System.out.println("Input stream ended.");
        } finally {
            scanner.close();
        }
    }

    boolean handleInput(String line) {
        Command command = parser.parse(line);

        switch (command.type) {
            case STEP:
                system.step();
                printSystem();
                break;
            case REQUEST:
                try {
                    system.submitRequest(command.floor, command.destination);
                } catch (IllegalArgumentException e) {
                    System.err.println(e.getMessage());
                }
                printSystem();

                break;
            case EXIT:
                return false;
            case INVALID:
                if (command.error != null) {
                    System.err.println(command.error);
                }
                break;

            default:
                System.err.println("Some bullshit has ocurred.");
                break;
        }

        return true;
    }


    void printIntro() {
        System.out.println("Elevator dispatch system simulation.");
        System.out.println("Type in requests in this format [1, 2]");
        System.out.println("where 1 is where the request comes from and 2 is the destination");
        System.out.println("Press Enter with nothing typed in to advance one step");
        System.out.println();
        System.out.println();
    }

    void printSystem() {
        System.out.println("Visualization not implemented yet.");
        System.out.println("We're working on it boss!");
    }
}
