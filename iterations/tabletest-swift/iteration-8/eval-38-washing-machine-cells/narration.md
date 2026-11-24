# Narration — 38

The agent's visible narration, thinking cost and file writes, in order, distilled from
`conversation.jsonl`. Thinking text appears when the run asked the CLI for a summary
(`thinking_display: "summarized"`); the per-block token counts appear either way.

_[~164 thinking tokens (10 deltas)]_

> (thinking) The skill tells me to read references/table-design.md in full, so I'll open that file and the source material next.

_[~1,992 thinking tokens (255 deltas)]_

> (thinking) I'm settling on the design: Fabric needs CaseIterable, and since the tests use cases like `runs(minutes: 120)`, WashOutcome and Fabric both need Codable too.
> 
> I'm weighing whether to make Fabric CaseIterable alone or also Codable, and I'm leaning toward CaseIterable since it's enough for this. Then I'm sketching out how to lay out the temperature-limit cases as a table, with the fabric, temperature, and outcome as columns. The tricky part is that the outcome for accepted cases depends on minutes, so I'm considering whether to keep that arithmetic in the table and note it in a doc comment.
> 
> I'm going to lay out the refusal cases as a table, one row per fabric, with temperatures just past each limit, since the finest step is one degree. For cotton, that means 91 should come back as too hot against a limit of 90, and the same pattern applies to synthetics at 61 and wool at 41.
> 
> I'm sketching a second table that tracks how the wash duration changes with temperature, with fabric, temperature, and minutes as columns. I'll start each fabric at 40 °C, where the base time applies, and add steps above that, keeping the exact boundary points I'm confident the rules pin down and leaving out partial-degree cases the spec doesn't settle.
> 
> I'm now deciding which cases to include in the duration table: I'll cover cotton at 40, 50, 60, and 90 degrees, with the other fabrics at their base temperature, and I'm leaving out partial-degree values like 45 since the spec doesn't pin them down.
> 
> I'm now planning a third table that compares the optional add-ons, prewash and extra rinse, against a cotton load at 40 °C, with the combinations giving 120, 140, 135, and 155 minutes. I'm also asking whether fabric should vary there, since the add-on effect is meant to be the same for every fabric.
> 
> I'm now deciding whether the delayed-start cases should be a table too, with a blank delay giving an immediate run and a set delay giving a scheduled outcome, and I'm weighing how to handle zero hours since the spec leaves that case open.
> 
> I'm going to cover the delayed-start cases next: a run with no delay, a one-hour delay, and an eight-hour delay, plus a combined row showing a delayed run with prewash and rinse still lands at 155 minutes. I also want to check how a too-hot load behaves when it's delayed, since that decides which outcome wins.
> 
> I'm laying out the delay table with columns for fabric, temperature, prewash, extra rinse, delay hours, and outcome, and I'm weighing whether the options should be columns or held at their defaults with a note in the doc comment.
> 
> I'm setting up the delay table now, starting with a cotton load at 40 °C that runs immediately with no delay.
> 
> I'm now writing out the delay table's remaining rows: a delay of zero hours, a delayed run with prewash and rinse that should still total 155 minutes, and a too-hot wool load with a delay that should come back as too hot.
> 
> I'm adding the CaseIterable conformance to Fabric and the Codable conformance the tests need, then writing doc comments on the functions and settling on the scenario names.

I wrote four `@TableTest` tables for `WashPlanner.outcome(of:)` in `Tests/WashingMachineTests/WashPlannerTests.swift`. `swift test` passes all 24 cases.

**One change outside the test file.** The table cells (`cotton`, `runs(minutes: 170)`) need conversion support. I added `CaseIterable` to `Fabric` and `Codable` to `WashOutcome` in `Sources/WashingMachine/WashPlanner.swift`. These are synthesised conformances, so no behaviour changed. If you'd rather leave the app code untouched, I can add hand-written conversions in the test target instead.

**The tables**
- **`refusesATemperatureAboveTheFabricLimit` (6 rows):** the last accepted temperature and the first refused one (1 °C higher) for each fabric. The expected cell shows the fabric's limit, e.g. `tooHot(limit: 90)`.
- **`addsTenMinutesForEachTenDegreesAboveForty` (7 rows):** cotton at 30, 40, 50, 60 and 70 °C, plus the 40 °C base time for synthetics and wool.
- **`addsTimeForPrewashAndExtraRinse` (6 rows):** all four option combinations on cotton, both options on wool, and both options on hot cotton.
- **`schedulesADelayedWashWithoutChangingItsDuration` (5 rows):**
  - a blank delay (starts now);
  - a delay of 0 hours;
  - a delay of 8 hours;
  - a delay combined with both options;
  - a too-hot wash that has a delay, which is still refused.

**Assumptions** (the doc comments also say these)
- A temperature below 40 °C adds no time. The rules only say "each 10 °C above 40", but the code does this and I assert it with the 30 °C row.
- A delay of 0 hours counts as scheduled, not as "starts now". The rules only say a missing delay starts now.
- I only used temperatures in steps of 10 °C for the duration table. The rules don't say what a part-step like 45 °C should do, so I left it untested.
- The accepted outcomes in the first table include their minutes. The planner only returns the whole outcome, so I couldn't separate refusal from duration there.

