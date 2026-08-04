Compiles cleanly and all 7 rows fail as expected against the unimplemented stub (`UnsupportedOperationException`), confirming the tests are wired up correctly and will pass once pricing logic is added.

**Summary of what was done:**
- Added `org.tabletest:tabletest-junit:1.2.2` test dependency to `build.gradle` (JUnit Jupiter was already at 6.0.3, well above the 5.11 minimum).
- Created `src/test/java/com/example/cinema/TicketPricerTest.java` with one `@TableTest` covering `priceInEuros(age, matinee)`.

**Design notes:**
- Age tier and matinee discount are treated as one rule (two contributions combining into one final price), so they share a single table rather than being split — matches the "combining contributions" pattern rather than being two unrelated concerns.
- Rows cover both age-tier boundaries (11/12 child→adult, 64/65 adult→senior) without matinee, then verify the flat 2-euro matinee discount applies uniformly across each of the three tiers (child/adult/senior) at representative ages.
- Assumption: "children under 12" is exclusive of 12, "seniors 65 and over" is inclusive of 65 — documented in the `@Description`.
- `TicketPricer` stub is untouched, as requested — all 7 cases currently fail with `UnsupportedOperationException`, and will pass once pricing logic is implemented.