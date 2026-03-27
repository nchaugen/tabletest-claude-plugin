# Eval Review — Iteration 5

**Model:** sonnet · **Date:** 2026-03-25 · **Evals:** 16

## Summary

**with_skill:** 100/134 (74.6%) · 978849 tokens · 872.3s · $1.7752

## Delta vs Iteration 4

**Regressions (25):**
- ❌ eval-1-convert-repetitive-tests: `description-adds-information`
- ❌ eval-1-convert-repetitive-tests: `description-uses-textblock`
- ❌ eval-2-parse-dates: `description-adds-information`
- ❌ eval-2-parse-dates: `description-uses-textblock`
- ❌ eval-7-permission-check: `no-duplicate-role-output`
- ❌ eval-7-permission-check: `has-descriptive-title`
- ❌ eval-7-permission-check: `description-adds-information`
- ❌ eval-7-permission-check: `description-uses-textblock`
- ❌ eval-8-money-parse: `description-adds-information`
- ❌ eval-9-bonus-contractor-structure: `annotation-order`
- ❌ eval-9-bonus-contractor-structure: `description-uses-textblock`
- ❌ eval-13-shipping-partial-applicability: `express-uses-value-sets`
- ❌ eval-13-shipping-partial-applicability: `overnight-grouped`
- ❌ eval-14-weekly-pay: `1.2-error-has-expected-column`
- ❌ eval-14-weekly-pay: `1.12-format-annotation-order`
- ❌ eval-14-weekly-pay: `1.13-format-description-textblock`
- ❌ eval-15-reis-discount: `2.4-depth-rolling-window-boundary`
- ❌ eval-15-reis-discount: `2.10-format-displayname`
- ❌ eval-15-reis-discount: `2.11-format-description`
- ❌ eval-15-reis-discount: `2.13-format-annotation-order`
- ❌ eval-15-reis-discount: `2.14-format-description-textblock`
- ❌ eval-16-order-splitting: `3.10-format-displayname`
- ❌ eval-16-order-splitting: `3.11-format-description`
- ❌ eval-16-order-splitting: `3.12-format-annotation-order`
- ❌ eval-16-order-splitting: `3.13-format-description-textblock`

**Improvements (9):**
- ✅ eval-8-money-parse: `null-as-blank-cell`
- ✅ eval-12-subscription-loyalty-trial: `does-not-invent-loyalty-trial-resolution`
- ✅ eval-13-shipping-partial-applicability: `standard-destination-blank`
- ✅ eval-14-weekly-pay: `1.1-decomposition-concern-separation`
- ✅ eval-14-weekly-pay: `1.2-decomposition-rules-not-arithmetic`
- ✅ eval-14-weekly-pay: `1.8-correctness-payable-hours`
- ✅ eval-16-order-splitting: `3.2-decomposition-trigger-interactions`
- ✅ eval-16-order-splitting: `3.4-depth-companion-ambiguity-surfaced`
- ✅ eval-16-order-splitting: `3.8-readability-scalar-column`

## Per-Eval Results

### ⚠️ Eval eval-1-convert-repetitive-tests [with_skill]

