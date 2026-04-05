# Eval Review — Iteration 16

**Model:** sonnet · **Date:** 2026-03-30 · **Evals:** 2

## Summary

**with_skill:** 18/33 (54.5%) · 346332 tokens · 329.4s · $0.8003

## Delta vs Iteration 15

**Regressions (3):**
- ❌ eval-14-weekly-pay: `1.6-readability-empty-cells`
- ❌ eval-15-reis-discount: `2.1-decomposition-concern-separation`
- ❌ eval-15-reis-discount: `2.21-readability-relative-time`

**Improvements (7):**
- ✅ eval-15-reis-discount: `2.3-depth-tier-boundaries`
- ✅ eval-15-reis-discount: `2.9-correctness-value-set-tier-semantics`
- ✅ eval-15-reis-discount: `2.15-ticket-count-uses-value-sets`
- ✅ eval-15-reis-discount: `2.10-format-displayname`
- ✅ eval-15-reis-discount: `2.11-format-description`
- ✅ eval-15-reis-discount: `2.13-format-annotation-order`
- ✅ eval-15-reis-discount: `2.20-readability-one-row-per-tier`

## Resource Comparison vs Iteration 15

| Eval | Pass Rate | Prev | Tokens | Prev | Time(s) | Prev |
|------|-----------|------|--------|------|---------|------|
| eval-14-weekly-pay | 7/15 | 8/15 | 69310 | 67102 | 72.5 | 40.4 |
| eval-15-reis-discount | 11/18 | 6/18 | 277022 | 68447 | 256.9 | 64.0 |

## Per-Eval Results

### ⚠️ Eval eval-14-weekly-pay [with_skill]

**7/15** · 69310 tokens · 72537ms

- ❌ **1.1-traceability-columns**: The pay table includes intermediate expected columns (e.g. Regular pay?, Overtime pay?, Premium pay?) so the reader can trace which rule contributes what — not just a single total Weekly pay? column.
  > The table contains only: 'Scenario | Weekday Hours | Sunday Hours | Holiday Hours | Hourly Rate | Overtime Threshold | Weekly Pay? | Error?' The expected output column is a single 'Weekly Pay?' with no intermediate columns like 'Regular pay?', 'Overtime pay?', or 'Premium pay?'. The calculations are shown in explanatory text below the table, not as table columns.
- ❌ **1.2-error-has-expected-column**: The error/rejection table has an expected column (e.g. 'Throws?' or 'Exception?') specifying the exception type per row — rather than hardcoding the exception type in @Description or the method name.
  > The 'Error?' column contains only a message string: 'Negative rate not allowed' for the error case. No exception type (such as 'IllegalArgumentException' or 'ValidationException') is specified per row.
- ✅ **1.3-depth-overtime-boundary**: Overtime threshold boundary is covered: at least a value at the threshold (40) and just above (41 or similar). Not just values well below and well above.
- ✅ **1.4-depth-combined-scenario**: A combined scenario is present: a row with weekday + Sunday + holiday hours, testing the composition of all hour types.
- ❌ **1.5-depth-error-edge-cases**: Error/edge cases covered: at least negative hours (floored at zero) and negative or missing rate (rejected).
  > The table includes 'Negative hourly rate' with -$5.00 rejected, but contains no scenario testing negative hours being floored at zero. The 'No hours worked | 0 | 0 | 0' row tests zero hours, not negative hours. The design notes section acknowledges: 'Can hours be negative? — the spec rejects negative rates but says nothing about negative hours.'
- ❌ **1.6-readability-empty-cells**: Sunday and Holiday hour columns use empty cells (not 0) when those inputs are not relevant to a scenario — making it easier to spot which rows involve weekend/holiday hours. This is a readability preference; parameter types should be Integer (not int) to support null from blank cells.
  > The table correctly uses empty cells (blanks) for irrelevant Sunday/Holiday hours in rows like 'Standard 40-hour week | 40 | [blank] | [blank]'. However, the response contains no Java method signatures or parameter declarations; the requirement that 'parameter types should be Integer (not int)' is not addressed or demonstrated in code.
- ✅ **1.7-readability-scenario-names**: Scenario names describe work patterns (e.g. 'Part-time', 'Five hours overtime', 'Full Sunday shift') — not 'Test case 1' or expected outcomes.
- ✅ **1.8-correctness-expected-values**: Expected values in intermediate and total columns are arithmetically correct for every row, consistent with the formulas in @Description.
- ✅ **1.9-correctness-value-set-semantics**: Value sets are only used where all values in the set produce the same expected result (e.g. any rate × 0 = 0). Not used where results would differ.
- ❌ **1.10-format-displayname**: @DisplayName is present on test methods, OR method names read as clear descriptive titles when converted from camelCase/snake_case.
  > The response contains no Java method definitions, code, or @DisplayName annotations. It provides a table structure and design notes, but no actual test method signatures or annotations.
- ❌ **1.11-format-description**: @Description is present on at least the pay calculation method and provides context beyond what the table rows already express — such as the overtime threshold (40 hours), premium multipliers (Sunday 2×, Holiday 2.5×), or other fixed assumptions not visible in the table. Does NOT merely restate the column names or summarise what the rows show.
  > The response contains no @Description annotation on any method. While a 'Design notes' section provides explanatory text, it is not in the form of a @Description annotation attached to a method.
- ❌ **1.12-format-annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
  > The response contains no method code or annotations of any kind (@DisplayName, @Description, @TableTest, or otherwise). Annotation order cannot be verified.
