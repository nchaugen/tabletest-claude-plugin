Here's a structured set of examples covering the full state machine, organized by scenario:

---

## Valid Transitions

| # | From | Event | To | Notes |
|---|------|-------|----|-------|
| 1 | PENDING | confirm | CONFIRMED | Normal flow |
| 2 | CONFIRMED | ship | SHIPPED | Normal flow |
| 3 | SHIPPED | deliver | DELIVERED | Normal flow |
| 4 | PENDING | cancel | CANCELLED | Allowed |
| 5 | CONFIRMED | cancel | CANCELLED | Allowed |
| 6 | DELIVERED | flag_return | RETURN_REQUESTED | Within 30 days |

---

## Invalid Transitions (should error)

| # | From | Event | Expected Error |
|---|------|-------|----------------|
| 7 | SHIPPED | cancel | "Cannot cancel order after it has shipped" |
| 8 | DELIVERED | cancel | "Cannot cancel order after it has shipped" |
| 9 | CANCELLED | cancel | "Order is already cancelled" |
| 10 | CANCELLED | confirm | "Cannot transition from CANCELLED" |
| 11 | DELIVERED | flag_return | "Return window expired" (> 30 days since delivery) |
| 12 | SHIPPED | flag_return | "Order not yet delivered" |
| 13 | PENDING | deliver | "Invalid transition: PENDING → DELIVERED" (skipping steps) |

---

## Edge Cases

| # | Scenario | Expected |
|---|----------|----------|
| 14 | Flag return exactly on day 30 | Allowed (boundary inclusive) |
| 15 | Flag return on day 31 | Rejected — window expired |
| 16 | Flag return on day 0 (same day delivery) | Allowed |
| 17 | Cancel a CANCELLED order | Error — idempotency question: reject or silently succeed? |
| 18 | Deliver a DELIVERED order | Error — same idempotency question |
| 19 | RETURN_REQUESTED → cancel? | Decide: should returns be cancellable? |
| 20 | Two concurrent cancel requests on CONFIRMED | One succeeds, one gets "already cancelled" |

---

## Questions to settle before coding

1. **Return state**: Is `RETURN_REQUESTED` a new state, or just a boolean flag on the order? (A state makes the 30-day check easier to enforce at transition time.)
2. **30-day clock**: Does it start at the `DELIVERED` timestamp, or order creation?
3. **Idempotency (#17, #18)**: Should repeating a terminal-state event be a no-op or an error?
4. **Skip transitions (#13)**: Do you want to explicitly reject them, or just not define them (let the default "invalid" case handle it)?
5. **CANCELLED is terminal?**: Can a cancelled order ever be re-opened (e.g., back to PENDING)?

Settling these before coding will save you from retrofitting conditional branches later. Want me to start on the implementation once you've confirmed these?