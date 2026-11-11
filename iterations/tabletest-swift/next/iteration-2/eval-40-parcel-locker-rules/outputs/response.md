I wrote `Tests/ParcelLockerTests/PickupTests.swift` with two `@TableTest` tables. It compiles (`swift build --build-tests` succeeds). It fails when run, as intended, because `Locker.pickUp` still throws `NotImplemented`. All 19 cases fail (9 + 10, value sets expanded).

**Tests**
- **"Checks the typed code and blocks the parcel on the third wrong code":** the parcel is held at 12 hours since delivery. The rows are:
  - the right code opens the locker, including after two wrong codes in a row
  - a first wrong code leaves 2 attempts, a second leaves 1, and a third blocks the parcel
  - a blocked parcel stays blocked for both the right and a wrong code
- **"Returns a parcel to the depot after the 72-hour pickup window":** the rows are 0 hours (opens), 72 hours (opens), and 73 hours. At 73 hours, either code and any wrong-code count from 0 to 3 gives `returnedToDepot`.

**One change outside the tests:** I added `Codable` to `PickupResult` in `Pickup.swift` so a table cell can read `wrongCode(attemptsLeft: 2)`. The stub is still unimplemented.

**Assumptions** (also in the doc comments)
- "Up to 72 hours" is inclusive, in whole hours: 72 still opens, 73 is returned to the depot.
- A code that is not the six-digit pickup code (too short or too long) counts as a wrong code.
- The counter before the third wrong code is 2, and `wrongCodesInARow >= 3` means already blocked.
- An expired parcel gets `returnedToDepot` even if it is also blocked, since the locker says so "whatever code is typed".

The 72-hour limit is not a column because `pickUp` doesn't take it as input. It appears in the table title and doc comment instead.