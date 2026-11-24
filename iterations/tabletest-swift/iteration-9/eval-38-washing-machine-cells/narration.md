# Narration — 38

The agent's visible narration, thinking cost and file writes, in order, distilled from
`conversation.jsonl`. Thinking text appears when the run asked the CLI for a summary
(`thinking_display: "summarized"`); the per-block token counts appear either way.

_[~1,250 thinking tokens (163 deltas)]_

> (thinking) I'm deciding the Fabric conversion: the app enum needs to drop raw values, so I'll add CaseIterable to Fabric in the app source, since the skill calls for it and it changes no behavior. WashOutcome also needs Codable because it has associated values, and it's already Sendable. I'm weighing that against the user saying the washing machine code is done.
> 
> I'm working out how the table cases should be laid out, weighing a refusal table keyed on temperature against one that mixes in the runtime outcome. The straddle rows would show the runs result at the limit, and I'm checking whether that blends two separate rules.
> 
> I'm settling the table layout: one limit per fabric, with a pair of temperatures on either side of each, so every fabric gets a straddling check. For the duration table I'm noting that temperatures under 40 add nothing, and that I need to decide how to treat a value like 49, since the added time only moves in full 10-degree steps.
> 
> Next I'm filling in the duration table: base times per fabric at 40 degrees, then temperature rows using cotton, with one case below 40 and the 49 and 50 boundary. After that come the prewash and extra rinse rows, and I'm checking the synthetics case at 60 degrees against the refusal boundary.
> 
> I'm settling on a delay table next, with the same fabric and a scheduled start. A zero-hour delay still counts as scheduled, and a refusal should win over the delay, so a wool run at 60 degrees gives a too-hot outcome.
> 
> I'm laying out the delay cases now: no delay, a delayed cotton run at 8 hours, and a zero-hour delay that should still count as scheduled. I'm also adding a hot cotton run with the delay and a wool run at 60 degrees that should be refused.
> 
> I'm settling on the app source edits now: Fabric gets CaseIterable and WashOutcome gets Codable, with the two enum declarations spelled out. Next I'm just choosing the file titles and writing it out.

I wrote three `@TableTest` tables in `Tests/WashingMachineTests/WashPlannerTests.swift`, with 21 cases in all. `swift test` passes against the existing planner logic.

**Tables**
- **`refusesATemperatureAboveTheFabricLimit`** (6 cases): each fabric has its own limit, so each gets a pair at the limit and one degree past it. That is 90/91 for cotton, 60/61 for synthetics and 40/41 for wool.
- **`addsTimeForHeatPrewashAndExtraRinse`** (10 cases): it covers:
  - the base time for each fabric;
  - 30 °C, which doesn't shorten the wash;
  - 49 °C vs 50 °C, and 70 °C, for the 10 °C steps;
  - prewash alone, extra rinse alone, and both combined with heat.
- **`schedulesAWashWhenAStartDelayIsChosen`** (5 cases): no delay, a delay of 8 hours, a delay of 0 hours, a delay on a hotter wash, and a delay on a refused temperature.

**Assumptions** (also in the doc comments)
- Only each full 10 °C above 40 adds 10 minutes, so 49 °C adds nothing. The code does this, but the rule text doesn't say it.
- A delay of 0 hours counts as scheduled, because only a missing delay starts the wash now.
- A refused temperature takes precedence over a delayed start.

**One edit outside the tests:** I added a conformance to each of two types in `Sources/WashingMachine/WashPlanner.swift`. `Fabric` is now `CaseIterable` and `WashOutcome` is now `Codable`, so table cells like `cotton` and `runs(minutes: 120)` convert. The compiler synthesises both, so no behaviour changes. This is the TableTest skill's recommended route, but it does touch code you called done.

The `tabletest-format` command also reformatted both Swift files. I didn't check what changed in the source file.

