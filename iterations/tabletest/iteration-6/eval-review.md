# Eval Review — Iteration 6

**Model:** sonnet · **Date:** 2026-03-26 · **Evals:** 16

## Summary

**with_skill:** 105/133 (78.9%) · 1045965 tokens · 1050.1s · $1.9395

## Delta vs Iteration 5

**Regressions (9):**
- ❌ eval-8-money-parse: `null-as-blank-cell`
- ❌ eval-8-money-parse: `exception-has-expected-column`
- ❌ eval-8-money-parse: `annotation-order`
- ❌ eval-9-bonus-contractor-structure: `description-adds-information`
- ❌ eval-14-weekly-pay: `1.1-traceability-columns`
- ❌ eval-15-reis-discount: `2.1-decomposition-concern-separation`
- ❌ eval-16-order-splitting: `3.4-depth-companion-ambiguity-surfaced`
- ❌ eval-16-order-splitting: `3.5-depth-companion-edge-cases`
- ❌ eval-17-shopping-cart: `4.13-coupon-cart-contents-column`

**Improvements (15):**
- ✅ eval-7-permission-check: `no-duplicate-role-output`
- ✅ eval-7-permission-check: `has-descriptive-title`
- ✅ eval-7-permission-check: `description-uses-textblock`
- ✅ eval-9-bonus-contractor-structure: `annotation-order`
- ✅ eval-13-shipping-partial-applicability: `express-uses-value-sets`
- ✅ eval-13-shipping-partial-applicability: `overnight-grouped`
- ✅ eval-14-weekly-pay: `1.12-format-annotation-order`
- ✅ eval-15-reis-discount: `2.10-format-displayname`
- ✅ eval-15-reis-discount: `2.11-format-description`
- ✅ eval-15-reis-discount: `2.14-format-description-textblock`
- ✅ eval-16-order-splitting: `3.7-readability-set-notation`
- ✅ eval-17-shopping-cart: `4.13-format-displayname`
- ✅ eval-17-shopping-cart: `4.14-format-description`
- ✅ eval-17-shopping-cart: `4.15-format-annotation-order`
- ✅ eval-17-shopping-cart: `4.16-format-description-textblock`

## Per-Eval Results

### ⚠️ Eval eval-1-convert-repetitive-tests [with_skill]

**8/10** · 42259 tokens · 14936ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ✅ **has-question-mark-column**: Table has at least one column name ending with '?' (e.g. 'Discount?' or 'Expected?')
- ✅ **has-three-data-rows**: Table has exactly 3 data rows (GOLD, SILVER, BRONZE)
- ✅ **single-assertion-in-method**: Test method body contains only one assertEquals (or equivalent assertion) — not three
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements
- ✅ **scenario-column-present**: Table has a scenario/description column as the leftmost column
- ✅ **has-descriptive-title**: Test method has either a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case (e.g. 'discountByCustomerTier' → 'Discount By Customer Tier'). Not a generic name like 'test1' or 'testMethod'.
- ❌ **description-adds-information**: @Description is present and provides context beyond what the table rows already express — such as fixed values shared by all rows, where/when the rule applies, or open questions. Does NOT merely restate the column names or summarise what the rows show.
  > No @Description annotation appears anywhere in the response. The note about the constant order value appears only in prose outside the code block: "The order value `100` is constant across all three original tests, so it's inlined in the method body rather than added as a column."
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ❌ **description-uses-textblock**: @Description text uses a text block (triple-quoted string """) when the text is long, not string concatenation with +.
  > No @Description annotation is present in the response at all, so the requirement for a text block cannot be satisfied.

### ⚠️ Eval eval-2-parse-dates [with_skill]

**7/9** · 73825 tokens · 53561ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ✅ **null-as-blank-cell**: Null input is represented as a blank cell (not the string 'null') in the table
- ✅ **exception-handled-cleanly**: The empty string / exception case is handled — either via a Throws? column, assertThrows inside the method, or a separate @TableTest — rather than being silently omitted
- ✅ **localdate-result-column**: There is a result column typed as LocalDate (or using a string representation that maps to LocalDate)
- ✅ **type-conversion-addressed**: The response addresses type conversion for non-standard date formats (slash format, short year) — mentions @TypeConverter or a converter method
- ✅ **has-descriptive-title**: Test method has either a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case (e.g. 'dateParsing' → 'Date Parsing'). Not a generic name like 'test1' or 'testMethod'.
- ❌ **description-adds-information**: @Description is present and provides context beyond what the table rows already express — such as supported date formats, where/when the rule applies, or open questions. Does NOT merely restate the column names or summarise what the rows show.
  > No @Description annotation is present anywhere in the response.
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ❌ **description-uses-textblock**: @Description text uses a text block (triple-quoted string """) when the text is long, not string concatenation with +.
  > No @Description annotation is present in the response, so this assertion cannot be satisfied.

