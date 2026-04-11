No existing Java files or build files in the project. The three tests are structurally identical — same method under test, same order value (100), only the tier and expected discount vary. This is a perfect fit for TableTest.

The order value `100` is fixed across all rows, so I'll note it in a `@Description` rather than making it a column.

Here's the converted TableTest:

```java
@Description("Order value is 100 for all scenarios.")
@TableTest("""
    Scenario        | Customer tier | Discount?
    Gold customer   | GOLD          | 20
    Silver customer | SILVER        | 10
    Bronze customer | BRONZE        | 0
    """)
void calculatesDiscountByTier(String customerTier, int discount) {
    assertEquals(discount, discountService.calculate(customerTier, 100));
}
```

Key decisions:
- `Customer tier` and `Discount?` are the only columns — the order value `100` is fixed and goes in `@Description` instead of adding a column that never varies.
- Scenario names describe the condition (`Gold customer`), not the outcome (`Returns 20`).
- `Discount?` uses the `?` suffix to mark it as the expectation column.
- Parameters align left-to-right with the data columns (scenario excluded).