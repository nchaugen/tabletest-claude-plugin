Since this needs to run without follow-up questions, I made a few explicit assumptions (called out below) rather than guessing silently. Following the spec-by-example approach, I split this into two tables — the state machine's transition rule is one concern, and the return-flag eligibility rule is a different concern (it has its own input, days since delivery, that doesn't apply anywhere else).

## Assumptions (please confirm before coding)

1. Forward progression is strictly sequential — `PENDING → CONFIRMED → SHIPPED → DELIVERED`, no skipping steps.
2. `CANCEL` is only valid from `PENDING` or `CONFIRMED` (as you stated).
3. "Flagged for return" is a separate attribute on a `DELIVERED` order, not a new status — you didn't list a `RETURN_REQUESTED` state, so status stays `DELIVERED` after flagging.
4. The 30-day return window is measured from the delivery date and is **inclusive** of day 30 (day 31 is rejected). This is a guess — flag it if you intended exclusive.

## Table 1 — Transitions an Order Between Statuses

| Scenario | Status Before | Action | Status After? | Rejected Reason? |
|---|---|---|---|---|
| Confirms a pending order | PENDING | CONFIRM | CONFIRMED | |
| Ships a confirmed order | CONFIRMED | SHIP | SHIPPED | |
| Delivers a shipped order | SHIPPED | DELIVER | DELIVERED | |
| Cancels a pending order | PENDING | CANCEL | CANCELLED | |
| Cancels a confirmed order | CONFIRMED | CANCEL | CANCELLED | |
| Rejects cancelling a shipped order | SHIPPED | CANCEL | SHIPPED | Cannot cancel after shipping |
| Rejects cancelling an already-cancelled order | CANCELLED | CANCEL | CANCELLED | Order already cancelled |
| Rejects shipping an order that hasn't been confirmed | PENDING | SHIP | PENDING | Order must be confirmed before shipping |
| Rejects delivering an order that hasn't shipped | CONFIRMED | DELIVER | CONFIRMED | Order must be shipped before it can be delivered |

Notes:
- `Cancels a confirmed order` / `Rejects cancelling a shipped order` is the deliberate boundary pair — the last status where cancellation is allowed, next to the first where it isn't.
- I dropped a redundant "rejects cancelling a delivered order" row — it would reject for the identical reason as the shipped case, one step further past the same boundary, and adds nothing new.

## Table 2 — Flags a Delivered Order for Return

| Scenario | Status | Days Since Delivery | Return Window (Policy, days) | Eligible For Return Flag? | Rejected Reason? |
|---|---|---|---|---|---|
| Flags a return well within the window | DELIVERED | 5 | 30 | yes | |
| Flags a return on the last eligible day | DELIVERED | 30 | 30 | yes | |
| Rejects flagging just past the window | DELIVERED | 31 | 30 | no | Return window has expired |
| Rejects flagging an order that hasn't been delivered | {SHIPPED, CANCELLED} | | 30 | no | Order has not been delivered |

Notes:
- `Days Since Delivery` is blank in the last row — there's no delivery date to measure from, since the order was never delivered.
- The last row uses a value set `{SHIPPED, CANCELLED}` because the rejection reason is the same regardless of which non-delivered status the order is in — that's a claim worth stating as data rather than duplicating rows.

## Open questions to resolve before implementation

- Is the 30-day boundary inclusive (day 30 allowed) or exclusive?
- Can a `DELIVERED` order be flagged for return more than once — does a first flag change any state that blocks a second attempt?
- Should invalid/skipped transitions (e.g. `SHIP` requested directly from `PENDING`) be silently rejected with an error, or should some of them be allowed as a fast path?

Once these are confirmed, hand this table structure straight to `/tabletest` (Java/Kotlin) or `/table-driven-testing` (other languages) — the columns map directly to test parameters and the `?`-suffixed columns become your assertions.