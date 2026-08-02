# Analysis to-do — tabletest, iteration 54

Compared against **iteration 52**, grading claude-sonnet-5/default.

## ⚠️ Partial comparison

**4 of 5 evals comparable.** The rest were excluded from them:

- `eval-30-order-splitting-tt` — generation failed (timeout or crash), so this eval produced no answer to compare — re-run it before reading anything into the gap

**11 assertion verdicts moved.**

These are deltas, not attributions. Before explaining any of them, read the generated
output for that eval and its narration — a grader justification can name the right
assertion and still name the wrong cause, and graders do misfire outright. Fill in the
cause line from the artefact, not from the justification.

Do not start the next iteration until every line below has a cause.

## WON `1.11-format-description` — eval-14-weekly-pay

Grader said: _Assumes weekday hours may be negative to represent pay corrections or adjustments... documented this as an open assumption_

- Output: `eval-14-weekly-pay/outputs/`
- Narration: `eval-14-weekly-pay/narration.md`
- Raw transcript: `eval-14-weekly-pay/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): **Rule 10 (cluster 3 / A4).** iteration-52's `@Description` restated the
  multipliers — *"Overtime is paid at 1.5x the base rate; Sunday and holiday hours are always paid at
  2x. These multipliers are fixed pay rules, not a configurable policy, so they are not columns
  here"* — which is exactly the restatement `1.11` forbids, and it was written under the old rule 10
  permission ("a constant is either a column, or declared in the title or the description").
  iteration-54's description carries an assumption the rows cannot state instead
  (`outputs/…/WeeklyPayCalculatorTest.java:45-51`), matching the new wording: *"the title and
  description carry what a column cannot … an assumption the rows cannot state."* **A4 working as
  designed.**

## LOST `1.4-depth-combined-scenario` — eval-14-weekly-pay

Grader said: _No row has nonzero Weekday, Sunday, and Holiday hours simultaneously; e.g. 'Negative and positive components net to zero | -10 | 5 | 0'_

- Output: `eval-14-weekly-pay/outputs/`
- Narration: `eval-14-weekly-pay/narration.md`
- Raw transcript: `eval-14-weekly-pay/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): **Rule 01's conjunction test (cluster 3 / B2), applied to hour types.**
  iteration-52 had one calculation table with an `All hour types combined | 40 | 5 | 8 | 8` row.
  iteration-54 has one table per hour type, each holding the others at zero
  (`WeeklyPayCalculatorTest.java:13-42`), which is the new rule verbatim — *"give each condition its
  own table, holding the others satisfied."* The combined row has no table left to sit in. The
  floor-at-zero table (`:52-57`) is the closest thing and reaches only two of the three types
  (holiday is 0 in every row) — the agent believed it was the combining table (narration:45: *"the
  combining table with negative weekday and positive sunday hours is legitimate"*). **Weekday, Sunday
  and holiday hours are not independent conditions of one rule; they are three addends of one sum.
  The conjunction test does not distinguish those two cases.**

## WON `1.5-depth-error-edge-cases` — eval-14-weekly-pay

Grader said: _Negative and positive components net to zero | -10 | 5 | 0 | 10 | 0 ... Negative rate is rejected | ... | -0.01 | java.lang.IllegalArgumentException_

- Output: `eval-14-weekly-pay/outputs/`
- Narration: `eval-14-weekly-pay/narration.md`
- Raw transcript: `eval-14-weekly-pay/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): **Same rule 10 change as `1.11`, one step earlier.** iteration-52 resolved
  the negative-hours ambiguity by declaring it unreachable in prose and writing no row for it
  (*"this arithmetic can never produce a negative total, so the 'pay cannot go below zero' rule has
  no reachable case here"*). iteration-54 chose a semantics and made it visible as rows
  (`WeeklyPayCalculatorTest.java:52-57`), reasoning at narration:23 *"hours can be negative (for
  adjustments) … I'll document this assumption clearly"*. The assumption went to `@Description` and
  the consequence went to rows — the division of labour the new rule 10 states.

## LOST `concern-not-over-split` — eval-14-weekly-pay

Grader said: _paysSundayHoursAtDoubleTime and paysHolidayHoursAtDoubleTime share the same fixture/output column and only vary which hour type is exercised, fragmenting one 'premium hours' concern across two tables_

- Output: `eval-14-weekly-pay/outputs/`
- Narration: `eval-14-weekly-pay/narration.md`
- Raw transcript: `eval-14-weekly-pay/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): **Rule 03's collapse instruction fired, was understood, and was then
  overruled by the no-branching quality rule.** The agent named the family unprompted
  (narration:35: *"Sunday and Holiday hours … are really one family of 'special hours' with different
  members. Combining them into a single table with an hour type column would make it explicit"*) and
  rejected the merge on mechanics (narration:37: *"I'd need to vary which parameter receives the
  hours value while keeping the other at zero, but that would require branching logic in the test
  method itself … That violates the 'no if/switch' quality rule"*). **This is not B2 over-splitting.
  It is a gap in cluster 3's own rule 03 text**, which says *"collapse them into one column keyed by
  member"* — a recipe that only works when the members are values of one parameter, not separate
  parameters. The available answer, which the agent never considered, is **one column per member in
  one table with the others left blank** (rule 19), which needs no branching. Nothing states it.

## WON `no-duplicate-rows-within-a-table` — eval-14-weekly-pay

Grader said: _paysRegularAndOvertimeRatesForWeekdayHours PASS (0/30/40/40.5 boundary rows); paysSundayHoursAtDoubleTime PASS (0/6); paysHolidayHoursAtDoubleTime PASS (0/8); floorsTotalWeeklyPayAtZero PASS (positive/zero/below-zero cases); rejectsNegativeHourlyRate PASS (zero/negative rate)_

- Output: `eval-14-weekly-pay/outputs/`
- Narration: `eval-14-weekly-pay/narration.md`
- Raw transcript: `eval-14-weekly-pay/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): **Rule 01's *"crossing them multiplies rows without adding a claim"* clause
  (cluster 3).** iteration-52's weekday table walked hours up to a fourth row, `Well past the
  threshold | 50`. iteration-54 dropped it, and the narration shows the deliberation in the rule's
  own terms (narration:31: *"questioning whether that last one might be redundant if the rule is
  already validated by the 40.5 case"*). The same win appeared in `table-driven-testing` eval-31 in
  iteration-7, so the shared rule reads correctly through two vocabularies.

## LOST `separates-classification-and-calculation` — eval-14-weekly-pay

Grader said: _'assertEquals(weeklyPay, WeeklyPayCalculator.calculateWeeklyPay(weekdayHours, 0, 0, hourlyRate)' mixes classification (weekday/overtime) and calculation (weeklyPay result) in one table/method_

- Output: `eval-14-weekly-pay/outputs/`
- Narration: `eval-14-weekly-pay/narration.md`
- Raw transcript: `eval-14-weekly-pay/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): **The API the agent designed changed, and the seam went with it.** eval-14
  is test-first, so the agent invents the signature. iteration-52 invented
  `classifyWeekdayHours(...) -> HoursSplit` plus `calculatePay(...)` — two functions, hence two
  tables, hence a pass. iteration-54 settled on one `calculateWeeklyPay(weekday, sunday, holiday,
  rate)` and got the seam nowhere. The reason is on record twice, and both citations are
  cluster-3 rules: narration:33 — *"a single public `calculateWeeklyPay` method with separate
  parameters for each hour category makes more sense — I can test each concern in isolation by
  zeroing out the others"* (rule 01's hold-the-others construction, now reinforced by the
  conjunction test) — and narration:29 — *"the overtime rule combining regular and overtime pay is
  actually one cohesive rule since it defines the complete weekday pay tier, which aligns with the
  'rules regarding tiers' pattern"* (rule 07). **Rule 01 chose the decomposition axis (by hour type)
  and rule 07 licensed merging the other axis (classify vs calculate) away.** Same root as
  `1.4-depth-combined-scenario`.

