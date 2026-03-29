Looking at this feature, I can identify two distinct concerns:
1. The discount rate calculation (the interesting rule — the ladder, child flat rate, max cap)
2. The final ticket price (pure arithmetic — a small verification table)

---

## Reis Discount Rate

| Scenario | Passenger Type | Tickets in Last 30 Days | Discount Rate? |
|---|---|---|---|
| Child, any travel history | Child | {0, 1, 5, 40} | 20% |
| Adult/Senior, no travel history | {Adult, Senior} | 0 | 0% |
| Adult/Senior, below first threshold | {Adult, Senior} | 4 | 0% |
| Adult/Senior, at first discount level | {Adult, Senior} | 5 | 5% |
| Adult/Senior, just below second level | {Adult, Senior} | 9 | 5% |
| Adult/Senior, at second level | {Adult, Senior} | 10 | 10% |
| Adult/Senior, at third level | {Adult, Senior} | 15 | 15% |
| Adult/Senior, at fourth level | {Adult, Senior} | 20 | 20% |
| Adult/Senior, at fifth level | {Adult, Senior} | 25 | 25% |
| Adult/Senior, at sixth level | {Adult, Senior} | 30 | 30% |
| Adult/Senior, at seventh level | {Adult, Senior} | 35 | 35% |
| Adult/Senior, at maximum level | {Adult, Senior} | 40 | 40% |
| Adult/Senior, beyond maximum | {Adult, Senior} | 50 | 40% |

---

## Discounted Ticket Price

| Scenario | Base Price | Discount Rate | Final Price? |
|---|---|---|---|
| No discount applied | 50.00 | 0% | 50.00 |
| 5% discount | 50.00 | 5% | 47.50 |
| Child flat 20% | 50.00 | 20% | 40.00 |
| Maximum 40% discount | 50.00 | 40% | 30.00 |

---

**Open questions before implementation:**

- **"Tickets in last 30 days"** — does the ticket being purchased right now count toward the measurement, or only previously completed purchases? The spec says "measured at the time of purchase," which implies the current ticket is not yet counted, but this should be confirmed.
- **Rolling window boundary** — are tickets purchased exactly 30 days ago included or excluded? (i.e., is the window `> 30 days ago` or `>= 30 days ago`?)
- **Adult vs Senior** — the spec treats them identically. Should these be separate passenger type values in the domain model, or one combined category? Affects how the type is represented in code.
- **Zones** — the spec explicitly states zones do not affect the discount. The zone column is intentionally absent from this table; a domain expert should confirm no zone-specific override exists.