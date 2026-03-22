# Experiment 0: Spec-Test Identity — Summary

**Date:** 2026-03-22
**Hypothesis tested:** H0 — A single table can simultaneously be a readable behaviour specification and a well-structured `@TableTest`.

## Scores

| Feature | Type | Spec readability | Test quality | Min |
|---------|------|:---:|:---:|:---:|
| 1. Weekly Pay | Simple calculation | **5** | **4** | 4 |
| 2. Reis Discount | Multi-condition decision | **5** | **5** | 5 |
| 3. Order Splitting | Multi-condition + constraints | **3** | **4** | 3 |
| 4. Shopping Cart | Stateful workflow | **4** | **4** | 4 |

**Pass criteria:** Merged tables score ≥ 4 on both readability and test quality for ≥ 3/4 features.

**Result: PASS (3/4 features meet the threshold)**

Features 1, 2, and 4 score ≥ 4 on both dimensions. Feature 3 (order splitting) scores 3 on readability due to nested map notation for inventory and shipment assignments.

---

## Key Findings

### 1. The identity holds strongly for decision logic and calculations

When inputs are **scalars** (weekly pay) or **enumerations** (discount tiers, eligibility), the table format achieves near-perfect spec-test identity. The natural spec structure (separate tables per concern, domain scenario names) is also good test structure (focused tests, descriptive names).

The strongest example: the Reis discount tier table using value sets. Nine rows with value sets (`{5, 6, 7, 8, 9}`) generate 45 test cases while reading as the authoritative definition of the tier programme. **Exhaustiveness serves readability when expressed declaratively.**

### 2. Separating concerns improves both spec and test quality

A consistent finding across features: separating intertwined concerns into distinct columns or tables improves both dimensions simultaneously.

- **Weekly pay**: Separating hour classification from pay calculation matches how payroll works AND how tests should decompose calculations.
- **Shopping cart**: Separating catalogue (product→price) from cart (product→quantity) makes the price lookup explicit as a business concept AND enables independent testing of pricing and quantity logic.
- **Shopping cart**: Separating coupon operations from item operations focuses each table.

These are not compromises — they make BOTH the spec and the test better.

### 3. Structural complexity is the enemy

When inputs are **complex objects** (orders with items, inventory maps, cart states), the representation challenge dominates. Table cells become mini-DSLs (`[NYC: [Body, Bag], LA: [Lens, Bag]]`) that require the reader to learn notation. This is learnable but not self-evident.

The dividing line: **closed enumerations in cells** (e.g., `{delivery, pickup}`) work well; **nested structured data** (e.g., `Map<String, List<String>>`) does not.

### 4. Multi-table structure converges naturally

In all 4 features, the spec and test versions arrived at similar table boundaries. This is a strong signal: **the natural conceptual grouping of business rules aligns with good test decomposition**. Separate concerns in separate tables is simultaneously good spec design and good test design.

| Feature | Spec tables | Test tables | Structural convergence |
|---------|:-----------:|:-----------:|:----------------------:|
| Weekly Pay | 4 | 2 | Medium-high — shared two-step decomposition |
| Reis Discount | 4 | 4 | **High — same four concerns** |
| Order Splitting | 5 | 3 | Medium — agree on companion/optimisation split |
| Shopping Cart | 4 | 4 | Medium — same count, different grouping logic |

### 5. Scenario names bridge the readability gap

When cell values are complex, **the scenario column IS the spec; the data columns are the test.** "Consolidate: two beats three" tells the business story; the inventory map proves it. This pattern emerged consistently in Features 3 and 4.

### 6. State transition rules fit tables; sequential paths don't

The original analysis framed stateful workflows as sequential step-by-step paths — creating an apparent conflict with TableTest's row-independence. The key reframe: **spec as rules, not paths**.

| Framing | Each row is... | Independent? | Spec quality | Test quality |
|---------|---------------|:---:|:---:|:---:|
| **Sequential path** | A step in a journey | No | High (narrative) | Low (row-dependent) |
| **State transition rule** | state × action → new state | Yes | High (rule) | High (self-contained) |

When specs describe *rules* ("empty cart + add item → cart with item"), every row is independent — matching TableTest's execution model. Sequential narratives (customer journeys) are better served by `@Test` methods with `@DisplayName`.

This decomposition holds for stateful features:
- **State transition rules**: table format works well (stack-style)
- **Pure calculations**: table format excels
- **Integration journeys**: `@Test` methods with `@DisplayName`

### 7. Value sets and TypeConverters are key enablers

- **Value sets** bridge the coverage gap between "representative examples" (spec instinct) and "test every path" (test instinct). `{1, 2, 3, 4}` for a tier range is both readable and exhaustive.
- **`@TypeConverterSources`** enables spec-friendly values in test code. Using "yes"/"no" in tables instead of true/false, with a shared converter, makes tables read naturally without sacrificing type safety.
- These tools resolve what would otherwise be fundamental spec-test tensions.

### 8. Focus on rules, not arithmetic

A recurring pattern: early versions had tables testing multiplication (pay calculation, price calculation, total calculation). These are not interesting rules — they're testing the language's arithmetic. The improved versions focus tables on **the domain rules** (hour classification, tier lookup, coupon validation) and let integration tests verify the pipeline. Tables should test decisions, not calculations that have no branching logic.

### 9. Column names should reflect the user's world

"Message?" is better than "Result?" for user-facing operations. "Coupon code entered" is better than "Coupon" because it describes the user's action. "Valid coupons" as a column makes the system's knowledge explicit. These naming choices significantly affect spec readability — the column name IS the spec for what that value represents.

