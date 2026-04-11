# Eval Review — Iteration 15

**Model:** sonnet · **Date:** 2026-03-30 · **Evals:** 2

## Summary

**with_skill:** 14/33 (42.4%) · 135549 tokens · 104.3s · $0.3317

## Per-Eval Results

### ⚠️ Eval eval-14-weekly-pay [with_skill]

**8/15** · 67102 tokens · 40381ms

- ❌ **1.1-traceability-columns**: The pay table includes intermediate expected columns (e.g. Regular pay?, Overtime pay?, Premium pay?) so the reader can trace which rule contributes what — not just a single total Weekly pay? column.
  > The table has columns: Scenario | Weekday Hours | Sunday Hours | Holiday Hours | Hourly Rate | Weekly Pay? | Error?. It lacks intermediate columns such as 'Regular pay?', 'Overtime pay?', or 'Premium pay?'; only the final 'Weekly Pay?' total is shown.
- ❌ **1.2-error-has-expected-column**: The error/rejection table has an expected column (e.g. 'Throws?' or 'Exception?') specifying the exception type per row — rather than hardcoding the exception type in @Description or the method name.
  > The table includes an 'Error?' column, but the rejection row shows 'Invalid rate' (a descriptive message) rather than a specific exception type like 'IllegalArgumentException' or similar.
- ✅ **1.3-depth-overtime-boundary**: Overtime threshold boundary is covered: at least a value at the threshold (40) and just above (41 or similar). Not just values well below and well above.
- ✅ **1.4-depth-combined-scenario**: A combined scenario is present: a row with weekday + Sunday + holiday hours, testing the composition of all hour types.
- ❌ **1.5-depth-error-edge-cases**: Error/edge cases covered: at least negative hours (floored at zero) and negative or missing rate (rejected).
  > The table covers 'Negative hourly rate — rejected' but does not include a scenario with negative hours. Only 'No hours worked' (all zeros) is shown, not negative values.
- ✅ **1.6-readability-empty-cells**: Sunday and Holiday hour columns use empty cells (not 0) when those inputs are not relevant to a scenario — making it easier to spot which rows involve weekend/holiday hours. This is a readability preference; parameter types should be Integer (not int) to support null from blank cells.
- ✅ **1.7-readability-scenario-names**: Scenario names describe work patterns (e.g. 'Part-time', 'Five hours overtime', 'Full Sunday shift') — not 'Test case 1' or expected outcomes.
- ✅ **1.8-correctness-expected-values**: Expected values in intermediate and total columns are arithmetically correct for every row, consistent with the formulas in @Description.
- ✅ **1.9-correctness-value-set-semantics**: Value sets are only used where all values in the set produce the same expected result (e.g. any rate × 0 = 0). Not used where results would differ.
- ❌ **1.10-format-displayname**: @DisplayName is present on test methods, OR method names read as clear descriptive titles when converted from camelCase/snake_case.
  > The response is a markdown specification that states 'Ready to convert this into a @TableTest?' It contains no @DisplayName annotations or Java method names to evaluate.
- ❌ **1.11-format-description**: @Description is present on at least the pay calculation method and provides context beyond what the table rows already express — such as the overtime threshold (40 hours), premium multipliers (Sunday 2×, Holiday 2.5×), or other fixed assumptions not visible in the table. Does NOT merely restate the column names or summarise what the rows show.
  > The response lacks a @Description annotation. While arithmetic checks such as '(40 × $10) + (1 × $15)' and '8 × $20' imply multipliers, there is no explicit @Description stating the overtime threshold, premium multiplier values, or other fixed assumptions.
- ❌ **1.12-format-annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
  > The response is a markdown specification without any code annotations to order. No @DisplayName, @Description, or @TableTest annotations are present.
- ✅ **1.13-format-description-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +. Passes if @Description is absent.
- ✅ **1.14-depth-zero-rate**: A row where the hourly rate is zero, showing that pay is zero regardless of hours worked. This is a distinct edge case from negative rate (which is rejected).
- ❌ **1.15-format-clean-method**: The test method body contains no if/ternary null-handling or parsing logic. Null-to-default conversion (e.g. blank Sunday hours → 0) is handled via a @TypeConverter method or a private helper, keeping the test method limited to arrange/act/assert.
  > The response is a specification without Java test method implementations shown. The response ends with 'Ready to convert this into a @TableTest?' indicating code conversion has not yet occurred.

### ⚠️ Eval eval-15-reis-discount [with_skill]

**6/18** · 68447 tokens · 63952ms

- ✅ **2.1-decomposition-concern-separation**: Discount ladder, and traveller eligibility are in separate tables. Rolling window counting as a third table is desirable.
- ✅ **2.2-children-flat-discount**: Children's flat 20% discount is represented — either as a row in the eligibility table or a separate note. It does not follow the ladder.
- ❌ **2.3-depth-tier-boundaries**: The discount ladder has multiple tiers derived from the 'every fifth trip' rule, with boundary values or value sets showing which ticket counts produce which discount. The maximum 40% discount is represented.
  > Table 1 lists individual scenarios (1, 2, 3, 4, 5, 7, 9, 10, 15, 20, 25, 30, 35, 40, 45) rather than grouping ticket counts into value sets. The assertion requires 'value sets showing which ticket counts produce which discount' but receives enumerated rows instead.
