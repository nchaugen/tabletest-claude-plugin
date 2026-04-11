# Eval Review — Iteration 27

**Model:** sonnet · **Date:** 2026-04-05 · **Evals:** 27

## Summary

**with_skill:** 298/356 (83.7%) · 3329783 tokens · 3381.1s · $5.6241

## Delta vs Iteration 26

**Regressions (18):**
- ❌ eval-6-discount-interaction: `extreme-discount-row`
- ❌ eval-10-subscription-billing: `tables-have-distinct-concerns`
- ❌ eval-10-subscription-billing: `output-columns-have-question-marks`
- ❌ eval-10-subscription-billing: `24h-boundary-near-boundary`
- ❌ eval-10-subscription-billing: `eligibility-separate-from-amount`
- ❌ eval-10-subscription-billing: `question-mark-only-on-outputs`
- ❌ eval-10-subscription-billing: `rules-separate-from-arithmetic`
- ❌ eval-10-subscription-billing: `concerns-decomposed`
- ❌ eval-12-subscription-loyalty-trial: `open-question-surfaced`
- ❌ eval-13-shipping-partial-applicability: `concerns-decomposed`
- ❌ eval-13-shipping-partial-applicability: `separates-availability-and-cost`
- ❌ eval-15-reis-discount: `2.1-decomposition-concern-separation`
- ❌ eval-15-reis-discount: `2.6-readability-human-readable-values`
- ❌ eval-21-event-registration-sbe: `blank-for-absent-optional`
- ❌ eval-23-loan-approval-tt: `scenario-names-describe-conditions`
- ❌ eval-25-convert-from-spock: `business-language-columns`
- ❌ eval-28-convert-from-methodsource: `options-as-map`
- ❌ eval-28-convert-from-methodsource: `concerns-decomposed`

**Improvements (25):**
- ✅ eval-4-loan-approval: `scenario-names-describe-conditions`
- ✅ eval-10-subscription-billing: `prorated-refund-formula`
- ✅ eval-10-subscription-billing: `output-values-traceable`
- ✅ eval-10-subscription-billing: `refund-table-includes-price-paid`
- ✅ eval-15-reis-discount: `2.15-ticket-count-uses-value-sets`
- ✅ eval-15-reis-discount: `2.20-readability-one-row-per-tier`
- ✅ eval-15-reis-discount: `minimal-rows-per-concern`
- ✅ eval-16-order-splitting: `3.1a-concern-fulfillment-method`
- ✅ eval-16-order-splitting: `3.1b-concern-delivery-address`
- ✅ eval-16-order-splitting: `3.1c-concern-availability`
- ✅ eval-16-order-splitting: `concerns-decomposed`
- ✅ eval-16-order-splitting: `minimal-rows-per-concern`
- ✅ eval-17-shopping-cart: `4.5-depth-coupon-application`
- ✅ eval-17-shopping-cart: `4.6-depth-cart-total-scenarios`
- ✅ eval-17-shopping-cart: `minimal-rows-per-concern`
- ✅ eval-18-convert-from-code: `scenario-names-describe-conditions`
- ✅ eval-18-convert-from-code: `minimal-rows-per-concern`
- ✅ eval-22-event-registration-tt: `blank-for-absent-optional`
- ✅ eval-22-event-registration-tt: `validation-includes-optional-fields`
- ✅ eval-25-convert-from-spock: `no-if-switch-in-method`
- ✅ eval-26-convert-from-kotest: `concerns-decomposed`
- ✅ eval-26-convert-from-kotest: `business-language-columns`
- ✅ eval-26-convert-from-kotest: `no-if-switch-in-method`
- ✅ eval-27-convert-from-testng: `options-as-map`
- ✅ eval-27-convert-from-testng: `options-type-converter`

## Resource Comparison vs Iteration 26

| Eval | Pass Rate | Prev | Tokens | Prev | Time(s) | Prev |
|------|-----------|------|--------|------|---------|------|
| eval-1-convert-repetitive-tests | 10/10 | 10/10 | 67108 | 104523 | 21.6 | 23.9 |
| eval-2-parse-dates | 13/13 | 13/13 | 112865 | 106297 | 64.5 | 51.9 |
| eval-3-dependency-setup | 4/4 | 4/4 | 103483 | 103945 | 11.6 | 12.4 |
| eval-4-loan-approval | 9/13 | 8/13 | 66673 | 66529 | 32.3 | 35.6 |
| eval-5-order-transitions | 10/10 | 10/10 | 67381 | 67835 | 43.8 | 51.5 |
| eval-6-discount-interaction | 6/7 | 7/7 | 65454 | 65334 | 19.8 | 17.0 |
| eval-7-permission-check | 11/11 | 11/11 | 108161 | 150923 | 36.3 | 54.6 |
| eval-8-money-parse | 13/13 | 13/13 | 118583 | 209823 | 320.5 | 126.2 |
| eval-9-bonus-contractor-structure | 11/11 | 11/11 | 109012 | 108357 | 51.3 | 42.8 |
| eval-10-subscription-billing | 8/16 | 12/16 | 70183 | 68115 | 87.5 | 62.0 |
| eval-12-subscription-loyalty-trial | 11/13 | 12/13 | 70364 | 68448 | 88.1 | 64.2 |
| eval-13-shipping-partial-applicability | 6/11 | 8/11 | 69709 | 70521 | 83.8 | 92.3 |
| eval-14-weekly-pay | 15/19 | 15/19 | 170186 | 361428 | 207.2 | 280.8 |
| eval-15-reis-discount | 15/20 | 14/20 | 223772 | 322521 | 413.8 | 384.2 |
| eval-16-order-splitting | 18/19 | 13/19 | 77037 | 93969 | 181.1 | 475.7 |
| eval-17-shopping-cart | 18/18 | 15/18 | 74187 | 70005 | 135.5 | 86.0 |
| eval-18-convert-from-code | 12/14 | 10/14 | 194698 | 73488 | 122.8 | 102.5 |
| eval-19-convert-from-parameterized | 8/8 | 8/8 | 104360 | 67155 | 24.2 | 20.9 |
| eval-20-collections-and-quoting | 9/10 | 9/10 | 158341 | 119634 | 123.4 | 141.7 |
| eval-21-event-registration-sbe | 11/13 | 12/13 | 66758 | 66217 | 35.1 | 29.5 |
| eval-22-event-registration-tt | 16/20 | 14/20 | 178842 | 220456 | 276.9 | 216.4 |
| eval-23-loan-approval-tt | 9/14 | 10/14 | 209546 | 218847 | 132.2 | 174.3 |
| eval-24-weekly-pay-sbe | 6/9 | 6/9 | 68158 | 69185 | 58.7 | 72.0 |
| eval-25-convert-from-spock | 10/15 | 10/15 | 265980 | 200782 | 209.5 | 147.8 |
| eval-26-convert-from-kotest | 12/15 | 9/15 | 210373 | 132257 | 206.4 | 180.1 |
| eval-27-convert-from-testng | 14/15 | 12/15 | 164842 | 201361 | 190.6 | 159.2 |
| eval-28-convert-from-methodsource | 13/15 | 15/15 | 133727 | 209554 | 202.5 | 496.3 |

## Per-Eval Results

### ✅ Eval eval-1-convert-repetitive-tests [with_skill]

**10/10** · 67108 tokens · 21578ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ✅ **has-question-mark-column**: Table has at least one column name ending with '?'
- ✅ **has-three-data-rows**: Table has exactly 3 data rows (GOLD, SILVER, BRONZE)
- ✅ **single-assertion-in-method**: Test method body contains only one assertEquals
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements
- ✅ **scenario-column-present**: Table has a scenario/description column as leftmost column
- ✅ **has-descriptive-title**: Test method has clear, descriptive title
- ✅ **description-if-present-adds-information**: @Description provides context beyond table rows if present
- ✅ **annotation-order**: Annotations in order: @DisplayName (if present), @Description (if present), @TableTest
- ✅ **description-uses-textblock**: If @Description present and longer than one line, uses text block not concatenation

