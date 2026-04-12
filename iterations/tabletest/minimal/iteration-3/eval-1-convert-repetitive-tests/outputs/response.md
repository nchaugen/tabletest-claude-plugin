```java
@Description("Purchase amount is 100 for all scenarios.")
@TableTest("""
    Scenario        | Tier   | Discount?
    Gold customer   | GOLD   | 20
    Silver customer | SILVER | 10
    Bronze customer | BRONZE | 0
    """)
void calculatesDiscountByTier(String tier, int discount) {
    assertEquals(discount, discountService.calculate(tier, 100));
}
```

The purchase amount `100` is fixed across all rows, so it stays as a hardcoded argument and is documented via `@Description`. The varying parts — tier and expected discount — become the table columns.