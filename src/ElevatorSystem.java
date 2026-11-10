import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;

class ElevatorSystem {
    List<Elevator> elevators = new ArrayList<>();
    List<Floor> floors = new ArrayList<>();

    ElevatorSystem(int elevatorNum, int floorNum) {
        for (int i = 0; i < elevatorNum; i++) {
            elevators.add(new Elevator((char)('A'+i), floorNum));
            // System.out.println("Elevator " + elevators.get(i).getName() + " created.");
        }
        for (int i = 0; i < floorNum; i++) {
            floors.add(new Floor(i+1));
            // System.out.println("Floor number " + floors.get(i).getNumber() + " created.");
        }
    }

    void begin() {
        System.out.println("Elevator dispatch system simulation.");
        System.out.println("Type in requests in this format [1, 2]");
        System.out.println("where 1 is where the request comes from and 2 is the destination");
        System.out.println("Press Enter with nothing typed in to advance one step");

        boolean running = true;
        while (running) {
            running = handleInput();
        }
    }

    boolean handleInput() {
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        scanner.close();
        Command command = parseInput(input);

        switch (command.type) {
        case STEP:
            step();
            return true;
        case REQUEST:
            makeRequest(command.request);
            return true;
        case EXIT:
            exit();
            return false;

        default:
            System.err.println("Some bullshit has ocurred.");
            return false;
        }
    }

    Command parseInput(String input) {
        Command command = new Command();

        if (input.equals("exit")) {
            command.type = Command.Type.EXIT;
        } else if (input.matches("\\d+,\\s*\\d+")) {
            command.type = Command.Type.REQUEST;
            command.request = input;
        } else {
            command.type = Command.Type.STEP;
        }

        return command;
    }

    void makeRequest(String request) {
        String[] parts = request.split(",");

        int floor = Integer.parseInt(parts[0].trim());
        int destination = Integer.parseInt(parts[1].trim());
        floors.get(floor-1).addRequest(new Request(floor, destination));
    }

    void checkFloors() {
		for (Floor floor : floors) {
			if (floor.hasRequests() && !floor.isWaiting()) {
				callElevator(floor);
				floor.toggleWaiting();
			} else {
				continue;
			}
		}
    }

    void sortElevators(List<Elevator> elevators, Floor floor) {
        // look at each elevator in the list and the previous. swap them if the current is closer than the previous
        boolean swapped = false;
        for (int i = 1; i < elevators.size(); i++) {
            Elevator prevElevator = elevators.get(i-1);
            Elevator currElevator = elevators.get(i);
            int currElevatorDistance = currElevator.distanceFromFloor(floor);
            int prevElevatorDistance = prevElevator.distanceFromFloor(floor);
            if ((currElevatorDistance < prevElevatorDistance)
                ||
                (currElevatorDistance == prevElevatorDistance &&
                 currElevator.getName() < prevElevator.getName()))
                {
                    elevators.set(i-1, currElevator);
                    elevators.set(i, prevElevator);
                    swapped = true;
                }

            if (i == elevators.size()-1 && swapped) {
                i = 0;
                swapped = false;
            }
        }
 	}

    List<Elevator> getAvailableElevators(Floor floor) {
		List<Elevator> available = new ArrayList<>();
		for (Elevator elevator : elevators) {
			if (!elevator.isMovingAwayFrom(floor)) {
				available.add(elevator);
			}
		}
        sortElevators(available, floor);

		return available;
    }

    void callElevator(Floor floor) {
        // this could error because getAvailableElevators() can return an empty list.
        Elevator chosenElevator = getAvailableElevators(floor).get(0);
        chosenElevator.addToDestinations(floor.getNumber());
    }

    void handleArrival(Elevator elevator) {
        // deal with the elevator arriving.
        // remove current floor from destinations.
        // look at the floor requests and add to destination list
        // ONLY if they match current direction.
        Floor floor = floors.get(elevator.getPosition()-1);
        elevator.removeDestination(floor);
        Request[] nonMatching = elevator.receiveRequests(floor.getRequests());
        floor.removeRequests(nonMatching);
        floor.toggleWaiting();
    }

    void step() {
        // TODO: handle the edge case where a request can't be assigned to an IDLE Elevator
        // which is when multiple Requests with the same level of priority come in the same step().
        // to do this, choose the Request from the closest Floor, prioritizing Floors below in ties.

        checkFloors();
        for (Elevator e : elevators) {
            if (e.isBlocked()) { continue; }
            e.move();
            e.setBlocked(false);
            if (e.hasArrived()) {
                handleArrival(e);
                e.setBlocked(true);
            }
        }
        printSystem();
        handleInput();
    }

    void printSystem() {
        System.out.println("Visualization unimplemented.");
        System.out.println("We're working on it boss!");
    }

    void exit() {
    }
}
