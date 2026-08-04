Here's the decomposition. "Splits shipments by fulfillment method **and** availability **and** address **and** warehouse, while keeping companions together" fails the "name it without 'and'" test — it's five independent rules, not one. I've given each its own table, and used the companion table to show how it defers to the others when they conflict.

**Assumptions** (stated because this is a non-interactive run):
- A pickup selection is store-specific; two items picked up at different stores can't share a "shipment" (pickup event) any more than two different delivery addresses can.
- Availability grouping is keyed by expected ship date, not just the coarse status — two backordered items with different restock dates still split.
- Companion affinity is a *preference* that only activates as a tie-break inside warehouse selection; it never overrides fulfillment method, address, or availability splits (those are harder constraints).
- Warehouse selection minimizes shipment count by warehouse *count*, not other cost factors — see open question on tie-breaking.

---

### 1. Splits shipments across fulfillment methods

| Scenario | Item 1 Fulfillment Method | Item 2 Fulfillment Method | Same Shipment? |
|---|---|---|---|
| Both items for home delivery | Delivery | Delivery | yes |
| Both items for store pickup | Pickup | Pickup | yes |
| One item for pickup, one for delivery | Pickup | Delivery | no |

### 2. Splits shipments across fulfillment locations

| Scenario | Fulfillment Method | Item 1 Location | Item 2 Location | Same Shipment? |
|---|---|---|---|---|
| Both delivered to the same address | Delivery | 123 Main St | 123 Main St | yes |
| Delivered to different addresses (gift + self) | Delivery | 123 Main St | 45 Oak Ave | no |
| Both picked up at the same store | Pickup | Store #12 | Store #12 | yes |
| Picked up at different stores | Pickup | Store #12 | Store #45 | no |

### 3. Splits shipments across availability windows

| Scenario | Item 1 Availability | Item 1 Expected Ship Date | Item 2 Availability | Item 2 Expected Ship Date | Same Shipment? |
|---|---|---|---|---|---|
| Both items in stock | In Stock | — | In Stock | — | yes |
| One in stock, one backordered | In Stock | — | Backorder | 2026-09-15 | no |
| One in stock, one pre-order | In Stock | — | Pre-order | 2026-10-01 | no |
| Both backordered, same expected date | Backorder | 2026-09-15 | Backorder | 2026-09-15 | yes |
| Both backordered, different expected dates | Backorder | 2026-09-15 | Backorder | 2026-10-20 | no |

*(— means the item ships immediately; no future date applies.)*

### 4. Allocates warehouses to a single item's quantity, minimizing shipment count

| Scenario | Order Qty | Warehouse Stock | Allocation? | Shipments? | Backordered Qty? |
|---|---|---|---|---|---|
| One warehouse fully covers the order | 6 | Denver:6, Chicago:4, LA:1, Houston:1 | Denver:6 | 1 | 0 |
| A 2-warehouse combo beats an available 3-warehouse combo | 6 | Philadelphia:3, Washington:3, Chicago:4, LA:1, Houston:1 | Philadelphia:3, Washington:3 | 2 | 0 |
| No 1- or 2-warehouse combo exists, only a 3-way combo | 6 | Chicago:4, LA:1, Houston:1 | Chicago:4, LA:1, Houston:1 | 3 | 0 |
| Combined stock can't cover the order | 6 | Chicago:2, LA:1 | Chicago:2, LA:1 | 2 | 3 |
| Two equally-minimal combos exist | 6 | Philadelphia:3, Washington:3, Atlanta:4, Miami:2 | ? | 2 | 0 |

*The remainder in row 4 becomes its own backorder shipment once restocked, per Table 3's rule — this table only allocates what's available now.*

### 5. Keeps companion items together when a shared warehouse exists

| Scenario | Item A Fulfillment | Item A Availability | Item A Stock | Item B Fulfillment | Item B Availability | Item B Stock | Same Shipment? | Shipped From? |
|---|---|---|---|---|---|---|---|---|
| Shared warehouse stocks both companions | Delivery | In Stock | Chicago:2, LA:3 | Delivery | In Stock | Chicago:1 | yes | Chicago |
| No warehouse stocks both companions | Delivery | In Stock | Chicago:2 | Delivery | In Stock | LA:1 | no | Chicago (A) / LA (B) |
| Companions share stock at multiple warehouses | Delivery | In Stock | Chicago:2, Houston:1 | Delivery | In Stock | Chicago:1, Houston:1 | yes | Chicago or Houston (per Table 4 tie-break) |
| Companions requested with different fulfillment methods | Pickup | In Stock | Chicago:2 | Delivery | In Stock | Chicago:1 | no | Chicago (A) / Chicago (B) — still split, method wins |
| One companion is backordered | Delivery | In Stock | Chicago:2 | Delivery | Backorder | Chicago:0, expected 2026-09-20 | no | Chicago (A now) / Chicago (B later) — still split, availability wins |

---

**Open questions:**
1. Table 4, row 5 — what breaks a tie between equally-minimal warehouse combos (proximity to delivery address, shipping cost, warehouse priority list)?
2. Can a companion item be *partially* split across warehouses just to co-locate one unit with its companion, when that companion item alone wouldn't need splitting? (E.g., body needs 3 units, available whole at Warehouse A, but splitting 1 unit off to Warehouse B would co-locate with the lens.) Not covered above — needs a business decision before implementation.
3. Is "ship together" for companions ever a hard requirement (reject/delay the order rather than split), or is it always best-effort as described?

These tables are ready to hand to `/tabletest` for `@TableTest` conversion once you confirm the assumptions and resolve the open questions.