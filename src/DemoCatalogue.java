public final class DemoCatalogue {
    private DemoCatalogue() {
    }

    static void load(int number, ElevatorSystem system) {
        switch (number) {
            case 1 -> singleRequest(system);
            case 2 -> sameDirection(system);
            case 3 -> oppositeDirections(system);
            case 4 -> crowdedFloor(system);
            case 5 -> busyBuilding(system);
            default -> throw new IllegalArgumentException("Unknown demo: " + number);
        }
    }

    private static void singleRequest(ElevatorSystem system) {
        system.submitRequest(1, 10);
    }

    private static void sameDirection(ElevatorSystem system) {
        system.submitRequest(1, 10);
        system.submitRequest(4, 15);
    }

    private static void oppositeDirections(ElevatorSystem system) {
        system.submitRequest(1, 15);
        system.submitRequest(20, 2);
    }

    private static void crowdedFloor(ElevatorSystem system) {
        system.submitRequest(8, 20);
        system.submitRequest(8, 1);
        system.submitRequest(8, 12);
        system.submitRequest(3, 18);
    }

    private static void busyBuilding(ElevatorSystem system) {
        system.submitRequest(1, 20);
        system.submitRequest(3, 14);
        system.submitRequest(6, 2);
        system.submitRequest(9, 17);
        system.submitRequest(12, 5);
        system.submitRequest(15, 19);
        system.submitRequest(18, 1);
        system.submitRequest(20, 8);
    }
}
