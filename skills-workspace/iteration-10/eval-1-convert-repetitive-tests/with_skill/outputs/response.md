Here's the TableTest conversion:

```java
@Description("Order value is 100 in all scenarios.")
@TableTest("""
    Scenario        | Customer tier | Discount?
    Gold customer   | GOLD          | 20
    Silver customer | SILVER        | 10
    Bronze customer | BRONZE        | 0
    """)
void returnsDiscountForCustomerTier(String customerTier, int discount) {
    assertEquals(discount, discountService.calculate(customerTier, 100));
}
```

The order value `100` is fixed across all three original tests, so it's hardcoded in the method body and noted in `@Description` rather than repeated as a column. The `Customer tier` column captures the only varying input, and `Discount?` captures the expected output.