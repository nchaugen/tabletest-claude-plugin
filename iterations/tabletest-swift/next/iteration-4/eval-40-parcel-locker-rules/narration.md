# Narration — 40

The agent's visible narration, thinking cost and file writes, in order, distilled from
`conversation.jsonl`. Thinking text appears when the run asked the CLI for a summary
(`thinking_display: "summarized"`); the per-block token counts appear either way.

_[~186 thinking tokens (12 deltas)]_

> (thinking) I need to read the table-design reference file in full, at skills/tabletest-swift/references/table-design.md in the work directory. I'll also check Package.swift and the tests directory alongside it.

_[~2,597 thinking tokens (347 deltas)]_

> (thinking) I'm working through the code-check rules: a right code opens the lock, while wrong codes step through the remaining attempts until the third wrong one blocks it. A right code after one or two wrong attempts still opens it and resets the streak. Once blocked, it stays blocked.
> 
> I'm now weighing the blocked state against the code columns, and whether a wrong-code counter above 3 could even happen. I'm leaning toward treating the counter as capped at 3. I'm also working out the expiry boundary: 72 whole hours still allows pickup, and 73 returns the parcel to the depot whatever code is typed. I still need to settle which wins when a parcel is both expired and blocked.
> 
> I'm settling on a single attempts table that covers wrong-code counting and the blocked state together. A blocked parcel stays blocked whatever code is typed, and when a parcel is both expired and blocked, I'm treating expiry as the winner and returning it to the depot.
> 
> I'm weighing the blocked state as a single attempts table covering wrong-code counting too, and I'm treating the hours input as only two cases, 0 and 72, since it only matters at the window edge.
> 
> I'm settling on concrete values for the test: a pickup code of 482913 and typed codes of either that right code or a wrong one like 482914 or 000000. I'm keeping the pickup code fixed as a column, and treating the typed code as a string so leading zeros survive. I also need the result type to be Codable so the wrong-code outcome can be converted in the table cells.
> 
> I'm settling on the expiry window as a fixed 72-hour threshold column, though nothing in the app passes that value in, so it only needs to be readable in the table. To make the result cells convertible I'll add Codable to the PickupResult enum in Pickup.swift, since the opens case has no associated values and the conformance can be synthesized.
> 
> I'm weighing whether the window itself should be a table column, since nothing in the app passes a window value in. I'm leaning toward documenting the 72-hour threshold rather than adding a parameter the code would never use.
> 
> I'm now deciding how to cover the expiry boundary, and I'm leaning toward a single expiry table where the 72-hour edge and the 73-hour cutoff sit side by side, so the straddle is visible in one place.
> 
> I'm laying out the expiry table now: a parcel at the 72-hour mark with the right code opens, while one past it goes back to the depot whatever is typed, and I'm covering the wrong-code counts in the same set of rows.
> 
> I'm settling the expiry rows now: the last hour of the window with the right code opens the parcel, and I'm holding the hours at a fixed value for the other rows. Past the window I'm just documenting the 72-hour limit instead of adding a parameter the app never receives.
> 
> I'm setting the expiry rows now: a parcel within the window opens with the right code at either edge, and a parcel past it goes back to the depot whatever is typed. A blocked parcel past the window gets its own row, so the precedence between expiry and blocking is explicit. I'm also checking whether the 72-hour mark needs wrong-code cases of its own.
> 
> Now I'm laying out the parcel setup for the tests: a Parcel built from a pickup code, a count of wrong codes in a row, and hours since delivery. The expectation is a PickupResult, and the call to the locker throws, so the stub's unimplemented error will make these tests fail for now. I'm also settling the table columns: scenario, pickup code, wrong codes in a row, hours since delivery, code typed, and the expected result.
> 
> I'm drafting the test table now, with a right-code row and the wrong-code rows after it. The first miss is easy to name, and I'm also settling how the third miss and the already-blocked case should be labelled so they don't echo the result.
> 
> Now I'm drafting the table titles for the test, using the `@TableTest` form that names what the table checks.
> 
> I'm settling on test names that describe the behaviour rather than the outcome: one table for refusing wrong codes until the third blocks the parcel, and one for returning an expired parcel to the depot. I'm also making sure the PickupResult enum conforms to Codable, then I'll build to check it compiles.

I wrote `Tests/ParcelLockerTests/PickupTests.swift`. It compiles, and the tests fail as expected. `swift build --build-tests` succeeds. `swift test` fails every case with `NotImplemented()` from the stub, which I left unimplemented. I didn't count the total, because my output filter only showed some of the failures.

**Tests**
- `refusesWrongCodesUntilTheThirdBlocksTheParcel` has five rows, with the window held at 24 hours:
  - The right code opens the locker after 0, 1 or 2 earlier wrong codes, so a right code resets the count.
  - The first wrong code leaves 2 attempts, the second leaves 1, and the third blocks the parcel.
  - A parcel with 3 wrong codes already typed stays blocked, for both the right code and a wrong one.
- `returnsAnExpiredParcelToTheDepot` has three rows:
  - At 0 and 72 hours with the right code, the locker opens.
  - At 73 hours the parcel goes back to the depot, whichever code is typed and after 0, 1 or 2 wrong codes.
  - At 73 hours with 3 wrong codes already typed, it also goes back to the depot.

**One change outside the test file:** I added `Codable` to `PickupResult` in `Pickup.swift`. This lets cells like `wrongCode(attemptsLeft: 2)` be read directly. It adds no behaviour.

**Assumptions** (also written in the doc comments):
- `hoursSinceDelivery` is whole hours, so 72 is still inside the window and 73 is the first hour outside it.
- If a parcel is both blocked and past 72 hours, the locker reports `returnedToDepot`, because the courier has already taken the parcel. You didn't say which should win, so please confirm.
- "Blocked" means `wrongCodesInARow == 3`.
- The 72-hour limit is in the doc comment, not a column. The stub has no window constant for the test to check a column against.
- Malformed codes, such as five digits or letters, are treated as ordinary wrong codes and have no rows of their own.