**8/10** · 42331 tokens · 15965ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ✅ **has-question-mark-column**: Table has at least one column name ending with '?' (e.g. 'Discount?' or 'Expected?')
- ✅ **has-three-data-rows**: Table has exactly 3 data rows (GOLD, SILVER, BRONZE)
- ✅ **single-assertion-in-method**: Test method body contains only one assertEquals (or equivalent assertion) — not three
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements
- ✅ **scenario-column-present**: Table has a scenario/description column as the leftmost column
- ✅ **has-descriptive-title**: Test method has either a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case
- ❌ **description-adds-information**: @Description is present and provides context beyond what the table rows already express — such as fixed values shared by all rows, where/when the rule applies, or open questions. Does NOT merely restate the column names or summarise what the rows show.
  > No @Description annotation appears anywhere in the model response. The response contains only @TableTest and the method body.
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ❌ **description-uses-textblock**: @Description text uses a text block (triple-quoted string """) when the text is long, not string concatenation with +.
  > No @Description annotation is present in the model response at all, so there is no @Description text to evaluate for text block usage.

### ⚠️ Eval eval-2-parse-dates [with_skill]

**7/9** · 74811 tokens · 53273ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ✅ **null-as-blank-cell**: Null input is represented as a blank cell (not the string 'null') in the table
- ✅ **exception-handled-cleanly**: The empty string / exception case is handled — either via a Throws? column, assertThrows inside the method, or a separate @TableTest — rather than being silently omitted
- ✅ **localdate-result-column**: There is a result column typed as LocalDate (or using a string representation that maps to LocalDate)
- ✅ **type-conversion-addressed**: The response addresses type conversion for non-standard date formats (slash format, short year) — mentions @TypeConverter or a converter method
- ✅ **has-descriptive-title**: Test method has either a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case (e.g. 'dateParsing' → 'Date Parsing'). Not a generic name like 'test1' or 'testMethod'.
- ❌ **description-adds-information**: @Description is present and provides context beyond what the table rows already express — such as supported date formats, where/when the rule applies, or open questions. Does NOT merely restate the column names or summarise what the rows show.
  > No @Description annotation appears anywhere in the code sample. The annotations used are only @TableTest and @Test.
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ❌ **description-uses-textblock**: @Description text uses a text block (triple-quoted string """) when the text is long, not string concatenation with +.
  > No @Description annotation is present in the response at all, so this requirement cannot be satisfied.

### ✅ Eval eval-3-dependency-setup [with_skill]

**4/4** · 66690 tokens · 12009ms

- ✅ **flags-junit-version**: Response explicitly flags that JUnit 5.10.0 is below the required minimum (5.11) and recommends upgrading
- ✅ **provides-correct-groupid**: Response provides the tabletest dependency with groupId 'org.tabletest' (not guessed)
- ✅ **provides-artifactid**: Response includes 'tabletest-junit' as the artifactId
- ✅ **includes-test-scope**: The suggested dependency includes <scope>test</scope>

### ✅ Eval eval-4-loan-approval [with_skill]

**6/6** · 42441 tokens · 29347ms

- ✅ **produces-markdown-table**: Output contains a markdown table (using | column | syntax)
- ✅ **output-column-has-question-mark**: At least one output column name ends with '?' (e.g. 'Eligible?' or 'Approved?')
- ✅ **business-language-columns**: Column names use domain/business language — no implementation terms like 'creditScore', 'isEligible', 'boolean', 'int'
- ✅ **senior-threshold-row**: Table includes at least one row specifically for the senior applicant (65+) threshold scenario
- ✅ **missing-income-marked-open**: The missing income scenario is represented — either as a row with an open/uncertain expected value (?, TBD, or blank) or as an explicit open question note
- ✅ **scenario-names-describe-conditions**: Scenario names describe conditions ('Senior applicant at threshold') not outcomes ('Approved')

### ✅ Eval eval-5-order-transitions [with_skill]

**5/5** · 42684 tokens · 34986ms

- ✅ **produces-markdown-table**: Output contains at least one markdown table
- ✅ **cancellation-coverage**: Table covers cancellation rules — includes rows for states where cancellation is allowed and where it is not
- ✅ **return-window-addressed**: The 30-day return window rule is addressed — either as a threshold column, a separate table, or flagged as an open question
- ✅ **value-set-or-multiple-states**: Uses value sets {PENDING, CONFIRMED} or equivalent to express 'regardless of which starting state' for cases where a rule holds across states
- ✅ **output-column-has-question-mark**: At least one output column ends with '?'

### ✅ Eval eval-6-discount-interaction [with_skill]

**5/5** · 43101 tokens · 44086ms

- ✅ **produces-markdown-table**: Output contains a markdown table
- ✅ **does-not-invent-resolution**: The output does NOT silently resolve the stacking/cap ambiguity — it leaves the conflicting cases as open questions, blank cells, or explicitly marks them as unresolved
- ✅ **covers-both-discounts-applying**: Table includes at least one row where both bulk and loyalty discounts apply simultaneously (exposing the interaction)
- ✅ **open-question-surfaced**: The unresolved decision (stacking vs. higher-only vs. cap) is explicitly surfaced — via an Open Questions column, a '?' cell, or a note
- ✅ **output-column-has-question-mark**: At least one output column ends with '?'

