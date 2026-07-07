## Summary

I decomposed the Reis discount feature into three testable concerns and wrote `@TableTest` suites for each, following the tabletest skill's "write tests before implementing" workflow:

1. **`TripNumberCalculator`** (`src/test/.../TripNumberCalculatorTest.java`) — counts single-ticket purchases in a trailing window (7 rows: no history, tiered counts, window boundary at/past 30 days, non-single tickets ignored, narrower window).
2. **`ReisDiscountLadder`** (`ReisDiscountLadderTest.java`) — pure ladder arithmetic from trip number to discount %, with tiers grouped via value sets and boundary rows at/past the 40% cap.
3. **`ReisDiscountCalculator`** (`ReisDiscountCalculatorTest.java`) — the composed entry point: flat 20% for children (zone/history-independent), and the Reis ladder for adults/seniors (zone-independent, with a `Trip Number?` traceability column verified against the real `TripNumberCalculator`).

I added `tabletest-junit:1.2.1` to `build.gradle`, stubbed the three new classes to throw `UnsupportedOperationException`, and — as a validation step only — temporarily implemented the real logic to confirm all 43 generated test cases pass with correct expected values, then reverted the implementations to stubs so the repo is left in "tests written, logic not yet implemented" state as requested. Final `gradle test` run: 43/43 fail solely on `UnsupportedOperationException`, confirming the tables compile and are numerically correct.

Assumptions made (documented in `TODO.md`): the calculator only handles single-ticket purchases (ticket type isn't a parameter), the 30-day window is left-closed/right-open, only past `SINGLE` history entries count, purchase history is assumed pre-scoped to the traveler, the ladder formula is `floor(tripNumber/5)*5` capped at 40%, and there's no ratchet (discount is recomputed fresh each purchase, so it can rise or fall).