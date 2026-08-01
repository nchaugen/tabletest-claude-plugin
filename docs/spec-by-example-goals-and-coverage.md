# Spec-by-Example: Skill Goals & Eval Coverage

Last updated: 2026-04-06

> **Every pass rate below is from `iteration-1` and is void** (2026-08-01). That baseline was graded
> on no recorded model, before the sonnet standard; `iteration-2` re-baselined the suite at 110/130.
> The assertion inventory is stale too: `minimal-rows-per-concern` was replaced by
> `no-duplicate-rows-within-a-table`, and five mechanical assertions are now graded in code. Read
> `evals/spec-by-example/*/eval.json` for the inventory and the newest `benchmark.json` for the
> rates. Rewrite this document from the slice 6 step 3 regrade.

## Purpose

The spec-by-example skill helps clarify requirements before implementation by working
through concrete examples organised in tables. The tables serve as a shared specification
— readable and verifiable by domain experts and developers alike — and a natural starting
point for TableTests when coding begins.

---

## Skill Goals

The goals are organised in three layers:

### Layer A: What the tables capture

1. **Clarify known rules** — Surface and pin down decision rules, validation rules,
   state transitions, thresholds, rule interactions, and default/fallback behavior
   through concrete examples.

2. **Surface unknown rules** — Explicitly identify ambiguities, unresolved decisions,
   and conflicting cases rather than silently resolving them.

3. **Specify precisely** — Distinguish what matters from what doesn't (value sets for
   irrelevant inputs), where boundaries are (threshold rows), and what's absent vs
   irrelevant (blank cell semantics).

### Layer B: How the tables are organised and expressed

4. **Separate concerns** — One table per decision or rule. Rules separate from arithmetic.

5. **Communicate across audiences** — Business language in columns and values, traceable
   outputs, scenario names that describe conditions.

### Layer C: Where the tables go next

6. **Produce TableTest-ready structure** — `?` output columns, scenario column, row
   independence, `{...}` value set notation.

---

## Eval Inventory

| # | Eval | Key Assertions | Iter 27 |
|---|------|----------------|---------|
| 4 | loan-approval | threshold-values-visible, senior-threshold-row, missing-income-marked-open, threshold-as-column, concerns-decomposed | 69% |
| 5 | order-transitions | cancellation-coverage, return-window-addressed, value-set-or-multiple-states, row-independence, concerns-decomposed | 100% |
| 6 | discount-interaction | does-not-invent-resolution, covers-both-discounts-applying, open-question-surfaced, extreme-discount-row | 86% |
| 10 | subscription-billing | tables-have-distinct-concerns, 24h-boundary-near-boundary, eligibility-separate-from-amount, rules-separate-from-arithmetic, concerns-decomposed | 50% |
| 12 | subscription-loyalty-trial | annual-no-trial-rule, loyalty-discount-annual-only, loyalty-refund-ambiguity, refund-table-includes-loyalty-dimension, concerns-decomposed | 85% |
| 13 | shipping-partial-applicability | express-uses-value-sets, overnight-grouped, blank-vs-value-set-correct, separates-availability-and-cost, concerns-decomposed | 55% |
| 16 | order-splitting | 5 concern assertions (fulfillment/delivery/availability/warehouse/companion), depth per concern, readability, concerns-decomposed | 95% |
| 17 | shopping-cart | 4 concern assertions (items/coupon/total/checkout), depth per concern, coupon-expiry-column, coupon-before-after-columns, concerns-decomposed | 100% |
| 21 | event-registration-sbe | validation-rules-covered, blank-for-absent-optional, separates-validation-and-pricing, no-redundant-policy-columns, concerns-decomposed | 85% |
| 24 | weekly-pay-sbe | overtime-boundary-covered, separates-classification-and-calculation, concerns-decomposed, minimal-rows-per-concern | 67% |

---

## Goal → Coverage Mapping

### Goal 1: Clarify known rules

| Category | Testable Aspects | Coverage | Notes |
|----------|-----------------|----------|-------|
| **Depth of examples** | Different outcomes, boundary conditions, special cases, missing inputs | **Reasonable** — evals 4,5,6,10,12,13,16,17,21,24 all include depth assertions | Spread across many evals; per-aspect coverage varies |
| **Boundaries & thresholds** | Threshold values visible; boundary rows at/above/below | **Partial** — `threshold-values-visible` in eval 4 (passes); `24h-boundary-near-boundary` in eval 10 (fails); `overtime-boundary-covered` in eval 24 | Eval 10 boundary assertion unreliable; `threshold-as-column` in eval 4 fails |
| **Stateful features** | Row independence; state as before/action/after; value sets for multi-state rules | **Reasonable** — eval 5 (100%), eval 17 (coupon-before-after-columns passes) | Eval 5 solid; eval 17 covers before/after framing |

