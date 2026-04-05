# Eval Review — Iteration 25

**Model:** sonnet · **Date:** 2026-04-04 · **Evals:** 23

## Summary

**with_skill:** 221/248 (89.1%) · 2873555 tokens · 2618.6s · $4.7146

## Delta vs Iteration 24

**Regressions (15):**
- ❌ eval-4-loan-approval: `scenario-names-describe-conditions`
- ❌ eval-9-bonus-contractor-structure: `traceability-column-present`
- ❌ eval-12-subscription-loyalty-trial: `open-question-surfaced`
- ❌ eval-12-subscription-loyalty-trial: `question-mark-only-on-outputs`
- ❌ eval-14-weekly-pay: `rules-separate-from-arithmetic`
- ❌ eval-14-weekly-pay: `arithmetic-minimal-rows`
- ❌ eval-15-reis-discount: `2.9-correctness-value-set-tier-semantics`
- ❌ eval-15-reis-discount: `2.12-format-typeconverter`
- ❌ eval-15-reis-discount: `2.15-ticket-count-uses-value-sets`
- ❌ eval-15-reis-discount: `2.20-readability-one-row-per-tier`
- ❌ eval-15-reis-discount: `2.21-readability-relative-time`
- ❌ eval-16-order-splitting: `3.1a-concern-fulfillment-method`
- ❌ eval-16-order-splitting: `3.1b-concern-delivery-address`
- ❌ eval-16-order-splitting: `3.1c-concern-availability`
- ❌ eval-17-shopping-cart: `4.6-depth-cart-total-scenarios`

**Improvements (3):**
- ✅ eval-7-permission-check: `has-descriptive-title`
- ✅ eval-13-shipping-partial-applicability: `express-uses-value-sets`
- ✅ eval-15-reis-discount: `2.4-depth-rolling-window-boundary`

## Resource Comparison vs Iteration 24

| Eval | Pass Rate | Prev | Tokens | Prev | Time(s) | Prev |
|------|-----------|------|--------|------|---------|------|
| eval-1-convert-repetitive-tests | 10/10 | 10/10 | 104523 | 141481 | 23.9 | 25.2 |
| eval-2-parse-dates | 10/10 | 10/10 | 106297 | 153027 | 51.9 | 73.7 |
| eval-3-dependency-setup | 4/4 | 4/4 | 103945 | 103607 | 12.4 | 11.6 |
| eval-4-loan-approval | 9/10 | 7/7 | 66529 | 66603 | 35.6 | 32.1 |
| eval-5-order-transitions | 7/7 | 5/5 | 67835 | 66920 | 51.5 | 36.2 |
| eval-6-discount-interaction | 7/7 | 6/6 | 65334 | 65628 | 17.0 | 24.7 |
| eval-7-permission-check | 11/11 | 9/10 | 150923 | 67407 | 54.6 | 27.8 |
| eval-8-money-parse | 10/10 | 10/10 | 209823 | 152163 | 126.2 | 80.6 |
| eval-9-bonus-contractor-structure | 11/12 | 9/9 | 108357 | 151179 | 42.8 | 70.4 |
| eval-10-subscription-billing | 12/12 | 10/10 | 68115 | 68325 | 62.0 | 65.7 |
| eval-12-subscription-loyalty-trial | 8/10 | 10/10 | 68448 | 71053 | 64.2 | 92.4 |
| eval-13-shipping-partial-applicability | 7/8 | 4/6 | 70521 | 69145 | 92.3 | 67.6 |
| eval-14-weekly-pay | 13/18 | 12/15 | 361428 | 182379 | 280.8 | 291.8 |
| eval-15-reis-discount | 12/18 | 16/18 | 322521 | 602983 | 384.2 | 372.7 |
| eval-16-order-splitting | 13/17 | 16/17 | 93969 | 75092 | 475.7 | 150.4 |
| eval-17-shopping-cart | 14/16 | 15/16 | 70005 | 68993 | 86.0 | 64.6 |
| eval-18-convert-from-code | 10/11 | — | 73488 | — | 102.5 | — |
| eval-19-convert-from-parameterized | 8/9 | — | 67155 | — | 20.9 | — |
| eval-20-collections-and-quoting | 10/10 | — | 119634 | — | 141.7 | — |
| eval-21-event-registration-sbe | 7/7 | — | 66217 | — | 29.5 | — |
| eval-22-event-registration-tt | 12/12 | — | 220456 | — | 216.4 | — |
| eval-23-loan-approval-tt | 10/11 | — | 218847 | — | 174.3 | — |
| eval-24-weekly-pay-sbe | 6/8 | — | 69185 | — | 72.0 | — |

## Per-Eval Results

### ✅ Eval eval-1-convert-repetitive-tests [with_skill]

**10/10** · 104523 tokens · 23871ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ✅ **has-question-mark-column**: Table has at least one column name ending with '?' (e.g. 'Discount?' or 'Expected?')
- ✅ **has-three-data-rows**: Table has exactly 3 data rows (GOLD, SILVER, BRONZE)
- ✅ **single-assertion-in-method**: Test method body contains only one assertEquals (or equivalent assertion) — not three
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements
- ✅ **scenario-column-present**: Table has a scenario/description column as the leftmost column
- ✅ **has-descriptive-title**: Test method has either a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case
- ✅ **description-if-present-adds-information**: If @Description is present, it provides context beyond what the table rows already express
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order
- ✅ **description-uses-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +. Passes if @Description is absent.

### ✅ Eval eval-2-parse-dates [with_skill]

**10/10** · 106297 tokens · 51928ms

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

### ✅ Eval eval-3-dependency-setup [with_skill]

**4/4** · 103945 tokens · 12445ms