### ⚠️ Eval eval-7-permission-check [with_skill]

**6/10** · 41821 tokens · 10356ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ✅ **uses-value-sets**: Table uses value-set syntax (curly braces like {READ, WRITE} or {READ, WRITE, DELETE}) to express rules that hold across multiple actions — rather than one row per role/action combination
- ✅ **fewer-than-nine-rows**: Table has fewer than 9 data rows (i.e. does not enumerate every role/action combination individually)
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements
- ✅ **scenario-column-present**: Table has a scenario/description column as the leftmost column
- ❌ **no-duplicate-role-output**: No two rows share both the same Role value and the same boolean output — forces value set consolidation (e.g. USER can READ and USER can WRITE, both true, should be one row with {READ, WRITE}).
  > USER/READ/true and USER/WRITE/true are separate rows sharing the same Role (USER) and same output (true); similarly GUEST/WRITE/false and GUEST/DELETE/false share Role (GUEST) and output (false). These should have been consolidated with value sets like {READ, WRITE} and {WRITE, DELETE} respectively.
- ❌ **has-descriptive-title**: Test method has either a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case (e.g. 'permissionsByRoleAndAction' → 'Permissions By Role And Action'). Not a generic name like 'test1' or 'canPerform'.
  > void canPerform(Role role, Action action, boolean allowed) — the assertion explicitly lists 'canPerform' as an example of a generic name that does not pass.
- ❌ **description-adds-information**: @Description is present and provides context beyond what the table rows already express — such as where permissions are checked in the request lifecycle, or open questions. Does NOT merely restate the column names or summarise what the rows show.
  > No @Description annotation appears anywhere in the response. The only annotation present is @TableTest.
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ❌ **description-uses-textblock**: @Description text uses a text block (triple-quoted string """) when the text is long, not string concatenation with +.
  > No @Description annotation is present in the response at all, so the requirement for a text-block-formatted @Description cannot be satisfied.

### ⚠️ Eval eval-8-money-parse [with_skill]

**9/10** · 75645 tokens · 48753ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ✅ **null-as-blank-cell**: Null input is represented as a blank cell in the valid inputs @TableTest table (not the string 'null', and not as a separate @Test method). The blank cell maps to a null parameter.
- ✅ **exception-has-expected-column**: The exception/invalid-input table has an expected column (e.g. 'Throws?' or 'Exception?') specifying the exception type per row — rather than hardcoding the exception type in @Description or the method name.
- ✅ **exception-cases-handled**: The exception cases (empty string, letters-only, negative) are handled — via a Throws? column, assertThrows in the method body, or a separate @TableTest — not silently omitted
- ✅ **result-column-with-question-mark**: Valid inputs have a result column name ending with '?' (e.g. 'Money?' or 'Result?')
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements
- ✅ **has-descriptive-title**: Test method has either a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case (e.g. 'moneyParsing' → 'Money Parsing'). Not a generic name like 'test1' or 'testMethod'.
- ❌ **description-adds-information**: @Description is present and provides context beyond what the table rows already express — such as the expected currency format, or open questions. Does NOT merely restate the column names or summarise what the rows show.
  > @Description("Valid string inputs produce a Money with the same value; null input returns null") and @Description("Empty string, non-numeric input, and negative values are rejected") — both merely summarise what the rows already show; no currency format details, no open questions, no context beyond the row data
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **description-uses-textblock**: @Description text uses a text block (triple-quoted string """) when the text is long, not string concatenation with +.

### ⚠️ Eval eval-9-bonus-contractor-structure [with_skill]

