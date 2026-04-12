# Eval Review — tabletest variant=minimal, Iteration 4

**Model:** sonnet · **Date:** 2026-04-12 · **Evals:** 19

## Summary

46/47 (97.9%) · 418822 tokens · 406.0s · $0.5876

## Delta vs Iteration 3

**Regressions (1):**
- ❌ eval-8-money-parse: `null-as-blank-cell`

## Resource Comparison vs Iteration 3

| Eval | Pass Rate | Prev | Tokens | Prev | Time(s) | Prev |
|------|-----------|------|--------|------|---------|------|
| eval-1-convert-repetitive-tests | 10/10 | 10/10 | 98875 | 44278 | 17.7 | 13.2 |
| eval-2-parse-dates | 12/12 | 13/13 | 100691 | 46136 | 56.9 | 47.1 |
| eval-3-dependency-setup | 4/4 | 4/4 | 44008 | 43867 | 9.6 | 8.3 |
| eval-7-permission-check | — | 6/11 | — | 70919 | — | 16.6 |
| eval-8-money-parse | 12/13 | 13/13 | 103864 | 74739 | 59.8 | 48.1 |
| eval-9-bonus-contractor-structure | — | 11/11 | — | 44635 | — | 21.6 |
| eval-14-weekly-pay | — | — | — | — | — | — |
| eval-15-reis-discount | — | — | — | — | — | — |
| eval-18-convert-from-code | — | 15/18 | — | 54127 | — | 131.9 |
| eval-19-convert-from-parameterized | 8/8 | 8/8 | 71384 | 44737 | 262.0 | 18.7 |
| eval-20-collections-and-quoting | — | 13/13 | — | 127350 | — | 184.0 |
| eval-22-event-registration-tt | — | 20/23 | — | 94849 | — | 220.3 |
| eval-23-loan-approval-tt | T/O | 14/16 | — | 103192 | T/O | 83.0 |
| eval-25-convert-from-spock | T/O | 7/15 | — | 50752 | T/O | 79.3 |
| eval-26-convert-from-kotest | T/O | 11/15 | — | 56106 | T/O | 136.4 |
| eval-27-convert-from-testng | T/O | 12/15 | — | 52219 | T/O | 87.4 |
| eval-28-convert-from-methodsource | T/O | 12/15 | — | 93296 | T/O | 134.4 |
| eval-29-shopping-cart-tt | T/O | 21/21 | — | 218477 | T/O | 249.2 |
| eval-30-order-splitting-tt | T/O | 18/18 | — | 309990 | T/O | 689.0 |

## Per-Eval Results

### ✅ Eval eval-1-convert-repetitive-tests

**10/10** · 98875 tokens · 17710ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ✅ **has-question-mark-column**: Table has at least one column name ending with '?' (e.g. 'Discount?' or 'Expected?')
- ✅ **has-three-data-rows**: Table has exactly 3 data rows (GOLD, SILVER, BRONZE)
- ✅ **single-assertion-in-method**: Test method body contains only one assertEquals (or equivalent assertion) — not three
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements
- ✅ **scenario-column-present**: Table has a scenario/description column as the leftmost column
- ✅ **has-descriptive-title**: Test method has either a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case
- ✅ **description-if-present-adds-information**: If @Description is present, it provides context beyond what the table rows already express. It is acceptable to omit @Description if the table already conveys all relevant context.
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **description-uses-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string). Passes if @Description is absent.

### ✅ Eval eval-2-parse-dates

**12/12** · 100691 tokens · 56947ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ✅ **exception-handled-cleanly**: The empty string / exception case is handled — either via a Throws? column, assertThrows inside the method, or a separate @TableTest
- ✅ **localdate-result-column**: There is a result column typed as LocalDate (or using a string representation that maps to LocalDate)
- ✅ **type-conversion-addressed**: The response addresses type conversion for non-trivial column types
- ✅ **has-descriptive-title**: Test method has either a @DisplayName annotation or a method name that reads as a clear, descriptive title
- ✅ **description-if-present-adds-information**: If @Description is present, it provides context beyond what the table rows already express
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest
- ✅ **description-uses-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block
- ✅ **empty-string-uses-quotes**: Empty string input uses quoted syntax (e.g. '' or "") in the table
- ✅ **concerns-decomposed**: Multiple tables (or @TableTest methods) are used, each addressing a distinct concern
- ✅ **minimal-rows-per-concern**: Each table has only the rows needed to express its concern's rules
- ✅ **separates-valid-and-invalid**: Valid parsing cases and error/exception cases are in separate @TableTest methods

### ✅ Eval eval-3-dependency-setup

**4/4** · 44008 tokens · 9589ms

- ✅ **flags-junit-version**: Response explicitly flags that JUnit 5.10.0 is below the required minimum (5.11) and recommends upgrading
- ✅ **provides-correct-groupid**: Response provides the tabletest dependency with groupId 'org.tabletest' (not guessed)
- ✅ **provides-artifactid**: Response includes 'tabletest-junit' as the artifactId
- ✅ **includes-test-scope**: The suggested dependency includes <scope>test</scope>

### ⚠️ Eval eval-8-money-parse

**12/13** · 103864 tokens · 59771ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ❌ **null-as-blank-cell**: Null input is represented as a blank cell in the valid inputs @TableTest table (not the string 'null', and not as a separate @Test method). The blank cell maps to a null parameter.
  > Null is handled as a separate @Test method (parsesNullAsNull), not as a blank cell in the table
- ✅ **exception-has-expected-column**: The exception/invalid-input table has an expected column (e.g. 'Throws?' or 'Exception?') specifying the exception type per row
- ✅ **exception-cases-handled**: The exception cases (empty string, letters-only, negative) are handled
- ✅ **result-column-with-question-mark**: Valid inputs have a result column name ending with '?'
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements
- ✅ **has-descriptive-title**: Test method has either a @DisplayName annotation or a method name that reads as a clear, descriptive title
- ✅ **description-if-present-adds-information**: If @Description is present, it provides context beyond what the table rows already express
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest
- ✅ **description-uses-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block
- ✅ **concerns-decomposed**: Multiple tables (or @TableTest methods) are used, each addressing a distinct concern
- ✅ **minimal-rows-per-concern**: Each table has only the rows needed to express its concern's rules
- ✅ **separates-valid-and-invalid**: Valid parsing cases and error/exception cases are in separate @TableTest methods

### ✅ Eval eval-19-convert-from-parameterized

**8/8** · 71384 tokens · 262008ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ✅ **scenario-column-present**: Table has a scenario/description column as the leftmost column
- ✅ **has-question-mark-column**: At least one output column name ends with '?'
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements
- ✅ **scenario-names-describe-conditions**: Scenario names describe conditions not outcomes
- ✅ **annotation-order**: Annotations in order: @DisplayName, @Description, @TableTest
- ✅ **has-descriptive-title**: Test method has descriptive title or @DisplayName annotation
- ✅ **description-uses-textblock**: @Description uses text block if longer than one line, or absent

