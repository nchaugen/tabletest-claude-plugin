# Experiment 1: Agent Table Generation — Eval Design

**Date:** 2026-03-22
**Status:** Draft — rubric and assertions defined, requirements drafted, skills not yet updated
**Prerequisite:** Experiment 0 passed (with scope qualification)

## What changed from the original design

The original experiment design (in `docs/superpowers/specs/`) defined three separate rubrics:
- Rubric A — Spec readability (1–5)
- Rubric B — Test quality (1–5)
- Rubric C — Table structure (1–5)

Experiment 0 showed that good spec tables and good test tables share most quality criteria. Concern separation, boundary condition depth, scenario naming, column naming — these all serve both goals simultaneously. The few genuine tensions (exhaustive coverage vs representative examples, human-readable values vs type safety) are resolved by specific techniques (value sets, TypeConverters), not by trading off one dimension against another.

**The updated rubric replaces three subjective scores with concrete assertions grouped by quality dimension.** This is more reproducible, matches our existing eval framework, and reflects the convergence finding.

## Rubric: Unified Table Quality

Five dimensions, each with specific assertions. Each assertion is pass/fail with evidence.

### Dimension 1 — Decomposition

Does the table structure reflect the domain?

| ID | Assertion | What to check |
|---|---|---|
| D1 | Concern separation | Each table focuses on one rule/concern. Mixed concerns are split into separate tables. |
| D2 | Rules not arithmetic | Tables test domain decisions (classification, eligibility, validation), not multiplication or string concatenation. |
| D3 | State as rules | Stateful features use state × action → new state rows, not sequential paths. Integration journeys in `@Test` methods. |
| D4 | Appropriate table count | Neither too many (one scenario per table) nor too few (everything crammed into one table). |

### Dimension 2 — Depth

Does the table specify enough for implementation?

| ID | Assertion | What to check |
|---|---|---|
| E1 | Boundary conditions | Threshold values tested (e.g., 39/40/41 for overtime). Not just representative values from the middle of each range. |
| E2 | Edge cases | Error conditions, empty inputs, null/missing values covered where relevant. |
| E3 | Ambiguities surfaced | Domain questions flagged as open (`?`) or noted, not silently resolved with an assumed answer. |
| E4 | Coverage completeness | All rules stated in the requirement have corresponding table rows. No rule is mentioned but untested. |

### Dimension 3 — Readability

Can a non-developer understand the table?

| ID | Assertion | What to check |
|---|---|---|
| R1 | Scenario names | Business language describing the situation, not the implementation or expected outcome. |
| R2 | Column names | User-facing vocabulary ("Message?" not "Result?", "Coupon code entered" not "couponInput"). |
| R3 | Empty cells | Optional/irrelevant inputs are blank, not filled with zeroes or defaults. Parameter types accommodate null. |
| R4 | Value representation | Complex values use readable notation. TypeConverters for human-friendly values (yes/no, domain enums). |
| R5 | Traceability columns | Intermediate expected values included where they help the reader trace the logic (e.g., "Discount?" in a pricing table). |

### Dimension 4 — Correctness

Is the table actually right?

| ID | Assertion | What to check |
|---|---|---|
| C1 | Expected values correct | Arithmetic in expected columns is verified. Every row's expected output matches the stated rules. |
| C2 | Value set semantics | Value sets only used where every value in the set produces the same expected result. |
| C3 | Cross-table consistency | Values that appear in multiple tables (e.g., payable hours calculated in one table, used as input in another) are consistent. |

### Dimension 5 — Format

Correct use of TableTest features?

| ID | Assertion | What to check |
|---|---|---|
| F1 | @DisplayName present | Test methods have display names that work as section headers in reports. |
| F2 | @Description present | Test class or methods have descriptions that provide context beyond what the table shows. |
| F3 | TypeConverterSources | Used where needed for human-readable values (yes/no, domain-friendly formats). |
| F4 | Valid syntax | Table parses correctly (aligned columns, no multi-line rows, correct quoting for whitespace). |

### Scoring

- **Per-feature score**: assertions passed / total assertions for that feature
- **Per-dimension score**: assertions passed in that dimension, across all features
- **Overall score**: total passed / total assertions
- **Pass threshold**: overall ≥ 80%, no single dimension below 60%, no single feature below 60%

