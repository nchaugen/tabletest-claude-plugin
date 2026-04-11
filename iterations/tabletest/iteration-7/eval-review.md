# Eval Review — Iteration 7

**Model:** sonnet · **Date:** 2026-03-27 · **Evals:** 16

## Summary

**with_skill:** 114/148 (77.0%) · 1077075 tokens · 1202.3s · $2.0553

## Delta vs Iteration 6

**Regressions (12):**
- ❌ eval-2-parse-dates: `annotation-order`
- ❌ eval-7-permission-check: `no-duplicate-role-output`
- ❌ eval-7-permission-check: `description-uses-textblock`
- ❌ eval-10-subscription-billing: `eligibility-separate-from-amount`
- ❌ eval-10-subscription-billing: `question-mark-only-on-outputs`
- ❌ eval-12-subscription-loyalty-trial: `question-mark-only-on-outputs`
- ❌ eval-13-shipping-partial-applicability: `overnight-grouped`
- ❌ eval-14-weekly-pay: `1.3-depth-overtime-boundary`
- ❌ eval-14-weekly-pay: `1.8-correctness-expected-values`
- ❌ eval-14-weekly-pay: `1.11-format-description`
- ❌ eval-15-reis-discount: `2.15-ticket-count-uses-value-sets`
- ❌ eval-15-reis-discount: `2.14-format-description-textblock`

**Improvements (6):**
- ✅ eval-1-convert-repetitive-tests: `description-uses-textblock`
- ✅ eval-2-parse-dates: `description-uses-textblock`
- ✅ eval-8-money-parse: `annotation-order`
- ✅ eval-15-reis-discount: `2.13-format-annotation-order`
- ✅ eval-16-order-splitting: `3.4-depth-companion-ambiguity-surfaced`
- ✅ eval-16-order-splitting: `3.5-depth-companion-edge-cases`

## Per-Eval Results

### ⚠️ Eval eval-1-convert-repetitive-tests [with_skill]

**9/10** · 41999 tokens · 16782ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ✅ **has-question-mark-column**: Table has at least one column name ending with '?' (e.g. 'Discount?' or 'Expected?')
- ✅ **has-three-data-rows**: Table has exactly 3 data rows (GOLD, SILVER, BRONZE)
- ✅ **single-assertion-in-method**: Test method body contains only one assertEquals (or equivalent assertion) — not three
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements
- ✅ **scenario-column-present**: Table has a scenario/description column as the leftmost column
- ✅ **has-descriptive-title**: Test method has either a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case (e.g. 'discountByCustomerTier' → 'Discount By Customer Tier'). Not a generic name like 'test1' or 'testMethod'.
- ❌ **description-adds-information**: @Description is present and provides context beyond what the table rows already express — such as fixed values shared by all rows, where/when the rule applies, or open questions. Does NOT merely restate the column names or summarise what the rows show.
  > The response contains no @Description annotation anywhere. The only annotation is @TableTest. Contextual information (e.g., that the order value 100 is constant) is provided only in prose after the code block, not in a @Description annotation.
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **description-uses-textblock**: @Description text uses a text block (triple-quoted string """) when the text is long, not string concatenation with +.

### ⚠️ Eval eval-2-parse-dates [with_skill]

**7/9** · 74800 tokens · 68897ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ✅ **null-as-blank-cell**: Null input is represented as a blank cell (not the string 'null') in the table
- ✅ **exception-handled-cleanly**: The empty string / exception case is handled — either via a Throws? column, assertThrows inside the method, or a separate @TableTest — rather than being silently omitted
- ✅ **localdate-result-column**: There is a result column typed as LocalDate (or using a string representation that maps to LocalDate)
- ✅ **type-conversion-addressed**: The response addresses type conversion for non-standard date formats (slash format, short year) — mentions @TypeConverter or a converter method
- ✅ **has-descriptive-title**: Test method has either a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case (e.g. 'dateParsing' → 'Date Parsing'). Not a generic name like 'test1' or 'testMethod'.
- ❌ **description-adds-information**: @Description is present and provides context beyond what the table rows already express — such as supported date formats, where/when the rule applies, or open questions. Does NOT merely restate the column names or summarise what the rows show.
  > @Description("Parses multiple date string formats; null input returns null") — this merely restates what the table rows already show (the rows demonstrate multiple formats and the null row shows null returns null). No additional context, open questions, or behavioural intent is added.
- ❌ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
  > @TableTest(...) appears first, then @Description — the order is reversed from the required @Description before @TableTest order.
- ✅ **description-uses-textblock**: @Description text uses a text block (triple-quoted string """) when the text is long, not string concatenation with +.