**6/8** · 44347 tokens · 48364ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ✅ **contractor-uses-value-set**: CONTRACTOR row uses a value set for Department (e.g. {SALES, ENGINEERING}) to express 'regardless of department' — not a dummy placeholder like 'ANY' or 'N/A', and not enumerated as separate rows.
- ✅ **four-core-rules-covered**: The four level×department combinations (SENIOR+SALES, SENIOR+ENGINEERING, JUNIOR+SALES, JUNIOR+ENGINEERING) are all present in the table(s)
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements
- ✅ **has-descriptive-title**: Test method has either a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case
- ✅ **description-adds-information**: @Description is present and provides context beyond what the table rows already express — such as how bonus rates are applied (e.g. as percentage of base salary), or open questions.
- ❌ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
  > @TableTest appears before @Description in the code: '@TableTest("""...""") @Description("...")' — @Description should come before @TableTest
- ❌ **description-uses-textblock**: @Description text uses a text block (triple-quoted string """) when the text is long, not string concatenation with +.
  > @Description("Bonus rates: SENIOR 15% Sales / 12% Engineering; JUNIOR 8% Sales / 5% Engineering; CONTRACTOR always 0% regardless of department") — uses a regular single-quoted string, not a text block

### ✅ Eval eval-10-subscription-billing [with_skill]

**5/5** · 46033 tokens · 92106ms

- ✅ **produces-multiple-tables**: Output contains at least 2 separate markdown tables (not everything crammed into one table)
- ✅ **tables-have-distinct-concerns**: The tables cover distinct concerns — e.g. pricing/plans is separate from cancellation/refund rules
- ✅ **output-columns-have-question-marks**: At least one output column in each table ends with '?'
- ✅ **refund-exception-covered**: The 24-hour no-refund exception is included as a distinct scenario row
- ✅ **business-language-columns**: Column names use domain/business language — no implementation terms like 'boolean', 'int', 'isProrated', camelCase identifiers

### ✅ Eval eval-12-subscription-loyalty-trial [with_skill]

**5/5** · 43602 tokens · 49081ms

- ✅ **produces-multiple-tables**: Output contains at least 2 separate markdown tables
- ✅ **annual-no-trial-rule-represented**: A table or note explicitly captures that the free trial is not available on annual plans (not just implied)
- ✅ **loyalty-discount-row-present**: At least one table row demonstrates the loyalty programme discount scenario for annual plan subscribers
- ✅ **surfaces-genuine-open-questions**: The response identifies at least one genuinely underspecified interaction — such as: what happens when a trial subscriber upgrades to annual mid-trial, when a subscriber joins the loyalty programme mid-billing-cycle, or whether a cancellation refund is based on the discounted or full price. These are NOT stated in the rules and require a product decision.
- ✅ **open-question-surfaced**: The annual/loyalty/trial interaction is explicitly surfaced as an open question — via '?', 'TBD', blank expected value, or a written open question — rather than silently resolved

### ⚠️ Eval eval-13-shipping-partial-applicability [with_skill]

**4/6** · 43380 tokens · 47688ms

- ✅ **produces-markdown-table**: Output contains a markdown table
- ✅ **express-split-by-uk-free-tier**: Express shipping is split into at least two cases distinguishing the UK free tier (UK, order £50+, free) from the paid tier. The split is driven by the UK-only free threshold rule.
- ❌ **express-uses-value-sets**: Express rows use value sets to consolidate cases with the same outcome — e.g. non-UK destinations at/above threshold in one row (such as {Ireland, Other} or {Ireland, International}), or UK at/above threshold in one row ({£50, £75}). Not enumerated as separate rows per destination or per order value when the result is identical. Any reasonable label for non-UK/Ireland destinations is acceptable (International, Other, Rest of World, etc.).
  > The model enumerates four separate Express rows above threshold: 'Express: at threshold in UK (free tier) | Express | £50.00 | UK | £0.00', 'Express: above threshold in UK (free tier) | Express | £75.00 | UK | £0.00', 'Express: at threshold outside UK (no free tier) | Express | £50.00 | {France, Ireland} | £9.99', 'Express: above threshold outside UK (no free tier) | Express | £100.00 | {France, Ireland} | £9.99' — UK at/above threshold not consolidated into one row with {£50, £75}, and non-UK at/above threshold not consolidated into one row.
- ❌ **overnight-grouped**: Overnight UK and Ireland are in one row using a value set ({UK, Ireland}) — not separate rows, since both have the same cost and availability.
  > | Overnight: available in UK | Overnight | {£20, £80} | UK | £14.99 | and | Overnight: available in Ireland | Overnight | {£20, £80} | Ireland | £14.99 | — these are two separate rows, not consolidated into one row with {UK, Ireland}.
- ✅ **standard-destination-value-set-or-blank**: Standard shipping destination uses a value set covering all options (e.g. {UK, Ireland, International} or {UK, Ireland, Other}) or is blank/absent — either approach is valid since destination is irrelevant to the cost. Any reasonable label for non-UK/Ireland destinations is acceptable.
- ✅ **output-column-has-question-mark**: At least one output column ends with '?'

### ⚠️ Eval eval-14-weekly-pay [with_skill]

**8/13** · 236741 tokens · 190858ms

- ✅ **1.1-traceability-columns**: The pay table includes intermediate expected columns (e.g. Regular pay?, Overtime pay?, Premium pay?) so the reader can trace which rule contributes what — not just a single total Weekly pay? column.
- ❌ **1.2-error-has-expected-column**: The error/rejection table has an expected column (e.g. 'Throws?' or 'Exception?') specifying the exception type per row — rather than hardcoding the exception type in @Description or the method name.
  > The error table header is 'Scenario | Rate' with no Throws? or Exception? column. The exception type is hardcoded in the assertion: '.isInstanceOf(IllegalArgumentException.class)'
- ✅ **1.3-depth-overtime-boundary**: Overtime threshold boundary is covered: at least a value at the threshold (40) and just above (41 or similar). Not just values well below and well above.
- ✅ **1.4-depth-combined-scenario**: A combined scenario is present: a row with weekday + Sunday + holiday hours, testing the composition of all hour types.
- ❌ **1.5-depth-error-edge-cases**: Error/edge cases covered: at least negative hours (floored at zero) and negative or missing rate (rejected).
  > Only negative rate is covered in the error table. Negative hours are only mentioned in prose: 'If your API can ever receive negative hours as input, add rows for those' — no test rows for negative hours are included.
- ❌ **1.6-readability-empty-cells**: Sunday and Holiday hour columns use empty cells (not 0) when those inputs are not relevant to a scenario — making it easier to spot which rows involve weekend/holiday hours. This is a readability preference; parameter types should be Integer (not int) to support null from blank cells.
  > Rows like 'Standard week, no overtime' use explicit zeros: '| 40 | 0 | 0 | 15.00 |' rather than blank cells for Sunday hrs and Holiday hrs.
- ✅ **1.7-readability-scenario-names**: Scenario names describe work patterns (e.g. 'Part-time', 'Five hours overtime', 'Full Sunday shift') — not 'Test case 1' or expected outcomes.
- ✅ **1.8-correctness-expected-values**: Expected values in intermediate and total columns are arithmetically correct for every row, consistent with the formulas in @Description.
- ✅ **1.9-correctness-value-set-semantics**: Value sets are only used where all values in the set produce the same expected result (e.g. any rate × 0 = 0). Not used where results would differ.
- ✅ **1.10-format-displayname**: @DisplayName is present on test methods, OR method names read as clear descriptive titles when converted from camelCase/snake_case.
- ✅ **1.11-format-description**: @Description is present on at least the payable hours method and provides context beyond what the table rows already express — such as the conversion rules, fixed values, or application context. Does NOT merely restate the column names or summarise what the rows show.
- ❌ **1.12-format-annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
  > On test methods the order is @TableTest first, then @Description: '@TableTest("""...""")
    @Description(...)' — @Description appears after @TableTest, violating the required order.
- ❌ **1.13-format-description-textblock**: @Description text uses a text block (triple-quoted string """) when the text is long, not string concatenation with +.
  > @Description uses string concatenation: '"Calculates gross weekly pay for hourly employees. " + "Weekday hours up to 40 are paid at the base rate; ..."' — not a text block.

