Here are three spec tables covering the scenarios you described. I've separated them by concern — what forces a split, how to choose warehouses, and how companions affect that choice.

---

## Table 1: Order Split Triggers

_When do items land in separate shipments?_

| Scenario | Fulfillment methods | Availability windows | Delivery addresses | Shipments? | Split reason? |
|---|---|---|---|---|---|
| All same conditions | Delivery, Delivery | In stock, In stock | 123 Main St, 123 Main St | 1 | |
| Pickup mixed with delivery | Pickup (Store A), Delivery | In stock, In stock | —, 123 Main St | 2 | Different fulfillment methods |
| Two pickup items, same store | Pickup (Store A), Pickup (Store A) | In stock, In stock | —, — | 1 | |
| Two pickup items, different stores | Pickup (Store A), Pickup (Store B) | In stock, In stock | —, — | 2 | Different pickup locations |
| In-stock with pre-order | Delivery, Delivery | In stock, Pre-order Apr 1 | 123 Main St, 123 Main St | 2 | Different availability windows |
| In-stock with backorder | Delivery, Delivery | In stock, Backorder | 123 Main St, 123 Main St | 2 | Different availability windows |
| Two pre-orders, same release date | Delivery, Delivery | Pre-order Apr 1, Pre-order Apr 1 | 123 Main St, 123 Main St | 1 | Same availability window |
| Two pre-orders, different release dates | Delivery, Delivery | Pre-order Mar 15, Pre-order Apr 1 | 123 Main St, 123 Main St | 2 | Different availability windows |
| Pre-order and backorder | Delivery, Delivery | Pre-order Apr 1, Backorder | 123 Main St, 123 Main St | ? | Open: if backorder ETA overlaps pre-order release date, do they group? |
| Different delivery addresses | Delivery, Delivery | In stock, In stock | 123 Main St, 456 Oak Ave | 2 | Different delivery addresses |
| All three triggers: pickup + in-stock delivery + pre-order delivery to separate address | Pickup (Store A), Delivery, Delivery | In stock, In stock, Pre-order Apr 1 | —, 123 Main St, 456 Oak Ave | 3 | Each trigger generates its own shipment independently |

---

## Table 2: Warehouse Selection

_Given a delivery group, which warehouses fulfill it using the fewest shipments?_

The item is "Blue shirt" throughout; stock figures show units per warehouse.

| Scenario | Quantity ordered | Warehouse stock | Shipments? | Warehouse allocation? |
|---|---|---|---|---|
| Single warehouse has more than enough | 6 | Chicago: 8, LA: 3, Houston: 2 | 1 | Chicago: 6 |
| Single warehouse has exactly enough | 6 | Chicago: 6, LA: 4 | 1 | Chicago: 6 |
| No single warehouse sufficient; one two-warehouse solution | 6 | Chicago: 4, LA: 2 | 2 | Chicago: 4, LA: 2 |
| Multiple two-warehouse solutions exist; prefer over three | 6 | Chicago: 4, Philadelphia: 3, Washington: 3, LA: 1, Houston: 1 | 2 | Philadelphia: 3, Washington: 3 (or any valid two-warehouse pair — open: tie-breaking rule between Chicago+Philadelphia, Chicago+Washington, Philadelphia+Washington?) |
| No two-warehouse solution; three needed | 6 | Chicago: 3, LA: 2, Houston: 1 | 3 | Chicago: 3, LA: 2, Houston: 1 |
| Total stock across all warehouses insufficient | 6 | Chicago: 2, LA: 2, Houston: 1 | Unfulfillable | — |
| Multiple items; one warehouse covers all | Shirt × 1, Shoes × 1 | Chicago: Shirt + Shoes / LA: Shirt only | 1 | Chicago: Shirt + Shoes |
| Multiple items; no warehouse covers all | Shirt × 1, Shoes × 1 | Chicago: Shirt only / LA: Shoes only | 2 | Chicago: Shirt, LA: Shoes |

---

## Table 3: Companion Product Grouping

_When items are companions (e.g., camera body + matching lens), can they ship from the same warehouse?_

| Scenario | Items | Companion group | Warehouse stock | Companions in same shipment? | Allocation? |
|---|---|---|---|---|---|
| One warehouse stocks both companions | Camera body + Lens | Camera set | Chicago: Camera ✓ Lens ✓ / LA: Camera ✗ Lens ✗ | yes | Chicago: both |
| Both warehouses stock companions; minimize shipments | Camera body + Lens | Camera set | Chicago: Camera ✓ Lens ✓ / LA: Camera ✓ Lens ✓ | yes | Either warehouse; 1 shipment either way |
| Only one warehouse has the companion partner; prefer it | Camera body + Lens | Camera set | Chicago: Camera ✓ Lens ✓ / LA: Camera ✓ Lens ✗ | yes | Chicago (keeps companions together); avoid LA split |
| No warehouse stocks both; companions must split | Camera body + Lens | Camera set | Chicago: Camera ✓ Lens ✗ / LA: Camera ✗ Lens ✓ | no (unavoidable) | Chicago: Camera, LA: Lens |
| Companion pair + unrelated item; all at one warehouse | Camera body + Lens + Tripod | Camera+Lens grouped | Chicago: Camera ✓ Lens ✓ Tripod ✓ | yes | Chicago: all three |
| Companion pair + unrelated item; unrelated item elsewhere | Camera body + Lens + Tripod | Camera+Lens grouped | Chicago: Camera ✓ Lens ✓ Tripod ✗ / Houston: Tripod ✓ | yes | Chicago: Camera + Lens, Houston: Tripod |
| Companions with different delivery addresses | Camera body (self) + Lens (gift) | Camera set | Chicago: Camera ✓ Lens ✓ | no (address split overrides) | Chicago: Camera → 123 Main St, Chicago: Lens → 456 Oak Ave |
| Companion preference vs. shipment minimisation | Camera body + Lens + Shirt | Camera+Lens grouped | Chicago: Camera ✓ Lens ✗ Shirt ✓ / LA: Camera ✗ Lens ✓ Shirt ✓ | ? | Open: does keeping companions together take priority if it doesn't increase shipment count? What if it would increase the count? |

---

**Open questions to resolve before implementation:**

1. **Pre-order + backorder grouping** — if a backorder has an estimated availability date matching a pre-order release date, do they ship together, or are they always separate categories?
2. **Warehouse tie-breaking** — when multiple combinations meet the minimum warehouse count, which do we choose? (E.g. does balance of allocation matter, or is any minimum solution acceptable?)
3. **Companion vs. minimisation trade-off** — if keeping companions together requires more shipments than splitting them, which wins? The current description says "if possible" for companions, suggesting minimisation takes priority — worth confirming.