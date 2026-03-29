# Eval Review — Iteration 10

**Model:** sonnet · **Date:** 2026-03-29 · **Evals:** 16

## Summary

**with_skill:** 64/78 (82.1%) · 881593 tokens · 1286.8s · $1.2102

## Delta vs Iteration 9

**Regressions (1):**
- ❌ eval-13-shipping-partial-applicability: `overnight-grouped`

**Improvements (1):**
- ✅ eval-4-loan-approval: `scenario-names-describe-conditions`

## Per-Eval Results

### ✅ Eval eval-1-convert-repetitive-tests [with_skill]

**10/10** · 66303 tokens · 17147ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ✅ **has-question-mark-column**: Table has at least one column name ending with '?' (e.g. 'Discount?' or 'Expected?')
- ✅ **has-three-data-rows**: Table has exactly 3 data rows (GOLD, SILVER, BRONZE)
- ✅ **single-assertion-in-method**: Test method body contains only one assertEquals (or equivalent assertion) — not three
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements
- ✅ **scenario-column-present**: Table has a scenario/description column as the leftmost column
- ✅ **has-descriptive-title**: Test method has either a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case
- ✅ **description-if-present-adds-information**: If @Description is present, it provides context beyond what the table rows already express
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest
- ✅ **description-uses-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +

### ✅ Eval eval-3-dependency-setup [with_skill]

**4/4** · 102956 tokens · 12905ms

- ✅ **flags-junit-version**: Response explicitly flags that JUnit 5.10.0 is below the required minimum (5.11) and recommends upgrading
- ✅ **provides-correct-groupid**: Response provides the tabletest dependency with groupId 'org.tabletest' (not guessed)
- ✅ **provides-artifactid**: Response includes 'tabletest-junit' as the artifactId
- ✅ **includes-test-scope**: The suggested dependency includes <scope>test</scope>

### ✅ Eval eval-4-loan-approval [with_skill]

**7/7** · 66154 tokens · 35535ms

- ✅ **produces-markdown-table**: Output contains a markdown table (using | column | syntax)
- ✅ **output-column-has-question-mark**: At least one output column name ends with '?'
- ✅ **business-language-columns**: Column names use domain/business language — no implementation terms
- ✅ **senior-threshold-row**: Table includes at least one row specifically for the senior applicant (65+) threshold scenario
- ✅ **missing-income-marked-open**: The missing income scenario is represented with open/uncertain expected value and/or explicit open question note
- ✅ **scenario-names-describe-conditions**: Scenario names describe conditions not outcomes
- ✅ **threshold-values-visible**: Policy thresholds (650 for standard, 600 for senior) appear as concrete values in Credit Score column

### ✅ Eval eval-6-discount-interaction [with_skill]

**6/6** · 66623 tokens · 44433ms

- ✅ **produces-markdown-table**: Output contains a markdown table
- ✅ **does-not-invent-resolution**: The output does NOT silently resolve the stacking/cap ambiguity — it leaves the conflicting cases as open questions, blank cells, or explicitly marks them as unresolved
- ✅ **covers-both-discounts-applying**: Table includes at least one row where both bulk and loyalty discounts apply simultaneously (exposing the interaction)
- ✅ **open-question-surfaced**: The unresolved decision (stacking vs. higher-only vs. cap) is explicitly surfaced — via an Open Questions column, a '?' cell, or a note
- ✅ **output-column-has-question-mark**: At least one output column ends with '?'
- ✅ **extreme-discount-row**: Table includes at least one row with a high combined discount (e.g. large order + top loyalty tier) that would force a decision about whether a cap applies — making the cap question concrete, not theoretical.

### ✅ Eval eval-8-money-parse [with_skill]

