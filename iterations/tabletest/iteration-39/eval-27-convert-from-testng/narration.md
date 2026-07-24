# Narration — eval-27-convert-from-testng

The agent's visible narration and file writes, in order, distilled from
`conversation.jsonl`. Thinking text is absent unless the model returns it in the
clear (Claude 5-family models encrypt it).

Now I'll check for other TestNG usages I might be missing, then convert the build file and write the TableTest class.

TestNG has no other usages in the repo, so I'll fully switch the build to JUnit Jupiter + TableTest.

**Edit ./build.gradle (edit)**

**Write ./src/test/java/com/example/ShippingCostCalculatorTest.java (175 lines)**

Now let's run the build to confirm everything compiles and passes.

Needs the JUnit Platform launcher on the test runtime classpath.

**Edit ./build.gradle (edit)**

All 25 generated test cases pass (8+2+2+2+4+2+2+3 across the 8 concern tables). Let me verify no TestNG remnants remain and check final diff.

No TestNG references remain. The conversion is complete and verified with `gradle test`.

**What changed:**

- `build.gradle`: replaced TestNG with JUnit Jupiter 5.11.4 + `tabletest-junit:1.2.1` (plus the JUnit Platform launcher needed at test runtime), switched `useTestNG()` → `useJUnitPlatform()`.
- `ShippingCostCalculatorTest.java`: rewritten from one 17-row `@DataProvider` into 8 focused `@TableTest` methods, one per concern (base rate by zone/weight, dimensional-weight override, oversize surcharge, fragile surcharge, insurance premium, hazmat fee, combined surcharge ordering, carrier neutrality — the last uses a `{DHL, UPS, FEDEX}` value set since carrier never affects price).

**Assumptions made** (documented in each table's `@Description`):
- Where an option is irrelevant to a concern (e.g. dimensions for the fragile table), it's fixed rather than threaded through as a column, per the skill's black-box design guidance.
- I added a few boundary rows the original suite didn't cover, since they were cheap and revealed real untested branches: an oversize boundary at exactly 100 vs. 101, and an insurance-premium boundary at exactly the $3 minimum (500) vs. above it (1000, newly added — the original never exercised the "premium exceeds minimum" branch).
- Kept the original `compareTo`-based equality check (as a small `assertCost` helper) since `BigDecimal.equals` is scale-sensitive and the calculator's `stripTrailingZeros()` output won't scale-match literals like `"5.00"`.

Verified via `gradle test`: 25 generated test cases, all passing.

