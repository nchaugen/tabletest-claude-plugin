All 3 scenarios pass as a single `@TableTest`.

**Changes made:**
- `pom.xml` — added the `tabletest-junit:1.2.2` test dependency (JUnit Jupiter is already 6.0.3, which satisfies the ≥5.11 requirement)
- `src/test/java/com/example/DiscountServiceTest.java` — replaced the three `@Test` methods with one `@TableTest`, varying `Tier` and keeping `Amount` as an explicit column (held constant at 100) since the discount is computed from both, and the design rule requires any input the outcome depends on to be declared rather than hidden in the method body.

Table:
```
Scenario         | Tier   | Amount | Discount?
Gold customer    | GOLD   | 100    | 20
Silver customer  | SILVER | 100    | 10
Bronze customer  | BRONZE | 100    | 0
```

Note: `BRONZE` exercises the `default` branch of `DiscountService.calculate`, so no coverage was lost in the conversion.