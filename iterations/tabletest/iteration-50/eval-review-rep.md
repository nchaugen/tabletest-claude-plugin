# Eval Review — tabletest, Iteration 50

**Model:** sonnet (claude-sonnet-5) · **Grading:** claude-sonnet-5 · **Date:** 2026-08-01 · **Evals:** 5

## Summary

71/71 (100.0%) · 3086846 tokens · 378.4s · $2.6145

_Cost figures are Claude Code list-price estimates; actual billing may differ (e.g. promotional pricing). Timed-out evals score 0 with unrecorded token usage._

> ⛔ **Void comparison — none of the 5 evals could be compared** vs iteration 49. Every delta below is computed over nothing; an absence of movement here is not evidence that nothing moved. See `analysis-todo.md`.

## Delta vs Iteration 49

**Not comparable (5) — excluded from the deltas above:**
- ⚠️ eval-1-convert-repetitive-tests: fingerprint differs from iteration 49; re-baseline to compare
- ⚠️ eval-2-parse-dates: iteration 49 did not run this eval; nothing to compare against
- ⚠️ eval-7-permission-check: fingerprint differs from iteration 49; re-baseline to compare
- ⚠️ eval-8-money-parse: fingerprint differs from iteration 49; re-baseline to compare
- ⚠️ eval-9-bonus-contractor-structure: fingerprint differs from iteration 49; re-baseline to compare

## Resource Comparison vs Iteration 49

| Eval | Pass Rate | Prev | Tokens | Prev | Time(s) | Prev |
|------|-----------|------|--------|------|---------|------|
| eval-1-convert-repetitive-tests | 13/13 | 13/13 | 573686 | 513472 | 57.3 | 42.1 |
| eval-2-parse-dates | 16/16 | — | 739489 | — | 114.3 | — |
| eval-7-permission-check | 13/13 | 12/13 | 574682 | 699285 | 52.2 | 72.7 |
| eval-8-money-parse | 16/16 | 16/16 | 720470 | 830752 | 88.6 | 89.4 |
| eval-9-bonus-contractor-structure | 13/13 | 13/13 | 478519 | 612022 | 66.0 | 84.7 |

## Per-Eval Results

### ✅ Eval eval-1-convert-repetitive-tests

**13/13** · 573686 tokens · 57299ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ✅ **has-question-mark-column**: Table has at least one column name ending with '?' (e.g. 'Discount?' or 'Expected?')
- ✅ **has-three-data-rows**: Table has exactly 3 data rows (GOLD, SILVER, BRONZE)
- ✅ **single-assertion-in-method**: Each @TableTest method applies one uniform assertion pattern to every row — not different assertions per scenario. Several unconditional assertions checking distinct observable outputs of one result (e.g. success, message, resulting cart) count as one uniform pattern; the anti-pattern is branching on the row (if/switch/ternary/try-catch) to choose what is asserted.
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements
- ✅ **scenario-column-present**: Table has a scenario/description column as the leftmost column
- ✅ **has-descriptive-title**: Test method has either a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case (e.g. 'discountByCustomerTier' → 'Discount By Customer Tier'). Not a generic name like 'test1' or 'testMethod'.
- ✅ **description-if-present-adds-information**: If @Description is present, it provides context beyond what the table rows already express — such as fixed values shared by all rows, where/when the rule applies, or open questions. Does NOT merely restate the column names or summarise what the rows show. It is acceptable to omit @Description if the table already conveys all relevant context (e.g. fixed values are included as a column rather than hardcoded in the method body).
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **description-uses-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +. Passes if @Description is absent.
- ✅ **has-tabletest-dependency**: The build file (pom.xml) includes org.tabletest:tabletest-junit as a test dependency
- ✅ **compiles**: The generated test code compiles successfully against the project scaffolding
- ✅ **tests-pass**: The generated tests pass when executed against DiscountService

### ✅ Eval eval-2-parse-dates