- ✅ **flags-junit-version**: Response explicitly flags that JUnit 5.10.0 is below the required minimum (5.11) and recommends upgrading
- ✅ **provides-correct-groupid**: Response provides the tabletest dependency with groupId 'org.tabletest' (not guessed)
- ✅ **provides-artifactid**: Response includes 'tabletest-junit' as the artifactId
- ✅ **includes-test-scope**: The suggested dependency includes <scope>test</scope>

### ⚠️ Eval eval-4-loan-approval [with_skill]

**9/10** · 66529 tokens · 35625ms

- ✅ **produces-markdown-table**: Output contains a markdown table (using | column | syntax)
- ✅ **output-column-has-question-mark**: At least one output column name ends with '?' (e.g. 'Eligible?' or 'Approved?')
- ✅ **business-language-columns**: Column names use domain/business language — no implementation terms like 'creditScore', 'isEligible', 'boolean', 'int'
- ✅ **senior-threshold-row**: Table includes at least one row specifically for the senior applicant (65+) threshold scenario
- ✅ **missing-income-marked-open**: The missing income scenario is represented — either as a row with an open/uncertain expected value (?, TBD, or blank) or as an explicit open question note
- ❌ **scenario-names-describe-conditions**: Scenario names describe conditions ('Senior applicant at threshold') not outcomes ('Approved')
  > Scenario names 'Standard approval' and 'Senior approval with lower threshold' explicitly name outcomes (approval), rather than describing only the input conditions like the recommended format 'Senior applicant at threshold'
- ✅ **threshold-values-visible**: The policy thresholds (650 for standard, 600 for senior) appear as concrete values in the Credit Score column — not just described in prose. The reader can see the boundary values in the table rows.
- ✅ **scenario-column-present**: Table has a scenario/description column as the leftmost column
- ✅ **threshold-as-column**: Policy thresholds (650/600 credit scores) appear as values in an input column — not buried in output values or only mentioned in prose. The reader sees the threshold as a concrete input the decision depends on.
- ✅ **concrete-domain-values**: Cell values use concrete domain terms (e.g. '65', '700', 'Stable') — not abstract codes or raw booleans like 'true', 'false', 'CATEGORY_A', '1'.

### ✅ Eval eval-5-order-transitions [with_skill]

**7/7** · 67835 tokens · 51509ms

- ✅ **produces-markdown-table**: Output contains at least one markdown table
- ✅ **cancellation-coverage**: Table covers cancellation rules — includes rows for states where cancellation is allowed and where it is not
- ✅ **return-window-addressed**: The 30-day return window rule is addressed — either as a threshold column, a separate table, or flagged as an open question
- ✅ **value-set-or-multiple-states**: Uses value sets {PENDING, CONFIRMED} or equivalent to express 'regardless of which starting state' for cases where a rule holds across states
- ✅ **output-column-has-question-mark**: At least one output column ends with '?'
- ✅ **scenario-column-present**: Table has a scenario/description column as the leftmost column
- ✅ **row-independence**: Each row is independently verifiable — no row references a prior row's outcome. Each row specifies its own starting state and expected result.

### ✅ Eval eval-6-discount-interaction [with_skill]

**7/7** · 65334 tokens · 17038ms

- ✅ **produces-markdown-table**: Output contains a markdown table
- ✅ **does-not-invent-resolution**: The output does NOT silently resolve the stacking/cap ambiguity — it leaves the conflicting cases as open questions, blank cells, or explicitly marks them as unresolved
- ✅ **covers-both-discounts-applying**: Table includes at least one row where both bulk and loyalty discounts apply simultaneously (exposing the interaction)
- ✅ **open-question-surfaced**: The unresolved decision (stacking vs. higher-only vs. cap) is explicitly surfaced — via an Open Questions column, a '?' cell, or a note
- ✅ **output-column-has-question-mark**: At least one output column ends with '?'
- ✅ **extreme-discount-row**: Table includes at least one row with a high combined discount (e.g. large order + top loyalty tier) that would force a decision about whether a cap applies — making the cap question concrete, not theoretical
- ✅ **scenario-column-present**: Table has a scenario/description column as the leftmost column

### ✅ Eval eval-7-permission-check [with_skill]

**11/11** · 150923 tokens · 54608ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ✅ **uses-value-sets**: Table uses value-set syntax (curly braces like {READ, WRITE} or {READ, WRITE, DELETE}) to express rules that hold across multiple actions — rather than one row per role/action combination
- ✅ **fewer-than-nine-rows**: Table has fewer than 9 data rows (i.e. does not enumerate every role/action combination individually)
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements
- ✅ **scenario-column-present**: Table has a scenario/description column as the leftmost column
- ✅ **no-duplicate-role-output**: No two rows share both the same Role value and the same boolean output — forces value set consolidation (e.g. USER can READ and USER can WRITE, both true, should be one row with {READ, WRITE}).
- ✅ **has-descriptive-title**: Test method has either a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case (e.g. 'permissionsByRoleAndAction' → 'Permissions By Role And Action'). Not a generic name like 'test1' or 'canPerform'.
- ✅ **description-if-present-adds-information**: If @Description is present, it provides context beyond what the table rows already express — such as where permissions are checked in the request lifecycle, or open questions. Does NOT merely restate the column names or summarise what the rows show. It is acceptable to omit @Description if the table already conveys all relevant context.
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **description-uses-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +. Passes if @Description is absent.
- ✅ **scenario-names-describe-conditions**: Scenario names describe conditions ('Admin performs any action', 'Guest reads') — not outcomes ('Allowed', 'Denied').

### ✅ Eval eval-8-money-parse [with_skill]

**10/10** · 209823 tokens · 126218ms

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

### ⚠️ Eval eval-9-bonus-contractor-structure [with_skill]

