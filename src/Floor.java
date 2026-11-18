import java.util.ArrayList;
import java.util.List;

public class Floor {
    private final int number;
    private List<Request> requests = new ArrayList<>();
    private boolean waiting = false;

    Floor(int number) {
        this.number = number;
    }
    int getNumber() {
        return this.number;
    }
    void addRequest(Request request) {
        requests.add(request);
    }
    boolean hasRequests() {
        return !this.requests.isEmpty();
    }
    boolean isWaiting() {
        return this.waiting;
    }
    void setWaiting(boolean value) {
        this.waiting = value;
    }

    List<Request> getRequests() {
        List<Request> requests = this.requests;
        return requests;
    }

    void removeRequests(List<Request> requests, boolean matching) {
        if (matching) {
            this.requests.removeAll(requests);
        } else {
            this.requests.retainAll(requests);
        }
    }

}
