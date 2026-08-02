# Analysis to-do — spec-by-example, iteration 4

Compared against **iteration 3**, grading claude-sonnet-5/default.

**8 of 8 evals comparable.**

**13 assertion verdicts moved.**

These are deltas, not attributions. Before explaining any of them, read the generated
output for that eval and its narration — a grader justification can name the right
assertion and still name the wrong cause, and graders do misfire outright. Fill in the
cause line from the artefact, not from the justification.

Do not start the next iteration until every line below has a cause.

## WON `refund-table-shows-proportion` — eval-10-subscription-billing

Grader said: _Table 'Calculates Prorated Refund Amount' has a 'Daily Rate?' column before 'Refund Amount?'_

- Output: `eval-10-subscription-billing/outputs/`
- Narration: `eval-10-subscription-billing/narration.md`
- Raw transcript: `eval-10-subscription-billing/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): **Rule 10 (cluster 3 / A4), the same edit as eval-14's `1.11`.** iteration-3's
  refund table went straight from `Cycle Amount / Cycle Length / Days Remaining` to `Refund Amount
  (£)?`. iteration-4 inserts the intermediate `Daily Rate?` and adds a `Refund Window (Policy, hrs)`
  column for the 24-hour constant that iteration-3 stated only in prose (`response.md:27,40`).
  **A "(Policy)" threshold column appears independently in three evals this run** — eval-10's
  `Refund Window (Policy, hrs)`, eval-13's `Free Threshold (Policy) (£)`, and tabletest eval-14's
  `Overtime Threshold (hrs)`. Rule 17 was already in the skill; what changed is rule 10 no longer
  offering the description as an alternative home.

## WON `question-mark-only-on-outputs` — eval-12-subscription-loyalty-trial

Grader said: _Columns like 'Price?', 'Trial Granted?', 'Refund Eligible?' are outputs; 'Plan', 'Loyalty Member' lack '?'_

- Output: `eval-12-subscription-loyalty-trial/outputs/`
- Narration: `eval-12-subscription-loyalty-trial/narration.md`
- Raw transcript: `eval-12-subscription-loyalty-trial/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): **Cluster 1's rule 13 illustration fix (`ffb66d8`), exactly as the plan
  predicted it would show here.** iteration-3 wrote `Loyalty Member?` as an *input* column in three
  tables and `Refund Eligible?` as an input in a fourth. iteration-4 writes `Loyalty Member` bare and
  keeps `?` for outputs only (`response.md:18,35`). The plan reserved this slot as the read-out for
  the rule 13 fix rather than for cluster 1 as a whole — it is the one clean cluster-1 result in the
  run.

## LOST `no-duplicate-rows-within-a-table` — eval-13-shipping-partial-applicability

Grader said: _Table 1 PASS (single row); Table 2 PASS (3 distinct boundary rows); Table 3 FAIL: 'UK destination... yes | 14.99' and 'Ireland destination... yes | 14.99' are identical-outcome rows not merged into a {UK, Ireland} set_

- Output: `eval-13-shipping-partial-applicability/outputs/`
- Narration: `eval-13-shipping-partial-applicability/narration.md`
- Raw transcript: `eval-13-shipping-partial-applicability/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): **One artefact fact, two slots — see `overnight-grouped` below for the
  cause.** Both verdicts name the same pair of rows. Count this as one regression, not two.

## LOST `overnight-grouped` — eval-13-shipping-partial-applicability

Grader said: _Separate rows: 'UK destination | ... | yes | 14.99' and 'Ireland destination | ... | yes | 14.99' instead of one {UK, Ireland} row_

- Output: `eval-13-shipping-partial-applicability/outputs/`
- Narration: `eval-13-shipping-partial-applicability/narration.md`
- Raw transcript: `eval-13-shipping-partial-applicability/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): **Rule 08's value-set trigger is stated at column level and this case is at
  row level.** The agent is fluent with value sets in this very response — table 1 collapses both
  inputs (`{0.01, 49.99, 50.00, 500.00}`, `{UK, Ireland, France, USA}`), and in table 3 it collapses
  the *negative* pair, reasoning at narration:38 *"Since France and USA produce identical
  outcomes — both unavailable with blank costs — I can collapse them into a single value set rather
  than duplicate rows."* It then wrote UK and Ireland, which produce identical outcomes by the same
  test, as two rows. The tell is narration:48: *"the destination axis in Table 3 … is categorical
  (UK, Ireland, Rest-of-World)"* — three categories in the agent's model, so the catch-all category
  collapses and the two named ones do not. Rule 08's trigger, including cluster 2's addition, asks
  whether *one column could be `{true, false}` for every single row* — whole-column indifference.
  **Nothing asks the row-level question: do two members of this column produce the same outcome
  cell?** iteration-3 got it right (`{UK, Ireland}` in one row), so this is a real regression.
  Contributing: iteration-3 kept Standard and Express in one table with a `Shipping Method` axis and
  destination as a value-set column; iteration-4 splits into three tables (rule 01's conjunction
  test), which promotes destination to table 3's axis, and an axis invites one row per value.

## WON `standard-destination-value-set-or-blank` — eval-13-shipping-partial-applicability

Grader said: _Table 1: 'Any order value, any destination | {0.01, 49.99, 50.00, 500.00} | {UK, Ireland, France, USA} | 3.99'_

