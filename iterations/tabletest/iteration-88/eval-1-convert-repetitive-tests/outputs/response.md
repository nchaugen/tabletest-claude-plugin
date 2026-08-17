All 3 scenarios pass (same coverage as before, now as one `@TableTest`).

**What changed:**
- `src/test/java/com/example/DiscountServiceTest.java` — replaced the three `@Test` methods with one `@TableTest`, table columns `Tier | Amount | Discount?`.
- `pom.xml` — added the `tabletest-junit:1.2.2` test dependency (JUnit Jupiter was already ≥5.11, so no version bump needed).

**Assumption:** `Amount` is included as its own column (rather than hidden inside the method body) even though all three original tests fixed it at `100` — the discount is a function of both `tier` and `amount`, so per this project's table-design convention any value the outcome depends on should be visible as a column, not silently held constant. I didn't add new rows varying the amount, since that would go beyond what the original three tests exercised.