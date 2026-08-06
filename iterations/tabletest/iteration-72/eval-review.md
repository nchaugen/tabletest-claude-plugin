# Eval Review — tabletest, Iteration 72

**Model:** sonnet (claude-sonnet-5) · **Grading:** claude-sonnet-5 · **Date:** 2026-08-06 · **Evals:** 2

## Summary

19/23 (82.6%) · 1152532 tokens · 1217.8s · $1.0503

_Cost figures are Claude Code list-price estimates; actual billing may differ (e.g. promotional pricing). Timed-out evals score 0 with unrecorded token usage._

> ⚠️ **2 assertion verdicts moved** vs iteration 71 (over 1 of 2 evals — the rest were not comparable). These are deltas, not attributions: read each eval's `outputs/` and `narration.md` before explaining any of them, and do not start the next iteration until every entry in `analysis-todo.md` has a cause.

## Delta vs Iteration 71

**Improvements (2):**
- ✅ eval-14-weekly-pay: `1.11-format-description`
- ✅ eval-14-weekly-pay: `concern-not-over-split`

**Not comparable (1) — excluded from the deltas above:**
- ⚠️ eval-25-convert-from-spock: fingerprint differs from iteration 71; re-baseline to compare

## Resource Comparison vs Iteration 71

| Eval | Pass Rate | Prev | Tokens | Prev | Time(s) | Prev |
|------|-----------|------|--------|------|---------|------|
| eval-14-weekly-pay | 19/23 | 17/23 | 1152532 | 1314090 | 317.8 | 250.4 |
| eval-25-convert-from-spock | 0/27 | 25/27 | 0 | 1584017 | 900.0 | 382.8 |

## Per-Eval Results

### ⚠️ Eval eval-14-weekly-pay

**19/23** · 1152532 tokens · 317815ms

- ✅ **1.2-error-has-expected-column**: The error/rejection table has an expected column (e.g. 'Throws?' or 'Exception?') specifying the exception type per row — rather than hardcoding the exception type in @Description or the method name.
- ✅ **1.3-depth-overtime-boundary**: Judge every @TableTest in the class. Overtime starts strictly above 40 hours, so the boundary is shown by two rows in the SAME column of the SAME table: one holding exactly 40, and one holding a value greater than 40 and no greater than 41 (41 is the intended value; 40.5 and 41.0 also count). FAILS when no single column carries both. A value of 42 or above does NOT discharge it, however close it looks — 45 shows that overtime exists, not where it starts, because 41 through 44 could still be regular under a different threshold. Rows well below 40 are fine but discharge nothing here. Your evidence must quote the two cell values and name the column and method they sit in, or say which of the two is missing.
- ✅ **1.4-depth-combined-scenario**: A combined scenario is present: a row with weekday + Sunday + holiday hours, testing the composition of all hour types.
- ✅ **1.5-depth-error-edge-cases**: Error/edge cases covered with explicit rows: negative hours in some slot are handled by a visible example row stating the chosen semantics (rejected, floored at zero, or offsetting a positive slot) rather than being decided silently in the method body; and a negative or missing rate is rejected.
- ❌ **1.6-readability-empty-cells**: Sunday and Holiday hour columns use empty cells (not 0) when those inputs are not relevant to a scenario — making it easier to spot which rows involve weekend/holiday hours. This is a readability preference; parameter types should be Integer (not int) to support null from blank cells.
  > "Correction exactly balances category pay | -50 | 25 | 0 | 10 | 0" uses 0 not blank for Holiday Hours; params are double not Integer
- ✅ **1.7-readability-scenario-names**: Scenario names describe work patterns (e.g. 'Part-time', 'Five hours overtime', 'Full Sunday shift') — not 'Test case 1' or expected outcomes.
- ✅ **1.8-correctness-expected-values**: Expected values in intermediate and total columns are arithmetically correct for every row, consistent with the rate rules the rows state (regular 1x, overtime 1.5x, Sunday 2x, holiday 2x).
- ✅ **1.9-correctness-value-set-semantics**: Value sets are only used where all values in the set produce the same expected result (e.g. any rate × 0 = 0). Not used where results would differ. This assertion polices misuse only. An output that uses no value sets at all PASSES vacuously; the absence of a value set where one would have been possible is not a failure here.
- ✅ **1.10-format-displayname**: @DisplayName is present on test methods, OR method names read as clear descriptive titles when converted from camelCase/snake_case.
- ✅ **1.11-format-description**: If @Description is present, it provides context beyond what the table rows already express — such as what constitutes a pay week, the currency, rounding, or other fixed assumptions not visible in the rows. It does NOT restate the rate multipliers or the overtime threshold that the example rows already demonstrate, nor merely echo the column names. It is acceptable to omit @Description when the rows carry all the context.
- ✅ **1.12-format-annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **1.13-format-description-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +. Passes if @Description is absent.
- ❌ **1.14-depth-zero-rate**: A row where the hourly rate is zero, showing that pay is zero regardless of hours worked. This is a distinct edge case from negative rate (which is rejected).
  > "Rate at zero | 0 |" only tests rejection with hours all 0, no row shows zero-rate pay across varying/nonzero hours
