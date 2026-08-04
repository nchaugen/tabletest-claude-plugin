# Analysis to-do — tabletest, iteration 60

Compared against **iteration 58**, grading claude-sonnet-5/default.

**1 of 1 evals comparable.**

**12 assertion verdicts moved.**

These are deltas, not attributions. Before explaining any of them, read the generated
output for that eval and its narration — a grader justification can name the right
assertion and still name the wrong cause, and graders do misfire outright. Fill in the
cause line from the artefact, not from the justification.

Do not start the next iteration until every line below has a cause.

## WON `2.15-ticket-count-uses-value-sets` — eval-15-reis-discount

Grader said: _"{0, 4}", "{5, 9}", "{10, 14}", "{40, 44}", "{45, 100}" used as tier boundaries_

- Output: `eval-15-reis-discount/outputs/`
- Narration: `eval-15-reis-discount/narration.md`
- Raw transcript: `eval-15-reis-discount/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): **Group A — the ladder table went to a value set per tier.** iteration-58 wrote 17 plain rows
  (4, 5, 9, 10, 14, 15, 19, 20, 24, 25, 29, 30, 34, 35, 39, 40, 50); iteration-60 writes five rows of
  `{ADULT, SENIOR} | {n, m} | x%`. Same edit, four wins and one loss — see `2.19-depth-all-tiers`.
  What pushed it there is the batch's value-set pressure, visible in narration:93-97 drafting
  `{0,4} {5,9} {10,14} {35,39} {40,44} {45,100}`.

## LOST `2.17-zone-irrelevance-visible` — eval-15-reis-discount

Grader said: _ReisDiscountPolicyTest has no Zone column at all; zone-independence of the discount percentage itself is never exercised_

- Output: `eval-15-reis-discount/outputs/`
- Narration: `eval-15-reis-discount/narration.md`
- Raw transcript: `eval-15-reis-discount/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): **Group C, with a gap worth naming.** iteration-58 carried `{ZONE_1, ZONE_2, ZONE_3}` in the
  discount table; iteration-60 has no zone column in `ReisDiscountPolicyTest` because the API it chose
  takes no zone. narration:61 — *"by not having a zone parameter, the API implicitly says zone is
  irrelevant"* — and it then applied the varying rule to the counter table only. **§ G item 1 governs
  an indifference claim you write in your own description; it does not reach an indifference claim the
  *requirement* makes about an input your design dropped.** That is B3 territory (name the seam you
  cannot test) and it is one draw on one eval, so it is recorded rather than repaired.

## WON `2.18-adult-senior-value-set` — eval-15-reis-discount

Grader said: _"Below the first discount tier | {ADULT, SENIOR} | {0, 4} | 0%"_

- Output: `eval-15-reis-discount/outputs/`
- Narration: `eval-15-reis-discount/narration.md`
- Raw transcript: `eval-15-reis-discount/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): **Group A, and it overturns a prediction.** § J recorded adult/senior as *"item 4's first negative
  case … its wording does not reach this case"*, and this run's registered prediction was that it
  would not move. It moved: the ladder table carries `{ADULT, SENIOR}` on every row. So something in
  the batch does reach it — the candidates are J1's narrowed floor bullet (ask which rule names the
  branch; no table here tells adult from senior) and `73071be`'s *collapsing means the value set*.
  One draw, two candidate causes, so record the fact and do not credit a specific item.

## LOST `2.19-depth-all-tiers` — eval-15-reis-discount

Grader said: _Only 0%, 5%, 10%, and 40% tiers appear; 15%, 20%, 25%, 30%, 35% are missing_

- Output: `eval-15-reis-discount/outputs/`
- Narration: `eval-15-reis-discount/narration.md`
- Raw transcript: `eval-15-reis-discount/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): **Group A's cost, and the agent overrode the rule knowingly.** narration:99 —
  *"Since this is a continuous linear formula with roughly nine tiers plus the cap, testing every
  boundary might be overkill — but the coverage rule does say every tier needs to be represented at
  least once. two consecutive tiers with their deltas will uniquely determine the linear formula.
  I'll cap the table at five rows."* It had already drafted `{35,39}`→35% at narration:97 and dropped
  it. So rule 07 fired, was read correctly, and lost to a row-economy argument it does not answer.
  **Repaired the same day:** rule 07 now says a formula behind the tiers does not reduce the tiers,
  and that collapsing a tier into a value set is economy inside a row rather than licence to drop
  rows. Unmeasured.

## WON `2.20-readability-one-row-per-tier` — eval-15-reis-discount

Grader said: _"Fifth ticket reaches the first discount tier | {ADULT, SENIOR} | {5, 9} | 5%" single row per tier_

- Output: `eval-15-reis-discount/outputs/`
- Narration: `eval-15-reis-discount/narration.md`
- Raw transcript: `eval-15-reis-discount/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): **Group A.** One row per tier is what the value-set-per-tier shape produces; iteration-58's
  begins/holds pair per tier is what it replaced.

## WON `2.4-depth-rolling-window-boundary` — eval-15-reis-discount

Grader said: _"Single ticket exactly 30 days ago is inside the window ... 2" and "Single ticket 31 days ago falls outside the window ... 1"_

- Output: `eval-15-reis-discount/outputs/`
- Narration: `eval-15-reis-discount/narration.md`
- Raw transcript: `eval-15-reis-discount/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): **The coin flip, landing the other way.** The eval never states whether a purchase exactly 30 days
  old counts; iteration-58 chose exclusive and failed, iteration-60 chose inclusive and passed, as
  iteration-54 did. Not attributable to anything in the batch — this is the trap already recorded in
  the plan's start-here, and the eval has to state the boundary before the slot is stable.

