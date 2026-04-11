Two distinct concerns here: **sign-up pricing and trial eligibility**, and **cancellation refund calculation**. I'll also surface the open questions you mentioned.

---

### Table 1: Sign-Up — Plan & First Charge

| Scenario | Plan | Loyalty Member? | Trial? | First Charge |
|---|---|---|---|---|
| Standard new subscriber | Monthly | no | yes (30 days) | £9.99 after trial |
| Loyalty member on monthly | Monthly | yes | yes (30 days) | £9.99 after trial |
| New annual subscriber | Annual | no | no | £95.88 immediately |
| New annual subscriber + loyalty | Annual | yes | no | £86.29 immediately |

> **Confirm**: The loyalty discount applies only to annual plan subscribers. Row 2 — a loyalty member on monthly pays full price (£9.99/month). Is that correct?

---

### Table 2: Cancellation Refund

| Scenario | Plan | Loyalty Member? | Cancellation Timing | Refund? |
|---|---|---|---|---|
| Cancel within 24h of monthly renewal | Monthly | {yes, no} | 12h after renewal charge | £0 — no-refund window |
| Cancel mid-cycle, monthly | Monthly | no | 15 days in, 15 days remaining | ~£5.00 (15/30 × £9.99) |
| Cancel mid-cycle, monthly + loyalty | Monthly | yes | 15 days in, 15 days remaining | ~£5.00 — loyalty N/A for monthly |
| Cancel within 24h of annual renewal | Annual | {yes, no} | 12h after renewal charge | £0 — no-refund window |
| Cancel mid-year, annual | Annual | no | 180 days in, 185 days remaining | ~£48.56 (185/365 × £95.88) |
| Cancel mid-year, annual + loyalty | Annual | yes | 180 days in, 185 days remaining | ? |
| Cancel during free trial | Monthly | {yes, no} | During trial (no charge yet) | £0 — no charge has occurred |

---

### Open Questions

**1. Refund basis for annual loyalty cancellation** (Table 2, row 6)
The subscriber paid £86.29. When they cancel mid-year, is the prorated refund calculated from:
- the £86.29 they actually paid, or
- the £95.88 full annual price?

These give different amounts (~£43.76 vs ~£48.56 for 185 days remaining).

**2. Trial → annual upgrade**
Can a subscriber in their 30-day trial switch to an annual plan? If so:
- Does the trial end immediately and annual billing start?
- Does any remaining trial credit offset the first annual charge?
- If they're a loyalty member, do they pay £86.29 or £95.88?

**3. Loyalty mid-cycle**
An annual subscriber joins the loyalty programme partway through their year. Does the 10% discount apply:
- immediately (prorated credit for remaining months?),
- at next renewal only, or
- not until the subscription is restarted?

**4. Loyalty member joins, then loses loyalty status mid-annual-cycle**
Does the discount get clawed back? This is the mirror of question 3.

---

Questions 1 and 2 are the most likely to affect implementation first — I'd pin those down before coding the billing engine. Questions 3 and 4 could be deferred if loyalty status changes are rare in practice.