### ✅ Eval eval-3-dependency-setup [with_skill]

**4/4** · 66724 tokens · 10532ms

- ✅ **flags-junit-version**: Response explicitly flags that JUnit 5.10.0 is below the required minimum (5.11) and recommends upgrading
- ✅ **provides-correct-groupid**: Response provides the tabletest dependency with groupId 'org.tabletest' (not guessed)
- ✅ **provides-artifactid**: Response includes 'tabletest-junit' as the artifactId
- ✅ **includes-test-scope**: The suggested dependency includes <scope>test</scope>

### ✅ Eval eval-4-loan-approval [with_skill]

**6/6** · 42634 tokens · 32845ms

- ✅ **produces-markdown-table**: Output contains a markdown table (using | column | syntax)
- ✅ **output-column-has-question-mark**: At least one output column name ends with '?' (e.g. 'Eligible?' or 'Approved?')
- ✅ **business-language-columns**: Column names use domain/business language — no implementation terms like 'creditScore', 'isEligible', 'boolean', 'int'
- ✅ **senior-threshold-row**: Table includes at least one row specifically for the senior applicant (65+) threshold scenario
- ✅ **missing-income-marked-open**: The missing income scenario is represented — either as a row with an open/uncertain expected value (?, TBD, or blank) or as an explicit open question note
- ✅ **scenario-names-describe-conditions**: Scenario names describe conditions ('Senior applicant at threshold') not outcomes ('Approved')

### ✅ Eval eval-5-order-transitions [with_skill]

**5/5** · 43218 tokens · 43027ms

- ✅ **produces-markdown-table**: Output contains at least one markdown table
- ✅ **cancellation-coverage**: Table covers cancellation rules — includes rows for states where cancellation is allowed and where it is not
- ✅ **return-window-addressed**: The 30-day return window rule is addressed — either as a threshold column, a separate table, or flagged as an open question
- ✅ **value-set-or-multiple-states**: Uses value sets {PENDING, CONFIRMED} or equivalent to express 'regardless of which starting state' for cases where a rule holds across states
- ✅ **output-column-has-question-mark**: At least one output column ends with '?'

### ✅ Eval eval-6-discount-interaction [with_skill]

**5/5** · 41958 tokens · 28701ms

- ✅ **produces-markdown-table**: Output contains a markdown table
- ✅ **does-not-invent-resolution**: The output does NOT silently resolve the stacking/cap ambiguity — it leaves the conflicting cases as open questions, blank cells, or explicitly marks them as unresolved
- ✅ **covers-both-discounts-applying**: Table includes at least one row where both bulk and loyalty discounts apply simultaneously (exposing the interaction)
- ✅ **open-question-surfaced**: The unresolved decision (stacking vs. higher-only vs. cap) is explicitly surfaced — via an Open Questions column, a '?' cell, or a note
- ✅ **output-column-has-question-mark**: At least one output column ends with '?'

### ⚠️ Eval eval-7-permission-check [with_skill]

