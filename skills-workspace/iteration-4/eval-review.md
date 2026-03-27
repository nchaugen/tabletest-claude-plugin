# Eval Review — Iteration 4

**Model:** sonnet · **Date:** 2026-03-22 · **Evals:** 17

## Summary

**with_skill:** 85/100 (85.0%) · 1050701 tokens · 1097.2s · $2.0910

## Delta vs Iteration 3

**Regressions (4):**
- ❌ eval-8-money-parse: `null-as-blank-cell`
- ❌ eval-11-shipping-cost: `blank-for-inapplicable-fields`
- ❌ eval-12-subscription-loyalty-trial: `does-not-invent-loyalty-trial-resolution`
- ❌ eval-13-shipping-partial-applicability: `standard-destination-blank`

## Per-Eval Results

### ✅ Eval eval-1-convert-repetitive-tests [with_skill]

**6/6** · 41747 tokens · 12630ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ✅ **has-question-mark-column**: Table has at least one column name ending with '?' (e.g. 'Discount?' or 'Expected?')
- ✅ **has-three-data-rows**: Table has exactly 3 data rows (GOLD, SILVER, BRONZE)
- ✅ **single-assertion-in-method**: Test method body contains only one assertEquals (or equivalent assertion) — not three
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements
- ✅ **scenario-column-present**: Table has a scenario/description column as the leftmost column

### ✅ Eval eval-2-parse-dates [with_skill]

**5/5** · 74250 tokens · 60261ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ✅ **null-as-blank-cell**: Null input is represented as a blank cell (not the string 'null') in the table
- ✅ **exception-handled-cleanly**: The empty string / exception case is handled — either via a Throws? column, assertThrows inside the method, or a separate @TableTest — rather than being silently omitted
- ✅ **localdate-result-column**: There is a result column typed as LocalDate (or using a string representation that maps to LocalDate)
- ✅ **type-conversion-addressed**: The response addresses type conversion for non-standard date formats (slash format, short year) — mentions @TypeConverter or a converter method

### ✅ Eval eval-3-dependency-setup [with_skill]

**4/4** · 66152 tokens · 11886ms

- ✅ **flags-junit-version**: Response explicitly flags that JUnit 5.10.0 is below the required minimum (5.11) and recommends upgrading
- ✅ **provides-correct-groupid**: Response provides the tabletest dependency with groupId 'org.tabletest' (not guessed)
- ✅ **provides-artifactid**: Response includes 'tabletest-junit' as the artifactId
- ✅ **includes-test-scope**: The suggested dependency includes <scope>test</scope>

### ✅ Eval eval-4-loan-approval [with_skill]

**6/6** · 42576 tokens · 38329ms

- ✅ **produces-markdown-table**: Output contains a markdown table (using | column | syntax)
- ✅ **output-column-has-question-mark**: At least one output column name ends with '?' (e.g. 'Eligible?' or 'Approved?')
- ✅ **business-language-columns**: Column names use domain/business language — no implementation terms like 'creditScore', 'isEligible', 'boolean', 'int'
- ✅ **senior-threshold-row**: Table includes at least one row specifically for the senior applicant (65+) threshold scenario
- ✅ **missing-income-marked-open**: The missing income scenario is represented — either as a row with an open/uncertain expected value (?, TBD, or blank) or as an explicit open question note
- ✅ **scenario-names-describe-conditions**: Scenario names describe conditions ('Senior applicant at threshold') not outcomes ('Approved')

### ✅ Eval eval-5-order-transitions [with_skill]

**5/5** · 42819 tokens · 43936ms

- ✅ **produces-markdown-table**: Output contains at least one markdown table
- ✅ **cancellation-coverage**: Table covers cancellation rules — includes rows for states where cancellation is allowed and where it is not
- ✅ **return-window-addressed**: The 30-day return window rule is addressed — either as a threshold column, a separate table, or flagged as an open question
- ✅ **value-set-or-multiple-states**: Uses value sets {PENDING, CONFIRMED} or equivalent to express 'regardless of which starting state' for cases where a rule holds across states
- ✅ **output-column-has-question-mark**: At least one output column ends with '?'