**11/12** · 108357 tokens · 42846ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ✅ **contractor-uses-value-set**: CONTRACTOR row uses a value set for Department (e.g. {SALES, ENGINEERING}) to express 'regardless of department' — not a dummy placeholder like 'ANY' or 'N/A', and not enumerated as separate rows.
- ✅ **four-core-rules-covered**: The four level×department combinations (SENIOR+SALES, SENIOR+ENGINEERING, JUNIOR+SALES, JUNIOR+ENGINEERING) are all present in the table(s)
- ✅ **expects-bonus-percentage**: The expected output column contains bonus percentages (e.g. 15%, 12%, 0%) — not bonus amounts. The method returns a percentage, and the table should reflect that.
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements
- ✅ **has-descriptive-title**: Test method has either a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case (e.g. 'bonusByLevelAndDepartment' → 'Bonus By Level And Department'). Not a generic name like 'test1' or 'testMethod'.
- ✅ **description-if-present-adds-information**: If @Description is present, it provides context beyond what the table rows already express — such as how bonus rates are applied (e.g. as percentage of base salary), or open questions. Does NOT merely restate the column names or summarise what the rows show. It is acceptable to omit @Description if the table already conveys all relevant context.
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **description-uses-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +. Passes if @Description is absent.
- ✅ **scenario-names-describe-conditions**: Scenario names describe conditions ('Senior in Sales', 'Contractor regardless of department') — not outcomes ('Gets 15%', 'No bonus').
- ✅ **business-language-columns**: Column names use domain/business language (e.g. 'Level', 'Department', 'Bonus %') — not implementation terms like 'employeeLevel', 'deptCode', 'result'.
- ❌ **traceability-column-present**: An intermediate result column exists (e.g. a tier, rule, or classification column) between the input columns (Level, Department) and the final output (Bonus %). The reader can trace the multi-step logic: inputs → classification → percentage.
  > The table structure is: Scenario | Level | Department | Bonus % with no intermediate column (such as tier, rule, or classification) between Department and the final output. The model confirms this is a 'pure lookup' with no intermediate steps shown.

### ✅ Eval eval-10-subscription-billing [with_skill]

**12/12** · 68115 tokens · 61958ms

- ✅ **produces-multiple-tables**: Output contains at least 2 separate markdown tables (not everything crammed into one table)
- ✅ **tables-have-distinct-concerns**: The tables cover distinct concerns — e.g. pricing/plans is separate from cancellation/refund rules
- ✅ **output-columns-have-question-marks**: At least one output column in each table ends with '?'
- ✅ **refund-exception-covered**: The 24-hour no-refund exception is included as a distinct scenario row
- ✅ **business-language-columns**: Column names use domain/business language — no implementation terms like 'boolean', 'int', 'isProrated', camelCase identifiers
- ✅ **trial-cancellation-row**: Includes a row or scenario for cancelling during the free trial — no charge was made, so no refund applies. This case should be explicit, not omitted.
- ✅ **prorated-refund-formula**: The prorated refund calculation is shown or explained — e.g. days remaining × daily rate, or an equivalent formula. Not just 'prorated refund' without showing how.
- ✅ **24h-boundary-near-boundary**: The 24-hour no-refund window is tested near the boundary (e.g. values at or close to 23h, 24h, 25h) — not just 'within 24h' vs 'well after renewal'.
- ✅ **eligibility-separate-from-amount**: Refund eligibility (yes/no, based on 24h rule and trial status) and refund amount (prorated calculation) are in separate tables — not mixed into one table. This separation makes each concern independently reviewable.
- ✅ **question-mark-only-on-outputs**: The '?' suffix is only used on output/expected columns (e.g. 'Refund Eligible?', 'Refund Amount?') — not on input columns like plan type, loyalty status, or yes/no flags. Input columns should not have '?' suffixes.
- ✅ **output-values-traceable**: Output values (e.g. refund amounts, prices) can be derived from input values by a reader — the calculation is visible or explained. Not just magic numbers with no traceable origin.
- ✅ **rules-separate-from-arithmetic**: Refund eligibility (yes/no rule based on 24h window, trial status) and refund amount (prorated calculation) are treated as separate concerns — not mixed into one table where rules and arithmetic are interleaved.

### ⚠️ Eval eval-12-subscription-loyalty-trial [with_skill]

**8/10** · 68448 tokens · 64191ms

- ✅ **produces-multiple-tables**: Output contains at least 2 separate markdown tables
- ✅ **annual-no-trial-rule-represented**: A table or note explicitly captures that the free trial is not available on annual plans (not just implied)
- ✅ **loyalty-discount-row-present**: At least one table row demonstrates the loyalty programme discount scenario for annual plan subscribers
- ✅ **surfaces-genuine-open-questions**: The response identifies at least one genuinely underspecified interaction
- ❌ **open-question-surfaced**: The annual/loyalty/trial interaction is correctly resolved with a note explaining that they are mutually exclusive (annual gets loyalty discounts but no trial, monthly gets trial but no loyalty discount) — not left as an unresolved open question
  > The response explicitly raises an open question about the resolved case: 'Is £9.99/month correct, or does loyalty membership ever apply to monthly plans? The rules only mention annual, but worth confirming.' This leaves the loyalty-monthly interaction unresolved rather than confirming mutual exclusivity.
- ✅ **loyalty-discount-annual-only**: Makes clear (via table structure, a note, or explicit rows) that the loyalty discount applies to the annual plan only — a loyalty member on monthly pays the same as anyone else.
- ✅ **loyalty-refund-ambiguity**: The loyalty annual subscriber's prorated cancellation refund is either surfaced as an open question (discounted vs list price) OR calculated from the discounted price with a visible trace
- ✅ **cancellation-refund-table**: Includes a cancellation/refund table or section (not just pricing)
- ✅ **refund-table-includes-loyalty-dimension**: The cancellation/refund table includes a loyalty member column (or equivalent) to distinguish loyalty vs non-loyalty annual refund rows
- ❌ **question-mark-only-on-outputs**: The '?' suffix is only used on output/expected columns (e.g. 'Refund?', 'Price?') — not on input columns like plan type, loyalty status, or yes/no flags.
  > Table 2 includes a column header 'Within 24h of Renewal?' which is an input condition flag (showing yes/no values describing the scenario state), not an output. The assertion explicitly forbids '?' suffixes on 'yes/no flags', yet this column violates that rule.