**10/10** · 189794 tokens · 277563ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ✅ **null-as-blank-cell**: Null input is represented as a blank cell in the valid inputs @TableTest table (not the string 'null', and not as a separate @Test method). The blank cell maps to a null parameter.
- ✅ **exception-has-expected-column**: The exception/invalid-input table has an expected column (e.g. 'Throws?' or 'Exception?') specifying the exception type per row — rather than hardcoding the exception type in @Description or the method name.
- ✅ **exception-cases-handled**: The exception cases (empty string, letters-only, negative) are handled — via a Throws? column, assertThrows in the method body, or a separate @TableTest — not silently omitted
- ✅ **result-column-with-question-mark**: Valid inputs have a result column name ending with '?' (e.g. 'Money?' or 'Result?')
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements
- ✅ **has-descriptive-title**: Test method has either a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case (e.g. 'moneyParsing' → 'Money Parsing'). Not a generic name like 'test1' or 'testMethod'.
- ✅ **description-if-present-adds-information**: If @Description is present, it provides context beyond what the table rows already express — such as the expected currency format, or open questions. Does NOT merely restate the column names or summarise what the rows show. It is acceptable to omit @Description if the table already conveys all relevant context.
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **description-uses-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +. Passes if @Description is absent.

### ✅ Eval eval-9-bonus-contractor-structure [with_skill]

**9/9** · 179048 tokens · 291716ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ✅ **contractor-uses-value-set**: CONTRACTOR row uses a value set for Department (e.g. {SALES, ENGINEERING}) to express 'regardless of department' — not a dummy placeholder like 'ANY' or 'N/A', and not enumerated as separate rows.
- ✅ **four-core-rules-covered**: The four level×department combinations (SENIOR+SALES, SENIOR+ENGINEERING, JUNIOR+SALES, JUNIOR+ENGINEERING) are all present in the table(s)
- ✅ **expects-bonus-percentage**: The expected output column contains bonus percentages (e.g. 15%, 12%, 0%) — not bonus amounts. The method returns a percentage, and the table should reflect that.
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements
- ✅ **has-descriptive-title**: Test method has either a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case. Not a generic name like 'test1' or 'testMethod'.
- ✅ **description-if-present-adds-information**: If @Description is present, it provides context beyond what the table rows already express. It is acceptable to omit @Description if the table already conveys all relevant context.
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **description-uses-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +. Passes if @Description is absent.

### ⚠️ Eval eval-13-shipping-partial-applicability [with_skill]

**4/6** · 70139 tokens · 279424ms

- ✅ **produces-markdown-table**: Output contains a markdown table
- ✅ **express-split-by-uk-free-tier**: Express shipping is split into at least two cases distinguishing the UK free tier (UK, order £50+, free) from the paid tier
- ❌ **express-uses-value-sets**: Express rows use value sets to consolidate cases with the same outcome — not enumerated as separate rows per order value when the result is identical
  > Row 4 'Express — at free threshold, UK | £50 | £0.00' and Row 5 'Express — above free threshold, UK | £75 | £0.00' are separate rows despite identical outcomes. Similarly, Row 6 'Express — at free threshold, outside UK | £50 | {Ireland, EU} | £9.99' and Row 7 'Express — above free threshold, outside UK | £75 | {Ireland, EU} | £9.99' are separate rows. These should be consolidated using value sets like {£50, £75} rather than enumerated separately.
- ❌ **overnight-grouped**: Overnight UK and Ireland are in one row using a value set ({UK, Ireland}) — not separate rows
  > Overnight Availability table shows two separate rows: 'Overnight — UK | UK | £14.99' and 'Overnight — Ireland | Ireland | £14.99'. These should be consolidated into a single row as 'Overnight | {UK, Ireland} | £14.99' since they have identical cost and availability.
- ✅ **standard-destination-value-set-or-blank**: Standard shipping destination uses a value set covering all options or is blank/absent
- ✅ **output-column-has-question-mark**: At least one output column ends with '?'

### ⚠️ Eval eval-15-reis-discount [with_skill]

**3/13** · 72103 tokens · 160540ms