### ✅ Eval eval-2-parse-dates [with_skill]

**13/13** · 112865 tokens · 64463ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ✅ **null-as-blank-cell**: Null input is represented as a blank cell (not the string 'null') in the table
- ✅ **exception-handled-cleanly**: The empty string / exception case is handled — either via a Throws? column, assertThrows inside the method, or a separate @TableTest — rather than being silently omitted
- ✅ **localdate-result-column**: There is a result column typed as LocalDate (or using a string representation that maps to LocalDate)
- ✅ **type-conversion-addressed**: The response addresses type conversion for non-trivial column types — either by providing a @TypeConverter/converter method, or by using a representation that leverages built-in conversion (e.g., FQCN for Class<?>, ISO format for LocalDate)
- ✅ **has-descriptive-title**: Test method has either a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case (e.g. 'dateParsing' → 'Date Parsing'). Not a generic name like 'test1' or 'testMethod'.
- ✅ **description-if-present-adds-information**: If @Description is present, it provides context beyond what the table rows already express — such as where/when the rule applies, or open questions. Does NOT merely restate the column names or summarise what the rows show. It is acceptable to omit @Description if the table already conveys all relevant context (e.g. supported date formats are visible as table rows).
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **description-uses-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +. Passes if @Description is absent.
- ✅ **empty-string-uses-quotes**: Empty string input uses quoted syntax (e.g. '' or "") in the table — not a blank cell, which represents null. The distinction between empty string and null is preserved.
- ✅ **concerns-decomposed**: Multiple tables (or @TableTest methods) are used, each addressing a distinct concern — not one monolithic table mixing unrelated rules. Acceptable to use a single table only when the domain genuinely has a single concern.
- ✅ **minimal-rows-per-concern**: Each table has only the rows needed to express its concern's rules — no unnecessary permutations from combining concerns. Fewer rows per table is expected when concerns are properly separated.
- ✅ **separates-valid-and-invalid**: Valid parsing cases and error/exception cases are in separate @TableTest methods

### ✅ Eval eval-3-dependency-setup [with_skill]

**4/4** · 103483 tokens · 11579ms

- ✅ **flags-junit-version**: Response explicitly flags that JUnit 5.10.0 is below the required minimum (5.11) and recommends upgrading
- ✅ **provides-correct-groupid**: Response provides the tabletest dependency with groupId 'org.tabletest' (not guessed)
- ✅ **provides-artifactid**: Response includes 'tabletest-junit' as the artifactId
- ✅ **includes-test-scope**: The suggested dependency includes <scope>test</scope>

### ⚠️ Eval eval-4-loan-approval [with_skill]

**9/13** · 66673 tokens · 32346ms

- ✅ **produces-markdown-table**: Output contains a markdown table (using | column | syntax)
- ✅ **output-column-has-question-mark**: At least one output column name ends with '?' (e.g. 'Eligible?' or 'Approved?')
- ✅ **business-language-columns**: Column names use domain/business language — no implementation terms like 'creditScore', 'isEligible', 'boolean', 'int'
- ✅ **senior-threshold-row**: Table includes at least one row specifically for the senior applicant (65+) threshold scenario
- ✅ **missing-income-marked-open**: The missing income scenario is represented — either as a row with an open/uncertain expected value (?, TBD, or blank) or as an explicit open question note
- ✅ **scenario-names-describe-conditions**: Scenario names describe conditions ('Senior applicant at threshold') not outcomes ('Approved')
- ✅ **threshold-values-visible**: The policy thresholds (650 for standard, 600 for senior) appear as concrete values in the Credit Score column — not just described in prose. The reader can see the boundary values in the table rows.
- ✅ **scenario-column-present**: Table has a scenario/description column as the leftmost column
- ❌ **threshold-as-column**: Policy thresholds (650/600 credit scores) appear in a dedicated policy column (e.g. 'Credit threshold') separate from the applicant's actual credit score column. The reader sees both the threshold and the score, making the comparison explicit.
  > Table has 'Credit Score' column but no separate 'Credit Threshold' column; thresholds only appear as row values
- ✅ **concrete-domain-values**: Cell values use concrete domain terms (e.g. '65', '700', 'Stable', 'yes', 'no') — not abstract codes or raw booleans like 'true', 'false', 'CATEGORY_A', '1'.
- ❌ **concerns-decomposed**: Multiple tables (or @TableTest methods) are used, each addressing a distinct concern — not one monolithic table mixing unrelated rules. Acceptable to use a single table only when the domain genuinely has a single concern.
  > Single 13-row table mixes age boundary, credit score rules, and income concerns; domain has multiple distinct rules
- ❌ **minimal-rows-per-concern**: Each table has only the rows needed to express its concern's rules — no unnecessary permutations from combining concerns. Fewer rows per table is expected when concerns are properly separated.
  > Single 13-row table combines all concerns; expected: separate tables with minimal rows per concern
- ❌ **separates-age-credit-income**: Age boundary policy, credit score categorisation, and income status are separated into distinct tables, with a final table combining these for the expected verdict
  > Response provides one monolithic table; expected: separate tables for age, credit, income, plus final combining table

### ✅ Eval eval-5-order-transitions [with_skill]

**10/10** · 67381 tokens · 43786ms

- ✅ **produces-markdown-table**: Output contains at least one markdown table
- ✅ **cancellation-coverage**: Table covers cancellation rules — includes rows for states where cancellation is allowed and where it is not
- ✅ **return-window-addressed**: The 30-day return window rule is addressed — either as a threshold column, a separate table, or flagged as an open question
- ✅ **value-set-or-multiple-states**: Uses value sets {PENDING, CONFIRMED} or equivalent to express 'regardless of which starting state' for cases where a rule holds across states
- ✅ **output-column-has-question-mark**: At least one output column ends with '?'
- ✅ **scenario-column-present**: Table has a scenario/description column as the leftmost column
- ✅ **row-independence**: Each row is independently verifiable — no row references a prior row's outcome. Each row specifies its own starting state and expected result.
- ✅ **concerns-decomposed**: Multiple tables (or @TableTest methods) are used, each addressing a distinct concern — not one monolithic table mixing unrelated rules.
- ✅ **minimal-rows-per-concern**: Each table has only the rows needed to express its concern's rules — no unnecessary permutations from combining concerns.
- ✅ **separates-transitions-and-returns**: Status transition rules and return eligibility rules are in separate tables

### ⚠️ Eval eval-6-discount-interaction [with_skill]

**6/7** · 65454 tokens · 19820ms

- ✅ **produces-markdown-table**: Output contains a markdown table
- ✅ **does-not-invent-resolution**: The output does NOT silently resolve the stacking/cap ambiguity — it leaves the conflicting cases as open questions, blank cells, or explicitly marks them as unresolved
- ✅ **covers-both-discounts-applying**: Table includes at least one row where both bulk and loyalty discounts apply simultaneously (exposing the interaction)
- ✅ **open-question-surfaced**: The unresolved decision (stacking vs. higher-only vs. cap) is explicitly surfaced — via an Open Questions column, a '?' cell, or a note
- ✅ **output-column-has-question-mark**: At least one output column ends with '?'
- ❌ **extreme-discount-row**: Table includes at least one row with a high combined discount (e.g. large order + top loyalty tier) that would force a decision about whether a cap applies — making the cap question concrete, not theoretical.
  > Highest combined discount is 25% on £200 order with 15% loyalty; numbers are modest, not extreme enough to force urgent cap decision
