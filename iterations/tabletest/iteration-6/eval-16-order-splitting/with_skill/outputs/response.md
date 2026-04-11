The scenarios break into three distinct concerns. I'll build a table for each, then flag the open questions that came up.

---

## Table 1: Order Shipment Grouping

Items get split along three independent dimensions: fulfillment method, delivery address, and availability. Each dimension creates its own shipment boundaries.

| Scenario | Items | Fulfillment | Delivery address | Availability | Shipments? | Shipment breakdown? |
|---|---|---|---|---|---|---|
| Everything ships together | Shirt, Jeans | {Home delivery, Home delivery} | 123 Main St | {In stock, In stock} | 1 | [Shirt + Jeans] |
| Pickup alongside home delivery | Shirt, Lamp | {Home delivery, Pickup} | — | {In stock, In stock} | 2 | [Shirt → home delivery], [Lamp → store pickup] |
| All pickup | Lamp, Vase | {Pickup, Pickup} | — | {In stock, In stock} | 1 | [Lamp + Vase → store] |
| In-stock item with backorder item | Shirt, Jacket | {Home delivery, Home delivery} | 123 Main St | {In stock, Backorder} | 2 | [Shirt → ships now], [Jacket → ships when available] |
| Both items on backorder, same address | Jacket, Coat | {Home delivery, Home delivery} | 123 Main St | {Backorder, Backorder} | ? | Open: together when both ready, or separately when each is ready? |
| Items to different delivery addresses | Shirt, Gift book | {Home delivery, Home delivery} | {123 Main St, 456 Oak Ave} | {In stock, In stock} | 2 | [Shirt → 123 Main], [Gift book → 456 Oak Ave] |
| Pickup + two delivery addresses | Lamp, Shirt, Gift | {Pickup, Home delivery, Home delivery} | {—, 123 Main St, 456 Oak Ave} | {In stock, In stock, In stock} | 3 | [Lamp → pickup], [Shirt → 123 Main], [Gift → 456 Oak Ave] |
| Backorder + address split | Shirt, Jacket, Gift | {Home delivery, Home delivery, Home delivery} | {123 Main, 123 Main, 456 Oak Ave} | {In stock, Backorder, In stock} | 3 | [Shirt → 123 Main, now], [Jacket → 123 Main, later], [Gift → 456 Oak, now] |

---

## Table 2: Warehouse Selection — Minimise Shipments

Once items are grouped into a shipment, this determines which warehouses fulfill it.

| Scenario | Qty needed | Warehouse stock | Shipments? | Warehouses selected? |
|---|---|---|---|---|
| One warehouse covers the full order | 6 shirts | Chicago: 6 | 1 | Chicago |
| One warehouse has exact qty; others are partial | 3 shirts | Chicago: 2, LA: 3 | 1 | LA |
| Fewer warehouses available via a different combination | 6 shirts | Chicago: 4, LA: 1, Houston: 1, Philadelphia: 3, Washington: 3 | 2 | Philadelphia + Washington |
| Two-way split is unavoidable | 4 shirts | Chicago: 2, LA: 2 | 2 | Chicago + LA |
| Total stock is insufficient across all warehouses | 6 shirts | Chicago: 3, LA: 2 | — | Unfulfillable — only 5 available |
| Two equally-minimal combinations exist | 4 shirts | Chicago: 2, LA: 2, Houston: 2, Dallas: 2 | 2 | ? | Open: how to break the tie? |

---

## Table 3: Companion Product Co-location

Companions should ship from the same warehouse *if possible*. This applies on top of the warehouse-selection logic.

| Scenario | Camera body stock | Matching lens stock | Ship together? | Shipments? | From where? |
|---|---|---|---|---|---|
| Both companions at same warehouse | Chicago: 1 | Chicago: 1 | yes | 1 | Chicago |
| One warehouse has both; others have only one | Chicago: 1, LA: 0 | Chicago: 1, LA: 1 | yes | 1 | Chicago — keeps companions together |
| No warehouse stocks both companions | Chicago: 1, LA: 0 | Chicago: 0, LA: 1 | no — unavoidable | 2 | Chicago (body) + LA (lens) |
| Need 2 of each; no single warehouse has both types | Chicago: 2 bodies, 0 lenses; LA: 0 bodies, 2 lenses | — | no — unavoidable | 2 | Chicago (bodies) + LA (lenses) |
| One companion in stock, other on backorder | Chicago: 1 | Backorder | ? | ? | Open: hold in-stock companion or ship immediately? |

---

## Open Questions

These need a decision before the rules can be fully specified:

1. **Multiple backorder items, same address**: do they wait and ship together when *both* are available, or ship independently when *each* becomes available? (Fewer shipments vs faster delivery for the first item.)

2. **Pre-order vs backorder**: you mentioned both — are these treated identically (not available now → separate shipment) or does a pre-order's known release date affect grouping?

3. **Warehouse selection tiebreaker**: when two equally-minimal warehouse combinations exist, what decides? Shipping cost, proximity to the customer, or something else?

4. **Companion + backorder**: if one companion is in stock and the other is on backorder, does the in-stock item wait? This interacts directly with questions 1 and 2.

5. **Companion + warehouse minimisation conflict**: if keeping companions together requires *more* shipments than splitting them, which constraint wins?