### 10. Spec and test formats serve different roles

The **ideal spec** is a plain table (markdown / `.table` file) — no Java annotations, no code. The **ideal test** is `@TableTest` Java code with `@DisplayName` and `@Description`. The merged table is code, but the tabletest-reporter bridges the gap — published reports present the tables in a readable format regardless of source format. The spec lives in how the table reads; the test lives in how it executes.

---

## Tension Patterns (Cross-Feature)

| Tension | Frequency | Typical resolution |
|---------|:---------:|-------------------|
| **Concern separation** (mixed vs decomposed) | 4/4 | Separate concerns into distinct tables and columns — improves BOTH spec and test |
| **Scenario naming** (business situation vs boundary condition) | 4/4 | Business language for most rows; boundary language for threshold rows. Both serve readability. |
| **Table structure** (many focused tables vs one comprehensive table) | 4/4 | Multiple focused tables — naturally convergent with good test design |
| **Column visibility** (hide vs show zero/irrelevant columns) | 3/4 | Hide in focused tables; show all in integration/combined tables |
| **Value representation** (natural language vs structured notation) | 3/4 | Structured notation with domain vocabulary. TypeConverters for human-readable values. |
| **Coverage depth** (representative vs exhaustive) | 2/4 | Value sets for "regardless of" coverage; exhaustive for tier/lookup tables. **Caution:** value sets mean "any value gives the same result" — not "test multiple values" |
| **Illustration vs specification** | 3/4 | Spec tables tend to illustrate rule types without specifying boundary conditions. Sufficient for product alignment but not for implementation. |
| **Ambiguity resolution** | 2/4 | Tables should surface domain ambiguities as open questions (`?`), not silently resolve them |
| **Empty cells for optional inputs** | 1/4 | Blank cells signal "not part of this scenario" — more readable than explicit zeroes for optional columns |
| **Rules vs arithmetic** | 4/4 | Focus tables on domain rules (decisions, classifications, validations); don't test multiplication |
| **Column naming** | 2/4 | Use user-facing names ("Message?" not "Result?", "Coupon code entered" not "Coupon") |
| **Sequential paths vs state rules** | 1/4 | Frame as state transition rules (independent rows), not sequential paths; journeys in `@Test` methods |

---

## Verdict

**H0 passes with a scope qualification.** The spec-test identity holds for:

- **Decision logic** (eligibility, triggers, priorities) — strong identity
- **Lookup tables** (discount tiers, pricing bands) — strongest identity; exhaustive value-set tables ARE the spec
- **Calculations** (pricing, totals, pay) — strong identity, especially when decomposed into pure functions
- **Individual state operations** (add/remove/apply) — good identity via stack pattern

The identity is weaker for:

- **Complex structured inputs** (nested maps for inventory/assignments) — notation overhead hurts readability
- **Sequential workflows** (multi-step customer journeys) — the row-independent model doesn't support narrative sequences

This is not a binary pass/fail. The conclusion is: **"the identity holds for decision/validation/calculation logic, with diminishing returns as input complexity grows and a hard boundary at sequential narratives."** This is a useful, actionable boundary.

---

## Implications for Experiment 1

1. **Feature selection for agent eval:** Use Features 1 and 2 (and similar decision/calculation features) to test H1 with the highest chance of success. Include Feature 3-style complexity as a stretch test.

2. **Skill guidance:** The spec-by-example skill should guide agents toward:
   - Focus tables on domain rules, not arithmetic
   - Separate tables per concern (the consistent convergent structure)
   - Decompose into the interesting rules (hour classification, not pay multiplication)
   - Frame stateful features as state transition rules, not sequential paths
   - Value sets for "regardless of" coverage
   - `@Test` methods for integration journeys
   - `@Description` on methods for spec context

3. **Column naming guidance:**
   - Use user-facing names: "Message?" not "Result?", "Coupon code entered" not "Coupon"
   - Make system knowledge explicit: "Valid coupons" column, not hardcoded test setup
   - Include intermediate expectation columns for traceability (e.g., "Discount?" in pricing tables)

4. **TypeConverter patterns:** Encourage:
   - `@TypeConverterSources` for reusable yes/no → boolean conversion
   - Domain-friendly value formats (case-insensitive enums, human-readable booleans)
   - Separate data maps (e.g., catalogue separate from cart) rather than compound values

5. **Notation guidance:** When agents produce tables with complex cell values:
   - Compact, consistent notation validated with tabletest-formatter
   - Strong scenario names to compensate for complex values
   - Scalar column headings where possible (e.g., "Shirts ordered" instead of item lists)

6. **Scope:** H1 can be tested confidently on decision/calculation features. Testing on inventory-optimisation features would stretch beyond where H0 showed strong identity.

7. **Boundary condition depth:** Experiment 1 rubrics should assess whether agent-produced tables specify boundary conditions (thresholds, edge cases, off-by-one) or merely illustrate rule types. This was the most consistent weakness across Features 2–4.

8. **Ambiguity surfacing:** Experiment 1 rubrics should assess whether agents flag domain ambiguities as open questions or silently resolve them. Spec-by-example's value is partly in forcing these questions — tables that assume answers defeat the purpose.

9. **Arithmetic self-checking:** Feature 1 had an incorrect expected value (55.5 instead of 71.5) that persisted through all four steps. Experiment 1 should note whether agents verify their own expected values.

10. **Empty cell convention:** Empty cells for optional inputs (e.g., `Integer` parameters with null-to-zero handling) improve readability. Skill guidance should encourage this pattern for columns that are irrelevant to specific scenarios.
