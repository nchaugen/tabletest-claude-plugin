# Feature 4: Shopping Cart (Stateful Workflow)

**Type:** Stateful workflow
**Source:** Common domain

## Rules

- **Add item**: product, quantity → item added to cart (price looked up from catalogue)
- **Remove item**: product → all units of that product removed
- **Apply coupon**: coupon code → discount applied if valid; rejected if expired or invalid
- **Checkout**: fails if any item has insufficient stock; fails if cart is empty
- **Cart total**: sum of (quantity × price) for all items, minus coupon discount, floored at zero
- **Coupon types**: percentage off total, fixed amount off total, product-specific discount
- **Page flow**: empty cart → cart with items → (optionally) coupon applied → checkout

---

## The State Transition Insight

The user's feedback surfaces an important reframe: an ideal spec for a stateful system describes **rules and state transitions**, not step-by-step paths.

Instead of "Step 1: add shirt, Step 2: add pants, Step 3: checkout" (a path through the system), a spec should say:

- **State transition**: "Empty cart + add item → cart with one item"
- **Rule**: "Coupon is applied to current cart total"
- **Constraint**: "Cannot checkout with empty cart"

This maps naturally to the stack-style table pattern: `state before × action → state after`. Each row IS a state transition rule. The table is not telling a story — it's defining the state machine.

---

## Step 1: Ideal Spec Table

Optimised for readability. A product person should understand the cart rules: what state transitions are allowed, what rules apply, and what feedback the customer sees.

Design choices:
- Plain table format (markdown / `.table` file) — no Java annotations
- Tables describe rules and state transitions, not paths
- Actions described from the customer's perspective
- Results described as user feedback (messages)
- Separate tables for: item management, coupon rules, checkout rules, page flow

### Item management

| Scenario | Cart state | Customer action | Cart after? | Message? |
|---|---|---|---|---|
| Add first item | Empty cart | Adds 2 shirts | 2× Shirt | "Added to cart" |
| Add another product | Shirts in cart | Adds 1 pants | Shirts + Pants | "Added to cart" |
| Add more of same | Shirts in cart | Adds 1 more shirt | 3× Shirt | "Added to cart" |
| Remove a product | Shirts and pants | Removes shirts | Pants only | "Removed" |
| Remove unknown product | Shirts in cart | Removes pants | Unchanged | "Not in cart" |
| Remove last item | Shirts in cart | Removes shirts | Empty cart | "Removed" |

### Coupon rules

| Scenario | Coupon code entered | Valid coupons | Message? | Effect? |
|---|---|---|---|---|
| Valid percentage coupon | SUMMER20 | [SUMMER20: 20% off total] | "20% discount applied" | 20% off cart total |
| Valid fixed amount coupon | SAVE10 | [SAVE10: 10.00 off total] | "10.00 off applied" | 10.00 off cart total |
| Product-specific coupon | SHIRTS10 | [SHIRTS10: 10% off shirts] | "10% off shirts applied" | 10% off shirt items only |
| Expired coupon | OLD20 | [OLD20: expired] | "Coupon has expired" | No discount |
| Unknown coupon code | FAKE | [] | "Coupon not recognised" | No discount |
| Coupon exceeds cart total | SAVE50 | [SAVE50: 50.00 off total] | "50.00 off applied" | Total becomes 0 |
| Replace existing coupon | SAVE10 | [SAVE10: 10.00 off total] | "Replaced SUMMER20 with SAVE10" | Previous coupon removed |

### Checkout rules

| Scenario | Cart state | Stock available | Result? | Message? |
|---|---|---|---|---|
| Successful checkout | 2 shirts in cart | 5 shirts | Order placed | "Order confirmed" |
| Insufficient stock | 5 shirts in cart | 3 shirts | Cannot complete | "Only 3 shirts available" |
| Empty cart | Empty | — | Cannot complete | "Cart is empty" |

### Page flow

| Scenario | Current state | Action | Next state? |
|---|---|---|---|
| Start shopping | Empty cart | Adds item | Cart with items |
| Continue shopping | Cart with items | Adds item | Cart with items |
| Apply discount | Cart with items | Enters coupon | Cart with coupon |
| Remove discount | Cart with coupon | Removes coupon | Cart with items |
| Begin checkout | Cart with items | Clicks checkout | Checkout page |
| Complete purchase | Checkout page | Confirms order | Order confirmed |
| Return to cart | Checkout page | Clicks back | Cart with items |
| Cannot checkout empty | Empty cart | Clicks checkout | Error shown |

