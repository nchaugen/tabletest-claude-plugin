All three original scenarios pass under the new table.

**Changes:**
- `pom.xml` — added `tabletest-junit:1.2.2` test dependency (was missing).
- `DiscountServiceTest.java` — replaced the three `@Test` methods with a single `@TableTest` covering GOLD/SILVER/BRONZE tiers, one row each, discount traceable from tier + amount.

**Assumption documented in `@Description`:** `Amount` is held at 100 across all rows since the original tests never varied it — the table's rule is the discount *rate* by tier, not the multiplication. If you want boundary coverage on `Amount` itself (e.g. 0, negative), that'd be a separate concern/table.