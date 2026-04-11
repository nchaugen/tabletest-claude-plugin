Two concerns need separate tables: the **discount rule by passenger type** (who gets Reis vs. flat) and the **Reis discount level** (how trip count maps to discount percentage). A third small table verifies the final price arithmetic.

---

## Table 1: Reis Discount Level (Adult / Senior)

The discount ladder based on total single tickets purchased in the last 30 days, counting the current ticket.

| Scenario | Trips in last 30 days (incl. current) | Discount? |
|---|---|---|
| First trip | 1 | 0% |
| Below first step | 4 | 0% |
| First discount step | 5 | 5% |
| Within first step | 9 | 5% |
| Second step boundary | 10 | 10% |
| Within second step | 14 | 10% |
| Third step boundary | 15 | 15% |
| Fourth step boundary | 20 | 20% |
| Fifth step boundary | 25 | 25% |
| Sixth step boundary | 30 | 30% |
| Seventh step boundary | 35 | 35% |
| Just below maximum | 39 | 35% |
| Maximum discount | 40 | 40% |
| Above maximum | 45 | 40% |

> **Open question:** Does "trips in the last 30 days, measured at the time of purchase" include or exclude the ticket being purchased right now? The rule "first discount applies to ticket number five" is only consistent if the current ticket is *included* in the count (count=5 → 5%). If excluded (count=4 → 0%), the first discount would land on ticket #6. The table above assumes **included**. Confirm with domain before implementing.

---

## Table 2: Discount by Passenger Type

| Scenario | Passenger type | Trips in last 30 days | Discount rule? | Discount? |
|---|---|---|---|---|
| Adult below threshold | Adult | 4 | Reis (earned) | 0% |
| Adult at first step | Adult | 5 | Reis (earned) | 5% |
| Adult at maximum | Adult | 40 | Reis (earned) | 40% |
| Senior below threshold | Senior | 4 | Reis (earned) | 0% |
| Senior at first step | Senior | 5 | Reis (earned) | 5% |
| Senior at maximum | Senior | 40 | Reis (earned) | 40% |
| Child — flat discount, regardless of trips | Child | {1, 4, 5, 40} | Flat | 20% |

> **Note — zone:** The spec states the Reis discount applies "regardless of the zones you travel in." Zone is not a column because it never affects the outcome. A single integration row confirming this may be worth adding once zone data is available in the model.

> **Open question:** Are there other passenger types (e.g. youth, student)? The spec mentions only Adult, Senior, and Child. If more exist, they need their own rows here.

---

## Table 3: Final Ticket Price

Arithmetic check once discount is known. Keeps rule logic (Tables 1–2) separate from price calculation.

| Scenario | Base price | Passenger type | Trips in last 30 days | Final price? |
|---|---|---|---|---|
| Adult, no discount yet | 50.00 | Adult | 4 | 50.00 |
| Adult, first step (5%) | 50.00 | Adult | 5 | 47.50 |
| Adult, 10% discount | 50.00 | Adult | 10 | 45.00 |
| Adult, max discount (40%) | 50.00 | Adult | 40 | 30.00 |
| Child flat discount (20%) | 50.00 | Child | 1 | 40.00 |
| Child flat discount, frequent traveller | 50.00 | Child | 40 | 40.00 |

---

### Remaining open questions

| # | Question | Impact |
|---|---|---|
| 1 | Is the current ticket included in the 30-day count? (See Table 1 note) | Shifts every threshold by one ticket |
| 2 | Other passenger types beyond Adult / Senior / Child? | New rows in Table 2 |
| 3 | Does a rolling-window drop (travel pattern decreases) apply mid-trip-sequence, or only at next purchase? | Affects discount recalculation timing |
| 4 | What is the standard base price? Is it fixed or zone-dependent? | Affects Table 3; clarify whether zone affects base price even if not the discount rate |