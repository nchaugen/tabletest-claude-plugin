The tests compile and generate all 10 expected rows (7 age-tier + 3 matinee), each failing with `UnsupportedOperationException` from the unimplemented stub — exactly as intended for TDD.

## Summary

Added `org.tabletest:tabletest-junit:1.2.1` to `build.gradle` and wrote `src/test/java/com/example/cinema/TicketPricerTest.java` with two `@TableTest` methods, splitting the "age tier" and "matinee discount" concerns per the tabletest skill:

- **`shouldPriceByAgeTier`** — 7 rows covering child/adult/senior tiers with boundary values (11 vs 12, 64 vs 65), matinee fixed to `false`.
- **`shouldApplyMatineeDiscount`** — 3 rows (one per tier) confirming the flat 2-euro discount applies uniformly, matinee fixed to `true`.

`TicketPricer` stub was left untouched (still throws `UnsupportedOperationException`). Ran `gradle test` — it compiles cleanly and all 10 generated test cases fail against the stub as expected, ready for the pricing logic to be implemented next.