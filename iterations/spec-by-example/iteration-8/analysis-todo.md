# Analysis to-do — spec-by-example, iteration 8

Compared against **iteration 7**, grading claude-sonnet-5/default.

**1 of 1 evals comparable.**

**1 assertion verdict moved.**

These are deltas, not attributions. Before explaining any of them, read the generated
output for that eval and its narration — a grader justification can name the right
assertion and still name the wrong cause, and graders do misfire outright. Fill in the
cause line from the artefact, not from the justification.

Do not start the next iteration until every line below has a cause.

## WON `no-duplicate-rows-within-a-table` — eval-17-shopping-cart

Grader said: _Table1 PASS (zero/negative are boundary-adjacent invalid branches); Table2 PASS (4 distinct removal branches); Table3 PASS (valid/replace/expired/unknown distinct); Table4 PASS (no-coupon/percentage/fixed/product-match/product-no-match distinct); Table5 PASS (no-discount/less/equal/exceeds distinct boundaries); Table6 PASS (empty vs non-empty); Table7 PASS (sufficient/exact-boundary/over-boundary/multi-short/out-of-stock all distinct branches)._

- Output: `eval-17-shopping-cart/outputs/`
- Narration: `eval-17-shopping-cart/narration.md`
- Raw transcript: `eval-17-shopping-cart/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 
