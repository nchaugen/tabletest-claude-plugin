Here are five tables covering the distinct split reasons, followed by open questions worth resolving before implementation begins.

---

## Fulfillment Method Split

*When does mixed fulfillment force a split, and how are items grouped?*

| Scenario | Items | Fulfillment Methods | Shipments? | Grouped As? |
|---|---|---|---|---|
| All home delivery | Shirt, Trousers | Home delivery, Home delivery | 1 | [Shirt, Trousers] |
| All store pickup | Shirt, Mug | Store pickup, Store pickup | 1 | [Shirt, Mug] |
| One delivery, one pickup | Shirt, Mug | Home delivery, Store pickup | 2 | [Shirt], [Mug — pickup] |
| Two delivery, one pickup | Shirt, Trousers, Mug | Home delivery, Home delivery, Store pickup | 2 | [Shirt, Trousers], [Mug — pickup] |

---

## Availability-Based Timing Split

*When do in-stock and pre-order/backorder items ship separately?*

| Scenario | Items | Availability | Shipments? | Dispatch Timing? |
|---|---|---|---|---|
| All items in stock | Chair, Lamp | In stock, In stock | 1 | Immediately |
| All pre-ordered, same date | Chair, Lamp | Pre-order 2026-05-01, Pre-order 2026-05-01 | 1 | 2026-05-01 |
| Two pre-orders, different dates | Chair, Lamp | Pre-order 2026-04-15, Pre-order 2026-05-01 | 2 | Chair 2026-04-15; Lamp 2026-05-01 |
| In-stock and pre-order | Chair, Lamp | In stock, Pre-order 2026-05-01 | 2 | Chair immediately; Lamp 2026-05-01 |
| In-stock and backorder | Chair, Lamp | In stock, Backorder | 2 | Chair immediately; Lamp when restocked |

---

## Warehouse Inventory Allocation

*How many shipments are needed, and does the system pick the minimum?*

| Scenario | Item | Qty Ordered | Available Warehouses | Min Shipments? | Fulfillable? |
|---|---|---|---|---|---|
| One warehouse covers full order | Shirt | 6 | Chicago: 6 | 1 | yes |
| Two warehouses together cover order | Shirt | 6 | Chicago: 3, LA: 3 | 2 | yes |
| One warehouse overshoots — not needed | Shirt | 4 | Chicago: 4, LA: 2 | 1 | yes — Chicago alone |
| Two-warehouse combo preferred over three | Shirt | 6 | Chicago: 4, LA: 1, Houston: 1, Philly: 3, Washington: 3 | 2 | yes — e.g. Philly + Washington |
| Only a three-warehouse combination covers order | Shirt | 6 | Chicago: 4, LA: 1, Houston: 1 | 3 | yes |
| Total stock below order quantity | Shirt | 6 | Chicago: 3, LA: 2 | — | no |

---

## Multi-Address Split

*Items to different destinations always split; items to the same destination group together.*

| Scenario | Items | Delivery Addresses | Shipments? | Grouped As? |
|---|---|---|---|---|
| All items to same address | Shirt, Book | Home, Home | 1 | [Shirt, Book → Home] |
| Two items to different addresses | Shirt, Book | Friend's house, Home | 2 | [Shirt → Friend's], [Book → Home] |
| Three items, two distinct addresses | Shirt, Pen, Book | Friend's house, Home, Friend's house | 2 | [Shirt, Book → Friend's], [Pen → Home] |
| Multiple items per address | Shirt, Trousers, Book, Pen | Home, Home, Friend's house, Friend's house | 2 | [Shirt, Trousers → Home], [Book, Pen → Friend's] |

---

## Companion Product Constraint

*Companion items ship together when at all possible; a split is only accepted when unavoidable.*

| Scenario | Items | Companion Pair | Warehouse Stock | Shipments? | Companions Same Shipment? |
|---|---|---|---|---|---|
| Both companions stocked at one warehouse | Camera body, Lens | Camera body + Lens | NYC: camera body + lens | 1 | yes |
| Companion warehouse preferred when a closer split exists | Camera body, Lens | Camera body + Lens | NYC: camera body + lens, Chicago: camera body | 1 | yes — both from NYC |
| Companions with an independent extra item | Camera body, Lens, Bag | Camera body + Lens | NYC: camera body + lens, Chicago: bag | 2 | yes — companions together in one shipment |
| No single warehouse stocks both companions | Camera body, Lens | Camera body + Lens | NYC: camera body, Chicago: lens | 2 | no — unavoidable |
| Non-companion items at separate warehouses | Camera body, Memory card | none | NYC: camera body, Chicago: memory card | 2 | N/A |

---

## Open Questions

These cases aren't covered by the tables above and need a decision before implementation:

1. **Companions forced to split** — is the order accepted with a warning to the customer, silently fulfilled across shipments, or held/rejected?

2. **Companion + address conflict** — if two companion items are going to different addresses, does the address split override the companion constraint, or is this a validation error at cart time?

3. **Compound split reasons** — when an order has both an availability split and an address split (e.g. an in-stock gift and a pre-ordered item for home), how is the grouping resolved? Address-first, then availability within each address group? The tables above treat each reason independently.

4. **Multiple pickup locations** — if a customer selects store pickup at two different stores, does each store become its own shipment (as the address table implies), or is this blocked at cart time?

5. **Companion + inventory allocation** — if keeping companions together requires using more warehouses (more shipments) than the minimum, does the companion rule take priority over minimisation? The warehouse table doesn't reflect this trade-off yet.