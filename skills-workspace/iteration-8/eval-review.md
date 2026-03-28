# Eval Review — Iteration 8

**Model:** sonnet · **Date:** 2026-03-28 · **Evals:** 2

## Summary

**with_skill:** 15/26 (57.7%) · 384560 tokens · 269.7s · $0.5824

## Delta vs Iteration 7

**Regressions (3):**
- ❌ eval-14-weekly-pay: `1.10-format-displayname`
- ❌ eval-14-weekly-pay: `1.12-format-annotation-order`
- ❌ eval-15-reis-discount: `2.9-correctness-value-set-tier-semantics`

**Improvements (6):**
- ✅ eval-14-weekly-pay: `1.2-error-has-expected-column`
- ✅ eval-14-weekly-pay: `1.3-depth-overtime-boundary`
- ✅ eval-14-weekly-pay: `1.6-readability-empty-cells`
- ✅ eval-14-weekly-pay: `1.8-correctness-expected-values`
- ✅ eval-14-weekly-pay: `1.13-format-description-textblock`
- ✅ eval-15-reis-discount: `2.14-format-description-textblock`

## Per-Eval Results

### ⚠️ Eval eval-14-weekly-pay [with_skill]

**8/13** · 41927 tokens · 56303ms

- ❌ **1.1-traceability-columns**: The pay table includes intermediate expected columns (e.g. Regular pay?, Overtime pay?, Premium pay?) so the reader can trace which rule contributes what — not just a single total Weekly pay? column.
  > The pay table contains only: Scenario, Weekday Hours, Sunday Hours, Holiday Hours, Hourly Rate, and Weekly Pay?. There are no intermediate columns like 'Regular pay?', 'Overtime pay?', 'Sunday pay?', or 'Holiday pay?' in the table itself. The intermediate calculations appear in a separate 'Traceability:' section outside the table, not as columns within it.
- ✅ **1.2-error-has-expected-column**: The error/rejection table has an expected column (e.g. 'Throws?' or 'Exception?') specifying the exception type per row — rather than hardcoding the exception type in @Description or the method name.
- ✅ **1.3-depth-overtime-boundary**: Overtime threshold boundary is covered: at least a value at the threshold (40) and just above (41 or similar). Not just values well below and well above.
- ✅ **1.4-depth-combined-scenario**: A combined scenario is present: a row with weekday + Sunday + holiday hours, testing the composition of all hour types.
- ❌ **1.5-depth-error-edge-cases**: Error/edge cases covered: at least negative hours (floored at zero) and negative or missing rate (rejected).
  > The error table covers negative rate rejection ('Negative hourly rate is rejected | -$10 | Negative rate not allowed'). However, there is no test case for negative hours being floored to zero. The first row tests 0 hours, but no row explicitly tests negative weekday, Sunday, or Holiday hours.
- ✅ **1.6-readability-empty-cells**: Sunday and Holiday hour columns use empty cells (not 0) when those inputs are not relevant to a scenario — making it easier to spot which rows involve weekend/holiday hours.
- ✅ **1.7-readability-scenario-names**: Scenario names describe work patterns (e.g. 'Part-time', 'Five hours overtime', 'Full Sunday shift') — not 'Test case 1' or expected outcomes.
- ✅ **1.8-correctness-expected-values**: Expected values in intermediate and total columns are arithmetically correct for every row, consistent with the formulas in @Description.
- ✅ **1.9-correctness-value-set-semantics**: Value sets are only used where all values in the set produce the same expected result (e.g. any rate × 0 = 0). Not used where results would differ.
- ❌ **1.10-format-displayname**: @DisplayName is present on test methods, OR method names read as clear descriptive titles when converted from camelCase/snake_case.
  > No @DisplayName annotations are present, and no test method code is shown at all. The response provides specification tables but not the actual Java test method definitions that would require @DisplayName or descriptive method names.
- ❌ **1.11-format-description**: @Description is present on at least the pay calculation method and provides context beyond what the table rows already express — such as the overtime threshold (40 hours), premium multipliers (Sunday 2×, Holiday 2.5×), or other fixed assumptions not visible in the table.
  > No @Description annotation is shown in the response. While the rules are implied through the traceability breakdowns (e.g., Sunday 2×, Holiday 2.5×, overtime at 40 hours), there is no formal @Description annotation present on any test method.