**9/10** · 42447 tokens · 20504ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ✅ **uses-value-sets**: Table uses value-set syntax (curly braces like {READ, WRITE} or {READ, WRITE, DELETE}) to express rules that hold across multiple actions — rather than one row per role/action combination
- ✅ **fewer-than-nine-rows**: Table has fewer than 9 data rows (i.e. does not enumerate every role/action combination individually)
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements
- ✅ **scenario-column-present**: Table has a scenario/description column as the leftmost column
- ✅ **no-duplicate-role-output**: No two rows share both the same Role value and the same boolean output — forces value set consolidation (e.g. USER can READ and USER can WRITE, both true, should be one row with {READ, WRITE}).
- ✅ **has-descriptive-title**: Test method has either a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case (e.g. 'permissionsByRoleAndAction' → 'Permissions By Role And Action'). Not a generic name like 'test1' or 'canPerform'.
- ❌ **description-adds-information**: @Description is present and provides context beyond what the table rows already express — such as where permissions are checked in the request lifecycle, or open questions. Does NOT merely restate the column names or summarise what the rows show.
  > @Description("ADMIN has full access; USER can read and write but not delete; GUEST is read-only") — this merely restates/summarises what the table rows already show (ADMIN has {READ,WRITE,DELETE}=true, USER has {READ,WRITE}=true and DELETE=false, GUEST has READ=true and {WRITE,DELETE}=false). It provides no context about where permissions are checked in the lifecycle, no open questions, and no information beyond the rows.
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **description-uses-textblock**: @Description text uses a text block (triple-quoted string """) when the text is long, not string concatenation with +.

### ⚠️ Eval eval-8-money-parse [with_skill]

**6/10** · 69128 tokens · 42591ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ❌ **null-as-blank-cell**: Null input is represented as a blank cell in the valid inputs @TableTest table (not the string 'null', and not as a separate @Test method). The blank cell maps to a null parameter.
  > @Test
void parse_returns_null_for_null_input() {
    assertNull(parse(null));
}
- ❌ **exception-has-expected-column**: The exception/invalid-input table has an expected column (e.g. 'Throws?' or 'Exception?') specifying the exception type per row — rather than hardcoding the exception type in @Description or the method name.
  > @TableTest("""
    Scenario       | Input
    Empty string   | ''
    Letters only   | abc
    Negative value | -5.00
    """)
- ✅ **exception-cases-handled**: The exception cases (empty string, letters-only, negative) are handled — via a Throws? column, assertThrows in the method body, or a separate @TableTest — not silently omitted
- ✅ **result-column-with-question-mark**: Valid inputs have a result column name ending with '?' (e.g. 'Money?' or 'Result?')
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements
- ✅ **has-descriptive-title**: Test method has either a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case (e.g. 'moneyParsing' → 'Money Parsing'). Not a generic name like 'test1' or 'testMethod'.
- ❌ **description-adds-information**: @Description is present and provides context beyond what the table rows already express — such as the expected currency format, or open questions. Does NOT merely restate the column names or summarise what the rows show.
  > @Description("Valid decimal strings are parsed into Money with the exact amount") ... @Description("Inputs that are empty, non-numeric, or negative are rejected with IllegalArgumentException")
- ❌ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
  > @TableTest("""
    ...
    """)
@Description("Valid decimal strings are parsed into Money with the exact amount")
void parse_returns_money_for_valid_input
- ✅ **description-uses-textblock**: @Description text uses a text block (triple-quoted string """) when the text is long, not string concatenation with +.

### ⚠️ Eval eval-9-bonus-contractor-structure [with_skill]

