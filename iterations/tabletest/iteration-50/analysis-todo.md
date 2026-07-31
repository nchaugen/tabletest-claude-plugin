# Analysis to-do — tabletest, iteration 50

Compared against **iteration 49**, grading claude-sonnet-5/default.

## ⚠️ Partial comparison

**5 of 17 evals comparable.** The rest were excluded from them:

- `eval-2-parse-dates` — the baseline never ran this eval; there is nothing to compare against
- `eval-14-weekly-pay` — the baseline never ran this eval; there is nothing to compare against
- `eval-15-reis-discount` — the baseline never ran this eval; there is nothing to compare against
- `eval-18-convert-from-code` — the baseline never ran this eval; there is nothing to compare against
- `eval-22-event-registration-tt` — the baseline never ran this eval; there is nothing to compare against
- `eval-23-loan-approval-tt` — the baseline never ran this eval; there is nothing to compare against
- `eval-25-convert-from-spock` — the baseline never ran this eval; there is nothing to compare against
- `eval-26-convert-from-kotest` — the baseline never ran this eval; there is nothing to compare against
- `eval-27-convert-from-testng` — the baseline never ran this eval; there is nothing to compare against
- `eval-28-convert-from-methodsource` — the baseline never ran this eval; there is nothing to compare against
- `eval-29-shopping-cart-tt` — the baseline never ran this eval; there is nothing to compare against
- `eval-30-order-splitting-tt` — the baseline never ran this eval; there is nothing to compare against

**1 assertion verdict moved.**

These are deltas, not attributions. Before explaining any of them, read the generated
output for that eval and its narration — a grader justification can name the right
assertion and still name the wrong cause, and graders do misfire outright. Fill in the
cause line from the artefact, not from the justification.

Do not start the next iteration until every line below has a cause.

## WON `description-if-present-adds-information` — eval-7-permission-check

Grader said: _No @Description annotation is present in the @TableTest method, so the assertion is vacuously satisfied_

