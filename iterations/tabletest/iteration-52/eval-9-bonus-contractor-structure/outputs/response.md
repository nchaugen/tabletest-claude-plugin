The test compiles correctly and generates all 6 expected scenarios (the value set expanded the CONTRACTOR row into 2 cases). All fail with `UnsupportedOperationException` as expected, since the calculator is still a stub — this confirms the tests are wired correctly and will pass once the bonus logic is implemented.

**Summary of assumptions** (documented in the test's `@Description`):
- `calculateBonusPercentage` returns the percentage as a whole number (`15` for 15%), not a fraction (`0.15`).

**What was done:**
- Added `org.tabletest:tabletest-junit:1.2.2` to `build.gradle`.
- Created `src/test/java/com/example/BonusCalculatorTest.java` with one `@TableTest` table (one rule: bonus percentage from level + department). CONTRACTOR's "regardless of department" behavior is expressed as a value set (`{SALES, ENGINEERING}`), expanding to 6 total cases covering every level/department combination.
- Left `BonusCalculator` as the existing stub (throws `UnsupportedOperationException`) — logic implementation was explicitly out of scope.