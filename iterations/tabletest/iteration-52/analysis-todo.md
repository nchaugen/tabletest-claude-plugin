# Analysis to-do — tabletest variant=next, iteration 10

Compared against **official (iterations 51, 50, 49, 48, 47, 46, 45, 44, 43, 42, 41, 40 merged)**, grading claude-sonnet-5/default.

## ⚠️ Partial comparison

**13 of 14 evals comparable.** The rest were excluded from them:

- `eval-25-convert-from-spock` — generation failed (timeout or crash), so this eval produced no answer to compare — re-run it before reading anything into the gap

**30 assertion verdicts moved.**

> **Gate closed 2026-08-01. Comparable 13 evals: 260/285 -> 260/285, exactly flat. 15 won, 15 lost.**
> All four pre-registered predictions held.
>
> **The falsifier is cleared, and it is the point of this run.** The prediction was that a drop of
> more than ~5 slots would mean the *reorganisation itself* is harmful — which would apply to all
> three skills, since the two thin suites gained content and were reordered at once and cannot
> separate the two effects. `tabletest` can, because for it the core is mostly its own text moved.
> **It did not drop at all.** Reordering into axis / what the reader sees / what is left in the body
> costs nothing.
>
> **Read the 30 moved verdicts as regeneration churn, not as findings.** This is a fresh generation,
> so the outputs differ; movement is expected and the net plus the *absence of a pattern* is the
> signal. Losses are scattered over six evals with no assertion lost more than twice, and **eval-15
> alone accounts for 5 of the 15 losses and 5 of the 15 wins** while ending on exactly the same score
> (26/34) — one hard eval churning, not a regression.
>
> `concern-not-over-split` won 1 of its 4 (predicted: no material improvement — held; rule 03 carries
> `tabletest`'s own wording, so there was nothing new for it to fire on). `concerns-decomposed` lost
> twice, and its text is still **un-unified** — sharing it needs its own regrade, so it is not
> attributable here.
>
> **eval-25 is excluded, not scored: it timed out at the 900s ceiling** after 1275s of wall clock.
> **Not a dropped connection** — `narration.md` shows no `api_retry`, and the agent was writing files
> and about to run tests when the budget ran out. Its stored runs are 356s, 409s and 514s, so this is
> the longer skill costing generation time on the suite's slowest eval. **Re-run it before reading
> anything into the gap.**


These are deltas, not attributions. Before explaining any of them, read the generated
output for that eval and its narration — a grader justification can name the right
assertion and still name the wrong cause, and graders do misfire outright. Fill in the
cause line from the artefact, not from the justification.

Do not start the next iteration until every line below has a cause.

## LOST `1.11-format-description` — eval-14-weekly-pay

Grader said: _Overtime is paid at 1.5x the base rate; Sunday and holiday hours are always paid at 2x. -- restates multipliers already shown by rows_

- Output: `eval-14-weekly-pay/outputs/`
- Narration: `eval-14-weekly-pay/narration.md`
- Raw transcript: `eval-14-weekly-pay/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## WON `1.3-depth-overtime-boundary` — eval-14-weekly-pay

Grader said: _At the threshold | 40 | 40 ... ; Just past the threshold | 40.5 | 40, in splitsWeekdayHoursIntoRegularAndOvertime, column 'Weekday Hours'_

- Output: `eval-14-weekly-pay/outputs/`
- Narration: `eval-14-weekly-pay/narration.md`
- Raw transcript: `eval-14-weekly-pay/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## LOST `1.5-depth-error-edge-cases` — eval-14-weekly-pay

Grader said: _No row anywhere tests negative hours; Description states 'the spec only requires rejecting a negative rate ... not negative hours'_

- Output: `eval-14-weekly-pay/outputs/`
- Narration: `eval-14-weekly-pay/narration.md`
- Raw transcript: `eval-14-weekly-pay/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## WON `1.7-readability-scenario-names` — eval-14-weekly-pay

Grader said: _Regular hours only, All hour types combined, Below the threshold -- describe work patterns_

- Output: `eval-14-weekly-pay/outputs/`
- Narration: `eval-14-weekly-pay/narration.md`
- Raw transcript: `eval-14-weekly-pay/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## LOST `no-duplicate-rows-within-a-table` — eval-14-weekly-pay

Grader said: _splitsWeekdayHoursIntoRegularAndOvertime: 'Just past the threshold 40.5' and 'Well past the threshold 50' both re-show the same overtime split arithmetic with no new branch — FAIL; calculatesTotalPayFromClassifiedHoursAndRate: PASS (each row discharges a distinct band); rejectsNegativeHourlyRate: PASS (value set + boundary row)_

- Output: `eval-14-weekly-pay/outputs/`
- Narration: `eval-14-weekly-pay/narration.md`
- Raw transcript: `eval-14-weekly-pay/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## WON `2.1-decomposition-concern-separation` — eval-15-reis-discount

Grader said: _DiscountLadderTest takes 'Prior Single Tickets' int directly; scheme mapping lives in ReisDiscountCalculatorTest, separate tables._

- Output: `eval-15-reis-discount/outputs/`
- Narration: `eval-15-reis-discount/narration.md`
- Raw transcript: `eval-15-reis-discount/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## LOST `2.15-ticket-count-uses-value-sets` — eval-15-reis-discount

Grader said: _DiscountLadderTest rows use single boundary integers like '3','4','8','9','38','39','44' not value sets_

- Output: `eval-15-reis-discount/outputs/`
- Narration: `eval-15-reis-discount/narration.md`
- Raw transcript: `eval-15-reis-discount/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## WON `2.17-zone-irrelevance-visible` — eval-15-reis-discount

Grader said: _Zone column '{ZONE_1, ZONE_2, ZONE_3}' in ReisDiscountCalculatorTest rows_

- Output: `eval-15-reis-discount/outputs/`
- Narration: `eval-15-reis-discount/narration.md`
- Raw transcript: `eval-15-reis-discount/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## LOST `2.19-depth-all-tiers` — eval-15-reis-discount

Grader said: _DiscountLadderTest rows only cover 0,5,10,35,40 percent tiers, missing 15,20,25,30_

- Output: `eval-15-reis-discount/outputs/`
- Narration: `eval-15-reis-discount/narration.md`
- Raw transcript: `eval-15-reis-discount/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## LOST `2.20-readability-one-row-per-tier` — eval-15-reis-discount

Grader said: _Rows '4 |5' and '8|5' both represent the 5% tier split across two rows instead of one_

- Output: `eval-15-reis-discount/outputs/`
- Narration: `eval-15-reis-discount/narration.md`
- Raw transcript: `eval-15-reis-discount/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## WON `2.21-readability-relative-time` — eval-15-reis-discount

Grader said: _History notation like '5d;15d;29d' expresses days before REFERENCE_PURCHASE_TIME_

- Output: `eval-15-reis-discount/outputs/`
- Narration: `eval-15-reis-discount/narration.md`
- Raw transcript: `eval-15-reis-discount/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## LOST `2.9-correctness-value-set-tier-semantics` — eval-15-reis-discount

Grader said: _DiscountLadderTest rows use single integer counts, not value sets, so tier value-set correctness isn't demonstrated._

- Output: `eval-15-reis-discount/outputs/`
- Narration: `eval-15-reis-discount/narration.md`
- Raw transcript: `eval-15-reis-discount/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## LOST `held-constants-declared` — eval-15-reis-discount

Grader said: _RecentPurchaseCounterTest history entries never vary traveler category (default ADULT via converter), yet category affects countability per the domain rule and this constant is not named in the column, title, or @Description._

- Output: `eval-15-reis-discount/outputs/`
- Narration: `eval-15-reis-discount/narration.md`
- Raw transcript: `eval-15-reis-discount/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## WON `new-purchase-inclusion-published` — eval-15-reis-discount

Grader said: _Both tables use column 'Prior Single Tickets (30 Days)?' and DiscountLadderTest @Description states 'Prior Single Tickets of 4 means this purchase is the fifth ticket'_

- Output: `eval-15-reis-discount/outputs/`
- Narration: `eval-15-reis-discount/narration.md`
- Raw transcript: `eval-15-reis-discount/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## LOST `no-table-reproves-another` — eval-15-reis-discount

Grader said: _ReisDiscountCalculatorTest rows use history counts 4 and 9 mapping to 5% and 10%, the same exact mappings already proven in DiscountLadderTest's 'Prior Single Tickets' 4->5 and 9->10 rows, re-deriving the ladder's specific thresholds in an integration table._

- Output: `eval-15-reis-discount/outputs/`
- Narration: `eval-15-reis-discount/narration.md`
- Raw transcript: `eval-15-reis-discount/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## WON `quantifier-covered-by-rows` — eval-15-reis-discount

Grader said: _ReisDiscountCalculatorTest's 'Child gets a flat discount regardless of history' row and its @Description's 'routed to the ladder regardless of zone' are discharged via {ZONE_1, ZONE_2, ZONE_3} value sets and varied history rows._

- Output: `eval-15-reis-discount/outputs/`
- Narration: `eval-15-reis-discount/narration.md`
- Raw transcript: `eval-15-reis-discount/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## WON `window-boundary-uses-purchase-time` — eval-15-reis-discount

Grader said: _'30d1s' entry distinguishes 30 days from 30 days + 1 second, sub-day granularity via converter._

- Output: `eval-15-reis-discount/outputs/`
- Narration: `eval-15-reis-discount/narration.md`
- Raw transcript: `eval-15-reis-discount/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## WON `black-box-columns` — eval-18-convert-from-code

Grader said: _Columns: 'Applicant Type | Age | Claim Count | Decision? | Premium?' — no internal state columns._

- Output: `eval-18-convert-from-code/outputs/`
- Narration: `eval-18-convert-from-code/narration.md`
- Raw transcript: `eval-18-convert-from-code/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## WON `concern-not-over-split` — eval-18-convert-from-code

Grader said: _Only a single @TableTest method 'decidesApplicationOutcome' exists in the class, so no concern is fragmented across multiple same-fixture tables_

- Output: `eval-18-convert-from-code/outputs/`
- Narration: `eval-18-convert-from-code/narration.md`
- Raw transcript: `eval-18-convert-from-code/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## LOST `concerns-decomposed` — eval-18-convert-from-code

Grader said: _Single @TableTest 'decidesApplicationOutcome' mixes both Decision? and Premium? in one monolithic table instead of two concern tables._

- Output: `eval-18-convert-from-code/outputs/`
- Narration: `eval-18-convert-from-code/narration.md`
- Raw transcript: `eval-18-convert-from-code/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## WON `no-duplicate-rows-within-a-table` — eval-18-convert-from-code

Grader said: _decidesApplicationOutcome: PASS — each row (renewal override, claim fallthrough, threshold 75/76, senior 64/65) discharges a distinct obligation, no excess repetition._

- Output: `eval-18-convert-from-code/outputs/`
- Narration: `eval-18-convert-from-code/narration.md`
- Raw transcript: `eval-18-convert-from-code/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## WON `observable-io-only` — eval-18-convert-from-code

Grader said: _No column named riskScore, hasActivePolicy, or internalRiskScore appears in the table header._

- Output: `eval-18-convert-from-code/outputs/`
- Narration: `eval-18-convert-from-code/narration.md`
- Raw transcript: `eval-18-convert-from-code/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## LOST `concerns-decomposed` — eval-2-parse-dates

Grader said: _Single @TableTest 'parsesDateString' mixes valid formats and error/empty-string case in one table_

- Output: `eval-2-parse-dates/outputs/`
- Narration: `eval-2-parse-dates/narration.md`
- Raw transcript: `eval-2-parse-dates/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## LOST `separates-valid-and-invalid` — eval-2-parse-dates

Grader said: _@TableTest("""Scenario | Input | Parsed? | Throws?\n...Empty string | '' | | java.lang.IllegalArgumentException") combines valid and error rows in one table_

- Output: `eval-2-parse-dates/outputs/`
- Narration: `eval-2-parse-dates/narration.md`
- Raw transcript: `eval-2-parse-dates/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## LOST `cutoff-date-column-if-literal-dates` — eval-22-event-registration-tt

Grader said: _No separate cutoff column exists; cutoff only appears in @Description text, not a table column_

- Output: `eval-22-event-registration-tt/outputs/`
- Narration: `eval-22-event-registration-tt/narration.md`
- Raw transcript: `eval-22-event-registration-tt/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## LOST `descriptive-registration-date` — eval-22-event-registration-tt

Grader said: _Table uses literal dates: '2025-03-01', '2025-02-28' instead of 'before cutoff'/'after cutoff'_

- Output: `eval-22-event-registration-tt/outputs/`
- Narration: `eval-22-event-registration-tt/narration.md`
- Raw transcript: `eval-22-event-registration-tt/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## WON `coupon-before-after-columns` — eval-29-shopping-cart-tt

Grader said: _Scenario | Coupons | Active Before | Code Entered | Success? | Active After? | Message?_

- Output: `eval-29-shopping-cart-tt/outputs/`
- Narration: `eval-29-shopping-cart-tt/narration.md`
- Raw transcript: `eval-29-shopping-cart-tt/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## WON `quantifier-covered-by-rows` — eval-29-shopping-cart-tt

Grader said: _No @DisplayName/@Description/method name in the class uses quantifying language such as 'regardless of', 'any', 'whatever', or 'with or without' — e.g. descriptions only state 'Only one coupon can be active at a time' and notation explanations, no quantifier claims found, so the assertion passes trivially._

- Output: `eval-29-shopping-cart-tt/outputs/`
- Narration: `eval-29-shopping-cart-tt/narration.md`
- Raw transcript: `eval-29-shopping-cart-tt/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## WON `scenario-names-describe-conditions` — eval-29-shopping-cart-tt

Grader said: _Names like 'Stock falls short of the quantity needed' and 'Code not found in the coupon store, coupon already active' describe conditions, not paraphrase expectation cells_

- Output: `eval-29-shopping-cart-tt/outputs/`
- Narration: `eval-29-shopping-cart-tt/narration.md`
- Raw transcript: `eval-29-shopping-cart-tt/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## LOST `type-converters-for-complex-objects` — eval-29-shopping-cart-tt

Grader said: _Method bodies construct domain objects from raw table values, e.g. 'Cart cart = Cart.empty().withActiveCouponCode(activeBefore);' and 'Cart cart = Cart.withItems(items);' and 'Cart cart = Cart.withItems(cartItems);' instead of using a @TypeConverter for Cart in those methods._

- Output: `eval-29-shopping-cart-tt/outputs/`
- Narration: `eval-29-shopping-cart-tt/narration.md`
- Raw transcript: `eval-29-shopping-cart-tt/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 
