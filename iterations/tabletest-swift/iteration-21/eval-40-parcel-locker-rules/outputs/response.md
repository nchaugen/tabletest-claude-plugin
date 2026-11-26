The tests are written in `Tests/ParcelLockerTests/PickupTests.swift`. They compile, and they fail as expected because `Locker.pickUp` is still unimplemented. `swift build --build-tests` succeeds. In `swift test` every case fails with `NotImplemented()`. The change on disk is just the table formatter's realignment, so nothing needs undoing.

There are three tables:

| Test | Rule it covers |
|---|---|
| `countsWrongCodesAndBlocksTheParcelOnTheThird` | The right code opens, including after two wrong codes. Wrong codes leave 2 and then 1 attempts. The third wrong code blocks the parcel. A blocked parcel stays blocked, for the right code and a wrong one. |
| `takesTheParcelBackToTheDepotAfter72Hours` | Hours 0 and 72 are inside the window. Hour 73 is past it, with the right or a wrong code. |
| `reportsTheDepotReturnAheadOfABlock` | Past the window, a blocked parcel gets the depot message. A wrong code that would have been the third miss does too. |

Hours 0 and 72 appear as a value set in the attempts rows. Those rows claim the hours don't matter inside the window.

I made one edit outside the test file. `PickupResult` in `Pickup.swift` now conforms to `Codable`, so a result like `wrongCode(attemptsLeft: 2)` reads from a table cell. The stub itself is still unimplemented.

**Assumptions** (also in the doc comments):
- **72 hours:** 72 whole hours is still inside the window and 73 is past it, from reading "up to 72 hours" as inclusive.
- **Expiry versus block:** I chose the depot message when both apply, because the parcel is no longer in the locker. Your rules don't say which wins. If a blocked parcel should stay blocked after 72 hours, change the two rows in the third table.
- **Wrong codes:** any code that differs from the pickup code is wrong, including a malformed one such as a five-digit code. I haven't tested malformed codes separately.
- **Past the window:** a wrong code typed after 72 hours does not use up an attempt.