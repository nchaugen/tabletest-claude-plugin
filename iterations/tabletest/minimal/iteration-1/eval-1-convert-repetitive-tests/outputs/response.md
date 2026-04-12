Here's the converted TableTest. The order amount (`100`) is constant across all three tests, so it stays in the method body and gets noted in `@Description`.

```java
@DisplayName("Discount calculation by customer tier")
@Description("Order amount is fixed at 100 for all scenarios.")
@TableTest("""
    Scenario        | Tier   | Discount?
    Gold customer   | GOLD   | 20
    Silver customer | SILVER | 10
    Bronze customer | BRONZE | 0
    """)
void returnsDiscountByCustomerTier(String tier, int discount) {
    assertEquals(discount, discountService.calculate(tier, 100));
}
```

`String` and `int` are handled by JUnit's built-in converters, so no `@TypeConverter` is needed. The three original `@Test` methods can be deleted.