import java.util.List;
import java.util.NoSuchElementException;
import java.util.ArrayList;
import java.util.Scanner;

public class ElevatorSystem {
    private final List<Elevator> elevators = new ArrayList<>();
    private final List<Floor> floors = new ArrayList<>();
    private final Scanner scanner = new Scanner(System.in);

    ElevatorSystem(int elevatorNum, int floorNum) {
        if (floorNum <= 0 || elevatorNum <= 0) {
            throw new IllegalArgumentException("System must be created with a positive number of elevators and floors.");
        }

        for (int i = 0; i < elevatorNum; i++) {
            elevators.add(new Elevator((char) ('A' + i), floorNum));
            // System.out.println("Elevator " + elevators.get(i).getName() + " created.");
        }

        for (int i = 0; i < floorNum; i++) {
            floors.add(new Floor(i + 1));
            // System.out.println("Floor number " + floors.get(i).getNumber() + "
            // created.");
        }
    }

    void begin() {
        System.out.println("Elevator dispatch system simulation.");
        System.out.println("Type in requests in this format [1, 2]");
        System.out.println("where 1 is where the request comes from and 2 is the destination");
        System.out.println("Press Enter with nothing typed in to advance one step");
        System.out.println();
        System.out.println();

        printSystem();
        boolean running = true;
        try {
            while (running)
                running = handleInput();
        } catch (NoSuchElementException e) {
            System.out.println("Input stream ended.");
        } finally {
            scanner.close();
        }
    }

    boolean handleInput() {
        if (!scanner.hasNextLine()) {
            return false;
        }
        String input = scanner.nextLine();
        Command command = parseInput(input);

        switch (command.type) {
            case STEP:
                step();
                break;
            case REQUEST:
                if (!makeRequest(command.request)) {
                    System.err.println("Request ignored, simulation continues.");
                }
                break;
            case EXIT:
                return false;
            case INVALID:
                break;

            default:
                System.err.println("Some bullshit has ocurred.");
                break;
        }

        return true;
    }

    Command parseInput(String input) {
        Command command = new Command();
        String trimmed = input.trim();

        if (trimmed.isEmpty()) {
            command.type = Command.Type.STEP;
        } else if (trimmed.equals("exit")) {
            command.type = Command.Type.EXIT;
        } else if (trimmed.matches("\\[\\s*\\d+\\s*,\\s*\\d+\\s*\\]")) {
            command.type = Command.Type.REQUEST;
            command.request = trimmed.substring(1, trimmed.length() - 1).trim();
        } else if (trimmed.matches("\\d+\\s*,\\s*\\d+")) {
            command.type = Command.Type.REQUEST;
            command.request = trimmed;
        } else {
            command.type = Command.Type.INVALID;
        }

        return command;
    }

    boolean makeRequest(String request) {
        String[] parts = request.trim().split("\\s*,\\s*");

        int floor, destination;
        try {
            floor = Integer.parseInt(parts[0].trim());
            destination = Integer.parseInt(parts[1].trim());
        } catch (NumberFormatException e) {
            System.err.println("Floor numbers are too large or malformed.");
            return false;
        }

        if (!isValidFloor(floor) || !isValidFloor(destination)) {
            System.err.println("Numbers must be between 1 and " + floors.size());
            return false;
        }

        Request finalRequest;
        try {
            finalRequest = new Request(floor, destination);
        } catch (IllegalArgumentException e) {
            System.err.println("Floor number cannot be the same as destination and neither can be <= 0.");
            return false;
        }

        floors.get(floor - 1).addRequest(finalRequest);
        return true;
    }

    private boolean isValidFloor(int n) {
        return n >= 1 && n <= floors.size();
    }

    void checkFloors() {
        for (Floor floor : floors) {
            if (floor.hasRequests() && !floor.isWaiting()) {
                boolean result = callElevator(floor);
                if (result)
                    floor.setWaiting(true);
            } else {
                continue;
            }
        }
    }

    void sortElevators(List<Elevator> elevators, Floor floor) {
        // look at each elevator in the list and the previous. swap them if the current
        // is closer than the previous
        boolean swapped = false;
        for (int i = 1; i < elevators.size(); i++) {
            Elevator prevElevator = elevators.get(i - 1);
            Elevator currElevator = elevators.get(i);
            int currElevatorDistance = currElevator.distanceFromFloor(floor);
            int prevElevatorDistance = prevElevator.distanceFromFloor(floor);
            if ((currElevatorDistance < prevElevatorDistance)
                    ||
                    (currElevatorDistance == prevElevatorDistance &&
                            currElevator.getName() < prevElevator.getName())) {
                elevators.set(i - 1, currElevator);
                elevators.set(i, prevElevator);
                swapped = true;
            }

            if (i == elevators.size() - 1 && swapped) {
                i = 0;
                swapped = false;
            }
        }
    }

    List<Elevator> getAvailableElevators(Floor floor) {
        List<Elevator> available = new ArrayList<>();
		for (Elevator elevator : elevators) {
			if (!elevator.isMovingAwayFrom(floor) && elevator.canServe(floor.getRequests())) {
				available.add(elevator);
			}
		}
        sortElevators(available, floor);

        return available;
    }

    boolean callElevator(Floor floor) {
        // this could error because getAvailableElevators() can return an empty list.

        List<Elevator> availableElevators = getAvailableElevators(floor);

        if (availableElevators.isEmpty()) {
            return false;
        }

        Elevator chosenElevator = availableElevators.get(0);
        try {
        chosenElevator.addToDestinations(floor.getNumber());
        } catch (IllegalArgumentException e) {
            System.err.println(e);
            return false;
        }
        return true;
    }

    void handleArrival(Elevator elevator) {
        // deal with the elevator arriving.
        // remove current floor from destinations.
        // look at the floor requests and add to destination list
        // ONLY if they match current direction.

        int position = elevator.getPosition();
        if (position < 1 || position > floors.size()) {
            throw new IllegalStateException("Elevator position " + position + " out of bounds.");
        }

        Floor floor = floors.get(elevator.getPosition() - 1);
        elevator.removeDestination(floor);
        List<Request> nonMatching = elevator.receiveRequests(floor.getRequests());
        floor.removeRequests(nonMatching, false);
        floor.setWaiting(false);
    }

    void step() {
        // TODO: handle the edge case where a request can't be assigned to an IDLE
        // Elevator
        // which is when multiple Requests with the same level of priority come in the
        // same step().
        // to do this, choose the Request from the closest Floor, prioritizing Floors
        // below in ties.

        checkFloors();
        for (Elevator e : elevators) {
            if (e.isBlocked()) {
                e.setBlocked(false);
                continue;
            }
            e.move();
            if (e.hasArrived()) {
                handleArrival(e);
                e.setBlocked(true);
            }
        }
        printSystem();
    }

    void printSystem() {
        System.out.println("Visualization unimplemented.");
        System.out.println("We're working on it boss!");
    }

}