### ✅ Eval eval-3-dependency-setup [with_skill]

**4/4** · 66174 tokens · 12840ms

- ✅ **flags-junit-version**: Response explicitly flags that JUnit 5.10.0 is below the required minimum (5.11) and recommends upgrading
- ✅ **provides-correct-groupid**: Response provides the tabletest dependency with groupId 'org.tabletest' (not guessed)
- ✅ **provides-artifactid**: Response includes 'tabletest-junit' as the artifactId
- ✅ **includes-test-scope**: The suggested dependency includes <scope>test</scope>

### ✅ Eval eval-4-loan-approval [with_skill]

**7/7** · 42320 tokens · 38675ms

- ✅ **produces-markdown-table**: Output contains a markdown table (using | column | syntax)
- ✅ **output-column-has-question-mark**: At least one output column name ends with '?' (e.g. 'Eligible?' or 'Approved?')
- ✅ **business-language-columns**: Column names use domain/business language — no implementation terms like 'creditScore', 'isEligible', 'boolean', 'int'
- ✅ **senior-threshold-row**: Table includes at least one row specifically for the senior applicant (65+) threshold scenario
- ✅ **missing-income-marked-open**: The missing income scenario is represented — either as a row with an open/uncertain expected value (?, TBD, or blank) or as an explicit open question note
- ✅ **scenario-names-describe-conditions**: Scenario names describe conditions ('Senior applicant at threshold') not outcomes ('Approved')
- ✅ **threshold-values-visible**: The policy thresholds (650 for standard, 600 for senior) appear as concrete values in the Credit Score column — not just described in prose. The reader can see the boundary values in the table rows.

### ✅ Eval eval-5-order-transitions [with_skill]

**5/5** · 41799 tokens · 28221ms

- ✅ **produces-markdown-table**: Output contains at least one markdown table
- ✅ **cancellation-coverage**: Table covers cancellation rules — includes rows for states where cancellation is allowed and where it is not
- ✅ **return-window-addressed**: The 30-day return window rule is addressed — either as a threshold column, a separate table, or flagged as an open question
- ✅ **value-set-or-multiple-states**: Uses value sets {PENDING, CONFIRMED} or equivalent to express 'regardless of which starting state' for cases where a rule holds across states
- ✅ **output-column-has-question-mark**: At least one output column ends with '?'

### ✅ Eval eval-6-discount-interaction [with_skill]

**6/6** · 41389 tokens · 23919ms

- ✅ **produces-markdown-table**: Output contains a markdown table
- ✅ **does-not-invent-resolution**: The output does NOT silently resolve the stacking/cap ambiguity — it leaves the conflicting cases as open questions, blank cells, or explicitly marks them as unresolved
- ✅ **covers-both-discounts-applying**: Table includes at least one row where both bulk and loyalty discounts apply simultaneously (exposing the interaction)
- ✅ **open-question-surfaced**: The unresolved decision (stacking vs. higher-only vs. cap) is explicitly surfaced — via an Open Questions column, a '?' cell, or a note
- ✅ **output-column-has-question-mark**: At least one output column ends with '?'
- ✅ **extreme-discount-row**: Table includes at least one row with a high combined discount (e.g. large order + top loyalty tier) that would force a decision about whether a cap applies — making the cap question concrete, not theoretical.

### ⚠️ Eval eval-7-permission-check [with_skill]

