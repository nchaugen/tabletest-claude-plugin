# Conversion Explanation

## What Changed

The three `@Test` methods were replaced with a single `@TableTest` method. Each original test became one data row in the table.

## Why TableTest Fits Here

All three tests share identical structure:
- Same method under test: `discountService.calculate(tier, amount)`
- Same assertion style: `assertEquals(expected, actual)`
- Only the inputs (`customerTier`) and expected output (`discount`) vary across rows

This is the core case TableTest is designed for: multiple scenarios with the same assertion logic, differentiated only by data.

## Column Design Decisions

- **Scenario column** (`Scenario`): Describes the condition being tested — the customer tier type. Scenario names describe *what kind of customer* rather than *what the outcome is* (e.g., "Gold customer" not "Returns 20% discount").
- **Customer Tier** (`String`): The first input to `calculate()`. Uses the concrete domain value (tier string) directly.
- **Amount** (`int`): The second input to `calculate()`. Even though it is the same value (100) across all rows, it is included as a column because it is a real input to the method. Including it keeps the table self-contained and makes the inputs explicit — a reader does not need to look inside the method body to understand what is being passed.
- **Discount?** (`int`): The expected return value. Suffixed with `?` to signal it is an expectation/output column.

## Quality Checks Passed

- Multiple rows (3 rows — satisfies 2+ requirement)
- Black-box design: columns are observable inputs and the return value
- Scenario names describe conditions (tier type), not outcomes
- Uniform assertion: all rows use the same `assertEquals` call
- No `if`/`switch` in the test method
- Parameter order matches column order (left-to-right, scenario column excluded)
- Expectation column present with `?` suffix
- Concrete values: discount values (20, 10, 0) are directly traceable to the tier input
