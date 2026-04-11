Looking at the scenarios you've described, there are three distinct concerns worth separating: **what forces items into different shipments**, **how to assign warehouses when a quantity spans locations**, and **how to handle companion products**. Let me draft each as an example table.

---

## Table 1: Item-Level Split Rules

*When do two order lines go into separate shipments?*

| Scenario | Item A Fulfillment | Item B Fulfillment | Item A Destination | Item B Destination | Item A Availability | Item B Availability | Same Shipment? | Split Reason? |
|---|---|---|---|---|---|---|---|---|
| Both home delivery, same address, in stock | Home Delivery | Home Delivery | 1 Main St | 1 Main St | In Stock | In Stock | yes | |
| Both store pickup at same store | Store Pickup | Store Pickup | Store #5 | Store #5 | In Stock | In Stock | yes | |
| Store pickup mixed with home delivery | Store Pickup | Home Delivery | Store #5 | 1 Main St | In Stock | In Stock | no | Different fulfillment methods |
| Store pickup at different stores | Store Pickup | Store Pickup | Store #5 | Store #12 | In Stock | In Stock | no | Different pickup locations |
| Home delivery to different addresses | Home Delivery | Home Delivery | 1 Main St | 99 Oak Ave | In Stock | In Stock | no | Different delivery addresses |
| In-stock item with backordered item | Home Delivery | Home Delivery | 1 Main St | 1 Main St | In Stock | Backordered | no | Different availability |
| In-stock item with pre-ordered item | Home Delivery | Home Delivery | 1 Main St | 1 Main St | In Stock | Pre-order | no | Different availability |
| Both items backordered | Home Delivery | Home Delivery | 1 Main St | 1 Main St | Backordered | Backordered | yes | |
| Two backordered items with different estimated dates | Home Delivery | Home Delivery | 1 Main St | 1 Main St | Backordered (2 weeks) | Backordered (6 weeks) | ? | **Open: do different ETA dates force a split?** |
| Multiple triggers present | Store Pickup | Home Delivery | Store #5 | 99 Oak Ave | In Stock | Backordered | no | Multiple reasons |

---

## Table 2: Warehouse Assignment — Minimising Shipments

*When a quantity can't come from a single location, which warehouses are used?*

| Scenario | Item | Qty Ordered | Chicago Stock | Philadelphia Stock | Washington Stock | Los Angeles Stock | Houston Stock | Shipments? | Warehouse Assignment? |
|---|---|---|---|---|---|---|---|---|---|
| Single warehouse covers full order | Blue Shirt (M) | 4 | 6 | 3 | — | — | — | 1 | Chicago: 4 |
| Two warehouses needed, only one combination | Blue Shirt (M) | 6 | 4 | 2 | — | — | — | 2 | Chicago: 4, Philadelphia: 2 |
| Three warehouses unavoidable | Blue Shirt (M) | 6 | 4 | — | — | 1 | 1 | 3 | Chicago: 4, Los Angeles: 1, Houston: 1 |
| Two-warehouse option available alongside three-warehouse option | Blue Shirt (M) | 6 | 4 | 3 | 3 | 1 | 1 | 2 | Philadelphia: 3, Washington: 3 |
| Multiple pairs of warehouses each satisfy minimum | Blue Shirt (M) | 6 | — | 3 | 3 | 4 | — | 1 | Los Angeles: 6 |
| Total stock falls short across all warehouses | Blue Shirt (M) | 10 | 4 | 3 | — | 1 | 1 | unfulfillable | |

**Open questions:**
- When multiple minimum-shipment assignments tie (e.g. Chicago+Philadelphia vs Chicago+Washington), what breaks the tie — cost, proximity, or arbitrary?
- Multi-SKU orders: if the order has 4 shirts *and* 3 jackets, is warehouse assignment solved per SKU independently, or jointly to reduce total shipment count further?

---

## Table 3: Companion Product Co-Shipping

*When items are companions, do they ship together, and from where?*

| Scenario | Item A | Item B | Companion Pair | Warehouse with Both | Warehouse with A Only | Warehouse with B Only | Shipped Together? | Assigned To? |
|---|---|---|---|---|---|---|---|---|
| Non-companions at different warehouses | T-shirt | Jeans | no | — | Chicago | New York | no | Chicago, New York |
| Companions, single shared warehouse | Camera X Body | Camera X Lens | yes | Chicago | — | — | yes | Chicago |
| Companions: no warehouse has both | Camera X Body | Camera X Lens | yes | — | Chicago | New York | no | Chicago, New York (forced split) |
| Companions: co-ship warehouse exists but isn't nearest for A | Camera X Body | Camera X Lens | yes | Chicago | Dallas | — | yes | Chicago (not Dallas) |
| Companion item in stock, partner backordered | Camera X Body | Camera X Lens | yes | — | Chicago (in stock) | Chicago (backordered) | ? | **Open: hold body until lens available, or ship separately?** |
| Two independent companion pairs in same order | Camera X Body + Camera X Lens | Tripod + Ball Head | yes | Chicago (camera pair), New York (tripod pair) | — | — | yes (within each pair) | Chicago: camera pair, New York: tripod pair |

---

## Things still to resolve

A few gaps surfaced while working through these:

1. **Backordered items with different ETAs** (Table 1): is the split trigger "any difference in availability status" or "difference in expected ship date"? Two items both backordered but three weeks apart might still warrant separate shipments.

2. **Tie-breaking in warehouse assignment** (Table 2): when multiple assignments achieve the same minimum shipment count, you'll need a tiebreaker rule (shipping cost, carbon footprint, warehouse proximity) — otherwise the implementation will be non-deterministic.

3. **Companion + availability conflict** (Table 3): if a companion item is in stock but its partner is backordered, holding both until the backorder arrives ships them together but delays the in-stock item. This is a customer-visible tradeoff worth an explicit policy decision.

4. **Joint warehouse assignment across SKUs** (Table 2): the shirt-assignment table treats one SKU in isolation. When multiple SKUs in the same shipment group each need warehouse splitting, the problem becomes joint optimisation — worth flagging before implementation starts.

Want to work through any of these open questions now, or does this give enough to move toward implementation?