**Strengths:** Rules and state transitions, not paths. Each row is an independent rule. The coupon table separates the coupon validation rules (what's valid) from the cart state (what's in the cart). Page flow table defines the state machine. Message column shows user feedback.

**Weaknesses:** "Cart state" column uses natural language that isn't precise enough for a test. The page flow table may be more of a UI concern than a domain concern.

---

## Step 2: Ideal Test Table

Optimised for test quality. Each row is independently executable.

Design choices:
- Stack-style: explicit cart state before/after as maps
- Catalogue as separate map (product→price)
- Coupon validation separated: "Valid coupons" column defines what the system recognises
- "Message?" column for user feedback (not "Result?")
- Checkout and total calculation as separate concerns

```java
@TableTest("""
    Scenario                       | Catalogue               | Cart before          | Action         | Message?     | Cart after?
    // Adding items
    Add to empty cart              | [Shirt: 30, Pants: 35]  | []                   | add Shirt ×2   | Added        | [Shirt: 2]
    Add a different product        | [Shirt: 30, Pants: 35]  | [Shirt: 2]           | add Pants ×1   | Added        | [Shirt: 2, Pants: 1]
    Add more of existing product   | [Shirt: 30]             | [Shirt: 2]           | add Shirt ×1   | Added        | [Shirt: 3]
    // Removing items
    Remove a product               | [Shirt: 30, Pants: 35]  | [Shirt: 2, Pants: 1] | remove Shirt  | Removed      | [Pants: 1]
    Remove product not in cart     | [Shirt: 30]             | [Shirt: 2]           | remove Pants   | Not in cart  | [Shirt: 2]
    Remove last product            | [Shirt: 30]             | [Shirt: 2]           | remove Shirt   | Removed      | []
    """)
@DisplayName("Cart item operations")
void shouldProcessCartOperations(Map<String, BigDecimal> catalogue,
        Map<String, Integer> cartBefore, CartAction action,
        String message, Map<String, Integer> cartAfter) { ... }

@TableTest("""
    Scenario                  | Cart items  | Coupon code entered | Valid coupons                  | Message?                     | Active coupon?
    Valid percentage coupon    | [Shirt: 2]  | SUMMER20            | [SUMMER20: 20% off]            | 20% discount applied         | SUMMER20
    Valid fixed amount coupon  | [Shirt: 2]  | SAVE10              | [SAVE10: 10.00 off]            | 10.00 off applied            | SAVE10
    Expired coupon             | [Shirt: 2]  | OLD20               | [OLD20: expired]               | Coupon has expired           |
    Unknown coupon code        | [Shirt: 2]  | FAKE                | []                             | Coupon not recognised        |
    Replace existing coupon    | [Shirt: 2]  | SAVE10              | [SAVE10: 10.00 off]            | Replaced SUMMER20            | SAVE10
    """)
@DisplayName("Coupon validation")
@Description("Validates coupon codes against the system's known coupons")
void shouldValidateCoupons(Map<String, Integer> cartItems, String couponCodeEntered,
        Map<String, CouponDef> validCoupons, String message, String activeCoupon) { ... }

@TableTest("""
    Scenario                | Cart items             | Catalogue                       | Coupon      | Total?
    Empty cart              | []                     | [:]                             |             | 0.00
    Single product          | [Shirt: 2]             | [Shirt: 30.00]                  |             | 60.00
    Multiple products       | [Shirt: 2, Pants: 1]   | [Shirt: 30.00, Pants: 35.00]   |             | 95.00
    Percentage discount     | [Shirt: 2, Pants: 1]   | [Shirt: 30.00, Pants: 35.00]   | 20% off     | 76.00
    Fixed discount          | [Shirt: 2]             | [Shirt: 30.00]                  | 10.00 off   | 50.00
    Discount exceeds total  | [Shirt: 1]             | [Shirt: 5.00]                   | 50.00 off   | 0.00
    """)
@DisplayName("Cart total calculation")
void shouldCalculateTotal(Map<String, Integer> cartItems,
        Map<String, BigDecimal> catalogue, Coupon coupon, BigDecimal total) { ... }

@TableTest("""
    Scenario              | Cart items  | Stock        | Message?
    Sufficient stock      | [Shirt: 2]  | [Shirt: 5]   | Order confirmed
    Insufficient stock    | [Shirt: 5]  | [Shirt: 3]   | Only 3 available
    Empty cart             | []          | [:]           | Cart is empty
    """)
@DisplayName("Checkout validation")
void shouldValidateCheckout(Map<String, Integer> cartItems,
        Map<String, Integer> stock, String message) { ... }
```