**7/10** · 41801 tokens · 14645ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ✅ **uses-value-sets**: Table uses value-set syntax (curly braces like {READ, WRITE} or {READ, WRITE, DELETE}) to express rules that hold across multiple actions — rather than one row per role/action combination
- ✅ **fewer-than-nine-rows**: Table has fewer than 9 data rows (i.e. does not enumerate every role/action combination individually)
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements
- ✅ **scenario-column-present**: Table has a scenario/description column as the leftmost column
- ❌ **no-duplicate-role-output**: No two rows share both the same Role value and the same boolean output — forces value set consolidation (e.g. USER can READ and USER can WRITE, both true, should be one row with {READ, WRITE}).
  > USER rows: 'USER can read' (USER, READ, true) and 'USER can write' (USER, WRITE, true) — both share Role=USER and output=true, but are listed as separate rows instead of being consolidated into one row with {READ, WRITE}
- ✅ **has-descriptive-title**: Test method has either a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case
- ❌ **description-adds-information**: @Description is present and provides context beyond what the table rows already express — such as where permissions are checked in the request lifecycle, or open questions. Does NOT merely restate the column names or summarise what the rows show.
  > No @Description annotation is present in the code output at all
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ❌ **description-uses-textblock**: @Description text uses a text block (triple-quoted string """) when the text is long, not string concatenation with +.
  > No @Description annotation is present in the code output, so this assertion cannot be satisfied

### ⚠️ Eval eval-8-money-parse [with_skill]

**7/10** · 70297 tokens · 66088ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ❌ **null-as-blank-cell**: Null input is represented as a blank cell in the valid inputs @TableTest table (not the string 'null', and not as a separate @Test method). The blank cell maps to a null parameter.
  > @Test
void returnsNullForNullInput() {
    assertNull(parse(null));
} — null is handled as a separate @Test method, not as a blank cell in the @TableTest table
- ❌ **exception-has-expected-column**: The exception/invalid-input table has an expected column (e.g. 'Throws?' or 'Exception?') specifying the exception type per row — rather than hardcoding the exception type in @Description or the method name.
  > The rejection table only has columns "Scenario | Input" with no exception column; the exception type is hardcoded in the method body: "assertThrows(IllegalArgumentException.class, () -> parse(input))"
- ✅ **exception-cases-handled**: The exception cases (empty string, letters-only, negative) are handled — via a Throws? column, assertThrows in the method body, or a separate @TableTest — not silently omitted
- ✅ **result-column-with-question-mark**: Valid inputs have a result column name ending with '?' (e.g. 'Money?' or 'Result?')
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements
- ✅ **has-descriptive-title**: Test method has either a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case (e.g. 'moneyParsing' → 'Money Parsing'). Not a generic name like 'test1' or 'testMethod'.
- ❌ **description-adds-information**: @Description is present and provides context beyond what the table rows already express — such as the expected currency format, or open questions. Does NOT merely restate the column names or summarise what the rows show.
  > @Description("Valid decimal strings are parsed into Money objects with the correct amount") and @Description("Malformed or negative inputs are rejected immediately") — both descriptions merely summarize the test behavior visible from the rows and method names; neither mentions currency format, units, open questions, or other context not already evident from the table.
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **description-uses-textblock**: @Description text uses a text block (triple-quoted string """) when the text is long, not string concatenation with +.

### ⚠️ Eval eval-9-bonus-contractor-structure [with_skill]