## LOST `2.21-readability-relative-time` — eval-15-reis-discount

Grader said: _Rows use absolute timestamps like '2026-07-05T08:00:00' rather than relative 'days ago' expressions_

- Output: `eval-15-reis-discount/outputs/`
- Narration: `eval-15-reis-discount/narration.md`
- Raw transcript: `eval-15-reis-discount/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): **Rule 10 (cluster 3 / A4) pushed the reference clock out of the converter
  and into a column, and the relative notation could not survive it.** iteration-53 — clusters 1–2,
  no A4 — wrote `[[daysAgo: 5, type: SINGLE], …]` with the reference time in a
  `REFERENCE_PURCHASE_TIME` field the converter read. That is a *conversion helper*, which rule 10's
  check names explicitly as not-declared, and the new wording adds *"a column wherever it can be
  one"*. iteration-54 duly made it the `Purchase Time` column — and once the reference is an absolute
  timestamp in one column, offsets in the neighbouring column no longer read against it, so every
  past purchase became absolute too
  (`SingleTicketTripCounterTest.java:26-32`, cells ~200 chars wide). **A4 and rule 15's "shorten the
  value" pull opposite ways here and nothing arbitrates.** The missing sentence: a declared reference
  value in one column licenses relative values in the columns measured from it.

## WON `held-constants-declared` — eval-15-reis-discount

Grader said: _SingleTicketTripCounterTest description states: 'Purchase history is assumed to already be scoped to the traveler buying the new ticket, so no filtering by traveler category is applied here.' declaring the held assumption; zone defaults are indifferent to outcome per spec._

- Output: `eval-15-reis-discount/outputs/`
- Narration: `eval-15-reis-discount/narration.md`
- Raw transcript: `eval-15-reis-discount/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): **Rule 10 (cluster 3 / A4), and this time the win is not new evidence.**
  The same slot won in iteration-53 *before* A4 existed, which the plan already flagged as an
  incomplete causal story. It has now won under both skill versions against the same iteration-52
  baseline, so **the registered A4 prediction for eval-14/15 is confirmed but not isolated** — the
  slot moves without A4. Note also `held-constants-declared`/eval-30 is on the flip list
  (`assertion-triage.md:579`). The unambiguous A4 evidence in this run is eval-14's `1.11` and
  eval-15's `2.21`, not this slot.

