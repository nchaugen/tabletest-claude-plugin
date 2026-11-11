# Narration — 38

The agent's visible narration, thinking cost and file writes, in order, distilled from
`conversation.jsonl`. Thinking text appears when the run asked the CLI for a summary
(`thinking_display: "summarized"`); the per-block token counts appear either way.

_[~1,318 thinking tokens (185 deltas)]_

> (thinking) I need to convert the Fabric types since they lack raw values and aren't Codable or CaseIterable. The skill points toward adding a conformance to the app type, and the washing machine code is apparently done, though the skill's first step is one word added to the app type. WashOutcome also needs Codable for the cells, so I'll add those minimal conformances to the app source.
> 
> I'm weighing whether to change the app's own types or keep the edits in the test target. The skill's first step adds CaseIterable or Codable to the app type, and I lean toward that for Fabric since it's simpler than synthesizing Codable for an enum without raw values. WashOutcome will need Codable for the cells either way. I'm also starting to sketch the refusal table: one temperature limit per fabric, with rows on both sides of each boundary.
> 
> I'm settling on a refusal table with a boundary pair per fabric, one at the temperature limit and one just over it. Cotton, synthetics, and wool each get their own ladder, since their limits differ, and a duration table can cover the accepted runs separately.
> 
> I'm sketching the duration table next: a base runtime per fabric at 40 °C, plus a step for each higher temperature. I want three consecutive temperatures per fabric so the increment is clear, and I'll also check whether temperatures below 40 keep the base time rather than subtracting. The one open question is how to handle 45 °C, since the 10 °C step rule doesn't cleanly cover it, so I'll stick to multiples of ten and note the assumption.
> 
> I'm settling on the duration table: cotton at 30, 40, 50, 60 and 90 °C, synthetics at 40 and 60, and wool at 30 and 40, with the fabric-specific base times and the per-step increase.
> 
> I'm sketching the delay cases now: no delay, a one-hour delay, and a twelve-hour delay. I also want one row showing that a too-hot outcome still applies when a delay is set, and I'll decide whether to pin the zero-hour case.
> 
> I'm now working out the delay cases: a 3-hour and a 12-hour start, each keeping the same wash minutes as the no-delay run, and I'm treating the Int? delay column as the input for the fixture. I'm also planning to add CaseIterable to Fabric and Codable to WashOutcome in the app source.
> 
> Writing the scheduled-outcome expectations now, with the delay value and minutes kept together.

I wrote four `@TableTest` tables in `Tests/WashingMachineTests/WashPlannerTests.swift`, and all 25 cases pass against the existing planner.

**The tables:**
- **`refusesATemperatureAboveTheFabricLimit`** (6 rows): each fabric gets a pair at its limit and one step above it, since the limits differ (cotton 90, synthetics 60, wool 40).
- **`timesAWashFromFabricAndTemperature`** (9 rows): base times at 40 °C, plus 10 minutes per 10 °C above 40. Cotton runs through 40, 50, 60 and 70 °C, so the rule reads as per step rather than a flat extra. A colder wash takes no less time.
- **`addsTimeForPrewashAndExtraRinse`** (5 rows): all four option combinations on cotton, plus both options on wool to show the additions don't depend on fabric.
- **`schedulesADelayedStartWithTheSameWashTime`** (5 rows): start now, two delays, a different fabric, and a delay with a too-hot temperature. That last row shows a refusal beats scheduling.

**One change to the app code:** I added `CaseIterable` to `Fabric` and `Codable` to `WashOutcome` in `Sources/WashingMachine/WashPlanner.swift`. TableTest needs them to read cells like `cotton` and `scheduled(inHours: 3, minutes: 140)`. They add no behaviour. If you'd rather not touch the app, say so and I'll move the conversion into the test target.

**Assumptions:**
- **Temperatures:** I only use multiples of 10 °C, because the doc comment doesn't say what 45 °C should do. The code currently rounds it down.
- **Delay:** I left out a 0-hour delay, which is also unspecified.
- **Refusal table:** the rows at the limit show the full wash time, so that table checks minutes as well as refusal.

