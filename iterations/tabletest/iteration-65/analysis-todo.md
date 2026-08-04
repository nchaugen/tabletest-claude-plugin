# Analysis to-do — tabletest, iteration 65

Compared against **iteration 59**, grading claude-sonnet-5/default.

**1 of 1 evals comparable.**

**4 assertion verdicts moved.**

These are deltas, not attributions. Before explaining any of them, read the generated
output for that eval and its narration — a grader justification can name the right
assertion and still name the wrong cause, and graders do misfire outright. Fill in the
cause line from the artefact, not from the justification.

Do not start the next iteration until every line below has a cause.

**§ K's falsifier was mis-stated and is answered a different way.** § K said
`type-converters-for-complex-objects` *"must hold"* on this eval. It never held:
**false in iterations 54, 59 and 65 alike**, so it can falsify nothing. The artefact answers the real
question — iteration-65 writes four `@TypeConverter`s (`Cart`, `ProductCatalogue`, `InventoryService`,
`Coupon`), each taking a map or a string and returning the domain type, with no relapse into an
invented separator. **§ K cost this eval nothing.** Correct the falsifier where it is written, in the
plan's § K, rather than leaving a slot named that cannot answer.

## LOST `concern-not-over-split` — eval-29-shopping-cart-tt

Grader said: _removesAnItemPresentInTheCart / rejectsRemovingAProductNotInTheCart both use columns 'Cart Before | Product Id | Cart After? | Message?' and only differ by success vs rejection rows, mirroring the single 4-row reference table but split into two methods; same pattern repeats for coupon (activatesACoupon.../rejectsAnInvalidCouponCode...) and checkout (verifiesStockLevels.../rejectsCheckoutOfAnEmptyCart...)._

- Output: `eval-29-shopping-cart-tt/outputs/`
- Narration: `eval-29-shopping-cart-tt/narration.md`
- Raw transcript: `eval-29-shopping-cart-tt/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): **A real rule 14 violation, three times over.** Success and rejection are
  split into two methods with *identical column sets* — the exact shape *Rejection as an Expected
  Column* exists to prevent, since a rejection is a row of the same table. It happens for remove,
  coupon and checkout alike, so it is a systematic reading rather than one slip. **Attribution to the
  batch is weak and one draw:** nothing in § G, J1–J3, § D, rule 21 or today's two repairs touches
  rejection placement, and the narration shows no passage driving the split. Record it; the repair
  candidate is that rule 14 states the principle and never names this shape (same columns, two
  methods, one success and one rejection) as the thing it forbids.

## LOST `consistent-quantity-naming` — eval-29-shopping-cart-tt

Grader said: _Cart contents column named 'Cart Before'/'Cart After?' in add/remove tables, 'Cart Items' in checkout tests, and 'Items' in calculatesCartTotalAsSumMinusCouponDiscountFlooredAtZero - three names for the same quantity._

- Output: `eval-29-shopping-cart-tt/outputs/`
- Narration: `eval-29-shopping-cart-tt/narration.md`
- Raw transcript: `eval-29-shopping-cart-tt/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): **Rule 21's first measured slot, and it is a loss — correcting the plan,
  which says no assertion reads rule 21.** `consistent-quantity-naming` reads it directly: the cart
  contents appear as `Cart Before`/`Cart After?`, `Cart Items` and `Items` across five tables in one
  class. Rule 21 (*consistent across tables*, landed this batch as B5) says exactly this must not
  happen, and it did not fire. **Do not read it as rule 21 causing the loss** — the baseline that
  passed this slot (iteration-59) also predates rule 21, so the rule's arrival coincides with a loss
  rather than preventing one. One draw on an eval whose five tables make it the only host; worth a
  second draw before any repair.

## LOST `no-duplicate-rows-within-a-table` — eval-29-shopping-cart-tt

Grader said: _activatesACouponReplacingAnyPreviouslyActiveCoupon: FAIL — 'Replaces an active coupon with a fixed-amount coupon' and 'Replaces an active coupon with a product-specific coupon' both just re-show the 'replace' branch with a different coupon type, which is irrelevant to this validity/replacement concern (type effects belong to the total table); the third row adds no new obligation._

- Output: `eval-29-shopping-cart-tt/outputs/`
- Narration: `eval-29-shopping-cart-tt/narration.md`
- Raw transcript: `eval-29-shopping-cart-tt/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): **A genuine redundant row, and — importantly — not the case today's rule 06
  edit covers.** The differing value is a plain column (`Coupon Code`), not a value inside a composite
  cell, so `fbb1126`'s new clause does not bear on it. Nor is a value set the fix: the expectation
  cells vary per code (`Active Coupon After?`, `Message?`), and rule 08 states a value set cannot vary
  an expectation. **The correct move is deleting the third row**, which is rule 06's plain
  *kinds-of-a-thing-another-rule-tells-apart* case. Guidance present, did not fire. **Watch for
  dilution:** `fbb1126` added six lines to this very bullet, and the loss is on the bullet's original
  half — one draw, no narration evidence, so recorded rather than attributed.

## WON `scenario-names-describe-conditions` — eval-29-shopping-cart-tt

Grader said: _Names like 'Stock is short for multiple items', 'Product-specific coupon has no effect when its product is absent' describe the condition, none paraphrase a specific expectation cell value or use generic labels._

- Output: `eval-29-shopping-cart-tt/outputs/`
- Narration: `eval-29-shopping-cart-tt/narration.md`
- Raw transcript: `eval-29-shopping-cart-tt/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): **Not attributed — this is the third-worst flipper in the triage.** It also
  moved the same way on eval-30 in the same portion, which is the signature of grader variance rather
  than of two evals improving at once. `docs/assertion-triage.md` holds the per-slot instability.
