# Analysis to-do — spec-by-example, iteration 5

Compared against **iteration 4**, grading claude-sonnet-5/default.

## ⚠️ Partial comparison

**2 of 3 evals comparable.** The rest were excluded from them:

- `eval-16-order-splitting` — generation failed (timeout or crash), so this eval produced no answer to compare — re-run it before reading anything into the gap

**3 assertion verdicts moved.**

These are deltas, not attributions. Before explaining any of them, read the generated
output for that eval and its narration — a grader justification can name the right
assertion and still name the wrong cause, and graders do misfire outright. Fill in the
cause line from the artefact, not from the justification.

Do not start the next iteration until every line below has a cause.

## WON `no-duplicate-rows-within-a-table` — eval-13-shipping-partial-applicability

Grader said: _Standard PASS (1 row); Express PASS (3 distinct branches); Overnight PASS (available/unavailable)_

- Output: `eval-13-shipping-partial-applicability/outputs/`
- Narration: `eval-13-shipping-partial-applicability/narration.md`
- Raw transcript: `eval-13-shipping-partial-applicability/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## WON `overnight-grouped` — eval-13-shipping-partial-applicability

Grader said: _UK or Ireland destination | {UK, Ireland} | yes | £14.99_

- Output: `eval-13-shipping-partial-applicability/outputs/`
- Narration: `eval-13-shipping-partial-applicability/narration.md`
- Raw transcript: `eval-13-shipping-partial-applicability/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## WON `4.4-depth-item-operations` — eval-17-shopping-cart

Grader said: _Rows include 'Adding a new item to an empty cart', 'Adding more of an item already in the cart', 'Adding a different item alongside an existing one', 'Removing the only item in the cart', 'Removing one item while others remain', 'Removing an item not in the cart'._

- Output: `eval-17-shopping-cart/outputs/`
- Narration: `eval-17-shopping-cart/narration.md`
- Raw transcript: `eval-17-shopping-cart/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 
