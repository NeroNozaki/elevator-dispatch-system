import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;

class ElevatorSystem {
    List<Elevator> elevators;
    List<Floor> floors;

    ElevatorSystem(int elevatorNum, int floorNum) {
        elevators = new ArrayList<>();
        floors = new ArrayList<>();
        for (int i = 0; i < elevatorNum; i++) {
            elevators.add(new Elevator((char)('A'+i)));
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
        System.out.println("Press Enter with nothing typed in to avance one step");

        handleInput();
    }

    void handleInput() {
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        scanner.close();
        Command command = parseInput(input);

        switch (command.type) {
        case STEP:
            step();
            break;
        case REQUEST:
            makeRequest(command.request);
            break;
        case EXIT:
            exit();
            break;

        default:
            break;
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

    List<Elevator> sortElevators(Floor floor) {
		List<Elevator> sortedElevators = new ArrayList<>();
		for (Elevator elevator : elevators) {
			if (!elevator.isMovingAwayFrom(floor)) {
				sortedElevators.add(elevator);
			}
		}
		
		// after having the available elevators, sort them by position.
		// after sorting them by position, if there are any ties, sort based on the position of this.elevators
		
		return sortedElevators;
 	}

    void callElevator(Floor floor) {
        List<Elevator> elevatorsSorted = sortElevators(floor);
    }

    void step() {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    void exit() {
        throw new UnsupportedOperationException("Not implemented yet.");
    }
}
