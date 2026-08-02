Here's the mapped-out set of example tables, organized as one table per independent splitting rule, plus a combining table for precedence. I made a few explicit assumptions where the rules were ambiguous — flagged inline and summarized at the end.

## Model

A shipment groups items by a **key**: `(Fulfillment Type, Delivery Address or Store, Availability Group)`. Items with different keys can never share a shipment. Within one key, the ordered quantity is sourced from one or more warehouses, and each distinct warehouse used produces its own shipment — minimized in count, with companion items bundled onto the same warehouse when that's achievable without exceeding the minimum.

---

### 1. Splits Shipments Between Store Pickup and Home Delivery

*Held constant: all items in stock, single address, single warehouse.*

| Scenario | Line Items (Item: Fulfillment Type) | Shipments? |
|---|---|---|
| All items requested for home delivery | Shirt: Delivery, Mug: Delivery, Hat: Delivery | {Delivery: Shirt, Mug, Hat} |
| All items requested for store pickup | Shirt: Pickup, Mug: Pickup | {Pickup: Shirt, Mug} |
| Order mixes pickup and delivery items | Shirt: Pickup, Mug: Pickup, Hat: Delivery | {Pickup: Shirt, Mug}, {Delivery: Hat} |

---

### 2. Delays Shipment Until Items Are Available to Ship

*Held constant: all items delivery, same address, single warehouse.*

| Scenario | Line Items (Item: Availability, Expected Ship Date) | Shipments? |
|---|---|---|
| All items in stock | Shirt: In Stock, Mug: In Stock | {Now: Shirt, Mug} |
| One item in stock, one on pre-order | Shirt: In Stock, Camera: Pre-order (ETA 2026-09-01) | {Now: Shirt}, {2026-09-01: Camera} |
| Two backordered items have different expected dates | Camera: Backorder (ETA 2026-09-01), Lens: Backorder (ETA 2026-09-15) | {2026-09-01: Camera}, {2026-09-15: Lens} |
| Two backordered items share the same expected date | Tripod: Backorder (ETA 2026-09-15), Lens: Backorder (ETA 2026-09-15) | {2026-09-15: Tripod, Lens} |

---

### 3. Splits Shipments Across Differing Delivery Addresses

*Held constant: all items delivery, all in stock, single warehouse.*

| Scenario | Line Items (Item: Delivery Address) | Shipments? |
|---|---|---|
| All items go to the same address | Shirt: 12 Main St, Mug: 12 Main St | {12 Main St: Shirt, Mug} |
| One item is a gift sent to a different address | Shirt: 12 Main St, Mug: 12 Main St, Watch: 5 Oak Ave | {12 Main St: Shirt, Mug}, {5 Oak Ave: Watch} |

*Open question: whether address matching is exact-string or normalized/geocoded (e.g., "12 Main St" vs "12 Main Street" vs a mistyped ZIP) — not specified.*

---

### 4. Minimises the Number of Warehouses Used to Fulfil an Order

*Held constant: fulfillment=Delivery, address=12 Main St, all items in stock. Assumption: ties between equally-minimal warehouse combinations are broken by lowest total shipping distance to the delivery address.*

| Scenario | Item & Qty Ordered | Warehouse Stock: Distance/Qty | Minimum Warehouses Needed? | Shipments? |
|---|---|---|---|---|
| A single warehouse holds enough stock for the whole order | Shirt ×6 | Chicago: 50mi/10, LA: 1800mi/5 | 1 | {Chicago: Shirt×6} |
| Order quantity requires drawing from more than one warehouse | Shirt ×6 | Chicago: 50mi/4, Denver: 900mi/2 | 2 | {Chicago: Shirt×4, Denver: Shirt×2} |
| A two-warehouse combination exists alongside a three-warehouse alternative | Shirt ×6 | Chicago: 50mi/4, LA: 1800mi/1, Houston: 1000mi/1, Philadelphia: 700mi/3, Washington: 750mi/3 | 2 | {Philadelphia: Shirt×3, Washington: Shirt×3} |
| Two warehouse combinations tie on warehouse count | Shirt ×6 | Atlanta: 300mi/3, Denver: 900mi/3, Miami: 1200mi/3, Chicago: 1500mi/3 | 2 | {Atlanta: Shirt×3, Denver: Shirt×3} |
| Combined stock across every warehouse is less than the order quantity | Shirt ×6 | Chicago: 50mi/4 (only source) | 1 (partial) | {Chicago: Shirt×4 (now)}, {Backorder (ETA unknown): Shirt×2} |

*Assumption: when total network stock can't cover the order, the fulfillable portion ships now and the shortfall becomes its own backorder shipment group with no ETA yet. This wasn't specified and should be confirmed — an alternative would be to reject the whole line item instead of partially shipping.*

