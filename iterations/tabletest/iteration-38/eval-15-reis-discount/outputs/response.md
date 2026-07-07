I've written the tests before implementing the Reis discount logic, following the tabletest skill's "feature description → tests → stubs" workflow.

**Design decomposition** (one table per concern, per the skill's guidance to separate rules from arithmetic and avoid traceability columns):

1. **`ReisWindowTest`** — pure windowing rule: counts `SINGLE` ticket purchases in the 30 days before a purchase time. Tests the exclusive boundary (exactly 30 days before doesn't count), filters out `WEEKLY`/`MONTHLY` tickets, and mixed-type history.
2. **`ReisLadderTest`** — pure arithmetic: maps a qualifying ticket count to a discount tier (5% every 5th ticket starting at ticket 5, capped at 40%). Tests every tier boundary and the cap.
3. **`ReisDiscountCalculatorTest`** — integration: wires the two together, plus the flat 20% child discount. Uses modest history sizes since boundaries are already covered by the two unit-level tables above.

**New production code** (`src/main/java/com/example/`): `ReisWindow`, `ReisLadder`, `ReisDiscountCalculator` — all stubs that `throw new UnsupportedOperationException()`, per your instruction to write tests before implementing.

**Key assumptions made** (documented in each `@Description`, since this is a non-interactive run):
- The ticket being purchased counts as the next ticket in the 30-day window (so 4 prior tickets → 5th ticket → first 5% discount).
- Only `SINGLE` tickets count toward the ladder; traveler category/zone of *past* purchases don't affect the count.
- Zone of the *current* ticket never affects the discount, so it's intentionally not a parameter of `calculateDiscount`.
- The "first 30 days ramps up / levels off / decreases with less travel" behavior all falls out naturally from the rolling 30-day window — no separate phase logic was needed.

**Verification**: I temporarily implemented the real logic to confirm all 23 table rows produce the expected values, then reverted the production code to stubs. Currently all 23 tests fail with `UnsupportedOperationException` — the correct "red" state for TDD. I also added the missing `tabletest-junit:1.2.1` dependency to `build.gradle`.

One thing worth flagging: I made a judgment call that past purchases in *any* traveler category count toward an adult/senior's ladder (not just past purchases made "as adult"). If your business rules actually restrict the count to same-category history, that's a one-line change to `ReisWindow`'s filter and a tweak to a couple of table rows.