- ✅ **scenario-column-present**: Table has a scenario/description column as the leftmost column

### ✅ Eval eval-7-permission-check [with_skill]

**11/11** · 108161 tokens · 36327ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ✅ **uses-value-sets**: Table uses value-set syntax (curly braces like {READ, WRITE} or {READ, WRITE, DELETE}) to express rules that hold across multiple actions
- ✅ **fewer-than-nine-rows**: Table has fewer than 9 data rows
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements
- ✅ **scenario-column-present**: Table has a scenario/description column as the leftmost column
- ✅ **no-duplicate-role-output**: No two rows share both the same Role value and the same boolean output
- ✅ **has-descriptive-title**: Test method has either a @DisplayName annotation or a method name that reads as a clear, descriptive title
- ✅ **description-if-present-adds-information**: If @Description is present, it provides context beyond what the table rows already express
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest
- ✅ **description-uses-textblock**: If @Description is present and longer than a single line, it uses a text block (triple-quoted string), not string concatenation
- ✅ **scenario-names-describe-conditions**: Scenario names describe conditions ('Admin performs any action', 'Guest reads') — not outcomes ('Allowed', 'Denied')

### ✅ Eval eval-8-money-parse [with_skill]

**13/13** · 118583 tokens · 320471ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ✅ **null-as-blank-cell**: Null input is represented as a blank cell in the valid inputs @TableTest table
- ✅ **exception-has-expected-column**: The exception/invalid-input table has an expected column specifying exception type per row
- ✅ **exception-cases-handled**: The exception cases (empty string, letters-only, negative) are handled
- ✅ **result-column-with-question-mark**: Valid inputs have a result column name ending with '?'
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements
- ✅ **has-descriptive-title**: Test method has either a @DisplayName annotation or a method name that reads as a clear, descriptive title
- ✅ **description-if-present-adds-information**: If @Description is present, it provides context beyond what the table rows already express
- ✅ **annotation-order**: Annotations appear in order: @DisplayName, @Description, @TableTest
- ✅ **description-uses-textblock**: If @Description is present and longer than one line, it uses a text block
- ✅ **concerns-decomposed**: Multiple tables address distinct concerns
- ✅ **minimal-rows-per-concern**: Each table has only the rows needed to express its concern's rules
- ✅ **separates-valid-and-invalid**: Valid parsing cases and error/exception cases are in separate @TableTest methods

### ✅ Eval eval-9-bonus-contractor-structure [with_skill]

**11/11** · 109012 tokens · 51272ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ✅ **contractor-uses-value-set**: CONTRACTOR row uses a value set for Department (e.g. {SALES, ENGINEERING}) to express 'regardless of department' — not a dummy placeholder like 'ANY' or 'N/A', and not enumerated as separate rows.
- ✅ **four-core-rules-covered**: The four level×department combinations (SENIOR+SALES, SENIOR+ENGINEERING, JUNIOR+SALES, JUNIOR+ENGINEERING) are all present in the table(s)
- ✅ **expects-bonus-percentage**: The expected output column contains bonus percentages (e.g. 15%, 12%, 0%) — not bonus amounts.
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements
- ✅ **has-descriptive-title**: Test method has either a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case.
- ✅ **description-if-present-adds-information**: If @Description is present, it provides context beyond what the table rows already express — such as how bonus rates are applied (e.g. as percentage of base salary), or open questions.
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **description-uses-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +.
- ✅ **scenario-names-describe-conditions**: Scenario names describe conditions ('Senior in Sales', 'Contractor regardless of department') — not outcomes ('Gets 15%', 'No bonus').
- ✅ **business-language-columns**: Column names use domain/business language (e.g. 'Level', 'Department', 'Bonus %') — not implementation terms like 'employeeLevel', 'deptCode', 'result'.

### ⚠️ Eval eval-10-subscription-billing [with_skill]

**8/16** · 70183 tokens · 87546ms

- ✅ **produces-multiple-tables**: Output contains at least 2 separate markdown tables (not everything crammed into one table)
- ❌ **tables-have-distinct-concerns**: The tables cover distinct concerns — e.g. pricing/plans is separate from cancellation/refund rules
  > Open Questions table is meta-analysis of unresolved issues, not a distinct business concern. Main table mixes plans, amounts, and refund calculations without separation.
- ❌ **output-columns-have-question-marks**: At least one output column in each table ends with '?'
  > Open Questions table columns are 'Open Question' and 'Impact' — neither header ends with '?'
- ✅ **refund-exception-covered**: The 24-hour no-refund exception is included as a distinct scenario row
- ✅ **business-language-columns**: Column names use domain/business language — no implementation terms like 'boolean', 'int', 'isProrated', camelCase identifiers
- ✅ **trial-cancellation-row**: Includes a row or scenario for cancelling during the free trial — no charge was made, so no refund applies.
- ✅ **prorated-refund-formula**: The prorated refund calculation is shown or explained — e.g. days remaining × daily rate, or an equivalent formula.
- ❌ **24h-boundary-near-boundary**: The 24-hour no-refund window is tested near the boundary (e.g. values at or close to 23h, 24h, 25h)
  > Rows test 'within 24h' vs 'outside' only. Open Questions acknowledges issue: 'What happens at exactly 24h00m00s?' — boundary not precisely tested.
- ❌ **eligibility-separate-from-amount**: Refund eligibility and refund amount are in separate tables — not mixed into one table.
  > Single table with 'Refund?' column mixing eligibility (yes/no implicitly) and amounts (£6.66, £0.00, etc.) in one column
- ❌ **question-mark-only-on-outputs**: The '?' suffix is only used on output or derived columns, not on given input columns.
  > 'Within 24h of Renewal Charge?' ends with ? but contains input scenario conditions (yes/no values), not derived outputs
- ✅ **output-values-traceable**: Output values can be derived from input values by a reader — the calculation is visible or explained.
- ✅ **refund-table-includes-price-paid**: The refund amount table includes the price paid as an input column, making the table self-contained.
- ❌ **refund-table-shows-proportion**: The refund amount table includes an intermediate traceability column (e.g. 'Refund Proportion?') before the final amount.
  > Table jumps from inputs (Amount Paid, Days in Billing Period, Days Used) directly to Refund amount. No Days Remaining or Proportion column shown.
- ❌ **rules-separate-from-arithmetic**: Refund eligibility and refund amount are treated as separate concerns — not mixed in one table.
  > Single table mixes eligibility rule ('Within 24h...?'), trial status, and prorated calculation in one monolithic table
- ❌ **concerns-decomposed**: Multiple tables are used, each addressing a distinct concern — not one monolithic table mixing unrelated rules.
  > Only one main table handles all refund logic. Should separate 'Is refund eligible?' from 'What is refund amount?'
- ✅ **minimal-rows-per-concern**: Each table has only the rows needed to express its concern's rules — no unnecessary permutations.

### ⚠️ Eval eval-12-subscription-loyalty-trial [with_skill]

**11/13** · 70364 tokens · 88099ms

- ✅ **produces-multiple-tables**: Output contains at least 2 separate markdown tables
- ✅ **annual-no-trial-rule-represented**: A table or note explicitly captures that the free trial is not available on annual plans (not just implied)
- ✅ **loyalty-discount-row-present**: At least one table row demonstrates the loyalty programme discount scenario for annual plan subscribers
- ✅ **surfaces-genuine-open-questions**: The response identifies at least one genuinely underspecified interaction
- ❌ **open-question-surfaced**: The annual/loyalty/trial interaction is correctly resolved with a note explaining mutual exclusivity
  > Note only states "monthly loyalty members...doesn't affect price" but doesn't explicitly explain annual/loyalty vs monthly/trial exclusivity
