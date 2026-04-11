```java
@TableTest("""
    Scenario        | Tier   | Discount?
    Gold customer   | GOLD   | 20
    Silver customer | SILVER | 10
    Bronze customer | BRONZE | 0
    """)
void returnsDiscountForCustomerTier(String tier, int discount) {
    assertEquals(discount, discountService.calculate(tier, 100));
}
```

The order value (`100`) is fixed across all rows so it stays in the method body rather than becoming a column. The scenario column is dropped from the parameters since it's not bound to one.