---

## Natural-Language Requirements

Written as a product person would write them in a ticket. These are the prompts given to the agent.

### Feature 1: Weekly Pay Calculation

> **Calculate weekly pay for hourly employees**
>
> We need to calculate weekly pay based on hours worked. The rules:
> - Weekday hours up to 40 are paid at the base hourly rate
> - Weekday hours beyond 40 are overtime, paid at 1.5× the base rate (time-and-a-half)
> - Sunday hours are always paid at 2× the base rate (double time)
> - Holiday hours are always paid at 2× the base rate (double time)
> - Total pay cannot go below zero
> - A negative hourly rate is not allowed (should be rejected)
>
> The employee submits their hours (weekday, Sunday, holiday) and their hourly rate. The system calculates their weekly pay.

### Feature 2: Reis Single Ticket Discount

> **Implement the Reis loyalty discount for single tickets**
>
> We're launching a loyalty programme called Reis for single ticket travellers. Frequent travellers earn progressive discounts based on their purchase history in a rolling 30-day window.
>
> Discount tiers (based on single tickets purchased in the last 30 days):
> - 1–4 tickets: 0%
> - 5–9 tickets: 5%
> - 10–14: 10%
> - 15–19: 15%
> - 20–24: 20%
> - 25–29: 25%
> - 30–34: 30%
> - 35–39: 35%
> - 40+: 40% (maximum)
>
> Zone pricing (adult, full price): 1 zone = 46 kr, 2 zones = 75, 3 = 105, 4 = 134, 5 = 162.
>
> Eligibility:
> - Adults and pensioners: full Reis discount ladder
> - Children: flat 20% discount (no ladder — always 20% regardless of ticket count)
> - Youth and students: no discount — they purchase adult tickets at full price
>
> The 30-day rolling window counts all ticket purchases regardless of zone or traveller category. Only tickets with a purchase timestamp within the last 30 days are counted.
>
> Ticket price = zone base price × (1 − discount%).

### Feature 3: Order Splitting

> **Split orders into multiple shipments when needed**
>
> An order must be split into multiple shipments when:
> 1. Items have mixed fulfilment methods (some for store pickup, some for home delivery)
> 2. Items have mixed stock status (some in-stock, some pre-order)
> 3. No single warehouse has all items in stock
> 4. Items are going to different delivery addresses
>
> When splitting is required, minimise the number of shipments — prefer fewer shipments with more items each.
>
> Companion products (e.g., a camera body and its matching lens) should ship together from the same location.

### Feature 4: Shopping Cart

> **Build a shopping cart with coupons and checkout**
>
> The cart supports:
> - **Add item**: specify product and quantity. Price is looked up from the product catalogue.
> - **Remove item**: removes all units of that product from the cart.
> - **Apply coupon**: enter a coupon code. If valid, the discount is applied. If expired or unrecognised, show an error message. Only one coupon can be active at a time — applying a new one replaces the previous.
> - **Checkout**: fails if any item has insufficient stock, or if the cart is empty.
>
> Coupon types: percentage off the total, fixed amount off the total, or a discount on a specific product.
>
> Cart total = sum of (quantity × price) for all items, minus coupon discount, floored at zero.
>
> The system should return user-facing messages for all operations (e.g., "Added to cart", "Coupon has expired", "Only 3 available").

---

## Per-Feature Assertions

Derived from the rubric dimensions and the experiment 0 refined tables. Each assertion has an ID referencing its rubric dimension (e.g., D1 = Decomposition assertion 1).

### Feature 1: Weekly Pay

