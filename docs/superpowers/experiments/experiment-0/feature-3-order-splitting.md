# Feature 3: Order Splitting

**Type:** Multi-condition decision with constraints
**Source:** [HotWax blog](https://www.hotwax.co/blog/3-reasons-to-offer-order-splitting-and-how-to-do-it-right)

## Rules

**Triggers** — an order must be split when:
1. Items have mixed fulfilment methods (BOPIS + home delivery)
2. Items have mixed stock status (in-stock + pre-order)
3. No single fulfilment location has all items in stock
4. Items have different delivery addresses

**Constraint:** Companion/dependent products (e.g., camera body + lens) must never be split across shipments.

**Optimisation:** When splitting is required, minimise total shipments and maximise items per location.

---

## The Representation Challenge

This feature exposes a fundamental challenge for table-based specs: **the input is structurally complex**. An order is not a flat set of parameters — it's a collection of line items, each with a product, fulfilment method, stock status, delivery address, and companion grouping, plus inventory availability across locations.

This is different from weekly pay (4 scalar inputs) or Reis discount (a lookup table). The question is whether table cells can represent this complexity concisely while remaining readable.

---

## Step 1: Ideal Spec Table

Optimised for readability. A product or logistics person should understand each splitting rule.

Design choices:
- Plain table format (markdown / `.table` file) — no Java annotations
- Separate tables per trigger — each table explains one reason to split
- Describe orders in natural language within cells
- Separate table for the companion constraint
- Separate table for location optimisation

### Mixed fulfilment requires splitting

| Scenario | Order items | Split? |
|---|---|---|
| All items for home delivery | [Shirt, Pants] | no |
| All items for store pickup | [Shirt, Pants] | no |
| Some pickup, some delivery | [Shirt(pickup), Pants(delivery)] | yes |

### Mixed stock status requires splitting

| Scenario | Order items | Split? |
|---|---|---|
| All items in stock | [Shirt, Pants] | no |
| In-stock and pre-order mixed | [Shirt(in-stock), Pants(pre-order)] | yes |

### Multiple delivery addresses require splitting

| Scenario | Delivery addresses | Split? |
|---|---|---|
| All to same address | [home, home] | no |
| Gift item to different address | [home, gift address] | yes |
| Three different addresses | [home, office, gift address] | yes |

### Location optimisation — minimise shipments

| Scenario | Shirts ordered | Inventory by warehouse | Shipments? | Shipped from? |
|---|---|---|---|---|
| All at one warehouse | 6 | [Chicago: 6] | 1 | [Chicago: 6] |
| No single warehouse has enough | 6 | [NYC: 4, LA: 2] | 2 | [NYC: 4, LA: 2] |
| Consolidate to fewer shipments | 6 | [Chicago: 4, LA: 1, Houston: 1, Philly: 3, DC: 3] | 2 | [Philly: 3, DC: 3] |

### Companion products must not be split

| Scenario | Order items | Companions | What happens? |
|---|---|---|---|
| Independent items, split is fine | [Camera, Tripod] | | Split across warehouses |
| Camera body and lens stay together | [Camera body, Lens] | [Body + Lens] | Must ship from same warehouse |
| No warehouse has both companions | [Camera body, Lens] | [Body + Lens] | **Open question**: hard constraint (unfulfillable) or soft preference (ship separately with warning)? |

**Strengths:** Each table tells a clear story about one rule. A logistics manager can verify each rule independently. Natural language in cells. The "Shirts ordered" column simplifies the optimisation table to a scalar.

**Weaknesses:** The notation is informal — `Shirt(pickup)` and `[Chicago: 4, LA: 2]` are invented for readability, not parseable by a test. Low scenario count per table. Like Feature 2, the spec tables illustrate the *types* of splitting rules without specifying boundary conditions (e.g., what happens when two warehouses tie? When a warehouse has exactly enough stock?). The companion table silently resolves a genuine domain ambiguity — whether companion shipment is a hard constraint or a soft preference — rather than surfacing it as an open question.

---

## Step 2: Ideal Test Table

Optimised for test quality. Needs structured, parseable representations and thorough coverage.

Design choices:
- Trigger detection combined into one table with set-valued columns
- Trigger interactions tested (what if multiple triggers fire?)
- Companion constraint tested against location splits
- Optimisation uses `Map<String, Integer>` for inventory

```java
@TableTest("""
    Scenario                   | Fulfilment methods  | Stock statuses          | Addresses        | Split required?
    // Single triggers
    Same method, same stock    | {delivery}          | {in-stock}              | {home}           | false
    Mixed fulfilment           | {delivery, pickup}  | {in-stock}              | {home}           | true
    Mixed stock                | {delivery}          | {in-stock, pre-order}   | {home}           | true
    Multiple addresses         | {delivery}          | {in-stock}              | {home, gift}     | true
    // Multiple triggers
    Mixed method and stock     | {delivery, pickup}  | {in-stock, pre-order}   | {home}           | true
    All triggers at once       | {delivery, pickup}  | {in-stock, pre-order}   | {home, gift}     | true
    """)
@DisplayName("Split trigger detection")
void shouldDetectSplitTriggers(Set<FulfilmentMethod> fulfilmentMethods,
        Set<StockStatus> stockStatuses, Set<AddressType> addresses,
        boolean splitRequired) { ... }

@TableTest("""
    Scenario                   | Shirts ordered | Inventory                                    | Shipments? | Shipped from?
    All at one location        | 3              | [NYC: 3]                                     | 1          | [NYC: 3]
    Split across two           | 3              | [NYC: 2, LA: 1]                              | 2          | [NYC: 2, LA: 1]
    Consolidate 3 to 2         | 6              | [Chi: 4, LA: 1, Hou: 1, Phi: 3, DC: 3]      | 2          | [Phi: 3, DC: 3]
    Only option is 3 locations | 3              | [NYC: 1, LA: 1, Hou: 1]                      | 3          | [NYC: 1, LA: 1, Hou: 1]
    """)
@DisplayName("Shipment optimisation")
void shouldOptimiseShipmentCount(int shirtsOrdered, Map<String, Integer> inventory,
        int shipments, Map<String, Integer> shippedFrom) { ... }

@TableTest("""
    Scenario                              | Items        | Companions  | Inventory                         | Shipments? | Assignment?
    No companions, can split              | [A, B]       | []          | [NYC: [A], LA: [B]]               | 2          | [NYC: [A], LA: [B]]
    Companions at same location           | [A, B]       | [[A, B]]    | [NYC: [A, B]]                     | 1          | [NYC: [A, B]]
    Companion splits from non-companion   | [A, B, C]    | [[A, B]]    | [NYC: [A, B], LA: [C]]            | 2          | [NYC: [A, B], LA: [C]]
    // Open question: is companion co-location a hard constraint or soft preference?
    No location has both companions       | [A, B]       | [[A, B]]    | [NYC: [A], LA: [B]]               | ?          | ?
    """)
@DisplayName("Companion constraint")
void shouldKeepCompanionsTogether(List<String> items, List<List<String>> companions,
        Map<String, List<String>> inventory, int shipments,
        Map<String, List<String>> assignment) { ... }
```

**Strengths:** Structured inputs enable test execution. Covers trigger interactions. Companion table uses `Map<String, List<String>>` for inventory — proper type. "Shirts ordered" column simplifies optimisation to scalar count. Companion table includes the unfulfillable edge case (no warehouse has both companions). All inventory assignments are physically valid — items are only assigned to locations that have them.

**Weaknesses:** The map/list notation is hard to read. `[NYC: [A, B], LA: [C]]` requires the reader to parse nested collections. Abstract items (A, B, C) lose domain flavour.

---

## Step 3: Comparison

### Dimension A — Content Convergence

| Aspect | Spec | Test | Agreement? |
|--------|------|------|------------|
| **Trigger coverage** | Each trigger in isolation, no combinations | Triggers isolated + combined | Diverge — test adds interaction scenarios |
| **Optimisation** | Shows the concept with examples | Tests the algorithm with structured data | Partial — both use "Shirts ordered" as scalar + inventory map |
| **Companion constraint** | Simple yes/no | Tests constraint vs trigger interaction | Diverge — test covers the hard cases |
| **Column names** | Natural ("Order items", "Inventory by warehouse") | Structured ("Fulfilment methods", "Inventory") | Partial — spec is warmer, test is more precise |
| **Value representation** | Natural language in cells | Structured collections | Diverge — this is the core tension |

**Convergence:** ~40%. The structural complexity of orders drives a wedge between readable representation and testable representation.

### Dimension B — Table Structure

| Aspect | Spec | Test |
|--------|------|------|
| **Number of tables** | 5 (one per trigger + companion + optimisation) | 3 (triggers combined + optimisation + companion) |
| **Table boundaries** | One trigger per table | Triggers grouped into decision table |

**Structural alignment:** Medium. Both isolate companion constraint and optimisation as separate tables. They diverge on trigger treatment: the spec separates each trigger for clarity; the test combines them to test interactions. The test's approach is arguably better for both — it shows that triggers combine, which is an important spec concept too.

### Dimension C — Value Representation

| Aspect | Spec | Test | Resolvable? |
|--------|------|------|-------------|
| **Trigger attributes** | Natural language `Shirt(pickup)` | Sets `{delivery, pickup}` | Sets work well for both — readable and parseable |
| **Inventory** | Map `[Chicago: 6]` with scalar | Map `[Chi: 4, LA: 1, ...]` with scalar | Aligned — both use inventory map notation |
| **Companion inventory** | Not shown | `Map<String, List<String>>` `[NYC: [A, C]]` | Nested maps are hard to read but necessary for the constraint |
| **Optimisation items** | "Shirts ordered" (scalar) | "Shirts ordered" (scalar) | **Aligned** — simplifying to a count makes this much more readable |

---

## Step 4: Merged Table

Key decisions:
1. **Triggers: combine into one table** — trigger interaction is a real business concern
2. **Use sets for triggers** — `{delivery, pickup}` is both readable and parseable
3. **Optimisation: "Shirts ordered" as scalar count** — avoids complex item lists
4. **Companion: use `Map<String, List<String>>` for inventory** — proper typed representation
5. **Use domain product names** for companion table — "Body" and "Lens" are more meaningful than "A" and "B"
6. **No multi-line rows** — TableTest does not support them; keep all data on one line

```java
@DisplayName("Order Splitting")
@Description("Orders are split into multiple shipments when items cannot or should not "
    + "be fulfilled together. The system minimises shipment count while respecting constraints.")
class OrderSplittingTest {

    @TableTest("""
        Scenario                        | Fulfilment      | Stock status            | Delivery to    | Split?
        // No split needed
        All same method and status      | {delivery}      | {in-stock}              | {home}         | no
        // Individual triggers
        Pickup and delivery mixed       | {pickup, delivery} | {in-stock}           | {home}         | yes
        In-stock and pre-order mixed    | {delivery}      | {in-stock, pre-order}   | {home}         | yes
        Gifts to a different address    | {delivery}      | {in-stock}              | {home, gift}   | yes
        // Combined triggers
        Pickup of pre-order item        | {pickup, delivery} | {in-stock, pre-order} | {home}        | yes
        """)
    @DisplayName("When to split an order")
    @Description("An order must be split when items differ in fulfilment method, "
        + "stock status, or delivery address")
    void shouldDetectSplitTriggers(Set<FulfilmentMethod> fulfilment,
            Set<StockStatus> stockStatus, Set<AddressType> deliverTo, boolean split) {
        // any heterogeneous set triggers a split
    }

    @TableTest("""
        Scenario                             | Shirts ordered | Inventory                               | Shipments? | Shipped from?
        Everything at one warehouse          | 6              | [Chicago: 6]                            | 1          | [Chicago: 6]
        Split across two warehouses          | 6              | [NYC: 4, LA: 2]                         | 2          | [NYC: 4, LA: 2]
        Consolidate: two beats three         | 6              | [Chi: 4, LA: 1, Hou: 1, Phi: 3, DC: 3] | 2          | [Phi: 3, DC: 3]
        Three locations when unavoidable     | 3              | [NYC: 1, LA: 1, Hou: 1]                 | 3          | [NYC: 1, LA: 1, Hou: 1]
        """)
    @DisplayName("Shipment optimisation — minimise shipment count")
    @Description("When splitting is required, the system assigns items to the fewest "
        + "locations possible, maximising items per location")
    void shouldOptimiseShipmentCount(int shirtsOrdered, Map<String, Integer> inventory,
            int shipments, Map<String, Integer> shippedFrom) {
        // prefer fewer shipments; maximise items per location
    }

    @TableTest("""
        Scenario                              | Items              | Companions      | Inventory                              | Shipments? | Assignment?
        Independent items can split           | [Camera, Tripod]   | []              | [NYC: [Camera], LA: [Tripod]]          | 2          | [NYC: [Camera], LA: [Tripod]]
        Companions at same location           | [Body, Lens]       | [[Body, Lens]]  | [NYC: [Body, Lens]]                    | 1          | [NYC: [Body, Lens]]
        Companions co-located with other item | [Body, Lens, Bag]  | [[Body, Lens]]  | [NYC: [Body, Lens, Bag], LA: [Bag]]    | 1          | [NYC: [Body, Lens, Bag]]
        Companion splits from non-companion   | [Body, Lens, Bag]  | [[Body, Lens]]  | [NYC: [Body, Lens], LA: [Bag]]         | 2          | [NYC: [Body, Lens], LA: [Bag]]
        // Open question: is companion co-location a hard constraint or soft preference?
        No warehouse has both companions      | [Body, Lens]       | [[Body, Lens]]  | [NYC: [Body], LA: [Lens]]              | ?          | ?
        """)
    @DisplayName("Companion products must ship together")
    @Description("Dependent products (e.g., camera body and lens) must never be "
        + "split across shipments, even if they are at different locations")
    void shouldKeepCompanionsTogether(List<String> items,
            List<List<String>> companions, Map<String, List<String>> inventory,
            int shipments, Map<String, List<String>> assignment) {
        // companion constraint overrides location-based splitting
    }
}
```

### Tension Catalogue

| # | Tension | Spec pulls toward | Test pulls toward | Resolution in merged table |
|---|---------|-------------------|-------------------|---------------------------|
| 1 | **Trigger granularity** | One table per trigger | Combined trigger table | Combined — trigger interaction is a real business concern, not just a test concern |
| 2 | **Value representation** | Natural language | Structured collections | Sets for triggers (readable AND parseable); maps for inventory (learnable notation) |
| 3 | **Optimisation detail** | Show the concept with a narrative | Test the algorithm | "Shirts ordered" as scalar simplifies the input; inventory map for availability; strong scenario names tell the story |
| 4 | **Assignment output** | Implicit (just shipment count) | Explicit (which items go where) | Explicit — the assignment is important. A logistics person cares about *where things ship from*. |
| 5 | **Item representation** | Domain products (Camera, Shirt) | Abstract (A, B, C) | Domain products — they make the companion constraint meaningful |
| 6 | **Multi-line rows** | Might seem needed for complex values | Not supported by TableTest | Compact single-line notation; validated with tabletest-formatter |

### Scores

| Dimension | Score (1–5) | Notes |
|-----------|-------------|-------|
| **Spec readability** | **3** | The trigger table reads well — set notation for fulfilment/stock/address is clear. But the companion table's nested maps `[NYC: [Body, Bag], LA: [Lens, Bag]]` require learning notation. The "Shirts ordered" scalar column helps the optimisation table. Strong scenario names and `@Description` compensate partially. |
| **Test quality** | **4** | Trigger combinations covered. Optimisation tested with concrete scenarios. Companion constraint covers valid co-location, split from non-companion, and unfulfillable edge case. Loses one point: doesn't test companion constraint against non-location triggers (e.g., companion items with different stock statuses or mixed fulfilment methods). |

### Observations

1. **Structural complexity is the real enemy of spec-test identity.** When inputs are scalar (weekly pay) or lookup-based (Reis tiers), the table format works beautifully for both spec and test. When inputs are structured objects (orders with items, inventory maps), the representation challenge dominates.

2. **Scenario names carry more weight when cells are complex.** In the trigger table, `{pickup, delivery}` is readable. In the companion table, `[NYC: [Body, Bag], LA: [Lens, Bag]]` is dense — the scenario name "Companions forced to same shipment" does the readability work. **The scenario column IS the spec; the data columns are the test.**

3. **Scalar column headings simplify complex domains.** "Shirts ordered" as a column with a scalar count is much more readable than a list of items. This works because the optimisation logic doesn't depend on product identity — only quantity matters.

4. **Set notation works well for closed enumerations.** `{delivery, pickup}` for fulfilment methods and `{in-stock, pre-order}` for stock status are both readable and testable. This pattern works because these are small, closed sets.

5. **The trigger table achieves strong identity.** Despite the overall feature scoring lower, the trigger detection table scores 5/5 on both dimensions. The identity holds well for decision logic with enumerable inputs. It's the inventory/assignment tables that struggle.

6. **The optimisation concern may not belong in exhaustive tables.** Shipment optimisation is fundamentally an algorithm — complex input, optimal output. The table format excels at decision logic and lookups, not algorithmic optimisation. A few representative scenarios (as shown) are appropriate; exhaustive coverage would need a different approach.

7. **Syntax validation matters.** The tabletest-formatter can validate table syntax before running tests, catching issues like multi-line rows or misaligned columns early.

8. **Spec tables should surface ambiguities, not resolve them.** The companion constraint was presented as a hard rule ("Cannot be fulfilled") when the domain admits two interpretations: companion co-location as a hard constraint (unfulfillable) or a soft preference (ship separately with a warning). The spec-by-example process should flag this as an open question for product decision rather than silently assuming one interpretation. A row with `?` in the expected columns explicitly communicates "we need to decide this."

9. **Spec depth: illustrating rule types is not specifying behaviour.** Like Feature 2, the spec tables show what *kinds* of splitting rules exist (fulfilment, stock status, address, companion, optimisation) without specifying boundary conditions. What happens when two warehouses tie for optimal allocation? When a warehouse has exactly enough stock? These questions matter for implementation but are invisible in the current tables.
