# Analysis to-do — tabletest, iteration 50

Compared against **iteration 49**, grading claude-sonnet-5/default.

## ⚠️ Partial comparison

**5 of 17 evals comparable.** The rest were excluded from them:

- `eval-2-parse-dates` — the baseline never ran this eval; there is nothing to compare against
- `eval-14-weekly-pay` — the baseline never ran this eval; there is nothing to compare against
- `eval-15-reis-discount` — the baseline never ran this eval; there is nothing to compare against
- `eval-18-convert-from-code` — the baseline never ran this eval; there is nothing to compare against
- `eval-22-event-registration-tt` — the baseline never ran this eval; there is nothing to compare against
- `eval-23-loan-approval-tt` — the baseline never ran this eval; there is nothing to compare against
- `eval-25-convert-from-spock` — the baseline never ran this eval; there is nothing to compare against
- `eval-26-convert-from-kotest` — the baseline never ran this eval; there is nothing to compare against
- `eval-27-convert-from-testng` — the baseline never ran this eval; there is nothing to compare against
- `eval-28-convert-from-methodsource` — the baseline never ran this eval; there is nothing to compare against
- `eval-29-shopping-cart-tt` — the baseline never ran this eval; there is nothing to compare against
- `eval-30-order-splitting-tt` — the baseline never ran this eval; there is nothing to compare against

**1 assertion verdict moved.**

These are deltas, not attributions. Before explaining any of them, read the generated
output for that eval and its narration — a grader justification can name the right
assertion and still name the wrong cause, and graders do misfire outright. Fill in the
cause line from the artefact, not from the justification.

Do not start the next iteration until every line below has a cause.

## WON `description-if-present-adds-information` — eval-7-permission-check

Grader said: _No @Description annotation is present in the @TableTest method, so the assertion is vacuously satisfied_

- Output: `eval-7-permission-check/outputs/`
- Narration: `eval-7-permission-check/narration.md`
- Raw transcript: `eval-7-permission-check/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): **Not a skill win — the grader correcting its own prior misfire on a vacuous assertion.** `PermissionCheckerTest.java` contains no `@Description` (grep: 0 occurrences across every output file), and the assertion's own text ends "It is acceptable to omit @Description if the table already conveys all relevant context" — so PASS is the correct verdict here. Iteration-49 FAILED the same omission, which the ledger already records as a grader misfire. The verdict moved because the grader changed its mind about an absent annotation, not because the output did.
- **Consequence:** `description-if-present-adds-information` is **vacuous whenever `@Description` is absent** — it cannot fail, and it flip-flops on the same input. This is the same defect as `annotation-order` (TODO, from `iteration-47`: satisfied by writing no `@DisplayName` at all). Two assertions now share it; fold both into that item and decide together — require the annotation in the same assertion, or let a sibling carry that half.
