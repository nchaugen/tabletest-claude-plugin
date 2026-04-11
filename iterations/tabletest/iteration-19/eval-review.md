# Eval Review — Iteration 19

**Model:** sonnet · **Date:** 2026-03-30 · **Evals:** 2

## Summary

**with_skill:** 22/33 (66.7%) · 623912 tokens · 596.1s · $1.2937

## Delta vs Iteration 18

**Regressions (2):**
- ❌ eval-15-reis-discount: `2.16-no-duplicate-tier-mapping`
- ❌ eval-15-reis-discount: `2.18-adult-senior-value-set`

**Improvements (6):**
- ✅ eval-15-reis-discount: `2.6-readability-human-readable-values`
- ✅ eval-15-reis-discount: `2.10-format-displayname`
- ✅ eval-15-reis-discount: `2.11-format-description`
- ✅ eval-15-reis-discount: `2.13-format-annotation-order`
- ✅ eval-15-reis-discount: `2.14-format-description-textblock`
- ✅ eval-15-reis-discount: `2.19-depth-all-tiers`

## Resource Comparison vs Iteration 18

| Eval | Pass Rate | Prev | Tokens | Prev | Time(s) | Prev |
|------|-----------|------|--------|------|---------|------|
| eval-14-weekly-pay | 13/15 | 13/15 | 279218 | 288163 | 286.6 | 309.9 |
| eval-15-reis-discount | 9/18 | 5/18 | 344694 | 69867 | 309.5 | 86.3 |

## Per-Eval Results

### ⚠️ Eval eval-14-weekly-pay [with_skill]

**13/15** · 279218 tokens · 286557ms

- ✅ **1.1-traceability-columns**: The pay table includes intermediate expected columns (e.g. Regular pay?, Overtime pay?, Premium pay?) so the reader can trace which rule contributes what — not just a single total Weekly pay? column.
- ✅ **1.2-error-has-expected-column**: The error/rejection table has an expected column (e.g. 'Throws?' or 'Exception?') specifying the exception type per row — rather than hardcoding the exception type in @Description or the method name.
- ✅ **1.3-depth-overtime-boundary**: Overtime threshold boundary is covered: at least a value at the threshold (40) and just above (41 or similar). Not just values well below and well above.
- ✅ **1.4-depth-combined-scenario**: A combined scenario is present: a row with weekday + Sunday + holiday hours, testing the composition of all hour types.
- ❌ **1.5-depth-error-edge-cases**: Error/edge cases covered: at least negative hours (floored at zero) and negative or missing rate (rejected).
  > Error table covers negative rates ('Negative rate | 40 | 0 | 0 | -1.00' and 'Negative decimal | 0 | 8 | 0 | -0.01'), but no test case exists for negative hours. Main table has no row with negative weekday/Sunday/holiday hours.
- ❌ **1.6-readability-empty-cells**: Sunday and Holiday hour columns use empty cells (not 0) when those inputs are not relevant to a scenario — making it easier to spot which rows involve weekend/holiday hours. This is a readability preference; parameter types should be Integer (not int) to support null from blank cells.
  > Table uses explicit '0' values (e.g., 'No hours worked | 0 | 0 | 0') rather than empty cells. Method signature uses primitive 'int' parameters: 'void shouldCalculateWeeklyPay(int weekdayHrs, int sundayHrs, int holidayHrs, BigDecimal rate, ...' rather than 'Integer' wrapper type.
- ✅ **1.7-readability-scenario-names**: Scenario names describe work patterns (e.g. 'Part-time', 'Five hours overtime', 'Full Sunday shift') — not 'Test case 1' or expected outcomes.
- ✅ **1.8-correctness-expected-values**: Expected values in intermediate and total columns are arithmetically correct for every row, consistent with the formulas in @Description.
- ✅ **1.9-correctness-value-set-semantics**: Value sets are only used where all values in the set produce the same expected result (e.g. any rate × 0 = 0). Not used where results would differ.
- ✅ **1.10-format-displayname**: @DisplayName is present on test methods, OR method names read as clear descriptive titles when converted from camelCase/snake_case.
- ✅ **1.11-format-description**: @Description is present on at least the pay calculation method and provides context beyond what the table rows already express — such as the overtime threshold (40 hours), premium multipliers (Sunday 2×, Holiday 2.5×), or other fixed assumptions not visible in the table. Does NOT merely restate the column names or summarise what the rows show.
- ✅ **1.12-format-annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **1.13-format-description-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +. Passes if @Description is absent.
- ✅ **1.14-depth-zero-rate**: A row where the hourly rate is zero, showing that pay is zero regardless of hours worked. This is a distinct edge case from negative rate (which is rejected).
- ✅ **1.15-format-clean-method**: The test method body contains no if/ternary null-handling or parsing logic. Null-to-default conversion (e.g. blank Sunday hours → 0) is handled via a @TypeConverter method or a private helper, keeping the test method limited to arrange/act/assert.

### ⚠️ Eval eval-15-reis-discount [with_skill]

**9/18** · 344694 tokens · 309517ms

- ❌ **2.1-decomposition-concern-separation**: Discount ladder, and traveller eligibility are in separate tables. Rolling window counting as a third table is desirable.
  > Only two @TableTest methods are provided: reisDiscountTier and discountedTicketPrice. No third table for rolling window counting exists.
