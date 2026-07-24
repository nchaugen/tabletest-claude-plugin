All tests compile and fail only on the stubbed `UnsupportedOperationException` (expected red state before implementation). Summary of what was added:

**Production stubs** (`src/main/java/com/example/`, each throws `UnsupportedOperationException`):
- `ReisPurchaseCounter.countPriorSingleTicketsInLast30Days(List<PastPurchase>, LocalDate now)` — counts prior qualifying tickets
- `ReisDiscountLadder.discountPercentageFor(int priorQualifyingTickets)` — pure tier lookup
- `SingleTicketDiscountCalculator.calculateDiscount(TravelerCategory, ZoneValidity, int priorQualifyingTickets)` — top-level entry point

**Tests** (`src/test/java/com/example/`), decomposed into three concerns per the tabletest skill's guidance (derived-input counting, tier arithmetic, and category/zone branching are separate concerns even though they combine into one final discount):
- `ReisPurchaseCounterTest` — counts only `SINGLE` tickets, 30-day window boundary (inclusive at exactly 30 days — documented as an assumption since the source text doesn't specify)
- `ReisDiscountLadderTest` — tier table using value sets, covering 0/5/10/15% tiers, the 35% tier just below max, the 39-ticket point where 40% is first reached, and that it never exceeds 40% even at 500 prior tickets
- `SingleTicketDiscountCalculatorTest` — `{ADULT, SENIOR}` collapsed as one value-set axis (treated identically), zone independence via `{ZONE_1, ZONE_2, ZONE_3}`, and `CHILD` at a flat 20% regardless of history or zone

Key assumption worth flagging: the "discount may decrease if you travel less" and "levels off with consistent travel" behaviors aren't separate logic — they fall out naturally from `ReisPurchaseCounter` re-counting a rolling 30-day window on each purchase, which is what its test table exercises directly.

Ran `gradle test`: 61 generated cases (from value-set expansion), all failing cleanly on the stub exception — confirms table syntax, conversions, and expected values are all correct ahead of implementation.