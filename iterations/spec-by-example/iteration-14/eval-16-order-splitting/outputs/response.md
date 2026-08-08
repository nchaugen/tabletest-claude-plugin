Here's a full spec-by-example breakdown of order-splitting behavior, organized as one table per independent rule, plus two tables that show how the rules interact.

**Pipeline assumption:** splitting happens in two phases. Phase 1 partitions order lines into shipment groups using three *hard* constraints (fulfillment method, ship-to address, availability) — these can never be merged back together. Phase 2 decides, within each group, which warehouse(s) fulfill it, minimizing shipment count and preferring to keep companion items co-located. Hard constraints always win over the companion preference (Table 6).

**Notation:** `[Warehouse: Qty, ...]` = single-SKU stock/allocation. `{Item: Qty, ...}` = per-warehouse stock for multi-SKU orders. Blank = absent/not applicable. `?` = open question, not yet decided.

---

### Table 1 — Splits an order by fulfillment method

| Scenario | Item A Method | Item B Method | Ship-To Address (both) | Availability (both) | Same Shipment Group? |
|---|---|---|---|---|---|
| Both items for pickup | Pickup | Pickup | Home | In Stock | yes |
| Both items for delivery | Delivery | Delivery | Home | In Stock | yes |
| One pickup, one delivery | Pickup | Delivery | Home | In Stock | no |

### Table 2 — Splits an order by ship-to address

| Scenario | Item A Address | Item B Address | Fulfillment Method (both) | Availability (both) | Same Shipment Group? |
|---|---|---|---|---|---|
| Both items to the same address | Home | Home | Delivery | In Stock | yes |
| Gift item and personal item to different addresses | Recipient's Address | Customer's Home | Delivery | In Stock | no |

### Table 3a — Splits an order by item availability (ready-now grouping)

| Scenario | Item A Availability | Item B Availability | Fulfillment Method (both) | Ship-To Address (both) | Same Shipment Group? |
|---|---|---|---|---|---|
| Both items in stock | In Stock | In Stock | Delivery | Home | yes |
| One in stock, one not yet available | In Stock | {Backorder, Pre-Order} | Delivery | Home | no |
| Both not yet available | {Backorder, Pre-Order} | {Backorder, Pre-Order} | Delivery | Home | yes |
| Backorder item and pre-order item with different ETAs | Backorder (ETA next week) | Pre-Order (ETA next month) | Delivery | Home | ? |

Row 4 is open: it's unclear whether two not-yet-available items should ship together once *both* are ready, or split further by ETA so the earlier-ready one doesn't wait on the later one.

### Table 3b — Computes quantity shipped now vs. held back for a single line

| Scenario | Quantity Ordered | Quantity Available Now | Quantity Shipped Now? | Quantity Held Back? |
|---|---|---|---|---|
| Fully available | 3 | 5 | 3 | (blank) |
| Fully unavailable | 3 | 0 | (blank) | 3 |
| Partially available | 6 | 4 | 4 | 2 |
| Exactly available (boundary) | 6 | 6 | 6 | (blank) |

*Assumption: a single line can itself split — the available portion ships now, the shortfall becomes a separate deferred shipment. If your system instead holds the whole line until fully available, tell me and I'll redo this table.*

### Table 4 — Selects warehouses to fulfill a line, minimizing shipment count

| Scenario | Quantity Ordered | Warehouse Stock | Warehouses Used? | Shipment Count? | Quantity Backordered? |
|---|---|---|---|---|---|
| Single warehouse covers it | 6 | [Chicago: 10] | [Chicago: 6] | 1 | (blank) |
| Two-warehouse combo beats a worse three-way split | 6 | [Chicago: 2, Philadelphia: 3, Washington: 3] | [Philadelphia: 3, Washington: 3] | 2 | (blank) |
| No two-warehouse combo suffices, three-way forced | 6 | [Chicago: 4, LA: 1, Houston: 1] | [Chicago: 4, LA: 1, Houston: 1] | 3 | (blank) |
| Network stock exactly matches order (boundary) | 5 | [Chicago: 4, LA: 1] | [Chicago: 4, LA: 1] | 2 | (blank) |
| Network stock falls one short (just past boundary) | 6 | [Chicago: 4, LA: 1] | [Chicago: 4, LA: 1] | 2 | 1 |
| Multiple equally-minimal combos exist (tie) | 6 | [Denver: 3, Atlanta: 3, Dallas: 3, ElPaso: 3] | ? | 2 | (blank) |