**7/9** · 42324 tokens · 23067ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ✅ **contractor-uses-value-set**: CONTRACTOR row uses a value set for Department (e.g. {SALES, ENGINEERING}) to express 'regardless of department' — not a dummy placeholder like 'ANY' or 'N/A', and not enumerated as separate rows.
- ✅ **four-core-rules-covered**: The four level×department combinations (SENIOR+SALES, SENIOR+ENGINEERING, JUNIOR+SALES, JUNIOR+ENGINEERING) are all present in the table(s)
- ✅ **expects-bonus-percentage**: The expected output column contains bonus percentages (e.g. 15%, 12%, 0%) — not bonus amounts. The method returns a percentage, and the table should reflect that.
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements
- ✅ **has-descriptive-title**: Test method has either a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case (e.g. 'bonusByLevelAndDepartment' → 'Bonus By Level And Department'). Not a generic name like 'test1' or 'testMethod'.
- ❌ **description-adds-information**: @Description is present and provides context beyond what the table rows already express — such as how bonus rates are applied (e.g. as percentage of base salary), or open questions. Does NOT merely restate the column names or summarise what the rows show.
  > @Description("Bonus percentage is determined by employee level and department. " + "Contractors always receive 0% regardless of department.") — this merely restates what the table rows already show (level/department determine bonus, contractor=0%), adding no new context such as how percentages are applied or open questions.
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ❌ **description-uses-textblock**: @Description text uses a text block (triple-quoted string """) when the text is long, not string concatenation with +.
  > @Description("Bonus percentage is determined by employee level and department. " + "Contractors always receive 0% regardless of department.") — uses string concatenation with + instead of a text block.

### ⚠️ Eval eval-10-subscription-billing [with_skill]

**8/10** · 48314 tokens · 130556ms

- ✅ **produces-multiple-tables**: Output contains at least 2 separate markdown tables (not everything crammed into one table)
- ✅ **tables-have-distinct-concerns**: The tables cover distinct concerns — e.g. pricing/plans is separate from cancellation/refund rules
- ✅ **output-columns-have-question-marks**: At least one output column in each table ends with '?'
- ✅ **refund-exception-covered**: The 24-hour no-refund exception is included as a distinct scenario row
- ✅ **business-language-columns**: Column names use domain/business language — no implementation terms like 'boolean', 'int', 'isProrated', camelCase identifiers
- ✅ **trial-cancellation-row**: Includes a row or scenario for cancelling during the free trial — no charge was made, so no refund applies. This case should be explicit, not omitted.
- ✅ **prorated-refund-formula**: The prorated refund calculation is shown or explained — e.g. days remaining × daily rate, or an equivalent formula. Not just 'prorated refund' without showing how.
- ✅ **24h-boundary-near-boundary**: The 24-hour no-refund window is tested near the boundary (e.g. values at or close to 23h, 24h, 25h) — not just 'within 24h' vs 'well after renewal'.
- ❌ **eligibility-separate-from-amount**: Refund eligibility (yes/no, based on 24h rule and trial status) and refund amount (prorated calculation) are in separate tables — not mixed into one table. This separation makes each concern independently reviewable.
  > Table 1 ('Subscription Cancellation Refund') contains both 'Refund given?' (eligibility) and 'Refund amount?' (amount) as columns in the same table — they are not in separate tables.
- ❌ **question-mark-only-on-outputs**: The '?' suffix is only used on output/expected columns (e.g. 'Refund Eligible?', 'Refund Amount?') — not on input columns like plan type, loyalty status, or yes/no flags. Input columns should not have '?' suffixes.
  > Table 1 has the column 'Within 24h of renewal?' which is an input condition used to determine refund eligibility, yet it carries the '?' suffix. Similarly, Table 2's 'Charged?' column is an input/factual state rather than an expected output column.

### ⚠️ Eval eval-12-subscription-loyalty-trial [with_skill]

**9/10** · 45974 tokens · 92302ms

- ✅ **produces-multiple-tables**: Output contains at least 2 separate markdown tables
- ✅ **annual-no-trial-rule-represented**: A table or note explicitly captures that the free trial is not available on annual plans (not just implied)
- ✅ **loyalty-discount-row-present**: At least one table row demonstrates the loyalty programme discount scenario for annual plan subscribers
- ✅ **surfaces-genuine-open-questions**: The response identifies at least one genuinely underspecified interaction — such as: what happens when a trial subscriber upgrades to annual mid-trial, when a subscriber joins the loyalty programme mid-billing-cycle, or whether a cancellation refund is based on the discounted or full price. These are NOT stated in the rules and require a product decision.
- ✅ **open-question-surfaced**: The annual/loyalty/trial interaction is explicitly surfaced as an open question — via '?', 'TBD', blank expected value, or a written open question — rather than silently resolved
- ✅ **loyalty-discount-annual-only**: Makes clear (via table structure, a note, or explicit rows) that the loyalty discount applies to the annual plan only — a loyalty member on monthly pays the same as anyone else.
- ✅ **loyalty-refund-ambiguity**: Surfaces whether a loyalty annual subscriber's prorated cancellation refund is calculated from the discounted price (£86.29) or the list price (£95.88) — as an open question, '?', or note. Not silently resolved.
- ✅ **cancellation-refund-table**: Includes a cancellation/refund table or section (not just pricing) — the loyalty discount creates a refund calculation ambiguity that should be shown with concrete rows.
- ✅ **refund-table-includes-loyalty-dimension**: The cancellation/refund table includes a loyalty member column (or equivalent) to distinguish loyalty vs non-loyalty annual refund rows — not just plan type alone.
- ❌ **question-mark-only-on-outputs**: The '?' suffix is only used on output/expected columns (e.g. 'Refund?', 'Price?') — not on input columns like plan type, loyalty status, or yes/no flags. Input columns should not have '?' suffixes.
  > Table 1 header: '| Scenario | Plan | Loyalty Member? | Trial? | First Charge |' — both 'Loyalty Member?' and 'Trial?' are input columns (loyalty status and yes/no flags) that carry '?' suffixes, violating the assertion. Table 2 header also uses 'Loyalty Member?' as an input column with a '?' suffix.

### ⚠️ Eval eval-13-shipping-partial-applicability [with_skill]

**5/6** · 43025 tokens · 45812ms

- ✅ **produces-markdown-table**: Output contains a markdown table
- ✅ **express-split-by-uk-free-tier**: Express shipping is split into at least two cases distinguishing the UK free tier (UK, order £50+, free) from the paid tier. The split is driven by the UK-only free threshold rule.
- ✅ **express-uses-value-sets**: Express rows use value sets to consolidate cases with the same outcome — e.g. non-UK destinations at/above threshold in one row (such as {Ireland, Other} or {Ireland, International}), or UK at/above threshold in one row ({£50, £75}). Not enumerated as separate rows per destination or per order value when the result is identical. Any reasonable label for non-UK/Ireland destinations is acceptable (International, Other, Rest of World, etc.).
- ❌ **overnight-grouped**: Overnight UK and Ireland are in one row using a value set ({UK, Ireland}) — not separate rows, since both have the same cost and availability.
  > | Overnight — UK | Overnight | UK | {£25, £75} | £14.99 | and | Overnight — Ireland | Overnight | Ireland | {£25, £75} | £14.99 | — these are two separate rows rather than a single row with {UK, Ireland}.
- ✅ **standard-destination-value-set-or-blank**: Standard shipping destination uses a value set covering all options (e.g. {UK, Ireland, International} or {UK, Ireland, Other}) or is blank/absent — either approach is valid since destination is irrelevant to the cost. Any reasonable label for non-UK/Ireland destinations is acceptable.
- ✅ **output-column-has-question-mark**: At least one output column ends with '?'

### ⚠️ Eval eval-14-weekly-pay [with_skill]

**5/13** · 291505 tokens · 194953ms

- ❌ **1.1-traceability-columns**: The pay table includes intermediate expected columns (e.g. Regular pay?, Overtime pay?, Premium pay?) so the reader can trace which rule contributes what — not just a single total Weekly pay? column.
  > The table has only one expected column: 'Weekly pay?' — no intermediate columns like 'Regular pay?' or 'Overtime pay?' are present. Table header: 'Scenario | Weekday hrs | Sunday hrs | Holiday hrs | Rate | Weekly pay?'
- ❌ **1.2-error-has-expected-column**: The error/rejection table has an expected column (e.g. 'Throws?' or 'Exception?') specifying the exception type per row — rather than hardcoding the exception type in @Description or the method name.
  > The negative rate test is a plain @Test method with assertThrows hardcoded: 'assertThrows(IllegalArgumentException.class, () -> calculator.calculateWeeklyPay(40, 0, 0, new BigDecimal("-10.00")))' — there is no @TableTest with an expected exception column at all.
- ❌ **1.3-depth-overtime-boundary**: Overtime threshold boundary is covered: at least a value at the threshold (40) and just above (41 or similar). Not just values well below and well above.
  > The table has 'Standard week, exactly 40 | 40' at the threshold and 'Overtime: 5 extra hours | 45' as the first overtime row. 45 is 5 hours above the threshold, not 'just above (41 or similar)' as the assertion requires for boundary coverage.
- ✅ **1.4-depth-combined-scenario**: A combined scenario is present: a row with weekday + Sunday + holiday hours, testing the composition of all hour types.
- ❌ **1.5-depth-error-edge-cases**: Error/edge cases covered: at least negative hours (floored at zero) and negative or missing rate (rejected).
  > Only negative rate is tested via 'shouldRejectNegativeHourlyRate()'. Negative hours (floored at zero) appear nowhere in the table or in any separate test method.
- ❌ **1.6-readability-empty-cells**: Sunday and Holiday hour columns use empty cells (not 0) when those inputs are not relevant to a scenario — making it easier to spot which rows involve weekend/holiday hours. This is a readability preference; parameter types should be Integer (not int) to support null from blank cells.
  > Rows use explicit 0: e.g. 'Standard week, exactly 40 | 40 | 0 | 0 | 20.00 | 800.00'. Parameter types are declared as 'int weekdayHours, int sundayHours, int holidayHours' — not Integer.
- ✅ **1.7-readability-scenario-names**: Scenario names describe work patterns (e.g. 'Part-time', 'Five hours overtime', 'Full Sunday shift') — not 'Test case 1' or expected outcomes.
- ❌ **1.8-correctness-expected-values**: Expected values in intermediate and total columns are arithmetically correct for every row, consistent with the formulas in @Description.
  > No @Description annotation is present anywhere in the response, so there are no stated formulas to verify consistency against. The assertion requires values to be consistent with formulas in @Description, which is absent.
- ✅ **1.9-correctness-value-set-semantics**: Value sets are only used where all values in the set produce the same expected result (e.g. any rate × 0 = 0). Not used where results would differ.
- ✅ **1.10-format-displayname**: @DisplayName is present on test methods, OR method names read as clear descriptive titles when converted from camelCase/snake_case.
- ❌ **1.11-format-description**: @Description is present on at least the payable hours method and provides context beyond what the table rows already express — such as the conversion rules, fixed values, or application context. Does NOT merely restate the column names or summarise what the rows show.
  > No @Description annotation appears anywhere in the response — neither on shouldCalculateWeeklyPay nor on any other method.
- ✅ **1.12-format-annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ❌ **1.13-format-description-textblock**: @Description text uses a text block (triple-quoted string """) when the text is long, not string concatenation with +.
  > No @Description annotation is present anywhere in the response, so no text block is used for it.