- ✅ **1.13-format-description-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +. Passes if @Description is absent.
- ✅ **1.14-depth-zero-rate**: A row where the hourly rate is zero, showing that pay is zero regardless of hours worked. This is a distinct edge case from negative rate (which is rejected).
- ❌ **1.15-format-clean-method**: The test method body contains no if/ternary null-handling or parsing logic. Null-to-default conversion (e.g. blank Sunday hours → 0) is handled via a @TypeConverter method or a private helper, keeping the test method limited to arrange/act/assert.
  > The response contains no Java test method bodies or implementations to evaluate. The statement 'Ready to hand off to `/tabletest`' indicates code has not yet been written.

### ⚠️ Eval eval-15-reis-discount [with_skill]

**11/18** · 277022 tokens · 256851ms

- ❌ **2.1-decomposition-concern-separation**: Discount ladder, and traveller eligibility are in separate tables. Rolling window counting as a third table is desirable.
  > The response provides three tables: `reisDiscountTier`, `discountByPassengerType`, and `discountedTicketPrice`. The third table tests price application arithmetic, not rolling window boundary counting. No separate table exists for rolling window testing (e.g., boundary at 30 vs 31 days).
- ✅ **2.2-children-flat-discount**: Children's flat 20% discount is represented — either as a row in the eligibility table or a separate note. It does not follow the ladder.
- ✅ **2.3-depth-tier-boundaries**: The discount ladder has multiple tiers derived from the 'every fifth trip' rule, with boundary values or value sets showing which ticket counts produce which discount. The maximum 40% discount is represented.
- ❌ **2.4-depth-rolling-window-boundary**: The 30-day rolling window boundary is tested: a ticket at exactly 30 days is included, at 31 days is excluded.
  > No table exists dedicated to rolling window boundary testing. The tables mention 'Trips in last 30 days' in column headers but provide no separate test table with boundary cases (e.g., testing difference between 30-day and 31-day windows).
- ❌ **2.6-readability-human-readable-values**: Eligibility uses human-readable values (yes/no or equivalent) rather than raw true/false. TypeConverter or similar mechanism mentioned.
  > The code uses PassengerType enum values (CHILD, ADULT, SENIOR) which are readable, but the response explicitly states 'TypeConverter or similar mechanism mentioned' — the response does not mention TypeConverter at all, only the enum usage.
- ✅ **2.9-correctness-value-set-tier-semantics**: Value sets in the tier table contain exactly the values that produce the same discount. No cross-tier contamination.
- ✅ **2.15-ticket-count-uses-value-sets**: The discount ladder table uses value sets for the ticket count column to group counts within the same tier (e.g. {5, 9} for min/max → 5%) — not one row per boundary value. The 'every fifth trip' rule creates tiers; the table should express tiers, not enumerate boundaries.
- ✅ **2.16-no-duplicate-tier-mapping**: The tier-to-discount mapping is expressed once — not repeated across multiple tables. A price application table (if present) should test the arithmetic (base price × discount → final price) without re-enumerating which ticket counts map to which tier.
- ✅ **2.10-format-displayname**: @DisplayName is present on test methods, OR method names read as clear descriptive titles when converted from camelCase/snake_case.
- ✅ **2.11-format-description**: @Description is present on at least the class or the tier method and provides context beyond what the table rows already express — such as the discount ladder structure, eligibility rules, or application context. Does NOT merely restate the column names or summarise what the rows show.
- ❌ **2.12-format-typeconverter**: TypeConverterSources is used for at least one human-readable conversion (yes/no, or rolling window notation).
  > The response does not mention TypeConverter, TypeConverterSources, or any converter implementation. No code shows conversion from boolean or numeric values to human-readable strings.
- ✅ **2.13-format-annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **2.14-format-description-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +. Passes if @Description is absent.
- ❌ **2.17-zone-irrelevance-visible**: The eligibility/passenger-type table includes a Zone column (or equivalent) with a value set to show that the discount applies regardless of zone — rather than silently omitting zone. The requirement explicitly says 'regardless of the zones you travel in'.
  > The `discountByPassengerType` table has columns 'Scenario | Passenger type | Trips in last 30 days | Discount?' with no Zone column to demonstrate zone irrelevance.
- ❌ **2.18-adult-senior-value-set**: Adult and senior are expressed as a value set {ADULT, SENIOR} (or equivalent) in at least one row of the eligibility table, since they follow identical discount rules. Not enumerated as separate rows with the same outcome.
  > In `discountByPassengerType`, Adult and Senior are separate rows: 'Adult, below first tier | ADULT | {0, 1, 4} | 0' and 'Senior, below first tier | SENIOR | {0, 1, 4} | 0', not combined as a value set {ADULT, SENIOR}.
- ✅ **2.19-depth-all-tiers**: All 9 discount tiers are represented in the ladder table: 0% (tickets 1-4), 5% (5-9), 10% (10-14), 15% (15-19), 20% (20-24), 25% (25-29), 30% (30-34), 35% (35-39), and 40% (40+). Not just a subset of tiers.
- ✅ **2.20-readability-one-row-per-tier**: Each tier in the discount ladder is expressed as a single row with a value set for ticket counts — not split across multiple rows (e.g. separate rows for 'tier kicks in' and 'tier holds'). One tier = one row.
- ❌ **2.21-readability-relative-time**: The rolling window table expresses time relatively (e.g. '14 days ago', '30 days ago', or an integer 'Days ago' column) rather than using absolute dates, making the table readable without needing to know the reference date.
  > No rolling window table is provided. The tables reference 'Trips in last 30 days' in column headers but do not have a dedicated table testing the rolling window boundary with relative time notation.