**Rule sources currently tested by evals:**

| Rule Source | Eval(s) | Notes |
|------------|---------|-------|
| Decision/eligibility rules | 4 (loan), 13 (shipping) | Well represented |
| State transitions | 5 (order status) | Single eval but 100% pass rate |
| Rule interactions/conflicts | 6 (discount stacking), 12 (loyalty+trial) | Good — 2 evals testing interaction ambiguity |
| Validation rules | 21 (event registration) | **New** — `validation-rules-covered` passes |
| Default/fallback behavior | 4 (missing income) | Single scenario in 1 eval |
| Temporal/window rules | 10 (24h boundary, 30-day return) | Single eval; boundary assertion fails |
| Classification + calculation | 24 (weekly pay) | **New** — overtime boundary passes; separation assertion fails |

### Goal 2: Surface unknown rules

| Category | Testable Aspects | Coverage | Notes |
|----------|-----------------|----------|-------|
| **Open questions & ambiguity** | Ambiguities surfaced not resolved; open cells marked | **Strong** — evals 6 (`open-question-surfaced` passes), 12 (`surfaces-genuine-open-questions` passes, `open-question-surfaced` fails), 17 (`4.10-depth-open-questions` passes), 21 (`open-question-surfaced` passes) | 4 evals with assertions; 3 of 4 pass reliably |

Well-covered goal. Minor inconsistency: eval 12 passes `surfaces-genuine-open-questions` but fails `open-question-surfaced`, suggesting format sensitivity.

### Goal 3: Specify precisely

| Category | Testable Aspects | Coverage | Notes |
|----------|-----------------|----------|-------|
| **Value sets & irrelevant inputs** | `{...}` notation; value sets preferred over duplicate rows | **Partial** — eval 13 `express-uses-value-sets` (fails), eval 5 `value-set-or-multiple-states` (passes) | 2 evals; one passes, one fails |
| **Blank cell semantics** | Blanks for absent values; value sets for irrelevant; no filler like "N/A" | **Partial** — eval 13 `blank-vs-value-set-correct` (passes), eval 21 `blank-for-absent-optional` (fails) | **Previously a gap**, now 2 assertions but only 1 passes |
| **Threshold as explicit column** | Threshold appears as column, not buried in output values | **Weak** — eval 4 `threshold-as-column` (fails) | Assertion exists but does not pass |

Improved from prior analysis (blank semantics was a total gap) but still the weakest goal. Both new assertions have reliability issues.

### Goal 4: Separate concerns

| Category | Testable Aspects | Coverage | Notes |
|----------|-----------------|----------|-------|
| **Decomposition** | Multiple tables; each table has distinct concern | **Strong breadth, Partial reliability** — `concerns-decomposed` present in 9 evals but fails in 4 (evals 4,10,13,24); `minimal-rows-per-concern` present in 8 evals, fails in 4 (evals 4,10,13,24) | Core decomposition assertions exist widely but fail ~44% of the time |
| **Concern-specific separation** | Named separation assertions per eval | **Strong** — `separates-transitions-and-returns` (5, passes), `eligibility-separate-from-amount` (10, fails), `separates-availability-and-cost` (13, fails), `separates-validation-and-pricing` (21, passes), `separates-classification-and-calculation` (24, fails), `separates-pricing-trial-loyalty-refund` (12, fails) | 6 evals with specific separation assertions; 2 pass, 4 fail |
| **Rules vs arithmetic** | Tables focus on decisions; arithmetic gets minimal rows | **Weak** — eval 10 `rules-separate-from-arithmetic` (fails), eval 24 `separates-classification-and-calculation` (fails) | **Previously a gap**, now 2 assertions but both fail |

This goal has excellent breadth (assertions in nearly every eval) but poor reliability. The `concerns-decomposed` and `minimal-rows-per-concern` meta-assertions fail in evals 4, 10, 13, and 24. The concern-specific separation assertions fail in 4 of 6 evals.

### Goal 5: Communicate across audiences