| # | Dim | Assertion | Detail |
|---|---|---|---|
| 1.1 | D1 | Hour classification separated from pay calculation | Tables decompose into: (a) how hours convert to payable hours, and (b) payable hours × rate = pay. Not one big table with all inputs and final pay. |
| 1.2 | D2 | Tables focus on the conversion rules | The interesting rule is hour classification (regular, overtime, Sunday, holiday → payable hours). Pay = payable hours × rate is arithmetic, not a rule. Tables should emphasise the former. |
| 1.3 | E1 | Overtime threshold boundary covered | At least values at the threshold (40) and just above (41). Ideally also just below (39) or a value well below (e.g., 24). |
| 1.4 | E1 | Combined scenario present | A row with weekday + Sunday + holiday hours, testing the composition of all hour types. |
| 1.5 | E2 | Error/edge cases covered | Negative hours (floored at zero), negative rate (rejected), missing rate (rejected). |
| 1.6 | R3 | Sunday and Holiday columns use empty cells | When Sunday or Holiday hours are not relevant to a scenario, the cell is blank (not 0). Parameter type is `Integer` (not `int`) to support null. |
| 1.7 | R1 | Scenario names describe work patterns | E.g., "Part-time", "Five hours overtime", "Full Sunday shift" — not "Test case 1" or "40 hours 0 Sunday". |
| 1.8 | C1 | Expected payable hours are correct | Every row's expected payable hours matches: regular + overtime×1.5 + sunday×2 + holiday×2. Verified independently. |
| 1.9 | C2 | Value sets used correctly | Value sets only where all values produce the same result (e.g., any rate × 0 hours = 0). Not used where results differ. |
| 1.10 | F1 | @DisplayName present on test methods | |
| 1.11 | F2 | @Description present | At least on the payable hours method, explaining the conversion rules. |

### Feature 2: Reis Discount

| # | Dim | Assertion | Detail |
|---|---|---|---|
| 2.1 | D1 | Discount ladder separated from eligibility and pricing | At least three distinct tables: tier determination, traveller eligibility, and ticket pricing. Rolling window counting as a fourth is desirable. |
| 2.2 | D2 | Tables focus on tier rules and eligibility | Not on price arithmetic (zone_price × discount). The pricing table tests the end-to-end pipeline, not multiplication. |
| 2.3 | E1 | Every tier boundary covered | Not just representative mid-range values. Each tier (0%, 5%, 10%, …, 40%) has at least one row, ideally with boundary values (4→5, 9→10, etc.). Value sets are the natural mechanism. |
| 2.4 | E1 | Rolling window boundary specified | The 30-day boundary is tested: a ticket at exactly 30 days is included, at 31 days is excluded. |
| 2.5 | E4 | Youth/student domain question addressed | Explicitly shows that youth and students pay adult price with no discount. Not left ambiguous. |
| 2.6 | R4 | Eligibility uses human-readable values | yes/no (via TypeConverter) or equivalent, not true/false. |
| 2.7 | R5 | Pricing table includes discount for traceability | A "Discount?" column so the reader can trace: traveller + tickets → discount tier → price. |
| 2.8 | C1 | Ticket prices are correct | zone_price × (1 − discount/100) verified for every pricing row. |
| 2.9 | C2 | Value sets have correct tier semantics | Each value set contains exactly the values that produce the same discount. No cross-tier contamination. |
| 2.10 | F1 | @DisplayName present on test methods | |
| 2.11 | F2 | @Description present | At least on the class or the tier method, explaining the discount ladder structure. |
| 2.12 | F3 | TypeConverterSources used | For at least one human-readable conversion (yes/no, or rolling window notation). |

### Feature 3: Order Splitting

| # | Dim | Assertion | Detail |
|---|---|---|---|
| 3.1 | D1 | Trigger detection separated from optimisation and companions | At least three tables: triggers, shipment optimisation, companion constraint. Not one monolithic table. |
| 3.2 | D1 | Trigger interactions tested | At least one row where multiple triggers fire simultaneously (e.g., mixed fulfilment AND mixed stock). |
| 3.3 | E4 | All four triggers covered | Mixed fulfilment, mixed stock, no single warehouse has all items, different addresses. Each has at least one row. |
| 3.4 | E3 | Companion ambiguity surfaced | The "no warehouse has both companions" case is flagged as an open question / product decision, not silently resolved as either "error" or "ship separately". The requirement does NOT hint at this — the agent must recognise the ambiguity unprompted. |
| 3.5 | E2 | Companion edge cases | At least: companions at same location, companions split from non-companion items, and the ambiguous case (no location has both). |
| 3.6 | R1 | Optimisation scenarios describe the business situation | E.g., "Consolidate: two beats three" or "Everything at one warehouse" — not "Test optimisation algorithm case 2". |
| 3.7 | R4 | Set notation for triggers | Closed enumerations use set notation ({delivery, pickup}) rather than prose or lists. |
| 3.8 | R4 | Scalar column for quantity | Optimisation table uses a scalar count ("Shirts ordered") not item lists, when product identity doesn't matter. |
| 3.9 | C1 | Inventory assignments are valid | Items are only assigned to locations that have them. Shipment counts match assignments. |
| 3.10 | F1 | @DisplayName present on test methods | |
| 3.11 | F2 | @Description present | At least on the class, explaining the splitting rules and constraints. |

