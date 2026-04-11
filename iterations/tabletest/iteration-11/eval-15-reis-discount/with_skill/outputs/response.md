## Reis Discount: Spec by Example

Two distinct concerns are in play here: the **discount tier ladder** for adults and seniors (dynamic, based on travel history), and the **flat child discount** (always 20%). I'll give each its own table.

---

### Table 1 — Reis Discount Tier (Adult / Senior)

The central rule: discount is determined by how many single tickets the passenger has purchased in the last 30 days, counted at the moment of purchase (i.e. *before* the current ticket is bought).

| Scenario | Passenger type | Tickets in last 30 days | Discount? |
|---|---|---|---|
| First ticket, no history | {Adult, Senior} | 0 | 0% |
| Second ticket | {Adult, Senior} | 1 | 0% |
| Fourth ticket (just below first tier) | {Adult, Senior} | 3 | 0% |
| Fifth ticket — first discount applies | {Adult, Senior} | 4 | 5% |
| Within first tier | {Adult, Senior} | 7 | 5% |
| Ninth ticket (top of first tier) | {Adult, Senior} | 8 | 5% |
| Tenth ticket — second tier begins | {Adult, Senior} | 9 | 10% |
| Fifteenth ticket | {Adult, Senior} | 14 | 15% |
| Twentieth ticket | {Adult, Senior} | 19 | 20% |
| Twenty-fifth ticket | {Adult, Senior} | 24 | 25% |
| Thirtieth ticket | {Adult, Senior} | 29 | 30% |
| Thirty-fifth ticket | {Adult, Senior} | 34 | 35% |
| Fortieth ticket — maximum tier begins | {Adult, Senior} | 39 | 40% |
| Well above maximum (discount capped) | {Adult, Senior} | 60 | 40% |

The tier boundaries implied by this table:

| Tickets in last 30 days | Discount |
|---|---|
| 0 – 3 | 0% |
| 4 – 8 | 5% |
| 9 – 13 | 10% |
| 14 – 18 | 15% |
| 19 – 23 | 20% |
| 24 – 28 | 25% |
| 29 – 33 | 30% |
| 34 – 38 | 35% |
| 39 + | 40% |

---

### Table 2 — Child Flat Discount

Children always receive a flat 20% regardless of how many tickets they have purchased. The value set on the history column makes the "regardless of" explicit.

| Scenario | Passenger type | Tickets in last 30 days | Discount? |
|---|---|---|---|
| Child: flat discount, no travel history | Child | 0 | 20% |
| Child: flat discount, frequent traveller | Child | {4, 9, 39, 60} | 20% |

---

### Table 3 — Final ticket price (arithmetic check)

Once the discount is known, the price calculation is straightforward. A small table is enough to confirm the formula.

| Scenario | Base price | Discount | Final price? |
|---|---|---|---|
| No discount applied | 40.00 | 0% | 40.00 |
| First tier (5%) | 40.00 | 5% | 38.00 |
| Maximum discount (40%) | 40.00 | 40% | 24.00 |
| Child flat discount (20%) | 40.00 | 20% | 32.00 |

---

### Open questions

These need answers before or during implementation:

| # | Question |
|---|---|
| 1 | Is "tickets in last 30 days" counted **before** the current purchase (as assumed above), or does the count include it? The phrase "ticket number five gets the first discount" is consistent with both interpretations — clarify. |
| 2 | Do cancelled or refunded tickets count toward the 30-day total? |
| 3 | The spec mentions "single tickets in the app" — does a purchase made through another channel (e.g. a physical kiosk) count toward the 30-day history? |
| 4 | Are there other passenger types (e.g. student, youth) and if so, which bucket do they fall into — the Reis ladder, the flat child rate, or neither? |
| 5 | What happens at exactly the tier boundary when two purchases happen simultaneously (race condition)? |