### ⚠️ Eval eval-13-shipping-partial-applicability [with_skill]

**7/8** · 70521 tokens · 92305ms

- ✅ **produces-markdown-table**: Output contains a markdown table
- ✅ **express-split-by-uk-free-tier**: Express shipping is split into at least two cases distinguishing the UK free tier (UK, order £50+, free) from the paid tier. The split is driven by the UK-only free threshold rule.
- ✅ **express-uses-value-sets**: Express rows use value sets to consolidate cases with the same outcome — e.g. non-UK destinations at/above threshold in one row (such as {Ireland, Other} or {Ireland, International}), or UK at/above threshold in one row ({£50, £75}). Not enumerated as separate rows per destination or per order value when the result is identical.
- ❌ **overnight-grouped**: Overnight UK and Ireland are in one row using a value set ({UK, Ireland}) — not separate rows, since both have the same cost and availability.
  > In the 'Shipping Method Availability' table, Overnight UK and Ireland are shown as separate rows: 'Overnight – UK' (UK, yes) and 'Overnight – Ireland' (Ireland, yes). Although the cost table correctly shows 'Overnight – UK or Ireland, any value' in one row, the availability table violates the principle by splitting them into separate rows despite identical outcomes.
- ✅ **standard-destination-value-set-or-blank**: Standard shipping destination uses a value set covering all options (e.g. {UK, Ireland, International} or {UK, Ireland, Other}) or is blank/absent — either approach is valid since destination is irrelevant to the cost.
- ✅ **output-column-has-question-mark**: At least one output column ends with '?'
- ✅ **concrete-domain-values**: Cell values use concrete domain terms (e.g. 'Standard', 'Express', 'UK', '£3.99') — not abstract codes or raw booleans like 'true', 'false', 'TYPE_1', '1'.
- ✅ **blank-vs-value-set-correct**: Value sets (not blanks) are used for irrelevant inputs — e.g. Standard shipping destination uses a value set like {UK, Ireland, International} because destination exists but doesn't affect the cost. Blanks are reserved for genuinely absent/N/A inputs. The distinction between 'irrelevant to this rule' (value set) and 'not applicable' (blank) is correct.

### ⚠️ Eval eval-14-weekly-pay [with_skill]

**13/18** · 361428 tokens · 280771ms

- ❌ **1.1-traceability-columns**: The pay table includes intermediate expected columns (e.g. Regular pay?, Overtime pay?, Premium pay?) so the reader can trace which rule contributes what — not just a single total Weekly pay? column.
  > The main @TableTest table has columns: "Scenario | Weekday hrs | Sunday hrs | Holiday hrs | Rate | Total pay?" — only a single "Total pay?" column for the final result, with no intermediate columns like "Regular pay?", "Overtime pay?", "Sunday pay?", or "Holiday pay?" to show traceability of individual components.
- ✅ **1.2-error-has-expected-column**: The error/rejection table has an expected column (e.g. 'Throws?' or 'Exception?') specifying the exception type per row — rather than hardcoding the exception type in @Description or the method name.
- ✅ **1.3-depth-overtime-boundary**: Overtime threshold boundary is covered: at least a value at the threshold (40) and just above (41 or similar). Not just values well below and well above.
- ✅ **1.4-depth-combined-scenario**: A combined scenario is present: a row with weekday + Sunday + holiday hours, testing the composition of all hour types.
- ❌ **1.5-depth-error-edge-cases**: Error/edge cases covered: at least negative hours (floored at zero) and negative or missing rate (rejected).
  > The error table tests only "Negative rate | {0, 40} | 0 | 0 | -1.00 | IllegalArgumentException". The main pay table contains no row with negative weekday, Sunday, or holiday hours to test the 'floored at zero' behavior. Negative hours as an edge case are not covered.
- ❌ **1.6-readability-empty-cells**: Sunday and Holiday hour columns use empty cells (not 0) when those inputs are not relevant to a scenario — making it easier to spot which rows involve weekend/holiday hours. This is a readability preference; parameter types should be Integer (not int) to support null from blank cells.
  > The table uses explicit 0 values for unused inputs, e.g. "Weekday hours under threshold | 35 | 0 | 0 | 20.00" uses '0' for Sunday and Holiday hrs instead of empty cells. Parameter types are defined as "int weekdayHours, int sundayHours, int holidayHours" (primitive int) rather than Integer, which cannot support nulls from blank cells.
- ✅ **1.7-readability-scenario-names**: Scenario names describe work patterns (e.g. 'Part-time', 'Five hours overtime', 'Full Sunday shift') — not 'Test case 1' or expected outcomes.
- ✅ **1.8-correctness-expected-values**: Expected values in intermediate and total columns are arithmetically correct for every row, consistent with the formulas in @Description.
- ✅ **1.9-correctness-value-set-semantics**: Value sets are only used where all values in the set produce the same expected result (e.g. any rate × 0 = 0). Not used where results would differ.
- ✅ **1.10-format-displayname**: @DisplayName is present on test methods, OR method names read as clear descriptive titles when converted from camelCase/snake_case.
- ✅ **1.11-format-description**: @Description is present on at least the pay calculation method and provides context beyond what the table rows already express — such as the overtime threshold (40 hours), premium multipliers (Sunday 2×, Holiday 2.5×), or other fixed assumptions not visible in the table. Does NOT merely restate the column names or summarise what the rows show.
- ✅ **1.12-format-annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **1.13-format-description-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +. Passes if @Description is absent.
- ✅ **1.14-depth-zero-rate**: A row where the hourly rate is zero, showing that pay is zero regardless of hours worked. This is a distinct edge case from negative rate (which is rejected).
- ✅ **1.15-format-clean-method**: The test method body contains no if/ternary null-handling or parsing logic. Null-to-default conversion (e.g. blank Sunday hours → 0) is handled via a @TypeConverter method or a private helper, keeping the test method limited to arrange/act/assert.
- ✅ **business-language-columns**: Column names use domain/business language (e.g. 'Weekday hours', 'Sunday hours', 'Hourly rate', 'Weekly pay?') — not implementation terms like 'weekdayHrs', 'int', 'param1'.
- ❌ **rules-separate-from-arithmetic**: Decision logic (which rate applies — regular, overtime, Sunday, holiday) and calculation (hours × rate) are in separate tables or clearly separated. The rule table tests rate selection; the calculation table tests arithmetic. Not interleaved in one undifferentiated table.
  > The main @TableTest 'calculatesWeeklyPay' contains 12 rows that interleave both rule logic (testing which pay rates apply: regular, overtime, Sunday, holiday) and arithmetic verification in a single table. No clear separation or distinct rule vs. arithmetic tables; all rows combined into one undifferentiated table testing both concerns together.
