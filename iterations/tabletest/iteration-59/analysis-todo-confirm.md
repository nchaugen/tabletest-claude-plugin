# Analysis to-do — tabletest, iteration 59

Compared against **iteration 58**, grading claude-sonnet-5/default.

**1 of 1 evals comparable.**

**5 assertion verdicts moved.**

These are deltas, not attributions. Before explaining any of them, read the generated
output for that eval and its narration — a grader justification can name the right
assertion and still name the wrong cause, and graders do misfire outright. Fill in the
cause line from the artefact, not from the justification.

Do not start the next iteration until every line below has a cause.

## WON `concern-not-over-split` — eval-29-shopping-cart-tt

Grader said: _Five distinct @TableTest methods: addsAProductToTheCart, removesAProductFromTheCart, appliesACouponCode, checksOutTheCart, calculatesTheCartTotal, each with its own inputs/fixture, no duplicate-fixture single-sub-rule tables._

- Output: `eval-29-shopping-cart-tt/outputs/`
- Narration: `eval-29-shopping-cart-tt/narration.md`
- Raw transcript: `eval-29-shopping-cart-tt/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## LOST `consistent-quantity-naming` — eval-29-shopping-cart-tt

Grader said: _'Items Before' / 'Items After?' used in add/remove tables, but the same cart-contents quantity is named plain 'Items' in checksOutTheCart and calculatesTheCartTotal_

- Output: `eval-29-shopping-cart-tt/outputs/`
- Narration: `eval-29-shopping-cart-tt/narration.md`
- Raw transcript: `eval-29-shopping-cart-tt/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## LOST `coupon-as-single-column` — eval-29-shopping-cart-tt

Grader said: _Coupon Code | Available Coupons | Expired Codes | Coupon After? ... spreads coupon data across three columns instead of one_

- Output: `eval-29-shopping-cart-tt/outputs/`
- Narration: `eval-29-shopping-cart-tt/narration.md`
- Raw transcript: `eval-29-shopping-cart-tt/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## WON `no-duplicate-rows-within-a-table` — eval-29-shopping-cart-tt

Grader said: _addsAProductToTheCart PASS (5 distinct rows: new item, accumulate, different product, unknown product, zero qty); removesAProductFromTheCart PASS (3 distinct rows); appliesACouponCode PASS (4 distinct rows: activate, replace, expired, nonexistent); checksOutTheCart PASS (6 distinct rows: empty, exact match, surplus, one-short, single-short-among-many, multiple-short); calculatesTheCartTotal PASS (7 distinct rows: no coupon, percent, fixed, product-specific, near-zero, floor, expired)_

- Output: `eval-29-shopping-cart-tt/outputs/`
- Narration: `eval-29-shopping-cart-tt/narration.md`
- Raw transcript: `eval-29-shopping-cart-tt/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## LOST `quantifier-covered-by-rows` — eval-29-shopping-cart-tt

Grader said: _addsAProductToTheCart @Description: 'so is any negative value' — domain FAIL, table has no negative-quantity row; appliesACouponCode @Description: 'Coupon type never affects whether a code is accepted' — domain FAIL, only PERCENT/FIXED shown, no PRODUCT; appliesACouponCode @Description: 'Cart contents don't affect this rule, so items are held empty throughout' — domain FAIL, cart contents never varied (hardcoded Map.of())_

- Output: `eval-29-shopping-cart-tt/outputs/`
- Narration: `eval-29-shopping-cart-tt/narration.md`
- Raw transcript: `eval-29-shopping-cart-tt/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 
