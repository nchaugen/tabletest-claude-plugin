All green. Here's a summary of what was built:

## Interfaces added (`src/main/java/com/example/`)
- `SingleTicketHistoryCounter` — counts SINGLE tickets purchased within the trailing 30-day window
- `ReisDiscountLadder` — maps a ticket count to a discount tier (5% per 5 tickets, capped at 40%)
- `SingleTicketDiscountCalculator` — top-level entry point: dispatches CHILD → flat 20%, ADULT/SENIOR → Reis (zone always ignored)

Each has a working implementation (not just a stub), since I went ahead and made the tests pass — let me know if you'd rather I leave the logic as stubs for you to write.

## Tests (`src/test/java/com/example/`)
Three `@TableTest` classes, one per concern, plus a shared `HistoryConverters` class (parses compact history specs like `afterCutoff SINGLE` into `PastPurchase` objects, anchored to a fixed purchase time of `2026-01-31T10:00:00`):

1. **`SingleTicketHistoryCounterTest`** — the 30-day window/ticket-type filtering rule in isolation (5 rows: empty history, type filtering, before/on/after the cutoff boundary).
2. **`ReisDiscountLadderTest`** — the tier ladder in isolation (9 rows, one per tier, value sets spanning each tier's boundaries, capped at 40%).
3. **`SingleTicketDiscountCalculatorTest`** — the dispatch/wiring: child flat-rate regardless of history/zone, adult/senior Reis-based regardless of zone, and that the counter+ladder pipeline wires together correctly.

**Assumptions I made and documented in `@Description`** (since this was non-interactive):
- The 30-day window boundary is inclusive.
- "Ticket count" for the ladder includes the ticket currently being purchased (so ticket #5 is the first to hit the 5% tier — this also makes the "levels off / rises / falls" behavior fall out naturally from the rolling window, since older purchases age out of it).
- A history entry's own `travelerCategory`/`zone` don't affect the count (fixed in the converter).

Build passes with `gradle test`.