public class Request {
    final int floor;
    final int destination;

    Request(int floor, int destination) {
        if (floor == destination || floor <= 0 || destination <= 0) {
            throw new IllegalArgumentException();
        }
        this.floor = floor;
        this.destination = destination;
    }
}
