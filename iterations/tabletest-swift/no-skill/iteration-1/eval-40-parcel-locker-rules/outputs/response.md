I wrote `Tests/ParcelLockerTests/PickupTests.swift`. It compiles, and `swift test` fails every row with `NotImplemented`, which is the expected result against the unimplemented stub. I did not touch `Pickup.swift`.

**The tests** use TableTest, one row per scenario:
- **`answersTheCodeByAttemptsUsed`:**
  - A right code opens the locker, whether 0, 1 or 2 wrong codes came before.
  - Wrong codes after 0, 1 and 2 earlier misses give `wrongCode(attemptsLeft: 2)`, `wrongCode(attemptsLeft: 1)` and `blocked`.
  - A parcel that already has 3 wrong codes stays `blocked`, for the right code and for a wrong one.
  - The in-window rows run at both 0 and 72 hours.
- **`returnsTheParcelToTheDepotAfter72Hours`:**
  - At 72 hours the code still decides the result.
  - At 73 hours the result is `returnedToDepot` for the right code, a wrong code and a malformed code.
- **`prefersTheDepotMessageOverTheAttemptCounter`:** the two cases where the rules overlap (see assumptions).

A small `TableTextDecodable` extension on `PickupResult` lets the table cells read `opens`, `blocked`, `returnedToDepot` and `wrongCode(attemptsLeft: 2)`.

**Assumptions**, which are also written at the top of the file. Each is a row you can change if the product decides otherwise:
1. `wrongCodesInARow` is the count before this attempt, so 3 means the parcel is already blocked.
2. A code of the wrong shape (`12345`, `1234567`, `12345a`, empty) counts as a wrong code, not a separate outcome.
3. "Up to 72 hours" on whole hours means 72 is still inside the window and 73 is outside. Whole hours could hide up to 72h59m, so confirm this if it matters.
4. After 72 hours the result is `returnedToDepot` even for a blocked parcel or a would-be third miss. The rules don't say which wins, so I let expiry win.

I did not pin down the error `pickUp` throws, because the rules don't define one. The tests only call it with `try`.