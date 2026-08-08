4 rows ran (matching the original 3 tests plus one extra row), all green.

Summary of what I did:
- Added the `tabletest-junit` 1.2.2 dependency to `pom.xml` (JUnit 6.0.3 already satisfies the ≥5.11 requirement).
- Replaced the three `@Test` methods in `DiscountServiceTest.java` with one `@TableTest` (`calculatesDiscountForCustomerTier`), columns `Tier | Amount | Discount?`.
- Assumption (noted in `@Description`): all three original tests used `amount = 100`, which would silently make "100" part of the rule. I added one extra row (`GOLD`, amount 250 → 60→ actually 50) to show the discount scales as a percentage of amount rather than being a fixed value — this wasn't in the original tests but follows from the table-design principle that a constant held across every row gets misread as part of the rule.
- Verified the table parses (format-table.sh) and ran `mvn test`: 4/4 pass, build succeeds.