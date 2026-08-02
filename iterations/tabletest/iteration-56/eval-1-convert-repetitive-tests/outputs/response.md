Full suite passes. Summary:

- `pom.xml`: added `tabletest-junit:1.2.2` test dependency (JUnit 6.0.3 already satisfies the ≥5.11 requirement).
- `DiscountServiceTest.java`: replaced the three `@Test` methods with one `@TableTest` (`calculatesDiscountByTier`), with `Tier` and `Amount` as input columns (both parameters of `calculate`) and `Discount?` as the expectation column. `Amount` is held at `100` for all rows since the original tests never varied it.

Ran `mvn test` — all tests pass, no regressions.