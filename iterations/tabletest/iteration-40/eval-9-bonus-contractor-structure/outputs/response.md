All 6 generated test cases (4 rules + 2 from the contractor value-set) compile and run correctly, failing only because the stub throws `UnsupportedOperationException` as intended — bonus logic is untouched.

Summary of changes:
- `build.gradle`: added `testImplementation 'org.tabletest:tabletest-junit:1.2.1'` (JUnit Jupiter was already 6.0.3, well above the 5.11 minimum).
- `src/test/java/com/example/BonusCalculatorTest.java` (new): one `@TableTest` covering all four level/department rules plus the contractor override, using a `{SALES, ENGINEERING}` value set to express "department doesn't matter for contractors."
- `BonusCalculator` left as the stub throwing `UnsupportedOperationException` — no bonus logic implemented, per instructions.

Assumption (documented in the test's `@Description`): `calculateBonusPercentage` returns the percentage as a plain number (e.g. `15.0` for 15%), not a fraction (`0.15`) — the spec didn't state which representation to use.