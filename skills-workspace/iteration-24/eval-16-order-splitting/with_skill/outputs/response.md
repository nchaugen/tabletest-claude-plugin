Five distinct concerns here — I'll build a table for each. They have different input shapes, so they work better as separate specs than one big table.

---

## Table 1: Fulfillment Method Split

The rule: items requiring home delivery and items for store pickup cannot share a shipment. Items with the same fulfillment method group together.

| Scenario | Items in order | Shipments? | Shipment groups? |
|---|---|---|---|
| All home delivery | Shirt [Home Delivery], Pants [Home Delivery] | 1 | Shirt + Pants |
| All store pickup | Book [Store Pickup], Mug [Store Pickup] | 1 | Book + Mug (pickup) |
| One delivery, one pickup | Shirt [Home Delivery], Book [Store Pickup] | 2 | Shirt (delivered); Book (pickup) |
| Multiple delivery + one pickup | Shirt [Home Delivery], Pants [Home Delivery], Book [Store Pickup] | 2 | Shirt + Pants (delivered); Book (pickup) |
| Multiple delivery + multiple pickup | Shirt [Home Delivery], Pants [Home Delivery], Book [Store Pickup], Mug [Store Pickup] | 2 | Shirt + Pants (delivered); Book + Mug (pickup) |

**Open question:** If a customer selects store pickup at two different store locations, are those counted as one shipment or two?

---

## Table 2: Availability Split

The rule: in-stock items ship immediately; pre-orders and backorders ship when they become available. Items available at the same time can be grouped.

| Scenario | Items in order | Shipments? | First shipment? | Second shipment? |
|---|---|---|---|---|
| All in stock | Shirt [In Stock], Pants [In Stock] | 1 | Shirt + Pants (immediately) | — |
| All pre-order, same date | Camera [Pre-order: 2026-06-15], Lens [Pre-order: 2026-06-15] | 1 | Camera + Lens (2026-06-15) | — |
| In stock + pre-order | Shirt [In Stock], Camera [Pre-order: 2026-06-15] | 2 | Shirt (immediately) | Camera (2026-06-15) |
| In stock + backordered | Shirt [In Stock], Boots [Backordered: ~3 weeks] | 2 | Shirt (immediately) | Boots (~3 weeks) |
| Pre-orders with different dates | Camera [Pre-order: 2026-06-15], Tripod [Pre-order: 2026-07-01] | 2 | Camera (2026-06-15) | Tripod (2026-07-01) |
| Pre-orders with same date | Camera [Pre-order: 2026-06-15], Bag [Pre-order: 2026-06-15] | 1 | Camera + Bag (2026-06-15) | — |

**Open question:** Do pre-order items with different dates always split, or is there a window (e.g. within 3 days of each other) where they'd be held and grouped?

---

## Table 3: Warehouse Inventory Split

The rule: when no single warehouse can fulfill the full quantity, split across the fewest warehouses possible. When multiple combinations cover the quantity, prefer the one with fewer warehouses (= fewer shipments).

| Scenario | Item | Qty ordered | Warehouse stock | Shipments? | Warehouses used? |
|---|---|---|---|---|---|
| Single warehouse covers order | Blue Shirt (L) | 6 | NYC: 10 | 1 | NYC |
| Single warehouse, exact stock | Blue Shirt (L) | 6 | NYC: 6 | 1 | NYC |
| Two warehouses needed, even split | Blue Shirt (L) | 6 | Philadelphia: 3, Washington: 3 | 2 | Philadelphia + Washington |
| Three warehouses needed (no 2-warehouse option) | Blue Shirt (L) | 6 | Chicago: 4, LA: 1, Houston: 1 | 3 | Chicago + LA + Houston |
| Prefer 2-warehouse solution over 3 | Blue Shirt (L) | 6 | Chicago: 4, LA: 1, Houston: 1, Philadelphia: 3, Washington: 3 | 2 | Philadelphia + Washington |
| Single unit, multiple warehouses available | Blue Shirt (L) | 1 | NYC: 5, LA: 3 | 1 | NYC or LA |
| Insufficient total stock across all warehouses | Blue Shirt (L) | 10 | NYC: 4, LA: 3 | — | Unfulfillable (7 available, 10 needed) |

