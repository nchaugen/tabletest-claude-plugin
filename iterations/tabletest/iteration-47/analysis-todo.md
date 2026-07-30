# Analysis to-do — tabletest, iteration 47

Compared against **iteration 45**, grading claude-sonnet-5/default.

**2 of 2 evals comparable.**

**5 assertion verdicts moved.**

These are deltas, not attributions. Before explaining any of them, read the generated
output for that eval and its narration — a grader justification can name the right
assertion and still name the wrong cause, and graders do misfire outright. Fill in the
cause line from the artefact, not from the justification.

Do not start the next iteration until every line below has a cause.

## LOST `annotation-order` — eval-2-parse-dates

Grader said: _Order violations: @DisplayName (line 58) after @Description (line 41)_

- Output: `eval-2-parse-dates/outputs/`
- Narration: `eval-2-parse-dates/narration.md`
- Raw transcript: `eval-2-parse-dates/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): Checker false positive, not a regression. `DateParserTest.java:34-35` has `@DisplayName` immediately before `@TableTest` and no `@Description` at all; the checker's 20-line backward scan crossed into `parsesSupportedDateFormats` and matched *its* `@Description` (line 17). See [[annotation-order-checker-crosses-methods]].

## WON `concerns-decomposed` — eval-2-parse-dates

Grader said: _Two methods: parsesSupportedDateFormats and rejectsEmptyInput_

- Output: `eval-2-parse-dates/outputs/`
- Narration: `eval-2-parse-dates/narration.md`
- Raw transcript: `eval-2-parse-dates/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): Real, caused by cb6cd5b's mixed-throw bound. The output now splits the task into `parsesSupportedDateFormats` and `rejectsEmptyInput`; iteration 45 had one table.

## WON `separates-valid-and-invalid` — eval-2-parse-dates

Grader said: _'rejectsEmptyInput' kept in its own table with a Throws? column rather than merged with the value table_

- Output: `eval-2-parse-dates/outputs/`
- Narration: `eval-2-parse-dates/narration.md`
- Raw transcript: `eval-2-parse-dates/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): Real, same edit. `rejectsEmptyInput` gets its own table with a `Throws?` column instead of nesting the value assertion inside `thrownBy`.

## WON `annotation-order` — eval-20-collections-and-quoting

Grader said: _Annotations in correct order: @DisplayName, @Description, @TableTest_

- Output: `eval-20-collections-and-quoting/outputs/`
- Narration: `eval-20-collections-and-quoting/narration.md`
- Raw transcript: `eval-20-collections-and-quoting/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): Not a win. Iteration 45's failure here was the same checker false positive, so cb6cd5b's annotation-order reunification is unmeasured, not confirmed. This run passes the assertion by writing no `@DisplayName` at all — an assertion that cannot fail when the annotation is absent.

## LOST `no-if-switch-in-method` — eval-20-collections-and-quoting

Grader said: _ternary operator found in method body_

- Output: `eval-20-collections-and-quoting/outputs/`
- Narration: `eval-20-collections-and-quoting/narration.md`
- Raw transcript: `eval-20-collections-and-quoting/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): Genuine new failure. `TagFilterTest.java:45` is `List<String> expected = kept ? List.of(tag) : List.of();` — a boolean `Kept?` column converted to a list by ternary, where the column should hold the list. Absent from iterations 40 and 45. Deterministic, so the verdict is certain, but causation is n=1: the whole output shape moved this run. Tracked as a slice-5 item, not a release blocker.
