# Analysis to-do — tabletest, iteration 64

Compared against **iteration 60**, grading claude-sonnet-5/default.

**1 of 1 evals comparable.**

**8 assertion verdicts moved.**

These are deltas, not attributions. Before explaining any of them, read the generated
output for that eval and its narration — a grader justification can name the right
assertion and still name the wrong cause, and graders do misfire outright. Fill in the
cause line from the artefact, not from the justification.

Do not start the next iteration until every line below has a cause.

**Read the four losses as one event.** They are not four findings: iteration-64 wrote the *17-row
ladder* shape that iteration-58 wrote before any of this batch existed, and iteration-60's five-row
value-set ladder is the outlier between them. The ledger already recorded this as one trade, four
slots each way (it-58 → it-60); iteration-64 runs it backwards. **The batch did not create the shape
— it restored the one that was there first.**

## LOST `2.15-ticket-count-uses-value-sets` — eval-15-reis-discount

Grader said: _Rows like 'First step reached | 4 | 5' and 'Last trip of the first step | 8 | 5' enumerate boundary values instead of using a value set like {4,5,6,7,8}._

- Output: `eval-15-reis-discount/outputs/`
- Narration: `eval-15-reis-discount/narration.md`
- Raw transcript: `eval-15-reis-discount/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): **Half of the one trade.** The ladder table is 17 plain rows, two per tier
  (`First step reached | 4 | 5`, `Last trip of the first step | 8 | 5`), where iteration-60 wrote
  `{ADULT, SENIOR} | {n, m} | x%`. **Candidate contributory cause, one draw, not attributed:**
  rule 07 now carries two statements about how a tier's boundaries appear — today's
  *"one just below it, one just above"* (`af1fd63`) and the older *"a value set spanning the tier
  carries its own boundaries"*, two paragraphs apart. A reader standing in a tier ladder cannot tell
  which governs, and this output wrote the pair explicitly. Against attribution: the same 17-row
  shape appeared in iteration-58, before either sentence existed.

## LOST `2.18-adult-senior-value-set` — eval-15-reis-discount

Grader said: _Rows 'Adult reaches the first ladder step' and 'Senior reaches the first ladder step' are separate enumerated rows, not a value set {ADULT, SENIOR}._

- Output: `eval-15-reis-discount/outputs/`
- Narration: `eval-15-reis-discount/narration.md`
- Raw transcript: `eval-15-reis-discount/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): Same trade, same table. Adult and senior are two enumerated rows in
  `routesTheDiscountByTravelerCategory` rather than one `{ADULT, SENIOR}` row. Note the two rows are
  not identical — each carries its own history whose `travelerCategory` matches — so collapsing them
  needs the history cell reshaped too, which is the composite-cell case `fbb1126` now covers and
  which did not fire here.

## WON `2.19-depth-all-tiers` — eval-15-reis-discount

Grader said: _computesTheDiscountFromTheRecentPurchaseCount covers counts mapping to discounts 0,5,10,15,20,25,30,35,40 across its 17 rows._

- Output: `eval-15-reis-discount/outputs/`
- Narration: `eval-15-reis-discount/narration.md`
- Raw transcript: `eval-15-reis-discount/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): **Predicted and confirmed.** All nine tiers appear, which is what rule 07's
  *a formula behind the tiers does not reduce the tiers* asks for — iteration-60 lost this slot by
  arguing two tiers plus the delta determine the rest. The other half of the trade above.

## LOST `2.20-readability-one-row-per-tier` — eval-15-reis-discount

Grader said: _Each tier is split into 'X step reached' and 'Last trip of the X step' rows, e.g. two rows both mapping to discount 5._

- Output: `eval-15-reis-discount/outputs/`
- Narration: `eval-15-reis-discount/narration.md`
- Raw transcript: `eval-15-reis-discount/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): **This is the slot that shows 2.19 and 2.15/2.18/2.20 are jointly winnable
  and were not won.** The shape that satisfies all four exists and rule 07 already states it — one
  {{row}} per tier whose count cell is a value set spanning the tier (`{4, 8} | 5`), nine rows, all
  tiers present, boundaries carried inside the cells. iteration-64 wrote the spanning pair as two
  rows instead. So the eval is not self-contradictory here; the guidance is present and did not fire.

## LOST `2.21-readability-relative-time` — eval-15-reis-discount

Grader said: _Purchase Time and history dates use absolute timestamps like '2026-03-31T09:00:00' and '2026-03-25T10:00:00', not relative 'days ago' values._

- Output: `eval-15-reis-discount/outputs/`
- Narration: `eval-15-reis-discount/narration.md`
- Raw transcript: `eval-15-reis-discount/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): **Coupled to the `window-boundary-uses-purchase-time` win below, and the
  coupling is avoidable.** To draw the window boundary at one second the agent declared an absolute
  `Purchase Time` column and restated absolute `purchasedAt` inside every history map. Rule 10 forbids
  exactly that — *once a reference point is declared, the columns measured from it read better as
  offsets against it than as restatements of it* — and rule 07's own unit example gives the shape that
  wins both slots at once (`Hours Ago`, 719 against 720). **Correct text, in two rules, neither of
  which fired.** Same pattern as J2's first bound: the sentence is not where the reader is standing
  when the cell is drafted.

## WON `concerns-decomposed` — eval-15-reis-discount

Grader said: _6 @TableTest methods: routesTheDiscountByTravelerCategory, computesTheDiscountFromTheRecentPurchaseCount, countsOnlySingleTicketPurchases, countsOnlyPurchasesByTheSameTravelerCategory, countsOnlyPurchasesWithinTheLast30Days, countsPurchasesRegardlessOfZone — each a distinct concern._

- Output: `eval-15-reis-discount/outputs/`
- Narration: `eval-15-reis-discount/narration.md`
- Raw transcript: `eval-15-reis-discount/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): Six tables against iteration-60's single `discountFor(category, count)` seam.
  The ledger recorded iteration-60's three losses here (`scheme-derived-once`, `concerns-decomposed`,
  `2.17-zone-irrelevance-visible`) as *"equally readable as ordinary design variance"* — two of the
  three return without anything in the batch targeting them, which supports that reading. **Not
  attributed to any repair.**

## WON `scheme-derived-once` — eval-15-reis-discount

Grader said: _Only routesTheDiscountByTravelerCategory decides category->scheme; other tables (countsOnlySingleTicketPurchases, countsOnlyPurchasesByTheSameTravelerCategory) test countability, not re-deriving the scheme mapping_

- Output: `eval-15-reis-discount/outputs/`
- Narration: `eval-15-reis-discount/narration.md`
- Raw transcript: `eval-15-reis-discount/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): The second of the same three. Same cause as `concerns-decomposed`, same
  non-attribution — design variance on the decomposition, not a repair firing.

## WON `window-boundary-uses-purchase-time` — eval-15-reis-discount

Grader said: _'2026-03-01T09:00:00' (counts) vs '2026-03-01T08:59:59' (excluded) - a 1-second sub-day distinction, not whole-day granularity_

- Output: `eval-15-reis-discount/outputs/`
- Narration: `eval-15-reis-discount/narration.md`
- Raw transcript: `eval-15-reis-discount/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): **Predicted, confirmed, and the first time this slot has ever been won.**
  J2's first bound was correct text in rule 10 that never fired; moving it to rule 07 (`2ab0fd5`),
  where the reader choosing boundary values is standing, is the only change touching it. The pair is
  one second apart — the finest distinction the rule draws. **The one repair in this batch whose
  before/after is unambiguous.**
