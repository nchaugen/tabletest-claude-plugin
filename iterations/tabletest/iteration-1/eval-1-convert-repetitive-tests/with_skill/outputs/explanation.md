# Design Decisions

## Why TableTest is appropriate here

All three original tests share identical structure: same method under test (`discountService.calculate`), same assertion type (`assertEquals`), and the only differences are data (customer tier and the expected discount). This is precisely the pattern TableTest is designed for.

## Column design

- **Scenario column**: Named to describe the condition ("Gold customer", "Silver customer", "Bronze customer") rather than the outcome, following the guideline that scenario names answer "under what circumstances?" not "what happens?".
- **Customer Tier**: Uses domain terminology rather than a parameter name like `arg0` or a generic label like `input`.
- **Order Total**: The order amount is kept as a column even though it is 100 in every row. This makes the input explicit and observable in the table, which is better black-box design. It also makes future rows with different totals easy to add without structural changes.
- **Discount?**: Suffixed with `?` to signal it is the expected output being verified, following the convention for expectation columns.

## Parameter alignment

Parameters are aligned left-to-right with the data columns (excluding the scenario column): `customerTier` → `Customer Tier`, `orderTotal` → `Order Total`, `discount` → `Discount?`.

## Method naming

The method name `calculatesDiscountByCustomerTier` describes what is being tested at the method level (the concern), while the scenario column describes the per-row condition. This is cleaner than the original test names like `returnsDiscountForGoldCustomer`, which describe outcomes rather than conditions.

## No custom type converters needed

All column types (`String`, `int`) are handled by JUnit's built-in conversion, so no `@TypeConverter` methods are required.
