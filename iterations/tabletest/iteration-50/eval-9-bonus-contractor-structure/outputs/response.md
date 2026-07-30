The table compiles correctly and generates 6 test cases (the value set expands the CONTRACTOR row into 2), all failing as expected against the unimplemented stub — exactly the pre-implementation state requested.

## Summary

Added `org.tabletest:tabletest-junit:1.2.2` to `build.gradle` and created `src/test/java/com/example/BonusCalculatorTest.java` with one `@TableTest` covering all 5 rules as 4 direct rows plus a `{SALES, ENGINEERING}` value set for the CONTRACTOR "regardless of department" case (6 generated test cases total). `BonusCalculator` was left as the existing stub (throws `UnsupportedOperationException`), so all 6 cases currently fail — ready for you to implement the logic next.

One assumption I documented in `@Description`: `calculateBonusPercentage` returns percentage points (e.g. `15.0` for 15%), not a fraction (`0.15`) — worth confirming before implementing.