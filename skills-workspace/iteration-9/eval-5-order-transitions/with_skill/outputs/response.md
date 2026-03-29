Let me work through this systematically. There are two distinct concerns here — **which transitions are allowed** and **when a return can be requested** — so I'll build a table for each.

---

## Table 1: Order Status Transition

| Scenario | Current Status | Target Status | Allowed? | Reason? |
|---|---|---|---|---|
| Standard confirmation | PENDING | CONFIRMED | yes | |
| Standard dispatch | CONFIRMED | SHIPPED | yes | |
| Standard delivery | SHIPPED | DELIVERED | yes | |
| Cancel before confirmation | PENDING | CANCELLED | yes | |
| Cancel after confirmation | CONFIRMED | CANCELLED | yes | |
| Cancel after dispatch | {SHIPPED, DELIVERED} | CANCELLED | no | Already dispatched |
| Skip confirmation step | PENDING | SHIPPED | no | Must confirm first |
| Skip directly to delivered | PENDING | DELIVERED | no | Must confirm first |
| Reverse a transition | CONFIRMED | PENDING | no | Cannot reverse |
| Transition from cancelled | CANCELLED | {PENDING, CONFIRMED, SHIPPED} | no | Order is cancelled |
| Same-state transition | PENDING | PENDING | ? | Open: no-op or error? |

The value set on row 6 is the key rule: cancellation is blocked from **any** post-dispatch state, not just SHIPPED specifically. Without it you'd have two near-identical rows that could drift out of sync.

**Open questions:**
- Same-state transition (last row): is this silently ignored, or an error?
- Can CONFIRMED skip back to PENDING (e.g. payment failure)? Not covered here — if that's a real case it needs its own row.

---

## Table 2: Return Request Eligibility

| Scenario | Order Status | Days Since Delivery | Return Window (Policy) | Return Allowed? | Reason? |
|---|---|---|---|---|---|
| Early return request | DELIVERED | 15 | 30 days | yes | |
| Return on last day | DELIVERED | 30 | 30 days | ? | Open: is day 30 within or past the window? |
| Return just after window | DELIVERED | 31 | 30 days | no | Return window expired |
| Same-day return | DELIVERED | 0 | 30 days | yes | |
| Return on not-yet-delivered order | {PENDING, CONFIRMED, SHIPPED} | — | 30 days | no | Not yet delivered |
| Return on cancelled order | CANCELLED | — | 30 days | no | Order cancelled |

Days Since Delivery is blank for non-DELIVERED rows because it's genuinely absent — the order has no delivery date. `Return Window (Policy)` is kept as an explicit column because it's a configurable threshold, not a magic constant; making it visible now also means changing it later (e.g., to 14 days for a product category) is a table row rather than a code change.

**Open questions:**
- Day 30 boundary (row 2): "within 30 days" is ambiguous — `< 30` or `<= 30`? Needs a call before coding the comparison.
- Does the return window start from delivery timestamp or calendar day?

---

Once these are agreed on, both tables map directly to `@TableTest` — the column structure carries over as-is. Start with the transition table; the boundary question on the return window needs resolving first.