Last row is open: several warehouse pairs achieve the minimum of 2 shipments — need a tie-break rule (e.g., lowest shipping cost/distance to customer, warehouse inventory balancing, or fixed priority order).

### Table 5 — Keeps companion items together when choosing among warehouse allocations

| Scenario | Items & Qty | Companion Pair | Warehouse Stock | Minimum Shipment Count? | Chosen Warehouses Used? | Companions Shipped Together? |
|---|---|---|---|---|---|---|
| Companions already at the sole warehouse needed | Body: 1, Lens: 1 | Body–Lens | Chicago: {Body: 5, Lens: 5} | 1 | Chicago: {Body: 1, Lens: 1} | yes |
| Tie between combos; the together-option is chosen | Body: 1, Lens: 1, Cable: 1 | Body–Lens | Chicago: {Body: 1, Lens: 1}, LA: {Body: 1, Cable: 1}, Denver: {Lens: 1, Cable: 1} | 2 | Chicago: {Body: 1, Lens: 1}, LA: {Cable: 1} | yes |
| No warehouse has both companions — split is the only option | Body: 1, Lens: 1 | Body–Lens | Chicago: {Body: 1}, Denver: {Lens: 1} | 2 | Chicago: {Body: 1}, Denver: {Lens: 1} | no |
| Keeping companions together costs one extra shipment vs. the true minimum | Body: 1, Lens: 1, Cable: 1, Mouse: 1 | Body–Lens | Chicago: {Body: 1, Lens: 1}, Denver: {Body: 1, Cable: 1}, ElPaso: {Lens: 1, Mouse: 1} | 2 (split: Denver{Body,Cable} + ElPaso{Lens,Mouse}) | ? | ? |

Last row is the key open question the whole exercise turns on: is "ship companions together" a *soft tie-break* (only applied when it's free) or a *hard preference* worth paying for an extra shipment? The user's phrasing — "if possible" — reads as soft, but I'd confirm before building it, since it directly trades off cost against the "camera without a lens" experience problem.

### Table 6 — Hard group splits take precedence over companion affinity

| Scenario | Item A | Item B | Companions? | Same Shipment? |
|---|---|---|---|---|
| Companions, same method/address/availability, co-located in stock | Delivery, Home, In Stock | Delivery, Home, In Stock | yes | yes |
| Companions, but one is pickup and one is delivery | Pickup, Home, In Stock | Delivery, Home, In Stock | yes | no |
| Companions, but one is in stock and one is backordered | Delivery, Home, In Stock | Delivery, Home, Backorder | yes | no |
| Companions, but shipped to different addresses | Delivery, Home, In Stock | Delivery, Recipient's Address, In Stock | yes | no |

---

### Open questions to resolve before implementation

1. **Table 3a, row 4** — do two different not-yet-available items (different ETAs) ship together once both are ready, or split further by ETA?
2. **Table 4, tie row** — what's the tie-break when multiple warehouse combinations achieve the same minimal shipment count (cost, distance, inventory balancing, fixed priority)?
3. **Table 5, last row — the big one** — when keeping companions together costs strictly more shipments than the true minimum, does companion-togetherness win, or does minimization win? This needs a business decision, not an engineering guess.
4. **Table 3b** — does a held-back remainder, once it becomes available, automatically merge with any other still-pending shipment for that customer, or always ship alone?

### Assumptions I made to keep this concrete

- A single order line can itself split by quantity (part ships now, remainder is backordered) — Table 3b.
- Backorder and pre-order are treated identically for grouping purposes (both "not ready now") — flagged as open in Table 3a where it matters.
- Companion affinity is evaluated only *after* hard splits and *within* warehouse selection — never overrides Tables 1–3.

Once questions 2 and 3 are answered, these tables map directly onto `@TableTest` cases if you want to hand them to `/tabletest`.