- Output: `eval-7-permission-check/outputs/`
- Narration: `eval-7-permission-check/narration.md`
- Raw transcript: `eval-7-permission-check/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): **Not a skill win — the grader correcting its own prior misfire on a vacuous assertion.** `PermissionCheckerTest.java` contains no `@Description` (grep: 0 occurrences across every output file), and the assertion's own text ends "It is acceptable to omit @Description if the table already conveys all relevant context" — so PASS is the correct verdict here. Iteration-49 FAILED the same omission, which the ledger already records as a grader misfire. The verdict moved because the grader changed its mind about an absent annotation, not because the output did.
- **Consequence:** `description-if-present-adds-information` is **vacuous whenever `@Description` is absent** — it cannot fail, and it flip-flops on the same input. This is the same defect as `annotation-order` (TODO, from `iteration-47`: satisfied by writing no `@DisplayName` at all). Two assertions now share it; fold both into that item and decide together — require the annotation in the same assertion, or let a sibling carry that half.

---

# Addendum — comparison against the official baseline

The run above compared against **iteration 49**, which held only 5 evals, so it reported one moved
verdict. `--compare-official` was not passed. Recomputed here against the merged official baseline
(newest result per eval across iterations 40–49, failed generations skipped — the rule
`loadOfficialBenchmark` applies), covering all 14 evals that produced output.

**Fingerprints are identical for every comparable eval**, so the instrument did not move.

**272/297 for iteration 50 against 272/297 for the baseline — exactly level.** The net hides the
movement: **9 slots won, 9 lost, across 8 of the 14 evals.**

| Eval | it-50 | Baseline | Δ |
|---|---|---|---|
| 7-permission-check | 13/13 | 12/13 (it-49) | +1 (vacuous, see above) |
| 15-reis-discount | 22/28 | 25/28 (it-46) | **−3** |
| 18-convert-from-code | 19/27 | 22/27 (it-45) | **−3** |
| 22-event-registration-tt | 29/29 | 27/29 (it-46) | +2 |
| 23-loan-approval-tt | 23/23 | 21/23 (it-46) | +2 |
| 25-convert-from-spock | 25/27 | 21/27 (it-45) | +4 |
| 26-convert-from-kotest | 25/27 | 26/27 (it-45) | **−1** |
| 28-convert-from-methodsource | 22/26 | 24/26 (it-45) | **−2** |
| 14-weekly-pay | 19/22 | 19/22 (it-45) | 0, composition changed |

**Baselines are heterogeneous.** Evals 15/22/23 baseline on iteration-46, the rest on 45/47/49, so a
delta spans every skill change since that iteration — never a single edit. Iteration-46's ledger row
records eval-15 grading twice at 25/24, so its 25 is the high draw.

Causes for the four regressed evals follow. Each was read from `outputs/` and `narration.md`, and
each grader justification was checked against the artefact.

## LOST ×3 — eval-15-reis-discount (22/28, was 25/28)

`2.1-decomposition-concern-separation`, `2.17-zone-irrelevance-visible`, `quantifier-covered-by-rows`

- Output: `eval-15-reis-discount/outputs/src/test/java/com/example/ReisDiscountCalculatorTest.java`
- Baseline output: `iterations/tabletest/iteration-46/eval-15-reis-discount/outputs/`
- Narration: `eval-15-reis-discount/narration.md:41`

**Cause (from artefact): real, and both losses trace to the same decision — the agent designed an API
with no zone parameter, then asserted zone irrelevance in prose.**

Iteration-46 wrote `discountFor(travelerCategory, zone, recentTicketCount)` and gave its eligibility
table a `Zone` column varying `{ZONE_1, ZONE_2, ZONE_3}`. Iteration-50 wrote
`discountForTicketCount(travelerCategory, ticketsInLast30Days)` — no zone anywhere — and pinned
`ZONE_1` in the `@TypeConverter` (line 79). Its counting table's `@Description` says "Traveler
category and zone are irrelevant to the count, so history entries use fixed placeholder values for
them". Narration line 41 states the decision outright: *"Zone doesn't factor into the discount at
all — the calculator API has no zone parameter, so it structurally can't vary by zone."*

Both `2.17` and `quantifier-covered-by-rows` require the irrelevant input to appear and vary. Once
the agent removes it from the API it invents, no table-design rule can recover it. **The failure is
upstream of the table**, in the API shape chosen before any table was written. Eval-15 lets the agent
design the stubs, so this is reachable.

`2.1` is separate and also real: iteration-46 had a dedicated eligibility table
(`selectsDiscountRuleByCategory`); iteration-50 merged the `CHILD` flat-discount row into the ladder
table `resolvesReisDiscountFromTicketCount` and has no eligibility table at all.

**The candidate trigger is `390acf4` — "separate the two senses of an irrelevant input".** That commit
split the guidance into *never mentions it → hold at one obviously-valid value* and *says it does not
matter → vary it*. The output lands in neither branch cleanly: it holds the value fixed **and** makes
the irrelevance claim in the `@Description`, which the same commit's checklist line forbids ("never
merely asserted in `@Description`"). Iteration-50 is the first measurement of `390acf4` on eval-15,
and eval-15 is the assertion's home. **Treat this as the leading hypothesis, not a settled cause** —
it is one run, and the API-shape decision in narration:41 may be sufficient on its own.

## LOST ×3 — eval-18-convert-from-code (19/27, was 22/27)

`no-duplicate-rows-within-a-table`, `separates-decision-and-premium`, `rule-statable-from-table`

- Output: `eval-18-convert-from-code/outputs/src/test/java/com/example/InsuranceEvaluatorTest.java`
- Baseline output: `iterations/tabletest/iteration-45/eval-18-convert-from-code/outputs/`
- Narration: `eval-18-convert-from-code/narration.md:31-39`

Iteration-45 wrote **2 tables**; iteration-50 wrote **5**. The three losses do not share one cause —
one is real, one is a grader inconsistency, one is arguable.

**`rule-statable-from-table` — real.** Iteration-45's decision table published a `Risk Score?` column
(75 at the limit, 76 past it). Iteration-50 dropped it and published a constant `Risk Threshold | 75`
instead, so `Age 9 + 5 claims → APPROVED` against `Age 10 + 5 claims → REJECTED` is unreadable
without the hidden `age/10 + claims*15` formula. This is a genuine structural difference, caused by
the split: the risk score column did not survive being distributed across five tables. Caveat: this
is the **most unstable slot in the suite** (4 flips, `docs/assertion-triage.md:12`), so confirm with
`--grade-only --grading-suffix` before acting on it.

**`separates-decision-and-premium` — grader inconsistency, not an output regression.** The assertion
text is "Decision logic and premium calculation are in separate `@TableTest` methods". Iteration-50
has **three** premium-only methods (`selectsPremiumFormulaByAge`,
`computesStandardPremiumFromRiskScore`, `computesSeniorPremiumFromRiskScore`). The grader failed it
because two decision tables *also* carry a `Premium?` column — but iteration-45's
`routesApplicationToDecision` carried a `Premium?` column too and passed. Same shape, opposite
verdict. **Do not fix the skill for this one.** Corroboration: `docs/assertion-triage.md:190` records
`separates-decision-and-premium` on eval-18 as a slot the grader scored wrong against the answer key
at `medium` effort — so this assertion has misgraded on this eval before.

**`no-duplicate-rows-within-a-table` — arguable, low confidence.** The grader failed
`computesStandardPremiumFromRiskScore` for rows `0 → 106.0`, `2 → 166.0`, `4 → 226.0`, calling them
three repeats of one claim-impact pattern. Defensible under the assertion's excess-row test: two
points state the linear rule and the third adds nothing. Not clearly wrong, not clearly right.
**Re-grade before acting.**

## LOST ×2 (net −1) — eval-26-convert-from-kotest (25/27, was 26/27)

`rule-statable-from-table`, `consistent-quantity-naming` lost; `concern-not-over-split` won.

- Output: `eval-26-convert-from-kotest/outputs/src/test/kotlin/com/example/ShippingCostCalculatorTest.kt`
- Baseline output: `iterations/tabletest/iteration-45/eval-26-convert-from-kotest/outputs/`
- Narration: `eval-26-convert-from-kotest/narration.md:25,32`

**`consistent-quantity-naming` — real.** Iteration-45 named every output column `Cost?` and every
weight input `Weight (kg)`. Iteration-50 names the same computed cost `Base Rate?` in tables 1 and 2
and `Cost?` in tables 3 and 4, and names the same actual-weight input `Weight (kg)` in table 1 and
`Actual Weight (kg)` in table 2. Both are the assertion's own worked example. This assertion **has
never flipped** (`docs/assertion-triage.md:75`), so the loss is trustworthy.

**`rule-statable-from-table` — noise. Do not act on it.** The grader failed
`addsPackageSurchargesToBaseCost` for not publishing the surcharge amounts (10, 8, ×1.15, 0.6%).
Iteration-45's `appliesPackageOptionSurcharges` does not publish them either — the two tables and
their `@Description`s are structurally equivalent on this point, and the earlier one passed. This is
the flipping-on-structurally-identical-outputs case AGENTS.md names, on the assertion that flips most.

**Firing evidence worth keeping.** Narration line 32: the agent says splitting the original 17-row
table "follows the *one rule, one axis* and *separate rules from arithmetic* guidance" — both phrases
are in `skills/tabletest/SKILL.md` (line 368 and the § heading). The split guidance fires and is
quoted back; nothing pulls the column names back into line afterwards.

## LOST ×3 (net −2) — eval-28-convert-from-methodsource (22/26, was 24/26)

`concern-not-over-split`, `rule-falsifiable-by-a-row`, `consistent-quantity-naming` lost;
`rule-statable-from-table` won.

- Output: `eval-28-convert-from-methodsource/outputs/src/test/java/com/example/ShippingCostCalculatorTest.java`
- Baseline output: `iterations/tabletest/iteration-45/eval-28-convert-from-methodsource/outputs/`
- Narration: `eval-28-convert-from-methodsource/narration.md:29`

Iteration-45 combined the surcharges into one 6-row table; iteration-50 wrote 5 tables. All three
losses are real and all three are consequences of that split.

**`consistent-quantity-naming` — real.** `resolvesBaseRateByZoneAndWeight` reports `Base Rate?`;
`selectsGreaterOfActualOrDimensionalWeight` reports `Total Cost?` for the same quantity — that table
applies no surcharges, so the two names describe one value. Same defect as eval-26, same class, and
the assertion's caveat about a base rate and a final total being two quantities does not apply here.

**`rule-falsifiable-by-a-row` — real.** `selectsGreaterOfActualOrDimensionalWeight` (lines 59-63) has
two rows both expecting `7.50`. Condition (1) of the assertion fires; the invariance exemption does
not, because the table's claim is "uses whichever is greater", not an independence claim. A
fixed-return implementation passes both rows. The `@Description` even states the problem — "Both rows
resolve to the same effective weight (3.0kg) by different routes" — without recognising it.

**`concern-not-over-split` — real, and it is the one to act on.**
`addsOversizeSurchargeForLongDimensions` and `appliesPackageOptionSurcharges` both fix zone
`EU standard`, weight `3.0`, carrier DHL, and both report `Total Cost?`, each varying one surcharge
family. That is the assertion's stated failure shape, and it is also the shape `6239c0c` was written
to stop ("splitting instead gives you several tables that fix the same setup and report the same
output, which is the over-split"). **The rule is present and did not fire.** `6239c0c` put it in
`references/table-design-advanced.md`, while the split guidance the agent quotes on eval-26 is in
`SKILL.md` itself.

## What the four have in common

**Six of the nine losses are cross-table defects: what one table does is not reconciled with what its
siblings do.** Column names diverge for one quantity (26, 28), a published value does not survive
being distributed across tables (18), an invariance row is lost (28), and two tables end up sharing
one fixture (28). Each table is defensible read alone; the class is not.

**Table count is not the shape.** Eval-18 went 2 → 5 and eval-28 3 → 5, but eval-26 went 5 → 4 and
still lost `consistent-quantity-naming`. Splitting more is not the common factor — failing to
reconcile siblings is, at any count.

**The hypothesis to test next: the skill states per-table rules at higher volume than
across-table rules.** Evidence: the agent quotes the splitting rules back verbatim
(`narration.md:32` on eval-26) while nothing in the narration mentions checking names across tables;
the anti-over-split rule that would have caught eval-28 sits in `references/table-design-advanced.md`
rather than `SKILL.md`; and `consistent-quantity-naming` — a stable assertion — regressed on both
hosts that had fixed it.

`consistent-quantity-naming` has a history worth reading before treating this as new: it failed on
eval-26 in iteration-41, on eval-28 in iteration-44, and on eval-29 in iterations 40, 41, 43 and 45.
Both 26 and 28 were passing at iteration-45 and have now gone back. **This is a recurring failure the
suite has not held down, not a fresh one.** (Iteration-50's benchmark also lists it failing on evals
27 and 29 — ignore those; both errored and every assertion they own reads as failed.)

**Before acting, do the free read-back** (AGENTS.md § Developing a variant, step 2): grep `SKILL.md`
for what it says about naming a quantity consistently across sibling tables, and check whether
anything states it at the same volume as *A table is one rule varying along one axis*.

**Confirm the flip-prone slots first.** `rule-statable-from-table` (18, 26) and
`rule-falsifiable-by-a-row` (28) are on the unstable list. A re-grade of the stored outputs
(`--grade-only --grading-suffix`) costs no generation and settles them. `consistent-quantity-naming`
and `concern-not-over-split` need no confirmation.

## Not analysed here

- **eval-14-weekly-pay** is level at 19/22 with changed composition: won `1.11-format-description`,
  lost `1.7-readability-scenario-names`. Not covered by this addendum.
- **Evals 27, 29 and 30 produced no output** and are unmeasured. Their baselines are 24/26, 23/30 and
  25/25. Eval-27's measurement is still owed before slice 4 cuts it.

---

# Confirmation — second grading pass (`[v1]`, 2026-07-31)

`--grade-only --grading-suffix v1` over the same stored outputs, 14 evals, $1.45, no generation.
Evals 27/29/30 excluded: their `outputs/` hold only an empty `response.md`.

**272 → 274.** Eleven of fourteen evals re-graded identically, including eval-15 (six failures, same
set), eval-14 and eval-25. Three moved, on seven slot flips:

| Eval | g1 | g2 | Flips |
|---|---|---|---|
| 18-convert-from-code | 19/27 | **20/27** | `rule-statable-from-table` → PASS, `description-no-internals` → PASS, `no-table-reproves-another` → FAIL |
| 26-convert-from-kotest | 25/27 | **27/27** | `rule-statable-from-table` → PASS, `consistent-quantity-naming` → PASS |
| 28-convert-from-methodsource | 22/26 | **21/26** | `rule-statable-from-table` → FAIL |

## Two corrections to the addendum above

**1. eval-18's `rule-statable-from-table` is not a confirmed regression.** The addendum called it
"real", reasoning from the artefact: iteration-45 published a `Risk Score?` column and iteration-50
dropped it. The artefact reading stands, but the verdict does not survive a second grading — it
flipped to PASS. It flipped on all three evals that carry it (18 and 26 to PASS, 28 to FAIL), so
**three of the seven flips are one assertion.** That matches `assertion-triage.md:12`, which already
names it the least stable in the suite. Do not act on it in either direction.

**2. eval-26's `consistent-quantity-naming` is not a confirmed regression either, and the reason I
trusted it was wrong.** The addendum called it "trustworthy" because `assertion-triage.md:75` records
that it never flipped. **It flipped here** — eval-26 FAIL → PASS, taking that eval to 27/27. The
`Weight (kg)` / `Actual Weight (kg)` divergence is still in the output; the grader stopped counting
it. `assertion-triage.md` corrected.

Its sibling verdict on **eval-28 is stable across both passes**, so the assertion is not uniformly
unstable — it held on one host and flipped on the other.

## What survives both passes

Act only on these:

- **eval-15** — all six failures, identical sets. The `2.1` / `2.17` / `quantifier-covered-by-rows`
  regression is confirmed, and so is the API-shape cause in `narration.md:41`.
- **eval-28** — `concern-not-over-split`, `rule-falsifiable-by-a-row`, `consistent-quantity-naming`.
- **eval-18** — `no-duplicate-rows-within-a-table` and `separates-decision-and-premium`. Note the
  latter is stable *within* this run and still inconsistent *across* runs: iteration-45 passed a
  structurally similar output. That is a cross-run inconsistency, not grading noise, and the repair
  is drafted (`products/claude-plugin/plans/eval-15-dimension-model-draft.md` § 11).

## What this does to the headline

The addendum's "six of the nine losses are cross-table defects" now rests on five confirmed slots,
not six, and eval-26 has left the regression list entirely — under g2 it is **+1** against its
baseline rather than −1. The cross-table finding is weaker than first stated but not withdrawn: it
still carries eval-28's three and eval-18's two, all stable.

**The re-graded per-eval deltas against the official baseline** are 15 −3, 18 −2, 26 **+1**, 28 −3.
Do not add these up against the single-pass baseline and read the total as a result — the baseline is
one grading pass and this is another, so the comparison inherits the same ±3 spread it just measured.

## Consequence for the suite-edit window

**Three of eval-18's and eval-26's seven flips sit on `rule-statable-from-table`, which
`assertion-triage.md` has already scheduled for splitting by clause.** This run is the fourth
independent probe to say so. Raise its priority in the window: it is now costing more comparisons
than any other single assertion.
