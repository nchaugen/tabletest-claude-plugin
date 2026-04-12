No build files found in the working directory, so I can't verify the dependency. Make sure `org.tabletest:tabletest-junit` is in your `pom.xml` or `build.gradle` before using this.

Here's the converted TableTest:

```java
@DisplayName("Discount by customer tier")
@Description("Order amount is fixed at 100 for all scenarios.")
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

The amount `100` is hardcoded in the method since it's constant across all rows — this is captured in the `@Description`. If you need to test different amounts too, add an `Amount` column and remove the `@Description`.