Good fit for spec-by-example — state machines with time-based rules have exactly the kind of boundary cases that bite you mid-implementation. Let me work through this systematically.

---

## Two Concerns, Two Tables

The behaviour you described splits cleanly into two separate decisions:

1. **Order Status Transition** — is a given state change allowed?
2. **Return Eligibility** — can a delivered order be flagged for return?

These belong in separate tables. The transition table is about the state machine; return eligibility is a time-based rule that applies after delivery. Mixing them would force blank cells for non-delivered states in the return columns, which obscures both rules.

---

## Table 1: Order Status Transition

| Scenario | Current Status | Target Status | Allowed? | Reason? |
|---|---|---|---|---|
| Standard order confirmation | PENDING | CONFIRMED | yes | |
| Standard dispatch | CONFIRMED | SHIPPED | yes | |
| Mark as delivered | SHIPPED | DELIVERED | yes | |
| Cancel pre-shipment order | {PENDING, CONFIRMED} | CANCELLED | yes | |
| Cancel shipped order | SHIPPED | CANCELLED | no | Order already dispatched |
| Cancel delivered order | DELIVERED | CANCELLED | no | Order already dispatched |
| Cancel already-cancelled order | CANCELLED | CANCELLED | no | Already cancelled |
| Reverse shipment to confirmed | SHIPPED | CONFIRMED | no | Cannot reverse order flow |
| Skip confirmation step | PENDING | SHIPPED | ? | **Open: is direct dispatch allowed?** |
| Re-open cancelled order | CANCELLED | PENDING | ? | **Open: is re-opening supported?** |

The row `{PENDING, CONFIRMED} → CANCELLED` uses a value set to express the rule directly: cancellation is allowed from *any* pre-shipment state, not just the two cases individually.

---

## Table 2: Return Eligibility

| Scenario | Current Status | Days Since Delivery | Return Window (Policy) | Return Eligible? | Reason? |
|---|---|---|---|---|---|
| Recent delivery, well within window | DELIVERED | 15 | 30 days | yes | |
| At the boundary — last eligible day | DELIVERED | 30 | 30 days | ? | **Open: is day 30 inclusive?** |
| One day past the window | DELIVERED | 31 | 30 days | no | Return window expired |
| Long after delivery | DELIVERED | 90 | 30 days | no | Return window expired |
| Order not yet delivered | {PENDING, CONFIRMED, SHIPPED} | | 30 days | no | Order not yet delivered |
| Cancelled order | CANCELLED | | 30 days | no | Order was cancelled |

`Return Window (Policy)` is a constant column here — but making it explicit signals it comes from configuration, not the request. If the window ever varies by order type or customer tier, you'd add a column rather than hunt for a hardcoded `30`.

Blank cells in `Days Since Delivery` for non-delivered states mean "genuinely absent" — the delivery date doesn't exist yet.

---

## Open Questions to Resolve Before Coding

1. **Day-30 boundary** — is the 30-day window inclusive or exclusive? `DELIVERED, 30 days → eligible?` is the boundary row to nail down.
2. **Direct dispatch** — can `PENDING → SHIPPED` skip CONFIRMED? (A warehouse might ship without a formal confirmation step.)
3. **Re-opening cancelled orders** — is `CANCELLED → PENDING` ever valid?
4. **Return as a state or a flag?** — Is "flagged for return" a new state (e.g., `RETURN_REQUESTED`) in the same state machine, or a separate operation? If it's a state, Table 1 needs a row for `DELIVERED → RETURN_REQUESTED`. If it's a side-effect, it belongs in Table 2's output columns.
5. **What happens after DELIVERED?** — Are there any other valid transitions out of `DELIVERED` beyond return flagging?

---

Once those open cells are filled in, this table hands off directly to `/tabletest` — the column structure maps 1:1 to `@TableTest` parameters.