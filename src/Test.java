import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Test {
    private interface TestCase {
        void run() throws Exception;
    }

    private static int passed;
    private static int failed;

    public static void main(String[] args) {
        run();
    }

    static void run() {
        passed = 0;
        failed = 0;

        List<Throwable> failures = new ArrayList<>();

        runTest("elevatorAtSetsStartingPosition", Test::elevatorAtSetsStartingPosition, failures);
        runTest("elevatorAtRejectsInvalidPosition", Test::elevatorAtRejectsInvalidPosition, failures);
        runTest("elevatorSystemAcceptsInjectedElevators", Test::elevatorSystemAcceptsInjectedElevators, failures);
        runTest("elevatorSystemRejectsOutOfRangeStartingPosition", Test::elevatorSystemRejectsOutOfRangeStartingPosition, failures);
        runTest("elevatorSystemRejectsMismatchedMax", Test::elevatorSystemRejectsMismatchedMax, failures);
        runTest("elevatorSystemRejectsEmptyElevatorList", Test::elevatorSystemRejectsEmptyElevatorList, failures);
        runTest("floorGetRequestsReturnsCopy", Test::floorGetRequestsReturnsCopy, failures);
        runTest("floorRemoveRequestsRemovesAccepted", Test::floorRemoveRequestsRemovesAccepted, failures);
        runTest("requestEqualityUsesFields", Test::requestEqualityUsesFields, failures);
        runTest("elevatorMovesTowardDestination", Test::elevatorMovesTowardDestination, failures);
        runTest("elevatorStopsAtTopBoundary", Test::elevatorStopsAtTopBoundary, failures);
        runTest("elevatorStopsAtBottomBoundary", Test::elevatorStopsAtBottomBoundary, failures);
        runTest("canServeMatchesDirection", Test::canServeMatchesDirection, failures);

        if (failed > 0) {
            AssertionError error = new AssertionError(failed + " test(s) failed");
            for (Throwable failure : failures) {
                error.addSuppressed(failure);
            }
            throw error;
        }

        System.out.println("All " + passed + " tests passed!");
    }

    private static void runTest(String name, TestCase testCase, List<Throwable> failures) {
        try {
            testCase.run();
            passed++;
            System.out.println("test " + name + " passed!");
        } catch (Throwable t) {
            failed++;
            String message = t.getMessage() == null ? t.getClass().getSimpleName() : t.getMessage();
            System.err.println("test " + name + " failed: " + message);
            failures.add(t);
        }
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    private static void checkEquals(Object expected, Object actual, String label) {
        if (!Objects.equals(expected, actual)) {
            throw new AssertionError(label + ": expected " + expected + " but was " + actual);
        }
    }

    private static void checkPosition(Elevator elevator, int expected) {
        checkEquals(expected, elevator.getPosition(), elevator.getName() + " position");
    }

    private static void expectThrows(Class<? extends Throwable> expected, TestCase action, String label) {
        try {
            action.run();
        } catch (Throwable t) {
            if (expected.isInstance(t)) {
                return;
            }
            throw new AssertionError(
                label + ": expected " + expected.getSimpleName()
                    + " but got " + t.getClass().getSimpleName(),
                t);
        }

        throw new AssertionError(
            label + ": expected " + expected.getSimpleName() + " but nothing was thrown");
    }

    private static void elevatorAtSetsStartingPosition() {
        Elevator elevator = Elevator.at('A', 20, 7);

        checkPosition(elevator, 7);
        checkEquals(20, elevator.getMax(), "A max");
    }

    private static void elevatorAtRejectsInvalidPosition() {
        expectThrows(
            IllegalArgumentException.class,
            () -> Elevator.at('A', 20, 0),
            "position below building");

        expectThrows(
            IllegalArgumentException.class,
            () -> Elevator.at('A', 20, 21),
            "position above building");
    }

    private static void elevatorSystemAcceptsInjectedElevators() {
        Elevator a = Elevator.at('A', 20, 1);
        Elevator b = Elevator.at('B', 20, 10);

        new ElevatorSystem(20, List.of(a, b));

        checkPosition(a, 1);
        checkPosition(b, 10);
    }

    private static void elevatorSystemRejectsOutOfRangeStartingPosition() {
        Elevator elevator = Elevator.at('A', 20, 20);

        expectThrows(
            IllegalArgumentException.class,
            () -> new ElevatorSystem(10, List.of(elevator)),
            "elevator starting above floor count");
    }

    private static void elevatorSystemRejectsMismatchedMax() {
        Elevator elevator = Elevator.at('A', 10, 1);

        expectThrows(
            IllegalArgumentException.class,
            () -> new ElevatorSystem(20, List.of(elevator)),
            "elevator max not matching floor count");
    }

    private static void elevatorSystemRejectsEmptyElevatorList() {
        expectThrows(
            IllegalArgumentException.class,
            () -> new ElevatorSystem(20, List.of()),
            "empty elevator list");
    }

    private static void floorGetRequestsReturnsCopy() {
        Floor floor = new Floor(1);
        floor.addRequest(new Request(1, 5));

        List<Request> requests = floor.getRequests();
        requests.add(new Request(1, 6));

        checkEquals(1, floor.getRequests().size(), "original request count");
    }

    private static void floorRemoveRequestsRemovesAccepted() {
        Floor floor = new Floor(1);
        Request accepted = new Request(1, 5);
        Request remaining = new Request(1, 6);

        floor.addRequest(accepted);
        floor.addRequest(remaining);
        floor.removeRequests(List.of(accepted));

        checkEquals(1, floor.getRequests().size(), "remaining request count");
        checkEquals(remaining, floor.getRequests().get(0), "remaining request");
    }

    private static void requestEqualityUsesFields() {
        check(
            new Request(3, 5).equals(new Request(3, 5)),
            "requests with the same fields should be equal");

        check(
            !new Request(3, 5).equals(new Request(3, 6)),
            "requests with different destinations should not be equal");
    }

    private static void elevatorMovesTowardDestination() {
        Elevator elevator = Elevator.at('A', 5, 1);

        elevator.addToDestinations(3);
        elevator.move();

        checkPosition(elevator, 2);
    }

    private static void elevatorStopsAtTopBoundary() {
        Elevator elevator = Elevator.at('A', 5, 4);

        elevator.addToDestinations(5);
        elevator.move();
        checkPosition(elevator, 5);

        expectThrows(
            IllegalStateException.class,
            elevator::move,
            "moving above the top floor");
    }

    private static void elevatorStopsAtBottomBoundary() {
        Elevator elevator = Elevator.at('A', 5, 2);

        elevator.addToDestinations(1);
        elevator.move();
        checkPosition(elevator, 1);

        expectThrows(
            IllegalStateException.class,
            elevator::move,
            "moving below the bottom floor");
    }

    private static void canServeMatchesDirection() {
        Elevator elevator = Elevator.at('A', 10, 5);

        check(
            elevator.canServe(List.of(new Request(5, 8))),
            "IDLE elevator should serve a non-empty request list");

        check(
            !elevator.canServe(List.of()),
            "IDLE elevator should not serve an empty request list");

        elevator.addToDestinations(8);

        check(
            elevator.canServe(List.of(new Request(5, 9))),
            "UP elevator should serve an up request");

        check(
            !elevator.canServe(List.of(new Request(5, 2))),
            "UP elevator should not serve a down request");
    }
}
