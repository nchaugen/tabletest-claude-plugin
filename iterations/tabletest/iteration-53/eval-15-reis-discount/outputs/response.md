Everything compiles cleanly and the table rows parse and expand correctly. Here's a summary of what was built.

## Interpretation of the ambiguous rules

The trickiest part of the spec was reconciling "first discount at ticket 5," "5% every 5 trips during the first 30 days," "levels off after 30 days," and "may decrease if you travel less" into one consistent rule. I resolved it as:

**`purchaseNumber` = count of `SINGLE` tickets purchased by this traveler in the trailing 30-day window (inclusive of the boundary), plus the ticket now being purchased. `discount = min(floor(purchaseNumber / 5) × 5%, 40%)`.**

This single formula satisfies every stated behavior for free: tickets 1–4 give 0%, ticket 5 gives 5% (first discount), every 5th ticket adds 5%, it naturally plateaus once travel is steady (old purchases roll off the 30-day window as new ones enter), it naturally decreases if travel drops, and it caps at 40%. No separate "first 30 days vs. after" logic was needed — it's one rolling window throughout.

Other assumptions: only `SINGLE` tickets count toward the tier (weekly/monthly don't); `ADULT` and `SENIOR` share one combined count; zone never affects the discount (I omitted `ZoneValidity` from the calculator's signature entirely, so this is true by construction, not just by test); children get a flat 20% independent of any history.

## What was added

- `build.gradle` — added the `tabletest-junit` dependency
- `ReisTravelFrequency` / `ReisTravelFrequencyImpl` — counts recent single-ticket purchases
- `ReisDiscountLadder` / `ReisDiscountLadderImpl` — maps a purchase number to a discount tier
- `ReisDiscountCalculator` / `ReisDiscountCalculatorImpl` — dispatches by traveler category, composing the two above
- `src/test/java/com/example/ReisDiscountCalculatorTest.java` — three `@TableTest` tables, one per concern (window counting, ladder arithmetic, category dispatch), each with its assumptions documented in `@Description`

All `Impl` classes throw `UnsupportedOperationException` as stubs, per your request not to implement the logic yet. `gradle test` confirms: compiles cleanly, all 22 generated test cases run (proving the tables parse correctly and wire up to the right types), and all fail on the stub exception as expected — ready for someone to implement against.