All 3 tests pass under the single `@TableTest`.

**Summary of changes:**
- `pom.xml`: added the `tabletest-junit` dependency (JUnit Jupiter was already 6.0.3, which satisfies the ≥5.11 requirement)
- `DiscountServiceTest.java`: replaced the three `@Test` methods with one `@TableTest`, using `Tier`/`Amount` as inputs and `Discount?` as the expectation column. Kept `Amount` as an explicit column (value `100` in all rows) rather than hiding it in the method body, since the discount is a percentage of that amount.

Ran `mvn test` — 3/3 pass, confirming the table preserves the original coverage.