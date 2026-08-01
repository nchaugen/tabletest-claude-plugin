Since this involves multiple interacting rules (trial conversion, proration, and a time-boundary exception) that aren't fully pinned down by the four bullet points, I'll spec it as example tables rather than jumping to code. I'm treating this as non-interactive, so I've made explicit assumptions where the rules are silent — flagged below and in the tables themselves.

## Assumptions

- **Monthly cycle = 30 days**, **annual cycle = 365 days**, for proration math (calendar-month variation ignored).
- **Proration formula**: `Refund = Renewal Charge × (Unused Days ÷ Cycle Length)`, rounded to the nearest penny.
- The **trial-to-monthly auto-conversion charge counts as a "renewal charge"** for the 24-hour exception rule — the rules don't say otherwise, and treating the first charge differently would be a surprising special case.
- "Within 24 hours" is tentatively read as **strictly less than 24h elapsed**; the exact 24h0m boundary is left as an open question (marked `?` below) since the wording doesn't disambiguate inclusive/exclusive.
- Elapsed time is measured to whole days for the proration calculation (a partial current day counts as unused, unless it falls inside the 24h exception window).
- Switching plans (e.g. monthly → annual, or selecting annual instead of the trial's default monthly conversion) isn't covered by the given rules — out of scope, listed as an open question rather than guessed at.

---

## Table 1: Trial Period Outcome

| Scenario | Days Into Trial | Trial Length (Policy) | Action | Billing Event? | Plan After? | Amount Charged? |
|---|---|---|---|---|---|---|
| Mid-trial, subscriber takes no action | 15 | 30 days | none | none | Trial | |
| Day before trial ends | 29 | 30 days | none | none | Trial | |
| Trial reaches day 30 | 30 | 30 days | none | Auto-conversion charge | Monthly | £9.99 |
| Subscriber cancels before trial ends | 10 | 30 days | Cancel | none | Cancelled | |
| Subscriber cancels at the exact moment the trial converts | 30 | 30 days | Cancel | ? | ? | ? |

The last row is a genuine race condition (cancel vs. auto-convert firing at the same instant) — needs a product decision, not a guess.

---

## Table 2: Refund Eligibility (24-Hour Exception Rule)

| Scenario | Plan | Time Since Renewal Charge | Refund Eligible? | Reason? |
|---|---|---|---|---|
| Cancel immediately after charge | {Monthly, Annual} | 0 hours | no | Within 24 hours of renewal charge |
| Cancel well within the 24-hour window | Monthly | 12 hours | no | Within 24 hours of renewal charge |
| Cancel just under the 24-hour window | Monthly | 23 hours 59 minutes | no | Within 24 hours of renewal charge |
| Cancel exactly at the 24-hour boundary | Monthly | 24 hours 0 minutes | ? | Open: is the boundary inclusive or exclusive? |
| Cancel just over the 24-hour window | Monthly | 24 hours 1 minute | yes | |
| Cancel well past the 24-hour window | {Monthly, Annual} | 10 days | yes | |
| Cancel on the last day of the cycle | Monthly | 29 days | yes | |

This is split from proration on purpose — eligibility is a yes/no/exception rule; the payout amount is a separate arithmetic step (below), so boundary rows here aren't cluttered with money columns.

---

## Table 3: Prorated Refund Amount

Applies only where Table 2 says `Refund Eligible? = yes`.

| Scenario | Plan | Renewal Charge (Policy) | Cycle Length (Policy) | Unused Days | Refund Amount? |
|---|---|---|---|---|---|
| Monthly plan, well past 24h, 20 days remaining | Monthly | £9.99 | 30 days | 20 | £6.66 |
| Monthly plan, cancelled on the last day of the cycle | Monthly | £9.99 | 30 days | 1 | £0.33 |
| Monthly plan, cancelled exactly at cycle end | Monthly | £9.99 | 30 days | 0 | £0.00 |
| Annual plan, well past 24h, 265 days remaining | Annual | £95.88 | 365 days | 265 | £69.61 |

All four amounts are traceable via `Renewal Charge × Unused Days ÷ Cycle Length`, rounded to the nearest penny.

---

## Open Questions

1. **Trial conversion vs. simultaneous cancellation** (Table 1, last row) — needs a locking/ordering decision.
2. **24-hour boundary inclusivity** (Table 2) — is exactly 24h0m "within" or "past" the window?
3. **Plan switching** (monthly ↔ annual, or choosing annual at trial signup) — not covered by the given rules at all; needs its own spec if in scope.
4. **Cancel-at-cycle-end** (Table 3, £0.00 row) — worth confirming this should even go through the refund flow, vs. simply letting the cycle lapse without charging the next renewal.

These three tables are ready to hand to `/tabletest` once the open questions are resolved — the column structure maps directly to `@TableTest` parameters, and the `{Monthly, Annual}` cells become value sets as-is.