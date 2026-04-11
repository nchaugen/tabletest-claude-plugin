# Design Decisions

## Why TableTest is appropriate here

The three original tests share identical structure: same method under test (`discountService.calculate`), same assertion logic (`assertEquals`), and the only differences are the customer tier and expected discount. This is exactly the scenario TableTest is designed for.

## Column choices

- **Scenario** (leftmost, not bound to a parameter): Describes the condition being tested — "Gold customer", "Silver customer", "Bronze customer" — following the guidance to describe the circumstance rather than the outcome.
- **Customer Tier**: The first varying input to `calculate()`. Named using domain language ("Customer Tier") rather than the parameter name ("tier" or "customerType").
- **Order Amount**: The second input. Kept as a column even though it is always 100 across all rows, because it is part of the method signature and making it explicit makes the table self-contained and traceable. If the value never varies it could be inlined into the test body, but having it visible keeps the table honest about what is being passed.
- **Discount?**: The expected return value, suffixed with `?` to mark it as an expectation column, per the skill's naming convention.

## Parameter alignment

Parameters are ordered left-to-right matching the data columns (excluding the scenario column): `customerTier`, `orderAmount`, `discount`. This matches the method signature order and requires no reordering logic.

## Method naming

The method is named `calculatesDiscountByCustomerTier` to describe the behavior under test. It is non-private, non-static, and returns void, satisfying TableTest's method requirements.

## What was removed

The three `@Test` methods are replaced entirely. No `if`/`switch` logic was introduced in the test body — the assertion remains a single `assertEquals` call identical in structure to the originals.
