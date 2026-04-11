# Order Status Transitions — Spec by Example

Good call to work out the examples before coding — state transition rules are exactly the kind of thing that looks simple until you enumerate the cases. Let's start with the cancellation rules, then handle the return window separately since it's a different concern.

## Table 1: Order Cancellation

**Concern**: Given the current order status, is cancellation allowed?

| Scenario                                            | Current Status            | Cancellation Allowed? | Reason?                         |
|-----------------------------------------------------|---------------------------|-----------------------|---------------------------------|
| Cancel a pending order                              | PENDING                   | yes                   |                                 |
| Cancel a confirmed order                            | CONFIRMED                 | yes                   |                                 |
| Cancel after shipping                               | SHIPPED                   | no                    | Order already shipped           |
| Cancel a delivered order                            | DELIVERED                 | no                    | Order already delivered         |
| Cancel an already-cancelled order                   | CANCELLED                 | no                    | Order already cancelled         |

**Value set summary**: cancellation is allowed from {PENDING, CONFIRMED} and rejected from {SHIPPED, DELIVERED, CANCELLED}.

## Table 2: Return Eligibility

**Concern**: When can a delivered order be flagged for return?

| Scenario                                    | Current Status | Days Since Delivery | Return Window (Policy) | Return Allowed? | Reason?                          |
|---------------------------------------------|----------------|---------------------|------------------------|-----------------|----------------------------------|
| Return within window                        | DELIVERED      | 15                  | 30 days                | yes             |                                  |
| Return on last day of window                | DELIVERED      | 30                  | 30 days                | ?               | Is day 30 within or outside window? |
| Return just outside window                  | DELIVERED      | 31                  | 30 days                | no              | Outside 30-day return window     |
| Return immediately after delivery           | DELIVERED      | 0                   | 30 days                | yes             |                                  |
| Return requested on non-delivered order     | {PENDING, CONFIRMED, SHIPPED, CANCELLED} | — | 30 days   | no              | Order not yet delivered          |

## Open Questions

1. **Return window boundary** — is day 30 "within" or "outside" the window? The boundary row makes this decision visible and forces a concrete answer.

2. **Non-delivered orders and returns** — can a return be requested for a SHIPPED order (in transit)? Or only once DELIVERED? The table currently says no for all non-DELIVERED statuses, but SHIPPED might warrant a specific conversation.

3. **Cancellation vs. return for delivered orders** — should DELIVERED orders block cancellation but allow returns? The tables above treat these as separate concerns (Table 1 handles cancellation, Table 2 handles returns). Confirm this separation is correct.

4. **Days since delivery source** — is this calculated from a timestamp on the order, or passed in explicitly? This affects the test setup, not the rule itself, but good to agree on now.

5. **Return flow** — "flagged for return" — is this a new status (e.g. `RETURN_REQUESTED`) or a side-effect? If it transitions the order to a new status, Table 1 may need a `RETURN_REQUESTED` row.

## Notes on Design

These are deliberately two separate tables because the concerns are different:
- **Cancellation** depends only on the current status.
- **Return eligibility** depends on status plus a time dimension (days since delivery).

Mixing them would require blank cells for `Days Since Delivery` on every cancellation row — a clear signal the columns belong to different tables.
