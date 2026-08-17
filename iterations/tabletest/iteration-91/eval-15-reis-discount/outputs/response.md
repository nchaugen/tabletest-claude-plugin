All three test classes compile, run, and fail only on the stub `UnsupportedOperationException` — confirming the table data, converters, and value-set expansions are wired correctly.

**What I built:**
- `ReisDiscountLadderTest` — pure ladder function (`ticketNumber → discount%`), 17 rows covering all 8 tiers and both sides of every boundary (5,10,...,40) plus the cap.
- `RollingSingleTicketCounterTest` — the 30-day window + ticket-type filter, with an hours-ago boundary at exactly 720h (30 days) vs 721h.
- `SingleTicketDiscountCalculatorTest` — the category branch (child flat 20% regardless of history, via a value-set proving history is ignored; adult/senior baseline at 0%) plus one wiring row proving the top-level calculator actually composes the counter and ladder.

**One ambiguity I had to resolve** (documented in `ReisDiscountLadderTest`'s `@Description`): the spec says "the first discount applies to ticket number five," but also says the discount is based on tickets "you have purchased" in the last 30 days. Taken literally, your *4th* previous ticket plus the one you're now buying makes this your 5th ticket — so I made the ladder key off the **ticket's ordinal position in the rolling window, counting the purchase in progress** (`trailingHistoryCount + 1`), not the raw history count alone. This is what makes "ticket #5 gets the first discount" literally true. If the intended behavior is instead "discount kicks in only once your history alone shows 5 prior tickets," that's a one-line change (drop the `+1`) and all 17 ladder rows would need shifting by one — worth confirming with the business before implementing.

Also note: `PurchaseHistoryRepository` wasn't threaded into the tests directly — the calculator takes `List<PastPurchase>` and `LocalDateTime now` as plain parameters for testability; a real caller would populate the list via `PurchaseHistoryRepository.loadPurchaseHistory()`.

Next step would be implementing `ReisDiscountLadder`, `RollingSingleTicketCounter`, and `ReisSingleTicketDiscountCalculator` to make these pass — say the word and I'll do that.