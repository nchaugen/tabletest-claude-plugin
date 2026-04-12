## Spec by Example: Reis Travel Discount

Two tables cover the core behaviour. The discount ladder is the central rule; passenger type determines which rule applies.

---

### Table 1 — Reis Discount Tier (Adults and Seniors)

The discount on a ticket depends on how many single tickets the customer purchased in the last 30 days **before** the current purchase.

| Scenario | Trips in last 30 days | Discount? |
|---|---|---|
| No travel history | 0 | 0% |
| Below first threshold | 3 | 0% |
| At first threshold — fifth ticket | 4 | 5% |
| Within first tier | 8 | 5% |
| At second threshold — tenth ticket | 9 | 10% |
| At third threshold — fifteenth ticket | 14 | 15% |
| Mid-ladder | 19 | 20% |
| At maximum threshold — fortieth ticket | 39 | 40% |
| Above maximum — discount capped | 45 | 40% |

**Open questions:**
- Is "last 30 days" a rolling window (e.g. exactly 720 hours), or calendar days? A ticket bought 30 days ago at 09:00 — does it count when buying at 10:00 today?
- "Trips in last 30 days" — does the current purchase count toward the **next** ticket's discount immediately after checkout, or only once travel is completed?

---

### Table 2 — Discount by Passenger Type

| Scenario | Passenger type | Trips in last 30 days | Discount? |
|---|---|---|---|
| Adult below threshold | Adult | 3 | 0% |
| Adult at first tier | Adult | 4 | 5% |
| Senior follows same ladder as adult | Senior | 4 | 5% |
| Adult and senior treated identically | {Adult, Senior} | 9 | 10% |
| Child — flat rate, no trips needed | Child | 0 | 20% |
| Child — flat rate regardless of trip count | Child | {4, 20, 39} | 20% |
| Adult at max discount, any zone | Adult | 39 | 40% |

**Note on zones:** The spec states the discount applies "regardless of the zones you travel in." Zones are not an input to the discount calculation; a zone column would hold any value in every row.

**Open questions:**
- Are there other passenger types (student, infant)? If so, do they follow the Reis ladder, a flat rate, or no discount at all?
- What defines "child" — an age range, a ticket category selected at purchase, or something else? Is there an upper age boundary where a child becomes an adult for discount purposes?

---

### What is **not** in these tables

Final price arithmetic (`base_price × (1 - discount%)`) is straightforward and does not need a table. A couple of representative assertions in implementation are sufficient once the discount percentage is verified by the tables above.