**16/16** · 739489 tokens · 114261ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ✅ **null-as-blank-cell**: Null input is represented as a blank cell (not the string 'null') in the table
- ✅ **exception-handled-cleanly**: The empty string / exception case is handled — either via a Throws? column, assertThrows inside the method, or a separate @TableTest — rather than being silently omitted
- ✅ **localdate-result-column**: There is a result column typed as LocalDate (or using a string representation that maps to LocalDate)
- ✅ **type-conversion-addressed**: The response addresses type conversion for non-trivial column types — either by providing a @TypeConverter/converter method, or by using a cell representation that TableTest's built-in conversion handles (e.g. ISO-8601 date strings for LocalDate — relying on built-in conversion counts as addressed).
- ✅ **has-descriptive-title**: Test method has either a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case (e.g. 'dateParsing' → 'Date Parsing'). Not a generic name like 'test1' or 'testMethod'.
- ✅ **description-if-present-adds-information**: If @Description is present, it provides context beyond what the table rows already express — such as where/when the rule applies, or open questions. Does NOT merely restate the column names or summarise what the rows show. It is acceptable to omit @Description if the table already conveys all relevant context (e.g. supported date formats are visible as table rows).
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **description-uses-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +. Passes if @Description is absent.
- ✅ **empty-string-uses-quotes**: Empty string input uses quoted syntax (e.g. '' or "") in the table — not a blank cell, which represents null. The distinction between empty string and null is preserved.
- ✅ **concerns-decomposed**: Multiple tables (or @TableTest methods) are used, each addressing a distinct concern — not one monolithic table mixing unrelated rules. Acceptable to use a single table only when the domain genuinely has a single concern.
- ✅ **no-duplicate-rows-within-a-table**: Judge each @TableTest in isolation. The assertion FAILS if a row re-covers an obligation an earlier row in the SAME table already discharged (e.g. a full region x speed x weight cross-product where one row per rate band would state the same rule). Judge each table against its concern's coverage obligations — the distinct behaviours the rule must demonstrate. Where the expected output states those obligations for a concern, judge against that list and do not invent a stricter one; where it does not, derive them from the concern itself. A row that discharges an obligation no other row reaches earns its place however simple it looks, and a value set that covers several values in one row is the preferred discharge, never a failure. ROWS YOU THINK ARE MISSING ARE NEVER A FAILURE HERE — this assertion judges excess rows only, and coverage is judged by 2.19-covers-every-tier and quantifier-covered-by-rows. Judge every @TableTest method in the class; your evidence must name each method with PASS or FAIL.
- ✅ **no-table-reproves-another**: Judge the class as a whole. The assertion FAILS if a whole @TableTest re-proves rules that earlier tables already established — an integration or end-to-end table whose rows re-prove established rules is the common case, and it fails however clean the other tables are. Sibling tables that each isolate one rule are NOT duplication merely because they share a baseline row: four surcharge tables that each show 'no surcharge' then 'surcharge applied' are four legitimate tables, not one duplicated four times. Collapsing several same-fixture tables into one, as concern-not-over-split requires, is likewise not a failure here. Rows you think are missing are never a failure here. Your evidence must name the re-proving table and the table it duplicates.
- ✅ **separates-valid-and-invalid**: Valid parsing cases and error/exception cases are in separate @TableTest methods
- ✅ **has-tabletest-dependency**: The build file (build.gradle) includes org.tabletest:tabletest-junit as a test dependency
- ✅ **compiles**: The generated test code compiles successfully against the project scaffolding

### ✅ Eval eval-7-permission-check

