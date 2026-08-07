The project compiles cleanly and all 7 table rows run and fail as expected, since `TicketPricer.priceInEuros` still throws `UnsupportedOperationException`.

I wrote `src/test/java/com/example/cinema/TicketPricerTest.java` with a single `@TableTest` covering age/matinee as one combining rule (base price by age category, minus a uniform matinee discount), added the `tabletest-junit` dependency to `build.gradle`, and verified:

- The table parses (format-check made no changes).
- `gradle test` compiles successfully and all 7 rows fail against the unimplemented stub, as intended.

Rows cover: both sides of the child/adult boundary (11/12), both sides of the adult/senior boundary (64/65), and one matinee-discount row per category (child/adult/senior), reusing the boundary ages to avoid redundant rows. Assumption documented in `@Description`: children are under 12, seniors are 65+, everyone else pays adult price.