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

    Elevator(char name) {
        this.name = name;
        this.position = (int)(Math.random()*20)+1;
    }

    char getName() {
        return this.name;
    }

    Boolean isMovingAwayFrom(Floor floor) {
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

    @Override
    public String toString() {
        return "Elevator = [\n\t name: " + this.name + "\n\t position: floor " + this.position + "\n]";
        // return this.position + ", ";
    }
}