- ❌ **arithmetic-minimal-rows**: Calculation/arithmetic verification uses few rows (2-3) — not exhaustive enumeration. Once the rule is tested, arithmetic needs only a sanity check.
  > The main @TableTest table contains 12 rows: "No hours worked", "Weekday hours under threshold", "Weekday hours at threshold", "One overtime hour", "Significant overtime", "Sunday hours only", "Holiday hours only", "Sunday with weekday under threshold", "Sunday with weekday overtime", "Holiday with weekday overtime", "All pay types in one week", "Zero hourly rate" — far exceeding the recommended 2-3 rows for arithmetic sanity checks.

### ⚠️ Eval eval-15-reis-discount [with_skill]

**12/18** · 322521 tokens · 384195ms

- ✅ **2.1-decomposition-concern-separation**: Discount ladder, and traveller eligibility are in separate tables. Rolling window counting as a third table is desirable.
- ✅ **2.2-children-flat-discount**: Children's flat 20% discount is represented — either as a row in the eligibility table or a separate note. It does not follow the ladder.
- ✅ **2.3-depth-tier-boundaries**: The discount ladder has multiple tiers derived from the 'every fifth trip' rule, with boundary values or value sets showing which ticket counts produce which discount. The maximum 40% discount is represented.
- ✅ **2.4-depth-rolling-window-boundary**: The 30-day rolling window boundary is tested: a ticket at exactly 30 days is included, at 31 days is excluded.
- ✅ **2.6-readability-human-readable-values**: Eligibility uses human-readable values (yes/no or equivalent) rather than raw true/false. TypeConverter or similar mechanism mentioned.
- ❌ **2.9-correctness-value-set-tier-semantics**: Value sets in the tier table contain exactly the values that produce the same discount. No cross-tier contamination.
  > The formula 'min(floor(count / 5) * 5, 40)%' means tickets 10-14 all produce 10%, but the table shows only 'Second tier: ticket 10 | 10 | 10'. Similarly, tickets 15-19 produce 15%, but only 'Third tier: ticket 15 | 15 | 15' is shown. Value sets are incomplete and do not represent all ticket counts that produce each discount tier.
- ✅ **2.10-format-displayname**: @DisplayName is present on test methods, OR method names read as clear descriptive titles when converted from camelCase/snake_case.
- ✅ **2.11-format-description**: @Description is present on at least the class or the tier method and provides context beyond what the table rows already express — such as the discount ladder structure, eligibility rules, or application context. Does NOT merely restate the column names or summarise what the rows show.
- ❌ **2.12-format-typeconverter**: TypeConverterSources is used for at least one human-readable conversion (yes/no, or rolling window notation).
  > @TypeConverter is present only for BigDecimal parsing ('public static BigDecimal parseBigDecimal(String value)'), not for human-readable conversions like yes/no or rolling window notation (e.g., converting days to '30 days ago')
- ✅ **2.13-format-annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **2.14-format-description-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +. Passes if @Description is absent.
- ❌ **2.15-ticket-count-uses-value-sets**: The discount ladder table uses value sets for the ticket count column to group counts within the same tier (e.g. {5, 6, 7, 8, 9} or {5, 9} for min/max → 5%) — not one row per boundary value. The 'every fifth trip' rule creates tiers; the table should express tiers, not enumerate boundaries.
  > The 5% tier is split across two rows: 'First discount: ticket 5 | 5 | 5' and 'Still on first tier | {6, 7, 8, 9} | 5' instead of a single value set {5, 6, 7, 8, 9}. Tiers 10%, 15%, 20%, 25%, 30%, 35% are each shown as single values (10, 15, 20, etc.) rather than complete value sets for their ranges (10-14, 15-19, etc.)
- ✅ **2.16-no-duplicate-tier-mapping**: The tier-to-discount mapping is expressed once — not repeated across multiple tables. A price application table (if present) should test the arithmetic (base price × discount → final price) without re-enumerating which ticket counts map to which tier.
- ❌ **2.17-zone-irrelevance-visible**: The eligibility/passenger-type table includes a Zone column (or equivalent) with a value set to show that the discount applies regardless of zone — rather than silently omitting zone. The requirement explicitly says 'regardless of the zones you travel in'.
  > The discountTypeByPassengerType table has columns 'Scenario | Passenger type | Discount type?' with no Zone column. Although @Description for reisDiscountTier mentions 'The discount applies regardless of which zones are traveled', the eligibility table itself does not include a Zone column to demonstrate this.
