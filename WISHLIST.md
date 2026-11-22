# Wishlist

## Possible future improvements

- [ ] Finish `Request` value equality: `equals` is implemented, but `hashCode` is still missing, violating the equals/hashCode contract. Only matters if `Request` is ever used in a hash-based collection.
- [x] Make elevator starting positions deterministic/injectable for testing.
- [ ] Add a simple build/test command; decide how to handle remaining ignored artifacts (`out/`, `.tuios.tape`).
- [ ] Remove or use `Floor.retainRequests()`; it is currently unused.
- [ ] Add a viewport/compact layout for very large floor counts; a 100-floor building does not fit vertically in a normal terminal.
