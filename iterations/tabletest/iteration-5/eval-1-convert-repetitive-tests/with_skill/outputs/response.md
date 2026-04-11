```java
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

The order amount `100` is constant across all three tests, so it stays hardcoded in the method body. The only varying inputs are `tier` and the expected `discount`.