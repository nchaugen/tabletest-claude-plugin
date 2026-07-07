A Python project has been created for this feature, with pytest configured.

Under `src/`, the project already contains a stub `baggage` module with a `BagNotAccepted` exception and an unimplemented `fee_for_checked_bag` function.

We want to write the tests before implementing the fee logic.

Write table-driven pytest tests for `fee_for_checked_bag(...)`.

The rules are:
- Bags up to 23 kg are included in the ticket — no fee
- Over 23 kg and up to 32 kg costs the 75 euro heavy-bag fee
- Over 32 kg and up to 45 kg costs the 150 euro oversize fee
- Bags over 45 kg are not accepted at all — `BagNotAccepted` is raised

Keep the stub unimplemented — the tests are expected to fail until the logic is written, but they must import and collect cleanly.