- ✅ **1.15-format-clean-method**: The test method body contains no if/ternary null-handling or parsing logic. Null-to-default conversion (e.g. blank Sunday hours → 0) is handled via a @TypeConverter method or a private helper, keeping the test method limited to arrange/act/assert.
- ✅ **business-language-columns**: Column names use domain/business language (e.g. 'Weekday hours', 'Sunday hours', 'Hourly rate', 'Weekly pay?') — not implementation terms like 'weekdayHrs', 'int', 'param1'.
- ✅ **concerns-decomposed**: Multiple tables (or @TableTest methods) are used, each addressing a distinct concern — not one monolithic table mixing unrelated rules. Acceptable to use a single table only when the domain genuinely has a single concern.
- ❌ **no-duplicate-rows-within-a-table**: Judge each @TableTest in isolation. The assertion FAILS if a row re-covers an obligation an earlier row in the SAME table already discharged (e.g. a full region x speed x weight cross-product where one row per rate band would state the same rule). Judge each table against its concern's coverage obligations — the distinct behaviours the rule must demonstrate. Where the expected output states those obligations for a concern, judge against that list and do not invent a stricter one; where it does not, derive them from the concern itself. A row that discharges an obligation no other row reaches earns its place however simple it looks, and a value set that covers several values in one row is the preferred discharge, never a failure. Rows that walk one input up while everything else is held are excess when the third and later ones only re-show a direction the first two already established — 'standard applicant, no claims' at ages 30, 40 and 50, or a linear premium sampled at 0, 2 and 4 claims: two samples state the effect and the rest exercise the same arithmetic. They are NOT excess when each value is a boundary, a band, or a distinct branch of the rule — 40 against 41 hours, below/at/above a threshold, one row per rate band — because each then discharges an obligation of its own. ROWS YOU THINK ARE MISSING ARE NEVER A FAILURE HERE — this assertion judges excess rows only, and coverage is judged by 2.19-covers-every-tier and quantifier-covered-by-rows. Judge every @TableTest method in the class; your evidence must name each method with PASS or FAIL.
  > computesWeekdayPay: FAIL - row 'Well past the overtime threshold | 48 | 15 | 780' re-shows the same overtime arithmetic already established by rows 40/41, adding no new boundary. computesDoubleTimePay: PASS - Sunday/Holiday/zero/negative rows each distinct. rejectsNegativeHourlyRate: PASS - below/at/above zero boundary rows distinct. calculatesTotalWeeklyPay: PASS - additive, exact-balance, and exceeds rows each discharge a distinct obligation.
- ✅ **no-table-reproves-another**: Judge the class as a whole, and only its @TableTest methods: a plain @Test method is not a table and can never fail this assertion, however much of an earlier table it re-proves. The assertion FAILS if a whole @TableTest re-proves rules that earlier tables already established — an integration or end-to-end table whose rows re-prove established rules is the common case, and it fails however clean the other tables are. Sibling tables that each isolate one rule are NOT duplication merely because they share a baseline row: four surcharge tables that each show 'no surcharge' then 'surcharge applied' are four legitimate tables, not one duplicated four times. Collapsing several same-fixture tables into one, as concern-not-over-split requires, is likewise not a failure here. Rows you think are missing are never a failure here. Your evidence must name the re-proving table and the table it duplicates, or state that no @TableTest re-proves another.
- ❌ **separates-classification-and-calculation**: Payable hours categorisation (1x regular, 1.5x overtime, 2x Sunday/holiday) and pay calculation (hours × rate) are in separate @TableTest methods
  > computesWeekdayPay directly returns 'Weekday Pay?' (hours×rate with overtime multiplier) rather than classifying hours into bands separately from a pay calculation table, e.g. row '41 | 20 | 830' bakes rate into the classification step.
- ✅ **has-tabletest-dependency**: The build file (build.gradle) includes org.tabletest:tabletest-junit as a test dependency
- ✅ **compiles**: The generated test code compiles successfully against the project scaffolding
- ✅ **held-constants-declared**: A value the rule's outcome depends on, and which the table holds constant for every row, must be visible as a column or named in the @DisplayName/@Description as deliberately held fixed. It must not sit silently in the test method body or a field. Readers generalise from what varies, so an unstated constant is read as part of the rule: a table whose every row happens to use one traveller category states, to its reader, a rule about that category. FAILS when the method body, a field, or a @TypeConverter fixes a value that (1) the outcome depends on, and (2) appears in no column and is not named in the title or description as held fixed. A value declared only in a source-code comment (// or /* */) FAILS: the published surface is the title, the description and the table, and a comment reaches none of them. A method with no @DisplayName publishes its own name as its title, with camelCase split into words, so a constant named in the method name is declared. PASSES when every such constant is either a column or declared. Values that cannot affect the outcome are not failures — a fixed clock used only to construct inputs, object identity, or fixture values the rule is indifferent to. Declaring a held constant in @Description is NOT redundancy and does not conflict with the description-* assertions: those forbid restating what the rows already show, whereas a held constant is precisely what the rows cannot show.
- ✅ **concern-not-over-split**: Do not fragment a single concern across multiple @TableTest methods that share the same fixture and assertion and differ only in which one sub-rule they exercise. Two or more tables that fix the same setup (e.g. same zone and base cost) and each vary a single option or surcharge, with the same output column, should instead be one table with a column for the varying input. FAILS when two or more such same-fixture, single-sub-rule tables exist for one concern (e.g. a separate table per surcharge). PASSES when each @TableTest addresses a genuinely distinct concern with its own inputs. This is the complement of concerns-decomposed: that forbids cramming unrelated concerns into one table; this forbids scattering one concern across many. Apply two guards when judging whether tables should be collapsed. Collapsing is right when the combined table's total row count stays small — a handful, never a cross-product — and each row still makes clear which sub-rule it demonstrates, normally through its scenario name and the varying column. Where collapsing would multiply into a cross-product, or would leave rows whose purpose is no longer legible, the tables are genuinely distinct and separate methods PASS.

### ⚠️ Eval eval-25-convert-from-spock

**0/27** · 0 tokens · 900027ms


