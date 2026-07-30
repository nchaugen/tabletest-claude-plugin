# Analysis to-do — tabletest, iteration 47

Compared against **iteration 45**, grading claude-sonnet-5/default.

**2 of 2 evals comparable.**

**4 assertion verdicts moved.**

These are deltas, not attributions. Before explaining any of them, read the generated
output for that eval and its narration — a grader justification can name the right
assertion and still name the wrong cause, and graders do misfire outright. Fill in the
cause line from the artefact, not from the justification.

Do not start the next iteration until every line below has a cause.

## WON `concerns-decomposed` — eval-2-parse-dates

Grader said: _Two @TableTest methods: parsesSupportedDateFormats and rejectsEmptyInput_

- Output: `eval-2-parse-dates/outputs/`
- Narration: `eval-2-parse-dates/narration.md`
- Raw transcript: `eval-2-parse-dates/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): Real, caused by cb6cd5b's mixed-throw bound. The output splits the task into `parsesSupportedDateFormats` and `rejectsEmptyInput`; iteration 45 had one table.

## WON `separates-valid-and-invalid` — eval-2-parse-dates

Grader said: _kept in its own table with a Throws? column rather than merged with the value table_

- Output: `eval-2-parse-dates/outputs/`
- Narration: `eval-2-parse-dates/narration.md`
- Raw transcript: `eval-2-parse-dates/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): Real, same edit. `rejectsEmptyInput` gets its own table with a `Throws?` column instead of nesting the value assertion inside `thrownBy`.

## WON `annotation-order` — eval-20-collections-and-quoting

Grader said: _Annotations in correct order: @DisplayName, @Description, @TableTest_

- Output: `eval-20-collections-and-quoting/outputs/`
- Narration: `eval-20-collections-and-quoting/narration.md`
- Raw transcript: `eval-20-collections-and-quoting/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): Not a win. Iteration 45's failure here was a checker false positive (the scan ran past the method boundary; fixed in b09dd15), so cb6cd5b's annotation-order reunification is unmeasured, not confirmed. This run also passes the assertion by writing no `@DisplayName` at all — an assertion that cannot fail when the annotation is absent.

## LOST `no-if-switch-in-method` — eval-20-collections-and-quoting

Grader said: _ternary operator found in method body_

- Output: `eval-20-collections-and-quoting/outputs/`
- Narration: `eval-20-collections-and-quoting/narration.md`
- Raw transcript: `eval-20-collections-and-quoting/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): Genuine new failure. `TagFilterTest.java:45` is `List<String> expected = kept ? List.of(tag) : List.of();` — a boolean `Kept?` column converted to a list by ternary, where the column should hold the list. Absent from iterations 40 and 45. Deterministic, so the verdict is certain, but causation is n=1: the whole output shape moved this run. Tracked as a slice-5 item, not a release blocker.

## Note on the missing fifth verdict

The run's own to-do listed a fifth move, LOST `annotation-order` on eval-2. It was a checker
false positive; b09dd15 scoped the backward scan to one method and the re-grade passes it, so it
no longer appears above. Iteration 47 stands at 32/33.
