Here's the spec, built as example tables per the `spec-by-example` approach. I split the three shipping methods into separate rules (each names cleanly without "and"), and further split Overnight into *availability* and *cost* since striking the unavailable-destination rows from a combined table would still leave a standalone pricing rule.

## Assumptions (stated explicitly — no follow-up possible)

1. **Destinations fall into three categories**: `UK`, `Ireland`, `International` (anything else). Ireland is treated as distinct from UK (i.e. "UK" = Great Britain + Northern Ireland, not the Republic of Ireland).
2. **Express's free tier is UK-only, but Express itself is available everywhere.** Orders ≥£50 to Ireland or International are charged the standard £9.99 Express rate — the free tier just doesn't apply, it doesn't mean Express is unavailable. This is the main ambiguity in the brief and the one most worth confirming with you.
3. **The £50 threshold is inclusive**: exactly £50.00 counts as "£50 and over."
4. **Standard shipping is available for all destinations** (no geographic restriction was stated).
5. **Order value has no bearing on Overnight's price or availability** — only destination matters (not stated explicitly, but no value-based rule was given for Overnight).
6. **Overnight to an unsupported destination is a rejection**, not a price of £0 or fallback to another method.
7. All amounts are GBP; "free" = £0.00.

Flag any of these that don't match your intent and I'll rework the tables.

---

### Table 1 — Charges A Flat £3.99 For Standard Shipping

| Scenario                          | Order Value | Destination                   | Standard Cost? |
|------------------------------------|-------------|--------------------------------|-----------------|
| Low-value order, any destination   | £0.01       | {UK, Ireland, International}  | £3.99           |
| High-value order, any destination  | £500.00     | {UK, Ireland, International}  | £3.99           |

### Table 2 — Waives The Express Fee Only For UK Orders At Or Above £50

| Scenario                              | Order Value | Destination              | Free-Tier Threshold (Policy) | Express Cost? |
|-----------------------------------------|-------------|---------------------------|-------------------------------|-----------------|
| Below the threshold, any destination    | £49.99      | {UK, Ireland, International} | £50.00                     | £9.99           |
| At the threshold, UK destination        | £50.00      | UK                         | £50.00                        | £0.00 (Free)    |
| At the threshold, non-UK destination    | £50.00      | {Ireland, International}   | £50.00                        | £9.99           |

Row 2/3 pair is the boundary that matters — below £50 destination is irrelevant (row 1), but at/above £50 the UK/non-UK split becomes the whole rule.

### Table 3 — Limits Overnight Shipping To UK And Irish Destinations

| Scenario                  | Destination    | Available? | Rejection Reason?                                   |
|----------------------------|----------------|------------|-------------------------------------------------------|
| UK destination              | UK             | yes        |                                                        |
| Irish destination           | Ireland        | yes        |                                                        |
| International destination   | International  | no         | Overnight shipping is not available for this destination |

### Table 4 — Fixes Overnight Shipping At £14.99 For Available Destinations

| Scenario                     | Order Value | Destination | Overnight Cost? |
|--------------------------------|-------------|-------------|-------------------|
| UK, low order value             | £0.01       | UK          | £14.99            |
| Ireland, high order value       | £500.00     | Ireland     | £14.99            |

---

## Open questions (not assumed away — flag before implementing)

- Is "UK" here meant to include Northern Ireland? (Assumed yes, per assumption 1.)
- Should Express to Ireland/International ever have its own discount, or is £9.99 truly flat outside the UK regardless of order value? (Assumed the latter — no row shows a non-UK order getting cheaper at any value.)
- Is order value ever zero, negative, or in a different currency? Out of scope here — assumed always a positive GBP amount.

If this matches your intent, I can hand these four tables straight to `/tabletest` to generate executable `@TableTest` code — just say the word (and let me know the language/framework if it's not Java/Kotlin, since `/tabletest` is JVM-specific).