### ⚠️ Eval eval-15-reis-discount [with_skill]

**4/11** · 44810 tokens · 57274ms

- ✅ **2.1-decomposition-concern-separation**: Discount ladder, and traveller eligibility are in separate tables. Rolling window counting as a third table is desirable.
- ✅ **2.2-children-flat-discount**: Children's flat 20% discount is represented — either as a row in the eligibility table or a separate note. It does not follow the ladder.
- ✅ **2.3-depth-tier-boundaries**: The discount ladder has multiple tiers derived from the 'every fifth trip' rule, with boundary values or value sets showing which ticket counts produce which discount. The maximum 40% discount is represented.
- ❌ **2.4-depth-rolling-window-boundary**: The 30-day rolling window boundary is tested: a ticket at exactly 30 days is included, at 31 days is excluded.
  > Table 4 uses 'Days Active > 30' for all rows and discusses travel pattern changes over the window, but there is no row testing a ticket at exactly 30 days (included) versus 31 days (excluded). The boundary between day 30 and day 31 is never tested.
- ❌ **2.6-readability-human-readable-values**: Eligibility uses human-readable values (yes/no or equivalent) rather than raw true/false. TypeConverter or similar mechanism mentioned.
  > TypeConverter is not mentioned anywhere in the response. The response contains no reference to TypeConverter, TypeConverterSources, or any similar conversion mechanism.
