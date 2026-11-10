import java.util.ArrayList;
import java.util.List;

public class Elevator {
    enum Direction {
        UP,
        IDLE,
        DOWN,
    }
    private char name;
    private int position;
    private Direction direction = Direction.IDLE;
    private List<Integer> destinations = new ArrayList<>();
    private boolean blocked = false;

    Elevator(char name, int max) {
        this.name = name;
        this.position = (int)(Math.random()*max)+1;
    }

    char getName() { return this.name; }

    int getPosition() { return this.position; }

    boolean isBlocked() { return this.blocked; }

    void setBlocked(boolean value) { this.blocked = value; }

    boolean isMovingAwayFrom(Floor floor) {
		if (this.direction == Direction.UP) {
			return this.position > floor.getNumber();
		} else if (this.direction == Direction.DOWN) {
			return this.position < floor.getNumber();
		} else {
			return false;
		}
    }

    int distanceFromFloor(Floor floor) {
        return Math.abs(this.position - floor.getNumber());
    }

    void addToDestinations(int floor) {
        // this should sort the destinations list based on the direction the elevator is moving
        // ascending for UP, descending for DOWN

        if (this.destinations.isEmpty()) { decideDirection(); }

        switch (direction) {
        case Direction.UP:
            for (int i = 0; i < destinations.size(); i++) {
                if (floor < destinations.get(i)) {
                    destinations.add(i, floor);
                    return;
                }
            }
            destinations.add(floor);
            break;
        case Direction.DOWN:
            for (int i = 0; i < destinations.size(); i++) {
                if (floor > destinations.get(i)) {
                    destinations.add(i, floor);
                    return;
                }
            }
            destinations.add(floor);
            break;
        case Direction.IDLE:
            destinations.add(floor);
            break;
        }
    }

    void decideDirection() {
        int firstStop = this.destinations.getFirst();
        if (firstStop > this.position) {
            this.direction = Direction.UP;
        } else if (firstStop < this.position) {
            this.direction = Direction.DOWN;
        } else {
            this.direction = Direction.IDLE;
        }
    }

    void move() {
        switch(direction) {
        case Direction.UP:
            position++;
            break;
        case Direction.DOWN:
            position--;
            break;
        case Direction.IDLE:
            break;
        }
    }

    boolean hasArrived() {
        return !destinations.isEmpty() && destinations.get(0) == position;
    }

    void removeDestination(Floor floor) {
        if (destinations.contains(floor.getNumber())) {
            destinations.remove(floor.getNumber());
        }
    }

    Request[] receiveRequests(List<Request> requests) {
        List<Request> nonMatching = new ArrayList<>();
        for (Request r : requests) {
            if (directionMatches(r)) {
                addToDestinations(r.destination);
                continue;
            }
            nonMatching.add(r);
        }
        return nonMatching.toArray(Request[]::new);
    }

    private boolean directionMatches(Request request) {
        if (
        request.destination > this.position && this.direction == Direction.UP ||
        request.destination < this.position && this.direction == Direction.DOWN
    )
    { return true; }

        return false;
    }

    @Override
    public String toString() {
        return "Elevator = [\n\t name: " + this.name + "\n\t position: floor " + this.position + "\n]";
        // return this.position + ", ";
    }
}