- Output: `eval-13-shipping-partial-applicability/outputs/`
- Narration: `eval-13-shipping-partial-applicability/narration.md`
- Raw transcript: `eval-13-shipping-partial-applicability/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): **Rule 08's cluster-2 addition, firing exactly where it is aimed.** The
  whole-column-indifferent case is what the new trigger describes, and the agent states it back in
  those terms (`response.md:20`: *"neither input affects the outcome, so both are value sets rather
  than duplicated rows"*). **Read this beside the `overnight-grouped` loss in the same file** — the
  pair is the evidence that the trigger works at column level and does not reach row level.

## WON `3.1a-concern-fulfillment-method` — eval-16-order-splitting

Grader said: _### 1. Splits Shipments Between Store Pickup and Home Delivery_

- Output: `eval-16-order-splitting/outputs/`
- Narration: `eval-16-order-splitting/narration.md`
- Raw transcript: `eval-16-order-splitting/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): **Rule 01's conjunction test (cluster 3 / B2). This is the target, and it
  hit.** iteration-3 produced one table titled *"Groups Order Lines into Shipment Buckets by
  Fulfillment, Address, **and** Ship Date"* — the conjunction the new rule is written against, and
  the exact title the plan quoted. iteration-4 produces six tables, three of them the previously
  merged dimensions (`response.md:9,21,34`). **eval-16 went 11/19 → 18/19; six of the seven predicted
  assertions moved** (`3.1a`, `3.1b`, `3.1c`, `3.2`, `3.3`, `3.4`, plus `concerns-decomposed`), and
  only `3.6-depth-companion-scenarios` did not. The rule fired in its own vocabulary and survived a
  real pull the other way: the agent considered collapsing the three back into one *"Splits Shipments
  by Hard Partition Dimension"* family table (narration:33, rule 03's family language) and rejected
  it — *"each addresses an independent business concern"* (narration:35). **The two cluster-3 rules
  are in direct tension on this artefact and the conjunction test won.** Six slots on one eval, so
  do not read the suite total as six independent confirmations.

## WON `3.1b-concern-delivery-address` — eval-16-order-splitting

Grader said: _### 3. Splits Shipments Across Differing Delivery Addresses_

- Output: `eval-16-order-splitting/outputs/`
- Narration: `eval-16-order-splitting/narration.md`
- Raw transcript: `eval-16-order-splitting/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): **Rule 01's conjunction test — same artefact fact as `3.1a`; see that entry.**

## WON `3.1c-concern-availability` — eval-16-order-splitting

Grader said: _### 2. Delays Shipment Until Items Are Available to Ship_

- Output: `eval-16-order-splitting/outputs/`
- Narration: `eval-16-order-splitting/narration.md`
- Raw transcript: `eval-16-order-splitting/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): **Rule 01's conjunction test — same artefact fact as `3.1a`; see that entry.**

## WON `3.2-depth-fulfillment-scenarios` — eval-16-order-splitting

Grader said: _All items requested for home delivery ... All items requested for store pickup ... Order mixes pickup and delivery items_

- Output: `eval-16-order-splitting/outputs/`
- Narration: `eval-16-order-splitting/narration.md`
- Raw transcript: `eval-16-order-splitting/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): **Rule 01's conjunction test — same artefact fact as `3.1a`; see that entry.**

## WON `3.3-depth-delivery-address-scenarios` — eval-16-order-splitting

Grader said: _All items go to the same address ... One item is a gift sent to a different address_

- Output: `eval-16-order-splitting/outputs/`
- Narration: `eval-16-order-splitting/narration.md`
- Raw transcript: `eval-16-order-splitting/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): **Rule 01's conjunction test — same artefact fact as `3.1a`; see that entry.**

## WON `3.4-depth-availability-scenarios` — eval-16-order-splitting

Grader said: _All items in stock ... one item in stock, one on pre-order ... Two backordered items have different expected dates_

- Output: `eval-16-order-splitting/outputs/`
- Narration: `eval-16-order-splitting/narration.md`
- Raw transcript: `eval-16-order-splitting/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): **Rule 01's conjunction test — same artefact fact as `3.1a`; see that entry.**

## WON `concerns-decomposed` — eval-16-order-splitting

Grader said: _Six numbered tables: pickup/delivery split, availability delay, address split, warehouse minimization, companion bundling, precedence_

- Output: `eval-16-order-splitting/outputs/`
- Narration: `eval-16-order-splitting/narration.md`
- Raw transcript: `eval-16-order-splitting/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): **Rule 01's conjunction test — same artefact fact as `3.1a`; see that entry.**

## LOST `4.4-depth-item-operations` — eval-17-shopping-cart

Grader said: _No scenario adds a *different* new item to a non-empty cart, and no scenario removes the last item leaving the cart empty._

- Output: `eval-17-shopping-cart/outputs/`
- Narration: `eval-17-shopping-cart/narration.md`
- Raw transcript: `eval-17-shopping-cart/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): **Row economy cut two rows that carried distinct coverage obligations.**
  iteration-3's add table had 6 rows including *"New product into a non-empty cart"*; its remove table
  had 5 including *"Removal empties the line exactly"*. iteration-4 has 4 and 2 (`response.md:7-10,
  18-19`) — and **the two rows it dropped are exactly the two the assertion names.** The
  consolidation is deliberate and on record: narration:41 *"I should consolidate by using row 5's
  qty=1 as the simplest success case instead of row 1's qty=2 … This removes the redundancy while
  preserving both the basic happy path and the boundary coverage"*, and narration:33 reads rule 06
  as a cutting rule — *"the concern about 'One Obligation, One Row' suggests avoiding duplicate test
  scenarios"*. `no-duplicate-rows-within-a-table` passed on the same tables. **This is the cluster-3
  trade in its clearest form: excess-row assertions win, coverage assertions lose.** Same shape as
  tabletest eval-14's `1.4-depth-combined-scenario`. Rule 01's *"crossing them multiplies rows
  without adding a claim"* has no counterweight naming the rows that must survive a consolidation.
