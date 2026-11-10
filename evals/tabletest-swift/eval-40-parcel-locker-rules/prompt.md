We are building the pickup step for our parcel lockers. `Sources/ParcelLocker/Pickup.swift` has the types and an unimplemented `Locker.pickUp(_:code:)`.

Write the tests first, in `Tests/ParcelLockerTests/PickupTests.swift`. The package already depends on TableTest.

The rules are:
- The recipient opens the locker with the parcel's six-digit pickup code.
- A wrong code is refused, and the locker says how many attempts are left. The third wrong code in a row blocks the parcel.
- A blocked parcel stays blocked, also for the right code. The recipient must then call support.
- The parcel can be picked up for up to 72 hours after delivery. After that the courier takes it back to the depot, and the locker says so whatever code is typed.

Keep the stub unimplemented — the tests are expected to fail until the logic is written, but they must compile.