- ✅ **loyalty-discount-annual-only**: Makes clear that loyalty discount applies to annual only; monthly loyalty pays same as non-loyalty
- ✅ **loyalty-refund-ambiguity**: Loyalty annual refund is surfaced as question or calculated with visible trace from discounted price
- ✅ **cancellation-refund-table**: Includes a cancellation/refund table or section with concrete rows
- ✅ **refund-table-includes-loyalty-dimension**: Refund table includes a loyalty member column to distinguish loyalty vs non-loyalty
- ✅ **question-mark-only-on-outputs**: '?' suffix only on output/derived columns, not input columns
- ✅ **concerns-decomposed**: Multiple tables address distinct concerns, not monolithic table mixing unrelated rules
- ✅ **minimal-rows-per-concern**: Each table has only rows needed to express its concern; fewer rows when separated
- ❌ **separates-pricing-trial-loyalty-refund**: Pricing/plans, trial eligibility, loyalty discount, and cancellation/refund in separate tables
  > Subscription Charge table combines pricing, trial eligibility, and loyalty discount; Cancellation Refund separate. Assertion requires 4 separate concerns, response merges first 3.

### ⚠️ Eval eval-13-shipping-partial-applicability [with_skill]

**6/11** · 69709 tokens · 83850ms

- ✅ **produces-markdown-table**: Output contains a markdown table
- ✅ **express-split-by-uk-free-tier**: Express shipping is split into at least two cases distinguishing the UK free tier (UK, order £50+, free) from the paid tier. The split is driven by the UK-only free threshold rule.
- ❌ **express-uses-value-sets**: Express rows use value sets to consolidate cases with the same outcome — e.g. non-UK destinations at/above threshold in one row (such as {Ireland, Other} or {Ireland, International}), or UK at/above threshold in one row ({£50, £75}). Not enumerated as separate rows per destination or per order value when the result is identical. Any reasonable label for non-UK/Ireland destinations is acceptable (International, Other, Rest of World, etc.).
  > Separate rows for 'Express | £50.00 | UK | £50.00 | £0.00' and 'Express | £75.00 | UK | £50.00 | £0.00' should consolidate to {£50, £75}
- ❌ **overnight-grouped**: Overnight UK and Ireland are in one row using a value set ({UK, Ireland}) — not separate rows, since both have the same cost and availability.
  > 'Overnight to UK' and 'Overnight to Ireland' are separate rows, not consolidated in one row with {UK, Ireland}
- ✅ **standard-destination-value-set-or-blank**: Standard shipping destination uses a value set covering all options (e.g. {UK, Ireland, International} or {UK, Ireland, Other}) or is blank/absent — either approach is valid since destination is irrelevant to the cost. Any reasonable label for non-UK/Ireland destinations is acceptable.
- ✅ **output-column-has-question-mark**: At least one output column ends with '?'
- ✅ **concrete-domain-values**: Cell values use concrete domain terms (e.g. 'Standard', 'Express', 'UK', '£3.99') — not abstract codes or raw booleans like 'true', 'false', 'TYPE_1', '1'.
- ✅ **blank-vs-value-set-correct**: Value sets (not blanks) are used for irrelevant inputs — e.g. Standard shipping destination uses a value set like {UK, Ireland, International} because destination exists but doesn't affect the cost. Blanks are reserved for genuinely absent/N/A inputs. The distinction between 'irrelevant to this rule' (value set) and 'not applicable' (blank) is correct.
- ❌ **concerns-decomposed**: Multiple tables (or @TableTest methods) are used, each addressing a distinct concern — not one monolithic table mixing unrelated rules. Acceptable to use a single table only when the domain genuinely has a single concern.
  > Author states 'Two distinct concerns here: what shipping options are available, and what they cost' but only one table is provided
- ❌ **minimal-rows-per-concern**: Each table has only the rows needed to express its concern's rules — no unnecessary permutations from combining concerns. Fewer rows per table is expected when concerns are properly separated.
  > 'Overnight not available internationally' provides availability info, unnecessary in cost table if availability was separated
- ❌ **separates-availability-and-cost**: Shipping method availability and shipping cost are in separate tables
  > Single table 'Shipping Cost Calculation' contains both costs ('£14.99') and availability ('Not available') in Cost column

### ⚠️ Eval eval-14-weekly-pay [with_skill]

**15/19** · 170186 tokens · 207228ms

- ❌ **1.1-traceability-columns**: The pay table includes intermediate expected columns (e.g. Regular pay?, Overtime pay?, Premium pay?) so the reader can trace which rule contributes what — not just a single total Weekly pay? column.
  > Table columns are 'Scenario | Weekday hrs | Sunday hrs | Holiday hrs | Rate | Total pay?' with only Total pay? shown, no Regular pay?, Overtime pay?, or Premium pay? columns
- ✅ **1.2-error-has-expected-column**: The error/rejection table has an expected column (e.g. 'Throws?' or 'Exception?') specifying the exception type per row — rather than hardcoding the exception type in @Description or the method name.
- ✅ **1.3-depth-overtime-boundary**: Overtime threshold boundary is covered: at least a value at the threshold (40) and just above (41 or similar). Not just values well below and well above.
- ✅ **1.4-depth-combined-scenario**: A combined scenario is present: a row with weekday + Sunday + holiday hours, testing the composition of all hour types.
- ❌ **1.5-depth-error-edge-cases**: Error/edge cases covered: at least negative hours (floored at zero) and negative or missing rate (rejected).
  > Error table only tests negative rates ('Minimum negative | -0.01' and 'Large negative | -100.00'), no negative hours edge case rows
- ❌ **1.6-readability-empty-cells**: Sunday and Holiday hour columns use empty cells (not 0) when those inputs are not relevant to a scenario — making it easier to spot which rows involve weekend/holiday hours. This is a readability preference; parameter types should be Integer (not int) to support null from blank cells.
  > Table uses '0' for empty cells (e.g. 'Weekday hours under 40 | 20 | 0 | 0') and parameters are 'int' not 'Integer'
- ✅ **1.7-readability-scenario-names**: Scenario names describe work patterns (e.g. 'Part-time', 'Five hours overtime', 'Full Sunday shift') — not 'Test case 1' or expected outcomes.
- ✅ **1.8-correctness-expected-values**: Expected values in intermediate and total columns are arithmetically correct for every row, consistent with the formulas in @Description.
- ✅ **1.9-correctness-value-set-semantics**: Value sets are only used where all values in the set produce the same expected result (e.g. any rate × 0 = 0). Not used where results would differ.
- ✅ **1.10-format-displayname**: @DisplayName is present on test methods, OR method names read as clear descriptive titles when converted from camelCase/snake_case.
- ✅ **1.11-format-description**: @Description is present on at least the pay calculation method and provides context beyond what the table rows already express — such as the overtime threshold (40 hours), premium multipliers (Sunday 2×, Holiday 2.5×), or other fixed assumptions not visible in the table.
- ✅ **1.12-format-annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **1.13-format-description-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +. Passes if @Description is absent.
- ✅ **1.14-depth-zero-rate**: A row where the hourly rate is zero, showing that pay is zero regardless of hours worked. This is a distinct edge case from negative rate (which is rejected).
- ✅ **1.15-format-clean-method**: The test method body contains no if/ternary null-handling or parsing logic. Null-to-default conversion (e.g. blank Sunday hours → 0) is handled via a @TypeConverter method or a private helper, keeping the test method limited to arrange/act/assert.
- ✅ **business-language-columns**: Column names use domain/business language (e.g. 'Weekday hours', 'Sunday hours', 'Hourly rate', 'Weekly pay?') — not implementation terms like 'weekdayHrs', 'int', 'param1'.
- ✅ **concerns-decomposed**: Multiple tables (or @TableTest methods) are used, each addressing a distinct concern — not one monolithic table mixing unrelated rules. Acceptable to use a single table only when the domain genuinely has a single concern.
- ✅ **minimal-rows-per-concern**: Each table has only the rows needed to express its concern's rules — no unnecessary permutations from combining concerns. Fewer rows per table is expected when concerns are properly separated.
- ❌ **separates-classification-and-calculation**: Payable hours categorisation (1x regular, 1.5x overtime, 2x Sunday/holiday) and pay calculation (hours × rate) are in separate @TableTest methods
  > Only calculateWeeklyPay method combines classification and calculation; no separate table for categorization (showing which hours get 1x/1.5x/2x)

