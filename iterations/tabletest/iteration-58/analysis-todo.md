# Analysis to-do — tabletest, iteration 58

Compared against **iteration 54**, grading claude-sonnet-5/default.

**2 of 2 evals comparable.**

**10 assertion verdicts moved.**

These are deltas, not attributions. Before explaining any of them, read the generated
output for that eval and its narration — a grader justification can name the right
assertion and still name the wrong cause, and graders do misfire outright. Fill in the
cause line from the artefact, not from the justification.

Do not start the next iteration until every line below has a cause.

**Gate closed 2026-08-03.** Every cause below is read off the two outputs and their iteration-54
counterparts. Movement on the two slots the § G predictions name was confirmed by a second grading
pass of the same stored outputs (`benchmark-confirm.json`): eval-15 reproduced all 34 verdicts
exactly, and eval-29 agreed on all four attributed slots. Two eval-29 slots disagreed between
passes — `type-converters-for-complex-objects` (pass 1) against `consistent-quantity-naming`
(pass 2), 28/32 either way. Neither is attributed here; both are grader noise on this artefact.

## LOST `2.18-adult-senior-value-set` — eval-15-reis-discount

Grader said: _Separate rows 'Adult reaches the first Reis tier...' and 'Senior also accumulates...' instead of a value set_

- Output: `eval-15-reis-discount/outputs/`
- Narration: `eval-15-reis-discount/narration.md`
- Raw transcript: `eval-15-reis-discount/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): The grader is right and the loss is real. `ReisDiscountTest.java:80-81` writes
  the adult and senior cases as two rows with **identical History cells and an identical `5%` outcome**,
  differing only in the category. iteration-54 wrote `{ADULT, SENIOR}` in the same position
  (`ReisDiscountCalculatorTest.java:27-28`), which is why the slot passed there. **§ G item 4's
  row-level grouping question did not fire on this column** — and the mechanism was plainly available
  to the agent, because the *same three rows* carry `{ZONE_1, ZONE_2, ZONE_3}` in the Zone column.
  So the agent applied value sets to the column it was told did not matter and not to the column whose
  two members produce the same outcome cell. Item 4 was confirmed on eval-13 in iteration-57; this is
  its first negative case, and it locates the gap precisely — the row-level question is being read as
  being about *indifferent* inputs rather than about *equal outcomes*.

## WON `2.19-depth-all-tiers` — eval-15-reis-discount

Grader said: _Ladder table rows span from 0% (4 tickets) through 40% (40+ tickets), covering all nine tiers_

- Output: `eval-15-reis-discount/outputs/`
- Narration: `eval-15-reis-discount/narration.md`
- Raw transcript: `eval-15-reis-discount/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): `ReisDiscountTest.java:47-66` is a single 17-row ladder table with a
  below/at pair for every tier from 4 tickets to 40, plus a 50-ticket row for the cap. Not one of
  § G's six items — this is the one-table-per-rule shape, which iteration-54 had split across a
  separate `ReisDiscountLadderTest` class that did not span every tier.

## WON `2.21-readability-relative-time` — eval-15-reis-discount

Grader said: _History uses 'daysAgo' field e.g. '[[daysAgo: 29, ticketType: SINGLE]]' expressing time relatively_

- Output: `eval-15-reis-discount/outputs/`
- Narration: `eval-15-reis-discount/narration.md`
- Raw transcript: `eval-15-reis-discount/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): **§ G item 2 CONFIRMED.** Every History cell is
  `[[daysAgo: N, ticketType: …]]` (`:30-34`), read against a `NOW` constant declared in the
  `@Description` at `:20-22` and applied in the converter at `:96` (`NOW.minusDays(daysAgo)`).
  This is exactly the shape item 2 set out to license: a relative value in a column measured from a
  declared reference. iteration-54 had promoted the reference to an absolute `Purchase Time` column
  and its cells became 200-character ISO strings (`SingleTicketTripCounterTest.java:28-34`), which
  is what failed the slot.

## LOST `2.4-depth-rolling-window-boundary` — eval-15-reis-discount

Grader said: _"Single ticket exactly 30 days ago falls outside the window ... 0" contradicts spec that 30 days should be included._

- Output: `eval-15-reis-discount/outputs/`
- Narration: `eval-15-reis-discount/narration.md`
- Raw transcript: `eval-15-reis-discount/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): Not a table-design failure — the agent resolved an ambiguity the wrong way
  and said so. `:32` asserts `0` for a ticket exactly 30 days old and the `@Description` at `:23-24`
  flags it: *"Open assumption: a purchase exactly 30 days before NOW falls outside the window"*.
  The assertion requires 30 included and 31 excluded. iteration-54 took the other branch
  (*"Purchase exactly 30 days ago counts"*) and passed. **No § G item touches this**, and nothing in
  the prompt settles it either — the source text says only "in the last 30 days". A skill cannot fix
  a coin flip; if this slot is to be stable the eval has to state the boundary.

