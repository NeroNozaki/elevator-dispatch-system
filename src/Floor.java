import java.util.ArrayList;
import java.util.List;

public class Floor {
    private int number;
    private List<Request> requests = new ArrayList<>();
    private Boolean waiting = false;

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

    List<Request> getRequests() {
        return this.requests;
    }

    void removeRequests(Request[] requests) {
        for (Request r : requests) {
            this.requests.remove(r);
        }
    }
}
