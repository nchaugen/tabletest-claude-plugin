Two concerns here: the tier lookup for adults/seniors (complex rules), and the flat child discount (simple, separate rule). I'll produce a table for each.

---

## Reis Discount Tier — Adult and Senior

The discount percentage for each purchase is determined by how many single tickets the passenger has bought in the 30 days before this purchase.

| Scenario | Passenger type | Prior single tickets (last 30 days) | Zone | Discount? |
|---|---|---|---|---|
| First trip ever | {Adult, Senior} | 0 | {1-zone, 2-zone, all-zones} | 0% |
| Still below first threshold | {Adult, Senior} | 3 | 1-zone | 0% |
| At first discount threshold (buying trip 5) | {Adult, Senior} | 4 | 1-zone | 5% |
| Within first tier | {Adult, Senior} | 6 | 1-zone | 5% |
| Top of first tier (buying trip 9) | {Adult, Senior} | 8 | 1-zone | 5% |
| At second tier (buying trip 10) | {Adult, Senior} | 9 | 1-zone | 10% |
| At third tier (buying trip 15) | {Adult, Senior} | 14 | 1-zone | 15% |
| Mid ladder (buying trip 20) | {Adult, Senior} | 19 | 1-zone | 20% |
| Just below maximum (buying trip 39) | {Adult, Senior} | 38 | 1-zone | 35% |
| At maximum (buying trip 40) | {Adult, Senior} | 39 | 1-zone | 40% |
| Above maximum — cap applies | {Adult, Senior} | 50 | 1-zone | 40% |
| Discount drops as old trips fall out of window | {Adult, Senior} | 4 | 1-zone | 5% |

**Open questions:**
- Does "prior single tickets" count only Reis single tickets, or all single tickets on the account?
- What if a trip was refunded — does it still count toward the 30-day total?
- Is trip 4 = 0% and trip 5 = 5%, or is the first discount on the 4th trip (i.e. does the count include the current purchase)? The spec says "first discount applies to ticket number five" — the table above assumes prior count = 4 (you are buying your 5th) gives 5%.

---

## Child Single Ticket Discount

| Scenario | Passenger type | Prior single tickets (last 30 days) | Discount? |
|---|---|---|---|
| Child — flat discount regardless of trip history | Child | {0, 4, 5, 40} | 20% |

**Open questions:**
- Does the child discount stack with any other promotional discount, or does the flat 20% always apply in isolation?
- Is "child" defined by a specific age range (e.g. under 18)? The spec doesn't state a threshold.

---

The tier rule for adults/seniors produces the interesting boundary cases — particularly the boundary at trip 4→5 (first discount), each step threshold, and the cap at 40. The zone column is included in the first row with a value set to document that zones have no effect on the discount percentage.