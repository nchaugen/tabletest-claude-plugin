A Gradle Java project has been created for this feature.

Under `src/main/java/com/example`, the project already contains a stub `BonusCalculator`, plus `Employee`, `Level`, and `Department`. The bonus logic is not implemented yet.

We want to write tests before implementing the bonus calculation.

Write TableTests for `BonusCalculator.calculateBonusPercentage(...)`.

The bonus rules are:
- SENIOR + SALES department = 15%
- SENIOR + ENGINEERING department = 12%
- JUNIOR + SALES department = 8%
- JUNIOR + ENGINEERING department = 5%
- CONTRACTOR level always gets 0%, regardless of department

It is OK to keep a stub implementation for the calculator so the tests compile, but do not implement the bonus logic yet.
