Tests pass with no failures. The conversion is complete.

**Summary of changes:**
- `pom.xml`: added the `tabletest-junit` dependency (JUnit Jupiter is already at 6.0.3, well above the 5.11 minimum).
- `DiscountServiceTest.java`: replaced the three `@Test` methods with one `@TableTest`, with columns `Tier`, `Amount`, `Discount?` (amount kept visible since the discount is a percentage of it, even though it's constant across the original rows), and one row per original scenario.

Ran `mvn test` — all cases pass.