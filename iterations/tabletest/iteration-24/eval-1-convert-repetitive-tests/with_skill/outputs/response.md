No project files found — the user provided standalone code. The test shape is a clear conversion candidate: three methods with identical structure, varying only by `customerTier` and expected discount.

The order value (`100`) is fixed across all rows — it should be hardcoded in the method and noted in `@Description` rather than made a column.

Here's the converted TableTest:

```java
@Description("Order value is 100 in all scenarios.")
@TableTest("""
    Scenario        | Customer tier | Discount?
    Gold customer   | GOLD          | 20
    Silver customer | SILVER        | 10
    Bronze customer | BRONZE        | 0
    """)
void returnsDiscountByTier(String customerTier, int discount) {
    assertEquals(discount, discountService.calculate(customerTier, 100));
}
```

Key decisions:
- **No `orderValue` column** — it's always `100`, so it stays in the method body; `@Description` makes that visible without cluttering the table.
- **`Customer tier`** uses domain language rather than a parameter name like `tier` or `type`.
- **`Discount?`** suffix signals it's the expectation column.
- Scenario names describe the condition ("Gold customer"), not the outcome ("Returns 20").