### ⚠️ Eval eval-15-reis-discount [with_skill]

**7/13** · 87266 tokens · 161452ms

- ❌ **2.1-decomposition-concern-separation**: Discount ladder, and traveller eligibility are in separate tables. Rolling window counting as a third table is desirable.
  > The response produces two tables: one for children's flat discount and one for the adult/senior tier ladder. There is no eligibility table separating who qualifies for what (children vs adults/seniors vs seniors). Eligibility is implicitly embedded in the table structure by having separate tables per type, not as a dedicated concern. No rolling window table is present.
- ✅ **2.2-children-flat-discount**: Children's flat 20% discount is represented — either as a row in the eligibility table or a separate note. It does not follow the ladder.
- ✅ **2.3-depth-tier-boundaries**: The discount ladder has multiple tiers derived from the 'every fifth trip' rule, with boundary values or value sets showing which ticket counts produce which discount. The maximum 40% discount is represented.
- ❌ **2.4-depth-rolling-window-boundary**: The 30-day rolling window boundary is tested: a ticket at exactly 30 days is included, at 31 days is excluded.
  > The response does not include any table or test rows for the 30-day rolling window boundary. The spec ambiguity note only discusses whether the current ticket is included in the count, not the 30-day cutoff boundary.
- ❌ **2.6-readability-human-readable-values**: Eligibility uses human-readable values (yes/no or equivalent) rather than raw true/false. TypeConverter or similar mechanism mentioned.
  > No eligibility table with yes/no values is present. The tables use numeric discount percentages and CustomerType enum values. No TypeConverter or human-readable eligibility conversion is mentioned.