**Open question:** When multiple minimum-shipment solutions tie (e.g. two different pairs of warehouses that each cover the quantity in 2 shipments), is there a tiebreaker — closest to the customer, lowest shipping cost, or arbitrary?

---

## Table 4: Delivery Address Split

The rule: items going to different addresses cannot share a shipment. Items going to the same address are grouped together.

| Scenario | Items and addresses | Shipments? | Shipment groups? |
|---|---|---|---|
| All same address | Shirt [123 Main St], Pants [123 Main St] | 1 | Shirt + Pants → 123 Main St |
| Two different addresses | Shirt [123 Main St], Gift Box [456 Oak Ave] | 2 | Shirt → 123 Main St; Gift Box → 456 Oak Ave |
| Multiple items per address | Shirt + Pants [123 Main St], Gift + Card [456 Oak Ave] | 2 | Shirt + Pants → 123 Main St; Gift + Card → 456 Oak Ave |
| Three different addresses | Shirt [123 Main St], Book [456 Oak Ave], Mug [789 Elm Rd] | 3 | One shipment per address |
| Same address, different fulfillment methods | Shirt [123 Main St, Home Delivery], Book [123 Main St, Store Pickup] | 2 | Fulfillment method split still applies within same address |

---

## Table 5: Companion Product Grouping

The rule: companion items (separate SKUs that are useless without each other) should ship from the same warehouse if any warehouse stocks both. If no single warehouse has the complete pair, split is unavoidable.

| Scenario | Items in order | Companion pair | Warehouse stock | Companions together? | Shipments? |
|---|---|---|---|---|---|
| One warehouse has both companions | Camera body, Matching lens | Camera body ↔ Matching lens | NYC: camera body (1), lens (1) | yes | 1 |
| Companions + unrelated item, all at one warehouse | Camera body, Matching lens, SD Card | Camera body ↔ Matching lens | NYC: camera body (1), lens (1), SD card (1) | yes | 1 |
| Companions at one warehouse, unrelated item elsewhere | Camera body, Matching lens, SD Card | Camera body ↔ Matching lens | NYC: camera body (1), lens (1); LA: SD card (1) | yes | 2 |
| No warehouse has both companions | Camera body, Matching lens | Camera body ↔ Matching lens | NYC: camera body only; LA: lens only | no (unavoidable) | 2 |
| Two companion pairs, both at one warehouse | Camera body, Lens, Tripod, Tripod head | Camera body ↔ Lens; Tripod ↔ Tripod head | NYC: all four | yes (both pairs) | 1 |
| Non-companion items, inventory split needed | Shirt, Jeans | none | NYC: shirt only; LA: jeans only | N/A | 2 |

**Open question:** If keeping companions together requires *more* shipments than separating them, which wins — companion preference or shipment minimization? The description says "if possible" but doesn't define how hard to try.

---

## Interactions to resolve

A few combinations that may need their own rows or tables once the basic rules are confirmed:

- **Companion + availability split:** One companion is in stock, the other is pre-ordered. Should both be held until the pre-order is available, or does availability split override companion grouping?
- **Companion + address split:** Companions ordered to different addresses — grouping is impossible by definition, so this is probably fine to state as "companion preference doesn't apply across addresses."
- **Multiple split triggers on one order:** E.g. an order with mixed fulfillment methods *and* inventory split needed. Do the rules compose independently (fulfillment method first, then inventory within each group)?

These interactions are worth confirming before implementation starts, as they determine whether the split logic is a single pass or a pipeline of rules applied in sequence.