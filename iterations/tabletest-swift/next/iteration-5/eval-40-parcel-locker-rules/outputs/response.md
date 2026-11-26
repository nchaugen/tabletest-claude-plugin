The tests are written in `Tests/ParcelLockerTests/PickupTests.swift`. They compile, and all 20 cases fail with `NotImplemented()`, as expected. `Locker.pickUp` is still unimplemented.

I added one word to the app source: `PickupResult` is now `Codable`. That lets a cell read `wrongCode(attemptsLeft: 2)` directly, and it adds no behaviour.

**Table 1, `decidesPickupFromTheTypedCode`** (11 cases):

| Scenario | Wrong codes before | Typed code | Result |
|---|---|---|---|
| Right code after misses | 0, 1 or 2 | right | `opens` |
| First attempt, mistyped | 0 | wrong | `wrongCode(attemptsLeft: 2)` |
| Second wrong code in a row | 1 | wrong | `wrongCode(attemptsLeft: 1)` |
| Third wrong code in a row | 2 | wrong | `blocked` |
| Already blocked, any code | 3 | right or wrong | `blocked` |

**Table 2, `returnsAnExpiredParcelToTheDepot`** (9 cases):
- **72 hours, right code:** the locker opens.
- **73 hours:** the result is `returnedToDepot` for either a right or a wrong code, and for 0, 1, 2 or 3 earlier wrong codes. Those combinations also show that the depot message wins over blocking.

**Assumptions** (also in the doc comments):
- **Wrong-code count:** `wrongCodesInARow` is the count before this attempt. The third wrong code returns `blocked` rather than `wrongCode(attemptsLeft: 0)`.
- **Already blocked:** a parcel arriving with 3 wrong codes is already blocked.
- **72-hour limit:** `hoursSinceDelivery` is in whole hours, so 72 is still inside the window and 73 is expired.
- **Precedence:** an expired parcel gets `returnedToDepot` even if it was blocked, because you said the locker says so whatever code is typed.
- **Malformed codes:** I only used well-formed six-digit wrong codes. Whether an empty or short code counts as an attempt is unspecified, so there's no row for it.