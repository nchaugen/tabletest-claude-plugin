# Narration — 40

The agent's visible narration, thinking cost and file writes, in order, distilled from
`conversation.jsonl`. Thinking text appears when the run asked the CLI for a summary
(`thinking_display: "summarized"`); the per-block token counts appear either way.

_[~172 thinking tokens (12 deltas)]_

> (thinking) I'm going to read the table design reference in full first, since the skill requires it before I write anything. I'll also check Package.swift and the test directory.

_[~1,992 thinking tokens (206 deltas)]_

> (thinking) I'm sketching the code-check rules: a right code opens the window, while wrong codes drop attempts down to a block on the third strike. I'm also checking whether a wrong code at that point should return blocked, and I'm assuming it should. Next I'm working out how an already-blocked state stays blocked.
> 
> I'm settling the expiry boundary: a parcel stays pickable through 72 hours and goes back to the depot after that, whatever code is typed. For a parcel that is both blocked and expired, I'm assuming the depot outcome wins, since it's no longer in the locker.
> 
> I'm laying out a table of pickup-code cases with a fixed hours value, covering a right code at each wrong-count and a wrong code at 0, 1, and 2 strikes. I'm also checking that the result type needs Codable so the enum with associated values can be stored.
> 
> I'm filling in the blocked-state cases next, checking whether a wrong code after three strikes still returns blocked and whether a right code can open it.
> 
> I'm laying out the expiry cases next: at 72 hours the parcel can still be picked up, so a right code opens it and a wrong one counts as a strike, while at 73 hours it goes back to the depot whatever code is typed.
> 
> I'm now building a table that pairs the expiry boundary with the wrong-code count, so the precedence case, a blocked parcel past 72 hours, is visible alongside the others.
> 
> I'm setting up the expiry cases as one table: at 72 hours a right code with no wrong attempts opens the parcel, and at 73 hours every combination of code and wrong-code count goes back to the depot. I'm now writing the test function around that table.
> 
> I'm naming the test functions now, each after the behavior it checks, and I'm settling on the one for typed codes up to the wrong-code count.
> 
> I'm weighing whether a malformed code like "12345" belongs in the wrong-code value set, and I'm leaning toward counting it as a wrong code, though I'm unsure whether that's overfitting the spec. I'll keep the table focused on the main right and wrong cases and decide on the edge case separately.
> 
> I'm now working through the wrong-code values for the table, leaning toward including a one-digit-off code and a too-short code alongside the obvious wrong one. For the right-code rows, I'm splitting them into a first attempt with no earlier wrong codes and a case after one or two wrong codes, since both should open the parcel.
> 
> Now I'm filling in the expiry table, pairing the 72-hour and 73-hour boundary rows with the same pickup code and wrong-code counts.
> 
> Adding Codable to the result type so the enum can be stored, then building and running the tests.

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