### ⚠️ Eval eval-15-reis-discount [with_skill]

**15/20** · 223772 tokens · 413788ms

- ❌ **2.1-decomposition-concern-separation**: Discount ladder, and traveller eligibility are in separate tables. Rolling window counting as a third table is desirable.
  > First @TableTest combines 'Passenger type' and 'Tickets in last 30 days' in one table—both discount ladder and eligibility mixed, not separate.
- ✅ **2.2-children-flat-discount**: Children's flat 20% discount is represented — either as a row in the eligibility table or a separate note. It does not follow the ladder.
- ✅ **2.3-depth-tier-boundaries**: The discount ladder has multiple tiers derived from the 'every fifth trip' rule, with boundary values or value sets showing which ticket counts produce which discount. The maximum 40% discount is represented.
- ✅ **2.4-depth-rolling-window-boundary**: The 30-day rolling window boundary is tested: a ticket at exactly 30 days is included, at 31 days is excluded.
- ❌ **2.6-readability-human-readable-values**: Eligibility uses human-readable values (yes/no or equivalent) rather than raw true/false. TypeConverter or similar mechanism mentioned.
  > Uses 'Adult', 'Senior', 'Child' (human-readable) but no TypeConverter or conversion mechanism mentioned anywhere.
- ✅ **2.9-correctness-value-set-tier-semantics**: Value sets in the tier table contain exactly the values that produce the same discount. No cross-tier contamination.
- ✅ **2.15-ticket-count-uses-value-sets**: The discount ladder table uses value sets for the ticket count column to group counts within the same tier (e.g. {5, 6, 7, 8, 9} or {5, 9} for min/max → 5%) — not one row per boundary value.
- ✅ **2.16-no-duplicate-tier-mapping**: The tier-to-discount mapping is expressed once — not repeated across multiple tables.
- ✅ **2.10-format-displayname**: @DisplayName is present on test methods, OR method names read as clear descriptive titles when converted from camelCase/snake_case.
- ✅ **2.11-format-description**: @Description is present on at least the class or the tier method and provides context beyond what the table rows already express.
- ❌ **2.12-format-typeconverter**: TypeConverterSources is used for at least one human-readable conversion (yes/no, or rolling window notation).
  > No TypeConverterSources annotations found anywhere in the code.
- ✅ **2.13-format-annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **2.14-format-description-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +.
- ❌ **2.17-zone-irrelevance-visible**: The eligibility/passenger-type table includes a Zone column (or equivalent) with a value set to show that the discount applies regardless of zone.
  > First table columns: 'Passenger type | Tickets in last 30 days | Discount %'—Zone column omitted despite requirement statement.
- ✅ **2.18-adult-senior-value-set**: Adult and senior are expressed as a value set {ADULT, SENIOR} (or equivalent) in at least one row, since they follow identical discount rules.
- ✅ **2.19-depth-all-tiers**: All 9 discount tiers are represented: 0%, 5%, 10%, 15%, 20%, 25%, 30%, 35%, 40%.
- ✅ **2.20-readability-one-row-per-tier**: Each tier in the discount ladder is expressed as a single row with a value set—not split across multiple rows.
- ❌ **2.21-readability-relative-time**: The rolling window table expresses time relatively (e.g. '14 days ago', '30 days ago', or integer 'Days ago' column) rather than absolute dates.
  > Data column shows absolute dates: '[2026-03-07, 2026-03-15, 2026-04-04]' instead of relative notation like '29 days ago'.
- ✅ **concerns-decomposed**: Multiple @TableTest methods, each addressing a distinct concern — not one monolithic table mixing unrelated rules.
- ✅ **minimal-rows-per-concern**: Each table has only rows needed to express its concern—no unnecessary permutations from combining concerns.

### ⚠️ Eval eval-16-order-splitting [with_skill]

**18/19** · 77037 tokens · 181142ms

- ✅ **3.1a-concern-fulfillment-method**: Fulfillment method splitting is represented as its own table — items with different fulfillment types (store pickup vs home delivery) cannot share a shipment.
- ✅ **3.1b-concern-delivery-address**: Delivery address splitting is represented as its own table — items going to different addresses must be in separate shipments.
- ✅ **3.1c-concern-availability**: Availability splitting is represented as its own table — in-stock items ship immediately, backordered/pre-ordered items ship when available.
- ✅ **3.1d-concern-warehouse-allocation**: Warehouse allocation (minimising shipments) is represented as its own table — choosing which warehouses to ship from to minimise total shipment count.
- ✅ **3.1e-concern-companion-products**: Companion product grouping is represented as its own table — paired items (e.g. camera body and lens) should ship together from the same location when possible.
- ✅ **3.2-depth-fulfillment-scenarios**: Fulfillment table covers at least: all items for home delivery, all items for store pickup, and a mix of delivery and pickup items.
- ✅ **3.3-depth-delivery-address-scenarios**: Delivery address table covers at least: all items to the same address, and items to different addresses.
- ✅ **3.4-depth-availability-scenarios**: Availability table covers at least: all items in stock, a mix of in-stock and pre-order/backorder, and two backordered/pre-ordered items with different dates.
- ✅ **3.5-depth-warehouse-scenarios**: Warehouse allocation table covers at least: single warehouse has full quantity, multi-warehouse split needed, fewer-warehouse split preferred over more warehouses, and cannot fulfill (insufficient total stock).
- ❌ **3.6-depth-companion-scenarios**: Companion table covers at least: single warehouse has both companions, unavoidable split (no warehouse has both but both are available), and fulfillment not possible (one companion not available anywhere).
  > Table covers 'Shared warehouse available' (both in one warehouse) and 'No warehouse stocks both — forced split' (unavoidable split), but lacks scenario where one companion is unavailable anywhere.
- ✅ **3.7-readability-scenario-names**: Scenario names describe the business situation (e.g. 'Consolidate: two beats three', 'Everything at one warehouse') — not 'Test case 2'.
- ✅ **3.8-readability-item-property-mapping**: Trigger tables represent multi-item orders where each item has its own fulfillment type, address, and availability. The notation makes the item-to-property mapping unambiguous — not value sets like {delivery, pickup}.
- ✅ **3.9-readability-scalar-column**: Warehouse allocation table uses a scalar count column (e.g. 'Qty ordered') not item lists, when product identity doesn't matter for the logic.
- ✅ **3.10-readability-business-language**: Column names use business/domain language (e.g. 'Shipments?' not 'Result?', 'Warehouse stock' not 'inventory_map'). The tables read as a specification a product person could review.
- ✅ **3.11-readability-output-column-question-mark**: Output/expected columns end with '?' (e.g. 'Shipments?', 'Groups?') to distinguish them from input columns.
- ✅ **3.12-correctness-valid-assignments**: Inventory assignments are physically valid: items are only assigned to locations that have them. Shipment counts match the assignments shown.
- ✅ **3.13-depth-open-questions**: Multiple open questions are surfaced across the tables — ambiguities, edge cases, or product decisions that the requirements don't fully specify.
- ✅ **concerns-decomposed**: Multiple tables (or @TableTest methods) are used, each addressing a distinct concern — not one monolithic table mixing unrelated rules.
- ✅ **minimal-rows-per-concern**: Each table has only the rows needed to express its concern's rules — no unnecessary permutations from combining concerns.

