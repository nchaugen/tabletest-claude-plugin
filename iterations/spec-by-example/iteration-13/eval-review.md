# Eval Review — spec-by-example, Iteration 13

**Model:** sonnet (claude-sonnet-5) · **Grading:** claude-sonnet-5 · **Date:** 2026-08-05 · **Evals:** 3

## Summary

32/38 (84.2%) · 259682 tokens · 445.8s · $1.2299

_Cost figures are Claude Code list-price estimates; actual billing may differ (e.g. promotional pricing). Timed-out evals score 0 with unrecorded token usage._

> ⚠️ **1 assertion verdict moved** vs iteration 11 (over 1 of 3 evals — the rest were not comparable). These are deltas, not attributions: read each eval's `outputs/` and `narration.md` before explaining any of them, and do not start the next iteration until every entry in `analysis-todo.md` has a cause.

## Delta vs Iteration 11

**Regressions (1):**
- ❌ eval-13-shipping-partial-applicability: `separates-availability-and-cost`

**Not comparable (2) — excluded from the deltas above:**
- ⚠️ eval-4-loan-approval: iteration 11 did not run this eval; nothing to compare against
- ⚠️ eval-12-subscription-loyalty-trial: iteration 11 did not run this eval; nothing to compare against

## Resource Comparison vs Iteration 11

| Eval | Pass Rate | Prev | Tokens | Prev | Time(s) | Prev |
|------|-----------|------|--------|------|---------|------|
| eval-4-loan-approval | 12/13 | — | 83438 | — | 124.2 | — |
| eval-12-subscription-loyalty-trial | 11/13 | — | 97358 | — | 250.4 | — |
| eval-13-shipping-partial-applicability | 9/12 | 10/12 | 78886 | 82196 | 71.3 | 124.3 |

## Per-Eval Results

### ⚠️ Eval eval-4-loan-approval

**12/13** · 83438 tokens · 124195ms

- ✅ **produces-markdown-table**: Output contains a markdown table (using | column | syntax)
- ✅ **output-column-has-question-mark**: At least one output column name ends with '?' (e.g. 'Eligible?' or 'Approved?')
- ✅ **business-language-columns**: Column names use domain/business language — no implementation terms like 'creditScore', 'isEligible', 'boolean', 'int'
- ✅ **senior-threshold-row**: Table includes at least one row specifically for the senior applicant (65+) threshold scenario
- ✅ **missing-income-marked-open**: The missing income scenario is represented — either as a row with an open/uncertain expected value (?, TBD, or blank) or as an explicit open question note
- ✅ **scenario-names-describe-conditions**: Scenario names name the variation the row exercises, not the result it produces. The decidable test: FAILS when a scenario name states or paraphrases a value that appears in an expectation column of that same row — 'User cannot delete' beside an Allowed? cell of false, 'No discount applies' beside a Discount? cell of 0.00, 'Contractor gets no bonus' beside a Bonus? cell of 0. Also FAILS on generic labels ('Test 1', 'Test case 2'), which name no variation at all. PASSES otherwise. Naming the rule or the situation is correct even when it makes the outcome inferable — 'Delivery and pickup items always split', 'At the standard threshold, not above it', 'EU express, light package', 'Renewal with no claims', 'Missing name' all PASS. The failure this catches is a name echoing its own expectation cell, not a name that describes what the row is about, and not a name from which a reader who knows the rule could predict the outcome. A single offending name fails the assertion. Judge every table in the response.
- ✅ **threshold-values-visible**: The policy thresholds (650 for standard, 600 for senior) appear as concrete values in the Credit Score column — not just described in prose. The reader can see the boundary values in the table rows.
- ✅ **scenario-column-present**: Table has a scenario/description column as the leftmost column
- ✅ **threshold-as-column**: Policy thresholds (650/600 credit scores) appear in a dedicated policy column (e.g. 'Credit threshold') separate from the applicant's actual credit score column. The reader sees both the threshold and the score, making the comparison explicit.
- ✅ **concrete-domain-values**: Cell values use concrete domain terms (e.g. '65', '700', 'Stable', 'yes', 'no') — not abstract codes or raw booleans like 'true', 'false', 'CATEGORY_A', '1'.
- ✅ **concerns-decomposed**: Multiple tables (or @TableTest methods) are used, each addressing a distinct concern — not one monolithic table mixing unrelated rules. Acceptable to use a single table only when the domain genuinely has a single concern.
- ✅ **no-duplicate-rows-within-a-table**: Judge each table in isolation. The assertion FAILS if a row re-covers an obligation an earlier row in the SAME table already discharged (e.g. a full region x speed x weight cross-product where one row per rate band would state the same rule). Judge each table against its concern's coverage obligations — the distinct behaviours the rule must demonstrate. Where the expected output states those obligations for a concern, judge against that list and do not invent a stricter one; where it does not, derive them from the concern itself. A row that discharges an obligation no other row reaches earns its place however simple it looks, and a value set that covers several values in one row is the preferred discharge, never a failure. Rows that walk one input up while everything else is held are excess when the third and later ones only re-show a direction the first two already established — 'standard applicant, no claims' at ages 30, 40 and 50, or a linear premium sampled at 0, 2 and 4 claims: two samples state the effect and the rest exercise the same arithmetic. They are NOT excess when each value is a boundary, a band, or a distinct branch of the rule — 40 against 41 hours, below/at/above a threshold, one row per rate band — because each then discharges an obligation of its own. ROWS YOU THINK ARE MISSING ARE NEVER A FAILURE HERE — this assertion judges excess rows only, and coverage is judged by this eval's own coverage assertions. Judge every table in the response; your evidence must name each table with PASS or FAIL.
- ❌ **separates-age-credit-income**: Age boundary policy, credit score categorisation, and income status are separated into distinct tables, with a final table combining these for the expected verdict
  > Only two tables: age→threshold, and a combined credit score + income status table, not separated into distinct tables plus a final combining table

