import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

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
        runTest("requestMakerCreatesValidRequest", Test::requestMakerCreatesValidRequest, failures);
        runTest("requestMakerRejectsOutOfRange", Test::requestMakerRejectsOutOfRange, failures);
        runTest("requestMakerRejectsSameFloor", Test::requestMakerRejectsSameFloor, failures);
        runTest("commandParserParsesStep", Test::commandParserParsesStep, failures);
        runTest("commandParserParsesBracketedRequest", Test::commandParserParsesBracketedRequest, failures);
        runTest("commandParserParsesUnbracketedRequest", Test::commandParserParsesUnbracketedRequest, failures);
        runTest("commandParserRejectsMalformedRequest", Test::commandParserRejectsMalformedRequest, failures);
        runTest("commandParserRejectsOverflow", Test::commandParserRejectsOverflow, failures);
        runTest("elevatorSystemSubmitRequestAddsToFloor", Test::elevatorSystemSubmitRequestAddsToFloor, failures);
        runTest("elevatorSystemSubmitRequestRejectsInvalidRequest", Test::elevatorSystemSubmitRequestRejectsInvalidRequest, failures);
        runTest("buildFramePadsFloorLabels", Test::buildFramePadsFloorLabels, failures);
        runTest("buildFramePadsHundredFloors", Test::buildFramePadsHundredFloors, failures);
        runTest("buildFrameShowsRequestsOnBothSides", Test::buildFrameShowsRequestsOnBothSides, failures);
        runTest("buildFrameShowsElevatorDirection", Test::buildFrameShowsElevatorDirection, failures);
        runTest("buildFrameHasNoEscapeCodes", Test::buildFrameHasNoEscapeCodes, failures);
        runTest("renderFrameErasesEachLine", Test::renderFrameErasesEachLine, failures);
        runTest("arrivalClearsAcceptedFloorRequests", Test::arrivalClearsAcceptedFloorRequests, failures);
        runTest("demosLoadAllScenarios", Test::demosLoadAllScenarios, failures);
        runTest("unknownDemoRejected", Test::unknownDemoRejected, failures);
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

    private static void requestMakerCreatesValidRequest() {
        Request request = new RequestMaker(20).create(3, 5);

        checkEquals(3, request.floor, "request floor");
        checkEquals(5, request.destination, "request destination");
    }

    private static void requestMakerRejectsOutOfRange() {
        RequestMaker maker = new RequestMaker(20);

        expectThrows(
            IllegalArgumentException.class,
            () -> maker.create(0, 5),
            "floor below 1");

        expectThrows(
            IllegalArgumentException.class,
            () -> maker.create(3, 21),
            "destination above floor count");
    }

    private static void requestMakerRejectsSameFloor() {
        expectThrows(
            IllegalArgumentException.class,
            () -> new RequestMaker(20).create(5, 5),
            "floor matching destination");
    }

    private static void commandParserParsesStep() {
        Command command = new CommandParser().parse("   ");

        checkEquals(Command.Type.STEP, command.type, "blank command type");
    }

    private static void commandParserParsesBracketedRequest() {
        Command command = new CommandParser().parse("[3, 7]");

        checkEquals(Command.Type.REQUEST, command.type, "bracketed command type");
        checkEquals(3, command.floor, "bracketed floor");
        checkEquals(7, command.destination, "bracketed destination");
    }

    private static void commandParserParsesUnbracketedRequest() {
        Command command = new CommandParser().parse(" 4 , 9 ");

        checkEquals(Command.Type.REQUEST, command.type, "unbracketed command type");
        checkEquals(4, command.floor, "unbracketed floor");
        checkEquals(9, command.destination, "unbracketed destination");
    }

    private static void commandParserRejectsMalformedRequest() {
        Command command = new CommandParser().parse("3, 7, 9");

        checkEquals(Command.Type.INVALID, command.type, "malformed command type");
        check(command.error != null, "malformed command should include an error");
    }

    private static void commandParserRejectsOverflow() {
        Command command = new CommandParser().parse("999999999999, 2");

        checkEquals(Command.Type.INVALID, command.type, "overflow command type");
    }

    private static void elevatorSystemSubmitRequestAddsToFloor() {
        ElevatorSystem system = new ElevatorSystem(20, List.of(Elevator.at('A', 20, 1)));

        system.submitRequest(3, 7);

        List<Request> requests = system.getFloor(3).getRequests();
        checkEquals(1, requests.size(), "request count");
        checkEquals(new Request(3, 7), requests.get(0), "submitted request");
    }

    private static void elevatorSystemSubmitRequestRejectsInvalidRequest() {
        ElevatorSystem system = new ElevatorSystem(20, List.of(Elevator.at('A', 20, 1)));

        expectThrows(
            IllegalArgumentException.class,
            () -> system.submitRequest(0, 5),
            "floor outside system");

        checkEquals(0, system.getFloor(1).getRequests().size(), "invalid request count");
    }

    private static void buildFramePadsFloorLabels() {
        ElevatorSystem system = new ElevatorSystem(20, List.of(Elevator.at('A', 20, 1)));
        String frame = new Simulation(system, new Scanner("")).buildFrame();

        check(frame.contains("20 | "), "top floor label");
        check(frame.contains("01 | "), "bottom floor label");
        check(frame.contains("A–"), "idle arrow");
    }

    private static void buildFramePadsHundredFloors() {
        ElevatorSystem system = new ElevatorSystem(100, List.of(Elevator.at('A', 100, 1)));
        String frame = new Simulation(system, new Scanner("")).buildFrame();

        check(frame.contains("100 | "), "top floor label");
        check(frame.contains("001 | "), "bottom floor label");
    }

    private static void buildFrameShowsRequestsOnBothSides() {
        ElevatorSystem system = new ElevatorSystem(20, List.of(Elevator.at('A', 20, 1)));
        system.submitRequest(3, 7);

        String frame = new Simulation(system, new Scanner("")).buildFrame();

        check(frame.contains("03 | "), "left floor label");
        check(frame.contains("03: [07]"), "right floor label and request");
    }

    private static void buildFrameShowsElevatorDirection() {
        Elevator elevator = Elevator.at('A', 20, 5);
        elevator.addToDestinations(10);

        ElevatorSystem system = new ElevatorSystem(20, List.of(elevator));
        String frame = new Simulation(system, new Scanner("")).buildFrame();

        check(frame.contains("A↑"), "up arrow");
    }

    private static void buildFrameHasNoEscapeCodes() {
        ElevatorSystem system = new ElevatorSystem(20, List.of(Elevator.at('A', 20, 1)));
        String frame = new Simulation(system, new Scanner("")).buildFrame();

        check(!frame.contains("\033"), "frame should be plain text");
    }

    private static void renderFrameErasesEachLine() {
        ElevatorSystem system = new ElevatorSystem(20, List.of(Elevator.at('A', 20, 1)));
        Simulation simulation = new Simulation(system, new Scanner(""));

        int lineCount = simulation.buildFrame().split("\n", -1).length;
        String rendered = simulation.renderFrame();
        int eraseCount = rendered.split("\033\\[K", -1).length - 1;

        checkEquals(lineCount, eraseCount, "erase sequence count");
        check(rendered.contains("\033[?25h"), "cursor should be shown");
    }

    private static void arrivalClearsAcceptedFloorRequests() {
        Elevator elevator = Elevator.at('A', 10, 1);
        ElevatorSystem system = new ElevatorSystem(10, List.of(elevator));
        system.submitRequest(1, 5);

        system.step();

        checkEquals(0, system.getFloor(1).getRequests().size(), "floor 1 request count");
        checkEquals(Elevator.Direction.UP, elevator.getDirection(), "elevator direction");

        String frame = new Simulation(system, new Scanner("")).buildFrame();
        check(!frame.contains("01: [05]"), "accepted request should leave the frame");
    }

    private static void demosLoadAllScenarios() {
        int[] expectedCounts = {1, 2, 2, 4, 8};

        for (int demo = 1; demo <= expectedCounts.length; demo++) {
            ElevatorSystem system = new ElevatorSystem(20, List.of(Elevator.at('A', 20, 1)));
            DemoCatalogue.load(demo, system);

            checkEquals(expectedCounts[demo - 1], totalRequests(system), "demo " + demo + " request count");
        }
    }

    private static void unknownDemoRejected() {
        ElevatorSystem system = new ElevatorSystem(20, List.of(Elevator.at('A', 20, 1)));

        expectThrows(
            IllegalArgumentException.class,
            () -> DemoCatalogue.load(99, system),
            "unknown demo");
    }

    private static int totalRequests(ElevatorSystem system) {
        int total = 0;

        for (Floor floor : system.getFloors()) {
            total += floor.getRequests().size();
        }

        return total;
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
