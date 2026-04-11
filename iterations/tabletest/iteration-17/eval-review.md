# Eval Review — Iteration 17

**Model:** sonnet · **Date:** 2026-03-30 · **Evals:** 2

## Summary

**with_skill:** 16/33 (48.5%) · 733130 tokens · 540.8s · $0.9276

## Delta vs Iteration 16

**Regressions (7):**
- ❌ eval-15-reis-discount: `2.9-correctness-value-set-tier-semantics`
- ❌ eval-15-reis-discount: `2.10-format-displayname`
- ❌ eval-15-reis-discount: `2.11-format-description`
- ❌ eval-15-reis-discount: `2.13-format-annotation-order`
- ❌ eval-15-reis-discount: `2.15-ticket-count-uses-value-sets`
- ❌ eval-15-reis-discount: `2.16-no-duplicate-tier-mapping`
- ❌ eval-15-reis-discount: `2.20-readability-one-row-per-tier`

**Improvements (5):**
- ✅ eval-14-weekly-pay: `1.2-error-has-expected-column`
- ✅ eval-14-weekly-pay: `1.6-readability-empty-cells`
- ✅ eval-14-weekly-pay: `1.10-format-displayname`
- ✅ eval-14-weekly-pay: `1.11-format-description`
- ✅ eval-14-weekly-pay: `1.12-format-annotation-order`

## Resource Comparison vs Iteration 16

| Eval | Pass Rate | Prev | Tokens | Prev | Time(s) | Prev |
|------|-----------|------|--------|------|---------|------|
| eval-14-weekly-pay | 12/15 | 7/15 | 661342 | 69310 | 440.3 | 72.5 |
| eval-15-reis-discount | 4/18 | 11/18 | 71788 | 277022 | 100.5 | 256.9 |

## Per-Eval Results

### ⚠️ Eval eval-14-weekly-pay [with_skill]

**12/15** · 661342 tokens · 440325ms

- ❌ **1.1-traceability-columns**: The pay table includes intermediate expected columns (e.g. Regular pay?, Overtime pay?, Premium pay?) so the reader can trace which rule contributes what — not just a single total Weekly pay? column.
  > The table has only these columns: Weekday hrs, Sunday hrs, Holiday hrs, Rate, and Weekly pay? — there are no intermediate expected columns like Regular pay?, Overtime pay?, Premium pay?, or Sunday pay?.
- ✅ **1.2-error-has-expected-column**: The error/rejection table has an expected column (e.g. 'Throws?' or 'Exception?') specifying the exception type per row — rather than hardcoding the exception type in @Description or the method name.
- ✅ **1.3-depth-overtime-boundary**: Overtime threshold boundary is covered: at least a value at the threshold (40) and just above (41 or similar). Not just values well below and well above.
- ✅ **1.4-depth-combined-scenario**: A combined scenario is present: a row with weekday + Sunday + holiday hours, testing the composition of all hour types.
- ❌ **1.5-depth-error-edge-cases**: Error/edge cases covered: at least negative hours (floored at zero) and negative or missing rate (rejected).
  > The pay table contains no rows with negative hour values to verify floor behavior; the error table tests only negative rate rejection, not missing/null rate rejection.
