All 3 scenarios pass. I converted the three `@Test` methods into a single `@TableTest`:

```java
@TableTest("""
    Scenario           | Tier   | Amount | Discount?
    Gold customer      | GOLD   | 100    | 20
    Silver customer    | SILVER | 100    | 10
    Bronze customer    | BRONZE | 100    | 0
    """)
void calculatesDiscountByTier(String tier, int amount, int discount) {
    assertEquals(discount, discountService.calculate(tier, amount));
}
```

Also added the `tabletest-junit:1.2.1` dependency to `pom.xml` (JUnit 6.0.3 already satisfies the ≥5.11 requirement). Ran `mvn test` — all 3 cases pass, confirming coverage is preserved.