- ✅ **2.9-correctness-value-set-tier-semantics**: Value sets in the tier table contain exactly the values that produce the same discount. No cross-tier contamination.
- ❌ **2.10-format-displayname**: @DisplayName is present on test methods, OR method names read as clear descriptive titles when converted from camelCase/snake_case.
  > The response contains only markdown tables and prose — there is no Java/Kotlin code, no @DisplayName annotations, and no method names anywhere in the output.
- ❌ **2.11-format-description**: @Description is present on at least the class or the tier method and provides context beyond what the table rows already express — such as the discount ladder structure, eligibility rules, or application context. Does NOT merely restate the column names or summarise what the rows show.
  > The response contains no Java/Kotlin code and no @Description annotation anywhere. It is entirely markdown tables and prose commentary.
- ❌ **2.12-format-typeconverter**: TypeConverterSources is used for at least one human-readable conversion (yes/no, or rolling window notation).
  > Neither 'TypeConverterSources' nor 'TypeConverter' appears anywhere in the response.
- ❌ **2.13-format-annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
  > The response contains no Java/Kotlin annotations of any kind — no @DisplayName, @Description, or @TableTest appear anywhere in the output.
- ❌ **2.14-format-description-textblock**: @Description text uses a text block (triple-quoted string """) when the text is long, not string concatenation with +.
  > The response contains no @Description annotation and no Java/Kotlin code whatsoever, so no text block or string concatenation is present.

### ⚠️ Eval eval-16-order-splitting [with_skill]

**8/13** · 45779 tokens · 80059ms

- ✅ **3.1-decomposition-concern-separation**: Trigger detection, shipment optimisation, and companion constraint are in separate tables. Not one monolithic table.
- ✅ **3.2-decomposition-trigger-interactions**: At least one row where multiple triggers fire simultaneously (e.g. mixed fulfilment AND mixed stock status).
- ✅ **3.3-depth-all-four-triggers**: All four split triggers are covered: mixed fulfilment, mixed stock status, no single warehouse has all items, different addresses.
- ✅ **3.4-depth-companion-ambiguity-surfaced**: The 'no warehouse has both companions' case is flagged as an open question or product decision, not silently resolved as either 'error' or 'ship separately'. The requirement does NOT hint at this — the agent must recognise the ambiguity unprompted.
- ✅ **3.5-depth-companion-edge-cases**: Companion constraint covers at least: companions at same location, companions split from non-companion items, and the ambiguous case (no location has both).
- ✅ **3.6-readability-scenario-names**: Optimisation scenarios describe the business situation (e.g. 'Consolidate: two beats three', 'Everything at one warehouse') — not 'Test case 2'.
- ❌ **3.7-readability-set-notation**: Trigger table uses set notation ({delivery, pickup}) for closed enumerations rather than prose or lists.
  > The trigger table uses prose like 'all delivery', 'all store pickup', 'store pickup + delivery' rather than set notation such as {delivery} or {delivery, pickup}.
- ✅ **3.8-readability-scalar-column**: Optimisation table uses a scalar count column (e.g. 'Shirts ordered') not item lists, when product identity doesn't matter for the logic.
- ✅ **3.9-correctness-valid-assignments**: Inventory assignments are physically valid: items are only assigned to locations that have them. Shipment counts match the assignments shown.
- ❌ **3.10-format-displayname**: @DisplayName is present on test methods, OR method names read as clear descriptive titles when converted from camelCase/snake_case.
  > The response contains no Java/Kotlin code at all — no test methods, no annotations, no @DisplayName. The response is entirely in table/prose format.
- ❌ **3.11-format-description**: @Description is present on at least the class and provides context beyond what the table rows already express — such as the splitting rules, constraints, or application context. Does NOT merely restate the column names or summarise what the rows show.
  > The response contains no Java/Kotlin code at all — no @Description annotation is present anywhere in the response.
- ❌ **3.12-format-annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
  > The response contains no Java/Kotlin code and no annotations of any kind.
- ❌ **3.13-format-description-textblock**: @Description text uses a text block (triple-quoted string """) when the text is long, not string concatenation with +.
  > The response contains no Java/Kotlin code and no @Description annotation, so no text block usage can be assessed.

