Five distinct concerns here — I'll give each its own table.

---

## Order Splitting — Spec by Example

---

### Table 1: Fulfillment Method Split

Items in the same order cannot share a shipment when their fulfillment methods differ.

| Scenario | Items | Fulfillment Methods | Split? | Shipments? | Shipment Groups? |
|---|---|---|---|---|---|
| All items for home delivery | Shirt, Shoes | Delivery, Delivery | no | 1 | {Shirt, Shoes} |
| All items for store pickup | Shirt, Shoes | Pickup, Pickup | no | 1 | {Shirt, Shoes} |
| One delivery, one pickup | Shirt, Shoes | Delivery, Pickup | yes | 2 | {Shirt}, {Shoes} |
| Multiple delivery items with one pickup | Shirt, Shoes, Toy | Delivery, Delivery, Pickup | yes | 2 | {Shirt, Shoes}, {Toy} |
| One delivery item with multiple pickup | Shirt, Shoes, Toy | Delivery, Pickup, Pickup | yes | 2 | {Shirt}, {Shoes, Toy} |

---

### Table 2: Availability-Based Grouping

In-stock items should ship immediately rather than wait for pre-orders or backorders.

| Scenario | Items | Availability | Split? | Shipments? | Shipment Groups? |
|---|---|---|---|---|---|
| All items in stock | Shirt, Shoes | In stock, In stock | no | 1 | {Shirt, Shoes} |
| In-stock + pre-order | Shirt, Shoes | In stock, Pre-order | yes | 2 | {Shirt}, {Shoes} |
| In-stock + backorder | Shirt, Shoes | In stock, Backorder | yes | 2 | {Shirt}, {Shoes} |
| All pre-order, same expected date | Shirt, Shoes | Pre-order Apr 1, Pre-order Apr 1 | no | 1 | {Shirt, Shoes} |
| Pre-orders with different expected dates | Shirt, Shoes | Pre-order Apr 1, Pre-order May 1 | yes | 2 | {Shirt}, {Shoes} |
| In-stock + two pre-orders, same expected date | Shirt, Shoes, Hat | In stock, Pre-order Apr 1, Pre-order Apr 1 | yes | 2 | {Shirt}, {Shoes, Hat} |
| In-stock + two pre-orders, different expected dates | Shirt, Shoes, Hat | In stock, Pre-order Apr 1, Pre-order May 1 | yes | 3 | {Shirt}, {Shoes}, {Hat} |
| All backorder, unknown dates | Shirt, Shoes | Backorder, Backorder | ? | ? | ? |

**Open questions:**
- Are same-ETA pre-orders always grouped? Or is that a customer opt-in?
- Are backorders treated the same as pre-orders for grouping, or is the ETA always unknown and therefore always split?
- Does a pre-order and backorder with the same date group together?

---

### Table 3: Multiple Delivery Addresses

Items destined for different addresses cannot ship together.

| Scenario | Items | Delivery Addresses | Split? | Shipments? | Shipment Groups? |
|---|---|---|---|---|---|
| All items to same address | Shirt, Shoes | Home, Home | no | 1 | {Shirt, Shoes} |
| Two items, two addresses | Shirt, Gift | Home, Friend's address | yes | 2 | {Shirt}, {Gift} |
| Multiple items per address | Shirt, Shoes, Gift, Toy | Home, Home, Friend's address, Friend's address | yes | 2 | {Shirt, Shoes}, {Gift, Toy} |
| Three different addresses | Shirt, Gift 1, Gift 2 | Home, Friend A's address, Friend B's address | yes | 3 | {Shirt}, {Gift 1}, {Gift 2} |

---

### Table 4: Warehouse Assignment — Minimizing Shipments

When no single warehouse holds the full quantity, split across warehouses. Prefer the combination that uses fewest warehouses (shipments), not the greedy largest-first approach.

| Scenario | Item | Qty Needed | Warehouse Stock | Fulfillable? | Shipments? | Assignment? |
|---|---|---|---|---|---|---|
| One warehouse has full stock | Shirt | 6 | Chicago: 6 | yes | 1 | Chicago: 6 |
| One warehouse has all — others also stock it | Shirt | 6 | Chicago: 6, LA: 4, Houston: 3 | yes | 1 | Chicago: 6 |
| Two warehouses, only one combination works | Shirt | 5 | Chicago: 3, LA: 2 | yes | 2 | Chicago: 3, LA: 2 |
| Two warehouses optimal — avoid three | Shirt | 6 | Chicago: 4, LA: 1, Houston: 1, Philadelphia: 3, Washington: 3 | yes | 2 | Philadelphia: 3, Washington: 3 |
| Three warehouses minimum — no two-warehouse option | Shirt | 5 | Chicago: 2, LA: 2, Houston: 1 | yes | 3 | Chicago: 2, LA: 2, Houston: 1 |
| Insufficient total stock | Shirt | 6 | Chicago: 3, LA: 2 | no | — | — |
| Different items, each at a different warehouse | Shirt, Hat | Shirt: 1, Hat: 1 | Chicago: Shirt, LA: Hat | yes | 2 | Chicago: Shirt 1, LA: Hat 1 |
| Different items, one warehouse has both | Shirt, Hat | Shirt: 1, Hat: 1 | Chicago: Shirt and Hat, LA: Hat | yes | 1 | Chicago: Shirt 1, Hat 1 |

---

### Table 5: Companion Product Grouping

Companion items (separate line items that are useless apart) should ship from the same warehouse where possible. When no warehouse stocks both, split is unavoidable.

| Scenario | Items | Companion Pair | Warehouse Stock | Companions Together? | Shipments? |
|---|---|---|---|---|---|
| One warehouse stocks both companions | Camera Body, Camera Lens | Body + Lens | Chicago: Body and Lens | yes | 1 |
| No warehouse stocks both companions | Camera Body, Camera Lens | Body + Lens | Chicago: Body only, LA: Lens only | no | 2 |
| Companions at one warehouse, non-companion at another | Camera Body, Camera Lens, Tripod | Body + Lens | Chicago: Body and Lens, LA: Tripod | yes | 2 |
| Two warehouses stock companions — pick one that also has non-companion | Camera Body, Camera Lens, Tripod | Body + Lens | Chicago: Body and Lens, LA: Body and Lens and Tripod | yes | 1 |
| Companions unavoidably split — non-companion follows one of them | Camera Body, Camera Lens, Tripod | Body + Lens | Chicago: Body and Tripod, LA: Lens | no | 2 |

**Open questions:**
- When companions must split (no warehouse has both), which companion does a non-companion item travel with?
- Can three or more items form a companion group? If so, does "all possible in one warehouse" still apply, or does a partial co-location count?
- If routing companions together would increase the total shipment count, does that constraint override minimisation? Or does minimisation always win?

---

The warehouse assignment table (Table 4) contains the most algorithmic complexity — particularly the "prefer two warehouses over three" rule where a greedy approach would give the wrong answer. That rule and the companion grouping constraint are worth pinning down with a domain expert before implementation begins.