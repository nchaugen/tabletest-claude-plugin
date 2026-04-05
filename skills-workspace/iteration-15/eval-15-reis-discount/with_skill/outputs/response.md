Looking at this feature, there are multiple interlocking rules to pin down with concrete examples before writing tests. Let me work through two tables — one for the discount tier ladder (the core rule), and one for which passenger types qualify for which discount.

---

## Table 1: Reis Discount Tier

The key question is how the rolling 30-day trip count maps to a discount percentage. The spec says "every fifth trip takes you 5% higher", "first discount applies to ticket number five", and "maximum discount is 40%".

The count includes the ticket being purchased now (since ticket 5 is where the first 5% applies).

| Scenario | Trips in last 30 days (incl. current) | Discount? |
|---|---|---|
| Very first trip | 1 | 0% |
| Still accumulating | 2 | 0% |
| Still accumulating | 3 | 0% |
| Last trip before first discount | 4 | 0% |
| First discount kicks in | 5 | 5% |
| Mid first tier | 7 | 5% |
| Last trip in first tier | 9 | 5% |
| Second tier | 10 | 10% |
| Third tier | 15 | 15% |
| Fourth tier | 20 | 20% |
| Fifth tier | 25 | 25% |
| Sixth tier | 30 | 30% |
| Seventh tier | 35 | 35% |
| Maximum reached | 40 | 40% |
| Above maximum | 45 | 40% |

**Open questions:**
- Is the current ticket counted in the 30-day window, or only prior tickets? (I've assumed inclusive — ticket 5 triggers 5% — but this needs confirmation)
- Ticket 8: does it get 5% or 0%? The "every fifth trip" wording could mean trips 5, 10, 15... are milestones, with the tier persisting until the next milestone.

---

## Table 2: Discount Type by Passenger Category

The spec gives two distinct discount mechanisms. This table pins down which applies to whom.

| Scenario | Passenger type | Ticket type | Discount type? | Discount? |
|---|---|---|---|---|
| Adult, first trip | Adult | Single | Reis (rolling) | 0% |
| Adult, 5 trips this month | Adult | Single | Reis (rolling) | 5% |
| Adult, 20 trips this month | Adult | Single | Reis (rolling) | 20% |
| Senior, first trip | Senior | Single | Reis (rolling) | 0% |
| Senior, 10 trips this month | Senior | Single | Reis (rolling) | 10% |
| Child, regardless of trip count | Child | Single | Flat | 20% |
| Child with high travel history | Child | Single | Flat | 20% |

**Notes:**
- Zone does not affect discount eligibility (`{Zone 1, Zone 2, Zone 3, ...}` — the discount applies regardless)
- Only single tickets qualify; period ticket holders are outside the scope of this feature

---

## Table 3: 30-Day Window Drop-Off

When old trips fall out of the 30-day window, the discount may decrease. This table covers the decay rule.

| Scenario | Trips in last 30 days (excl. current) | Trips older than 30 days (no longer counted) | Effective count (incl. current)? | Discount? |
|---|---|---|---|---|
| Steady traveller, 10/month | 9 | 0 | 10 | 10% |
| Trips fell off window, still tier 2 | 9 | 5 | 10 | 10% |
| Trips fell off, now below a threshold | 4 | 6 | 5 | 5% |
| Heavy use followed by drop-off | 1 | 39 | 2 | 0% |
| Consistent at maximum tier | 39 | many | 40 | 40% |

**Open questions:**
- Is the 30-day window exactly 30 × 24h rolling, or calendar days?
- When a batch of old trips falls out on the same day, can the discount drop multiple tiers at once? (The spec says it "may decrease" — confirming gradual vs step drops matters for UX)

---

Ready to hand these to `/tabletest` to produce the `@TableTest` code once the open questions are confirmed. The tier table maps cleanly to a single method; the passenger type table and the drop-off table each become separate test methods.