### ✅ Eval eval-6-discount-interaction [with_skill]

**5/5** · 41306 tokens · 22790ms

- ✅ **produces-markdown-table**: Output contains a markdown table
- ✅ **does-not-invent-resolution**: The output does NOT silently resolve the stacking/cap ambiguity — it leaves the conflicting cases as open questions, blank cells, or explicitly marks them as unresolved
- ✅ **covers-both-discounts-applying**: Table includes at least one row where both bulk and loyalty discounts apply simultaneously (exposing the interaction)
- ✅ **open-question-surfaced**: The unresolved decision (stacking vs. higher-only vs. cap) is explicitly surfaced — via an Open Questions column, a '?' cell, or a note
- ✅ **output-column-has-question-mark**: At least one output column ends with '?'

### ✅ Eval eval-7-permission-check [with_skill]

**5/5** · 41668 tokens · 15610ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ✅ **uses-value-sets**: Table uses value-set syntax (curly braces like {READ, WRITE} or {READ, WRITE, DELETE}) to express rules that hold across multiple actions — rather than one row per role/action combination
- ✅ **fewer-than-nine-rows**: Table has fewer than 9 data rows (i.e. does not enumerate every role/action combination individually)
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements
- ✅ **scenario-column-present**: Table has a scenario/description column as the leftmost column

### ⚠️ Eval eval-8-money-parse [with_skill]

**4/5** · 69102 tokens · 47010ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ❌ **null-as-blank-cell**: Null input is represented as a blank cell (not the string 'null') in the table
  > The null case is handled entirely outside any table: "@Test
void nullInput_returnsNull() {
    assertThat(Money.parse(null)).isNull();
}" — null does not appear as a blank cell (or any cell) in a @TableTest table.
- ✅ **exception-cases-handled**: The exception cases (empty string, letters-only, negative) are handled — via a Throws? column, assertThrows in the method body, or a separate @TableTest — not silently omitted
- ✅ **result-column-with-question-mark**: Valid inputs have a result column name ending with '?' (e.g. 'Money?' or 'Result?')
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements

### ✅ Eval eval-9-bonus-contractor-structure [with_skill]

**5/5** · 42154 tokens · 24021ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ✅ **contractor-handled-separately-or-noted**: CONTRACTOR is either placed in a separate @TableTest (not mixed with the level×department grid) OR the response explicitly notes the structural asymmetry and explains the design choice
- ✅ **no-dummy-department-for-contractor**: If CONTRACTOR appears in the same table as SENIOR/JUNIOR rows, the department value is not a dummy or placeholder (e.g. 'ANY', 'N/A') just to fill the grid
- ✅ **four-core-rules-covered**: The four level×department combinations (SENIOR+SALES, SENIOR+ENGINEERING, JUNIOR+SALES, JUNIOR+ENGINEERING) are all present in the table(s)
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements

### ✅ Eval eval-10-subscription-billing [with_skill]

**5/5** · 43160 tokens · 49687ms

- ✅ **produces-multiple-tables**: Output contains at least 2 separate markdown tables (not everything crammed into one table)
- ✅ **tables-have-distinct-concerns**: The tables cover distinct concerns — e.g. pricing/plans is separate from cancellation/refund rules
- ✅ **output-columns-have-question-marks**: At least one output column in each table ends with '?'
- ✅ **refund-exception-covered**: The 24-hour no-refund exception is included as a distinct scenario row
- ✅ **business-language-columns**: Column names use domain/business language — no implementation terms like 'boolean', 'int', 'isProrated', camelCase identifiers

### ⚠️ Eval eval-11-shipping-cost [with_skill]