- ❌ **2.1-decomposition-concern-separation**: Discount ladder, and traveller eligibility are in separate tables. Rolling window counting as a third table is desirable.
  > The response provides two tables, both mixing discount ladder data with passenger type information in the same columns. Expected structure: separate tables for (1) discount ladder, (2) traveller eligibility, (3) rolling window counting. Response has no rolling window table and does not separate the discount ladder concept from the traveller eligibility concept into distinct tables.
- ✅ **2.2-children-flat-discount**: Children's flat 20% discount is represented — either as a row in the eligibility table or a separate note. It does not follow the ladder.
- ✅ **2.3-depth-tier-boundaries**: The discount ladder has multiple tiers derived from the 'every fifth trip' rule, with boundary values or value sets showing which ticket counts produce which discount. The maximum 40% discount is represented.
- ❌ **2.4-depth-rolling-window-boundary**: The 30-day rolling window boundary is tested: a ticket at exactly 30 days is included, at 31 days is excluded.
  > The table column 'Prior single tickets (last 30 days)' mentions the 30-day window but does not include explicit test cases for the boundary. The open question 'Discount drops as old trips fall out of window' acknowledges the issue but does not present specific test scenarios (e.g., one row testing 30 days inclusion, another testing 31 days exclusion).
- ❌ **2.6-readability-human-readable-values**: Eligibility uses human-readable values (yes/no or equivalent) rather than raw true/false. TypeConverter or similar mechanism mentioned.
  > The response uses percentages (0%, 5%, 20%, 40%) which are human-readable, but the required second part—'TypeConverter or similar mechanism mentioned'—is absent. No TypeConverter, TypeConverterSources, or conversion mechanism is described in the response.
- ❌ **2.9-correctness-value-set-tier-semantics**: Value sets in the tier table contain exactly the values that produce the same discount. No cross-tier contamination.
  > The table enumerates individual rows per ticket count rather than consolidated value sets: 'At first discount threshold | 4 | 5%', 'Within first tier | 6 | 5%', 'Top of first tier | 8 | 5%'. No value set notation (e.g., {4,6,8} → 5%) is used to group counts that produce the same discount.
- ❌ **2.15-ticket-count-uses-value-sets**: The discount ladder table uses value sets for the ticket count column to group counts within the same tier (e.g. {5, 6, 7, 8, 9} or {5, 9} for min/max → 5%) — not one row per boundary value. The 'every fifth trip' rule creates tiers; the table should express tiers, not enumerate boundaries.
  > The response explicitly enumerates one row per scenario (e.g., 'At first discount threshold | 4 | 5%', 'Within first tier | 6 | 5%', 'Top of first tier | 8 | 5%'), not consolidated value sets. The assertion states 'not one row per boundary value'—which is exactly what the response does.
- ❌ **2.16-no-duplicate-tier-mapping**: The tier-to-discount mapping is expressed once — not repeated across multiple tables. A price application table (if present) should test the arithmetic (base price × discount → final price) without re-enumerating which ticket counts map to which tier.
  > The tier-to-discount mapping is present in both the 'Reis Discount Tier — Adult and Senior' table and the 'Child Single Ticket Discount' table. Although the mappings differ by passenger type, the assertion specifies the mapping should be 'expressed once' without duplication across tables.
- ❌ **2.10-format-displayname**: @DisplayName is present on test methods, OR method names read as clear descriptive titles when converted from camelCase/snake_case.
  > The response provides table structures but no actual test code, method definitions, or @DisplayName annotations. No test method names are shown.
- ❌ **2.11-format-description**: @Description is present on at least the class or the tier method and provides context beyond what the table rows already express — such as the discount ladder structure, eligibility rules, or application context. Does NOT merely restate the column names or summarise what the rows show.
  > No @Description annotations are shown in the response. While open questions are provided at the end, they do not constitute class-level @Description annotations in test code format.
