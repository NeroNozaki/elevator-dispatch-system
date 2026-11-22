import java.util.List;
import java.util.ArrayList;

public class ElevatorSystem {
    private final List<Elevator> elevators = new ArrayList<>();
    private final List<Floor> floors = new ArrayList<>();
    private final RequestMaker requests;

    ElevatorSystem(int elevatorNum, int floorNum) {
        this(floorNum, createRandomElevators(elevatorNum, floorNum));
    }

    ElevatorSystem(int floorNum, List<Elevator> initialElevators) {
        if (floorNum <= 0) throw new IllegalArgumentException("floorNum must be greater than 0.");
        if (initialElevators.isEmpty()) throw new IllegalArgumentException("at least one elevator is required.");

        for (Elevator e : initialElevators) {
            if (e.getPosition() < 1 || e.getPosition() > floorNum) throw new IllegalArgumentException("Elevator " + e.getName() + " starts outside the building.");

            if (e.getMax() != floorNum) throw new IllegalArgumentException("Elevator maximum value must match the number of floors.");
        }

        this.elevators.addAll(initialElevators);

        for (int i = 0; i < floorNum; i++) {
            this.floors.add(new Floor(i + 1));
        }

        this.requests = new RequestMaker(floorNum);
    }

    private static List<Elevator> createRandomElevators(int elevatorNum, int floorNum) {
        if (elevatorNum <= 0) throw new IllegalArgumentException("elevatorNum must be greater than 0.");

        List<Elevator> result = new ArrayList<>();

        for (int i = 0; i < elevatorNum; i++) {
            result.add(new Elevator((char)('A' + i), floorNum));
        }

        return result;
    }

    int getFloorCount() { return floors.size(); }

    List<Elevator> getElevators() { return List.copyOf(elevators); }
    List<Floor> getFloors() { return List.copyOf(floors); }


    void submitRequest(int floor, int destination) {
        Request request = requests.create(floor, destination);

        if (request.floor > floors.size() || request.destination > floors.size()) {
            throw new IllegalStateException("Request refers to a floor outside this system.");
        }

        floors.get(request.floor - 1).addRequest(request);
    }

    Floor getFloor(int number) {
        if (number < 1 || number > floors.size()) {
            throw new IllegalArgumentException("Floor " + number + " is outside this system.");
        }

        return floors.get(number - 1);
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

    void sortElevators(List<Elevator> availableElevators, Floor floor) {
        // look at each elevator in the list and the previous. swap them if the current
        // is closer than the previous
        boolean swapped = false;
        for (int i = 1; i < availableElevators.size(); i++) {
            Elevator prevElevator = availableElevators.get(i - 1);
            Elevator currElevator = availableElevators.get(i);

            int currElevatorDistance = currElevator.distanceFromFloor(floor);
            int prevElevatorDistance = prevElevator.distanceFromFloor(floor);

            if ((currElevatorDistance < prevElevatorDistance)
                    ||
                    (currElevatorDistance == prevElevatorDistance &&
                            currElevator.getName() < prevElevator.getName())) {
                availableElevators.set(i - 1, currElevator);
                availableElevators.set(i, prevElevator);
                swapped = true;
            }

            if (i == availableElevators.size() - 1 && swapped) {
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

        List<Elevator> availableElevators = getAvailableElevators(floor);

        if (availableElevators.isEmpty()) { return false; }

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
        List<Request> matching = elevator.receiveRequests(floor.getRequests());
        floor.removeRequests(matching);
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
    }


}
