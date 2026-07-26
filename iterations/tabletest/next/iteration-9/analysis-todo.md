# Analysis to-do — tabletest variant=next, iteration 9

Compared against **official (iterations 41, 40 merged)**, grading claude-sonnet-5/default.

**2 of 2 evals comparable.**

**8 assertion verdicts moved.**

These are deltas, not attributions. Before explaining any of them, read the generated
output for that eval and its narration — a grader justification can name the right
assertion and still name the wrong cause, and graders do misfire outright. Fill in the
cause line from the artefact, not from the justification.

Do not start the next iteration until every line below has a cause.

## WON `2.16-no-duplicate-tier-mapping` — eval-15-reis-discount

Grader said: _dispatch table delegates via 'see discountLadderByTicketNumber for tier boundaries'; only proves delegation, not re-enumeration_

- Output: `eval-15-reis-discount/outputs/`
- Narration: `eval-15-reis-discount/narration.md`
- Raw transcript: `eval-15-reis-discount/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): TARGETED, real, and now measured on a SOUND run (iteration-8's eval-15 was void — the sandbox left the agent unable to find the provided `src/main`, so it invented its own types). The ladder is stated once; the baseline re-enumerated it in the category table and again in the end-to-end table. Same verdict as the void run reached, but this time against the real `PastPurchase`/`TravelerCategory`/`PurchaseHistoryRepository` API, so it counts.

## WON `2.19-depth-all-tiers` — eval-15-reis-discount

Grader said: _Rows cover 0,5,10,15,20,25,30,35,40 tiers plus a cap-check row in discountLadderByTicketNumber._

- Output: `eval-15-reis-discount/outputs/`
- Narration: `eval-15-reis-discount/narration.md`
- Raw transcript: `eval-15-reis-discount/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): TARGETED, real, sound run. The baseline showed 5 of 9 tiers (0/5/10/35/40); the variant enumerates all nine, one row each. This is the assertion the published skill already addressed at line 752 and still failed — promoting it out of the syntax subsection into a first-class section is what changed. The strongest single move in the cluster.

## WON `2.20-readability-one-row-per-tier` — eval-15-reis-discount

Grader said: _'First tier reached at ticket five | {5, 6, 7, 8, 9} | 5' shows one row per tier using a value set._

- Output: `eval-15-reis-discount/outputs/`
- Narration: `eval-15-reis-discount/narration.md`
- Raw transcript: `eval-15-reis-discount/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): TARGETED, real, sound run. The baseline split every tier into `Exactly at first tier | 5` plus `Within first tier | {6,7,8,9}`; the variant writes one row per tier with both boundaries inside the value set. Exactly the "Do not split a tier in two" instruction.

## LOST `2.21-readability-relative-time` — eval-15-reis-discount

Grader said: _Table uses absolute dates like '2024-07-31' and '2024-07-01 SINGLE', not relative 'days ago' notation._

- Output: `eval-15-reis-discount/outputs/`
- Narration: `eval-15-reis-discount/narration.md`
- Raw transcript: `eval-15-reis-discount/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): COLLATERAL, weakly attributable. The variant writes absolute dates (`2024-07-31`, `2024-07-01 SINGLE`) where the baseline used days-ago notation (`[1/SINGLE/ZONE_1]`). Nothing in the cluster's guidance asks for absolute dates; this looks like fallout from rewriting the purchase-history representation while restructuring the class. Cheap to fix and adjacent to the converter problem above — the same converter is where the relative-date conversion belonged.

## WON `no-table-reproves-another` — eval-15-reis-discount

Grader said: _Each table isolates a distinct concern (ladder, counting, dispatch); the plain @Test is not a @TableTest re-proving rules._

- Output: `eval-15-reis-discount/outputs/`
- Narration: `eval-15-reis-discount/narration.md`
- Raw transcript: `eval-15-reis-discount/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): TARGETED, real, sound run. `calculatesDiscountEndToEnd` is gone. Note what did NOT happen this time: in the void run the wiring became two plain `@Test` methods, which cost `title-states-system-behaviour` and part of `quantifier-covered-by-rows`. On the sound run those two losses did not recur, so that side effect was an artefact of the invented API, not of the guidance.

## LOST `quantifier-covered-by-rows` — eval-15-reis-discount

Grader said: _parsePastPurchase converter hardcodes 'new PastPurchase(purchasedAt, TravelerCategory.ADULT, ticketType, ZoneValidity.ZONE_1)' for every row, so the claimed zone-irrelevance is narrated but never exercised across different zones._

- Output: `eval-15-reis-discount/outputs/`
- Narration: `eval-15-reis-discount/narration.md`
- Raw transcript: `eval-15-reis-discount/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): REAL REGRESSION, same single root cause as `zone-independent-counting` above — `parsePastPurchase` hardcodes `ZoneValidity.ZONE_1`, so the zone-irrelevance claim is "narrated but never exercised" in the grader's words. Recurred on the sound run. One wording fix should take both slots. (In the void run this assertion failed for a different reason — a `@Test` that could not quantify — which is why the void run could not have told us this.)

## LOST `zone-independent-counting` — eval-15-reis-discount

Grader said: _parsePastPurchase converter hardcodes 'ZoneValidity.ZONE_1' for every history entry, never varying zone in the counting table._

- Output: `eval-15-reis-discount/outputs/`
- Narration: `eval-15-reis-discount/narration.md`
- Raw transcript: `eval-15-reis-discount/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): REAL REGRESSION, and it recurred on the sound run, so it is not an artefact — this is the cluster's most important loss. Root cause is shared with `quantifier-covered-by-rows` below: `parsePastPurchase` hardcodes `ZoneValidity.ZONE_1` for every row, so zone never varies and the baseline's `Zone does not affect the count` row has nothing behind it. **The guidance caused this by being read one step too far.** "A value the rule is indifferent to" tells the agent to collapse such an input into a *value set* — one row, still varying. The agent instead removed the variation altogether and pinned the value inside a `@TypeConverter`, which makes the indifference claim unfalsifiable. **Fix: say that an indifferent input must still be shown varying, in a value set; pinning it in a converter or the method body is what `2.17-zone-irrelevance-visible` and this assertion exist to catch.** Note this is also cluster 4's A2 material (a held constant hidden in a converter), so the fix belongs in both places.

## WON `no-duplicate-rows-within-a-table` — eval-18-convert-from-code

Grader said: _evaluatesRenewalShortcut: 3 distinct rows (renewal-zero, renewal-one, non-renewal-zero); evaluatesRiskRejectionThreshold: 2 boundary rows (75/76); evaluatesPremiumByAgeTier: 2 boundary rows (64/65) - all PASS, no re-covered obligations_

- Output: `eval-18-convert-from-code/outputs/`
- Narration: `eval-18-convert-from-code/narration.md`
- Raw transcript: `eval-18-convert-from-code/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): TARGETED, real, sound run. The baseline's `Well above threshold | 50 | 10 | REJECTED` (redundant beside `Risk score just above threshold`) is gone; the risk table now carries only the straddling pair `At the risk limit | 8 | 5 | APPROVED` / `Just past the risk limit | 15 | 5 | REJECTED`, whose outcomes differ. The age table likewise keeps only 64/65.