**Strengths:** Each row is an independent state transition rule. Catalogue separated from cart. Coupon table has "Valid coupons" column making the test's coupon knowledge explicit. "Message?" shows user feedback. "Coupon code entered" is the user's action, "Valid coupons" is the system's knowledge.

**Weaknesses:** No workflow-level integration tests. Map notation requires learning.

---

## Step 3: Comparison

### Dimension A — Content Convergence

| Aspect | Spec | Test | Agreement? |
|--------|------|------|------------|
| **Row semantics** | State transition rules | State transition rules | **Agree** — the reframe to rules/transitions aligns both |
| **Item operations** | Natural language state | Map notation | Diverge in representation, converge in structure |
| **Coupon model** | "Valid coupons" as column, "Coupon code entered" as action | Same structure | **Agree** |
| **User feedback** | "Message?" column | "Message?" column | **Agree** |
| **Page flow** | Explicit state machine table | Not present (UI concern) | Diverge — test focuses on domain, not UI |

**Convergence:** ~70%. The state-transition reframe significantly increases convergence. Both versions model the same rules with the same column roles — they differ mainly in value representation (natural language vs maps).

### Dimension B — Table Structure

| Aspect | Spec | Test |
|--------|------|------|
| **Number of tables** | 4 (items, coupons, checkout, page flow) | 4 (items, coupons, total, checkout) |
| **Table boundaries** | Items / Coupon rules / Checkout / Page flow | Items / Coupon validation / Total calc / Checkout |

**Structural alignment:** Medium-high. Both separate items, coupons, and checkout. The spec adds a page flow table; the test adds a total calculation table.

### Dimension C — Value Representation

| Aspect | Spec | Test | Resolvable? |
|--------|------|------|-------------|
| **Cart state** | Natural language ("Shirts in cart") | Map `[Shirt: 2]` | Map is more precise; natural language is warmer |
| **Coupon definition** | Descriptive ("20% off total") | Map `[SUMMER20: 20% off]` | Both are readable |
| **Messages** | Full sentences ("Coupon has expired") | Same | **Agree** |

### Dimension D — Sequential/Temporal Readability

| Question | Assessment |
|----------|------------|
| **Can a reader understand the cart rules from the tables?** | Yes — each row defines one state transition. The page flow table shows allowed transitions. |
| **Are preconditions clear?** | Spec: natural language ("Shirts in cart"). Test: explicit map. Both work. |
| **Does the state-transition approach avoid the sequential limitation?** | **Yes.** By framing each row as a rule rather than a step in a path, every row is independent. The sequential limitation only arises when rows represent steps in a single journey. |

**Key finding:** The state-transition reframe resolves the fundamental tension from the original analysis. When specs describe *rules* ("empty cart + add item → cart with item"), every row is independent — matching TableTest's execution model. The sequential limitation only appears when specs describe *paths* ("step 1, step 2, step 3").

---

## Step 4: Merged Table

The state-transition reframe means layers 1 and 2 merge cleanly, and layer 3 (workflows) is replaced by the page flow table and standard `@Test` integration methods.

