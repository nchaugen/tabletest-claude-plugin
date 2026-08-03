# Analysis to-do — tabletest, iteration 59

Compared against **iteration 58**, grading claude-sonnet-5/default.

**1 of 1 evals comparable.**

**4 assertion verdicts moved.**

These are deltas, not attributions. Before explaining any of them, read the generated
output for that eval and its narration — a grader justification can name the right
assertion and still name the wrong cause, and graders do misfire outright. Fill in the
cause line from the artefact, not from the justification.

Do not start the next iteration until every line below has a cause.

## WON `concern-not-over-split` — eval-29-shopping-cart-tt

Grader said: _Five distinct @TableTest methods (addsAProductToTheCart, removesAProductFromTheCart, appliesACouponCode, checksOutTheCart, calculatesTheCartTotal), each with its own fixture and rows; no concern is split into multiple same-fixture tables._

- Output: `eval-29-shopping-cart-tt/outputs/`
- Narration: `eval-29-shopping-cart-tt/narration.md`
- Raw transcript: `eval-29-shopping-cart-tt/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): **Not predicted, and not attributable to this batch.** Checkout is one table of 6 rows again
  (`ShoppingCartTest.java:88-95`), with the empty-cart row inside it — the shape iteration-54 had and
  iteration-58 broke into two tables. Nothing in J1/J2/J3 addresses splitting one concern across two
  tables; the rule 06 bound is about rows. The slot has now gone one table (54) -> two (58) -> one (59)
  under three different skill states, and it is on the triage's high-instability list. Treat as
  unattributed movement, not as evidence for the batch.

## WON `no-duplicate-rows-within-a-table` — eval-29-shopping-cart-tt

Grader said: _addsAProductToTheCart: PASS (5 distinct rows - new, accumulate, coexist, unknown, zero-qty). removesAProductFromTheCart: PASS (3 distinct rows). appliesACouponCode: PASS (4 distinct rows: activate, replace, expired, unknown). checksOutTheCart: PASS (6 distinct rows incl. boundary and shortfall-list variants). calculatesTheCartTotal: PASS (7 distinct rows incl. boundary '0.01 remainder' vs 'floors at zero')._

- Output: `eval-29-shopping-cart-tt/outputs/`
- Narration: `eval-29-shopping-cart-tt/narration.md`
- Raw transcript: `eval-29-shopping-cart-tt/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): **J1 CONFIRMED, and stable across both grading passes.** The coupon table is 4 rows
  (`:66-69`: activate, replace, expired, unknown) against iteration-58's 7. The three rows that made it
  fail — percentage / fixed / product-specific, all "valid code, none active" — are gone. That is
  exactly the shape the narrowed rule 06 bullet refuses: kinds of a thing that a *neighbouring* table
  tells apart. This is the slot the run was bought for and it moved.

## LOST `quantifier-covered-by-rows` — eval-29-shopping-cart-tt

Grader said: _addsAProductToTheCart: '... so is any negative value' (negative-quantity domain) FAIL, no negative-quantity row exists. appliesACouponCode: 'Coupon type never affects whether a code is accepted' FAIL (only PERCENT/FIXED tested, no PRODUCT). appliesACouponCode: 'Cart contents don't affect this rule, so items are held empty throughout' FAIL (cart never varied)._

- Output: `eval-29-shopping-cart-tt/outputs/`
- Narration: `eval-29-shopping-cart-tt/narration.md`
- Raw transcript: `eval-29-shopping-cart-tt/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): **REAL — failed in both grading passes — and J1 caused it.** The bound fired and its second half did
  not: the agent collapsed the three kind-rows by **deleting** them rather than by replacing them with a
  value set, while the description kept making claims about what it deleted. All three grader
  complaints are claims with no row behind them: *"Coupon type never affects whether a code is
  accepted"* (`:59-60`, only PERCENT and FIXED now appear), *"Cart contents don't affect this rule, so
  items are held empty throughout"* (`:61-62` — the sentence G item 1 was written to remove, back
  verbatim), and *"so is any negative value"* (`:16-17`, no negative-quantity row).
  **The repair is one clause in the bullet**: the collapse *is* a value set, `{PERCENT, FIXED, PRODUCT}`
  in the cell, not the removal of the rows. Rule 08 says this and the bullet points at it; pointing was
  not enough.

## LOST `rule-falsifiable-by-a-row` — eval-29-shopping-cart-tt

Grader said: _addsAProductToTheCart: Items After? PASS, Added? PASS, Message? PASS. removesAProductFromTheCart: Items After? PASS, Removed? PASS, Message? PASS. appliesACouponCode: Coupon After? FAIL ('Coupon After' equals 'Coupon Code' or 'Coupon Before' verbatim every row, e.g. 'SAVE10 | ... | SAVE10' and 'FLAT5 | ... | FLAT5'), Applied? PASS, Message? PASS. checksOutTheCart: Checked Out? PASS, Message? PASS. calculatesTheCartTotal: Total? PASS._

- Output: `eval-29-shopping-cart-tt/outputs/`
- Narration: `eval-29-shopping-cart-tt/narration.md`
- Raw transcript: `eval-29-shopping-cart-tt/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): **Noise — it passed in the confirmation pass over the same stored output.** The complaint is that
  `Coupon After?` restates `Coupon Code` or `Coupon Before` on every row, which was equally true of
  iteration-58's table, where the slot passed. Structurally identical outputs, flipped verdict, on a
  slot the triage already lists as unstable. Not attributed.

## What this run settles

- **J1's bound works on its target and is stable**: `no-duplicate-rows-within-a-table` returned in
  both passes.
- **J1 also costs `quantifier-covered-by-rows`, and that loss is stable too.** Collapsing rows without
  writing the value set leaves the description quantifying over values no row exercises. One clause
  fixes it; landed unmeasured the same day.
- **No evidence against S D.** Every moved slot traces to the rule 06 bullet or to grader instability;
  nothing points at the 1,300 deleted reference lines. The falsifier tripped, but not for the reason it
  was set to watch.
- **The score is flat, 28/32 both ways**, which is the point of judging targeted slots rather than
  totals: two wins and two losses, of which one win and one loss are real.
- Still unmeasured after this run: J2 (eval-15), J1's illustration half (eval-31), the eval-17
  falsifier, and rule 21, which no assertion in this suite reads directly.
