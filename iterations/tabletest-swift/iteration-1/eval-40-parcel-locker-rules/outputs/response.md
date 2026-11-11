The tests are written in `Tests/ParcelLockerTests/PickupTests.swift`. They compile and fail as intended: every case ends with `NotImplemented()` from the stub. The `Locker.pickUp` stub is unchanged.

I also added `Codable` to `PickupResult` in `Pickup.swift`. That lets table cells write results the way Swift spells them, such as `wrongCode(attemptsLeft: 2)`. It adds no behaviour.

**Tests**
- **`sendsAnExpiredParcelBackToTheDepot`** covers the 72-hour window:
  - At 72 hours a right code opens the locker.
  - At 73 hours the result is `returnedToDepot` for a right or a wrong code, with 0, 1, 2 or 3 earlier wrong codes.
- **`refusesAWrongCodeAndBlocksTheThirdInARow`** covers code handling inside the window:
  - A right code opens the locker with 0, 1 or 2 earlier wrong codes.
  - A wrong code gives `attemptsLeft: 2`, then `attemptsLeft: 1`, then `blocked` on the third wrong code.
  - A parcel that is already blocked stays blocked for both the right and a wrong code.

The tests were not run against a real implementation, so they are untested against any logic. The two tables expand into 22 cases.

**Assumptions** (also written in the doc comments)
- **72 hours is inclusive.** `hoursSinceDelivery` is whole hours, so 72 can still be picked up and 73 is expired.
- **Expiry wins over blocking.** A blocked parcel past 72 hours gets `returnedToDepot`, because the courier has already taken it back.
- **Blocked means a count of 3 or more.** A count above 3 is treated as blocked too.
- **A malformed code is just a wrong code.** A five-digit code counts as a wrong attempt, not a separate error.
- **The attempts table fixes the delivery age at 10 hours.** Time rules are tested only in the expiry table.

I ran the table formatter, which realigned the columns.