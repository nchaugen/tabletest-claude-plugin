All 3 test cases pass under the single `@TableTest`. Summary:

- Added the `tabletest-junit` dependency to `pom.xml`.
- Replaced the three `@Test` methods in `DiscountServiceTest.java` with one `@TableTest` method (`calculatesDiscount`) covering Gold/Silver/Bronze tiers as table rows, with `Discount?` as the expectation column.
- Verified with `mvn test`: 3 tests run, 0 failures.

Assumption made (no follow-up possible): kept the table to the three original scenarios only, without adding extra edge cases (e.g. unknown tier, negative amount) since those weren't in the original tests and weren't requested.