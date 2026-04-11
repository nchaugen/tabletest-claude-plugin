Great use case for this. I can see two distinct concerns here — let's tackle them separately.

---

## Table 1: Registration Validation

| Scenario | Name | Email | Dietary Requirements | Accessibility Needs | Accepted? | Rejection Reason? |
|---|---|---|---|---|---|---|
| Typical registration | Alice Smith | alice@example.com | vegetarian | | yes | |
| All fields provided | Bob Jones | bob@events.org | vegan | wheelchair access | yes | |
| Missing name | | alice@example.com | | | no | Name is required |
| Blank name | (empty) | alice@example.com | | | no | Name is required |
| Missing email | Alice Smith | | | | no | Email is required |
| Invalid email format | Alice Smith | not-an-email | | | no | Invalid email format |
| Email missing domain | Alice Smith | alice@ | | | no | Invalid email format |
| Optional fields absent | Alice Smith | alice@example.com | | | yes | |

**Open questions:**
- Is "alice@example" (no TLD) a valid email? Most validators reject it — worth confirming.
- Are dietary/accessibility fields truly free-text, or from a fixed list? (affects validation)

---

## Table 2: Discount Calculation

This is where the stacking question lives. I've marked the uncertain cell with `?`.

| Scenario | Registration Date | Group Size | Early Bird Cutoff (Policy) | Min Group Size (Policy) | Discount? | Notes? |
|---|---|---|---|---|---|---|
| Standard registration | after cutoff | 1 | 2026-06-01 | 5 | 0% | |
| Early bird, solo | before cutoff | 1 | 2026-06-01 | 5 | 20% | |
| Group, after cutoff | after cutoff | 5 | 2026-06-01 | 5 | 15% | |
| Group of exactly 5 | after cutoff | 5 | 2026-06-01 | 5 | 15% | boundary |
| Group of 4, just under | after cutoff | 4 | 2026-06-01 | 5 | 0% | |
| On the cutoff date | on cutoff | 1 | 2026-06-01 | 5 | ? | Is cutoff day early-bird or not? |
| Early bird + group | before cutoff | 5 | 2026-06-01 | 5 | ? | **Do discounts stack?** |
| Early bird + large group | before cutoff | 20 | 2026-06-01 | 5 | ? | Same stacking question |

**The key open questions to resolve:**

1. **Do early-bird and group discounts stack?** The two natural answers are:
   - **Yes, additive:** 20% + 15% = 35% off
   - **Yes, multiplicative:** 20% off first, then 15% off that → 32% off (i.e., pay 80% × 85%)
   - **No, take the better one:** max(20%, 15%) = 20%
   - **No, they're mutually exclusive** for some policy reason

2. **Is the cutoff date inclusive?** Does registering *on* 2026-06-01 count as early-bird?

Once you answer those, I can fill in the `?` cells and the table becomes a complete spec ready to hand to `/tabletest`. Which of those stacking options feels right for your domain?