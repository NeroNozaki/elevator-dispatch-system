public class RequestMaker {
    private final int floorCount;

    RequestMaker(int floorCount) {
        if (floorCount <= 0) {
            throw new IllegalArgumentException("floorCount must be greater than 0.");
        }

        this.floorCount = floorCount;
    }

    Request create(int floor, int destination) {
        if (!isValidFloor(floor) || !isValidFloor(destination)) {
            throw new IllegalArgumentException("Numbers must be between 1 and " + floorCount + ".");
        }

        if (floor == destination) {
            throw new IllegalArgumentException("Floor and destination must be different.");
        }

        return new Request(floor, destination);
    }

    private boolean isValidFloor(int n) {
        return n >= 1 && n <= floorCount;
    }
}

