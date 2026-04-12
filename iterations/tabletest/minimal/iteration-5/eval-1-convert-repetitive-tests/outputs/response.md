Here's the converted TableTest. The amount (`100`) is fixed across all rows, so I'll note it in `@Description` rather than add a redundant column:

```java
@Description("Order amount is 100 for all scenarios.")
@TableTest("""
    Scenario        | tier   | Discount?
    Gold customer   | GOLD   | 20
    Silver customer | SILVER | 10
    Bronze customer | BRONZE | 0
    """)
void calculatesDiscountByTier(String tier, int discount) {
    assertEquals(discount, discountService.calculate(tier, 100));
}
```

Key points:
- `tier` and `discount` parameters map left-to-right to the data columns (scenario column excluded)
- `Discount?` suffix signals it's the expected output
- `String` and `int` are handled by JUnit's built-in converters — no `@TypeConverter` needed