- ✅ **2.2-children-flat-discount**: Children's flat 20% discount is represented — either as a row in the eligibility table or a separate note. It does not follow the ladder.
- ✅ **2.3-depth-tier-boundaries**: The discount ladder has multiple tiers derived from the 'every fifth trip' rule, with boundary values or value sets showing which ticket counts produce which discount. The maximum 40% discount is represented.
- ❌ **2.4-depth-rolling-window-boundary**: The 30-day rolling window boundary is tested: a ticket at exactly 30 days is included, at 31 days is excluded.
  > No rolling window boundary test exists. No table tests the 30-day boundary condition.
- ✅ **2.6-readability-human-readable-values**: Eligibility uses human-readable values (yes/no or equivalent) rather than raw true/false. TypeConverter or similar mechanism mentioned.
- ✅ **2.9-correctness-value-set-tier-semantics**: Value sets in the tier table contain exactly the values that produce the same discount. No cross-tier contamination.
- ❌ **2.15-ticket-count-uses-value-sets**: The discount ladder table uses value sets for the ticket count column to group counts within the same tier (e.g. {5, 6, 7, 8, 9} or {5, 9} for min/max → 5%) — not one row per boundary value. The 'every fifth trip' rule creates tiers; the table should express tiers, not enumerate boundaries.
  > The 5% tier (4-8) is split across two rows: 'First threshold — fifth trip | 4 | 5' and 'First tier upper boundary | 8 | 5'. Similarly, 10% tier spans 'Second tier lower boundary | 9 | 10' and 'Second tier upper boundary | 13 | 10'. Tiers are enumerated by boundaries rather than expressed as single rows with complete value sets.
- ❌ **2.16-no-duplicate-tier-mapping**: The tier-to-discount mapping is expressed once — not repeated across multiple tables. A price application table (if present) should test the arithmetic (base price × discount → final price) without re-enumerating which ticket counts map to which tier.
  > Second table includes 'Prior tickets' column that re-enumerates tier mappings: rows show ADULT with 0 prior tickets (0% discount), 4 prior tickets (5% discount), 39 prior tickets (40% discount), duplicating the tier-to-discount mapping from the first table.
- ✅ **2.10-format-displayname**: @DisplayName is present on test methods, OR method names read as clear descriptive titles when converted from camelCase/snake_case.
- ✅ **2.11-format-description**: @Description is present on at least the class or the tier method and provides context beyond what the table rows already express — such as the discount ladder structure, eligibility rules, or application context. Does NOT merely restate the column names or summarise what the rows show.
- ❌ **2.12-format-typeconverter**: TypeConverterSources is used for at least one human-readable conversion (yes/no, or rolling window notation).
  > No @TypeConverterSources annotation or TypeConverter mechanism is mentioned in the code. While human-readable values (ADULT, SENIOR, CHILD) appear in the table, no TypeConverter implementation is shown.
- ✅ **2.13-format-annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **2.14-format-description-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +. Passes if @Description is absent.
- ❌ **2.17-zone-irrelevance-visible**: The eligibility/passenger-type table includes a Zone column (or equivalent) with a value set to show that the discount applies regardless of zone — rather than silently omitting zone. The requirement explicitly says 'regardless of the zones you travel in'.
  > Second table has no Zone column. The @Description states 'Zones have no effect on the discount amount' textually, but the table structure does not include a Zone column with value sets to demonstrate this claim.
- ❌ **2.18-adult-senior-value-set**: Adult and senior are expressed as a value set {ADULT, SENIOR} (or equivalent) in at least one row of the eligibility table, since they follow identical discount rules. Not enumerated as separate rows with the same outcome.
  > Second table enumerates adult and senior as separate rows: 'Adult, no prior trips | ADULT | 0 | ...' and 'Senior follows same Reis tiers as adult | SENIOR | 4 | ...'. They are not combined into a single row with value set {ADULT, SENIOR}.
- ✅ **2.19-depth-all-tiers**: All 9 discount tiers are represented in the ladder table: 0% (tickets 1-4), 5% (5-9), 10% (10-14), 15% (15-19), 20% (20-24), 25% (25-29), 30% (30-34), 35% (35-39), and 40% (40+). Not just a subset of tiers.
- ❌ **2.20-readability-one-row-per-tier**: Each tier in the discount ladder is expressed as a single row with a value set for ticket counts — not split across multiple rows (e.g. separate rows for 'tier kicks in' and 'tier holds'). One tier = one row.
  > Multiple tiers are split across rows: 0% appears in 'No travel history | 0 | 0' and 'Below first threshold | {1, 2, 3} | 0'; 5% appears in 'First threshold — fifth trip | 4 | 5' and 'First tier upper boundary | 8 | 5'; 10% appears in 'Second tier lower boundary | 9 | 10' and 'Second tier upper boundary | 13 | 10'.
- ❌ **2.21-readability-relative-time**: The rolling window table expresses time relatively (e.g. '14 days ago', '30 days ago', or an integer 'Days ago' column) rather than using absolute dates, making the table readable without needing to know the reference date.
  > No rolling window table exists in the provided response. Only two @TableTest methods are present.