**4/5** · 44440 tokens · 65950ms

- ✅ **produces-markdown-table**: Output contains a markdown table
- ❌ **blank-for-inapplicable-fields**: Destination/country is blank (or absent from the table) for standard and express shipping rows — not filled with a dummy value or 'ANY'
  > Table 1 shows Standard and Express rows with '| {UK, International} |' in the Destination column, and Table 2 (the cost table) has no Destination column at all. The availability table explicitly fills Destination with '{UK, International}' for Standard and Express rows rather than leaving it blank.
- ✅ **overnight-country-specified**: Overnight rows include an explicit UK destination value (not blank), since availability depends on it
- ✅ **output-column-has-question-mark**: At least one output column ends with '?'
- ✅ **all-three-shipping-types-present**: Table includes rows for all three shipping types: standard, express, and overnight

### ⚠️ Eval eval-12-subscription-loyalty-trial [with_skill]

**4/5** · 43823 tokens · 55480ms

- ✅ **produces-multiple-tables**: Output contains at least 2 separate markdown tables
- ✅ **annual-no-trial-rule-represented**: A table or note explicitly captures that the free trial is not available on annual plans (not just implied)
- ✅ **loyalty-discount-row-present**: At least one table row demonstrates the loyalty programme discount scenario for annual plan subscribers
- ❌ **does-not-invent-loyalty-trial-resolution**: The response does NOT state a definitive invented answer about how the loyalty discount interacts with the free trial (e.g. does not silently assign a specific discount amount for a trial+loyalty combo without flagging it as unresolved)
  > The model states definitively: 'The loyalty/trial interaction at sign-up is actually clean (they can't both apply: loyalty discount is annual-only, trial is monthly-only).' and 'A loyalty member starting a trial gets the trial, then £9.99/month, no discount.' This is a definitive invented resolution of the loyalty+trial interaction at sign-up, presented as settled fact rather than an open question.
- ✅ **open-question-surfaced**: The annual/loyalty/trial interaction is explicitly surfaced as an open question — via '?', 'TBD', blank expected value, or a written open question — rather than silently resolved

### ⚠️ Eval eval-13-shipping-partial-applicability [with_skill]

**4/5** · 43257 tokens · 51077ms

- ✅ **produces-markdown-table**: Output contains a markdown table
- ✅ **express-split-into-two-cases**: Express shipping is represented as at least two distinct rows or cases: one where destination is irrelevant (paid tier, order under £50) and one where UK is specified (free tier, order £50+)
- ❌ **standard-destination-blank**: Standard shipping destination is blank or absent from the table — it is genuinely irrelevant and should not be filled with a dummy value
  > | Standard — flat rate always | Standard | {£10.00, £50.00, £100.00} | {UK, Ireland, International} | £3.99 | — the Destination column is filled with {UK, Ireland, International}, not blank or absent
- ✅ **overnight-destinations-specified**: Overnight shipping rows specify UK and/or Ireland as destinations — not left blank, since availability depends on it
- ✅ **output-column-has-question-mark**: At least one output column ends with '?'

### ⚠️ Eval eval-14-weekly-pay [with_skill]

**6/11** · 171006 tokens · 163434ms

- ❌ **1.1-decomposition-concern-separation**: Hour classification (how hours convert to payable hours) is in a separate table from pay calculation (payable hours × rate). Not one big table with all inputs and final pay.
  > There is only one table (`shouldCalculateWeeklyPay`) that combines all inputs (weekday hrs, Sunday hrs, holiday hrs, rate) with all outputs (regular pay, overtime pay, premium pay, weekly pay) in a single table. There is no separate table for hour classification vs pay calculation.
- ❌ **1.2-decomposition-rules-not-arithmetic**: Tables focus on the conversion rules (regular, overtime, Sunday, holiday → payable hours). Pay = payable hours × rate is treated as arithmetic, not a rule worthy of exhaustive testing.
  > The single table includes rate as an input column and pay amounts as outputs, directly testing pay = hours × rate arithmetic across all rows. The multiplication arithmetic is itself being exhaustively tested rather than treated as trivial.