### ✅ Eval eval-17-shopping-cart [with_skill]

**18/18** · 74187 tokens · 135499ms

- ✅ **4.1a-concern-item-operations**: Item operations (add/remove) are represented as their own table or tables — separate from coupon and cart total logic.
- ✅ **4.1b-concern-coupon-application**: Coupon application (entering, replacing, rejecting codes) is represented as its own table — separate from item operations and cart total calculation.
- ✅ **4.1c-concern-cart-total**: Cart total calculation (how different coupon types affect the price) is represented as its own table — separate from coupon application and item operations.
- ✅ **4.1d-concern-checkout**: Checkout (stock verification) is represented as its own table — separate from the other concerns.
- ✅ **4.3-rows-independently-executable**: Each table row is independently executable — no row depends on a prior row's result. The table expresses rules, not a sequential test script.
- ✅ **4.4-depth-item-operations**: Item operations cover at least: add to empty cart, add to non-empty cart, add more of an item already in cart, remove item from cart, remove last item in cart, remove item not in cart.
- ✅ **4.5-depth-coupon-application**: Coupon application table covers validity and replacement — not coupon types. Scenarios include at least: add valid coupon (none active), add valid coupon (another active), add expired coupon (none active), add expired coupon (another active), add nonexistent code (none active), add nonexistent code (another active).
- ✅ **4.6-depth-cart-total-scenarios**: Cart total table covers how different coupon types affect the price. Scenarios include at least: no coupon, percentage off whole cart, fixed amount off, fixed amount exceeding cart total (floors at zero), product-specific discount (product in cart), product-specific discount (product not in cart).
- ✅ **4.7-depth-checkout-scenarios**: Checkout table covers at least: empty cart, all items in stock, and not all items in stock (insufficient inventory).
- ✅ **4.8-readability-business-language**: Column names use business/user-perspective language (e.g. 'Message?' not 'Result?', 'Coupon code' not 'input'). The tables read as a specification a product person could review.
- ✅ **4.9-readability-test-data-visible**: Test data that affects the outcome — product prices, coupon rules (type, amount, expiry) — is visible in the table via columns, notes, or explicit setup, not silently hardcoded. The reader can trace how inputs lead to outputs.
- ✅ **4.10-depth-open-questions**: Multiple open questions are surfaced — ambiguities or product decisions the requirements don't specify (e.g. what happens to a product-specific coupon when that product is removed, or how percentage and product-specific discounts interact).
- ✅ **4.11-correctness-cart-totals**: Cart totals are arithmetically correct: sum(quantity × price) − coupon = total, for every row in the total calculation table.
- ✅ **4.12-coupon-expiry-column**: Coupon application table uses a column to signal expiry status — either an 'Expired?' yes/no column, a 'Status' column, or equivalent. Expiry is visible per row, not just implied by the scenario name.
- ✅ **4.14-coupon-before-after-columns**: Coupon application table uses before/after columns (e.g. 'Active coupon' / 'Active coupon after?') to specify the state change when entering, replacing, or entering invalid coupon codes.
- ✅ **4.15-readability-scenario-names**: Scenario names describe the business situation (e.g. 'Add more of same product', 'Replace active coupon') — not 'Test case 1' or generic labels.
- ✅ **concerns-decomposed**: Multiple tables (or @TableTest methods) are used, each addressing a distinct concern — not one monolithic table mixing unrelated rules. Acceptable to use a single table only when the domain genuinely has a single concern.
- ✅ **minimal-rows-per-concern**: Each table has only the rows needed to express its concern's rules — no unnecessary permutations from combining concerns. Fewer rows per table is expected when concerns are properly separated.

### ⚠️ Eval eval-18-convert-from-code [with_skill]

**12/14** · 194698 tokens · 122827ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ✅ **black-box-columns**: Table columns represent the method's public inputs (applicant type, age, claim count) and observable outputs (decision, premium)
- ✅ **observable-io-only**: No column is named after private fields or internal variables (hasActivePolicy, internalRiskScore, riskScore)
- ✅ **scenario-column-present**: Table has a scenario/description column as the leftmost column
- ✅ **business-language-columns**: Column names use domain/business language, not code identifiers
- ✅ **scenario-names-describe-conditions**: Scenario names describe conditions, not outcomes
- ✅ **has-question-mark-column**: Table has at least one column name ending with '?'
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements
- ✅ **annotation-order**: Annotations appear in order: @DisplayName, @Description, @TableTest
- ✅ **has-descriptive-title**: Test method has a descriptive @DisplayName or method name
- ✅ **description-uses-textblock**: If @Description is present and text is long, uses text block (triple-quotes), not string concatenation
- ❌ **concerns-decomposed**: Multiple tables address distinct concerns (or acceptable if domain genuinely has single concern)
  > Single @TableTest mixes decision logic (AUTO_APPROVED/REJECTED) and premium calculation. These are distinct concerns, requiring separation.
- ✅ **minimal-rows-per-concern**: Each table has only the rows needed to express its concern's rules
- ❌ **separates-decision-and-premium**: Decision logic and premium calculation are in separate @TableTest methods
  > Single method tests both 'Status?' (decision) and 'Premium?' (premium) in same @TableTest

### ✅ Eval eval-19-convert-from-parameterized [with_skill]

**8/8** · 104360 tokens · 24157ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ✅ **scenario-column-present**: Table has a scenario/description column as the leftmost column — this was missing in the @ParameterizedTest and should be added.
- ✅ **has-question-mark-column**: At least one output column name ends with '?' (e.g. 'Valid?' or 'Expected?')
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements
- ✅ **scenario-names-describe-conditions**: Scenario names describe conditions ('Valid standard format', 'Missing local part') — not outcomes ('True', 'False') or 'Test case 1'.
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **has-descriptive-title**: Test method has either a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case.
- ✅ **description-uses-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +. Passes if @Description is absent.

### ⚠️ Eval eval-20-collections-and-quoting [with_skill]

**9/10** · 158341 tokens · 123444ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ✅ **list-syntax-correct**: List values in the table use bracket syntax like [tech:java, biz:sales, dev:ci] — not comma-separated strings without brackets.
- ✅ **empty-list-explicit**: Empty list input uses [] (not a blank cell, which represents null). The distinction between empty list and null is preserved.
- ❌ **special-chars-quoted**: Values containing pipes (|), brackets, or colons (:) are properly quoted (e.g. "tech:java") so they don't conflict with table syntax. Colons without quoting would be mis-interpreted as map key:value entries.
  > [tech:java, biz:sales, dev:ci] - colons present but unquoted; only pipes and brackets quoted
- ✅ **scenario-column-present**: Table has a scenario/description column as the leftmost column
- ✅ **has-question-mark-column**: At least one output column name ends with '?' (e.g. 'Filtered tags?' or 'Result?')
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **has-descriptive-title**: Test method has either a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case.
- ✅ **description-uses-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +. Passes if @Description is absent.

### ⚠️ Eval eval-21-event-registration-sbe [with_skill]

**11/13** · 66758 tokens · 35079ms

- ✅ **produces-markdown-table**: Output contains at least one markdown table (using | column | syntax)
- ✅ **validation-rules-covered**: Email validation and name-required scenarios are present — at least one row for invalid email and one for missing name.
- ❌ **blank-for-absent-optional**: Dietary requirements and/or accessibility needs use blank cells when the attendee has not provided them (genuinely absent) — not 'N/A' or 'none'.
  > Rows mention 'With dietary requirements' and 'With accessibility needs' but table has no Dietary/Accessibility columns to show blank values
