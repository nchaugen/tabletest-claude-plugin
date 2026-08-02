# Analysis to-do — tabletest, iteration 54

Compared against **iteration 52**, grading claude-sonnet-5/default.

**1 of 1 evals comparable.**

**3 assertion verdicts moved.**

These are deltas, not attributions. Before explaining any of them, read the generated
output for that eval and its narration — a grader justification can name the right
assertion and still name the wrong cause, and graders do misfire outright. Fill in the
cause line from the artefact, not from the justification.

Do not start the next iteration until every line below has a cause.

## WON `consistent-quantity-naming` — eval-29-shopping-cart-tt

Grader said: _'Cart Before'/'Cart After?' used consistently in addsItemPricedFromCatalogue and removesItemFromCart; 'Success?'/'Message?' used consistently across all four operation tables with no renamed duplicate quantities found._

- Output: `eval-29-shopping-cart-tt/outputs/`
- Narration: `eval-29-shopping-cart-tt/narration.md`
- Raw transcript: `eval-29-shopping-cart-tt/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## LOST `quantifier-covered-by-rows` — eval-29-shopping-cart-tt

Grader said: _Title 'replacing any coupon already active' (apply-coupon table): only one non-blank 'Active Coupon Before' value (SAVE10) appears across rows — FAIL. Description 'removing an item removes the whole line regardless of its quantity' (removesItemFromCart): only quantity=2 is tested for a successful whole-line removal — FAIL. Description 'Cart items do not affect this rule and are held empty throughout' (appliesCouponReplacingActive): cart is held at a single value ([:]) throughout, never varied — FAIL._

- Output: `eval-29-shopping-cart-tt/outputs/`
- Narration: `eval-29-shopping-cart-tt/narration.md`
- Raw transcript: `eval-29-shopping-cart-tt/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## LOST `scenario-names-describe-conditions` — eval-29-shopping-cart-tt

Grader said: _'An empty cart cannot check out' directly paraphrases Success? = false, matching the FAIL example pattern 'User cannot delete' beside Allowed? false._

- Output: `eval-29-shopping-cart-tt/outputs/`
- Narration: `eval-29-shopping-cart-tt/narration.md`
- Raw transcript: `eval-29-shopping-cart-tt/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 
