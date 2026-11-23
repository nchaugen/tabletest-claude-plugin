I wrote the tests in `Tests/ParcelLockerTests/PickupTests.swift`. They compile, and they all fail with `NotImplemented()`, as intended. The stub body is unchanged.

I made one change outside the test file. I added `Codable` to `PickupResult`, so a table cell can spell `wrongCode(attemptsLeft: 2)`. It changes no behaviour.

There are three tables, one per rule:
- **`answersATypedCodeFromTheWrongCodesSoFar`**: a right code opens. The first, second and third wrong codes give 2 attempts left, 1 attempt left, then blocked.
- **`keepsABlockedParcelBlocked`**: with three wrong codes already, both the right and a wrong code give `blocked`.
- **`returnsAnExpiredParcelToTheDepot`**: at 72 hours the right code opens. At 73 hours, every code and every wrong-code count gives `returnedToDepot`.

Value sets expand each row into several cases. The first two tables run each row at both 0 and 72 hours, so they also check that the code rules hold across the whole window.

**Assumptions** (also in the doc comment):
- **Window edge:** 72 whole hours is still inside the window, and 73 is past it.
- **Malformed codes:** a code that isn't the right six digits is an ordinary wrong code. The wrong-code rows try `654321`, `123457` and `12345`.
- **Blocked and expired:** a parcel that is both blocked and past 72 hours reports `returnedToDepot`, since it is no longer in the locker. The spec doesn't settle this, so please confirm it.
- **The third wrong code:** it returns `blocked` itself, rather than `wrongCode(attemptsLeft: 0)`.