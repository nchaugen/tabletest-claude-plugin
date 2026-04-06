Good fit for this skill. Let me work through the examples with you.

---

## Naming the Concerns

Your description already hints at two separate rules:
1. **Order Cancellation** — which states permit cancellation
2. **Return Eligibility** — time-windowed rule for delivered orders

These are distinct enough to warrant separate tables. Let's start with cancellation since you described that rule most concretely.

---

## Table 1: Order Cancellation

| Scenario | Current Status | Cancellation Allowed? | Reason? |
|---|---|---|---|
| Cancel a pre-shipment order | {PENDING, CONFIRMED} | yes | |
| Cancel after dispatch | SHIPPED | no | Already dispatched |
| Cancel after delivery | DELIVERED | no | Already delivered |
| Cancel an already-cancelled order | CANCELLED | no | Already cancelled |

A few things worth confirming:
- The value set `{PENDING, CONFIRMED}` expresses "cancellation is allowed from either pre-shipment state" as a single rule — does that match your intent?
- Is the rejection reason for SHIPPED vs DELIVERED meaningfully different, or should they share a single reason? If the message is the same, they could collapse into `{SHIPPED, DELIVERED}`.
- What happens to a CANCELLED order — is it a terminal state (no further transitions at all), or only cancellation is blocked?

---

## Table 2: Return Eligibility

You mentioned DELIVERED orders can be flagged for return within 30 days. That 30-day window is a policy threshold — worth making explicit as a column so the rule is readable without digging into code.

| Scenario | Order Status | Days Since Delivery | Return Window (Policy) | Return Eligible? | Reason? |
|---|---|---|---|---|---|
| Return requested same day | DELIVERED | 0 | 30 days | yes | |
| Return within window | DELIVERED | 15 | 30 days | yes | |
| Return on last day of window | DELIVERED | 30 | 30 days | ? | Open: is day 30 inclusive? |
| Return just outside window | DELIVERED | 31 | 30 days | no | Outside return window |
| Return long after delivery | DELIVERED | 90 | 30 days | no | Outside return window |
| Order not yet delivered | {PENDING, CONFIRMED, SHIPPED} | — | 30 days | no | Order not delivered |
| Cancelled order | CANCELLED | — | 30 days | no | Order not delivered |

Two open questions flagged:
1. **Day 30 boundary** — is it `<= 30` (inclusive) or `< 30` (exclusive)? This is where bugs hide.
2. **Days Since Delivery for non-DELIVERED states** — marked `—` (genuinely absent). Does your system even surface this field before delivery, or is it only set on the DELIVERED transition?

---

## What's Not Covered Yet

A few areas you haven't described — worth deciding now or flagging as open:

- **Forward transitions** — the valid path PENDING → CONFIRMED → SHIPPED → DELIVERED. Any rules there (e.g. can you skip CONFIRMED and go straight to SHIPPED)?
- **Can a DELIVERED order transition to anything other than a return flag?** Is "flagged for return" a new state (e.g. `RETURN_REQUESTED`) or a side attribute?
- **Who can cancel?** Is cancellation available to any actor, or are there role/actor restrictions? If so, that becomes an input column.

Want to nail down the day-30 boundary and the forward transitions, or is the cancellation + return table enough to start?