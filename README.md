## Elevator dispatch system
- this is a mock up of a real elevator system that i saw irl and wanted to understand how it worked so i decided to try and create my own version.
- it's supposed to work like this:
    - a passenger issues a request to which floor they want to go (since this is a simulation, there are no passengers)
    - the system sorts all of the elevators by distance to the floor where the request was made from, ignoring elevators moving away, and picks the best candidate.
    - then adds the floor of the coming request to the list of destinations of the chosen elevator.
    - whenever an elevator arrives at its destination, it discards the request that got it to this floor and picks up any requests that are in this floor to add to its list of destinations.


