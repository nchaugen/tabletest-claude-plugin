Now I have a clear picture. The requirements are mostly clear but have boundary ambiguities worth pinning down. Here are the spec-by-example tables:

---

## Reis Discount Tier

| Scenario | Trips in last 30 days | Discount? |
|---|---|---|
| No travel yet | 0 | 0% |
| Just below discount threshold | 4 | 0% |
| At discount threshold | 5 | 5% |
| Mid-tier, no step up | 9 | 5% |
| At second tier boundary | 10 | 10% |
| At third tier boundary | 15 | 15% |
| At fourth tier boundary | 20 | 20% |
| At fifth tier boundary | 25 | 25% |
| At sixth tier boundary | 30 | 30% |
| At seventh tier boundary | 35 | 35% |
| At maximum discount | 40 | 40% |
| Above maximum discount | 50 | 40% |

---

## Discount by Passenger Type

| Scenario | Passenger Type | Trips in last 30 days | Discount? |
|---|---|---|---|
| Child flat discount, regardless of travel history | Child | {0, 5, 40} | 20% |
| Adult below threshold | Adult | 4 | 0% |
| Senior below threshold | Senior | 4 | 0% |
| Adult at threshold | Adult | 5 | 5% |
| Senior gets same Reis tiers as adult | Senior | 5 | 5% |
| Adult at maximum | Adult | 40 | 40% |
| Senior at maximum | Senior | 40 | 40% |

---

**Open question**: The description says "first discount applies to ticket number five." Does this mean the *current purchase* is counted in the 30-day window before the discount is applied — so buying your 5th ticket gives you 5 trips and 5% off that purchase — or is the purchase counted *after* and you need 5 prior trips? The table above assumes the purchase being made is included in the count (buying ticket 5 → 5 trips → 5% off). Confirm this before implementation.

---

Once that's confirmed, this maps directly to `@TableTest`. Two methods, one assertion each:

```java
@TableTest("""
    Scenario                        | Trips in last 30 days | Discount?
    No travel yet                   | 0                     | 0
    Just below discount threshold   | 4                     | 0
    At discount threshold           | 5                     | 5
    Mid-tier, no step up            | 9                     | 5
    At second tier boundary         | 10                    | 10
    At third tier boundary          | 15                    | 15
    At fourth tier boundary         | 20                    | 20
    At fifth tier boundary          | 25                    | 25
    At sixth tier boundary          | 30                    | 30
    At seventh tier boundary        | 35                    | 35
    At maximum discount             | 40                    | 40
    Above maximum discount          | 50                    | 40
    """)
void reisDiscountTier(int tripsInLast30Days, int discountPercent) {
    assertEquals(discountPercent, ReisPricing.discountTier(tripsInLast30Days));
}

@TableTest("""
    Scenario                                        | Passenger Type | Trips in last 30 days | Discount?
    Child flat discount regardless of travel history | CHILD          | {0, 5, 40}            | 20
    Adult below threshold                           | ADULT          | 4                     | 0
    Senior below threshold                          | SENIOR         | 4                     | 0
    Adult at threshold                              | ADULT          | 5                     | 5
    Senior gets same Reis tiers as adult            | SENIOR         | 5                     | 5
    Adult at maximum                                | ADULT          | 40                    | 40
    Senior at maximum                               | SENIOR         | 40                    | 40
    """)
void discountByPassengerType(PassengerType passengerType, int tripsInLast30Days, int discountPercent) {
    assertEquals(discountPercent, ReisPricing.discountFor(passengerType, tripsInLast30Days));
}
```

The class and method names (`ReisPricing.discountTier`, `ReisPricing.discountFor`) are placeholders — adjust to match your actual implementation. If `PassengerType` is a custom enum, you'll need a type converter registered for it (see the tabletest skill's `references/type-converters.md` for the pattern).