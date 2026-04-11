Great fit for spec-by-example. There are two distinct concerns here — let me separate them.

---

## Table 1: Order Status Transitions

| Scenario | Current Status | Requested Status | Allowed? | Reason? |
|---|---|---|---|---|
| Normal progression to confirmed | PENDING | CONFIRMED | yes | |
| Cancel before confirmation | PENDING | CANCELLED | yes | |
| Advance to shipping | CONFIRMED | SHIPPED | yes | |
| Cancel after confirmation | CONFIRMED | CANCELLED | yes | |
| Mark as delivered | SHIPPED | DELIVERED | yes | |
| Cancellation after dispatch | SHIPPED | CANCELLED | no | Order already dispatched |
| Cancellation of delivered order | DELIVERED | CANCELLED | no | Order already delivered |
| Transition from cancelled | CANCELLED | {PENDING, CONFIRMED, SHIPPED} | no | Order is cancelled |
| Reverse a delivery | DELIVERED | SHIPPED | no | Cannot reverse delivery |

**Open questions:**
- Can orders skip states? e.g. PENDING → SHIPPED (fulfilment centre bypasses confirmation)? Mark as `?` until resolved.
- Is CANCELLED truly terminal, or can customer service reopen orders?

---

## Table 2: Return Request Eligibility

Return flagging is a separate concern — it applies only to delivered orders and has its own time rule.

| Scenario | Order Status | Days Since Delivery | Return Window (Policy) | Return Request Allowed? | Reason? |
|---|---|---|---|---|---|
| Request well within window | DELIVERED | 5 | 30 days | yes | |
| Request on final day of window | DELIVERED | 30 | 30 days | yes | |
| Request one day past window | DELIVERED | 31 | 30 days | no | Past 30-day return window |
| Request long after delivery | DELIVERED | 90 | 30 days | no | Past 30-day return window |
| Order not yet delivered | {PENDING, CONFIRMED, SHIPPED} | | 30 days | no | Order not delivered |
| Cancelled order return request | CANCELLED | | 30 days | no | Order was cancelled |

The `Return Window (Policy)` column is constant at 30 days here — but making it explicit signals it's a configurable policy, not a hardcoded constant. If it ever varies by product category or customer tier, that column is already in the right place.

**Open questions:**
- Day 30: is the boundary *less than* 30 or *less than or equal to* 30? The table says "yes" on day 30 — confirm this.
- Does flagging for return change the order status (e.g. to RETURN\_REQUESTED), or is it a side-effect with no status change? If there's a new state, it belongs back in Table 1.

---

Two clean tables rather than one sprawling one — the transition rules don't need `Days Since Delivery`, and the return rules don't need `Requested Status`. Once you've confirmed the open questions, `/tabletest` can turn either table directly into `@TableTest` code.