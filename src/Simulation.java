import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.List;

public class Simulation {
    ElevatorSystem system;
    Scanner scanner;
    CommandParser parser;
    String status = "";

    // =================================================
    private static final String ESC = "\033[";
    private static final String ALT_ON = ESC + "?1049h";
    private static final String ALT_OFF = ESC + "?1049l";
    private static final String HIDE = ESC + "?25l";
    private static final String SHOW = ESC + "?25h";
    private static final String HOME = ESC + "H";
    private static final String CLEAR = ESC + "2J";
    private static final String CLEAR_TO_END = ESC + "J";
    private static final String ERASE_LINE = ESC + "K";
    // =================================================

    Simulation(ElevatorSystem system) {
        this(system, new Scanner(System.in));
    }

    Simulation(ElevatorSystem system, Scanner scanner) {
        this.system = system;
        this.scanner = scanner;
        this.parser = new CommandParser();
    }

    void enterScreen() {
        System.out.print(ALT_ON + HOME + CLEAR);
        System.out.flush();
    }

    void restoreTerminal() {
        System.out.print(SHOW + ALT_OFF);
        System.out.flush();
    }

    void run() {
        enterScreen();

        boolean inputEnded = false;
        try {
            printSystem();
            while (scanner.hasNextLine()) {
                if (!handleInput(scanner.nextLine())) {
                    break;
                }
            }
        } catch (NoSuchElementException e) {
            inputEnded = true;
        } finally {
            restoreTerminal();
            scanner.close();
        }

        if (inputEnded) {
            System.out.println("Input stream ended.");
        }
    }

    void runDemo(int demoNumber) {
        DemoCatalogue.load(demoNumber, system);
        status = "Demo " + demoNumber + " | Press Enter to exit the demo.";
        enterScreen();

        CountDownLatch exit = new CountDownLatch(1);

        Thread inputThread = new Thread(() -> {
            try {
                if (scanner.hasNextLine()) {
                    scanner.nextLine();
                }
            } catch (RuntimeException ignored) {
            } finally {
                exit.countDown();
            }
        });

        inputThread.setDaemon(true);
        inputThread.start();

        try {
            while (true) {
                printSystem();

                if (exit.await(1, TimeUnit.SECONDS)) {
                    break;
                }

                system.step();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            restoreTerminal();
        }
    }

    boolean handleInput(String line) {
        Command command = parser.parse(line);
        status = "";

        switch (command.type) {
            case STEP:
                system.step();
                printSystem();
                break;
            case REQUEST:
                try {
                    system.submitRequest(command.floor, command.destination);
                } catch (IllegalArgumentException e) {
                    status = e.getMessage();
                }
                printSystem();
                break;
            case EXIT:
                return false;
            case INVALID:
                status = command.error == null ? "Invalid command." : command.error;
                printSystem();
                break;
            default:
                status = "Unknown command.";
                printSystem();
                break;
        }

        return true;
    }

    String buildFrame() {
        List<Elevator> elevators = system.getElevators();
        List<Floor> floors = system.getFloors();
        int width = Integer.toString(system.getFloorCount()).length();
        StringBuilder frame = new StringBuilder();

        frame.append("Elevator Dispatch System   \n");
        frame.append("Enter: step   [floor, destination]: request   exit: quit\n");

        for (int floor = system.getFloorCount(); floor > 0; floor--) {
            String label = String.format("%0" + width + "d", floor);
            frame.append(label).append(" | ");

            for (Elevator elevator : elevators) {
                String direction = switch (elevator.getDirection()) {
                    case UP -> "↑";
                    case DOWN -> "↓";
                    case IDLE -> "–";
                };

                if (elevator.getPosition() == floor) {
                    frame.append(elevator.getName()).append(direction).append(' ');
                } else {
                    frame.append(".. ");
                }
            }

            frame.append("| ").append(label).append(": ");

            for (Request request : floors.get(floor - 1).getRequests()) {
                String destination = String.format("%0" + width + "d", request.destination);
                frame.append('[').append(destination).append("] ");
            }

            frame.append('\n');
        }

        frame.append(status).append('\n');
        return frame.toString();
    }

    String renderFrame() {
        StringBuilder output = new StringBuilder(HIDE + HOME);
        String[] lines = buildFrame().split("\n", -1);

        for (int i = 0; i < lines.length; i++) {
            if (i > 0) {
                output.append('\n');
            }
            output.append(lines[i]).append(ERASE_LINE);
        }

        output.append(CLEAR_TO_END).append(SHOW);
        return output.toString();
    }

    void printSystem() {
        System.out.print(renderFrame());
        System.out.print("> ");
        System.out.flush();
    }
}
