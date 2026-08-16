# Eval Review — tabletest variant=reference, Iteration 9

**Model:** sonnet · **Grading:** claude-sonnet-5 · **Date:** 2026-08-16 · **Evals:** 1

## Summary

19/19 (100.0%) · 0 tokens · 0.0s

> ⛔ **Void comparison — none of the 1 evals could be compared** vs iteration 8. Every delta below is computed over nothing; an absence of movement here is not evidence that nothing moved. See `analysis-todo.md`.

## Delta vs Iteration 8

**Not comparable (1) — excluded from the deltas above:**
- ⚠️ eval-20-collections-and-quoting: iteration 8 did not run this eval; nothing to compare against

## Resource Comparison vs Iteration 8

| Eval | Pass Rate | Prev | Tokens | Prev | Time(s) | Prev |
|------|-----------|------|--------|------|---------|------|
| eval-20-collections-and-quoting | 19/19 | — | 0 | — | 0.0 | — |

## Per-Eval Results

### ✅ Eval eval-20-collections-and-quoting

**19/19** · 0 tokens · 0ms

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
- ✅ **rule-traceable-to-requirement**: Every rule the rows assert is traceable to the requirement. The requirement is the prompt plus the source the prompt points at — the class under test, the existing test file. Enumerate before you decide: for each @TableTest, state in one sentence the rule its rows establish, then name the requirement text that decides that point, or write "requirement silent". A verdict reached without that list is not a verdict. FAILS when the requirement states a rule without a qualifier and the rows only hold under a qualifier the requirement does not attach — the rows narrow a rule the requirement states plainly. Worked example of a failure: the requirement says a count is "the number of single tickets you have purchased in the last 30 days", and a table whose rows exclude a past purchase because its traveller category differs from the new purchase's has added a matching condition the requirement never states. PASSES when the requirement is silent on the point. Resolving something the requirement leaves open is what is being asked for here, and EITHER reading passes: an inclusive-versus-exclusive boundary, behaviour for an input the requirement never mentions, which of two colliding requirement rules wins, what an operation does to an entry that is already there. Naming the rule an assumption on `@Description` neither creates a failure nor discharges one — the test is whether the requirement already decided the point, not how the solution described its choice, and whether an assumption is published is graded elsewhere. Rules you think are MISSING are never a failure here: this assertion judges rules the rows add, and coverage is judged by the depth and concern assertions. Judge every @TableTest method in the class.
- ✅ **native-collection-output**: Judge the expectation columns only — the '?'-suffixed result columns. Input columns are out of scope and can never fail this assertion, including an input whose cells carry a compact shorthand that a @TypeConverter parses. A compound output — a collection of items, or items keyed by a tag — is expressed as a native TableTest list, map, or set (possibly nested, e.g. [[a, b], [c]] or [W1: [a, b]]), not as a hand-rolled string that encodes the structure with embedded brackets or colons (e.g. "W1:[a,b]") and is assembled or parsed by a helper. FAILS when an expectation cell packs multiple values into one quoted scalar that the test builds via a stringifying helper. PASSES when the output column is a native collection — sets for order-independent semantics, or ordered lists/maps with a canonical sort. Scalar outputs (a number, an enum, or a single message) are exempt. Your evidence must name each expectation column and its verdict.

