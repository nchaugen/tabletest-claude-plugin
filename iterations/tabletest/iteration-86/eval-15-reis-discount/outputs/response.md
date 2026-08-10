All tests compile and run — every generated case fails with exactly `UnsupportedOperationException`, confirming the tables are correctly designed and only the (intentionally unimplemented) production logic is missing.

## Summary

Added Reis discount tests before implementation, split into three focused concerns per the ladder/classification/derivation seams the rules naturally have:

- **`ReisDiscountLadder`** + `ReisDiscountLadderTest` — pure tier arithmetic: `int` travel count → `DiscountPercentage`, 17 rows covering every 5% tier boundary from both sides up to the 40% cap.
- **`SingleTicketTravelCount`** + `SingleTicketTravelCountTest` — derives the rolling count from raw `PastPurchase` history: window edge (30 vs 31 days ago), filters out non-`SINGLE` ticket types, handles empty/mixed history.
- **`SingleTicketDiscountCalculator`** + `SingleTicketDiscountCalculatorTest` — routes `CHILD` to the flat 20% (travel count proven irrelevant via a value set) vs. `ADULT`/`SENIOR` to the Reis ladder; plus one plain `@Test` wiring the full public entry point (history + category + time) end-to-end.

Key assumption, documented in the `@Description` blocks: the rolling 30-day count used for the ladder **includes the ticket currently being purchased** (so ticket #5 is the first discounted one, per "the first discount applies to ticket number five").

All production classes are stubs (`throw new UnsupportedOperationException`) as instructed — logic isn't implemented yet. One gotcha found and fixed along the way: `@TypeConverter` methods are only discovered in **public** test classes; the initial package-private classes silently failed conversion.

`gradle test` → 31 test cases, all failing with `UnsupportedOperationException` (expected red phase). Added `tabletest-junit:1.2.2` to `build.gradle`.