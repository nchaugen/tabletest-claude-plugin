# Eval Review — tabletest, Iteration 47

**Model:** sonnet (claude-sonnet-5) · **Grading:** claude-sonnet-5 · **Date:** 2026-07-29 · **Evals:** 2

## Summary

31/33 (93.9%) · 1864319 tokens · 389.7s · $1.4479

_Cost figures are Claude Code list-price estimates; actual billing may differ (e.g. promotional pricing). Timed-out evals score 0 with unrecorded token usage._

> ⚠️ **5 assertion verdicts moved** vs iteration 45. These are deltas, not attributions: read each eval's `outputs/` and `narration.md` before explaining any of them, and do not start the next iteration until every entry in `analysis-todo.md` has a cause.

## Delta vs Iteration 45

**Regressions (2):**
- ❌ eval-2-parse-dates: `annotation-order`
- ❌ eval-20-collections-and-quoting: `no-if-switch-in-method`

**Improvements (3):**
- ✅ eval-2-parse-dates: `concerns-decomposed`
- ✅ eval-2-parse-dates: `separates-valid-and-invalid`
- ✅ eval-20-collections-and-quoting: `annotation-order`

## Resource Comparison vs Iteration 45

| Eval | Pass Rate | Prev | Tokens | Prev | Time(s) | Prev |
|------|-----------|------|--------|------|---------|------|
| eval-2-parse-dates | 15/16 | 14/16 | 831223 | 606093 | 124.1 | 89.5 |
| eval-20-collections-and-quoting | 16/17 | 16/17 | 1033096 | 1278784 | 265.6 | 354.9 |

## Per-Eval Results

### ⚠️ Eval eval-2-parse-dates

**15/16** · 831223 tokens · 124115ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ✅ **null-as-blank-cell**: Null input is represented as a blank cell (not the string 'null') in the table
- ✅ **exception-handled-cleanly**: The empty string / exception case is handled — either via a Throws? column, assertThrows inside the method, or a separate @TableTest — rather than being silently omitted
- ✅ **localdate-result-column**: There is a result column typed as LocalDate (or using a string representation that maps to LocalDate)
- ✅ **type-conversion-addressed**: The response addresses type conversion for non-trivial column types — either by providing a @TypeConverter/converter method, or by using a cell representation that TableTest's built-in conversion handles (e.g. ISO-8601 date strings for LocalDate — relying on built-in conversion counts as addressed).
- ✅ **has-descriptive-title**: Test method has either a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case (e.g. 'dateParsing' → 'Date Parsing'). Not a generic name like 'test1' or 'testMethod'.
- ✅ **description-if-present-adds-information**: If @Description is present, it provides context beyond what the table rows already express — such as where/when the rule applies, or open questions. Does NOT merely restate the column names or summarise what the rows show. It is acceptable to omit @Description if the table already conveys all relevant context (e.g. supported date formats are visible as table rows).
- ❌ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
  > Order violations: @DisplayName (line 58) after @Description (line 41)
