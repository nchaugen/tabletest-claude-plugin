```java
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

The order value (`100`) is constant across all three original tests, so it stays as a hardcoded argument rather than a column — columns should only represent what varies. The `Customer tier` column captures the differing input, and `Discount?` captures the differing expected output.