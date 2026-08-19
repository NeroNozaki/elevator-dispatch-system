import java.util.List;

public class Floor {
    private int number;
    private List<Request> requests;
    private Boolean waiting;

    Floor(int number) {
        this.number = number;
    }
    int getNumber() {
        return this.number;
    }
    void addRequest(Request request) {
        requests.add(request);
    }
    Boolean hasRequests() {
        return !this.requests.isEmpty();
    }
    Boolean isWaiting() {
        return this.waiting;
    }
    void toggleWaiting() {
        waiting = !waiting;
    }
}