| Category | Testable Aspects | Coverage | Notes |
|----------|-----------------|----------|-------|
| **Business language** | Domain terms in headers; concrete domain values in cells | **Strong** — `business-language-columns` in evals 4, 10, 24 (all pass); `concrete-domain-values` in evals 4, 13 (both pass) | 4 evals, reliable |
| **Scenario naming** | Scenario names describe conditions, not outcomes | **Strong** — evals 4, 16, 17, 21, 24 all have `scenario-names-describe-conditions` or equivalent; all pass | 5 evals, reliable |
| **Output traceability** | Outputs derivable from inputs; test data visible in table | **Partial** — eval 10 `output-values-traceable` (fails), eval 17 `4.9-readability-test-data-visible` (passes) | 2 evals; mixed results |

Improved from prior analysis. Business language and scenario naming now have strong coverage.

### Goal 6: Produce TableTest-ready structure

| Category | Testable Aspects | Coverage | Notes |
|----------|-----------------|----------|-------|
| **Table structure** | Markdown table; `?` on output columns; `?` only on outputs | **Strong** — `produces-markdown-table` in 7 evals (all pass); `output-column-has-question-mark` in 7 evals (all pass); `question-mark-only-on-outputs` in evals 10 (fails), 12 (passes) | Well covered; `?`-only-on-outputs needs more reliable coverage |
| **Scenario column** | Scenario column present in table | **Strong** — `scenario-column-present` in evals 4, 5, 6, 13, 21, 24 (all pass) | **Previously a gap**, now 6 evals, all pass |
| **Value set notation** | `{...}` syntax used | **Partial** — eval 5 `value-set-or-multiple-states` (passes), eval 13 `express-uses-value-sets` (fails) | Mixed |
| **Row independence** | Each row independently verifiable; no sequential dependencies | **Reasonable** — eval 5 `row-independence` (passes), eval 17 `4.3-rows-independently-executable` (passes) | 2 evals, both pass |

Much improved. Scenario column and row independence were previously gaps/weak — now solid.

---

## Gap Summary by Priority

| Priority | Gap | Goals Blocked | Current State | What's Needed |
|----------|-----|--------------|---------------|---------------|
| **P1** | Concern decomposition reliability | 4 (Separate concerns) | `concerns-decomposed` fails in 4/9 evals (4,10,13,24); `minimal-rows-per-concern` fails in 4/8 | Skill guidance improvements — assertions exist but agent doesn't reliably decompose |
| **P2** | Rules vs arithmetic separation | 4 (Separate concerns) | 2 assertions exist, both fail (evals 10, 24) | Skill guidance — both evals test this but agent merges rules with arithmetic |
| **P3** | Concern-specific separation assertions | 4 (Separate concerns) | 4 of 6 specific separation assertions fail | Related to P1 — agent produces tables but doesn't split by concern |
| **P4** | Blank cell semantics reliability | 3 (Precision), 6 (TableTest-ready) | 2 assertions exist; only 1 passes (eval 13) | Eval 21 `blank-for-absent-optional` needs skill guidance fix |
| **P5** | Threshold as explicit column | 1 (Clarify rules), 3 (Precision) | 1 assertion exists, fails (eval 4) | Skill guidance — `threshold-as-column` tested but not achieved |
| **P6** | Value set preference over duplicate rows | 3 (Precision), 6 (TableTest-ready) | 2 evals; 1 passes, 1 fails | Eval 13 `express-uses-value-sets` needs improvement |
| **P7** | Output traceability | 5 (Communicate) | 2 evals; 1 passes, 1 fails | Improve skill guidance for traceable outputs |
| **P8** | `?`-only-on-outputs reliability | 6 (TableTest-ready) | 2 evals; 1 passes, 1 fails (eval 10) | Add assertion to more evals; fix eval 10 |

---

## Closed Gaps

Items that were previously identified as gaps but now have coverage:

| Former Gap | Resolution | Current State |
|-----------|-----------|---------------|
| Validation rules as rule source | Eval 21 (event-registration-sbe) added | `validation-rules-covered` passes |
| Blank cell semantics (zero assertions) | Evals 13, 21 now have assertions | Partial — 1 of 2 passes |
| Rules vs arithmetic separation (zero assertions) | Evals 10, 24 now have assertions | Weak — both fail, but assertions exist |
| Scenario column presence (zero assertions) | `scenario-column-present` added to 6 evals | Strong — all 6 pass |
| Row independence (1 eval, partial fail) | Evals 5, 17 now both pass | Reasonable — 2 evals, both pass |
| Open question format | Eval 21 `open-question-surfaced` passes | 4 evals now cover this |
| Concrete domain values (never asserted) | `concrete-domain-values` in evals 4, 13 | Both pass |
