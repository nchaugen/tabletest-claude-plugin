# Narration — 38

The agent's visible narration, thinking cost and file writes, in order, distilled from
`conversation.jsonl`. Thinking text appears when the run asked the CLI for a summary
(`thinking_display: "summarized"`); the per-block token counts appear either way.

_[~1,413 thinking tokens (194 deltas)]_

> (thinking) I'm weighing whether to add Codable and CaseIterable conformances to the app's enum types, since Fabric's types don't provide them. The skill suggests adding them in the app source, which is a small change, though it sits a bit awkwardly with the idea that the washing machine code is already done. I'm considering a test-local alternative as well.
> 
> I'm working out the boundary cases for the temperature rule, pairing a value at the limit with one just past it for each fabric, since each fabric has its own limit. I'll keep the at-limit rows as runs and the past-limit rows as refusals, and make sure the run durations are computed correctly for each case.
> 
> I'm now writing the second table, covering wash time by fabric and temperature. I'm treating the extra 10 minutes per full 10 °C above 40 as applying only to complete steps, so the in-between values fall to the lower step, and I'm adding rows for 30, 49, and 50 to pin that down.
> 
> I'm laying out the cotton temperature ladder at 40, 50, 60 and 70 °C, where each step adds 10 minutes, and carrying it up to the 90 °C limit. Then I'm deciding whether the other fabrics need their own ladder or just one row each at 40 °C and at their limits.
> 
> I'm settling on the options table now: no extras, prewash alone, rinse alone, and both together for cotton, plus a wool row with both options to show the base changes by fabric.
> 
> I'm settling the delay cases now: no delay should give a plain run time, while a set number of hours should produce a scheduled start that carries the run time. I'm also checking how a delay combines with the options, using a cotton case at 60 °C with prewash and an 8-hour start.
> 
> I'm now deciding how the delay and temperature cases interact, so I'm planning a wool case at 60 °C with a 2-hour delay, where the refusal should win over scheduling. For the tables that follow, I'll include a temperature column in each so every row builds the full program.
> 
> I'm settling on the test shape now: a Codable cell for each outcome, one body shape across all the tables, and a title on each table written in the usual form. I'll add CaseIterable to Fabric and Codable to WashOutcome in the app source, since that's the minimal change the skill prescribes.

I wrote four `@TableTest` tables for `WashPlanner.outcome(of:)` in `Tests/WashingMachineTests/WashPlannerTests.swift`. All 25 cases pass against the existing planner.

**One change outside the test file.** The table cells needed `Fabric` and `WashOutcome` to be readable, so I made two small edits in `Sources/WashingMachine/WashPlanner.swift`. `Fabric` now conforms to `CaseIterable` and `WashOutcome` to `Codable`. Both conformances are synthesised and change no behaviour. They let a cell say `cotton` or `runs(minutes: 170)`.

**The tables:**
1. **Refusal (6 cases):** each fabric has its own pair around its limit: 90/91 for cotton, 60/61 for synthetics, 40/41 for wool.
2. **Wash time (9 cases):** each fabric's base time at 40 °C. The cotton ladder runs 30, 40, 49, 50, 60 and 70 °C. Synthetics and wool each add one row.
3. **Options (6 cases):** prewash alone, extra rinse alone, both, and both again on another fabric and at a higher temperature.
4. **Delayed start (4 cases):** a blank delay runs now, a delay schedules with the same minutes, and a too-hot program on a delay is still refused.

**Assumptions where the doc comment is silent.** Each is written in the relevant test's doc comment, and the planner matches all of them:
- "Each 10 °C above 40" counts full steps only, so 49 °C adds nothing.
- A temperature below 40 °C adds nothing and takes the base time.
- A refusal takes precedence over a delayed start.

I didn't test a delay of 0 hours, because the rules don't say whether that means "now" or "scheduled in 0 hours".

**Known compromise.** The refusal table also shows the wash time on its accepted rows, because `outcome(of:)` returns refusal and time together and I can't split them without a narrower function.

