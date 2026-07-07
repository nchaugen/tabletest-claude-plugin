I added the `tabletest-junit` dependency to `build.gradle`. Before writing the full tables, I want to lock down the discount *rule* since it's genuinely underspecified — getting this wrong bakes an incorrect spec into every test.

**My reading of the rule as a formula:**

`discount% = min(40, floor(N / 5) * 5)`, where `N` = count of `SINGLE` tickets purchased by this traveler in the trailing 30 days, counting the ticket being purchased right now.

This single formula would explain every behavior in the prompt: 0% for tickets 1–4, +5% at ticket 5, +5% every 5th ticket thereafter, capped at 40%, and the "levels off / decreases if you travel less / increases if you travel more" behavior falls out automatically since `N` is a rolling window recomputed on every purchase — no separate "leveling" logic needed.

I'd split this into two concerns, per the skill's rule-vs-derivation split:

1. **Tier lookup** (`Discount tier by trailing ticket count`) — pure rule: given `N`, what's the discount? Applies identically to ADULT and SENIOR.
2. **Trailing count derivation** (`Counting single tickets in the last 30 days`) — given purchase history + "now", what's `N`? This has its own boundary questions (exactly 30 days ago — in or out?).

Plus two simple ones: **child flat 20%** discount, and a small **final-price arithmetic** table.

Mockup for the two core tables:

```
Discount tier by trailing ticket count
Scenario                | Traveler Category | Tickets in last 30 days | Discount?
Before first threshold  | {ADULT, SENIOR}    | 4                       | 0%
First discount applies  | {ADULT, SENIOR}    | 5                       | 5%
Between tiers           | {ADULT, SENIOR}    | 9                       | 5%
Second tier             | {ADULT, SENIOR}    | 10                      | 10%
At the cap              | {ADULT, SENIOR}    | 40                      | 40%
Beyond the cap          | {ADULT, SENIOR}    | 45                      | 40%

Counting single tickets in the last 30 days
Scenario                       | Past Single Ticket Purchases (days ago) | Other Ticket Types (days ago) | Count?
No history                     | []                                      |                                | 0
Exactly at the 30-day boundary | [30]                                    |                                | ?
Just outside the window        | [31]                                    |                                | 0
Weekly/monthly excluded        | []                                      | [1]                            | 0
```

Questions before I write the full tables:

1. Is `N` inclusive of the ticket being purchased right now (so 4 prior + this one = 5)? Or is it 5 *prior* purchases (so the 5th trip overall has 4 in history)?
2. Is a purchase from exactly 30 days ago still "in the last 30 days," or does the window exclude it?
3. Do only `SINGLE` tickets count toward `N`, or should `WEEKLY`/`MONTHLY` purchases in history count too?
4. Does the ladder ever apply per-traveler only (i.e., is `PastPurchase` history already scoped to one traveler), or could history contain other people's tickets that need filtering?

If my defaults above look right, say so and I'll go ahead and write the tables as shown, plus the child-discount and final-price tables.