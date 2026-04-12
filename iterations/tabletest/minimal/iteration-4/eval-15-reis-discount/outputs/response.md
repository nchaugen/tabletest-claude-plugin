Looking at the Reis feature, I can identify two separable concerns worth specifying:

1. **Discount percentage** — given passenger type and ticket history, what discount applies?
2. **Rolling 30-day window** — which prior tickets count toward the window at purchase time?

---

## Table 1: Single Ticket Discount

The discount applied when purchasing a single ticket, based on passenger type and how many single tickets have been purchased in the rolling 30-day window (including the current purchase).

| Scenario | Passenger Type | Tickets in last 30 days (incl. this one) | Discount? |
|---|---|---|---|
| Child, no purchase history | Child | 1 | 20% |
| Child discount is always flat | Child | 10 | 20% |
| Adult, first ticket | Adult | 1 | 0% |
| Adult, second through fourth | Adult | 4 | 0% |
| Adult, fifth — first discount applies | Adult | 5 | 5% |
| Adult, ninth — still in first tier | Adult | 9 | 5% |
| Adult, tenth — second tier | Adult | 10 | 10% |
| Adult, fourteenth — still second tier | Adult | 14 | 10% |
| Adult, fifteenth — third tier | Adult | 15 | 15% |
| Adult, twentieth — fourth tier | Adult | 20 | 20% |
| Adult, twenty-fifth | Adult | 25 | 25% |
| Adult, thirtieth | Adult | 30 | 30% |
| Adult, thirty-fifth | Adult | 35 | 35% |
| Adult, fortieth — maximum discount reached | Adult | 40 | 40% |
| Adult, beyond maximum | Adult | 50 | 40% |
| Senior receives same discount as adult | Senior | 5 | 5% |
| Senior at maximum | Senior | 40 | 40% |

**Open questions:**
- Are there passenger types other than Adult, Senior, and Child? (e.g., Youth, Student — do they follow the Reis ladder, the child flat rate, or something else?)
- Does a Child with a long travel history ever get more than 20%?

---

## Table 2: Rolling 30-day Window

Which prior tickets count toward the "last 30 days" window at the moment of purchase?

| Scenario | Purchase date | Previous ticket dates | Tickets counted? |
|---|---|---|---|
| No purchase history | 2026-04-12 | (none) | 0 |
| All tickets well within window | 2026-04-12 | 2026-03-20, 2026-03-25 | 2 |
| Some tickets outside the window | 2026-04-12 | 2026-03-10, 2026-03-25 | 1 |
| Ticket exactly 30 days ago | 2026-04-12 | 2026-03-13 | ? |
| Ticket 31 days ago | 2026-04-12 | 2026-03-12 | 0 |
| Mixed: some in, some out | 2026-04-12 | 2026-02-01, 2026-03-15, 2026-04-01 | 2 |

**Open questions:**
- Is a ticket purchased exactly 30 days ago included or excluded from the window? (Is the boundary `>= 30 days ago` or `> 30 days ago`?)
- Are tickets purchased the same day as the current purchase all counted?

---

## Notes on what is not tested here

- **Zone does not affect discount** — the spec states this explicitly; no zone column is needed.
- **Arithmetic** — once the discount % is known, `price paid = standard price × (1 − discount%)` is straightforward multiplication and does not need a table of its own.
- **Discount changing over time** (travel more/less across multiple weeks) is a consequence of the rolling window shrinking or growing — the two tables above are sufficient to specify the rule. Sequential narrative examples ("after week 3 you travel less…") belong in a prose walkthrough or a `@Test`, not in these tables.