- ✅ **1.3-depth-overtime-boundary**: Overtime threshold boundary is covered: at least a value at the threshold (40) and just above (41 or similar). Not just values well below and well above.
- ✅ **1.4-depth-combined-scenario**: A combined scenario is present: a row with weekday + Sunday + holiday hours, testing the composition of all hour types.
- ❌ **1.5-depth-error-edge-cases**: Error/edge cases covered: at least negative hours (floored at zero) and negative or missing rate (rejected).
  > The response covers negative rate rejection in `shouldRejectNegativeHourlyRate`, but there is no test row covering negative hours (floored at zero). All hour inputs in the main table are non-negative integers.
- ❌ **1.6-readability-empty-cells**: Sunday and Holiday hour columns use empty cells (not 0) when those inputs are not relevant to a scenario. Parameter types are Integer (not int) to support null from blank cells.
  > The table uses explicit 0 values: "Under-40 weekday hours only | 32 | 0 | 0 | 20.00". Parameter types are declared as primitive `int`: "void shouldCalculateWeeklyPay(int weekdayHours, int sundayHours, int holidayHours, double rate,"
- ✅ **1.7-readability-scenario-names**: Scenario names describe work patterns (e.g. 'Part-time', 'Five hours overtime', 'Full Sunday shift') — not 'Test case 1' or expected outcomes.
- ❌ **1.8-correctness-payable-hours**: Expected payable hours are arithmetically correct for every row: regular + overtime×1.5 + sunday×2 + holiday×2.
  > The table does not use payable hours as a column. Instead it uses pay amounts (regular pay, overtime pay, premium pay). The assertion requires correctness in terms of payable hours columns, which are absent. The response uses pay = hours × rate rather than isolating payable hours as an intermediate value.
- ✅ **1.9-correctness-value-set-semantics**: Value sets are only used where all values in the set produce the same expected result (e.g. any rate × 0 = 0). Not used where results would differ.
- ✅ **1.10-format-displayname**: @DisplayName is present on test methods.
- ✅ **1.11-format-description**: @Description is present on at least the payable hours method, explaining the conversion rules.

### ⚠️ Eval eval-15-reis-discount [with_skill]

**10/12** · 105424 tokens · 189728ms

- ✅ **2.1-decomposition-concern-separation**: Discount ladder, traveller eligibility, and ticket pricing are in separate tables. Rolling window counting as a fourth table is desirable.
- ✅ **2.2-decomposition-rules-not-arithmetic**: Tables focus on tier rules and eligibility, not on price arithmetic (zone_price × discount). The pricing table tests the end-to-end pipeline.
- ✅ **2.3-depth-every-tier-boundary**: Every tier (0%, 5%, 10%, …, 40%) has at least one row. Ideally with boundary values (4→5, 9→10, etc.) or value sets covering the full range per tier.
- ✅ **2.4-depth-rolling-window-boundary**: The 30-day rolling window boundary is tested: a ticket at exactly 30 days is included, at 31 days is excluded.
- ✅ **2.5-depth-youth-student-addressed**: Youth and students are explicitly shown as paying adult price with no discount. Not left ambiguous or omitted.
- ❌ **2.6-readability-human-readable-values**: Eligibility uses human-readable values (yes/no or equivalent) rather than raw true/false. TypeConverter or similar mechanism mentioned.
  > The response uses numeric discount percentages (0, 5, 10, etc.) and enum names (ADULT, CHILD, YOUTH, STUDENT) rather than yes/no human-readable eligibility values. No TypeConverter or similar mechanism is mentioned for converting human-readable strings.