---

### 5. Bundles Companion Line Items Into the Same Shipment When Feasible

*Held constant: quantities are 1 unit each unless noted; companions are Body + Lens.*

| Scenario | Companion Items (Fulfillment, Address, Availability, Warehouse Stock) | Constraint Blocking Bundling? | Shipments? |
|---|---|---|---|
| Both companions are fully stocked at one shared warehouse | Body: Delivery, 12 Main St, In Stock, Chicago(3); Lens: Delivery, 12 Main St, In Stock, Chicago(2) | (none) | {Chicago: Body, Lens} |
| One of two tied warehouse choices also stocks the companion item | Body: Delivery, 12 Main St, In Stock, Chicago(3) & Denver(3) [either alone sufficient]; Lens: Delivery, 12 Main St, In Stock, Denver(2) only | (none — tie broken toward Denver) | {Denver: Body, Lens} |
| No warehouse stocks both companions | Body: Delivery, 12 Main St, In Stock, Denver(3) only; Lens: Delivery, 12 Main St, In Stock, Chicago(2) only | no common warehouse | {Denver: Body}, {Chicago: Lens} |
| Companions differ in availability status | Body: Delivery, 12 Main St, In Stock, Chicago(3); Lens: Delivery, 12 Main St, Backorder (ETA 2026-09-10) | availability mismatch | {Chicago: Body (now)}, {2026-09-10: Lens} |
| Companions differ in fulfillment type | Body: Store Pickup, In Stock; Lens: Delivery, 12 Main St, In Stock | fulfillment type mismatch | {Pickup: Body}, {Delivery/12 Main St: Lens} |
| Companions are addressed to different recipients | Body: Delivery, 12 Main St, In Stock; Lens: Delivery, 5 Oak Ave (gift), In Stock | address mismatch | {12 Main St: Body}, {5 Oak Ave: Lens} |

*Assumption (resolves the "conflict with minimization" question): companion bundling only ever consolidates shipments or is infeasible — it never costs an extra shipment beyond the true minimum. It is applied only as a tie-breaker among equally-minimal warehouse solutions (row 2) and falls back to independent split-optimal sourcing when no common warehouse can cover both (row 3). If your intent was instead that companions should sometimes force one extra shipment to stay together, that's a different rule and should be confirmed.*

---

### 6. Resolves Precedence When Splitting Rules Apply Together

*This table exists only to show the interaction between rules — not to re-prove any single rule.*

| Scenario | Line Items | Shipments? |
|---|---|---|
| An order mixes a multi-warehouse item, a companion pair, a pickup item, and a backordered item | Shirt×6 (Delivery,12 Main St,In Stock; Philadelphia(3)+Washington(3)); Body (Delivery,12 Main St,In Stock,Denver(1)+Chicago(1), companion of Lens); Lens (Delivery,12 Main St,In Stock,Denver(1) only, companion of Body); Mug (Pickup, In Stock); Candle (Delivery,12 Main St,Backorder ETA 2026-09-01) | {Philadelphia: Shirt×3}, {Washington: Shirt×3}, {Denver: Body, Lens}, {Pickup: Mug}, {2026-09-01: Candle} — 5 shipments |
| Companion items differ in availability status | Body (Delivery,12 Main St,In Stock,Chicago(1), companion of Lens); Lens (Delivery,12 Main St,Backorder ETA 2026-09-10, companion of Body) | {Chicago: Body (now)}, {2026-09-10: Lens} — companions split |

Row 1 shows the shipment key composing correctly across dimensions (hard constraints partition first; within a partition, warehouse minimization and companion bundling apply, and can still yield multiple shipments per partition). Row 2 answers the open question about partial companion availability: **the in-stock item ships immediately rather than waiting for its companion**, since the stated business goal is "don't make the customer wait for everything." Hard partition constraints (fulfillment/address/availability) always outrank companion bundling.

---

## Assumptions requiring sign-off

1. **Tie-break rule** for equally-minimal warehouse combinations: lowest total shipping distance to the delivery address. (Could instead be cost, carrier SLA, or a fixed warehouse priority list.)
2. **Companion bundling never costs an extra shipment** — it's a free consolidation/tie-break, not a hard requirement that can increase shipment count.
3. **Partial companion availability**: the available companion ships now; the pair is not held back to wait for the other.
4. **Stock shortfall across the whole network**: fulfillable quantity ships now, shortfall becomes an open-ended backorder group. (Alternative: reject the item instead.)
5. **Address matching** is exact/normalized string match — fuzzy/geocoded matching is unconfirmed.
6. **Store pickup** items are fulfilled from a single customer-chosen store, not warehouse-selected/minimized like delivery items.