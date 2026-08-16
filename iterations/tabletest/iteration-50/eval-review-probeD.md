# Eval Review — tabletest, Iteration 50

**Model:** sonnet (claude-sonnet-5) · **Grading:** claude-sonnet-5 · **Date:** 2026-08-16 · **Evals:** 1

## Summary

16/17 (94.1%) · 739489 tokens · 114.3s · $0.6267

_Cost figures are Claude Code list-price estimates; actual billing may differ (e.g. promotional pricing). Timed-out evals score 0 with unrecorded token usage._

> ⛔ **Void comparison — none of the 1 evals could be compared** vs iteration 49. Every delta below is computed over nothing; an absence of movement here is not evidence that nothing moved. See `analysis-todo.md`.

## Delta vs Iteration 49

**Not comparable (1) — excluded from the deltas above:**
- ⚠️ eval-2-parse-dates: iteration 49 did not run this eval; nothing to compare against

## Resource Comparison vs Iteration 49

| Eval | Pass Rate | Prev | Tokens | Prev | Time(s) | Prev |
|------|-----------|------|--------|------|---------|------|
| eval-2-parse-dates | 16/17 | — | 739489 | — | 114.3 | — |

## Per-Eval Results

### ⚠️ Eval eval-2-parse-dates

**16/17** · 739489 tokens · 114261ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ✅ **null-as-blank-cell**: Null input is represented as a blank cell (not the string 'null') in the table
- ✅ **exception-handled-cleanly**: The empty string / exception case is handled — either via a Throws? column, assertThrows inside the method, or a separate @TableTest — rather than being silently omitted
- ✅ **localdate-result-column**: There is a result column typed as LocalDate (or using a string representation that maps to LocalDate)
- ✅ **type-conversion-addressed**: The response addresses type conversion for non-trivial column types — either by providing a @TypeConverter/converter method, or by using a cell representation that TableTest's built-in conversion handles (e.g. ISO-8601 date strings for LocalDate — relying on built-in conversion counts as addressed).
- ✅ **has-descriptive-title**: Test method has either a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case (e.g. 'dateParsing' → 'Date Parsing'). Not a generic name like 'test1' or 'testMethod'.
- ✅ **description-if-present-adds-information**: PASSES when no @Description is present anywhere in the class — omitting it is always acceptable, and this assertion NEVER penalises its absence. Do not fail it for a missing @Description, and do not treat a @DisplayName as a substitute. When a @Description IS present, it must provide context beyond what the table rows already express — such as where/when the rule applies, or open questions — and FAILS when it merely restates the column names or summarises what the rows already show.
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **description-uses-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +. Passes if @Description is absent.
- ✅ **empty-string-uses-quotes**: Empty string input uses quoted syntax (e.g. '' or "") in the table — not a blank cell, which represents null. The distinction between empty string and null is preserved.
- ✅ **concerns-decomposed**: Multiple tables (or @TableTest methods) are used, each addressing a distinct concern — not one monolithic table mixing unrelated rules. Acceptable to use a single table only when the domain genuinely has a single concern.
- ✅ **no-duplicate-rows-within-a-table**: Judge each @TableTest in isolation. The assertion FAILS if a row re-covers an obligation an earlier row in the SAME table already discharged (e.g. a full region x speed x weight cross-product where one row per rate band would state the same rule). Judge each table against its concern's coverage obligations — the distinct behaviours the rule must demonstrate. Where the expected output states those obligations for a concern, judge against that list and do not invent a stricter one; where it does not, derive them from the concern itself. A row that discharges an obligation no other row reaches earns its place however simple it looks, and a value set that covers several values in one row is the preferred discharge, never a failure. Rows that walk one input up while everything else is held are excess when the third and later ones only re-show a direction the first two already established — 'standard applicant, no claims' at ages 30, 40 and 50, or a linear premium sampled at 0, 2 and 4 claims: two samples state the effect and the rest exercise the same arithmetic. They are NOT excess when each value is a boundary, a band, or a distinct branch of the rule — 40 against 41 hours, below/at/above a threshold, one row per rate band — because each then discharges an obligation of its own. ROWS YOU THINK ARE MISSING ARE NEVER A FAILURE HERE — this assertion judges excess rows only, and coverage is judged by 2.19-covers-every-tier and quantifier-covered-by-rows. Judge every @TableTest method in the class; your evidence must name each method with PASS or FAIL.
- ✅ **no-table-reproves-another**: Judge the class as a whole, and only its @TableTest methods: a plain @Test method is not a table and can never fail this assertion, however much of an earlier table it re-proves. The assertion FAILS if a whole @TableTest re-proves rules that earlier tables already established — an integration or end-to-end table whose rows re-prove established rules is the common case, and it fails however clean the other tables are. Sibling tables that each isolate one rule are NOT duplication merely because they share a baseline row: four surcharge tables that each show 'no surcharge' then 'surcharge applied' are four legitimate tables, not one duplicated four times. Collapsing several same-fixture tables into one, as concern-not-over-split requires, is likewise not a failure here. Rows you think are missing are never a failure here. Your evidence must name the re-proving table and the table it duplicates, or state that no @TableTest re-proves another.
- ✅ **separates-valid-and-invalid**: Valid parsing cases and error/exception cases are in separate @TableTest methods
- ✅ **has-tabletest-dependency**: The build file (build.gradle) includes org.tabletest:tabletest-junit as a test dependency
- ✅ **compiles**: The generated test code compiles successfully against the project scaffolding

