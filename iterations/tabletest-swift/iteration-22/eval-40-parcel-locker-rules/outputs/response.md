I wrote the tests in `Tests/ParcelLockerTests/PickupTests.swift`. `swift build --build-tests` succeeds. `swift test` fails with 31 issues, as expected, because `Locker.pickUp` still throws `NotImplemented`. The table formatter realigned the columns after I wrote the file; that's the only change to it.

I made one change to the source: `PickupResult` now conforms to `Codable`, which adds no behaviour. This lets a table cell write the result as Swift spells it, for example `wrongCode(attemptsLeft: 2)`. The stub is unchanged.

There are two `@TableTest` tables with 31 cases in total:

- **`countsWrongCodesAndBlocksTheParcel`** (22 cases) covers a typed code inside the 72-hour window:
  - The right code opens the locker after 0, 1 or 2 earlier mistakes.
  - The first wrong code leaves 2 attempts, the second leaves 1, and the third blocks the parcel.
  - With 3 wrong codes already, both the right and a wrong code get `blocked`.
  - Malformed entries (five digits, seven digits, letters) count as wrong codes.
  - Each row runs at both hour 0 and hour 72, to show the hour doesn't matter inside the window.
- **`returnsAnExpiredParcelToTheDepot`** (9 cases) covers the 72-hour limit, which is a column in the table:
  - Hour 72 with the right code opens the locker.
  - Hour 73 gives `returnedToDepot` for both the right and a wrong code.
  - That holds with earlier wrong codes (1 or 2) and with 3.

**Assumptions** (also in the doc comments, and each is a one-line change):
1. **Window edge:** the 72nd whole hour is still inside the window, and hour 73 is expired. The spec says "up to 72 hours" but `hoursSinceDelivery` is in whole hours, so this could be read either way.
2. **Expired and blocked:** expiry wins, so an expired parcel that is also blocked gets `returnedToDepot`. Your rules say expiry applies "whatever code is typed", but they don't say what happens when both conditions hold.
3. **Malformed codes:** a code that isn't exactly the pickup code, including one of the wrong length, counts as a wrong code and uses up an attempt.
4. **Wrong-code counter:** `wrongCodesInARow` is the count before the attempt being made, and a count of 3 or more means the parcel is already blocked.

The window is a column in the second table, but the locker has no setting for it. Its parameter is therefore unused and only documents the limit.