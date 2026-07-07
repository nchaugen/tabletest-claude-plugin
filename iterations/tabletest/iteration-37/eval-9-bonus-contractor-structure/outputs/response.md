The table compiles and runs, expanding the value-set row into 6 total test cases as expected (all currently failing due to the unimplemented stub, which is correct per the task).

The TableTest is in place at `src/test/java/com/example/BonusCalculatorTest.java`:

- One `@TableTest` table with 5 rows covering all four SENIOR/JUNIOR × SALES/ENGINEERING combinations plus a CONTRACTOR row using a `{SALES, ENGINEERING}` value set to express "department doesn't matter" — this expands to 6 actual test cases.
- Added `org.tabletest:tabletest-junit:1.2.1` as a `testImplementation` dependency in `build.gradle` (JUnit Jupiter was already at 6.0.3, well above the 5.11 requirement).
- Left `BonusCalculator` untouched — all 6 cases currently fail with `UnsupportedOperationException`, confirming the tests compile and run against the stub without the logic being implemented.