- ✅ **2.9-correctness-value-set-tier-semantics**: Value sets in the tier table contain exactly the values that produce the same discount. No cross-tier contamination.
- ❌ **2.15-ticket-count-uses-value-sets**: The discount ladder table uses value sets for the ticket count column to group counts within the same tier (e.g. {5, 6, 7, 8, 9} or {5, 9} for min/max → 5%) — not one row per boundary value. The 'every fifth trip' rule creates tiers; the table should express tiers, not enumerate boundaries.
  > The adult/senior table uses individual boundary rows (4, 5, 9, 10, 15, 20, 39, 40, 50) rather than value sets grouping each tier. For example, tier 1 (5%) is represented by two separate rows `| 5 | 5` and `| 9 | 5` instead of `| {5, 9} | 5`.
- ✅ **2.16-no-duplicate-tier-mapping**: The tier-to-discount mapping is expressed once — not repeated across multiple tables. A price application table (if present) should test the arithmetic (base price × discount → final price) without re-enumerating which ticket counts map to which tier.
- ✅ **2.10-format-displayname**: @DisplayName is present on test methods, OR method names read as clear descriptive titles when converted from camelCase/snake_case.
- ✅ **2.11-format-description**: @Description is present on at least the class or the tier method and provides context beyond what the table rows already express — such as the discount ladder structure, eligibility rules, or application context. Does NOT merely restate the column names or summarise what the rows show.
- ❌ **2.12-format-typeconverter**: TypeConverterSources is used for at least one human-readable conversion (yes/no, or rolling window notation).
  > No TypeConverterSources annotation or TypeConverter is present anywhere in the response. All values use numeric and enum types directly.