## WON `consistent-quantity-naming` — eval-29-shopping-cart-tt

Grader said: _'Cart After?' and 'Success?'/'Message?' reused consistently across addsItemPricedFromCatalogue and removesItemFromCart_

- Output: `eval-29-shopping-cart-tt/outputs/`
- Narration: `eval-29-shopping-cart-tt/narration.md`
- Raw transcript: `eval-29-shopping-cart-tt/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): **A side effect of rule 10 hoisting the fixtures out of the tables.**
  iteration-52 carried `Catalogue` and `Coupons` as columns whose contents varied per row, so the
  column set differed table by table. iteration-54 made both static fields declared in
  `@Description` (`CartServiceTest.java:17-33`, narration:41 *"a constant apparatus across all rows
  rather than a decision axis being tested"*), which left every table with the same short expectation
  set — `Cart After? | Success? | Message?`. Consistency here is a consequence of fewer, shorter
  columns, not of anything that targeted naming. `consistent-quantity-naming` also flips on eval-25
  (`assertion-triage.md:570`); treat this as weak evidence.

## LOST `quantifier-covered-by-rows` — eval-29-shopping-cart-tt

Grader said: _removesItemFromCart 'regardless of its quantity' claim: PASS (quantities 1,2 shown); appliesCouponReplacingActive 'Cart items do not affect this rule and are held empty throughout': FAIL (only empty cart value used, no variation shown)_

- Output: `eval-29-shopping-cart-tt/outputs/`
- Narration: `eval-29-shopping-cart-tt/narration.md`
- Raw transcript: `eval-29-shopping-cart-tt/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): **Rule 10 (cluster 3 / A4) created the claim that this assertion then
  failed.** The failing sentence did not exist in iteration-52, whose coupon-table `@Description` made
  no indifference claim at all. iteration-54 wrote *"Cart items do not affect this rule and are held
  empty throughout"* (`CartServiceTest.java:88-91`) — a held constant, correctly declared per the new
  rule 10, phrased as indifference. **An indifference claim is precisely what rule 01 and rule 08
  require a varying row for** (*"an input this rule claims not to affect the outcome … has to vary"*),
  and the agent varied nothing. So declaring the constant discharged rule 10 and incurred rule 08,
  and only the first fired. **Repair: rule 10 must hand off to rule 08 when the declaration says the
  value does not matter.** `table-driven-testing` eval-34 shows the same shared rule producing the
  right answer in the other vocabulary — it held booking value at 1000 *and added a 2000 row* — so
  this is salience in the JVM rendering, not a missing rule.
  **Confirmed.** This slot flips on eval-29 historically (`assertion-triage.md:576`, `p F p`), so a
  re-grade of the same stored outputs was run: `grading-vp54.json` reproduces **all 32 verdicts
  exactly**, this one included. The artefact evidence stands on its own anyway — the sentence the
  grader failed does not exist in the iteration-52 output.

## LOST `scenario-names-describe-conditions` — eval-29-shopping-cart-tt

Grader said: _'Fixed-amount coupon larger than the total floors at zero | ... | 0.00' names paraphrase the Total? cell value (0.00) directly_

- Output: `eval-29-shopping-cart-tt/outputs/`
- Narration: `eval-29-shopping-cart-tt/narration.md`
- Raw transcript: `eval-29-shopping-cart-tt/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): **Row compression, with the outcome absorbed into the surviving name.**
  iteration-52 had two rows — `Fixed-amount coupon equal to the subtotal` and `Fixed-amount coupon
  exceeding the subtotal` — both named by condition alone. iteration-54 keeps one
  (`CartServiceTest.java:126`) and appends the effect: *"larger than the total **floors at zero**"*.
  The compression is rule 01's row economy; the naming is rule 12, which nothing in cluster 2 or 3
  touched. **Weakest attribution on the page, and the slot is the worst offender on the instrument**
  — `F p F` on eval-29 (`assertion-triage.md:577`), and `assertion-triage.md:429` already records it
  as majority-*wrong* rather than merely unstable. **The confirming re-grade was run and agrees** —
  `grading-vp54.json` reproduces all 32 eval-29 verdicts — but two agreeing passes on a slot recorded
  as `F p F` is weak, and the assertion is on the repair list already. **Do not spend a cluster on
  this.**
