All 3 tests passed — the `@TableTest` replaced the three individual `@Test` methods cleanly. Here's a summary of what changed:

**`pom.xml`** — Added the `tabletest-junit` dependency (v1.2.1).

**`DiscountServiceTest.java`** — Collapsed three repetitive `@Test` methods into one `@TableTest`:

| Before | After |
|--------|-------|
| Three separate methods, each calling `discountService.calculate()` with hardcoded inputs and asserting a single expected value | One `@TableTest` method with a data table: scenario column + input columns (`tier`, `amount`) + expectation column (`discount?`) |
| ~20 lines of repetitive boilerplate | ~12 lines: the table is self-documenting, assertion logic appears once |

Key design choices following the skill:
- **Scenario names** describe conditions (`Gold customer`, `Silver customer`, `Bronze customer`), not outcomes.
- **Expectation column** suffixed with `?` (`Discount?`) to signal it's an output being verified.
- **Domain terminology** — column names use `Tier`/`Amount`/`Discount` rather than internal parameter names.