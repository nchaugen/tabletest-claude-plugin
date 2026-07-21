Done. Here's what was delivered:

**`BonusCalculatorTest.java`** — a single `@TableTest` method covering all 6 rules:

| Level | Department | Expected Bonus? |
|-------|-----------|----------------|
| SENIOR | SALES | 15.0 |
| SENIOR | ENGINEERING | 12.0 |
| JUNIOR | SALES | 8.0 |
| JUNIOR | ENGINEERING | 5.0 |
| CONTRACTOR | SALES | 0.0 |
| CONTRACTOR | ENGINEERING | 0.0 |

**Key design decisions:**
- **Single table, no decomposition needed** — all rules apply to the same method with the same output type; splitting would just fragment one concern.
- **Value set pattern not used** — even though CONTRACTOR always returns 0% regardless of department, each combination is tested explicitly so the contract is visible in the table. (A value set like `{SALES, ENGINEERING}` could compress it later.)
- **`Bonus?` column** — expectation suffix per skill guidelines; values are concrete traceable numbers from the rules.
- **JUnit enum conversion** — no custom `@TypeConverter` needed; JUnit converts `"SENIOR"`, `"SALES"`, etc. to enum constants via built-in support.

The stub (`UnsupportedOperationException`) is preserved and all 6 tests fail as expected, ready for implementation.