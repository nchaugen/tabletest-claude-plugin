It compiles correctly and expands to 6 test cases as designed — the value set correctly produced two rows for CONTRACTOR (SALES and ENGINEERING). All 6 fail with `UnsupportedOperationException`, exactly as expected since the stub isn't implemented yet.

Summary:
- `src/test/java/com/example/BonusCalculatorTest.java` — one `@TableTest` covering all 5 rules (4 level×department combos + CONTRACTOR override), using a `{SALES, ENGINEERING}` value set to state that department is ignored for CONTRACTOR (expands to 6 cases total).
- `build.gradle` — added `tabletest-junit:1.2.2` test dependency.
- `BonusCalculator` left untouched (still throws `UnsupportedOperationException`) per your instruction not to implement the logic yet.

Tests compile and run, failing only due to the unimplemented stub — ready for TDD implementation next.