- ✅ **output-column-has-question-mark**: At least one output column name ends with '?' (e.g. 'Accepted?', 'Price?')
- ✅ **scenario-names-describe-conditions**: Scenario names describe conditions ('Missing email', 'Early-bird single attendee') — not outcomes ('Rejected', 'Discounted').
- ✅ **open-question-surfaced**: The discount stacking ambiguity (early-bird + group) is surfaced as an open question — not silently resolved.
- ✅ **scenario-column-present**: Table has a scenario/description column as the leftmost column
- ✅ **concerns-decomposed**: Multiple tables (or @TableTest methods) are used, each addressing a distinct concern — not one monolithic table mixing unrelated rules.
- ✅ **minimal-rows-per-concern**: Each table has only the rows needed to express its concern's rules — no unnecessary permutations from combining concerns.
- ✅ **separates-validation-and-pricing**: Input validation rules and pricing/discount calculation are in separate tables
- ✅ **descriptive-registration-date**: Registration date uses descriptive values (e.g. 'before cutoff', 'on cutoff', 'after cutoff') rather than raw dates
- ❌ **no-redundant-policy-columns**: If registration date uses descriptive values like 'before cutoff', the cutoff date is not also present as a separate column.
  > Table has both 'Before Cutoff' column (descriptive) AND 'Early-Bird Cutoff (Policy)' column (2026-05-01 date)
- ✅ **group-size-policy-as-column**: The minimum group size threshold appears as a policy column (e.g. 'Min group size (policy)') — surfacing this as a configurable business rule.

### ⚠️ Eval eval-22-event-registration-tt [with_skill]