- ❌ **2.12-format-typeconverter**: TypeConverterSources is used for at least one human-readable conversion (yes/no, or rolling window notation).
  > The response does not mention or show TypeConverterSources or any TypeConverter implementation. No conversion mechanism for human-readable values is described.
- ❌ **2.13-format-annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
  > The response provides table definitions but no test code with annotations. No @DisplayName, @Description, or @TableTest annotations are shown to verify order.
- ✅ **2.14-format-description-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +. Passes if @Description is absent.

### ⚠️ Eval eval-17-shopping-cart [with_skill]

**11/13** · 68473 tokens · 167544ms

- ✅ **4.1-decomposition-items-separate-from-coupons**: Item operations (add/remove) are in a separate table from coupon validation. Not interleaved in one table.
- ✅ **4.3-rows-independently-executable**: Each table row is independently executable — no row depends on a prior row's result. The table expresses rules, not a sequential test script.
- ❌ **4.4-depth-item-operations**: Item operations covered: add to empty cart, add different product, add more of same, remove product, remove unknown product, remove last item.
  > Table 1 covers: 'Add item to empty cart', 'Add a second product', 'Add more of existing product'. Table 2 covers: 'Remove some of an item', 'Remove all of an item', 'Remove item not in cart', 'Remove more than in cart'. The operation 'remove unknown product' (removing a product not in the catalogue) is not explicitly tested. Table 1 tests 'Product not in catalogue' for add operations, but there is no symmetric test for removing a product not in the catalogue.
- ✅ **4.5-depth-coupon-variations**: Coupon variations covered: valid percentage, valid fixed amount, product-specific, expired, unknown code, replace existing coupon.
- ✅ **4.6-depth-coupon-exceeds-total**: A scenario where the coupon discount exceeds the cart total — total floors at zero.
- ✅ **4.7-depth-checkout-edge-cases**: Checkout covers: sufficient stock, insufficient stock, and empty cart.
- ✅ **4.8-readability-business-language**: Column names use business/user-perspective language (e.g. 'Message?' not 'Result?', 'Coupon code' not 'input'). The table reads as a specification a product person could review.
- ✅ **4.9-readability-test-data-visible**: Test data that affects the outcome — product prices, coupon rules (type, amount, expiry) — is visible in the table via columns, notes, or explicit setup, not silently hardcoded. The reader can trace how inputs lead to outputs.
- ✅ **4.10-depth-open-questions-surfaced**: At least one genuinely underspecified interaction is surfaced as an open question — such as: what happens to a product-specific coupon when that product is removed from the cart, or how does a percentage coupon interact with a product-specific discount. These are NOT stated in the requirements.
- ✅ **4.11-correctness-cart-totals**: Cart totals are arithmetically correct: sum(quantity × price) − coupon = total, for every row in the total calculation table.
- ✅ **4.12-coupon-expiry-column**: Coupon table uses a column to signal expiry status — either an 'Expired?' yes/no column, an expiry date column, or equivalent. Expiry is visible per row, not just implied by the scenario name.
- ❌ **4.13-coupon-cart-contents-column**: Coupon table includes a cart contents column (or 'Applies to' / 'Product in cart?' column) so the reader can see whether a product-specific coupon's target is actually in the cart. The product discount applied/not-applied distinction is driven by visible data, not just the scenario name.
  > Table 3 (Applying Coupons) does not include a cart contents column or 'Product in cart?' column. Rows like 'Apply product-specific coupon' do not show what items are in the cart at the time of coupon application. While Table 4 does show this for total calculation scenarios, the coupon validation table itself (Table 3) lacks visibility into cart contents.
- ✅ **4.14-coupon-before-after-columns**: Coupon entry scenarios use before/after columns (e.g. 'Coupon before' / 'Coupon after?' or 'Active coupon' / 'Active coupon after?') to specify the state change when entering, replacing, or entering invalid coupon codes.

