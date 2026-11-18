public class Command {
    enum Type {
        STEP,
        REQUEST,
        EXIT,
        INVALID,
    }
    Type type;
    String request;
}

