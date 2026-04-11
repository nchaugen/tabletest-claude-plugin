```java
@TableTest("""
    Scenario              | tier   | amount | discount?
    gold customer         | GOLD   | 100    | 20
    silver customer       | SILVER | 100    | 10
    bronze customer       | BRONZE | 100    | 0
    """)
void calculatesDiscount(String tier, int amount, int discount) {
    assertEquals(discount, discountService.calculate(tier, amount));
}
```

The `?` suffix on `discount` marks it as the expected output column. Each row becomes an independent test case named after the scenario.