- ✅ **1.6-readability-empty-cells**: Sunday and Holiday hour columns use empty cells (not 0) when those inputs are not relevant to a scenario — making it easier to spot which rows involve weekend/holiday hours. This is a readability preference; parameter types should be Integer (not int) to support null from blank cells.
- ✅ **1.7-readability-scenario-names**: Scenario names describe work patterns (e.g. 'Part-time', 'Five hours overtime', 'Full Sunday shift') — not 'Test case 1' or expected outcomes.
- ✅ **1.8-correctness-expected-values**: Expected values in intermediate and total columns are arithmetically correct for every row, consistent with the formulas in @Description.
- ✅ **1.9-correctness-value-set-semantics**: Value sets are only used where all values in the set produce the same expected result (e.g. any rate × 0 = 0). Not used where results would differ.
- ✅ **1.10-format-displayname**: @DisplayName is present on test methods, OR method names read as clear descriptive titles when converted from camelCase/snake_case.
- ✅ **1.11-format-description**: @Description is present on at least the pay calculation method and provides context beyond what the table rows already express — such as the overtime threshold (40 hours), premium multipliers (Sunday 2×, Holiday 2.5×), or other fixed assumptions not visible in the table. Does NOT merely restate the column names or summarise what the rows show.
- ✅ **1.12-format-annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **1.13-format-description-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +. Passes if @Description is absent.
- ✅ **1.14-depth-zero-rate**: A row where the hourly rate is zero, showing that pay is zero regardless of hours worked. This is a distinct edge case from negative rate (which is rejected).
- ❌ **1.15-format-clean-method**: The test method body contains no if/ternary null-handling or parsing logic. Null-to-default conversion (e.g. blank Sunday hours → 0) is handled via a @TypeConverter method or a private helper, keeping the test method limited to arrange/act/assert.
  > The method body contains ternary null-handling operators: 'int weekday = weekdayHrs != null ? weekdayHrs : 0;' and 'int sunday = sundayHrs != null ? sundayHrs : 0;' and 'int holiday = holidayHrs != null ? holidayHrs : 0;' — these should be in a @TypeConverter or helper method instead.

### ⚠️ Eval eval-15-reis-discount [with_skill]

**4/18** · 71788 tokens · 100461ms

- ❌ **2.1-decomposition-concern-separation**: Discount ladder, and traveller eligibility are in separate tables. Rolling window counting as a third table is desirable.
  > The response provides three tables: 'Reis Discount Level', 'Discount by Passenger Type', and 'Final Ticket Price'. While discount ladder and eligibility are separated, the third table is for price arithmetic ('A third small table verifies the final price arithmetic') rather than rolling window boundary testing as expected ('30-day rolling window counting (with boundary testing)').
- ✅ **2.2-children-flat-discount**: Children's flat 20% discount is represented — either as a row in the eligibility table or a separate note. It does not follow the ladder.
- ✅ **2.3-depth-tier-boundaries**: The discount ladder has multiple tiers derived from the 'every fifth trip' rule, with boundary values or value sets showing which ticket counts produce which discount. The maximum 40% discount is represented.
- ❌ **2.4-depth-rolling-window-boundary**: The 30-day rolling window boundary is tested: a ticket at exactly 30 days is included, at 31 days is excluded.
  > Table 1 shows trip counts (1, 4, 5, etc.) but does not test time-based rolling window boundaries (e.g., 'ticket purchased 30 days ago' vs '31 days ago'). The response has no dedicated rolling window table with relative time testing.
- ❌ **2.6-readability-human-readable-values**: Eligibility uses human-readable values (yes/no or equivalent) rather than raw true/false. TypeConverter or similar mechanism mentioned.
  > Table 2 uses human-readable values like 'Reis (earned)', 'Flat', and percentages, but the response contains no mention of 'TypeConverter' or any similar mechanism for achieving these conversions.
- ❌ **2.9-correctness-value-set-tier-semantics**: Value sets in the tier table contain exactly the values that produce the same discount. No cross-tier contamination.
  > Table 1 lists individual trip counts per row ('5 → 5%', '9 → 5%') rather than grouping them in value sets (e.g., '{5, 6, 7, 8, 9} → 5%' or '{5, 9} → 5%'). Each tier is split across multiple rows without value set notation.
- ❌ **2.10-format-displayname**: @DisplayName is present on test methods, OR method names read as clear descriptive titles when converted from camelCase/snake_case.
  > The response shows only markdown tables with scenario names ('First trip', 'Below first step'), not actual test method code. No @DisplayName annotations or Java method names are shown, making it impossible to verify this format requirement.
- ❌ **2.11-format-description**: @Description is present on at least the class or the tier method and provides context beyond what the table rows already express — such as the discount ladder structure, eligibility rules, or application context. Does NOT merely restate the column names or summarise what the rows show.
  > The response contains no @Description annotations. While there is contextual text (open questions, notes about zones), the response does not show test code with @Description annotations as required.