**13/13** · 574682 tokens · 52203ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ✅ **uses-value-sets**: Table uses value-set syntax (curly braces like {READ, WRITE} or {READ, WRITE, DELETE}) to express rules that hold across multiple actions — rather than one row per role/action combination
- ✅ **fewer-than-nine-rows**: Table has fewer than 9 data rows (i.e. does not enumerate every role/action combination individually)
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements
- ✅ **scenario-column-present**: Table has a scenario/description column as the leftmost column
- ✅ **no-duplicate-role-output**: No two rows share both the same Role value and the same boolean output — forces value set consolidation (e.g. USER can READ and USER can WRITE, both true, should be one row with {READ, WRITE}).
- ✅ **has-descriptive-title**: Test method has either a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case (e.g. 'permissionsByRoleAndAction' → 'Permissions By Role And Action'). Not a generic name like 'test1' or 'canPerform'.
- ✅ **description-if-present-adds-information**: If @Description is present, it provides context beyond what the table rows already express — such as where permissions are checked in the request lifecycle, or open questions. Does NOT merely restate the column names or summarise what the rows show. It is acceptable to omit @Description if the table already conveys all relevant context.
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **description-uses-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +. Passes if @Description is absent.
- ✅ **scenario-names-describe-conditions**: Scenario names name the variation the row exercises, not the result it produces. The decidable test: FAILS when a scenario name states or paraphrases a value that appears in an expectation column of that same row — 'User cannot delete' beside an Allowed? cell of false, 'No discount applies' beside a Discount? cell of 0.00, 'Contractor gets no bonus' beside a Bonus? cell of 0. Also FAILS on generic labels ('Test 1', 'Test case 2'), which name no variation at all. PASSES otherwise. Naming the rule or the situation is correct even when it makes the outcome inferable — 'Delivery and pickup items always split', 'At the standard threshold, not above it', 'EU express, light package', 'Renewal with no claims', 'Missing name' all PASS. The failure this catches is a name echoing its own expectation cell, not a name that describes what the row is about, and not a name from which a reader who knows the rule could predict the outcome. A single offending name fails the assertion. Judge every @TableTest method in the class.
- ✅ **has-tabletest-dependency**: The build file (build.gradle) includes org.tabletest:tabletest-junit as a test dependency
- ✅ **compiles**: The generated test code compiles successfully against the project scaffolding

### ✅ Eval eval-8-money-parse

**16/16** · 720470 tokens · 88588ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ✅ **null-as-blank-cell**: Null input is represented as a blank cell in the valid inputs @TableTest table (not the string 'null', and not as a separate @Test method). The blank cell maps to a null parameter.
- ✅ **exception-has-expected-column**: The exception/invalid-input table has an expected column (e.g. 'Throws?' or 'Exception?') specifying the exception type per row — rather than hardcoding the exception type in @Description or the method name.
- ✅ **exception-cases-handled**: The exception cases (empty string, letters-only, negative) are handled — via a Throws? column, assertThrows in the method body, or a separate @TableTest — not silently omitted
- ✅ **result-column-with-question-mark**: Valid inputs have a result column name ending with '?' (e.g. 'Money?' or 'Result?')
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements
- ✅ **has-descriptive-title**: Test method has either a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case (e.g. 'moneyParsing' → 'Money Parsing'). Not a generic name like 'test1' or 'testMethod'.
- ✅ **description-if-present-adds-information**: If @Description is present, it provides context beyond what the table rows already express — such as the expected currency format, or open questions. Does NOT merely restate the column names or summarise what the rows show. It is acceptable to omit @Description if the table already conveys all relevant context.
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **description-uses-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +. Passes if @Description is absent.
- ✅ **concerns-decomposed**: Multiple tables (or @TableTest methods) are used, each addressing a distinct concern — not one monolithic table mixing unrelated rules. Acceptable to use a single table only when the domain genuinely has a single concern.
- ✅ **no-duplicate-rows-within-a-table**: Judge each @TableTest in isolation. The assertion FAILS if a row re-covers an obligation an earlier row in the SAME table already discharged (e.g. a full region x speed x weight cross-product where one row per rate band would state the same rule). Judge each table against its concern's coverage obligations — the distinct behaviours the rule must demonstrate. Where the expected output states those obligations for a concern, judge against that list and do not invent a stricter one; where it does not, derive them from the concern itself. A row that discharges an obligation no other row reaches earns its place however simple it looks, and a value set that covers several values in one row is the preferred discharge, never a failure. ROWS YOU THINK ARE MISSING ARE NEVER A FAILURE HERE — this assertion judges excess rows only, and coverage is judged by 2.19-covers-every-tier and quantifier-covered-by-rows. Judge every @TableTest method in the class; your evidence must name each method with PASS or FAIL.
- ✅ **no-table-reproves-another**: Judge the class as a whole. The assertion FAILS if a whole @TableTest re-proves rules that earlier tables already established — an integration or end-to-end table whose rows re-prove established rules is the common case, and it fails however clean the other tables are. Sibling tables that each isolate one rule are NOT duplication merely because they share a baseline row: four surcharge tables that each show 'no surcharge' then 'surcharge applied' are four legitimate tables, not one duplicated four times. Collapsing several same-fixture tables into one, as concern-not-over-split requires, is likewise not a failure here. Rows you think are missing are never a failure here. Your evidence must name the re-proving table and the table it duplicates.
- ✅ **separates-valid-and-invalid**: Valid parsing cases and error/exception cases are in separate @TableTest methods
- ✅ **has-tabletest-dependency**: The build file (build.gradle) includes org.tabletest:tabletest-junit as a test dependency
- ✅ **compiles**: The generated test code compiles successfully against the project scaffolding