- ✅ **2.13-format-annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ❌ **2.14-format-description-textblock**: @Description text uses a text block (triple-quoted string """) when the text is long, not string concatenation with +.
  > Both @Description annotations use string concatenation with `+`: `@Description("Children always receive..." + "regardless of how many...")` and the tier method similarly uses `+ "in the last 30 days..."` etc. — not triple-quoted text blocks.

### ⚠️ Eval eval-16-order-splitting [with_skill]

**9/13** · 52295 tokens · 201060ms

- ✅ **3.1-decomposition-concern-separation**: Trigger detection, shipment optimisation, and companion constraint are in separate tables. Not one monolithic table.
- ✅ **3.2-decomposition-trigger-interactions**: At least one row where multiple triggers fire simultaneously (e.g. mixed fulfilment AND mixed stock status).
- ✅ **3.3-depth-all-four-triggers**: All four split triggers are covered: mixed fulfilment, mixed stock status, no single warehouse has all items, different addresses.
- ✅ **3.4-depth-companion-ambiguity-surfaced**: The 'no warehouse has both companions' case is included and recognised as a product decision — e.g. flagged as an open question, or the chosen resolution is explained. Not silently omitted.
- ✅ **3.5-depth-companion-edge-cases**: Companion constraint covers at least: companions at same location, companions split from non-companion items, and the ambiguous case (no location has both).
- ✅ **3.6-readability-scenario-names**: Optimisation scenarios describe the business situation (e.g. 'Consolidate: two beats three', 'Everything at one warehouse') — not 'Test case 2'.
- ✅ **3.7-readability-item-property-mapping**: The trigger table represents multi-item orders where each item has its own fulfillment type, address, and availability. The notation used makes the item-to-property mapping unambiguous — not value sets like {delivery, pickup} which lose which item has which property.
- ✅ **3.8-readability-scalar-column**: Optimisation table uses a scalar count column (e.g. 'Shirts ordered') not item lists, when product identity doesn't matter for the logic.
- ✅ **3.9-correctness-valid-assignments**: Inventory assignments are physically valid: items are only assigned to locations that have them. Shipment counts match the assignments shown.
- ❌ **3.10-format-displayname**: @DisplayName is present on test methods, OR method names read as clear descriptive titles when converted from camelCase/snake_case.
  > The response contains no Java or Kotlin test code whatsoever — it is a pure spec-by-example table analysis. No @DisplayName annotations or test method names appear anywhere.
- ❌ **3.11-format-description**: @Description is present on at least the class and provides context beyond what the table rows already express — such as the splitting rules, constraints, or application context. Does NOT merely restate the column names or summarise what the rows show.
  > The response contains no Java or Kotlin test code. No @Description annotation is present anywhere in the output.
- ❌ **3.12-format-annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
  > No annotations of any kind appear in the response; it contains only markdown tables and prose, no Java/Kotlin code.
- ❌ **3.13-format-description-textblock**: @Description text uses a text block (triple-quoted string """) when the text is long, not string concatenation with +.
  > No @Description annotation or any Java/Kotlin code is present in the response.

