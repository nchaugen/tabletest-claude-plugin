# Narration — eval-15-reis-discount

The agent's visible narration and file writes, in order, distilled from
`conversation.jsonl`. Thinking text is absent unless the model returns it in the
clear (Claude 5-family models encrypt it).

I'll invoke the tabletest skill to work through the requirements decomposition and table test design for this Java project.

I'll explore the existing project structure first to understand what interfaces are already in place for ticket purchase history.

Good, Gradle 9.6.1 with JDK 26 available — should support the project's Java 25 target. Now let me design the test suite. First, let me check the type-converters reference to confirm custom converters apply to collection elements.

Let me verify network access and confirm the TableTest library's collection-of-custom-type behavior with a small spike before committing to the full design.

**Edit ./build.gradle (edit)**

The spike confirms custom `@TypeConverter` works with `List<PastPurchase>` using a dash-separated format (colons trigger map-syntax parsing, so I'll avoid them). Now removing the spike and building the real test suite.

Now I'll write the shared test fixture converter and the three test classes, one concern at a time, along with stub implementation classes.

**Write ./src/test/java/com/example/PastPurchaseFixtures.java (23 lines)**

**Edit ./src/test/java/com/example/PastPurchaseFixtures.java (edit)**

Now the Concern A test — trailing single-ticket count derivation:

**Write ./src/test/java/com/example/SingleTicketFrequencyCounterTest.java (36 lines)**

Now the Concern B test — discount ladder tier:

**Write ./src/test/java/com/example/ReisDiscountLadderTest.java (41 lines)**

Now the combining test — traveler-category routing and zone independence:

**Write ./src/test/java/com/example/ReisDiscountCalculatorTest.java (32 lines)**

Now the stub implementation classes (signatures only, no logic — per the skill's test-first workflow):

**Write ./src/main/java/com/example/SingleTicketFrequencyCounter.java (12 lines)**

**Write ./src/main/java/com/example/ReisDiscountLadder.java (9 lines)**

**Write ./src/main/java/com/example/ReisDiscountCalculator.java (13 lines)**

Now let's compile and run to confirm everything compiles and the tests fail correctly (with `UnsupportedOperationException`, not compile/table-parsing errors):

Everything compiles and parses correctly — all 30 test cases fail with `UnsupportedOperationException` as expected from stub implementations, confirming the table designs and type conversions are valid. Let's verify the row/test counts match my design (6 + 12 + 12 = 30) and double check the whole file listing.

I need to fix a description inaccuracy — the 9-ticket history is in the *first* row (child), not the second.

**Edit ./src/test/java/com/example/ReisDiscountCalculatorTest.java (edit)**

Let's re-run the full suite once more to confirm everything still passes parsing after the edit, and do a final review of all files.

All 30 test cases still parse and fail correctly (stub `UnsupportedOperationException`, not parse errors). Let's do a final review of the full set of new/changed files.

## Summary

I decomposed the Reis discount rule into three concerns, each with its own `@TableTest`, and added stub implementations (just `UnsupportedOperationException`, no logic) so everything compiles and runs:

1. **`SingleTicketFrequencyCounterTest`** — derives the trailing-30-day single-ticket count from raw purchase history (6 rows: empty history, non-single types excluded, multiple singles, the 30/31-day boundary, and a mixed case).
2. **`ReisDiscountLadderTest`** — the ladder rule itself: given a prior single-ticket count, what discount applies (12 rows, one per tier from 0% up to the 40% cap, including the boundary where the 5th ticket first gets a discount and the boundary where the cap kicks in).
3. **`ReisDiscountCalculatorTest`** — the combining/precedence table: child gets a flat 20% regardless of history or zone (proven against a 9-ticket history that would otherwise earn an adult a 10% ladder discount), adult/senior defer to the ladder regardless of zone.

Key assumptions, recorded in each table's `@Description`:
- The 30-day window is inclusive of the boundary day (a ticket bought exactly 30 days ago counts; 31 days ago doesn't).
- The trailing count is of *all* single tickets by the traveler, regardless of what category they were purchased under.
- Discount is recomputed fresh from the current trailing count on every purchase (not carried over from a prior discount) — this is what makes "levels off," "decreases if you travel less," and "increases if you travel more" fall out for free rather than needing separate logic.
- Ticket number = prior count + 1, so the discount first appears on the 5th ticket, matching the spec.

I verified the design against the real `tabletest-junit` library (added it to `build.gradle`) by running `gradle test`: all 30 generated cases parse correctly and fail with the stub's `UnsupportedOperationException`, confirming the table/type-converter design is sound before any real logic is written. One thing worth noting: I discovered mid-way that `:` inside a list element gets parsed as map syntax by TableTest, so the purchase-history fixture format uses `daysAgo-TicketType` (dash) instead of a colon.

Not done (by design, per test-first workflow): the actual discount logic in `ReisDiscountLadder`, `SingleTicketFrequencyCounter`, and `ReisDiscountCalculator` — those still throw `UnsupportedOperationException` pending your review of the test design.

