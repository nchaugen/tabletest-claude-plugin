Compiles cleanly and all 10 rows fail as expected, each hitting the stub's `UnsupportedOperationException`.

## Summary

Added `src/test/java/com/example/cinema/TicketPricerTest.java` with one `@TableTest` (10 rows) covering `priceInEuros(int age, boolean matinee)`:

- Age-tier boundaries on both sides: 11/12 (child→adult) and 64/65 (adult→senior)
- One representative value per tier (5, 30, 80)
- Matinee discount exercised once per tier (5/true→6, 30/true→12, 65/true→8) to prove the €2 reduction combines correctly with every price band

Also added the `org.tabletest:tabletest-junit:1.2.2` test dependency to `build.gradle` (JUnit Jupiter was already 6.0.3, well above the 5.11 minimum). Verified with `gradle test`: it compiles and all 10 cases fail against the stub's `UnsupportedOperationException`, as expected until the pricing logic is implemented.