**16/20** · 178842 tokens · 276937ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ✅ **validation-rules-covered**: Email validation and name-required error scenarios are present — at least one row for invalid email and one for missing name.
- ✅ **blank-for-absent-optional**: Dietary requirements and/or accessibility needs use blank cells (null) when not provided — not 'N/A' or 'none'. These are genuinely optional and absent.
- ✅ **blank-vs-value-set-correct**: When testing pricing rules, irrelevant inputs (e.g. dietary requirements don't affect price) use value sets or representative values — not blanks (which mean null). Blanks are reserved for genuinely absent/null inputs.
- ✅ **scenario-column-present**: Table has a scenario/description column as the leftmost column
- ✅ **has-question-mark-column**: At least one output column name ends with '?' (e.g. 'Result?', 'Price?')
- ✅ **business-language-columns**: Column names use domain/business language (e.g. 'Name', 'Email', 'Registration date', 'Group size', 'Price?') — not code identifiers.
- ✅ **scenario-names-describe-conditions**: Scenario names describe conditions ('Early-bird single attendee', 'Missing name') — not outcomes ('Rejected', '20% off').
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **has-descriptive-title**: Test method has either a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case.
- ✅ **description-uses-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +. Passes if @Description is absent.
- ✅ **concerns-decomposed**: Multiple tables (or @TableTest methods) are used, each addressing a distinct concern — not one monolithic table mixing unrelated rules. Acceptable to use a single table only when the domain genuinely has a single concern.
- ✅ **minimal-rows-per-concern**: Each table has only the rows needed to express its concern's rules — no unnecessary permutations from combining concerns. Fewer rows per table is expected when concerns are properly separated.
- ✅ **separates-validation-and-pricing**: Input validation and pricing/discount calculation are in separate @TableTest methods
- ✅ **validation-includes-optional-fields**: Validation @TableTest includes Dietary Requirements and Accessibility Needs columns, with rows showing both null (blank) and non-null values are accepted — not hardcoded as null in the method body.
- ❌ **descriptive-registration-date**: Registration date uses descriptive values (e.g. 'before cutoff', 'on cutoff', 'after cutoff') with a @TypeConverter method converting to actual dates — not raw date literals like '2025-02-28'. Readability over precision.
  > Table uses literal dates: "2025-02-28", "2025-03-01", "2025-04-01"—not descriptive values
- ❌ **cutoff-date-column-if-literal-dates**: If registration dates are literal (e.g. '2025-02-28' instead of descriptive), the early-bird cutoff date appears as a separate policy column so the reader can see both dates and verify the comparison. Passes automatically if descriptive date values are used.
  > Literal dates used; pricing table columns are Scenario|RegistrationDate|GroupSize|Price—no cutoff column
- ❌ **description-no-irrelevant-information**: If @Description is present, it does not include information that is already visible in the table columns or that can be derived from the table structure — such as fixed input values that could be columns, or restating the discount rules that the rows already demonstrate.
  > Description restates: "Early-bird: 20% off...→£80", "Group: 15% off...→£85", already shown in table rows
- ❌ **discount-column-preferred**: Output column is 'Discount?' (percentage or amount off) rather than 'Price?' — making verification simpler (discount is the rule's direct output; price requires knowing the base price). If 'Price?' is used instead, both 'Base price' and 'Price?' columns must be present so the reader can trace the calculation.
  > Output column is "Price?" with no "Base price" column for calculation trace

### ⚠️ Eval eval-23-loan-approval-tt [with_skill]

**9/14** · 209546 tokens · 132225ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ❌ **threshold-as-column**: Policy thresholds (650/600 credit scores) appear in a dedicated policy column (e.g. 'Credit threshold') separate from the applicant's actual credit score column.
  > No dedicated threshold column in table
- ✅ **concrete-domain-values**: Cell values use concrete domain terms (e.g. '70', '650', 'APPROVED') — not abstract codes like 'CATEGORY_A' or '1'.
- ✅ **scenario-column-present**: Table has a scenario/description column as the leftmost column
- ❌ **scenario-names-describe-conditions**: Scenario names describe conditions ('Senior at lower threshold', 'Missing income') — not outcomes ('Approved', 'Rejected').
  > 'Standard approval' and 'Senior approval' describe outcomes
- ✅ **business-language-columns**: Column names use domain/business language (e.g. 'Age', 'Credit score', 'Stable income', 'Decision?') — not code identifiers like 'customerAge', 'boolean'.
- ✅ **has-question-mark-column**: At least one output column name ends with '?' (e.g. 'Decision?' or 'Status?')
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **has-descriptive-title**: Test method has either a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case.
- ✅ **description-uses-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +.
- ❌ **concerns-decomposed**: Multiple tables (or @TableTest methods) are used, each addressing a distinct concern — not one monolithic table mixing unrelated rules.
  > Single @TableTest method mixes age boundaries, credit thresholds, income
- ❌ **minimal-rows-per-concern**: Each table has only the rows needed to express its concern's rules — no unnecessary permutations from combining concerns.
  > 12-row table combines age, credit, and income concerns
- ❌ **separates-age-credit-income**: Age boundary policy, credit score categorisation, and income status are separated into distinct @TableTest methods, with a method combining these for the expected verdict
  > One method present; requires distinct @TableTest methods

### ⚠️ Eval eval-24-weekly-pay-sbe [with_skill]

**6/9** · 68158 tokens · 58657ms

- ✅ **produces-markdown-table**: Output contains at least one markdown table (using | column | syntax)
- ✅ **output-column-has-question-mark**: At least one output column name ends with '?'
- ✅ **scenario-names-describe-conditions**: Scenario names describe work patterns or conditions — not outcomes
- ✅ **business-language-columns**: Column names use domain/business language — not code identifiers
- ✅ **scenario-column-present**: Table has a scenario/description column as the leftmost column
- ✅ **overtime-boundary-covered**: Overtime threshold boundary (40 hours) covered with threshold and above
- ❌ **concerns-decomposed**: Multiple tables addressing distinct concerns — not one monolithic table
  > Response contains single 'starting table' with rate selection and pay combined
- ❌ **minimal-rows-per-concern**: Each table has only rows needed — no unnecessary permutations
  > Rows like 'Weekday (under threshold) plus Sunday' combine multiple concerns
- ❌ **separates-classification-and-calculation**: Rate categorisation and pay calculation are in separate tables
  > Single table combines hours classification and pay calculation in one table

### ⚠️ Eval eval-25-convert-from-spock [with_skill]

**10/15** · 265980 tokens · 209532ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ❌ **options-as-map**: Package options collapsed into a single map column like [fragile: true, insuredValue: 500]
  > Three separate columns: "Fragile | Insured value | Handling" instead of single map
- ❌ **options-type-converter**: A @TypeConverter method is present that accepts Map<String, String> and returns PackageOptions
  > No @TypeConverter method present in provided code
- ✅ **dimensions-as-list**: Dimensions are represented as a [L, W, H] list in the table
- ✅ **uses-value-sets**: At least one table uses value set syntax {DHL, UPS, FEDEX}
- ❌ **concerns-decomposed**: Multiple @TableTest methods exist, each addressing a distinct concern
  > Single @TableTest method "shouldCalculateShippingCost" mixing base rates, surcharges, dimensional weight
- ❌ **numeric-types-correct**: Weight and cost parameters use BigDecimal (not double). Dimensions use Integer.
  > Parameters are "double weight" and "double expectedCost", not BigDecimal
- ✅ **scenario-column-present**: Each table has a scenario/description column as the leftmost column
- ✅ **scenario-names-describe-conditions**: Scenario names describe conditions, not outcomes or generic labels
- ✅ **has-question-mark-column**: At least one output column name ends with '?'
- ❌ **business-language-columns**: Column names use domain language, not code identifiers
  > Column "Dims" is a code identifier; should be "Dimensions"
- ✅ **no-if-switch-in-method**: Test method bodies contain no if or switch statements
- ✅ **annotation-order**: Annotations appear in order: @DisplayName, @Description, @TableTest
- ✅ **has-descriptive-title**: Test method has descriptive title via @DisplayName or method name
- ✅ **no-groovy-syntax**: Output contains no Groovy or Spock syntax

### ⚠️ Eval eval-26-convert-from-kotest [with_skill]

**12/15** · 210373 tokens · 206410ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ❌ **options-as-map**: Package options (fragile, insuredValue, handling) are collapsed into a single map column like [fragile: true, insuredValue: 500] — not kept as three separate columns with mostly-blank cells. Rows with no options use a blank cell or [:].
  > Surcharges table shows three separate columns: '| Fragile | Insured Value | Handling |'
- ❌ **options-type-converter**: A @TypeConverter method is present that accepts Map<String, String> (or similar) and returns PackageOptions, applying defaults for missing keys.
  > Response states 'Carrier, BigDecimal, List<Int>, and Boolean all convert built-in — no @TypeConverter needed'
- ✅ **dimensions-as-list**: Dimensions are represented as a [L, W, H] list in the table — not as three separate length/width/height columns
- ✅ **uses-value-sets**: At least one table uses value set syntax {DHL, UPS, FEDEX} (or subset) to express carrier equivalence, rather than duplicating rows per carrier
- ✅ **concerns-decomposed**: Multiple @TableTest methods exist, each addressing a distinct concern — not one monolithic table mixing base rates, surcharges, dimensional weight, and carrier equivalence
- ✅ **numeric-types-correct**: Weight and cost parameters use BigDecimal (not double or String). Dimensions use Integer or int.
- ✅ **scenario-column-present**: Each table has a scenario/description column as the leftmost column
- ✅ **scenario-names-describe-conditions**: Scenario names describe conditions ('EU express, light package', 'Oversized with fragile') — not outcomes ('8.00', 'Surcharge applied') or generic labels ('Test 1').
- ✅ **has-question-mark-column**: At least one output column name ends with '?' (e.g. 'Cost?', 'Base rate?')
- ✅ **business-language-columns**: Column names use domain language ('Zone', 'Weight (kg)', 'Dimensions', 'Options', 'Cost?') — not code identifiers ('shippingZone', 'weightKg', 'dims')
- ✅ **no-if-switch-in-method**: Test method bodies contain no if or switch statements
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **has-descriptive-title**: Each test method has a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case.
- ❌ **no-kotest-syntax**: Output contains no Kotest syntax: no forAll, row(), withData, shouldBe, context(), FunSpec, or Kotest imports.
  > Code uses 'shouldBe' in assertions: 'calculator.calculateShippingCost(...) shouldBe expected'

### ⚠️ Eval eval-27-convert-from-testng [with_skill]

**14/15** · 164842 tokens · 190565ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ✅ **options-as-map**: Package options collapsed into single map column like [fragile: true, insuredValue: 500]
- ✅ **options-type-converter**: A @TypeConverter method present that accepts Map<String, String> and returns PackageOptions
- ✅ **dimensions-as-list**: Dimensions represented as [L, W, H] list, not three separate columns
- ✅ **uses-value-sets**: At least one table uses value set syntax {DHL, UPS, FEDEX}
- ❌ **concerns-decomposed**: Multiple @TableTest methods exist, each addressing distinct concern
  > Only one method: shouldCalculateShippingCost mixes base rates, surcharges, dimensional weight, carrier equivalence
- ✅ **numeric-types-correct**: Weight and cost use BigDecimal; dimensions use Integer or int
- ✅ **scenario-column-present**: Each table has scenario/description column as leftmost column
- ✅ **scenario-names-describe-conditions**: Scenario names describe conditions, not outcomes or generic labels
- ✅ **has-question-mark-column**: At least one output column name ends with '?'
- ✅ **business-language-columns**: Column names use domain language, not code identifiers
- ✅ **no-if-switch-in-method**: Test method bodies contain no if or switch statements
- ✅ **annotation-order**: Annotations in order: @DisplayName, @Description, @TableTest
- ✅ **has-descriptive-title**: Each test method has @DisplayName or descriptive method name
- ✅ **no-testng-artifacts**: Output contains no TestNG syntax

### ⚠️ Eval eval-28-convert-from-methodsource [with_skill]

**13/15** · 133727 tokens · 202502ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ❌ **options-as-map**: Package options are collapsed into a single map column like [fragile: true, insuredValue: 500]
  > Options column uses DSL format: 'fragile', 'insured:500', 'fragile,insured:200', not map syntax [key: value]
- ✅ **options-type-converter**: A @TypeConverter method is present that accepts Map<String, String> (or similar) and returns PackageOptions
- ✅ **dimensions-as-list**: Dimensions are represented as a [L, W, H] list in the table
- ✅ **uses-value-sets**: At least one table uses value set syntax {DHL, UPS, FEDEX}
- ❌ **concerns-decomposed**: Multiple @TableTest methods exist, each addressing a distinct concern
  > Only one @TableTest method present; single table mixes base rates, surcharges, dimensional weight, carrier equivalence
- ✅ **numeric-types-correct**: Weight and cost use BigDecimal; dimensions use Integer or int
- ✅ **scenario-column-present**: Each table has a scenario/description column as the leftmost column
- ✅ **scenario-names-describe-conditions**: Scenario names describe conditions, not outcomes or generic labels
- ✅ **has-question-mark-column**: At least one output column name ends with '?'
- ✅ **business-language-columns**: Column names use domain language, not code identifiers
- ✅ **no-if-switch-in-method**: Test method bodies contain no if or switch statements
- ✅ **annotation-order**: Annotations appear in order: @DisplayName, @Description, @TableTest
- ✅ **has-descriptive-title**: Each test method has a @DisplayName or descriptive method name
- ✅ **no-methodsource-artifacts**: Output contains no @MethodSource, Stream<Arguments>, Arguments.of, or JUnit Jupiter parameterized imports

