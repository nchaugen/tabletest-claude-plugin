# Conversion Explanation

## What Changed

Three `@Test` methods were merged into a single `@TableTest`. Each original test differed only in the customer tier string and the expected discount value — making this a textbook TableTest conversion.

## Design Decisions

**Columns chosen**: The two varying parts become columns — `Customer Tier` (input) and `Discount?` (expectation). The `?` suffix on `Discount?` signals it is an output being verified.

**Amount omitted as a column**: The purchase amount `100` is the same in all three original tests and is not part of the behavioral variation being tested here. Keeping it as a fixed literal in the method body avoids adding a column that contributes no information.

**Scenario names**: Each name describes the condition ("Gold customer"), not the outcome ("Returns 20"). This matches the skill's guidance and produces clearer failure messages.

**Method name**: Renamed to `calculatesDiscountByCustomerTier` to describe the behavior under test rather than a single outcome.

## Quality Check

- Multiple rows (3): yes
- Black-box design: columns are observable inputs and outputs
- Scenario names describe conditions, not outcomes
- Uniform assertions: all rows use `assertEquals`
- No `if`/`switch` in the method body
- Parameter alignment: `customerTier` maps to `Customer Tier`, `discount` maps to `Discount?`
- Expectation column uses `?` suffix
- Concrete values: discount values (20, 10, 0) are traceable to the tier input
