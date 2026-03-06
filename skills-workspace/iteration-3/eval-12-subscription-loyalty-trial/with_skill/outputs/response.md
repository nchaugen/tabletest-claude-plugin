# Subscription Billing Specification

There are four distinct concerns here: plan pricing, trial eligibility, cancellation/refund, and the loyalty+annual interaction. The loyalty discount and trial period introduce edge cases that are not yet fully resolved — I've surfaced these explicitly in Table 4.

---

## Table 1: Plan Pricing

| Scenario | Plan Type | Loyalty Member? | Price? |
|---|---|---|---|
| Standard monthly plan | Monthly | {yes, no} | £9.99/month |
| Standard annual plan | Annual | no | £95.88/year |
| Annual plan, loyalty member | Annual | yes | £86.29/year |

Note: Loyalty discount (10%) applies to annual plans only. Monthly price is unchanged regardless of loyalty status.

---

## Table 2: Trial Eligibility

| Scenario | Plan Type | New Subscriber? | Trial Available? | After Trial? |
|---|---|---|---|---|
| New subscriber on monthly | Monthly | yes | yes | Auto-converts to monthly (£9.99/month) |
| New subscriber on annual | Annual | yes | no | Charged immediately (£95.88 or £86.29 with loyalty) |
| Existing subscriber | {Monthly, Annual} | no | no | Not eligible for trial |

---

## Table 3: Cancellation & Refund

| Scenario | Plan Type | Timing | Refund? | Amount? |
|---|---|---|---|---|
| Mid-cycle cancellation | {Monthly, Annual} | More than 24h after renewal | yes | Prorated for unused days |
| Cancellation within 24h of renewal | {Monthly, Annual} | Within 24h of renewal | no | — |
| Cancellation during free trial | Monthly | During 30-day trial | yes | Full refund (no charge taken) |

---

## Table 4: Loyalty + Trial Interaction (Open Questions)

The prompt notes uncertainty about how loyalty and trial interact. The following table distinguishes resolved cases from open questions.

| Scenario | Plan | Loyalty Member? | Trial Phase | Outcome? | Status |
|---|---|---|---|---|---|
| New loyalty member subscribes directly to annual | Annual | yes | n/a | £86.29/year, no trial | Resolved |
| New loyalty member starts monthly trial | Monthly | yes | trial | £0 for 30 days | Resolved |
| Loyalty member after trial auto-converts to monthly | Monthly | yes | post-trial | £9.99/month | Resolved |
| Trial subscriber switches to annual mid-trial (loyalty member) | Annual | yes | mid-trial | ? | **Open question** |
| Non-loyalty trial subscriber joins loyalty programme mid-trial | Monthly | becomes yes | mid-trial | ? | **Open question** |

**Open questions for resolution:**

1. Can a subscriber currently on the free trial (monthly) switch to the annual plan before the trial ends?
   - If yes: does the 10% loyalty discount apply immediately?
   - If yes: do unused trial days carry over as credit, or are they forfeited upon upgrade?

2. If a monthly trial subscriber joins the loyalty programme mid-trial, does this entitle them to switch to annual at the discounted rate during the trial period?
