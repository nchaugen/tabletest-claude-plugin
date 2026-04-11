Looking at this feature, there are three distinct concerns to pin down: who gets which discount type, how the Reis discount ladder works, and how the discount adjusts dynamically over time. Let me build each table in turn.

---

## Table 1: Discount Type by Passenger Category

| Scenario | Passenger Type | Ticket Type | Discount Type? | Notes? |
|---|---|---|---|---|
| Adult single ticket | Adult | Single | Reis (variable) | |
| Senior single ticket | Senior | Single | Reis (variable) | |
| Child single ticket | Child | Single | Flat 20% | |
| Adult period ticket | Adult | Period | None | Reis only applies to single tickets |
| Child period ticket | Child | Period | None | Flat 20% is single tickets only |

---

## Table 2: Reis Discount Ladder

The ladder is based on single tickets purchased in the rolling last-30-days window.

| Scenario | Trips in Last 30 Days | Discount %? | Notes? |
|---|---|---|---|
| No purchase history | 0 | 0% | |
| Building up, before first threshold | {1, 2, 3, 4} | 0% | |
| First discount threshold reached | 5 | ?% | **Open: what % is tier 1?** |
| Between tier 1 and tier 2 | {6, 7, 8, 9} | ?% | Same as tier 1 |
| Tier 2 threshold | 10 | ?% | **Open: what % is tier 2?** |
| Tier 3 threshold | 15 | ?% | **Open: what % is tier 3?** |
| Maximum discount threshold | 20 | 40% | |
| Beyond maximum | 25 | 40% | Capped — does not exceed 40% |
| Just below maximum | 19 | ?% | Same as tier 3 |

**Open question:** The spec states "every fifth trip moves you one step higher" and the max is 40%, implying 4 tiers (at 5, 10, 15, 20 trips). What are the percentages for tiers 1–3? Natural candidates: 10 / 20 / 30 / 40%, but this needs confirmation.

---

## Table 3: Discount Applied to Price

Once the tier is known, verify the calculation is applied correctly. (Keeps arithmetic separate from the ladder rules above.)

| Scenario | Passenger Type | Standard Price | Reis Discount | Price Paid? |
|---|---|---|---|---|
| No discount | Adult | 50 | 0% | 50 |
| 40% maximum discount | Adult | 50 | 40% | 30 |
| Child flat discount | Child | 50 | 20% | 40 |

---

## Table 4: Discount Dynamics (Rolling 30-Day Window)

This specifies how the discount moves after the first 30 days.

| Scenario | Days Active | Trips in Last 30 Days | Trips in Prior 30 Days | Discount Direction? |
|---|---|---|---|---|
| Stable travel pattern | > 30 | 10 | 10 | No change |
| Travelling less than before | > 30 | 5 | 10 | Decrease |
| Travelling more than before | > 30 | 15 | 10 | Increase |
| Already at maximum, same pattern | > 30 | 20 | 20 | No change (stays at 40%) |
| Already at maximum, travel more | > 30 | 25 | 20 | No change (capped at 40%) |
| Drop below a tier threshold | > 30 | 4 | 5 | Decrease to 0% |

**Open question:** How quickly does the discount decrease — is it immediate (next purchase reflects new count) or is there a lag? The spec says "may decrease" which suggests it could be gradual, but the mechanism is not specified.

---

## Summary of Open Questions

1. What are the exact discount percentages for tiers 1, 2, and 3 (the steps before 40%)?
2. Does the discount recalculate on every purchase, or at some interval?
3. Is "tickets in last 30 days" measured at time of purchase, or at midnight each day?
4. Are zone and route irrelevant to the Reis discount (the spec implies yes — worth confirming)?
5. Does the child flat discount stack with or replace any other promotions?

Once the tier percentages are confirmed, Table 2 fills in and these tables hand directly off to `/tabletest`.