**6/8** · 42615 tokens · 23983ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ✅ **contractor-uses-value-set**: CONTRACTOR row uses a value set for Department (e.g. {SALES, ENGINEERING}) to express 'regardless of department' — not a dummy placeholder like 'ANY' or 'N/A', and not enumerated as separate rows.
- ✅ **four-core-rules-covered**: The four level×department combinations (SENIOR+SALES, SENIOR+ENGINEERING, JUNIOR+SALES, JUNIOR+ENGINEERING) are all present in the table(s)
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements
- ✅ **has-descriptive-title**: Test method has either a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case (e.g. 'bonusByLevelAndDepartment' → 'Bonus By Level And Department'). Not a generic name like 'test1' or 'testMethod'.
- ❌ **description-adds-information**: @Description is present and provides context beyond what the table rows already express — such as how bonus rates are applied (e.g. as percentage of base salary), or open questions. Does NOT merely restate the column names or summarise what the rows show.
  > @Description("Bonus rates by employee level and department. " + "CONTRACTOR always receives 0% regardless of department.") — the first sentence summarises the table's content and the second restates what the CONTRACTOR row already shows; no context is added about how rates are applied (e.g. as a percentage of base salary) or any open questions.
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ❌ **description-uses-textblock**: @Description text uses a text block (triple-quoted string """) when the text is long, not string concatenation with +.
  > @Description("Bonus rates by employee level and department. "
    + "CONTRACTOR always receives 0% regardless of department.") — uses string concatenation with + instead of a triple-quoted text block.

### ✅ Eval eval-10-subscription-billing [with_skill]

**5/5** · 44630 tokens · 68056ms

- ✅ **produces-multiple-tables**: Output contains at least 2 separate markdown tables (not everything crammed into one table)
- ✅ **tables-have-distinct-concerns**: The tables cover distinct concerns — e.g. pricing/plans is separate from cancellation/refund rules
- ✅ **output-columns-have-question-marks**: At least one output column in each table ends with '?'
- ✅ **refund-exception-covered**: The 24-hour no-refund exception is included as a distinct scenario row
- ✅ **business-language-columns**: Column names use domain/business language — no implementation terms like 'boolean', 'int', 'isProrated', camelCase identifiers

### ✅ Eval eval-12-subscription-loyalty-trial [with_skill]

**5/5** · 44170 tokens · 62416ms

- ✅ **produces-multiple-tables**: Output contains at least 2 separate markdown tables
- ✅ **annual-no-trial-rule-represented**: A table or note explicitly captures that the free trial is not available on annual plans (not just implied)
- ✅ **loyalty-discount-row-present**: At least one table row demonstrates the loyalty programme discount scenario for annual plan subscribers
- ✅ **surfaces-genuine-open-questions**: The response identifies at least one genuinely underspecified interaction — such as: what happens when a trial subscriber upgrades to annual mid-trial, when a subscriber joins the loyalty programme mid-billing-cycle, or whether a cancellation refund is based on the discounted or full price. These are NOT stated in the rules and require a product decision.
- ✅ **open-question-surfaced**: The annual/loyalty/trial interaction is explicitly surfaced as an open question — via '?', 'TBD', blank expected value, or a written open question — rather than silently resolved

### ✅ Eval eval-13-shipping-partial-applicability [with_skill]

**6/6** · 44672 tokens · 61174ms

- ✅ **produces-markdown-table**: Output contains a markdown table
- ✅ **express-split-by-uk-free-tier**: Express shipping is split into at least two cases distinguishing the UK free tier (UK, order £50+, free) from the paid tier. The split is driven by the UK-only free threshold rule.
- ✅ **express-uses-value-sets**: Express rows use value sets to consolidate cases with the same outcome — e.g. non-UK destinations at/above threshold in one row (such as {Ireland, Other} or {Ireland, International}), or UK at/above threshold in one row ({£50, £75}). Not enumerated as separate rows per destination or per order value when the result is identical. Any reasonable label for non-UK/Ireland destinations is acceptable (International, Other, Rest of World, etc.).
- ✅ **overnight-grouped**: Overnight UK and Ireland are in one row using a value set ({UK, Ireland}) — not separate rows, since both have the same cost and availability.
- ✅ **standard-destination-value-set-or-blank**: Standard shipping destination uses a value set covering all options (e.g. {UK, Ireland, International} or {UK, Ireland, Other}) or is blank/absent — either approach is valid since destination is irrelevant to the cost. Any reasonable label for non-UK/Ireland destinations is acceptable.
- ✅ **output-column-has-question-mark**: At least one output column ends with '?'

### ⚠️ Eval eval-14-weekly-pay [with_skill]

**8/13** · 177867 tokens · 170235ms

- ❌ **1.1-traceability-columns**: The pay table includes intermediate expected columns (e.g. Regular pay?, Overtime pay?, Premium pay?) so the reader can trace which rule contributes what — not just a single total Weekly pay? column.
  > The table header is: `Scenario | Weekday hours | Sunday hours | Holiday hours | Rate | Weekly pay?` — there is only a single `Weekly pay?` output column; no intermediate columns like `Regular pay?`, `Overtime pay?`, or `Premium pay?` are present.
- ❌ **1.2-error-has-expected-column**: The error/rejection table has an expected column (e.g. 'Throws?' or 'Exception?') specifying the exception type per row — rather than hardcoding the exception type in @Description or the method name.
  > The error table header is `Scenario | Rate` with no expected exception column. The exception type is hardcoded in the method body: `assertThrows(IllegalArgumentException.class, () -> calculator.calculate(0, 0, 0, rate));`
- ✅ **1.3-depth-overtime-boundary**: Overtime threshold boundary is covered: at least a value at the threshold (40) and just above (41 or similar). Not just values well below and well above.
- ✅ **1.4-depth-combined-scenario**: A combined scenario is present: a row with weekday + Sunday + holiday hours, testing the composition of all hour types.
- ❌ **1.5-depth-error-edge-cases**: Error/edge cases covered: at least negative hours (floored at zero) and negative or missing rate (rejected).
  > Only negative rate is tested (`Negative rate | {-10.00, -1.00, -0.01}`). There is no test for negative hours. The design notes explicitly explain this omission: 'The zero floor is covered by @Description rather than a dedicated row, since valid non-negative inputs can never produce negative pay under these rules — no dedicated test scenario is possible without introducing negative hours, which the spec doesn't define.'
- ❌ **1.6-readability-empty-cells**: Sunday and Holiday hour columns use empty cells (not 0) when those inputs are not relevant to a scenario — making it easier to spot which rows involve weekend/holiday hours.
  > Rows like `Standard 40-hour week | 40 | 0 | 0 | 10.00 | 400.00` and `One overtime hour | 41 | 0 | 0 | 10.00 | 415.00` use `0` (not blank) in the Sunday and Holiday columns when those hour types are not relevant to the scenario.
- ✅ **1.7-readability-scenario-names**: Scenario names describe work patterns (e.g. 'Part-time', 'Five hours overtime', 'Full Sunday shift') — not 'Test case 1' or expected outcomes.
- ✅ **1.8-correctness-expected-values**: Expected values in intermediate and total columns are arithmetically correct for every row, consistent with the formulas in @Description.
- ✅ **1.9-correctness-value-set-semantics**: Value sets are only used where all values in the set produce the same expected result (e.g. any rate × 0 = 0). Not used where results would differ.
- ✅ **1.10-format-displayname**: @DisplayName is present on test methods, OR method names read as clear descriptive titles when converted from camelCase/snake_case.
- ✅ **1.11-format-description**: @Description is present on at least the payable hours method and provides context beyond what the table rows already express — such as the conversion rules, fixed values, or application context. Does NOT merely restate the column names or summarise what the rows show.
- ✅ **1.12-format-annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ❌ **1.13-format-description-textblock**: @Description text uses a text block (triple-quoted string """) when the text is long, not string concatenation with +.
  > The @Description on `calculatesWeeklyPay` uses string concatenation: `"Weekly pay = (weekday hours up to 40) × rate" + " + (weekday hours beyond 40) × 1.5 × rate  [overtime]" + " + sunday hours × 2 × rate                  [double time]" + " + holiday hours × 2 × rate                 [double time]." + " Total pay is floored at zero."` — this is multiple strings joined with `+`, not a triple-quoted text block.