```java
@DisplayName("Shopping Cart")
@Description("Manages items, coupons, and checkout for online shopping")
class ShoppingCartTest {

    @TableTest("""
        Scenario                       | Catalogue               | Cart before          | Action         | Message?     | Cart after?
        // Adding items
        Add to empty cart              | [Shirt: 30, Pants: 35]  | []                   | add Shirt ×2   | Added        | [Shirt: 2]
        Add a different product        | [Shirt: 30, Pants: 35]  | [Shirt: 2]           | add Pants ×1   | Added        | [Shirt: 2, Pants: 1]
        Add more of existing product   | [Shirt: 30]             | [Shirt: 2]           | add Shirt ×1   | Added        | [Shirt: 3]
        // Removing items
        Remove a product               | [Shirt: 30, Pants: 35]  | [Shirt: 2, Pants: 1] | remove Shirt  | Removed      | [Pants: 1]
        Remove product not in cart     | [Shirt: 30]             | [Shirt: 2]           | remove Pants   | Not in cart  | [Shirt: 2]
        Remove last product            | [Shirt: 30]             | [Shirt: 2]           | remove Shirt   | Removed      | []
        """)
    @DisplayName("Cart item operations")
    @Description("Adding and removing products from the cart")
    void shouldProcessCartOperations(Map<String, BigDecimal> catalogue,
            Map<String, Integer> cartBefore, CartAction action,
            String message, Map<String, Integer> cartAfter) {
        // catalogue provides price lookup; cart tracks product → quantity
    }

    @TableTest("""
        Scenario                  | Cart items  | Coupon code entered | Valid coupons                  | Message?                     | Active coupon?
        Valid percentage coupon    | [Shirt: 2]  | SUMMER20            | [SUMMER20: 20% off]            | 20% discount applied         | SUMMER20
        Valid fixed amount coupon  | [Shirt: 2]  | SAVE10              | [SAVE10: 10.00 off]            | 10.00 off applied            | SAVE10
        Product-specific coupon   | [Shirt: 2]  | SHIRTS10            | [SHIRTS10: 10% off Shirt]      | 10% off shirts applied       | SHIRTS10
        Expired coupon             | [Shirt: 2]  | OLD20               | [OLD20: expired]               | Coupon has expired           |
        Unknown coupon code        | [Shirt: 2]  | FAKE                | []                             | Coupon not recognised        |
        Replace existing coupon    | [Shirt: 2]  | SAVE10              | [SAVE10: 10.00 off]            | Replaced previous coupon     | SAVE10
        Coupon exceeds cart total  | [Shirt: 1]  | SAVE50              | [SAVE50: 50.00 off]            | 50.00 off applied            | SAVE50
        """)
    @DisplayName("Coupon validation and application")
    @Description("Validates coupon codes against the system's known coupons; "
        + "only one coupon can be active at a time")
    void shouldValidateCoupons(Map<String, Integer> cartItems, String couponCodeEntered,
            Map<String, CouponDef> validCoupons, String message, String activeCoupon) {
        // "Valid coupons" column defines what the system recognises
        // TypeConverter parses "20% off", "10.00 off", "10% off Shirt", "expired"
    }

    @TableTest("""
        Scenario                | Cart items             | Catalogue                       | Coupon      | Total?
        Empty cart              | []                     | [:]                             |             | 0.00
        Single product          | [Shirt: 2]             | [Shirt: 30.00]                  |             | 60.00
        Multiple products       | [Shirt: 2, Pants: 1]   | [Shirt: 30.00, Pants: 35.00]   |             | 95.00
        Percentage discount     | [Shirt: 2, Pants: 1]   | [Shirt: 30.00, Pants: 35.00]   | 20% off     | 76.00
        Fixed discount          | [Shirt: 2]             | [Shirt: 30.00]                  | 10.00 off   | 50.00
        Discount exceeds total  | [Shirt: 1]             | [Shirt: 5.00]                   | 50.00 off   | 0.00
        """)
    @DisplayName("Cart total calculation")
    @Description("Total = sum of (quantity × price) for each product, minus coupon, floored at zero")
    void shouldCalculateTotal(Map<String, Integer> cartItems,
            Map<String, BigDecimal> catalogue, Coupon coupon, BigDecimal total) {
        // pure function: cart × catalogue → subtotal, minus coupon → total
    }

    @TableTest("""
        Scenario              | Cart items  | Stock        | Message?
        Sufficient stock      | [Shirt: 2]  | [Shirt: 5]   | Order confirmed
        Insufficient stock    | [Shirt: 5]  | [Shirt: 3]   | Only 3 available
        Empty cart             | []          | [:]           | Cart is empty
        """)
    @DisplayName("Checkout validation")
    @Description("Checkout verifies stock availability for all items before placing the order")
    void shouldValidateCheckout(Map<String, Integer> cartItems,
            Map<String, Integer> stock, String message) {
        // all items must have sufficient stock; empty cart is rejected
    }
}
```

Integration tests for full workflows remain as `@Test` methods:

```java
@Test
@DisplayName("Customer adds items, applies coupon, and checks out")
void shouldCompleteTypicalPurchase() {
    cart.add("Shirt", 2);
    cart.add("Pants", 1);
    cart.applyCoupon("SUMMER20");
    cart.remove("Shirt");

    CheckoutResult result = cart.checkout(inventory);

    assertThat(result.isSuccessful()).isTrue();
    assertThat(result.total()).isEqualByComparingTo("28.00");
}
```

---

### Tension Catalogue

