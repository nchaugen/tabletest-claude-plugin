All 6 test cases (5 rows, with the CONTRACTOR value set expanding to 2) run correctly and fail as expected against the stub `UnsupportedOperationException` — confirming the tests are wired up properly and ready for the implementation.

**Summary:**
- Added `org.tabletest:tabletest-junit:1.2.2` to `build.gradle` (JUnit was already 6.0.3, well above the required 5.11).
- Wrote `src/test/java/com/example/BonusCalculatorTest.java` with one `@TableTest` covering all 5 rules as a single decision table (one axis: level × department → bonus percentage), using a `{SALES, ENGINEERING}` value set for the CONTRACTOR row to assert department is genuinely irrelevant there.
- Assumption documented in `@Description`: bonus percentage is returned as a percentage value (`15.0` = 15%), not a fraction — stated since the method name doesn't disambiguate.
- Left `BonusCalculator.calculateBonusPercentage` as the existing stub (throws `UnsupportedOperationException`) — tests compile and correctly fail, ready for implementation next.