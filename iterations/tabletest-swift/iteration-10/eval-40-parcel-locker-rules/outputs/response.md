I wrote `Tests/ParcelLockerTests/PickupTests.swift`. It compiles, and the tests fail as expected. `swift build --build-tests` succeeds. `swift test` fails every case with `NotImplemented()` from the stub, which I left unimplemented. I didn't count the total, because my output filter only showed some of the failures.

**Tests**
- `refusesWrongCodesUntilTheThirdBlocksTheParcel` has five rows, with the window held at 24 hours:
  - The right code opens the locker after 0, 1 or 2 earlier wrong codes, so a right code resets the count.
  - The first wrong code leaves 2 attempts, the second leaves 1, and the third blocks the parcel.
  - A parcel with 3 wrong codes already typed stays blocked, for both the right code and a wrong one.
- `returnsAnExpiredParcelToTheDepot` has three rows:
  - At 0 and 72 hours with the right code, the locker opens.
  - At 73 hours the parcel goes back to the depot, whichever code is typed and after 0, 1 or 2 wrong codes.
  - At 73 hours with 3 wrong codes already typed, it also goes back to the depot.

**One change outside the test file:** I added `Codable` to `PickupResult` in `Pickup.swift`. This lets cells like `wrongCode(attemptsLeft: 2)` be read directly. It adds no behaviour.

**Assumptions** (also written in the doc comments):
- `hoursSinceDelivery` is whole hours, so 72 is still inside the window and 73 is the first hour outside it.
- If a parcel is both blocked and past 72 hours, the locker reports `returnedToDepot`, because the courier has already taken the parcel. You didn't say which should win, so please confirm.
- "Blocked" means `wrongCodesInARow == 3`.
- The 72-hour limit is in the doc comment, not a column. The stub has no window constant for the test to check a column against.
- Malformed codes, such as five digits or letters, are treated as ordinary wrong codes and have no rows of their own.