A Swift package has been created for this feature, with Swift Testing configured.

Under `Sources/HotelBooking`, the package already contains a `CancellationError` enum and an unimplemented `cancellationFee` function.

We want to write the tests before implementing the fee logic.

Write table-driven Swift Testing tests for `cancellationFee(...)`.

The rules are:
- Cancelling 30 or more days before check-in is free
- Cancelling 7 to 29 days before check-in costs 50% of the booking value
- Cancelling 1 to 6 days before check-in costs 80% of the booking value
- Cancelling on the day of check-in costs the full booking value
- Once the stay has started, cancellation is no longer possible — `CancellationError.stayAlreadyStarted` is thrown

Keep the stub unimplemented — the tests are expected to fail until the logic is written, but they must compile.