- ✅ **description-uses-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +. Passes if @Description is absent.
- ✅ **empty-string-uses-quotes**: Empty string input uses quoted syntax (e.g. '' or "") in the table — not a blank cell, which represents null. The distinction between empty string and null is preserved.
- ✅ **concerns-decomposed**: Multiple tables (or @TableTest methods) are used, each addressing a distinct concern — not one monolithic table mixing unrelated rules. Acceptable to use a single table only when the domain genuinely has a single concern.
- ✅ **no-duplicate-rows-within-a-table**: Judge each @TableTest in isolation. The assertion FAILS if a row re-covers an obligation an earlier row in the SAME table already discharged (e.g. a full region x speed x weight cross-product where one row per rate band would state the same rule). Judge each table against its concern's coverage obligations — the distinct behaviours the rule must demonstrate. Where the expected output states those obligations for a concern, judge against that list and do not invent a stricter one; where it does not, derive them from the concern itself. A row that discharges an obligation no other row reaches earns its place however simple it looks, and a value set that covers several values in one row is the preferred discharge, never a failure. ROWS YOU THINK ARE MISSING ARE NEVER A FAILURE HERE — this assertion judges excess rows only, and coverage is judged by 2.19-covers-every-tier and quantifier-covered-by-rows. Judge every @TableTest method in the class; your evidence must name each method with PASS or FAIL.
- ✅ **no-table-reproves-another**: Judge the class as a whole. The assertion FAILS if a whole @TableTest re-proves rules that earlier tables already established — an integration or end-to-end table whose rows re-prove established rules is the common case, and it fails however clean the other tables are. Sibling tables that each isolate one rule are NOT duplication merely because they share a baseline row: four surcharge tables that each show 'no surcharge' then 'surcharge applied' are four legitimate tables, not one duplicated four times. Collapsing several same-fixture tables into one, as concern-not-over-split requires, is likewise not a failure here. Rows you think are missing are never a failure here. Your evidence must name the re-proving table and the table it duplicates.
- ✅ **separates-valid-and-invalid**: Valid parsing cases and error/exception cases are in separate @TableTest methods
- ✅ **has-tabletest-dependency**: The build file (build.gradle) includes org.tabletest:tabletest-junit as a test dependency
- ✅ **compiles**: The generated test code compiles successfully against the project scaffolding

### ⚠️ Eval eval-20-collections-and-quoting

**16/17** · 1033096 tokens · 265623ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ✅ **list-syntax-correct**: List values in the table use bracket syntax like [tech:java, biz:sales, dev:ci] — not comma-separated strings without brackets.
- ✅ **empty-list-explicit**: Empty list input uses [] (not a blank cell, which represents null). The distinction between empty list and null is preserved.
- ✅ **special-chars-quoted**: Values containing pipes (|), brackets, or colons (:) are properly quoted (e.g. "tech:java") so they don't conflict with table syntax. Colons without quoting would be mis-interpreted as map key:value entries.
- ✅ **set-syntax-correct**: Set<String> values in the table use curly brace syntax like {tech, dev} — not bracket syntax [tech, dev] which is for lists. The distinction between Set and List types is preserved in the table notation.
- ✅ **newline-in-cell**: A cell value containing a newline does not break the table row structure: the newline is either escaped as \n inside the cell, or the value is expressed as a list of lines joined in the test body. A literal line break inside a table row fails. Both representations are acceptable — escaping suits a newline inside a single value, a list of lines suits input that is inherently multi-line.
- ✅ **pipe-quoted**: Values containing pipe characters (|) are quoted so they don't conflict with the markdown table column separator. For example, a tag like 'biz:hr|recruiting' must be quoted.
- ✅ **scenario-column-present**: Table has a scenario/description column as the leftmost column
- ✅ **has-question-mark-column**: At least one output column name ends with '?' (e.g. 'Filtered tags?' or 'Result?')
- ❌ **no-if-switch-in-method**: Test method body contains no if or switch statements
  > ternary operator found in method body
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **has-descriptive-title**: Test method has either a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case.
- ✅ **description-uses-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +. Passes if @Description is absent.
- ✅ **has-tabletest-dependency**: The build file (build.gradle) includes org.tabletest:tabletest-junit as a test dependency
- ✅ **no-blank-collection-elements**: No collection value in any table contains a blank element. `[a, , c]`, `[a, b, ]` and `[, a, b]` are parse errors, not collections holding a null element — a collection value cannot express a null element at all.
- ✅ **empty-string-element-quoted**: An empty tag inside a tag list is written as a quoted empty string ("" or ''), not as a blank element. Passes if the tables express the empty tag some other legitimate way (e.g. a dedicated String column); fails if a blank element inside a collection is used to mean an empty or absent tag.
- ✅ **compiles**: The generated test code compiles successfully against the project scaffolding

