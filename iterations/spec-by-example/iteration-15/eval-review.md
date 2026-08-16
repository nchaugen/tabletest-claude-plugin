# Eval Review — spec-by-example, Iteration 15

**Model:** sonnet (claude-sonnet-5) · **Grading:** claude-sonnet-5 · **Date:** 2026-08-16 · **Evals:** 1

## Summary

10/12 (83.3%) · 82986 tokens · 96.3s · $0.3363

_Cost figures are Claude Code list-price estimates; actual billing may differ (e.g. promotional pricing). Timed-out evals score 0 with unrecorded token usage._

> ⛔ **Void comparison — none of the 1 evals could be compared** vs iteration 14. Every delta below is computed over nothing; an absence of movement here is not evidence that nothing moved. See `analysis-todo.md`.

## Delta vs Iteration 14

**Not comparable (1) — excluded from the deltas above:**
- ⚠️ eval-13-shipping-partial-applicability: fingerprint differs from iteration 14; re-baseline to compare

## Resource Comparison vs Iteration 14

| Eval | Pass Rate | Prev | Tokens | Prev | Time(s) | Prev |
|------|-----------|------|--------|------|---------|------|
| eval-13-shipping-partial-applicability | 10/12 | 9/12 | 82986 | 81091 | 96.3 | 81.7 |

## Per-Eval Results

### ⚠️ Eval eval-13-shipping-partial-applicability

**10/12** · 82986 tokens · 96331ms

- ✅ **produces-markdown-table**: Output contains a markdown table
- ✅ **express-split-by-uk-free-tier**: Express shipping is split into at least two cases distinguishing the UK free tier (UK, order £50+, free) from the paid tier. The split is driven by the UK-only free threshold rule.
- ✅ **express-uses-value-sets**: Express rows use value sets to consolidate cases with the same outcome — e.g. non-UK destinations at/above threshold in one row (such as {Ireland, Other} or {Ireland, International}), or UK at/above threshold in one row ({£50, £75}). Not enumerated as separate rows per destination or per order value when the result is identical. Any reasonable label for non-UK/Ireland destinations is acceptable (International, Other, Rest of World, etc.).
- ❌ **overnight-grouped**: Overnight UK and Ireland are in one row using a value set ({UK, Ireland}) — not separate rows, since both have the same cost and availability.
  > Overnight table has separate rows 'UK destination' and 'Ireland destination', not a combined {UK, Ireland} row
- ✅ **standard-destination-value-set-or-blank**: Standard shipping destination uses a value set covering all options (e.g. {UK, Ireland, International} or {UK, Ireland, Other}) or is blank/absent — either approach is valid since destination is irrelevant to the cost. Any reasonable label for non-UK/Ireland destinations is acceptable.
- ✅ **output-column-has-question-mark**: At least one output column ends with '?'
- ✅ **concrete-domain-values**: Cell values use concrete domain terms (e.g. 'Standard', 'Express', 'UK', '£3.99') — not abstract codes or raw booleans like 'true', 'false', 'TYPE_1', '1'.
- ✅ **blank-vs-value-set-correct**: Value sets (not blanks) are used for irrelevant inputs — e.g. Standard shipping destination uses a value set like {UK, Ireland, International} because destination exists but doesn't affect the cost. Blanks are reserved for genuinely absent/N/A inputs. The distinction between 'irrelevant to this rule' (value set) and 'not applicable' (blank) is correct.
- ✅ **concerns-decomposed**: Distinct concerns are addressed by distinct tables, rather than crammed into one monolithic table mixing unrelated rules. **What counts as a distinct concern is the Expected Output Description's own list wherever it gives one** — take its dimensions as written and do not regroup them into something you find more cohesive; where it gives no list, derive the concerns from the requirement. FAILS when one table carries concerns that answer different rules — whether it is the only table delivered or one of several, since a class can decompose most of its concerns correctly and still cram three unrelated ones into a fourth table. PASSES when every table delivered is focused on one concern, or on concerns related as the exemptions below describe. Three things are NOT failures here, and each is commonly mistaken for one. **Closely-related concerns may share one table** where they answer the same rule — a fulfillment type and a delivery address both deciding "same shipment iff same type and address" are two facets of one question, so four tables covering five named concerns is correct, not under-decomposed. **A single table is right** where the concerns share the same input columns and each is discharged by one row or two: the compact one-table form and a multi-table split are then equally correct, and a domain with genuinely one concern needs only one. **Repeated or redundant rows are not this assertion's subject at all** — whether one row re-covers ground an earlier one already discharged, and whether a whole table re-proves another, are judged elsewhere; do not fail decomposition for them. Judge the delivered tables and nothing else. Any claim the response's prose makes about how many concerns or tables it produced is irrelevant — an inaccurate summary standing beside correctly decomposed tables PASSES, and an accurate summary standing beside one monolithic table FAILS.
- ✅ **no-duplicate-rows-within-a-table**: Judge each table in isolation. The assertion FAILS if a row re-covers an obligation an earlier row in the SAME table already discharged (e.g. a full region x speed x weight cross-product where one row per rate band would state the same rule). Judge each table against its concern's coverage obligations — the distinct behaviours the rule must demonstrate. Where the expected output states those obligations for a concern, judge against that list and do not invent a stricter one; where it does not, derive them from the concern itself. A row that discharges an obligation no other row reaches earns its place however simple it looks, and a value set that covers several values in one row is the preferred discharge, never a failure. Rows that walk one input up while everything else is held are excess when the third and later ones only re-show a direction the first two already established — 'standard applicant, no claims' at ages 30, 40 and 50, or a premium sampled at 0, 2, 4 and 6 claims once the charge is already known to be per claim: two samples state a difference, a third states its shape, and the rest exercise the same arithmetic. They are NOT excess when each value is a boundary, a band, or a distinct branch of the rule — 40 against 41 hours, below/at/above a threshold, one row per rate band — because each then discharges an obligation of its own. Two further cases are NOT excess, and both are commonly mistaken for it. A third consecutive value of a count, where two would leave a per-unit rate and a one-off charge indistinguishable: 0, 1 and 2 prior claims, because 0 and 1 alone are equally consistent with a flat penalty for having any claim history, and the third row is what decides between them. And a row whose point is that an input does NOT move the outcome — two ages priced the same, a value set over every carrier against one cost — because an invariance is a rule of its own and the only way to state it is to vary the input and hold the expectation. Neither is a repeat of a direction: the first establishes a shape, the second establishes that there is no direction at all. ROWS YOU THINK ARE MISSING ARE NEVER A FAILURE HERE — this assertion judges excess rows only, and coverage is judged by this eval's own coverage assertions. Judge every table in the response; your evidence must name each table with PASS or FAIL.
- ❌ **separates-availability-and-cost**: Shipping method availability and shipping cost are in separate tables
  > Table 3 header: '| Destination | Order Value (£) | Available? | Overnight Cost (£)? |' combines both in one table
- ✅ **blank-output-for-na**: When a shipping method is unavailable (e.g. Overnight outside UK/Ireland), the output clearly indicates non-applicability — either via a blank cost cell in a combined table, or by the availability table showing 'No'/unavailable and the cost table omitting those combinations entirely. Filler text like 'N/A', '-', or '0' is not used for unavailable outcomes.