### ⚠️ Eval eval-12-subscription-loyalty-trial

**11/13** · 97358 tokens · 250387ms

- ✅ **produces-multiple-tables**: Output contains at least 2 separate markdown tables
- ✅ **annual-no-trial-rule-represented**: A table or note explicitly captures that the free trial is not available on annual plans (not just implied)
- ✅ **loyalty-discount-row-present**: At least one table row demonstrates the loyalty programme discount scenario for annual plan subscribers
- ✅ **surfaces-genuine-open-questions**: The response identifies at least one genuinely underspecified interaction — such as: what happens when a trial subscriber upgrades to annual mid-trial, when a subscriber joins the loyalty programme mid-billing-cycle, or whether a cancellation refund is based on the discounted or full price. These are NOT stated in the rules and require a product decision.
- ✅ **open-question-surfaced**: The annual/loyalty/trial interaction is correctly resolved with a note explaining that they are mutually exclusive (annual gets loyalty discounts but no trial, monthly gets trial but no loyalty discount) — not left as an unresolved open question
- ✅ **loyalty-discount-annual-only**: Makes clear (via table structure, a note, or explicit rows) that the loyalty discount applies to the annual plan only — a loyalty member on monthly pays the same as anyone else.
- ✅ **loyalty-refund-ambiguity**: The loyalty annual subscriber's prorated cancellation refund is either surfaced as an open question (discounted vs list price) OR calculated from the discounted price with a visible trace — since that is what was actually paid
- ✅ **cancellation-refund-table**: Includes a cancellation/refund table or section (not just pricing) — the loyalty discount creates a refund calculation ambiguity that should be shown with concrete rows.
- ✅ **refund-table-includes-loyalty-dimension**: The cancellation/refund table includes a loyalty member column (or equivalent) to distinguish loyalty vs non-loyalty annual refund rows — not just plan type alone.
- ❌ **question-mark-only-on-outputs**: The '?' suffix is only used on output or derived columns (e.g. 'Refund?', 'Price?') — not on given input columns like plan type, loyalty status, or subscription phase. A column that interprets or classifies an input value (e.g. deriving whether a cancellation falls within 24h from a raw time value) is a valid derived output and may use '?'.
  > Table 1 header '| Scenario | Plan | Loyalty Member? | Charge? |' uses '?' on an input column (Loyalty Member?).
- ✅ **concerns-decomposed**: Multiple tables (or @TableTest methods) are used, each addressing a distinct concern — not one monolithic table mixing unrelated rules. Acceptable to use a single table only when the domain genuinely has a single concern.
- ✅ **no-duplicate-rows-within-a-table**: Judge each table in isolation. The assertion FAILS if a row re-covers an obligation an earlier row in the SAME table already discharged (e.g. a full region x speed x weight cross-product where one row per rate band would state the same rule). Judge each table against its concern's coverage obligations — the distinct behaviours the rule must demonstrate. Where the expected output states those obligations for a concern, judge against that list and do not invent a stricter one; where it does not, derive them from the concern itself. A row that discharges an obligation no other row reaches earns its place however simple it looks, and a value set that covers several values in one row is the preferred discharge, never a failure. Rows that walk one input up while everything else is held are excess when the third and later ones only re-show a direction the first two already established — 'standard applicant, no claims' at ages 30, 40 and 50, or a linear premium sampled at 0, 2 and 4 claims: two samples state the effect and the rest exercise the same arithmetic. They are NOT excess when each value is a boundary, a band, or a distinct branch of the rule — 40 against 41 hours, below/at/above a threshold, one row per rate band — because each then discharges an obligation of its own. ROWS YOU THINK ARE MISSING ARE NEVER A FAILURE HERE — this assertion judges excess rows only, and coverage is judged by this eval's own coverage assertions. Judge every table in the response; your evidence must name each table with PASS or FAIL.
- ❌ **separates-pricing-trial-loyalty-refund**: Pricing/plans, trial eligibility, loyalty discount applicability, and cancellation/refund are in separate tables
  > Table 1 'Calculates the subscription charge from plan and loyalty status' merges pricing/plans with loyalty discount applicability in one table (rows for 'Annual subscriber, not a loyalty member' and 'loyalty member' alongside plain Monthly pricing), and Table 3 mixes trial transitions with charge/refund/upgrade-to-annual logic in a single table.