### ⚠️ Eval eval-17-shopping-cart [with_skill]

**12/13** · 45793 tokens · 83018ms

- ✅ **4.1-decomposition-items-separate-from-coupons**: Item operations (add/remove) are in a separate table from coupon validation. Not interleaved in one table.
- ✅ **4.3-rows-independently-executable**: Each table row is independently executable — no row depends on a prior row's result. The table expresses rules, not a sequential test script.
- ✅ **4.4-depth-item-operations**: Item operations covered: add to empty cart, add different product, add more of same, remove product, remove unknown product, remove last item.
- ✅ **4.5-depth-coupon-variations**: Coupon variations covered: valid percentage, valid fixed amount, product-specific, expired, unknown code, replace existing coupon.
- ✅ **4.6-depth-coupon-exceeds-total**: A scenario where the coupon discount exceeds the cart total — total floors at zero.
- ✅ **4.7-depth-checkout-edge-cases**: Checkout covers: sufficient stock, insufficient stock, and empty cart.
- ✅ **4.8-readability-business-language**: Column names use business/user-perspective language (e.g. 'Message?' not 'Result?', 'Coupon code' not 'input'). The table reads as a specification a product person could review.
- ✅ **4.9-readability-test-data-visible**: Test data that affects the outcome — product prices, coupon rules (type, amount, expiry) — is visible in the table via columns, notes, or explicit setup, not silently hardcoded. The reader can trace how inputs lead to outputs.
- ✅ **4.10-depth-open-questions-surfaced**: At least one genuinely underspecified interaction is surfaced as an open question — such as: what happens to a product-specific coupon when that product is removed from the cart, or how does a percentage coupon interact with a product-specific discount. These are NOT stated in the requirements.
- ✅ **4.11-correctness-cart-totals**: Cart totals are arithmetically correct: sum(quantity × price) − coupon = total, for every row in the total calculation table.
- ✅ **4.12-coupon-expiry-column**: Coupon table uses a column to signal expiry status — either an 'Expired?' yes/no column, an expiry date column, or equivalent. Expiry is visible per row, not just implied by the scenario name.
- ❌ **4.13-coupon-cart-contents-column**: Coupon table includes a cart contents column (or 'Applies to' / 'Product in cart?' column) so the reader can see whether a product-specific coupon's target is actually in the cart. The product discount applied/not-applied distinction is driven by visible data, not just the scenario name.
  > The Coupon Application table has no cart contents or 'Product in cart?' column. The Cart Total table separates the product-in-cart vs not-in-cart scenarios but the Coupon Application table (where coupon type 'off Widget' appears) does not show cart contents.
- ✅ **4.14-coupon-before-after-columns**: Coupon entry scenarios use before/after columns (e.g. 'Coupon before' / 'Coupon after?' or 'Active coupon' / 'Active coupon after?') to specify the state change when entering, replacing, or entering invalid coupon codes.

