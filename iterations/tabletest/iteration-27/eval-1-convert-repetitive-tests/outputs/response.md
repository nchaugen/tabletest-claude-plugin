These three tests have identical structure — same method call, same fixed order value, only the tier and expected discount vary. Perfect fit for a TableTest.

The order value `100` is fixed across all rows and won't appear as a column, so I'll note it in `@Description`.

```java
@Description("Order value is 100 for all scenarios.")
@TableTest("""
    Scenario        | Customer tier | Discount?
    Gold customer   | GOLD          | 20
    Silver customer | SILVER        | 10
    Bronze customer | BRONZE        | 0
    """)
void calculatesDiscount(String customerTier, int discount) {
    assertEquals(discount, discountService.calculate(customerTier, 100));
}
```

Key decisions:
- **`Customer tier`** and **`Discount?`** are the only columns — the fixed `100` order value stays in the method body, called out by `@Description` so it's not invisible.
- Scenario names describe the input condition ("Gold customer"), not the outcome ("Returns 20").
- `Discount?` suffix signals it's the expectation column.
- Parameters align left-to-right with data columns (scenario column excluded).