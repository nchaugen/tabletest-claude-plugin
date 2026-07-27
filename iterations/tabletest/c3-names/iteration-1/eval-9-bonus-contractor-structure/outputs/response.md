All 6 test cases generated correctly (the value set expanded row 5 into 2 rows), and they fail as expected against the unimplemented stub — confirming the tests are wired correctly and ready for the bonus logic to be implemented against.

**Summary:**
- Added `tabletest-junit:1.2.2` test dependency to `build.gradle`.
- Wrote `src/test/java/com/example/BonusCalculatorTest.java` with one `@TableTest` covering all 5 rules (4 level×department combos + a `{SALES, ENGINEERING}` value set for the contractor "regardless of department" rule), documenting the assumption that the percentage is returned as a whole number (e.g. `15` not `0.15`) via `@Description`.
- Left `BonusCalculator` as the unimplemented stub — compiles fine, tests fail with `UnsupportedOperationException` as expected since logic isn't implemented yet.