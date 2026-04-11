There are three distinct concerns here worth separating: what triggers the splits, how to pick warehouses to minimise shipment count, and how companion products affect that selection. Here are three tables covering each.

---

## Order Split Groups

| Scenario | Fulfillment Types | Availability | Delivery Addresses | Shipments? | Split Reasons? |
|---|---|---|---|---|---|
| Simple order, all ready | Home Delivery | All in stock | 1 | 1 | |
| All items for store pickup | Store Pickup | All in stock | 1 store | 1 | |
| All items backordered, single destination | Home Delivery | All backordered | 1 | 1 | |
| All items deferred to same address | Home Delivery | All pre-order | 1 | 1 | |
| Pickup and delivery in same order | Home Delivery, Store Pickup | All in stock | 1 home | 2 | Mixed fulfillment methods |
| In-stock and backordered, same destination | Home Delivery | In stock + backordered | 1 | 2 | Availability: in-stock ships immediately, backordered ships on arrival |
| In-stock and pre-order, same destination | Home Delivery | In stock + pre-order | 1 | 2 | Availability: in-stock ships immediately, pre-order ships on release |
| Both deferred but different dates, same destination | Home Delivery | Backordered (week 2) + pre-order (week 6) | 1 | 2 | Availability: different arrival dates |
| Two delivery addresses, all in stock | Home Delivery | All in stock | 2 | 2 | Multiple delivery addresses |
| Two delivery addresses, all deferred | Home Delivery | All backordered | 2 | 2 | Multiple delivery addresses |
| Pickup + delivery + backordered delivery item | Home Delivery, Store Pickup | In stock + backordered | 1 home | 3 | Mixed fulfillment, availability |
| Two addresses + backordered item | Home Delivery | In stock + backordered | 2 | 3 | Multiple addresses, availability |
| All splits in one order | Home Delivery, Store Pickup | In stock + backordered | 2 home, 1 store | 4 | Mixed fulfillment, availability, multiple addresses |

---

## Warehouse Selection — Minimising Shipments

Each row concerns one fulfillment group (same fulfillment type, availability window, and destination) where inventory must be drawn from multiple locations.

| Scenario | Quantity Needed | Warehouse Inventory | Shipments? | Selected Warehouses? |
|---|---|---|---|---|
| Single warehouse fully satisfies | 6 shirts | Chicago: 6 | 1 | Chicago |
| Single warehouse among several that fully satisfies | 6 shirts | Chicago: 6, LA: 3, Houston: 2 | 1 | Chicago |
| Two-warehouse solution (exact) | 6 shirts | Philadelphia: 3, Washington: 3 | 2 | Philadelphia, Washington |
| Two-warehouse solution preferred over three | 6 shirts | Philadelphia: 3, Washington: 3, Chicago: 2, Houston: 2 | 2 | Philadelphia, Washington |
| Larger pair preferred over smaller three | 9 shirts | Atlanta: 5, Boston: 4, Dallas: 3, Denver: 3, Phoenix: 3 | 2 | Atlanta, Boston |
| Three warehouses required — no two-warehouse option | 6 shirts | Chicago: 4, LA: 1, Houston: 1 | 3 | Chicago, LA, Houston |
| Insufficient total stock — cannot fulfill | 6 shirts | Chicago: 3, LA: 2 | unfulfillable | — |

---

## Companion Products — Keeping Items Together

Companions are products that are ordered together and should ship from the same warehouse where possible. The warehouse selection prefers options that keep pairs together, but does not override minimum-shipment selection when companions cannot be co-located.

| Scenario | Companion Pair | Other Items | Warehouse Inventory | Shipments? | Companions Together? |
|---|---|---|---|---|---|
| Both companions at one warehouse | Camera Body + Lens | — | Chicago: Camera Body + Lens | 1 | yes |
| Companions only available at separate warehouses | Camera Body + Lens | — | Chicago: Camera Body only, LA: Lens only | 2 | no — unavoidable |
| One warehouse has both; another has only one | Camera Body + Lens | — | Chicago: Camera Body + Lens, LA: Lens only | 1 | yes — prefer Chicago |
| Both warehouses carry both companions | Camera Body + Lens | — | Chicago: Camera Body + Lens, LA: Camera Body + Lens | 1 | yes — either warehouse |
| Companion pair plus non-companion, all at one warehouse | Camera Body + Lens | Memory Card | Chicago: all three | 1 | yes |
| Non-companion only at a second warehouse | Camera Body + Lens | Memory Card | Chicago: Camera Body + Lens, LA: Memory Card | 2 | yes — companions ship from Chicago |
| Non-companion also stocked at companion warehouse | Camera Body + Lens | Memory Card | Chicago: all three, LA: Memory Card | 1 | yes — ship all from Chicago |
| Two companion pairs, each pair co-located at different warehouse | Body 1 + Lens 1, Body 2 + Lens 2 | — | Chicago: Body 1 + Lens 1, LA: Body 2 + Lens 2 | 2 | yes — each pair ships from its warehouse |
| Two companion pairs, all at one warehouse | Body 1 + Lens 1, Body 2 + Lens 2 | — | Chicago: all four | 1 | yes — both pairs together |

**Open question — companion + availability conflict:** If a Camera Body is in stock but its companion Lens is backordered, should the Camera Body wait and ship with the Lens, or ship immediately at the cost of separating companions? The "if possible" language suggests companions may be separated when availability forces it — but this needs a decision before implementation.

---

A few things to confirm before coding starts:

1. **Availability grouping** — do all backordered items (regardless of their individual arrival dates) ship as one deferred group, or does each distinct arrival date produce its own shipment? The table assumes distinct dates produce distinct shipments — worth confirming.
2. **Store pickup count** — if a customer picks up from two different stores, does that count as two shipments or one order line each? The table treats each store as a separate shipment.
3. **Warehouse tie-breaking** — when multiple warehouse combinations give the same minimum count, is there a secondary rule (e.g., prefer geographically closer, prefer the one also stocking companion items)?