## LOST `quantifier-covered-by-rows` — eval-15-reis-discount

Grader said: _Table1 @Description claims 'traveler category and zone do not affect the count' but every history row uses ADULT/ZONE_1 only — zone/category never varied, so the invariance claim is narrated but not exercised._

- Output: `eval-15-reis-discount/outputs/`
- Narration: `eval-15-reis-discount/narration.md`
- Raw transcript: `eval-15-reis-discount/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): **§ G item 2 recreated the condition § G item 1 exists to remove, by a route
  item 1 does not cover.** The `@Description` at `:25-26` claims category and zone *"do not affect the
  count"*, and no row can exercise that claim because the shorthand converter hard-codes both fields:
  `new PastPurchase(NOW.minusDays(daysAgo), TravelerCategory.ADULT, …, ZoneValidity.ZONE_1)` (`:96`).
  Item 1 addresses the *sentence* — hand off to rule 08 and add a varying row. Here the sentence is
  fine and the **cell shape** forbids the row: a two-key map has nowhere to put a third varying value.
  Item 2 licenses that shorthand without saying it must keep a slot for every field the description
  quantifies over.

## LOST `window-boundary-uses-purchase-time` — eval-15-reis-discount

Grader said: _History uses 'daysAgo' as integer days (NOW.minusDays(daysAgo)), no hour-level distinction like 29d23h vs 30d1h._

- Output: `eval-15-reis-discount/outputs/`
- Narration: `eval-15-reis-discount/narration.md`
- Raw transcript: `eval-15-reis-discount/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): Same cell shape, second slot. The assertion is explicit that *"a table whose
  finest distinction is whole days (30 vs 31) FAILS, however many boundary rows it has"*, and integer
  `daysAgo` has no finer distinction to offer. iteration-54's absolute timestamps drew the pair a
  second apart (`2026-07-03T08:00:00` counts, `07:59:59` does not) and passed both boundary slots.
  **The two assertions are not in conflict** — `hoursAgo: 719` is relative *and* sub-day, and would
  satisfy both. What the artefact shows is that item 2's licensed shape was copied at the granularity
  of its illustration rather than at the granularity the rule under test needs.

## LOST `concern-not-over-split` — eval-29-shopping-cart-tt

Grader said: _checkout is split into `requiresANonEmptyCartToCheckOut` (2 rows) and `verifiesStockLevelsAtCheckout` (4 rows), both asserting `success`/`message` on `CheckoutResult`, differing only by which sub-rule (empty-cart guard vs stock shortage) is exercised — combinable into one small table as the reference decomposition does._

- Output: `eval-29-shopping-cart-tt/outputs/`
- Narration: `eval-29-shopping-cart-tt/narration.md`
- Raw transcript: `eval-29-shopping-cart-tt/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): Confirmed against both outputs. iteration-54 had **one** checkout table of
  4 rows with the empty-cart case as its first row (`CartServiceTest.java:107-113`). iteration-58 has
  **two** tables — `:87-91` (2 rows) and `:106-112` (4 rows) — with the same signature and the same
  two assertions on `CheckoutResult`. The empty-cart guard was lifted out into a table of its own.

## LOST `no-duplicate-rows-within-a-table` — eval-29-shopping-cart-tt

Grader said: _addsItemsPricedFromTheCatalogue PASS; removesItemsFromTheCart PASS; appliesACouponCodeToTheCart FAIL (three rows 'Applies a percentage-off coupon...', 'Applies a fixed-amount coupon...', 'Applies a product-specific coupon...' all re-show the same 'valid code, none active' rule varying only coupon type, which this concern does not care about); requiresANonEmptyCartToCheckOut PASS; verifiesStockLevelsAtCheckout PASS; calculatesTheCartTotalAfterAnyCoupon PASS._

- Output: `eval-29-shopping-cart-tt/outputs/`
- Narration: `eval-29-shopping-cart-tt/narration.md`
- Raw transcript: `eval-29-shopping-cart-tt/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): The coupon table went **4 rows → 7** (`CartServiceTest.java:83-88` against
  `ShoppingCartTest.java:62-69`), and the three added rows are the percentage / fixed / product-specific
  variants of one rule — valid code, none active before — in a table whose concern is *which coupon
  ends up active*, not what kind it is. **This is the second instance of the same over-correction:**
  `table-driven-testing` eval-31 lost this same assertion id in iteration-8 to a full 2×2×2 cross
  product. § G item 5 loosened row economy without a bound, and the two losses are on different
  suites, so it is the rule and not one eval's agent.