- ✅ **2.7-readability-traceability-column**: Pricing table includes a discount column (e.g. 'Discount?') so the reader can trace: traveller + tickets → discount tier → price.
- ✅ **2.8-correctness-ticket-prices**: Ticket prices are arithmetically correct: zone_price × (1 − discount/100) for every pricing row.
- ✅ **2.9-correctness-value-set-tier-semantics**: Value sets in the tier table contain exactly the values that produce the same discount. No cross-tier contamination.
- ✅ **2.10-format-displayname**: @DisplayName is present on test methods.
- ✅ **2.11-format-description**: @Description is present on at least the class or the tier method, explaining the discount ladder structure.
- ❌ **2.12-format-typeconverter**: TypeConverterSources is used for at least one human-readable conversion (yes/no, or rolling window notation).
  > No @TypeConverterSources annotation appears anywhere in the response. The rolling window uses List<Integer> directly with numeric values, and eligibility uses enum names (ADULT, CHILD, etc.) without any TypeConverter mechanism.

### ⚠️ Eval eval-16-order-splitting [with_skill]

**7/11** · 137817 tokens · 245338ms

- ✅ **3.1-decomposition-concern-separation**: Trigger detection, shipment optimisation, and companion constraint are in separate tables. Not one monolithic table.
- ❌ **3.2-decomposition-trigger-interactions**: At least one row where multiple triggers fire simultaneously (e.g. mixed fulfilment AND mixed stock status).
  > Each row in 'splittingTriggers' isolates exactly one trigger: 'Mixed fulfilment methods', 'Mixed stock status', 'No single warehouse covers all', 'Different delivery addresses'. No row combines multiple triggers simultaneously.
- ✅ **3.3-depth-all-four-triggers**: All four split triggers are covered: mixed fulfilment, mixed stock status, no single warehouse has all items, different addresses.
- ❌ **3.4-depth-companion-ambiguity-surfaced**: The 'no warehouse has both companions' case is flagged as an open question or product decision, not silently resolved as either 'error' or 'ship separately'. The requirement does NOT hint at this — the agent must recognise the ambiguity unprompted.
  > The response resolves the ambiguity as throwing 'UnfulfillableOrderException' without acknowledging this as an open question or product decision: 'assertThatThrownBy(() -> splitter.split(order)).isInstanceOf(UnfulfillableOrderException.class)'. No open question is raised.
- ✅ **3.5-depth-companion-edge-cases**: Companion constraint covers at least: companions at same location, companions split from non-companion items, and the ambiguous case (no location has both).
- ✅ **3.6-readability-scenario-names**: Optimisation scenarios describe the business situation (e.g. 'Consolidate: two beats three', 'Everything at one warehouse') — not 'Test case 2'.
- ❌ **3.7-readability-set-notation**: Trigger table uses set notation ({delivery, pickup}) for closed enumerations rather than prose or lists.
  > The trigger table uses full item descriptors like '[shirt:DELIVERY:IN_STOCK:WH1:home, gift:PICKUP:IN_STOCK:WH1:home]' rather than set notation such as '{DELIVERY, PICKUP}' for enumerations.
- ❌ **3.8-readability-scalar-column**: Optimisation table uses a scalar count column (e.g. 'Shirts ordered') not item lists, when product identity doesn't matter for the logic.
  > The optimisation table ('minimisesShipmentCount') uses full item descriptor lists: '[shirt:DELIVERY:IN_STOCK:WH1:home, trousers:DELIVERY:IN_STOCK:WH1:home, shoes:DELIVERY:IN_STOCK:WH2:home]' instead of a scalar count column.
- ✅ **3.9-correctness-valid-assignments**: Inventory assignments are physically valid: items are only assigned to locations that have them. Shipment counts match the assignments shown.
- ✅ **3.10-format-displayname**: @DisplayName is present on test methods.
- ✅ **3.11-format-description**: @Description is present on at least the class, explaining the splitting rules and constraints.

