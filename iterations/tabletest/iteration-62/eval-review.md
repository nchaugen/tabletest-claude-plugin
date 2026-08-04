# Eval Review — tabletest, Iteration 62

**Model:** sonnet (claude-sonnet-5) · **Grading:** claude-sonnet-5 · **Date:** 2026-08-04 · **Evals:** 1

## Summary

17/17 (100.0%) · 1999660 tokens · 526.6s · $1.5562

_Cost figures are Claude Code list-price estimates; actual billing may differ (e.g. promotional pricing). Timed-out evals score 0 with unrecorded token usage._

## Delta vs Iteration 54

No changes.

## Resource Comparison vs Iteration 54

| Eval | Pass Rate | Prev | Tokens | Prev | Time(s) | Prev |
|------|-----------|------|--------|------|---------|------|
| eval-20-collections-and-quoting | 17/17 | 17/17 | 1999660 | 1410056 | 526.6 | 312.3 |

## Per-Eval Results

### ✅ Eval eval-20-collections-and-quoting

**17/17** · 1999660 tokens · 526595ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ✅ **list-syntax-correct**: List values in the table use bracket syntax like [tech:java, biz:sales, dev:ci] — not comma-separated strings without brackets.
- ✅ **empty-list-explicit**: Empty list input uses [] (not a blank cell, which represents null). The distinction between empty list and null is preserved.
- ✅ **special-chars-quoted**: Values containing pipes (|), brackets, or colons (:) are properly quoted (e.g. "tech:java") so they don't conflict with table syntax. Colons without quoting would be mis-interpreted as map key:value entries.
- ✅ **set-syntax-correct**: Set<String> values in the table use curly brace syntax like {tech, dev} — not bracket syntax [tech, dev] which is for lists. The distinction between Set and List types is preserved in the table notation.
- ✅ **newline-in-cell**: A cell value containing a newline does not break the table row structure: the newline is either escaped as \n inside the cell, or the value is expressed as a list of lines joined in the test body. A literal line break inside a table row fails. Both representations are acceptable — escaping suits a newline inside a single value, a list of lines suits input that is inherently multi-line.
- ✅ **pipe-quoted**: Values containing pipe characters (|) are quoted so they don't conflict with the markdown table column separator. For example, a tag like 'biz:hr|recruiting' must be quoted.
- ✅ **scenario-column-present**: Table has a scenario/description column as the leftmost column
- ✅ **has-question-mark-column**: At least one output column name ends with '?' (e.g. 'Filtered tags?' or 'Result?')
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **has-descriptive-title**: Test method has either a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case.
- ✅ **description-uses-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +. Passes if @Description is absent.
- ✅ **has-tabletest-dependency**: The build file (build.gradle) includes org.tabletest:tabletest-junit as a test dependency
- ✅ **no-blank-collection-elements**: No collection value in any table contains a blank element. `[a, , c]`, `[a, b, ]` and `[, a, b]` are parse errors, not collections holding a null element — a collection value cannot express a null element at all.
- ✅ **empty-tag-case-covered**: The prompt's rule "an empty tag string is never kept, whatever the category" is exercised by a row. FAILS when no row anywhere feeds an empty tag into the input — the rule is then stated by the prompt and tested by nothing. The empty tag must appear as a quoted empty string ('' or "") inside a tag list, or as an equivalent explicit representation such as a dedicated column; a blank element inside a collection does NOT count, because a collection cannot express one at all. Judge COVERAGE, not syntax: whether such a row exists, not how many do and not how the rest of the table is punctuated. The syntax question — whether any collection holds a blank element — belongs to no-blank-collection-elements and must not be re-judged here.
- ✅ **compiles**: The generated test code compiles successfully against the project scaffolding

