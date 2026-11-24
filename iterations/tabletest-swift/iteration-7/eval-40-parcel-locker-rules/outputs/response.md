I wrote the tests in `Tests/ParcelLockerTests/PickupTests.swift`. They compile, and they fail as expected, because `Locker.pickUp` still throws `NotImplemented`. `swift test` runs 32 cases, and all 32 fail with that error.

**What the tests cover**
- **`checksTheTypedCodeWhileTheParcelIsInTheLocker`** covers the code rules inside the window. It has these rows:
  - The right code opens the locker, whether 0, 1 or 2 wrong codes came before.
  - A first wrong code leaves 2 attempts.
  - A second wrong code in a row leaves 1 attempt.
  - A third wrong code in a row blocks the parcel.
  - A parcel that already has 3 wrong codes stays blocked, for the right code and a wrong one.
  - Every row runs at hour 0 and hour 72. The wrong codes are one near miss (`482916`) and one too short (`48291`).
- **`returnsAnExpiredParcelToTheDepotWhateverCodeIsTyped`** covers the 72-hour rule. Hours 0 and 72 open the locker. Hour 73 returns the parcel to the depot, for the right or a wrong code, with 0 to 3 wrong codes before.

**Changes outside the test file**
- I added `Codable` to `PickupResult` in `Pickup.swift`, so a table cell can write `wrongCode(attemptsLeft: 2)`. This adds no behaviour, and the stub is still unimplemented.

**Assumptions**
1. Hour 72 is still inside the window and hour 73 is expired. "Up to 72 hours" is read as inclusive, and `hoursSinceDelivery` is whole hours.
2. The depot return beats a block. A blocked parcel past 72 hours gives `returnedToDepot`, because you said the locker says so "whatever code is typed". If it should say `blocked`, change the 4th wrong-count in that row.
3. `wrongCodesInARow` counts wrong codes already typed. So a wrong code with 2 before it is the third and gives `blocked`.
4. Any code that isn't the pickup code is wrong, for example one with the wrong length.
5. The 72-hour limit is stated in the doc comments, not as a column. `pickUp` doesn't take it as input, so a column would have nothing to feed.