- ✅ **2.18-adult-senior-value-set**: Adult and senior are expressed as a value set {ADULT, SENIOR} (or equivalent) in at least one row of the eligibility table, since they follow identical discount rules. Not enumerated as separate rows with the same outcome.
- ✅ **2.19-depth-all-tiers**: All 9 discount tiers are represented in the ladder table: 0% (tickets 1-4), 5% (5-9), 10% (10-14), 15% (15-19), 20% (20-24), 25% (25-29), 30% (30-34), 35% (35-39), and 40% (40+). Not just a subset of tiers.
- ❌ **2.20-readability-one-row-per-tier**: Each tier in the discount ladder is expressed as a single row with a value set for ticket counts — not split across multiple rows (e.g. separate rows for 'tier kicks in' and 'tier holds'). One tier = one row.
  > The 5% tier is split across two rows: 'First discount: ticket 5 | 5 | 5' and 'Still on first tier | {6, 7, 8, 9} | 5' instead of a single row with a combined value set. This violates the requirement that one tier = one row.
- ❌ **2.21-readability-relative-time**: The rolling window table expresses time relatively (e.g. '14 days ago', '30 days ago', or an integer 'Days ago' column) rather than using absolute dates, making the table readable without needing to know the reference date.
  > ticketsInRollingWindow table uses absolute dates: 'Ticket exactly at boundary | 2024-04-04 | [2024-03-05] | 1' and 'Ticket just outside boundary | 2024-04-04 | [2024-03-04] | 0' instead of relative notation like '30 days ago' or '31 days ago'

### ⚠️ Eval eval-16-order-splitting [with_skill]

**13/17** · 93969 tokens · 475738ms

- ❌ **3.1a-concern-fulfillment-method**: Fulfillment method splitting is represented as its own table — items with different fulfillment types (store pickup vs home delivery) cannot share a shipment.
  > The response shows a single 'Shipment Grouping' table combining fulfillment method, delivery address, and availability: 'Items sharing the same fulfillment method, destination, and availability window belong to one shipment'. Fulfillment method is not separated into its own table.
- ❌ **3.1b-concern-delivery-address**: Delivery address splitting is represented as its own table — items going to different addresses must be in separate shipments.
  > Delivery address is integrated into the 'Shipment Grouping' table alongside fulfillment method and availability (e.g., '123 Main St' and '456 Oak Ave' appear in the 'Order Contents' column), but no separate 'Delivery Address' table exists.
- ❌ **3.1c-concern-availability**: Availability splitting is represented as its own table — in-stock items ship immediately, backordered/pre-ordered items ship when available.
  > Availability is handled within the 'Shipment Grouping' table, not as a separate table. The introduction states 'Items sharing the same fulfillment method, destination, and availability window belong to one shipment' — combining all three concerns into one.
- ✅ **3.1d-concern-warehouse-allocation**: Warehouse allocation (minimising shipments) is represented as its own table — choosing which warehouses to ship from to minimise total shipment count.
- ✅ **3.1e-concern-companion-products**: Companion product grouping is represented as its own table — paired items (e.g. camera body and lens) should ship together from the same location when possible.
- ✅ **3.2-depth-fulfillment-scenarios**: Fulfillment table covers at least: all items for home delivery, all items for store pickup, and a mix of delivery and pickup items.
- ✅ **3.3-depth-delivery-address-scenarios**: Delivery address table covers at least: all items to the same address, and items to different addresses.
- ✅ **3.4-depth-availability-scenarios**: Availability table covers at least: all items in stock, a mix of in-stock and pre-order/backorder, and two backordered/pre-ordered items with different dates. Pre-order and backorder are recognised as equivalent for splitting semantics — either by comment, note, or by including both with the same rules.
- ✅ **3.5-depth-warehouse-scenarios**: Warehouse allocation table covers at least: single warehouse has full quantity, multi-warehouse split needed, fewer-warehouse split preferred over more warehouses, and cannot fulfill (insufficient total stock).
- ❌ **3.6-depth-companion-scenarios**: Companion table covers at least: single warehouse has both companions, unavoidable split (no warehouse has both but both are available), and fulfillment not possible (one companion not available anywhere).
  > Companion table includes 'Shared location available' and 'Only one location carries both' (covering single warehouse scenario), 'No shared location exists | Camera body... Chicago | Camera lens... LA' (covering unavoidable split), but lacks a scenario where one companion item has zero inventory at all locations. The 'Companion co-shipment conflicts' scenario has lens only at Philadelphia but still available—no unavailable item is shown.
- ✅ **3.7-readability-scenario-names**: Scenario names describe the business situation (e.g. 'Consolidate: two beats three', 'Everything at one warehouse') — not 'Test case 2'.
- ✅ **3.8-readability-item-property-mapping**: Trigger tables represent multi-item orders where each item has its own fulfillment type, address, and availability. The notation makes the item-to-property mapping unambiguous — not value sets like {delivery, pickup} which lose which item has which property.
- ✅ **3.9-readability-scalar-column**: Warehouse allocation table uses a scalar count column (e.g. 'Qty ordered') not item lists, when product identity doesn't matter for the logic.
- ✅ **3.10-readability-business-language**: Column names use business/domain language (e.g. 'Shipments?' not 'Result?', 'Warehouse stock' not 'inventory_map'). The tables read as a specification a product person could review.
- ✅ **3.11-readability-output-column-question-mark**: Output/expected columns end with '?' (e.g. 'Shipments?', 'Groups?') to distinguish them from input columns.
- ✅ **3.12-correctness-valid-assignments**: Inventory assignments are physically valid: items are only assigned to locations that have them. Shipment counts match the assignments shown.
- ✅ **3.13-depth-open-questions**: Multiple open questions are surfaced across the tables — ambiguities, edge cases, or product decisions that the requirements don't fully specify.

### ⚠️ Eval eval-17-shopping-cart [with_skill]

**14/16** · 70005 tokens · 86010ms

