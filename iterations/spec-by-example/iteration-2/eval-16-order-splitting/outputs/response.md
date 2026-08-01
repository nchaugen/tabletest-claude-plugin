I'll map order splitting to three separate concerns, since they're genuinely different decisions: **which items are allowed to share a shipment at all** (a hard eligibility rule), **how to pick warehouses when inventory is scattered** (an optimization), and **how companion products bend the warehouse choice** (a soft preference). Mixing these into one table would force columns that are blank for most rows.

## Table 1: Shipment Grouping Decision

*Can two line items share the same shipment?*

| Scenario | Item A Fulfillment | Item A Destination | Item A Availability | Item B Fulfillment | Item B Destination | Item B Availability | Same Shipment? | Reason? |
|---|---|---|---|---|---|---|---|---|
| Both delivered to the same address, both in stock | Home Delivery | 123 Main St | In Stock | Home Delivery | 123 Main St | In Stock | yes | |
| Pickup and delivery mixed, regardless of availability | Store Pickup, Store #12 | Store #12 | {In Stock, Backorder} | Home Delivery | 123 Main St | {In Stock, Backorder} | no | Different fulfillment method |
| Both pickup, same store | Store Pickup | Store #12 | In Stock | Store Pickup | Store #12 | In Stock | yes | |
| Both pickup, different stores | Store Pickup | Store #12 | In Stock | Store Pickup | Store #7 | In Stock | no | Different pickup location |
| Gift order, two delivery addresses | Home Delivery | 123 Main St | In Stock | Home Delivery | 456 Oak Ave | In Stock | no | Different delivery address |
| One item in stock, one on backorder, same address | Home Delivery | 123 Main St | In Stock | Home Delivery | 123 Main St | Backorder (ETA 2026-09-15) | no | Item not yet available |
| Two backorder items, same expected date | Home Delivery | 123 Main St | Backorder (ETA 2026-09-15) | Home Delivery | 123 Main St | Backorder (ETA 2026-09-15) | yes | |
| Two backorder items, different expected dates | Home Delivery | 123 Main St | Backorder (ETA 2026-09-15) | Home Delivery | 123 Main St | Backorder (ETA 2026-10-01) | no | Different availability dates |
| Pre-order and backorder, same expected date | Home Delivery | 123 Main St | Pre-order (ETA 2026-09-15) | Home Delivery | 123 Main St | Backorder (ETA 2026-09-15) | ? | Open — see below |

## Table 2: Warehouse Allocation to Minimize Shipments

*Given a required quantity and stock scattered across warehouses, which warehouses ship?*

| Scenario | Order Quantity | Stock by Warehouse | Ships From? | Shipment Count? |
|---|---|---|---|---|
| Single warehouse holds the full order | 6 | Chicago: 10, LA: 5, Houston: 0 | Chicago: 6 | 1 |
| Two warehouses cover the order exactly | 6 | Philadelphia: 3, Washington: 3, Chicago: 0 | Philadelphia: 3, Washington: 3 | 2 |
| No two-warehouse combination reaches the quantity | 6 | Chicago: 4, LA: 1, Houston: 1 | Chicago: 4, LA: 1, Houston: 1 | 3 |
| One warehouse is one unit short of covering it alone | 6 | Chicago: 5, LA: 1 | Chicago: 5, LA: 1 | 2 |
| Total stock across all warehouses falls short | 6 | Chicago: 2, LA: 1 | Chicago: 2, LA: 1 (3 backordered) | 2 (+ 1 backorder shipment) |
| Two equally-good two-warehouse combinations exist | 6 | Chicago: 4, LA: 2, Houston: 4 | ? | 2 (tie-break rule open — see below) |
| Two single warehouses could each cover it alone | 6 | Chicago: 6, LA: 6 | ? | 1 (which warehouse is open — see below) |

## Table 3: Companion Product Co-location

*Should companion items (e.g. camera body + matching lens) ship from the same warehouse?*

| Scenario | Companion A Stock by Warehouse | Companion B Stock by Warehouse | Ship Together? | Ships From? | Reason? |
|---|---|---|---|---|---|
| Both companions fully stocked at one shared warehouse | Chicago: 5 | Chicago: 5 | yes | Chicago | Shared warehouse has both |
| Companions only available at different single warehouses | Chicago: 5 | LA: 5 | no | A: Chicago, B: LA | No common warehouse stocks both |
| Companions available together at more than one warehouse | Chicago: 5, LA: 5 | Chicago: 5, LA: 5 | yes | ? | Tie-break rule open — see below |

## Open Questions

These need a decision before implementation — I've made reasonable assumptions elsewhere, but these are genuinely unresolved from the description given:

1. **Pre-order vs. backorder** — are these the same "not yet available" category (so a pre-order and a backorder item with matching ETAs could ship together), or always kept in separate shipments because they're distinct concepts to the customer?
2. **Warehouse tie-breaking** — when multiple warehouse combinations achieve the same minimum shipment count (or a single warehouse could fulfill the order from more than one location), what breaks the tie? Candidates: least excess/leftover stock, warehouse proximity to customer, lowest shipping cost, fixed warehouse priority order.
3. **Companion vs. minimum-shipment precedence** — the spec says companions ship together "if possible." If keeping them together would require *more* shipments than the minimum otherwise achievable (e.g., splitting them would let the rest of the order consolidate onto fewer warehouses), does companion co-location still win, or does shipment-count minimization take priority? Table 3 assumes co-location is checked first among warehouses that satisfy Table 1's hard constraints, but this precedence isn't stated in the requirements.
4. **Partial backorder remainder** — when total stock is insufficient (Table 2, row 5), is the shortfall automatically backordered as a follow-up shipment, or does that require a separate customer decision/notification?

Once these are resolved, each table maps directly to a `@TableTest` (or your framework's table-driven test) — happy to convert them once you confirm the open cells.