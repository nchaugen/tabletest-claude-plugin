No build files found in the working directory. The skill requires verifying the `org.tabletest:tabletest-junit` dependency before writing code — without a `pom.xml` or `build.gradle`, I can't confirm it's set up.

That said, I can show you exactly what the converted `@TableTest` would look like so you can drop it in once the dependency is confirmed.

---

The three tests vary only in `customerTier` and the expected discount. The order value (100) is fixed across all rows, so I'll note it in `@Description` rather than repeat it as a column:

```java
@Description("Order value is always 100.")
@TableTest("""
    Scenario        | Customer tier | Discount?
    Gold customer   | GOLD          | 20
    Silver customer | SILVER        | 10
    Bronze customer | BRONZE        | 0
    """)
void calculatesDiscountByCustomerTier(String customerTier, int discount) {
    assertEquals(discount, discountService.calculate(customerTier, 100));
}
```

A few notes:
- The scenario column isn't bound to a parameter — the first parameter maps to `Customer tier`.
- JUnit converts `String` and `int` automatically, so no `@TypeConverter` is needed.
- If you later add cases where the order value differs, promote it to its own column and remove the `@Description`.

If you don't yet have the TableTest dependency, let me know and I can add it to your build file — just share the `pom.xml` or `build.gradle`.