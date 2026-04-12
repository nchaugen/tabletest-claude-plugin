No build files found in this directory, so I'll proceed without a dependency check. The test shape is a clear fit for TableTest: three methods with identical structure (`assertEquals(discount, discountService.calculate(tier, 100))`), varying only by customer tier and expected discount.

The order value `100` is fixed across all rows — worth noting in a `@Description`.

Here's the converted TableTest:

```java
@Description("Order value is 100 for all scenarios.")
@TableTest("""
    Scenario       | Customer tier | Discount?
    Gold customer  | GOLD          | 20
    Silver customer| SILVER        | 10
    Bronze customer| BRONZE        | 0
    """)
void calculatesDiscount(String customerTier, int discount) {
    assertEquals(discount, discountService.calculate(customerTier, 100));
}
```

Key design decisions:
- `Customer tier` as a column (the only varying input — the `100` order value is fixed, documented in `@Description`)
- `Discount?` suffixed with `?` to mark it as the expectation column
- Scenario names describe the condition ("Gold customer"), not the outcome ("Returns 20")
- Parameter order matches column order left-to-right, skipping the scenario column