### ⚠️ Eval eval-13-shipping-partial-applicability

**9/12** · 78886 tokens · 71252ms

- ✅ **produces-markdown-table**: Output contains a markdown table
- ✅ **express-split-by-uk-free-tier**: Express shipping is split into at least two cases distinguishing the UK free tier (UK, order £50+, free) from the paid tier. The split is driven by the UK-only free threshold rule.
- ✅ **express-uses-value-sets**: Express rows use value sets to consolidate cases with the same outcome — e.g. non-UK destinations at/above threshold in one row (such as {Ireland, Other} or {Ireland, International}), or UK at/above threshold in one row ({£50, £75}). Not enumerated as separate rows per destination or per order value when the result is identical. Any reasonable label for non-UK/Ireland destinations is acceptable (International, Other, Rest of World, etc.).
- ❌ **overnight-grouped**: Overnight UK and Ireland are in one row using a value set ({UK, Ireland}) — not separate rows, since both have the same cost and availability.
  > Table3 lists "UK destination" and "Ireland destination" as separate rows instead of {UK, Ireland}
- ✅ **standard-destination-value-set-or-blank**: Standard shipping destination uses a value set covering all options (e.g. {UK, Ireland, International} or {UK, Ireland, Other}) or is blank/absent — either approach is valid since destination is irrelevant to the cost. Any reasonable label for non-UK/Ireland destinations is acceptable.
- ✅ **output-column-has-question-mark**: At least one output column ends with '?'
- ✅ **concrete-domain-values**: Cell values use concrete domain terms (e.g. 'Standard', 'Express', 'UK', '£3.99') — not abstract codes or raw booleans like 'true', 'false', 'TYPE_1', '1'.
- ✅ **blank-vs-value-set-correct**: Value sets (not blanks) are used for irrelevant inputs — e.g. Standard shipping destination uses a value set like {UK, Ireland, International} because destination exists but doesn't affect the cost. Blanks are reserved for genuinely absent/N/A inputs. The distinction between 'irrelevant to this rule' (value set) and 'not applicable' (blank) is correct.
- ✅ **concerns-decomposed**: Multiple tables (or @TableTest methods) are used, each addressing a distinct concern — not one monolithic table mixing unrelated rules. Acceptable to use a single table only when the domain genuinely has a single concern.
- ❌ **no-duplicate-rows-within-a-table**: Judge each table in isolation. The assertion FAILS if a row re-covers an obligation an earlier row in the SAME table already discharged (e.g. a full region x speed x weight cross-product where one row per rate band would state the same rule). Judge each table against its concern's coverage obligations — the distinct behaviours the rule must demonstrate. Where the expected output states those obligations for a concern, judge against that list and do not invent a stricter one; where it does not, derive them from the concern itself. A row that discharges an obligation no other row reaches earns its place however simple it looks, and a value set that covers several values in one row is the preferred discharge, never a failure. Rows that walk one input up while everything else is held are excess when the third and later ones only re-show a direction the first two already established — 'standard applicant, no claims' at ages 30, 40 and 50, or a linear premium sampled at 0, 2 and 4 claims: two samples state the effect and the rest exercise the same arithmetic. They are NOT excess when each value is a boundary, a band, or a distinct branch of the rule — 40 against 41 hours, below/at/above a threshold, one row per rate band — because each then discharges an obligation of its own. ROWS YOU THINK ARE MISSING ARE NEVER A FAILURE HERE — this assertion judges excess rows only, and coverage is judged by this eval's own coverage assertions. Judge every table in the response; your evidence must name each table with PASS or FAIL.
  > Table1 PASS (single row); Table2 PASS (3 distinct branches); Table3 FAIL - separate 'UK' and 'Ireland' rows both yield identical outcome (yes/£14.99), an excess split that a value set would discharge in one row.
- ❌ **separates-availability-and-cost**: Shipping method availability and shipping cost are in separate tables
  > Table3 columns: "Destination | Available? | Cost?" combine availability and cost in one table
- ✅ **blank-output-for-na**: When a shipping method is unavailable (e.g. Overnight outside UK/Ireland), the output clearly indicates non-applicability — either via a blank cost cell in a combined table, or by the availability table showing 'No'/unavailable and the cost table omitting those combinations entirely. Filler text like 'N/A', '-', or '0' is not used for unavailable outcomes.

