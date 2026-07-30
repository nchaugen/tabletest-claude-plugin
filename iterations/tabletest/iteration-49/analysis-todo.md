# Analysis to-do — tabletest, iteration 49

Compared against **iteration 45**, grading claude-sonnet-5/default.

**5 of 5 evals comparable.**

**3 assertion verdicts moved.**

These are deltas, not attributions. Before explaining any of them, read the generated
output for that eval and its narration — a grader justification can name the right
assertion and still name the wrong cause, and graders do misfire outright. Fill in the
cause line from the artefact, not from the justification.

Do not start the next iteration until every line below has a cause.

## WON `annotation-order` — eval-20-collections-and-quoting

Grader said: _Annotations in correct order: @DisplayName, @Description, @TableTest_

- Output: `eval-20-collections-and-quoting/outputs/`
- Narration: `eval-20-collections-and-quoting/narration.md`
- Raw transcript: `eval-20-collections-and-quoting/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): Not a win. The output carries **no `@DisplayName` at all** (0 occurrences, same as iteration-47), and the assertion cannot fail when the annotation is absent. Iteration-45's failure here was the checker false positive fixed in b09dd15. Two runs in a row have now passed this slot by omission — the assertion needs to require the annotation, or hand that half to `has-descriptive-title`.

## LOST `description-if-present-adds-information` — eval-7-permission-check

Grader said: _No @Description annotation present in the test file; only @DisplayName used_

- Output: `eval-7-permission-check/outputs/`
- Narration: `eval-7-permission-check/narration.md`
- Raw transcript: `eval-7-permission-check/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): Grader misfire, not a regression. The grader's reason is "No @Description annotation present", and the assertion's own text ends "It is acceptable to omit `@Description` if the table already conveys all relevant context." The output omits it; the assertion permits that. eval-7 is effectively 13/13. Its sibling `description-uses-textblock` states the same exemption crisply ("Passes if @Description is absent"), so the fix is to word this one the same way.

## WON `scenario-names-describe-conditions` — eval-7-permission-check

Grader said: _'Admin performs any action', 'User reads or writes', 'User attempts delete', 'Guest reads', 'Guest attempts write or delete' name conditions, not outcomes_

- Output: `eval-7-permission-check/outputs/`
- Narration: `eval-7-permission-check/narration.md`
- Raw transcript: `eval-7-permission-check/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): Real. The output names rows `Admin performs any action`, `User reads or writes`, `User attempts delete`, `Guest reads`, `Guest attempts write or delete` — conditions throughout, none restating the permission outcome.

## The primary reading, which does not appear above

`no-if-switch-in-method` on eval-20 **passed**, and it does not show as moved here because the
comparison baseline is iteration-45, which also passed it. The failure was iteration-47's, and
against that iteration this slot moved FAIL → PASS.

Confirmed from the artefact rather than the verdict: iteration-47 wrote
`List<String> expected = kept ? List.of(tag) : List.of();` off a boolean `Kept?` column. This run has
no ternary in any of its four method bodies, and the boolean column is gone — the expectation is a
`List<String> filtered` column, which is the shape the skill asks for. The defect is fixed at its
cause, not worked around.

**Attribution stays weak, as pre-committed.** The branching passages repaired today live in
`references/`, which an agent solving eval-20 has little reason to open. The run establishes that the
shipping skill no longer produces this branching; it does not establish that the reference edits are
why.
