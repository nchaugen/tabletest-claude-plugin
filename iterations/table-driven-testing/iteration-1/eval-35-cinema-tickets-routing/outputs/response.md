Compiles cleanly and all 13 rows fail with `UnsupportedOperationException`, confirming the stub is untouched and the tests are correctly wired.

I wrote two `@TableTest` methods in `src/test/java/com/example/cinema/TicketPricerTest.java`, added the `tabletest-junit` dependency to `build.gradle`, and left `TicketPricer` unimplemented:

- **`shouldPriceByAgeTier`** — the age-tier rule (child/adult/senior), non-matinee, with boundary rows at 11/12 and 64/65 and value sets grouping same-priced ages within a tier.
- **`shouldApplyMatineeDiscount`** — the -2 euro matinee reduction, shown as representative rows across each age tier.

Assumptions (documented in each table's `@Description`): child = under 12, adult = 12–64, senior = 65+; matinee discount is a flat 2 euros off regardless of age tier.