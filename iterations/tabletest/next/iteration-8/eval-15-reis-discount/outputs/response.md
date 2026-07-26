I've written the tests and stub production code for the Reis discount feature. Since Bash is non-functional in this session (`EPERM` on session-env setup — confirmed even for background agents), I could not actually run `gradle test` to confirm compilation; I hand-verified the code carefully instead. Flagging that explicitly rather than claiming a green build.

## What was added

**`build.gradle`** — added `testImplementation 'org.tabletest:tabletest-junit:1.2.2'` (project already had JUnit Jupiter 6.0.3, which satisfies the ≥5.11 requirement).

**Production stubs** (`src/main/java/com/example/reis/`), all throwing `UnsupportedOperationException` — no logic implemented yet, per your instructions:
- `PassengerType` (ADULT, SENIOR, CHILD), `TicketType` (SINGLE, PERIOD), `TicketPurchase` (record), `TicketPurchaseHistory` — the interface for the existing purchase-history lookup, since none existed in the (actually empty) skeleton project.
- `ReisTravelFrequency.countSingleTicketsInLast30Days(...)` — derives the trailing-30-day single-ticket count from raw history.
- `ReisDiscountLadder.discountFor(PassengerType, tripNumber)` — the tier/flat-rate rule.
- `ReisDiscountCalculator.calculateDiscount(...)` — wires the two together.

**Tests** (`src/test/java/com/example/reis/`):
- `ReisTravelFrequencyTest` — `@TableTest` covering the 30-day window derivation: empty history, multiple qualifying purchases, the 30-vs-31-day boundary, and single-vs-period-ticket filtering.
- `ReisDiscountLadderTest` — `@TableTest` covering the actual discount rule: child flat 20% (trip count irrelevant, shown via a value set), and every adult/senior tier from 0% to the 40% cap, one row per tier using value sets for both the boundary trip counts and the ADULT/SENIOR equivalence.
- `ReisDiscountCalculatorTest` — two plain `@Test`s proving the wiring (window-filtering + ladder lookup) composes correctly; not a full table since it's pure delegation, not a new rule.
- `FakeTicketPurchaseHistory` — shared test double.

## Key assumptions (also documented in the `@Description`s)
- "Last 30 days, measured at time of purchase" is a **rolling window** ending at purchase time — this one rule naturally explains both the ramp-up and the "levels off" behavior from the spec, so no separate first-30-days/after-30-days code path was needed.
- "Trip number" includes the ticket currently being purchased (prior count + 1), since "the first discount applies to ticket number five."
- The 30-day boundary is inclusive (exactly 30 days ago counts; 31 does not).
- Discount is modeled as a plain `int` percentage (0–40), not currency — computing the final ticket price is out of scope.

Since there's no `gradlew` wrapper in the repo, you'll need your own Gradle install to run `gradle test` and confirm these compile/fail-as-expected.