- ❌ **2.4-depth-rolling-window-boundary**: The 30-day rolling window boundary is tested: a ticket at exactly 30 days is included, at 31 days is excluded.
  > Table 3 contains scenarios describing trips in relative windows (past 30 days, older than 30 days) but does not explicitly test the boundary: a specific scenario showing a ticket at day 30 (included) versus day 31 (excluded) is absent.
- ❌ **2.6-readability-human-readable-values**: Eligibility uses human-readable values (yes/no or equivalent) rather than raw true/false. TypeConverter or similar mechanism mentioned.
  > While Table 2 uses human-readable categorical values (Adult, Senior, Child), the response contains no mention of 'TypeConverter or similar mechanism.' The assertion explicitly requires this mechanism to be mentioned.
- ❌ **2.9-correctness-value-set-tier-semantics**: Value sets in the tier table contain exactly the values that produce the same discount. No cross-tier contamination.
  > Table 1 does not employ value sets. Individual rows list single discount values (5, 7, 9 each in a separate row for 5%) rather than grouping ticket counts into sets like {5, 6, 7, 8, 9}.
- ❌ **2.15-ticket-count-uses-value-sets**: The discount ladder table uses value sets for the ticket count column to group counts within the same tier (e.g. {5, 6, 7, 8, 9} or {5, 9} for min/max → 5%) — not one row per boundary value. The 'every fifth trip' rule creates tiers; the table should express tiers, not enumerate boundaries.
  > Table 1 enumerates individual scenarios: 'First discount kicks in | 5 | 5%', 'Mid first tier | 7 | 5%', 'Last trip in first tier | 9 | 5%' instead of consolidating the 5% tier into a single row with a value set like {5, 6, 7, 8, 9}.
- ✅ **2.16-no-duplicate-tier-mapping**: The tier-to-discount mapping is expressed once — not repeated across multiple tables. A price application table (if present) should test the arithmetic (base price × discount → final price) without re-enumerating which ticket counts map to which tier.
- ❌ **2.10-format-displayname**: @DisplayName is present on test methods, OR method names read as clear descriptive titles when converted from camelCase/snake_case.
  > No @DisplayName annotations or @TableTest code are shown. The response states 'Ready to hand these to `/tabletest` to produce the `@TableTest` code once the open questions are confirmed,' indicating code generation is deferred.
- ❌ **2.11-format-description**: @Description is present on at least the class or the tier method and provides context beyond what the table rows already express — such as the discount ladder structure, eligibility rules, or application context. Does NOT merely restate the column names or summarise what the rows show.
  > No @Description annotations or code are present in the response. Code generation is deferred to a later step.
- ❌ **2.12-format-typeconverter**: TypeConverterSources is used for at least one human-readable conversion (yes/no, or rolling window notation).
  > No TypeConverter implementation or usage is shown or mentioned anywhere in the response.
- ❌ **2.13-format-annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
  > No annotations or code are shown in the response. The response defers code generation, so annotation order cannot be verified.
- ✅ **2.14-format-description-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +. Passes if @Description is absent.
- ❌ **2.17-zone-irrelevance-visible**: The eligibility/passenger-type table includes a Zone column (or equivalent) with a value set to show that the discount applies regardless of zone — rather than silently omitting zone. The requirement explicitly says 'regardless of the zones you travel in'.
  > Table 2 does not include a Zone column. The response mentions 'Zone does not affect discount eligibility (`{Zone 1, Zone 2, Zone 3, ...}` — the discount applies regardless)' as a separate note, not as a column in the eligibility table itself.
- ❌ **2.18-adult-senior-value-set**: Adult and senior are expressed as a value set {ADULT, SENIOR} (or equivalent) in at least one row of the eligibility table, since they follow identical discount rules. Not enumerated as separate rows with the same outcome.
  > Table 2 lists adults and seniors in separate rows: 'Adult, first trip | Adult | Single | Reis (rolling) | 0%' and 'Senior, first trip | Senior | Single | Reis (rolling) | 0%' instead of combining them into a single row with a value set {ADULT, SENIOR}.
- ✅ **2.19-depth-all-tiers**: All 9 discount tiers are represented in the ladder table: 0% (tickets 1-4), 5% (5-9), 10% (10-14), 15% (15-19), 20% (20-24), 25% (25-29), 30% (30-34), 35% (35-39), and 40% (40+). Not just a subset of tiers.
- ❌ **2.20-readability-one-row-per-tier**: Each tier in the discount ladder is expressed as a single row with a value set for ticket counts — not split across multiple rows (e.g. separate rows for 'tier kicks in' and 'tier holds'). One tier = one row.
  > Table 1 expresses the 5% tier across three separate rows: 'First discount kicks in | 5 | 5%', 'Mid first tier | 7 | 5%', 'Last trip in first tier | 9 | 5%' instead of consolidating into a single row.
- ✅ **2.21-readability-relative-time**: The rolling window table expresses time relatively (e.g. '14 days ago', '30 days ago', or an integer 'Days ago' column) rather than using absolute dates, making the table readable without needing to know the reference date.

