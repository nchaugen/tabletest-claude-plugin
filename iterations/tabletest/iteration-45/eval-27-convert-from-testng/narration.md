# Narration — 27

The agent's visible narration and file writes, in order, distilled from
`conversation.jsonl`. Thinking text is absent unless the model returns it in the
clear (Claude 5-family models encrypt it).

This is a Java test conversion to TableTest, so I'll use the tabletest skill for this.

**Edit /private/var/folders/fz/zxx_qbvj49gbql83cc5l47pr0000gn/T/eval-skill-1785164919741/eval-27-work/build.gradle (edit)**

Now I have all the calculations verified against the implementation. Let me write the converted test file.

**Write /private/var/folders/fz/zxx_qbvj49gbql83cc5l47pr0000gn/T/eval-skill-1785164919741/eval-27-work/src/test/java/com/example/ShippingCostCalculatorTest.java (182 lines)**

Now let's run the tests to confirm everything compiles and passes.

Need to add the JUnit Platform launcher dependency.

**Edit /private/var/folders/fz/zxx_qbvj49gbql83cc5l47pr0000gn/T/eval-skill-1785164919741/eval-27-work/build.gradle (edit)**

All 49 generated test cases pass (matches my count: 32 + 2 + 2 + 2 + 3 + 3 + 2 + 3). Let's also confirm the old TestNG dependency and references are fully gone.

No leftover TestNG references, and grep exited non-zero (nothing found), confirming a clean migration.

## Summary

Converted `ShippingCostCalculatorTest.java` from TestNG's single `@DataProvider` (17 hand-picked cases) into **8 focused `@TableTest` methods**, one per rule the calculator implements:

1. **Base rate by zone and weight tier** — full 4-zone × 4-tier rate matrix with boundary value sets (32 cases)
2. **Dimensional weight override** — actual vs. volumetric weight, whichever is larger
3. **Oversize fee** — boundary pair at the 100cm threshold
4. **Fragile multiplier** (15%)
5. **Insurance surcharge** — premium vs. $3.00 minimum, at and above the boundary
6. **Hazmat handling fee** — including that other handling strings are ignored
7. **Surcharge ordering** — a combining table proving hazmat is added *before* the fragile multiplier while insurance is added *after* (an interaction no single-rule table shows)
8. **Carrier independence** — one row with a `{DHL, UPS, FEDEX}` value set

Along the way:
- Added `@TypeConverter`s for `ShippingZone` (`"EU/standard"`) and `PackageOptions` (a map column like `[fragile: true, insuredValue: 200]`), keeping method bodies to arrange/act/assert only.
- Migrated `build.gradle` from TestNG to JUnit Jupiter 5.11 + `tabletest-junit`, and swapped `useTestNG()` for `useJUnitPlatform()`.
- Verified every expected value by hand against `ShippingCostCalculator`'s source.
- Ran `gradle test`: all 49 generated cases pass, no TestNG references remain anywhere in the repo.

One assumption worth flagging: I added several new boundary rows (e.g. tier boundaries, insurance-at-vs-above-minimum) that weren't in the original 17 cases, since the original data never tested tier/threshold boundaries directly — these are cheap now that the rules are decomposed.

