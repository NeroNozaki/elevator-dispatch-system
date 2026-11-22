import java.util.ArrayList;
import java.util.List;

public class Elevator {
    enum Direction {
        UP,
        IDLE,
        DOWN,
    }
    private final char name;
    private int position;
    private Direction direction = Direction.IDLE;
    private List<Integer> destinations = new ArrayList<>();
    private boolean blocked = false;
    private final int max;

    Elevator(char name, int max) {
        this(name, max, randomPosition(max));
    }

    static Elevator at(char name, int maxFloor, int position) {
        return new Elevator(name, maxFloor, position);
    }

    private Elevator(char name, int maxFloor, int position) {
        if (maxFloor <= 0) throw new IllegalArgumentException("max must be greater than 0.");

        if (position < 1 || position > maxFloor) throw new IllegalArgumentException("position must be between 1 and " + maxFloor);

        this.name = name;
        this.max = maxFloor;
        this.position = position;
    }

    private static int randomPosition(int maxFloor) {
        if (maxFloor <= 0) throw new IllegalArgumentException("maxFloor must be greater than 0.");

        return (int)(Math.random() * maxFloor) + 1;
    }

    char getName() { return this.name; }

    int getPosition() { return this.position; }

    boolean isBlocked() { return this.blocked; }

    void setBlocked(boolean value) { this.blocked = value; }

    int getMax() { return this.max; }

    Direction getDirection() { return this.direction; }

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
        if (!isValidFloor(floor)) {
            throw new IllegalArgumentException("number of floor " + floor + " is invalid on request to elevator " + this.name);
        }
        if (this.direction == Direction.IDLE) {
            this.destinations.add(floor);
        } else {
            int index = 0;
            while (index < this.destinations.size()) {
                int existing = this.destinations.get(index);
                boolean insertHere = this.direction == Direction.UP
                    ? floor < existing
                    : floor > existing;
                if (insertHere) {
                    break;
                }
                index++;
            }
            this.destinations.add(index, floor);
        }

        decideDirection();
    }

    void decideDirection() {
        decideDirection(List.of());
    }

    void decideDirection(List<Request> pendingRequests) {
        if (!this.destinations.isEmpty()) {
            int firstStop = this.destinations.getFirst();
            if (firstStop > this.position) {
                this.direction = Direction.UP;
                return;
            }
            if (firstStop < this.position) {
                this.direction = Direction.DOWN;
                return;
            }
        }

        if (!pendingRequests.isEmpty()) {
            this.direction = directionOf(pendingRequests.getFirst());
            return;
        }

        this.direction = Direction.IDLE;
    }

    void move() {
        switch(direction) {
        case Direction.UP:
            if (position >= max) {
                throw new IllegalStateException("Elevator " + name + " at top floor " + position + " cannot move UP");
            }
            position++;
            break;
        case Direction.DOWN:
            if (position <= 1) {
                throw new IllegalStateException("Elevator " + name + " at bottom floor " + position + " cannot move DOWN");
            }
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
        this.destinations.remove(Integer.valueOf(floor.getNumber()));
        decideDirection();
    }

    List<Request> receiveRequests(List<Request> requests) {
        decideDirection(requests);

        List<Request> matching = new ArrayList<>();
        for (Request r : requests) {
            if (directionMatches(r)) {
                matching.add(r);
                addToDestinations(r.destination);
                continue;
            }
        }
        return matching;
    }

    boolean canServe(List<Request> requests) {
        if (requests.isEmpty()) {
            return false;
        }

        if (this.direction == Direction.IDLE) {
            return true;
        }

        for (Request request : requests) {
            if (directionMatches(request)) {
                return true;
            }
        }

        return false;
    }

    private Direction directionOf(Request request) {
        if (request.destination > request.floor) {
            return Direction.UP;
        }

        return Direction.DOWN;
    }

    private boolean directionMatches(Request request) {
        return switch (this.direction) {
            case UP, DOWN -> this.direction == directionOf(request);
            case IDLE -> false;
        };
    }

    private boolean isValidFloor(int floorNum) {
        return floorNum > 0 && floorNum <= max;
    }

    @Override
    public String toString() {
        return "Elevator = [\n\t name: " + this.name + "\n\t position: floor " + this.position + "\n]";
        // return this.position + ", ";
    }
}

