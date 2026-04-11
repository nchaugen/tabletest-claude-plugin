Looking at this feature, there are two distinct concerns worth separating: **who gets which type of discount** (applicability), and **how the Reis tier is determined** (the discount ladder). I'll produce one table for each.

---

## Table 1: Reis Discount Tier

The core rule: discount is looked up from the number of single tickets purchased in the past 30 days, measured at time of purchase.

| Scenario | Tickets purchased in past 30 days | Discount? |
|---|---|---|
| First trip ever, no history | 0 | 0% |
| Building up, just below first threshold | 3 | 0% |
| At first threshold — 5th trip | 4 | 5% |
| Just below second threshold | 8 | 5% |
| At second threshold — 10th trip | 9 | 10% |
| At third threshold — 15th trip | 14 | 15% |
| At fourth threshold — 20th trip | 19 | 20% |
| At fifth threshold — 25th trip | 24 | 25% |
| At sixth threshold — 30th trip | 29 | 30% |
| At seventh threshold — 35th trip | 34 | 35% |
| At maximum threshold — 40th trip | 39 | 40% |
| Heavy traveler, above maximum | 55 | 40% |
| Frequent travel drops to just below a threshold | 8 | 5% |

---

## Table 2: Discount Applicability by Passenger Type and Ticket Type

| Scenario | Passenger type | Ticket type | Zones | Discount type? | Discount? |
|---|---|---|---|---|---|
| Adult single ticket, any zone | Adult | Single | {1-zone, 2-zone, 3-zone} | Reis tier-based | per tier |
| Senior single ticket, any zone | Senior | Single | {1-zone, 2-zone, 3-zone} | Reis tier-based | per tier |
| Child single ticket, any zone | Child | Single | {1-zone, 2-zone, 3-zone} | Flat | 20% |
| Adult period ticket | Adult | Period | {1-zone, 2-zone, 3-zone} | None | 0% |
| Senior period ticket | Senior | Period | {1-zone, 2-zone, 3-zone} | None | 0% |
| Child period ticket | Child | Period | {1-zone, 2-zone, 3-zone} | None | 0% |

The zone column uses value sets throughout — the spec explicitly states the Reis discount is zone-independent, so this documents that zones are irrelevant to discount calculation rather than leaving it ambiguous.

---

## Open Questions

A few things the spec leaves unresolved that will need to be pinned down before implementation:

1. **Age thresholds**: What ages define "child", "adult", and "senior"? The spec uses the terms but gives no boundaries. This likely needs its own table once the age rules are known.

2. **Ticket counting scope**: Do only tickets purchased in-app count toward the 30-day history, or do tickets purchased through other channels (e.g., ticket machines) also contribute?

3. **Discount applied to which base price?**: The spec says "off the price of a standard single ticket" — is this the full undiscounted price, or the zone-adjusted price? For children, is the 20% also off the standard single price?

4. **Tier boundary semantics**: The spec says "every fifth trip will take you 5% higher." The table above treats the threshold as 4 completed tickets → 5% on the 5th purchase. If the intent is that you need 5 *completed* trips in 30 days before the discount applies, all thresholds shift by 1. Confirm which interpretation is correct.

Once the age thresholds are resolved, a third table for **passenger type classification** (age → passenger category) would complete the spec.