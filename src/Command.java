public class Command {
    enum Type {
        STEP,
        REQUEST,
        EXIT,
    }
    Type type;
    String request;
}

