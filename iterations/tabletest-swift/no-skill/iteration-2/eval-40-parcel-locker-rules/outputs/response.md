I wrote the pickup tests in `Tests/ParcelLockerTests/PickupTests.swift`. They compile, and all 19 cases fail because the stub throws `NotImplemented`. I left the stub unimplemented.

**What's tested.** Each rule has its own TableTest table:
1. **Wrong codes:** the first wrong code leaves 2 attempts, the second leaves 1, and the third in a row blocks the parcel. A right code opens the locker, including on the third attempt. A code one digit short counts as a wrong code.
2. **Blocked parcel:** a parcel already at three wrong codes in a row stays blocked, for the right code and for a wrong one.
3. **72-hour window:** at 0 and 72 hours the right code opens and a wrong code is refused. At 73 hours the locker says `returnedToDepot` for any code.
4. **Blocked and expired:** a parcel that is both blocked and past 72 hours returns `returnedToDepot`.

**Assumptions** (also noted at the top of the test file):
- **Blocked state:** a parcel counts as blocked once `wrongCodesInARow` is 3. I only test 3.
- **72-hour limit:** 72 hours still opens, and 73 is the first hour that doesn't.
- **Blocked and expired:** returning the parcel to the depot takes precedence over blocking, because the parcel is no longer in the locker. You didn't specify this, so check it.
- **Malformed codes:** a typed code that isn't exactly the six-digit pickup code is treated as a wrong code.
- **Pickup code:** every table uses 493817 as the parcel's code.

To write the expected result as a table cell, I added a `TableTextDecodable` conformance for `PickupResult` in the test file. Cells read `opens`, `blocked`, `returnedToDepot` or `wrongCode 2`.