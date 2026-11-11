I wrote the tests in `Tests/ParcelLockerTests/PickupTests.swift`. They compile, and all 28 cases fail with `NotImplemented()` from the stub, as intended. I haven't run them against a real implementation, so the expected values are untested. I left `Pickup.swift` untouched.

**Tests**
- **Attempts:** one table covers a right code, the first and second wrong codes with attempts left, and the third wrong code in a row blocking. It also covers malformed codes (too short, too long, non-digit, empty) and an already-blocked parcel typing a right or wrong code. Each row runs at 0 and 72 hours, to show the hours don't affect these rules.
- **Expiry:** a second table covers 72 hours still opening and 73 hours returning to the depot for a right, wrong or malformed code. It also covers a wrong code that would otherwise block, and an already-blocked parcel.
- **Result column:** a small `TableTextDecodable` extension in the test file lets the Result column spell results as `wrongCode(attemptsLeft: 2)`.

**Assumptions** (also written at the top of the test file)
1. `wrongCodesInARow` is the count before the code being typed, so a wrong code at 2 blocks. A parcel arriving with 3 is already blocked.
2. A typed code that isn't exactly the six-digit pickup code counts as an ordinary wrong code.
3. "Up to 72 hours" includes 72, and 73 is expired. Because `hoursSinceDelivery` is in whole hours, 72 also covers 72h59m. If the cutoff should be exclusive, change the 72 and 73 rows.
4. A blocked parcel stays blocked for any code, wrong ones too.
5. If a parcel is both blocked and past 72 hours, it returns `returnedToDepot`. Your rules didn't settle this one, so please confirm. The last row of the expiry table pins it.