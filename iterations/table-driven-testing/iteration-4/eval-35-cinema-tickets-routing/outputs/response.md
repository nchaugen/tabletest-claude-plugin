All 12 generated test cases compile and fail red as expected (the stub throws `UnsupportedOperationException`), confirming the tests correctly target the unimplemented `TicketPricer.priceInEuros`.

## Summary

Added `org.tabletest:tabletest-junit:1.2.2` to `build.gradle`, and wrote `src/test/java/com/example/cinema/TicketPricerTest.java` with a single `@TableTest` covering the pricing rule:

- Three tier-classification rows use value sets spanning each age band including its boundaries (`{0,6,11}` → child/8, `{12,35,64}` → adult/14, `{65,90,120}` → senior/10), all at non-matinee.
- Three matinee rows (one representative age per tier) show the uniform €2 discount (8→6, 14→12, 10→8), so each row states its value directly rather than requiring the reader to combine tier lookup with arithmetic.
- `@Description` documents the assumption that negative ages are out of scope, since the given rules don't address them.

Verified with `gradle compileTestJava` (compiles clean) and `gradle test` (12 generated cases, all fail red against the `UnsupportedOperationException` stub, as intended).