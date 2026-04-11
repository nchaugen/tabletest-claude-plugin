There are two separable concerns here: **whether** a refund is owed, and **how much**. I'll build a table for each, then flag what's still open.

---

## Table 1: Cancellation Refund Eligibility

| Scenario | Account Status | Hours Since Last Charge | Refund Eligible? | Reason? |
|---|---|---|---|---|
| Cancelled during free trial | Trial | — | no | No charge was taken |
| Monthly, cancelled mid-cycle | Monthly | 120 | yes | |
| Monthly, cancelled within 24h of renewal | Monthly | 12 | no | Within 24h of renewal |
| Monthly, cancelled at exactly 24h | Monthly | 24 | ? | Open: is the 24h boundary inclusive or exclusive? |
| Annual, cancelled mid-year | Annual | 2160 | yes | |
| Annual, cancelled within 24h of renewal | Annual | 8 | no | Within 24h of renewal |
| Trial auto-converts; subscriber cancels within 24h | Monthly | 3 | no | Within 24h of first charge |

---

## Table 2: Prorated Refund Amount

*(Applies only when Refund Eligible? = yes. Kept small — this is arithmetic, not a rule.)*

| Scenario | Plan Type | Plan Price | Days Remaining | Days in Cycle | Refund Amount? |
|---|---|---|---|---|---|
| Monthly, half cycle used | Monthly | £9.99 | 15 | 30 | £5.00 |
| Monthly, nearly full cycle remaining | Monthly | £9.99 | 29 | 30 | £9.66 |
| Annual, half year remaining | Annual | £95.88 | 183 | 365 | £48.07 |
| Annual, one month remaining | Annual | £95.88 | 30 | 365 | £7.88 |

---

## Open Questions

Several decisions need pinning down before implementation:

1. **24h boundary**: Is "within 24h" strictly less than 24h, or ≤ 24h? The boundary row in Table 1 is marked `?`.

2. **Monthly cycle length**: Is a billing cycle always 30 days, or does it follow the calendar month (28–31 days)? This affects every refund calculation for monthly subscribers.

3. **Annual proration unit**: Is the annual refund calculated by remaining days (as shown), or by remaining complete months? The two approaches give different answers — e.g. 6 weeks remaining is either `42/365 × £95.88 = £11.03` or `1/12 × £95.88 = £7.99`.

4. **Rounding**: Prorated amounts will rarely be round numbers. Should refunds round to the nearest penny, always round down, or something else?

5. **Trial cancellation timing**: If a subscriber cancels on day 30 of the trial (the last day before conversion), do they avoid the first charge? Or does the system only check cancellation status at the moment of the scheduled charge?

6. **Trial-to-monthly 24h window**: When the trial auto-converts and the first monthly charge fires, does that charge start a 24h no-refund window (as shown in the last row of Table 1)? This would mean a subscriber who cancels on day 32 gets no refund, which may be surprising.

---

Want to work through any of these open questions, or does this cover enough to hand off to `/tabletest`?