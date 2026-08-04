# Analysis to-do — spec-by-example, iteration 7

Compared against **iteration 5**, grading claude-sonnet-5/default.

**1 of 1 evals comparable.**

**1 assertion verdict moved.**

These are deltas, not attributions. Before explaining any of them, read the generated
output for that eval and its narration — a grader justification can name the right
assertion and still name the wrong cause, and graders do misfire outright. Fill in the
cause line from the artefact, not from the justification.

Do not start the next iteration until every line below has a cause.

## LOST `no-duplicate-rows-within-a-table` — eval-17-shopping-cart

Grader said: _Table 1 (Add Item): PASS - distinct rows. Table 2 (Remove Item): PASS - distinct rows. Table 3 (Coupon Application): FAIL - row 'Replaces with a product-specific code' repeats the same replace-mechanic already shown by 'Replaces with a fixed-amount code', varying only coupon kind, which the model itself states is 'not a separate table per type' i.e. not a distinguishing factor for this concern. Table 4 (Cart Total): PASS - fixed-amount rows are boundary cases (under/at/over) and product-discount rows are distinct branches. Table 5 (Blocks Checkout): PASS - minimal two rows. Table 6 (Stock Verification): PASS - each row is a distinct success/failure branch._

- Output: `eval-17-shopping-cart/outputs/`
- Narration: `eval-17-shopping-cart/narration.md`
- Raw transcript: `eval-17-shopping-cart/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): **Not the rule 06 bound. The coupon table lost the column that a value set
  would have collapsed.** iteration-5 gave coupon kind a column of its own and wrote
  `{Percentage, Fixed, Product}` in it on both replace rows. iteration-7 has no kind column at all:
  kind is fused into one `Coupon Record` notation — `10% off cart`, `$5 off cart`, `20% off Widget` —
  shared with table 4, and the three replace rows then differ in *every* cell, so no row-economy
  check can see that two of them prove one claim. The narration shows rule 21 choosing that notation
  (narration:55, on the coupon columns: *"similar to how we treat catalogue prices as inputs in the
  cart table"*), and rule 21 is new since iteration-5 (`ded38c5`). Compounding it,
  `Active Coupon After?` merely echoes `Coupon Record` on every valid row — the shape rule 02 already
  names, *"a column that changes only as a side effect of another column is not being tested"*.
  **The generalisable defect: an attribute buried inside a composite value cannot be a value set.**
  Rule 21 asks for one notation per kind of value and says nothing about keeping separable the
  attribute a neighbouring rule ignores.
- **SETTLED BY A SECOND DRAW — not attributable to the batch, and no repair is owed.**
  `iteration-8` re-ran the same eval against the same skill state and scored **17/18 with
  `no-duplicate-rows-within-a-table` passing**. Its coupon table carries **one** replace row, not
  three. The fused notation is *still* there (`10% off cart`, `$5 off cart`), so rule 21's shared
  notation is the stable part and the duplicate replace rows are not: the analysis above had the
  mechanism right and the cause wrong. What varies between draws is how many kinds the agent walks
  through while chaining the state transition, and that is generation variance.
- **Keep the generalisable half anyway, unlanded:** an attribute buried inside a composite value
  cannot be a value set. It is true, it is stated without domain nouns, and it has no measured slot
  behind it — so it does not earn skill text on this evidence. Revisit only if the shape recurs.

## HELD (registered falsifier) `4.4-depth-item-operations` — eval-17-shopping-cart

It passed, so § G item 5's only win survives J1's narrowing of the rule 06 floor bullet. The add
table still carries *"Product already in cart"* — a branch the add-item rule itself names, which is
what J1 predicted by hand would survive the bound.
