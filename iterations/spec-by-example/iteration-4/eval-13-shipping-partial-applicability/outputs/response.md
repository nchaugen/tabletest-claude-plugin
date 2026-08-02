# Shipping Cost Calculator — Specification by Example

I've split this into three tables — one per shipping method — since each has its own independent rule (flat rate / value+destination combo / destination-only eligibility). No combining table is needed: a customer selects one method per order, there's no precedence or interaction between them.

## Table 1: Charges a Flat Rate for Standard Shipping

| Scenario | Order Value (£) | Destination | Cost (£)? |
|---|---|---|---|
| Any order value, any destination | {0.01, 49.99, 50.00, 500.00} | {UK, Ireland, France, USA} | 3.99 |

One row states the whole rule: neither input affects the outcome, so both are value sets rather than duplicated rows.

## Table 2: Calculates Express Shipping Cost by Order Value and Destination

| Scenario | Order Value (£) | Destination | Free Threshold (Policy) (£) | Cost (£)? |
|---|---|---|---|---|
| Below threshold, any destination | 49.99 | {UK, Ireland, France} | 50.00 | 9.99 |
| At threshold, UK | 50.00 | UK | 50.00 | 0.00 |
| At threshold, non-UK | 50.00 | Ireland | 50.00 | 9.99 |

The threshold is made visible as a column even though it's constant. Row 1 vs Row 2 shows the boundary; Row 2 vs Row 3 shows the free tier is UK-only at that same boundary. No further-above-threshold row is added — it would repeat the same claim without a new obligation.

## Table 3: Determines Overnight Shipping Eligibility and Cost by Destination

| Scenario | Order Value (£) | Destination | Available? | Cost (£)? |
|---|---|---|---|---|
| UK destination | {0.01, 50.00, 500.00} | UK | yes | 14.99 |
| Ireland destination | {0.01, 50.00, 500.00} | Ireland | yes | 14.99 |
| Destination outside UK and Ireland | {0.01, 50.00, 500.00} | {France, USA} | no | (blank) |

Order value is a value set throughout — the rules never make Overnight eligibility or price depend on it. Cost is blank (not £0.00) when unavailable, since blank means "does not apply," not "free."

---

## Assumptions made (no follow-up possible, so I resolved these rather than leaving them open)

1. **Non-UK order ≥ £50 for Express**: costs £9.99 (Table 2, row 3). The prompt only states the free tier is UK-only, not what happens elsewhere at/above £50 — I inferred it reverts to the standard £9.99 fee rather than becoming unavailable or scaling differently. **Flag for confirmation** — this is a real business-rule gap, not just an implementation detail.
2. **Northern Ireland** is treated as part of "UK," and "Ireland" means the Republic of Ireland only. Worth confirming since "Ireland" is genuinely ambiguous in UK shipping contexts.
3. **£50 boundary is inclusive** ("£50 and over") — exactly £50.00 gets free Express in the UK.
4. **Currency**: single-currency GBP throughout, no conversion logic.
5. Order value is assumed always ≥ £0.01 (validated upstream) — zero/negative values aren't modeled.

## Left genuinely open (not resolvable without a product decision)

- What happens when a customer selects Overnight for an ineligible destination — reject with an error, or simply not offer it as an option in the UI? This is a UX/API contract decision the tables can't settle.
- Behavior for an unrecognized/unsupported destination value (not UK, Ireland, or a known "rest of world" country) — out of scope of the three rules given.

These tables are ready to hand to `/tabletest` for implementation — each column maps directly to a method parameter, and the `?`-suffixed columns become the assertions.