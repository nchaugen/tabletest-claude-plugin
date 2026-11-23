I wrote four `@TableTest` tables for `WashPlanner.outcome(of:)` in `Tests/WashingMachineTests/WashPlannerTests.swift`. `swift build --build-tests` and `swift test` both pass.

**The tables**
- **`refusesATemperatureAboveTheFabricLimit`:** each fabric is tested at its limit and one degree above it. A `Refused Above?` column holds the limit, and is blank when the wash is accepted.
- **`lengthensTheWashForFabricAndHeat`:**
  - The three base times at 40 °C.
  - 30 °C, which subtracts nothing.
  - 49 °C against 50 °C.
  - 60 °C and the hottest allowed cotton and synthetics washes.
- **`addsTimeForPrewashAndExtraRinse`:** all four combinations on cotton, plus both options on wool.
- **`schedulesADelayedWashWithTheSameDuration`:** a wash with no delay, delayed washes with different durations, a delayed hot wash, and a refused wash that has a delay set.

**Change to the app code**
You said the washing machine code is done, but the cells couldn't convert without two additions. I added `CaseIterable` to `Fabric` and `Codable` to `WashOutcome` in `Sources/WashingMachine/WashPlanner.swift`. These are synthesised conformances and change no behaviour. `WashOutcome` needs `Codable` so cells like `scheduled(inHours: 6, minutes: 120)` can be read.

**Assumptions**
- The doc comment doesn't say what happens between steps, so I assumed temperatures are whole degrees and extra time counts whole 10 °C steps above 40 °C. 49 °C adds nothing and 50 °C adds 10 minutes. If you meant a proportional rule, the 49 °C row would need to change.
- A 0-hour delay isn't specified, so I left it untested.
- The doc comment doesn't say which rule wins when a delayed program is also too hot. I took refusal to win, which is what the code does, and pinned it in the last row of the delay table.