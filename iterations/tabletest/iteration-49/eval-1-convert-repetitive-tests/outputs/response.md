Converted successfully — 3 tests run, all pass.

**Summary of changes:**
- `pom.xml`: added the `org.tabletest:tabletest-junit:1.2.2` test dependency (was missing).
- `DiscountServiceTest.java`: replaced the three `@Test` methods with a single `@TableTest` (`calculatesDiscountFromCustomerTier`), covering GOLD/SILVER/BRONZE tiers.

**Assumption made:** the original tests all used `amount = 100`. Since `calculate` computes the discount from both `tier` and `amount`, I kept `Amount` as an explicit input column (rather than hardcoding it in the method body) so the expected `Discount?` values are traceable from the table — 20 = 100×20/100, etc. All rows use `Amount = 100` since that's the only value the original tests exercised; if you want boundary/rounding behavior on `amount` covered too, that'd need additional rows or its own table.