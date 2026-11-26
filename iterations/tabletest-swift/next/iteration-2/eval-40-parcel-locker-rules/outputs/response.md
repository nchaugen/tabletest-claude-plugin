I wrote the tests in `Tests/ParcelLockerTests/PickupTests.swift`. They compile and all 20 cases fail, every one with `NotImplemented()`. That's the expected failure, since `Locker.pickUp` is still the unimplemented stub.

**Tables**
- **`refusesAWrongCodeAndCountsTheAttemptsLeft`:**
  - A right code opens the locker, including after two earlier misses.
  - The first wrong code leaves 2 attempts, the second leaves 1, and the third gives `blocked`.
- **`keepsABlockedParcelBlocked`:** at 3 wrong codes in a row, both the right code and a wrong code give `blocked`.
- **`returnsAnExpiredParcelToTheDepot`:** 73 hours after delivery gives `returnedToDepot`. The row covers the right code and a wrong code, and every wrong-code count from 0 to 3.

Within the window, the first two tables use a value set of 0 and 72 hours. This claims the hours don't change the answer there, and it makes 72 hours the last hour that still opens.

**Assumptions** (also in the doc comment on `PickupTests`)
- **`wrongCodesInARow`:** I read it as the number of wrong codes before the current attempt. A count of 2 means the next wrong code is the third, and a count of 3 means the parcel is already blocked.
- **The 72 hours:** I treated `hoursSinceDelivery` as whole hours, with 72 still opening and 73 too late.
- **Expiry versus blocking:** an expired parcel gives `returnedToDepot` even if it is also blocked, because you said the locker says so "whatever code is typed".
- **Malformed codes:** I tested only well-formed six-digit codes, because the rules don't say what happens with anything else.

**One change outside the tests:** I added `Codable` to `PickupResult` in `Pickup.swift`. TableTest needs it to read results like `wrongCode(attemptsLeft: 2)` from a cell. The stub body is unchanged.