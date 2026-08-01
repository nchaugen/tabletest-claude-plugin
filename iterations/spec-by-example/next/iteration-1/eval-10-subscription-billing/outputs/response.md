Here's the spec, decomposed into three rule tables plus the assumptions I had to make to keep every cell decidable (this is a non-interactive pass, so I've resolved ambiguities rather than leaving blanks).

## Assumptions (stated because the brief doesn't pin these down)

- **"Within 24 hours" is inclusive of the boundary** — exactly 24h00m since the charge still counts as "within," so eligibility starts strictly after 24 hours. Flag this with the business owner; it's a genuine 50/50 read.
- **The 24-hour exception applies to every renewal charge, including the trial→monthly conversion charge** — that first charge is a renewal charge like any other.
- **Proration treats a monthly cycle as exactly 30 days** and an annual cycle as exactly 365 days (not calendar-accurate, but needed for a deterministic formula).
- **Refunds round to the nearest penny, half rounds up** (e.g., £4.995 → £5.00).
- **Trial always converts to monthly**, even if the subscriber's ultimate intent was annual — the brief only states conversion to monthly, so annual is presumed to be a separate, explicit upgrade after trial (out of scope here).
- **Cancelling during the trial cancels outright** — no charge has occurred, so no refund question arises.

Open question I couldn't resolve without a domain expert: does cancelling within the 24-hour window still end the subscription immediately, or only block the refund while leaving access running to cycle end? I assumed the former (cancels immediately, refund withheld) — worth confirming.

---

### Table 1 — Converts a Trial Subscriber to Monthly at Day 30

| Scenario | State Before | Days Since Trial Started | State After? | Charged Amount? |
|---|---|---|---|---|
| Trial not yet complete | Trial | 29 | Trial | (none) |
| Trial period elapses | Trial | 30 | Monthly, active | £9.99 |
| Subscriber cancels mid-trial | Trial | 10 | Cancelled | (none) |

### Table 2 — Withholds Refund Within 24 Hours of a Renewal Charge

*Billing Cycle is included only to show the rule is indifferent to plan type.*

| Scenario | Hours Since Last Renewal Charge | Billing Cycle | Refund Eligible? |
|---|---|---|---|
| Cancels immediately after a charge | 0 | {Monthly, Annual} | no |
| Cancels at the edge of the 24-hour window | 24 | {Monthly, Annual} | no |
| Cancels just past the 24-hour window | 24.5 | {Monthly, Annual} | yes |
| Cancels well into the cycle | 72 | {Monthly, Annual} | yes |

### Table 3 — Prorates the Refund by Unused Days in the Cycle

*Assumes eligibility already established by Table 2 — these rows all fall outside the 24-hour window.*

| Scenario | Cycle Amount (£) | Cycle Length (Days) | Days Remaining (Unused) | Refund Amount (£)? |
|---|---|---|---|---|
| Monthly plan, half the cycle unused | 9.99 | 30 | 15 | 5.00 |
| Monthly plan, cancels on the last day | 9.99 | 30 | 0 | 0.00 |
| Annual plan, most of the cycle unused | 95.88 | 365 | 182 | 47.81 |
| Annual plan, cancels on the last day | 95.88 | 365 | 0 | 0.00 |

---

**Why three tables, not one:** trial conversion is a state transition, refund eligibility is a yes/no classification driven by the 24-hour boundary, and the refund amount is arithmetic that only makes sense once eligibility is already "yes" — fusing the classification and the arithmetic would make cells like £5.00 impossible to verify in one step from the row (per the *Separate Rules from Arithmetic* guideline). A combined "cancel and get refund end-to-end" table isn't included because it wouldn't show anything these three don't already establish.

Ready to hand this to `/tabletest` for implementation once you confirm the two open items above.