### ⚠️ Eval eval-15-reis-discount [with_skill]

**6/11** · 174884 tokens · 210382ms

- ❌ **2.1-decomposition-concern-separation**: Discount ladder, and traveller eligibility are in separate tables. Rolling window counting as a third table is desirable.
  > The response has three tables: `reisDiscountLadderForAdultsAndSeniors` (discount ladder), `adultTicketPriceReflectsReisDiscount` (price calculation), and `childrenAlwaysReceiveFlatTwentyPercentDiscount` (children's flat discount). There is no dedicated traveller eligibility table comparing which passenger types get which discount type. There is also no rolling window counting table with boundary testing — the 30-day window appears only as a column header in the ladder tables.
- ✅ **2.2-children-flat-discount**: Children's flat 20% discount is represented — either as a row in the eligibility table or a separate note. It does not follow the ladder.
- ✅ **2.3-depth-tier-boundaries**: The discount ladder has multiple tiers derived from the 'every fifth trip' rule, with boundary values or value sets showing which ticket counts produce which discount. The maximum 40% discount is represented.
- ❌ **2.4-depth-rolling-window-boundary**: The 30-day rolling window boundary is tested: a ticket at exactly 30 days is included, at 31 days is excluded.
  > No table tests the rolling window boundary. The phrase "Tickets in last 30 days" appears as a column header, but there is no test case that distinguishes a ticket at exactly 30 days (included) vs. 31 days (excluded). The rolling window semantics are not tested anywhere in the response.
- ❌ **2.6-readability-human-readable-values**: Eligibility uses human-readable values (yes/no or equivalent) rather than raw true/false. TypeConverter or similar mechanism mentioned.
  > There is no eligibility table using yes/no values. Passenger types use enum values (ADULT, SENIOR, CHILD). No TypeConverter or TypeConverterSources is mentioned anywhere in the response.
- ✅ **2.9-correctness-value-set-tier-semantics**: Value sets in the tier table contain exactly the values that produce the same discount. No cross-tier contamination.
- ✅ **2.10-format-displayname**: @DisplayName is present on test methods, OR method names read as clear descriptive titles when converted from camelCase/snake_case.
- ✅ **2.11-format-description**: @Description is present on at least the class or the tier method and provides context beyond what the table rows already express — such as the discount ladder structure, eligibility rules, or application context. Does NOT merely restate the column names or summarise what the rows show.
- ❌ **2.12-format-typeconverter**: TypeConverterSources is used for at least one human-readable conversion (yes/no, or rolling window notation).
  > No @TypeConverterSources annotation appears anywhere in the response. No TypeConverter class is referenced or imported in the code.
- ❌ **2.13-format-annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
  > The annotations appear in the wrong order: `@TableTest("""..""")` is placed BEFORE `@Description("""..""")` on all three methods, e.g., `@TableTest("""...""")
    @Description("""...""")
    void reisDiscountLadderForAdultsAndSeniors`. The required order is @Description then @TableTest.
- ✅ **2.14-format-description-textblock**: @Description text uses a text block (triple-quoted string """) when the text is long, not string concatenation with +.

### ⚠️ Eval eval-16-order-splitting [with_skill]

**7/13** · 49602 tokens · 137417ms

- ✅ **3.1-decomposition-concern-separation**: Trigger detection, shipment optimisation, and companion constraint are in separate tables. Not one monolithic table.
- ✅ **3.2-decomposition-trigger-interactions**: At least one row where multiple triggers fire simultaneously (e.g. mixed fulfilment AND mixed stock status).
- ✅ **3.3-depth-all-four-triggers**: All four split triggers are covered: mixed fulfilment, mixed stock status, no single warehouse has all items, different addresses.
- ❌ **3.4-depth-companion-ambiguity-surfaced**: The 'no warehouse has both companions' case is flagged as an open question or product decision, not silently resolved as either 'error' or 'ship separately'. The requirement does NOT hint at this — the agent must recognise the ambiguity unprompted.
  > The 'No warehouse stocks both companions' row resolves the case definitively: 'no — unavoidable | 2 | Chicago (body) + LA (lens)', treating it as a settled outcome (ship separately) rather than flagging it as an open question or product decision.
- ❌ **3.5-depth-companion-edge-cases**: Companion constraint covers at least: companions at same location, companions split from non-companion items, and the ambiguous case (no location has both).
  > Table 3 covers 'Both companions at same warehouse' (same location) and 'No warehouse stocks both companions' (ambiguous case), but no row shows companions split from non-companion items — every row in Table 3 contains only the two companion items with no additional non-companion items in the order.
- ✅ **3.6-readability-scenario-names**: Optimisation scenarios describe the business situation (e.g. 'Consolidate: two beats three', 'Everything at one warehouse') — not 'Test case 2'.
- ✅ **3.7-readability-set-notation**: Trigger table uses set notation ({delivery, pickup}) for closed enumerations rather than prose or lists.
- ✅ **3.8-readability-scalar-column**: Optimisation table uses a scalar count column (e.g. 'Shirts ordered') not item lists, when product identity doesn't matter for the logic.
- ✅ **3.9-correctness-valid-assignments**: Inventory assignments are physically valid: items are only assigned to locations that have them. Shipment counts match the assignments shown.
- ❌ **3.10-format-displayname**: @DisplayName is present on test methods, OR method names read as clear descriptive titles when converted from camelCase/snake_case.
  > The response contains no Java/Kotlin code, no test methods, and no annotations of any kind — it is purely tables and prose with no @DisplayName or method names to evaluate.
- ❌ **3.11-format-description**: @Description is present on at least the class and provides context beyond what the table rows already express — such as the splitting rules, constraints, or application context. Does NOT merely restate the column names or summarise what the rows show.
  > The response contains no Java/Kotlin code and no @Description annotation. No class-level annotation or code structure is present anywhere in the response.
- ❌ **3.12-format-annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
  > No annotations of any kind are present in the response. The response contains only Markdown tables and prose, with no Java/Kotlin code.
- ❌ **3.13-format-description-textblock**: @Description text uses a text block (triple-quoted string """) when the text is long, not string concatenation with +.
  > No @Description annotation or any Java/Kotlin code is present in the response. There is no text block or string concatenation to evaluate.

### ⚠️ Eval eval-17-shopping-cart [with_skill]

**12/13** · 45332 tokens · 69756ms

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
  > The Coupon Application table has no cart contents column. The Cart Total table separates 'Product discount — product is in cart' vs 'Product discount — product not in cart' by scenario name only, not by a visible cart contents column. The distinction is implied by the scenario name, not driven by explicit data.
- ✅ **4.14-coupon-before-after-columns**: Coupon entry scenarios use before/after columns (e.g. 'Coupon before' / 'Coupon after?' or 'Active coupon' / 'Active coupon after?') to specify the state change when entering, replacing, or entering invalid coupon codes.

