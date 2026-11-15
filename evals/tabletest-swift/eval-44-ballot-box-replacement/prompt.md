We are adding vote replacement to our polling app. `Sources/Polling/BallotBox.swift` has the types and an unimplemented `BallotBox.replaceVote(_:for:)`.

Write the tests first, in `Tests/PollingTests/BallotBoxTests.swift`. The package already depends on TableTest.

The rules are:
- A voter can replace their vote while the poll is open.
- The box refuses a replacement when the poll is closed, when the voter has no vote in the box, or when the voter's ballot is sealed.
- A refused replacement changes nothing: the voter's vote and the tally stay as they were.

Keep the stub unimplemented — the tests are expected to fail until the logic is written, but they must compile.