### Feature 4: Shopping Cart

| # | Dim | Assertion | Detail |
|---|---|---|---|
| 4.1 | D1 | Item operations separated from coupon validation | Add/remove in one table, coupon application in another. Not interleaved. |
| 4.2 | D1 | Cart total separated from checkout | Total calculation (pure function) and checkout validation (stock check) in separate tables. |
| 4.3 | D3 | State as rules, not paths | Item and coupon tables use state × action → new state rows. Each row is independently executable. No row depends on a prior row's result. |
| 4.4 | E4 | Item operations covered | Add to empty, add different product, add more of same, remove, remove unknown product, remove last item. |
| 4.5 | E4 | Coupon variations covered | Valid percentage, valid fixed amount, product-specific, expired, unknown code, replace existing coupon. |
| 4.6 | E2 | Coupon exceeds total | A scenario where the coupon discount exceeds the cart total — total floors at zero. |
| 4.7 | E2 | Checkout edge cases | Sufficient stock, insufficient stock, empty cart. |
| 4.8 | R2 | User-facing column names | "Message?" (not "Result?"), "Coupon code entered" (not "coupon"), "Cart after?" (not "expectedState"). |
| 4.9 | R2 | System knowledge explicit | Coupon rules defined in a visible column ("Valid coupons") rather than hardcoded in test setup. |
| 4.10 | R2 | Catalogue separated from cart | Product prices come from an explicit catalogue column or setup, not embedded in action strings. |
| 4.11 | C1 | Cart totals correct | sum(quantity × price) − coupon = total, verified for every row. |
| 4.12 | C3 | State transitions consistent | "Cart after" in one row could serve as "Cart before" in another. Values are consistent if cross-referenced. |
| 4.13 | F1 | @DisplayName present on test methods | |
| 4.14 | F2 | @Description present | At least on the class, explaining the cart model. |

---

## Assertion Summary

| Feature | Assertions | Key focus |
|---|---|---|
| 1. Weekly Pay | 11 | Decomposition (hour classification vs arithmetic), correctness (payable hours), empty cells |
| 2. Reis Discount | 12 | Boundary depth (every tier), value set semantics, traceability columns |
| 3. Order Splitting | 11 | Ambiguity surfacing (companion constraint), trigger interactions, readable notation |
| 4. Shopping Cart | 14 | State as rules (not paths), user-facing naming, coupon depth |
| **Total** | **48** | |

### Dimension coverage across features

| Dimension | Assertions | Features touched |
|---|---|---|
| Decomposition (D) | 12 | All 4 |
| Depth (E) | 15 | All 4 |
| Readability (R) | 11 | All 4 |
| Correctness (C) | 7 | All 4 |
| Format (F) | 8 | All 4 (but F assertions are similar across features) |

---

## Variants

As in the original design, run each feature with three variants:

1. **With spec-by-example + tabletest skills** (full guidance)
2. **With tabletest skill only** (knows the format but not the spec design process)
3. **Without any skill** (baseline)

Each variant is graded against the same 48 assertions. The comparison shows which assertions the skills help with.

## Iterative Refinement (Part 2)

After single-shot grading, take the best variant's output for each feature and give the agent 2–3 rounds of feedback. The feedback should target the assertions that failed in the single-shot run.

Record:
- Which assertions flipped from fail to pass after feedback
- How many rounds to reach ≥ 80% per feature
- Whether the agent understood table-level feedback ("split this table", "add boundary values") or needed implementation-level instructions

## Informational Comparison

Not part of pass/fail: compare agent outputs against the experiment 0 merged tables. Note where the agent found equally valid or better structures, and where it missed insights that the iterative human review found.
