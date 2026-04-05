Looking at what you've described, there are three distinct concerns: **why items must be split** (fulfillment type, availability, address), **how to allocate across warehouses** (minimize shipments), and **companion grouping** (a constraint on allocation). I'll cover each separately.

---

## Table 1: Splitting by Fulfillment Type

*When items in the same order have different fulfillment types, they cannot share a shipment.*

| Scenario | Items | Fulfillment Types | Resulting Shipments? |
|---|---|---|---|
| All items for home delivery | Shirt, Jeans | Home delivery, Home delivery | 1 delivery |
| All items for store pickup | Shirt, Jeans | Store pickup, Store pickup | 1 pickup |
| One pickup, one delivery | Shirt, Jeans | Home delivery, Store pickup | 2 (shirt as delivery; jeans as pickup) |
| Multiple items of each type | Shirt, Jeans, Hat, Belt | Home delivery, Store pickup, Home delivery, Store pickup | 2 (shirt + hat as delivery; jeans + belt as pickup) |

---

## Table 2: Splitting by Delivery Address

*Items destined for different addresses cannot share a shipment.*

| Scenario | Items | Delivery Addresses | Resulting Shipments? |
|---|---|---|---|
| All items to same address | Book, Mug | 123 Main St, 123 Main St | 1 |
| Personal item and gift to different address | Book, Candle | 123 Main St, 456 Oak Ave | 2 (book to Main St; candle to Oak Ave) |
| Three items, two addresses | Lamp, Watch, Scarf | 123 Main St, 456 Oak Ave, 123 Main St | 2 (lamp + scarf to Main St; watch to Oak Ave) |

---

## Table 3: Splitting by Availability

*In-stock items should ship immediately; backordered items ship when ready. Items with the same availability date can share a shipment.*

| Scenario | Items | Availability | Resulting Shipments? | Timing? |
|---|---|---|---|---|
| All in stock | Shirt, Jeans | In stock, In stock | 1 | Ships now |
| All backordered, same date | Shirt, Jeans | Backorder 14 days, Backorder 14 days | 1 | Ships in 14 days |
| In stock + backordered | Shirt, Jeans | In stock, Backorder 7 days | 2 (shirt now; jeans in 7 days) | |
| Mixed backorder dates, group same-date items | Shirt, Jeans, Hat | In stock, Backorder 7 days, Backorder 7 days | 2 (shirt now; jeans + hat in 7 days) | |
| All different availability dates | Shirt, Jeans, Hat | In stock, Backorder 7 days, Backorder 14 days | 3 (shirt now; jeans in 7 days; hat in 14 days) | |

---

## Table 4: Warehouse Allocation — Minimising Shipments

*When no single warehouse can fulfill an order, prefer the combination of warehouses that results in the fewest shipments.*

| Scenario | Item | Quantity Needed | Warehouse Stocks | Shipments? | Fulfilling Warehouses? |
|---|---|---|---|---|---|
| Single warehouse covers full order | Blue shirt M | 6 | Chicago: 8, LA: 3 | 1 | Chicago |
| Single-warehouse option preferred over split | Blue shirt M | 6 | Chicago: 6, Philadelphia: 4, Washington: 3 | 1 | Chicago |
| Two-warehouse pair preferred over three | Blue shirt M | 6 | Chicago: 4, LA: 1, Houston: 1, Philadelphia: 3, Washington: 3 | 2 | Philadelphia + Washington |
| No single warehouse sufficient, minimum two | Blue shirt M | 5 | East: 3, West: 3, Central: 2 | 2 | East + West |
| Unfulfillable order — insufficient total stock | Blue shirt M | 6 | North: 2, South: 2 | — (rejected) | Total stock: 4, need: 6 |

> **Open question:** when two warehouse combinations both achieve the minimum shipment count (e.g. East+West vs East+Central in row 4), what's the tiebreaker — proximity to customer, cost, or arbitrary?

---

## Table 5: Companion Product Grouping

*Companion items (e.g. camera body + matching lens) should ship together from the same warehouse where possible.*

| Scenario | Items Ordered | Companion Pair | Warehouse Inventory | Shipments? | Notes? |
|---|---|---|---|---|---|
| One warehouse has both companions | EOS R5, RF 50mm lens | (EOS R5, RF 50mm) | NYC: {EOS R5: 1, RF 50mm: 1} | 1 (NYC) | |
| Prefer co-stocking warehouse over split | EOS R5, RF 50mm lens | (EOS R5, RF 50mm) | NYC: {EOS R5: 1, RF 50mm: 1}, Chicago: {EOS R5: 1} | 1 (NYC: both) | Chicago ignored — NYC co-locates the pair |
| Companion pair ships together; non-companion ships separately | EOS R5, RF 50mm lens, SD card | (EOS R5, RF 50mm) | NYC: {EOS R5: 1, RF 50mm: 1}, LA: {SD card: 1} | 2 (NYC: camera + lens; LA: card) | |
| Non-companion items follow normal allocation rules | SD card, USB hub | none | NYC: {SD card: 1}, LA: {USB hub: 1} | 2 | No companion constraint applies |
| No warehouse stocks both companions | EOS R5, RF 50mm lens | (EOS R5, RF 50mm) | NYC: {EOS R5: 1}, Chicago: {RF 50mm: 1} | ? | **Open: split into 2 shipments, or hold until one warehouse can supply both?** |

---

## Open Questions to Resolve

Before implementing, these need decisions:

1. **Companion unavailability** — when no single warehouse has both companion items, does the order split anyway, get held, or is the customer notified and given a choice?
2. **Warehouse tiebreaking** — when multiple warehouse combinations achieve the same minimum shipment count, what determines the winner?
3. **Combinations of split reasons** — if items differ in both fulfillment type *and* address, are those separate split axes or is the split purely by address once fulfillment type has been handled first?
4. **Three or more companions** — is a companion relationship always a pair, or can a set of three items (e.g. camera body, grip, battery) all be companions?