# Discount Interaction — Spec by Example

This is exactly the right moment to stop and lay out examples rather than picking an implementation. The table will make the conflict visible and give the team something concrete to decide on.

## What We Know So Far

There are two discount types that can apply to the same customer:
- **Bulk discount** — based on order quantity
- **Loyalty discount** — based on customer loyalty status

The unresolved question is: when both apply, what is the final discount?

## The Example Table

Here is the table with what we can fill in confidently, and honest `?` markers for the cases that depend on the unresolved decision:

| Scenario                                      | Bulk Discount | Loyalty Discount | Final Discount? | Open Questions                              |
|-----------------------------------------------|---------------|------------------|-----------------|---------------------------------------------|
| No discounts apply                            | 0%            | 0%               | 0%              |                                             |
| Bulk discount only                            | 20%           | 0%               | 20%             |                                             |
| Loyalty discount only                         | 0%            | 15%              | 15%             |                                             |
| Both apply — stacked                          | 20%           | 15%              | ?               | Stacked = 35%? Or compounded = 32%?         |
| Both apply — higher only                      | 20%           | 15%              | ?               | Higher only = 20%?                          |
| Both apply — capped                           | 20%           | 15%              | ?               | What is the cap? 25%? 30%?                  |
| Both apply, loyalty is higher                 | 10%           | 15%              | ?               | Higher only = 15%? Or still stacked?        |
| Both apply, equal discounts                   | 15%           | 15%              | ?               | Does tie-breaking matter?                   |
| Cap reached by single discount                | 30%           | 15%              | ?               | Does the cap apply per-discount or overall? |

## The Open Decision

The rows marked `?` all depend on **one unresolved policy decision** that the team needs to make:

> **How are bulk and loyalty discounts combined when both apply?**

The three candidate rules are:

**Option A — Stack (additive)**
Final discount = bulk + loyalty (possibly compounded).
Simple rule, maximum customer benefit. Risk: large orders by loyal customers get very high discounts.

**Option B — Higher wins**
Final discount = max(bulk, loyalty).
Conservative rule. But: does it feel fair to customers who earn both? And what if they're equal?

**Option C — Capped combination**
Final discount = min(bulk + loyalty, cap).
Middle ground. But: what is the cap value? Who sets it? Is it configurable?

## Filling In After the Decision

Once the team picks an option, the `?` cells can be filled in. For example, if Option A (stacked, additive) is chosen:

| Scenario                          | Bulk Discount | Loyalty Discount | Final Discount? |
|-----------------------------------|---------------|------------------|-----------------|
| Both apply — stacked              | 20%           | 15%              | 35%             |
| Both apply, loyalty is higher     | 10%           | 15%              | 25%             |
| Both apply, equal discounts       | 15%           | 15%              | 30%             |

If Option C (capped at 25%) is chosen:

| Scenario                          | Bulk Discount | Loyalty Discount | Cap (Policy) | Final Discount? |
|-----------------------------------|---------------|------------------|--------------|-----------------|
| Both apply, under cap             | 5%            | 10%              | 25%          | 15%             |
| Both apply, at cap                | 15%           | 10%              | 25%          | 25%             |
| Both apply, would exceed cap      | 20%           | 15%              | 25%          | 25%             |
| Single discount exceeds cap       | 30%           | 0%               | 25%          | ?               |

Note: Option C introduces a new open question — does the cap apply to the combined discount, or to each discount individually? That question is now visible in the table.

## Recommendation for the Team Meeting

Bring the first table to the team. Point at the `?` rows. Ask:

1. "What should the final discount be when bulk = 20% and loyalty = 15%?"
2. "What if loyalty = 10% and bulk = 20% — does the answer change?"
3. "Is there a number where the combined discount feels too high?"

The answers will tell you which option fits the business intent.
