Here's the spec, decomposed into three tables — one per shipping method, since each has a genuinely different rule (standard ignores everything, express depends on value *and* destination, overnight depends on destination alone). Cross-multiplying them into one table would just pad rows without adding a claim.

## Assumptions (stated because this is a non-interactive run)

1. **Order value** means the same input across all three tables — the order subtotal, excluding shipping cost itself. Whether it includes tax is unresolved (flagged below).
2. **"UK"** means the United Kingdom as a whole, so Northern Ireland counts as UK, not as "Ireland." Flagged as an open question below since it's a common real-world gotcha.
3. **"Ireland"** means the Republic of Ireland, distinct from UK.
4. **Standard shipping has no destination restriction** — the rules only state a restriction for Overnight, so Standard is assumed available worldwide.
5. **Express's £50 free tier is UK-only** — at or above £50, a non-UK order still pays £9.99. This was the main ambiguity in your rules ("free for orders £50 and over — but the free tier only applies in the UK" is read as: the *free* part is UK-only, not that Express itself is UK-only).
6. **The £50 threshold is inclusive** ("£50 and over" is explicit in your text, so £50.00 itself qualifies).
7. Zero/negative order values and unserviceable destinations (e.g., no delivery at all) are out of scope — this spec only covers cost/eligibility given a valid order.

---

### Table 1 — Charges a flat rate for standard shipping

| Scenario                    | Order Value                          | Destination                        | Cost?  |
|------------------------------|---------------------------------------|-------------------------------------|--------|
| Any order value, any destination | {£0.01, £49.99, £50.00, £500.00} | {UK, Ireland, France, USA}          | £3.99  |

One row, using value sets on both axes, states the "regardless of" claim explicitly rather than leaving it implied.

---

### Table 2 — Prices express shipping from order value and destination

| Scenario                                          | Order Value | Destination                | Free Threshold (Policy) | Cost?  |
|----------------------------------------------------|-------------|------------------------------|--------------------------|--------|
| Below the free threshold, any destination           | £49.99      | {UK, Ireland, France, USA}   | £50.00                   | £9.99  |
| At the free threshold, UK destination                | £50.00      | UK                            | £50.00                   | £0.00  |
| At the free threshold, non-UK destination            | £50.00      | {Ireland, France, USA}       | £50.00                   | £9.99  |

- Row 1 shows destination doesn't matter *below* the threshold (rule says nothing about destination for the paid tier).
- Rows 2 vs 3 isolate the actual rule under test: at the identical order value, only UK gets free shipping.
- The threshold is shown as a column even though constant, per "make thresholds visible" — it's the number the whole table hinges on.

---

### Table 3 — Restricts overnight shipping to UK and Ireland

| Scenario                              | Destination            | Available? | Cost?   |
|-----------------------------------------|--------------------------|------------|---------|
| UK destination                          | UK                        | yes        | £14.99  |
| Ireland destination                     | Ireland                   | yes        | £14.99  |
| Destination outside UK and Ireland      | {France, USA, Germany}   | no         |         |

Order value is deliberately absent as a column — nothing in your rules makes Overnight's cost or availability depend on it, so including it would just be a blank column implying a rule that doesn't exist.

---

## Open questions to resolve before implementation

| Cell | Question |
|---|---|
| Table 2, "order value" | Does the £50 threshold apply to the pre-tax subtotal, or the tax-inclusive total? |
| Table 2/3, "UK" | Does Northern Ireland count as UK (assumed here) or does it get Ireland's treatment for Overnight eligibility? |
| Table 3 | Should Overnight be offered for Channel Islands / Isle of Man — UK-adjacent but not strictly "UK"? |
| All tables | Is order value ever zero (free item order) — does Standard still charge £3.99? |

---

This maps directly to `/tabletest` if you want executable JUnit `@TableTest`s from these — the columns and value sets carry over as-is. Let me know if any of the open questions have known answers and I'll fold them in.