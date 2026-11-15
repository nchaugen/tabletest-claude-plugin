I wrote the tests in `Tests/ParcelLockerTests/PickupTests.swift`. They compile, and they fail as expected because `Locker.pickUp` still throws `NotImplemented`. `swift test` reports 29 failing cases across the two tests.

**Tests**
- **`refusesWrongCodesAndBlocksTheParcelOnTheThird`** holds the code-checking rules, inside the window:
  - A right code opens the locker whether 0, 1 or 2 wrong codes came before it.
  - The first wrong code leaves 2 attempts, the second leaves 1, and the third blocks the parcel.
  - A parcel that is already blocked stays blocked, for the right code and for a wrong one.
- **`returnsTheParcelToTheDepotAfterSeventyTwoHours`** holds the window rule:
  - Hours 0 and 72 open the locker.
  - Hours 73 and 1000 return the parcel to the depot, for the right or a wrong code and for any wrong-code count from 0 to 3.

**One change outside the test file**
- I added `Codable` to `PickupResult` in `Pickup.swift`. It lets a table cell read `wrongCode(attemptsLeft: 2)` directly. The stub is still unimplemented.

**Assumptions**
- **Blocked and past 72 hours:** I read this as `returnedToDepot`, because the courier has taken the parcel away. The spec's two rules conflict here. The assumption is recorded in the doc comment on the window test, and it is one row to flip if you disagree.
- **72 hours:** hour 72 is still inside the window ("up to 72 hours"), and hour 73 is past it. `hoursSinceDelivery` is a whole number, so a parcel at 72 h 30 min counts as hour 72.
- **Already blocked:** `wrongCodesInARow == 3` means the parcel is blocked.
- **Count reset:** a right code resets the count, so the result doesn't depend on the earlier wrong codes.
- **Code format:** I didn't test malformed codes such as five digits or non-digits. They count as wrong codes, and the spec doesn't say otherwise.
- **The 72-hour limit** isn't a column, because `pickUp` doesn't take it as an input. It is stated in the doc comment.