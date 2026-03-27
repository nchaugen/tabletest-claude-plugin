```java
@TableTest("""
    Scenario         | Tier   | Discount?
    Gold customer    | GOLD   | 20
    Silver customer  | SILVER | 10
    Bronze customer  | BRONZE | 0
    """)
void calculateDiscount(String tier, int expectedDiscount) {
    assertEquals(expectedDiscount, discountService.calculate(tier, 100));
}
```

The order value `100` is constant across all three original tests, so it's inlined in the method body rather than added as a column.