### ✅ Eval eval-9-bonus-contractor-structure

**13/13** · 478519 tokens · 66035ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ✅ **contractor-uses-value-set**: CONTRACTOR row uses a value set for Department (e.g. {SALES, ENGINEERING}) to express 'regardless of department' — not a dummy placeholder like 'ANY' or 'N/A', and not enumerated as separate rows.
- ✅ **four-core-rules-covered**: The four level×department combinations (SENIOR+SALES, SENIOR+ENGINEERING, JUNIOR+SALES, JUNIOR+ENGINEERING) are all present in the table(s)
- ✅ **expects-bonus-percentage**: The expected output column contains bonus percentages (e.g. 15%, 12%, 0%) — not bonus amounts. The method returns a percentage, and the table should reflect that.
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements
- ✅ **has-descriptive-title**: Test method has either a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case (e.g. 'bonusByLevelAndDepartment' → 'Bonus By Level And Department'). Not a generic name like 'test1' or 'testMethod'.
- ✅ **description-if-present-adds-information**: If @Description is present, it provides context beyond what the table rows already express — such as how bonus rates are applied (e.g. as percentage of base salary), or open questions. Does NOT merely restate the column names or summarise what the rows show. It is acceptable to omit @Description if the table already conveys all relevant context.
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **description-uses-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +. Passes if @Description is absent.
- ✅ **scenario-names-describe-conditions**: Scenario names name the variation the row exercises, not the result it produces. The decidable test: FAILS when a scenario name states or paraphrases a value that appears in an expectation column of that same row — 'User cannot delete' beside an Allowed? cell of false, 'No discount applies' beside a Discount? cell of 0.00, 'Contractor gets no bonus' beside a Bonus? cell of 0. Also FAILS on generic labels ('Test 1', 'Test case 2'), which name no variation at all. PASSES otherwise. Naming the rule or the situation is correct even when it makes the outcome inferable — 'Delivery and pickup items always split', 'At the standard threshold, not above it', 'EU express, light package', 'Renewal with no claims', 'Missing name' all PASS. The failure this catches is a name echoing its own expectation cell, not a name that describes what the row is about, and not a name from which a reader who knows the rule could predict the outcome. A single offending name fails the assertion. Judge every @TableTest method in the class.
- ✅ **business-language-columns**: Column names use domain/business language (e.g. 'Level', 'Department', 'Bonus %') — not implementation terms like 'employeeLevel', 'deptCode', 'result'.
- ✅ **has-tabletest-dependency**: The build file (build.gradle) includes org.tabletest:tabletest-junit as a test dependency
- ✅ **compiles**: The generated test code compiles successfully against the project scaffolding