- ✅ **4.1a-concern-item-operations**: Item operations (add/remove) are represented as their own table or tables — separate from coupon and cart total logic.
- ✅ **4.1b-concern-coupon-application**: Coupon application (entering, replacing, rejecting codes) is represented as its own table — separate from item operations and cart total calculation.
- ✅ **4.1c-concern-cart-total**: Cart total calculation (how different coupon types affect the price) is represented as its own table — separate from coupon application and item operations.
- ✅ **4.1d-concern-checkout**: Checkout (stock verification) is represented as its own table — separate from the other concerns.
- ✅ **4.3-rows-independently-executable**: Each table row is independently executable — no row depends on a prior row's result. The table expresses rules, not a sequential test script.
- ✅ **4.4-depth-item-operations**: Item operations cover at least: add to empty cart, add to non-empty cart, add more of an item already in cart, remove item from cart, remove last item in cart, remove item not in cart.
- ❌ **4.5-depth-coupon-application**: Coupon application table covers validity and replacement — not coupon types. Scenarios include at least: add valid coupon (none active), add valid coupon (another active), add expired coupon (none active), add expired coupon (another active), add nonexistent code (none active), add nonexistent code (another active).
  > The table includes three separate rows for the same validity/replacement scenario (add valid coupon, none active) differentiated only by coupon type: "Percentage coupon on empty slate" (10% off cart), "Fixed amount coupon" (£5 off cart), and "Product-specific coupon" (20% off Widget). The assertion states the table covers validity and replacement "not coupon types"; these rows violate that constraint by making coupon type a varying dimension.
- ❌ **4.6-depth-cart-total-scenarios**: Cart total table covers how different coupon types affect the price. Scenarios include at least: no coupon, percentage off whole cart, fixed amount off, fixed amount exceeding cart total (floors at zero), product-specific discount (product in cart), product-specific discount (product not in cart).
  > The table includes scenarios for: "No coupon", "Percentage off whole cart", "Fixed amount off", "Discount larger than order — floors at zero", and two product-specific scenarios (rows 4 and 6). However, both product-specific rows have the Widget in the cart (row 4: "Widget ×2, Gadget ×1" with "20% off Widget"; row 6: "Widget ×1, Gadget ×1" with "50% off Widget"). The assertion requires "product-specific discount (product not in cart)" as a scenario, which is missing (e.g., a coupon for Widget when the cart contains only Gadget).
- ✅ **4.7-depth-checkout-scenarios**: Checkout table covers at least: empty cart, all items in stock, and not all items in stock (insufficient inventory).
- ✅ **4.8-readability-business-language**: Column names use business/user-perspective language (e.g. 'Message?' not 'Result?', 'Coupon code' not 'input'). The tables read as a specification a product person could review.
- ✅ **4.9-readability-test-data-visible**: Test data that affects the outcome — product prices, coupon rules (type, amount, expiry) — is visible in the table via columns, notes, or explicit setup, not silently hardcoded. The reader can trace how inputs lead to outputs.
- ✅ **4.10-depth-open-questions**: Multiple open questions are surfaced — ambiguities or product decisions the requirements don't specify (e.g. what happens to a product-specific coupon when that product is removed, or how percentage and product-specific discounts interact).
- ✅ **4.11-correctness-cart-totals**: Cart totals are arithmetically correct: sum(quantity × price) − coupon = total, for every row in the total calculation table.
- ✅ **4.12-coupon-expiry-column**: Coupon application table uses a column to signal expiry status — either an 'Expired?' yes/no column, a 'Status' column, or equivalent. Expiry is visible per row, not just implied by the scenario name.
- ✅ **4.14-coupon-before-after-columns**: Coupon application table uses before/after columns (e.g. 'Active coupon' / 'Active coupon after?') to specify the state change when entering, replacing, or entering invalid coupon codes.
- ✅ **4.15-readability-scenario-names**: Scenario names describe the business situation (e.g. 'Add more of same product', 'Replace active coupon') — not 'Test case 1' or generic labels.

### ⚠️ Eval eval-18-convert-from-code [with_skill]

**10/11** · 73488 tokens · 102506ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ✅ **black-box-columns**: Table columns represent the method's public inputs (applicant type, age, claim count) and observable outputs (decision, premium) — not internal state like hasActivePolicy, internalRiskScore, or calculateRiskScore.
- ✅ **observable-io-only**: No column is named after private fields or internal variables (hasActivePolicy, internalRiskScore, riskScore). The test treats the method as a black box.
- ✅ **scenario-column-present**: Table has a scenario/description column as the leftmost column
- ✅ **business-language-columns**: Column names use domain/business language (e.g. 'Applicant type', 'Age', 'Claims', 'Decision?', 'Premium?') — not code identifiers like 'applicantType', 'claimCount', 'String'.
- ❌ **scenario-names-describe-conditions**: Scenario names describe conditions ('Renewal with no claims', 'Senior applicant') — not outcomes ('Auto approved', 'Rejected').
  > Scenario row 'High claim count, rejected' includes 'rejected' which is an outcome word, violating the requirement that scenario names describe conditions only, not outcomes
- ✅ **has-question-mark-column**: Table has at least one column name ending with '?' (e.g. 'Decision?' or 'Premium?')
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **has-descriptive-title**: Test method has either a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case.
- ✅ **description-uses-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +. Passes if @Description is absent.

### ⚠️ Eval eval-19-convert-from-parameterized [with_skill]

**8/9** · 67155 tokens · 20902ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ✅ **scenario-column-present**: Table has a scenario/description column as the leftmost column
- ✅ **has-question-mark-column**: At least one output column name ends with '?'
- ❌ **value-sets-used**: Value sets are used to consolidate rows that share the same rule and outcome
  > All rows are listed individually (Standard address, Multi-part TLD, etc. as separate rows; No @ symbol, Missing local part, etc. as separate rows), with no consolidation or grouping of rows that share the same outcome (e.g., the three valid cases are separate rows, the five invalid cases are separate rows)
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements
- ✅ **scenario-names-describe-conditions**: Scenario names describe conditions — not outcomes or 'Test case 1'
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest
- ✅ **has-descriptive-title**: Test method has either a @DisplayName annotation or a method name that reads as a clear, descriptive title
- ✅ **description-uses-textblock**: If @Description is present and longer than a single line, it uses a text block, not concatenation. Passes if @Description is absent.

