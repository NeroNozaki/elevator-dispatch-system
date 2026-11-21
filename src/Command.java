public class Command {
    enum Type {
        STEP,
        REQUEST,
        EXIT,
        INVALID,
    }
    Type type;
    int floor;
    int destination;
    String error;
}