## WON `2.9-correctness-value-set-tier-semantics` — eval-15-reis-discount

Grader said: _Value sets like {0,4}->0%, {5,9}->5%, {10,14}->10%, {40,44}->40%, {45,100}->40% align consistently with the inclusive-count ladder without overlap._

- Output: `eval-15-reis-discount/outputs/`
- Narration: `eval-15-reis-discount/narration.md`
- Raw transcript: `eval-15-reis-discount/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): **Group A.** The ranges are correct and non-overlapping (`{0,4}`→0%, `{5,9}`→5%, `{10,14}`→10%,
  `{40,44}`→40%, `{45,100}`→40%), which is the semantic half of the same shape.

## LOST `concerns-decomposed` — eval-15-reis-discount

Grader said: _countsSingleTicketsPurchasedInTheTrailingThirtyDays merges window boundary ('30 days ago... 31 days ago'), countability (ticket type filtering: 'Period tickets in the window are not counted'), and aggregation ('Qualifying and non-qualifying purchases both present') into one table instead of separate tables, and no scheme-selection table exists at all._

- Output: `eval-15-reis-discount/outputs/`
- Narration: `eval-15-reis-discount/narration.md`
- Raw transcript: `eval-15-reis-discount/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): **Group C.** Grader's complaint is two-part: the counter table merges window boundary, ticket-type
  filtering and aggregation, and no scheme table exists. The second half is the `scheme-derived-once`
  cause above. The first half is the counter table's own shape — six rows covering three questions —
  which iteration-58 also had; what changed is that iteration-58's extra table made the class look
  decomposed anyway. Same trade, third slot.

## WON `no-table-reproves-another` — eval-15-reis-discount

Grader said: _No @TableTest re-proves another; countsSingleTicketsPurchasedInTheTrailingThirtyDays covers counting, appliesAFlatDiscountToChildren covers the flat rule, calculatesARisingDiscountForAdultsAndSeniors covers the ladder — distinct concerns not overlapping._

- Output: `eval-15-reis-discount/outputs/`
- Narration: `eval-15-reis-discount/narration.md`
- Raw transcript: `eval-15-reis-discount/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): **Consequence of the same decomposition, not a separate cause.** iteration-58 had a third table
  (`resolvesSingleTicketDiscountByTravelerCategory`) that re-derived the count through the full
  calculator; iteration-60 drops it, so nothing re-proves the counter. The same deletion is what costs
  `scheme-derived-once` and `2.17-zone-irrelevance-visible` — read the three together as one trade.

## WON `quantifier-covered-by-rows` — eval-15-reis-discount

Grader said: _Counter table: 'regardless of the traveler category or zone' discharged by row 'Recorded category and zone do not affect the count' covering CHILD/SENIOR/ADULT and ZONE_1/2/3. Ladder table: 'Adults and seniors are treated identically' discharged by value set {ADULT, SENIOR} across every row._

- Output: `eval-15-reis-discount/outputs/`
- Narration: `eval-15-reis-discount/narration.md`
- Raw transcript: `eval-15-reis-discount/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): **J2's second bound CONFIRMED — this is the slot the probe was bought for.** The cell now carries
  four keys, `[[daysAgo: 5, type: SINGLE, category: CHILD, zone: ZONE_1], …]`, where iteration-58's
  converter hard-coded `TravelerCategory.ADULT` and `ZoneValidity.ZONE_1`. The description quantifies
  over both (*"regardless of the traveler category or zone it was recorded under"*) and the row
  *"Recorded category and zone do not affect the count"* varies CHILD/SENIOR/ADULT against
  ZONE_1/2/3. narration:61 shows the bound firing in its own words: *"since PastPurchase has a
  zoneValidity field that's required, I should include a value set in the counter's test table to
  explicitly show that zone of past purchases doesn't affect the count."*

## LOST `scheme-derived-once` — eval-15-reis-discount

Grader said: _ReisDiscountPolicy.discountFor(TravelerCategory, int) never takes ticket type, so the category+ticket-type->scheme mapping (including period ticket->no discount) is never derived at all._

- Output: `eval-15-reis-discount/outputs/`
- Narration: `eval-15-reis-discount/narration.md`
- Raw transcript: `eval-15-reis-discount/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): **Group C — the agent picked a narrower seam and the composing table went with it.** It settled on
  `discountFor(TravelerCategory, int recentSingleTicketCount)` (narration:89) to avoid fabricating
  history, so ticket type never reaches the discount API and the category+ticket-type→scheme mapping
  has nowhere to be derived. narration:117 shows why no combining table replaced it:
  *"staying consistent with the guidance against combining tables that don't introduce new rules"* —
  rule 05 firing, pointed at more firmly by § G item 6's addition to rule 01. **Plausible batch
  effect, one draw, not repaired:** the alternative reading is that this is ordinary API-design
  variance on an eval that prescribes a three-layer decomposition, and rule 05 is right that pure
  wiring earns no table.

## WON `zone-independent-counting` — eval-15-reis-discount

Grader said: _"Recorded category and zone do not affect the count | [[...zone: ZONE_1], [...zone: ZONE_2], [...zone: ZONE_3]] | 4"_

- Output: `eval-15-reis-discount/outputs/`
- Narration: `eval-15-reis-discount/narration.md`
- Raw transcript: `eval-15-reis-discount/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): **J2's second bound, same artefact fact as `quantifier-covered-by-rows`** — the three-element
  history varying zone across ZONE_1/2/3 in one row. One edit, two slots.