### ⚠️ Eval eval-17-shopping-cart [with_skill]

**10/14** · 44633 tokens · 58128ms

- ✅ **4.1-decomposition-items-separate-from-coupons**: Item operations (add/remove) are in a separate table from coupon validation. Not interleaved in one table.
- ✅ **4.3-rows-independently-executable**: Each table row is independently executable — no row depends on a prior row's result. The table expresses rules, not a sequential test script.
- ✅ **4.4-depth-item-operations**: Item operations covered: add to empty cart, add different product, add more of same, remove product, remove unknown product, remove last item.
- ✅ **4.5-depth-coupon-variations**: Coupon variations covered: valid percentage, valid fixed amount, product-specific, expired, unknown code, replace existing coupon.
- ✅ **4.6-depth-coupon-exceeds-total**: A scenario where the coupon discount exceeds the cart total — total floors at zero.
- ✅ **4.7-depth-checkout-edge-cases**: Checkout covers: sufficient stock, insufficient stock, and empty cart.
- ✅ **4.8-readability-business-language**: Column names use business/user-perspective language (e.g. 'Message?' not 'Result?', 'Coupon code' not 'input'). The table reads as a specification a product person could review.
- ✅ **4.9-readability-test-data-visible**: Test data that affects the outcome — product prices, coupon rules (type, amount, expiry) — is visible in the table via columns, @Description, or explicit setup, not silently hardcoded. The reader can trace how inputs lead to outputs.
- ✅ **4.10-depth-open-questions-surfaced**: At least one genuinely underspecified interaction is surfaced as an open question — such as: what happens to a product-specific coupon when that product is removed from the cart, or how does a percentage coupon interact with a product-specific discount. These are NOT stated in the requirements.
- ✅ **4.11-correctness-cart-totals**: Cart totals are arithmetically correct: sum(quantity × price) − coupon = total, for every row in the total calculation table.
- ❌ **4.13-format-displayname**: @DisplayName is present on test methods, OR method names read as clear descriptive titles when converted from camelCase/snake_case.
  > The response contains no Java or Kotlin test code at all — only markdown specification tables. There are no test methods, no @DisplayName annotations, and no method names.
- ❌ **4.14-format-description**: @Description is present on at least the class and provides context beyond what the table rows already express — such as the cart model, state management approach, or application context. Does NOT merely restate the column names or summarise what the rows show.
  > No Java or Kotlin code was produced. There is no @Description annotation anywhere in the response.
- ❌ **4.15-format-annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
  > No code was produced in the response; no annotations of any kind appear.
- ❌ **4.16-format-description-textblock**: @Description text uses a text block (triple-quoted string """) when the text is long, not string concatenation with +.
  > No code was produced in the response; no @Description annotation or text block appears.