- ❌ **1.12-format-annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
  > No @TableTest method is shown. The response provides specification tables but not the actual test method code with annotation ordering.
- ✅ **1.13-format-description-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +. Passes if @Description is absent.

### ⚠️ Eval eval-15-reis-discount [with_skill]

**7/13** · 342633 tokens · 213407ms

- ❌ **2.1-decomposition-concern-separation**: Discount ladder, and traveller eligibility are in separate tables. Rolling window counting as a third table is desirable.
  > Response provides only 2 @TableTest methods: childAlwaysReceivesFlatTwentyPercentDiscount and adultAndSeniorAccumulateDiscountByRecentTicketCount. The expected output specifies 3 separate tables for discount ladder, traveller eligibility, and 30-day rolling window counting; the response combines discount ladder with traveller eligibility in the adult/senior table and omits a dedicated rolling window counting table.
- ✅ **2.2-children-flat-discount**: Children's flat 20% discount is represented — either as a row in the eligibility table or a separate note. It does not follow the ladder.
- ✅ **2.3-depth-tier-boundaries**: The discount ladder has multiple tiers derived from the 'every fifth trip' rule, with boundary values or value sets showing which ticket counts produce which discount. The maximum 40% discount is represented.
- ❌ **2.4-depth-rolling-window-boundary**: The 30-day rolling window boundary is tested: a ticket at exactly 30 days is included, at 31 days is excluded.
  > The adult/senior table tests different recentTickets counts (0, 4, 5, 10, ..., 50) but contains no test cases explicitly checking the 30-day time boundary (e.g., a ticket at exactly 30 days old vs. 31 days old). The description mentions '30 days' but no test row isolates this boundary.
- ❌ **2.6-readability-human-readable-values**: Eligibility uses human-readable values (yes/no or equivalent) rather than raw true/false. TypeConverter or similar mechanism mentioned.
  > While tables use human-readable values (CHILD, ADULT, SENIOR, and discount percentages), TypeConverter is not mentioned anywhere in the code, annotations, design decisions, or dependencies section.
- ❌ **2.9-correctness-value-set-tier-semantics**: Value sets in the tier table contain exactly the values that produce the same discount. No cross-tier contamination.
  > The adult/senior table uses individual boundary rows (4→0%, 5→5%, 9→5%, 10→10%, etc.) rather than value sets grouping counts per tier. The design decisions explicitly state: 'Adult/Senior table pins every boundary pair.'
- ✅ **2.10-format-displayname**: @DisplayName is present on test methods, OR method names read as clear descriptive titles when converted from camelCase/snake_case.
- ✅ **2.11-format-description**: @Description is present on at least the class or the tier method and provides context beyond what the table rows already express — such as the discount ladder structure, eligibility rules, or application context.
- ❌ **2.12-format-typeconverter**: TypeConverterSources is used for at least one human-readable conversion (yes/no, or rolling window notation).
  > No TypeConverterSources annotation or TypeConverter import appears in the code. Tables display raw enum values (CHILD, ADULT, SENIOR) and plain integer percentages without converter mechanism.
- ✅ **2.13-format-annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **2.14-format-description-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +. Passes if @Description is absent.
- ❌ **2.15-ticket-count-uses-value-sets**: The discount ladder table uses value sets for the ticket count column to group counts within the same tier (e.g. {5, 6, 7, 8, 9} or {5, 9} for min/max → 5%) — not one row per boundary value.
  > The adult/senior discount ladder table uses individual rows for each boundary: 'Just below first threshold | {ADULT, SENIOR} | 4 | 0', 'First tier — ticket 5 | {ADULT, SENIOR} | 5 | 5', 'Just below second threshold | {ADULT, SENIOR} | 9 | 5', etc. Design decisions state: 'Adult/Senior table pins every boundary pair.'
- ✅ **2.16-no-duplicate-tier-mapping**: The tier-to-discount mapping is expressed once — not repeated across multiple tables. A price application table (if present) should test the arithmetic (base price × discount → final price) without re-enumerating which ticket counts map to which tier.