## WON `quantifier-covered-by-rows` — eval-29-shopping-cart-tt

Grader said: _Method2 desc: 'whether or not other items are present' — rows include empty-cart and gadget-present absent-item cases: PASS. Method6 title 'AfterAnyCoupon' — rows cover none/percentage/fixed/product-present/product-absent: PASS._

- Output: `eval-29-shopping-cart-tt/outputs/`
- Narration: `eval-29-shopping-cart-tt/narration.md`
- Raw transcript: `eval-29-shopping-cart-tt/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): **§ G item 1 CONFIRMED, and confirmed twice** — this is the flip-prone slot
  the prediction said to re-grade, and the second pass agreed. The sentence item 1 was written against
  is gone: iteration-54's *"Cart items do not affect this rule and are held empty throughout"*
  (`CartServiceTest.java:80-81`) has no counterpart in iteration-58. **The pass is earned by rows, not
  by deleting a sentence.** `removesItemsFromTheCart` declares *"whether or not other items are
  present"* (`:39`) and carries both an empty-cart row and a gadget-present row (`:45-46`);
  `calculatesTheCartTotalAfterAnyCoupon` covers none / percentage / fixed / product-present /
  product-absent (`:128-133`). Note the handoff is applied with judgement, not mechanically: the coupon
  table still holds cart items constant and says so (*"since applyCoupon does not read cart items"*,
  `:57-58`), and the grader did not penalise it — a held constant with a reason is not a quantified
  claim.

## WON `rule-falsifiable-by-a-row` — eval-29-shopping-cart-tt

Grader said: _addsItemsPricedFromTheCatalogue: Cart Items After? PASS, Message? PASS; removesItemsFromTheCart: Cart Items After? PASS, Message? PASS; appliesACouponCodeToTheCart: Active Coupon After? PASS, Message? PASS; requiresANonEmptyCartToCheckOut: Success? PASS, Message? PASS; verifiesStockLevelsAtCheckout: Success? PASS, Message? PASS; calculatesTheCartTotalAfterAnyCoupon: Total? PASS (values 39.97,18.00,15.00,0.00,27.00,20.00 all differ)._

- Output: `eval-29-shopping-cart-tt/outputs/`
- Narration: `eval-29-shopping-cart-tt/narration.md`
- Raw transcript: `eval-29-shopping-cart-tt/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): Every expectation column varies within its own table across all six methods,
  which the grader enumerates and the outputs bear out. Partly the same cause as the loss above — the
  wider coupon table that cost `no-duplicate-rows-within-a-table` is what makes `Active Coupon After?`
  vary — so **these two slots are one trade, not two independent movements.**

## What this run settles

- **Item 1: CONFIRMED**, on the eval it was drawn from, across two grading passes.
- **Item 2: CONFIRMED, and it costs two slots.** The relative shape wins
  `2.21-readability-relative-time` and loses `window-boundary-uses-purchase-time` and
  `quantifier-covered-by-rows`, both because the shorthand map it licenses was written with only the
  fields the rule under test reads. Neither loss is inherent: `hoursAgo` keeps the sub-day pair, and a
  third key keeps the quantified claim exercisable. **The rule needs the constraint stated —
  a relative column keeps the granularity of the boundary it tests, and a shorthand cell keeps a slot
  for every field its description quantifies over.**
- **Item 5's over-correction is now two slots on two suites**, both `no-duplicate-rows-within-a-table`
  (`table-driven-testing` eval-31, `tabletest` eval-29). Weigh against its one win
  (`spec-by-example` eval-17's `4.4-depth-item-operations`) before touching rule 06 again.
- **Item 4 has a negative case** (eval-15's category column) to set against its eval-13 win: the
  row-level question is being read as being about indifferent inputs rather than equal outcomes.
- **eval-15's net −2 is not a § G verdict.** Four of its six movements are the two items above; the
  other two are a coin-flip boundary the prompt never settles (`2.4`) and a one-table-per-rule win
  (`2.19`) no item claims.
