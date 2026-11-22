## Elevator dispatch system
- this is a mock up of a real elevator system that i saw irl and wanted to understand how it worked so i decided to try and create my own version.
- it's supposed to work like this:
    - a passenger issues a request to which floor they want to go (since this is a simulation, there are no passengers)
    - the system sorts all of the elevators by distance to the floor where the request was made from, ignoring elevators moving away, and picks the best candidate.
    - then adds the floor of the coming request to the list of destinations of the chosen elevator.
    - whenever an elevator arrives at its destination, it discards the request that got it to this floor and picks up any requests that are in this floor to add to its list of destinations.

## Running

Compile the project:

```bash
javac -d out src/*.java
```

Interactive mode:

```bash
java -cp out Main
```

Demo mode:

```bash
java -cp out Main --demo <number>
```

Demos advance one step per second. Press Enter to exit a demo.
For the moment, all demos use the same 5-elevator, 20-floor building.

## Demos

1. Single request: floor 1 to 10.
2. Same-direction requests: 1 to 10 and 4 to 15.
3. Opposite directions: 1 to 15 and 20 to 2.
4. Crowded floor: 8 to 20, 8 to 1, 8 to 12, and 3 to 18.
5. Busy building: eight requests spread across floors 1 through 20.