| # | Tension | Spec pulls toward | Test pulls toward | Resolution |
|---|---------|-------------------|-------------------|------------|
| 1 | **Spec framing** | Rules and state transitions | Independent test cases | **No tension** — state transition rules ARE independent test cases. The reframe resolves the original sequential vs independent tension. |
| 2 | **Coupon model** | "Coupon code entered" + "Valid coupons" | Same | **No tension** — separating user action from system knowledge serves both. |
| 3 | **User feedback** | "Message?" column | "Message?" column | **No tension** — both want to verify what the user sees. |
| 4 | **Price representation** | Embedded in actions | Separate catalogue map | Catalogue is a real concept — separating it is cleaner for both. |
| 5 | **State representation** | Natural language | Map notation | Maps are compact and precise. Scenario names provide the natural language gloss. |
| 6 | **Page flow** | Explicit state machine table | Not needed for domain tests | Page flow is a UI/navigation concern — valuable as spec documentation but not needed in domain tests. |
| 7 | **Coupon variations** | Show different coupon types (%, fixed, product-specific) | Same | **No tension** — the "Valid coupons" column makes coupon type explicit in both. |

### Scores

| Dimension | Score (1–5) | Notes |
|-----------|-------------|-------|
| **Spec readability** | **4** | State transition framing makes each row a clear rule. "Message?" column shows user feedback. Coupon table with "Valid coupons" and "Coupon code entered" columns reads naturally. Catalogue separation makes price lookup explicit. Loses one point: map notation `[Shirt: 2]` still requires learning, though it's compact. |
| **Test quality** | **4** | Comprehensive coverage of item ops, coupon validation (including product-specific and expired), total calculation, and checkout. Each row independently executable. Loses one point: integration/workflow coverage relies on separate `@Test` methods. |

### Observations

1. **The state-transition reframe is the key insight for stateful features.** The original analysis framed the shopping cart as a sequential workflow problem, creating an apparent conflict with TableTest's row-independence. Reframing as state transition rules resolves the tension: each row defines one rule (state × action → new state), which is both a readable spec and an independent test case.

2. **"Message?" is better than "Result?" for user-facing operations.** The column name should reflect what the user sees. "Result?" is implementation-oriented; "Message?" is user-oriented. This is a small naming choice that significantly affects spec readability.

3. **"Valid coupons" as a column makes the test's knowledge explicit.** Instead of hardcoding coupon definitions in the test setup, making them a column turns the coupon system into a rule table: "given these valid coupons, when the user enters this code, they see this message." This is simultaneously better spec design (the rules are visible) and better test design (the test is self-contained).

4. **Coupon rules are domain rules, just like discount tiers.** The user's observation that coupons have their own rules and variations (percentage, fixed, product-specific, expiry) is correct. These are business rules that deserve their own table — not just parameters to an "apply coupon" action. The "Valid coupons" column pattern makes this explicit.

5. **Page flow is a valuable spec artefact but not a table test.** The state machine table (current state × action → next state) is excellent spec documentation. It's testable too, but it tests UI navigation rather than domain logic. Including it in the spec tables but not in the domain `@TableTest` methods is a reasonable scope decision.

6. **Dimension D revised verdict:** The original conclusion was "sequential workflows don't fit tables." The revised conclusion is: **"Sequential paths don't fit tables, but state transition rules do."** The key is how you frame each row — as a step in a story (dependent on order) or as a rule (independent). Rules are both better specs and better tests.

7. **Coupon rules are significantly under-specified.** The coupon table covers the obvious cases (percentage, fixed, product-specific, expired, unknown) but the coupon domain has considerably more depth that the tables don't explore:
   - **Existence**: how are valid coupons defined and stored?
   - **Date validity**: not just "expired" — what about "not yet active" or "valid between dates"?
   - **Item validity**: product-specific coupons — which items qualify? Category-level coupons?
   - **Discount strategy**: percentage vs fixed vs free shipping vs buy-one-get-one — each has different calculation rules
   - **Coupon precedence**: when multiple coupons could apply, which takes priority? Can coupons stack?
   - **Minimum order value**: many coupons require a minimum spend
   - **Usage limits**: per-customer limits, total redemption limits

   A real coupon system would need multiple tables to specify these rules, each with its own boundary conditions. The current table scratches the surface — it demonstrates the *format* works for coupon rules but doesn't demonstrate the *depth* needed for implementation.

8. **Under-specification is the most common failure mode for rich domains.** Across features 2–4, the tables consistently cover the obvious cases without exploring the depth of the domain. The table format is good at making rules explicit — but only if the author thinks to include them. This is a pattern to watch in Experiment 1: does the agent stop at the obvious cases, or does it probe the domain for hidden complexity?