- ❌ **2.12-format-typeconverter**: TypeConverterSources is used for at least one human-readable conversion (yes/no, or rolling window notation).
  > The word 'TypeConverter' does not appear anywhere in the response. The response provides tables but no mention of TypeConverterSources or implementation of human-readable conversions via this mechanism.
- ❌ **2.13-format-annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
  > No annotations are shown in the response. The response displays markdown tables rather than Java test code with annotations, making it impossible to verify annotation order.
- ✅ **2.14-format-description-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +. Passes if @Description is absent.
- ❌ **2.15-ticket-count-uses-value-sets**: The discount ladder table uses value sets for the ticket count column to group counts within the same tier (e.g. {5, 6, 7, 8, 9} or {5, 9} for min/max → 5%) — not one row per boundary value. The 'every fifth trip' rule creates tiers; the table should express tiers, not enumerate boundaries.
  > Table 1 enumerates boundary values with one row per value: '5 → 5%', '9 → 5%', '10 → 10%', etc. The assertion explicitly forbids 'one row per boundary value', but this is exactly how Table 1 is structured. No value sets like '{5, 6, 7, 8, 9} → 5%' are used.
- ❌ **2.16-no-duplicate-tier-mapping**: The tier-to-discount mapping is expressed once — not repeated across multiple tables. A price application table (if present) should test the arithmetic (base price × discount → final price) without re-enumerating which ticket counts map to which tier.
  > Table 1 shows '5 → 5%', '10 → 10%', '40 → 40%'. Table 2 repeats this mapping with rows like 'Adult at first step | Adult | 5 | Reis (earned) | 5%' and 'Adult | 10 | ... → 10%'. The tier-to-discount relationship is enumerated in both Table 1 and Table 2.
- ❌ **2.17-zone-irrelevance-visible**: The eligibility/passenger-type table includes a Zone column (or equivalent) with a value set to show that the discount applies regardless of zone — rather than silently omitting zone. The requirement explicitly says 'regardless of the zones you travel in'.
  > Table 2 does not include a Zone column. The response explicitly states: 'Zone is not a column because it never affects the outcome. A single integration row confirming this may be worth adding once zone data is available in the model.' This violates the requirement to make zone irrelevance visible via a value set.
- ❌ **2.18-adult-senior-value-set**: Adult and senior are expressed as a value set {ADULT, SENIOR} (or equivalent) in at least one row of the eligibility table, since they follow identical discount rules. Not enumerated as separate rows with the same outcome.
  > Table 2 has separate rows: 'Adult below threshold | Adult | 4 | Reis (earned) | 0%' and 'Senior below threshold | Senior | 4 | Reis (earned) | 0%'. These are enumerated as separate rows instead of combined as a value set '{ADULT, SENIOR}' in a single row with identical outcomes.
- ✅ **2.19-depth-all-tiers**: All 9 discount tiers are represented in the ladder table: 0% (tickets 1-4), 5% (5-9), 10% (10-14), 15% (15-19), 20% (20-24), 25% (25-29), 30% (30-34), 35% (35-39), and 40% (40+). Not just a subset of tiers.
- ❌ **2.20-readability-one-row-per-tier**: Each tier in the discount ladder is expressed as a single row with a value set for ticket counts — not split across multiple rows (e.g. separate rows for 'tier kicks in' and 'tier holds'). One tier = one row.
  > Table 1 splits each tier across multiple rows. For example, the 5% tier is shown as 'First discount step | 5 | 5%' and 'Within first step | 9 | 5%'—two rows for the same tier. This violates the requirement of 'One tier = one row'.
- ❌ **2.21-readability-relative-time**: The rolling window table expresses time relatively (e.g. '14 days ago', '30 days ago', or an integer 'Days ago' column) rather than using absolute dates, making the table readable without needing to know the reference date.
  > There is no rolling window table in the response. The three tables are: Reis Discount Level, Discount by Passenger Type, and Final Ticket Price. No table tests rolling window boundaries with relative time notation.

