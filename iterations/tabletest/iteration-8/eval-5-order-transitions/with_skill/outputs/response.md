Here are two tables covering the two distinct concerns: **which transitions are allowed** and **when a return can be flagged**.

---

### Table 1: Order Status Transition

| Scenario | Current Status | Target Status | Allowed? | Reason? |
|---|---|---|---|---|
| Confirm a new order | PENDING | CONFIRMED | yes | |
| Dispatch a confirmed order | CONFIRMED | SHIPPED | yes | |
| Mark order as delivered | SHIPPED | DELIVERED | yes | |
| Cancel before confirmation | PENDING | CANCELLED | yes | |
| Cancel confirmed order | CONFIRMED | CANCELLED | yes | |
| Cancel after dispatch | SHIPPED | CANCELLED | no | Already dispatched |
| Cancel a delivered order | DELIVERED | CANCELLED | no | Already delivered |
| Any action on a cancelled order | CANCELLED | {PENDING, CONFIRMED, SHIPPED, DELIVERED} | no | Terminal state |
| Skip straight to shipped | PENDING | SHIPPED | ? | Open: is skipping states allowed? |
| Reverse to earlier state | SHIPPED | {PENDING, CONFIRMED} | ? | Open: are reversals ever allowed? |

The value set `{PENDING, CONFIRMED}` in the cancellation rows makes the rule legible: cancellation is allowed from any pre-shipment state. The two rows before the open questions cover all specified rules.

---

### Table 2: Return Eligibility for Delivered Orders

| Scenario | Order Status | Days Since Delivery | Return Window (Policy) | Return Eligible? | Reason? |
|---|---|---|---|---|---|
| Flagged immediately after delivery | DELIVERED | 0 | 30 days | yes | |
| Flagged well within window | DELIVERED | 15 | 30 days | yes | |
| Flagged one day before window closes | DELIVERED | 29 | 30 days | yes | |
| Flagged exactly at the limit | DELIVERED | 30 | 30 days | ? | Open: is day 30 inside or outside the window? |
| Flagged one day past the limit | DELIVERED | 31 | 30 days | no | Return window closed |
| Order not yet delivered | {PENDING, CONFIRMED, SHIPPED} | — | 30 days | no | Order not delivered |
| Cancelled order | CANCELLED | — | 30 days | no | Order was cancelled |

---

### Open questions to resolve before coding

1. **Day-30 boundary**: Is the return window `days <= 30` (inclusive) or `days < 30` (exclusive)? The row at day 30 is marked `?` — this is where off-by-one bugs live.
2. **Skipping states**: Can PENDING jump directly to SHIPPED (e.g. via an admin override)? If yes, that row becomes `yes` and the reason column stays blank.
3. **Reversals**: Can any state be walked back (e.g. SHIPPED → CONFIRMED to correct a dispatch error)? If not, a value set row captures all illegal reversals cleanly.
4. **Days Since Delivery source**: Is this computed from a `deliveredAt` timestamp, or is it a caller-supplied value? This affects how the transition method is tested.

Once the boundary at day 30 is settled, these tables are ready to hand to `/tabletest`.