### ✅ Eval eval-20-collections-and-quoting [with_skill]

**10/10** · 119634 tokens · 141702ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ✅ **list-syntax-correct**: List values in the table use bracket syntax like [tech:java, biz:sales, dev:ci] — not comma-separated strings without brackets.
- ✅ **empty-list-explicit**: Empty list input uses [] (not a blank cell, which represents null). The distinction between empty list and null is preserved.
- ✅ **special-chars-quoted**: Values containing pipes (|) or brackets are properly quoted or escaped so they don't conflict with table syntax.
- ✅ **scenario-column-present**: Table has a scenario/description column as the leftmost column
- ✅ **has-question-mark-column**: At least one output column name ends with '?' (e.g. 'Filtered tags?' or 'Result?')
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **has-descriptive-title**: Test method has either a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case.
- ✅ **description-uses-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +. Passes if @Description is absent.

### ✅ Eval eval-21-event-registration-sbe [with_skill]

**7/7** · 66217 tokens · 29508ms

- ✅ **produces-markdown-table**: Output contains at least one markdown table (using | column | syntax)
- ✅ **validation-rules-covered**: Email validation and name-required scenarios are present — at least one row for invalid email and one for missing name.
- ✅ **blank-for-absent-optional**: Dietary requirements and/or accessibility needs use blank cells when the attendee has not provided them (genuinely absent) — not 'N/A' or 'none'.
- ✅ **output-column-has-question-mark**: At least one output column name ends with '?' (e.g. 'Accepted?', 'Price?')
- ✅ **scenario-names-describe-conditions**: Scenario names describe conditions ('Missing email', 'Early-bird single attendee') — not outcomes ('Rejected', 'Discounted').
- ✅ **open-question-surfaced**: The discount stacking ambiguity (early-bird + group) is surfaced as an open question — not silently resolved.
- ✅ **scenario-column-present**: Table has a scenario/description column as the leftmost column

### ✅ Eval eval-22-event-registration-tt [with_skill]

**12/12** · 220456 tokens · 216385ms

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

### ⚠️ Eval eval-23-loan-approval-tt [with_skill]

**10/11** · 218847 tokens · 174263ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ✅ **threshold-as-column**: Policy thresholds (650/600 credit scores) appear as values in an input column — not buried in output values or only mentioned in prose.
- ❌ **concrete-domain-values**: Cell values use concrete domain terms (e.g. '70', '650', 'APPROVED') — not abstract codes or raw booleans like 'true', '1'.
  > The 'Has Stable Income' column uses raw boolean values 'true' and 'false' (e.g., 'Standard approval | 30 | 700 | true | APPROVED'), which the assertion explicitly prohibits.
- ✅ **scenario-column-present**: Table has a scenario/description column as the leftmost column
- ✅ **scenario-names-describe-conditions**: Scenario names describe conditions ('Senior at lower threshold', 'Missing income') — not outcomes ('Approved', 'Rejected').
- ✅ **business-language-columns**: Column names use domain/business language (e.g. 'Age', 'Credit score', 'Stable income', 'Decision?') — not code identifiers like 'customerAge', 'boolean'.
- ✅ **has-question-mark-column**: At least one output column name ends with '?' (e.g. 'Decision?' or 'Status?')
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **has-descriptive-title**: Test method has either a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case.
- ✅ **description-uses-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +. Passes if @Description is absent.

### ⚠️ Eval eval-24-weekly-pay-sbe [with_skill]

**6/8** · 69185 tokens · 72028ms

- ✅ **produces-markdown-table**: Output contains at least one markdown table (using | column | syntax)
- ❌ **rules-separate-from-arithmetic**: Decision logic (which rate applies — regular, overtime, Sunday, holiday) and calculation (hours × rate = pay) are in separate tables or clearly separated. Not interleaved in one undifferentiated table.
  > The single '## Weekly Pay Calculation' table contains both the rules (Overtime Threshold, different hour types) and the arithmetic results (Total Pay) all in one undifferentiated structure. No separate rules table showing decision logic is provided.
- ❌ **arithmetic-minimal-rows**: Calculation/arithmetic verification uses few rows (2-3) — not exhaustive enumeration. Once the rule is tested, arithmetic needs only a sanity check.
  > The '## Weekly Pay Calculation' table contains 13 rows of scenarios, which is exhaustive enumeration rather than minimal verification. Rows include: 'Regular weekday hours under limit', 'Weekday hours exactly at threshold', 'One hour over threshold', 'Substantial overtime', 'Sunday hours only', 'Holiday hours only', 'Sunday and holiday hours together', 'Sunday/holiday do not count toward overtime threshold', 'Weekday regular + Sunday', 'Weekday overtime + Sunday', 'All three hour types with overtime', 'Zero hours', and 'Zero rate'.
- ✅ **output-column-has-question-mark**: At least one output column name ends with '?' (e.g. 'Pay?', 'Rate?')
- ✅ **scenario-names-describe-conditions**: Scenario names describe work patterns or conditions ('Part-time weekday', 'Full week with overtime', 'Sunday shift') — not outcomes ('Gets overtime', 'Double pay').
- ✅ **business-language-columns**: Column names use domain/business language (e.g. 'Weekday hours', 'Sunday hours', 'Hourly rate', 'Weekly pay?') — not code identifiers.
- ✅ **scenario-column-present**: Table has a scenario/description column as the leftmost column
- ✅ **overtime-boundary-covered**: The overtime threshold boundary (40 hours) is covered with at least a value at the threshold and one above.

