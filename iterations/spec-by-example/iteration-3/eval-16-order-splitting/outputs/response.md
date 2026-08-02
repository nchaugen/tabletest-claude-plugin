# Order Splitting — Example Tables

Order splitting isn't one rule — it's three separate concerns that compose:

1. **Bucketing** — which line items *can't* physically go in the same shipment at all (fulfillment method, delivery address, ship date)
2. **Warehouse selection** — once a bucket needs more of an item than one warehouse holds, which warehouses to draw from, minimizing shipment count
3. **Companion colocation** — a preference layered on top of (2): when two line items are companions, prefer a warehouse split that keeps them together

Each gets its own table below. I decomposed pre-order vs. backorder into a single **Expected Ship Date** column rather than a category — a "ships when it ships" rule falls out naturally that way, and two backordered items restocking on the same date correctly end up in the same bucket without needing a special case.

---

### Table 1 — Groups Order Lines into Shipment Buckets by Fulfillment, Address, and Ship Date

| Scenario | Line A Fulfillment | Line A Delivery Address | Line A Expected Ship Date | Line B Fulfillment | Line B Delivery Address | Line B Expected Ship Date | Same Shipment Bucket? | Split Reason? |
|---|---|---|---|---|---|---|---|---|
| Two in-stock items to the same delivery address | Delivery | 12 Elm St | Today | Delivery | 12 Elm St | Today | yes | |
| Store pickup paired with a home delivery item, same ship date | Store Pickup | | Today | Delivery | 12 Elm St | Today | no | Different fulfillment method |
| Two store pickup items | Store Pickup | | Today | Store Pickup | | Today | yes | |
| Delivery items addressed to different recipients (e.g. a gift and a personal item) | Delivery | 12 Elm St | Today | Delivery | 45 Oak Ave | Today | no | Different delivery address |
| Delivery items with different expected ship dates (one in stock, one backordered) | Delivery | 12 Elm St | Today | Delivery | 12 Elm St | Sep 15 | no | Different expected ship date |
| Two backordered delivery items restocking on the same date | Delivery | 12 Elm St | Sep 15 | Delivery | 12 Elm St | Sep 15 | yes | |

**Note:** Store pickup lines have a blank delivery address — address doesn't apply to that fulfillment method (this spec doesn't model multiple pickup locations; assumed out of scope).

---

### Table 2 — Selects Warehouses to Cover Quantity While Minimizing Shipment Count

| Scenario | Quantity Needed | Warehouse Stock | Warehouses Selected? | Shipment Count? |
|---|---|---|---|---|
| A single warehouse holds the full quantity | 6 | {Chicago: 10} | {Chicago: 6} | 1 |
| No single warehouse suffices; exactly one two-warehouse combination covers it | 6 | {Philadelphia: 3, Washington: 3} | {Philadelphia: 3, Washington: 3} | 2 |
| No single warehouse suffices; only a three-warehouse combination covers it | 6 | {Chicago: 4, LA: 1, Houston: 1} | {Chicago: 4, LA: 1, Houston: 1} | 3 |
| Both a two-warehouse and a three-warehouse combination are available | 6 | {Chicago: 4, LA: 1, Houston: 1, Philadelphia: 3, Washington: 3} | {Philadelphia: 3, Washington: 3} | 2 |
| Combined warehouse stock falls short of the quantity needed | 6 | {Chicago: 4, LA: 1} | {Chicago: 4, LA: 1} (remaining 1 backordered) | 2 |
| Two different two-warehouse combinations both cover the quantity | 6 | {Philadelphia: 3, Washington: 3, Chicago: 3, LA: 3} | {Chicago: 3, LA: 3} | 2 |

**ASSUMPTION (last row):** when multiple combinations tie on shipment count, this spec assumes a warehouse priority order breaks the tie (e.g. proximity to the customer or lowest fulfillment cost — here, illustratively, Chicago/LA outrank Philadelphia/Washington). **The actual tie-break key isn't specified anywhere in the request and needs a business decision** — this row should be revisited once that's confirmed.

**Note (row 5):** the unfulfilled remainder becomes its own line with a later expected ship date, which is where it re-enters Table 1's bucketing.

---

### Table 3 — Colocates Companion Line Items at a Shared Warehouse When Possible

| Scenario | Camera Body Stock | Lens Stock | Other Order Items Stock | Quantity Needed (each) | Warehouses Selected? | Shipment Count? |
|---|---|---|---|---|---|---|
| A single warehouse stocks enough of both companion items | {Denver: 3, Atlanta: 2} | {Denver: 2, Atlanta: 1} | | 1 | {Denver: Camera Body 1, Lens 1} | 1 |
| Companion items only overlap at a warehouse already needed for another item | {Denver: 1, Atlanta: 1} | {Atlanta: 1} | {Denver: Tripod 1} | 1 | {Denver: Tripod 1, Atlanta: Camera Body 1, Lens 1} | 2 |
| No warehouse stocks enough of either companion item to cover both | {Denver: 2} | {Atlanta: 2} | | 2 | {Denver: Camera Body 2, Atlanta: Lens 2} | 2 |
| The only shared warehouse for companions is otherwise unnecessary | {Denver: 1, Chicago: 1} | {Denver: 1, Atlanta: 1} | {Chicago: Tripod 1, Atlanta: Gift Box 1} | 1 | {Chicago: Tripod 1, Camera Body 1, Atlanta: Gift Box 1, Lens 1} | 2 |

**ASSUMPTION (last row):** colocating the companions here (both from Denver) would require opening a warehouse that nothing else in the order needs, raising the shipment count from 2 to 3. **This spec assumes minimizing shipment count wins when it genuinely conflicts with colocation** — companions ship separately rather than adding a shipment. "If possible" (per your description) is read as "when it doesn't cost an extra shipment." **This precedence needs confirmation from the business** — if colocation should win even at the cost of an extra shipment, this row's expected output flips.

---

## How the tables compose

For a real order: run Table 1 first to partition line items into buckets that can never share a shipment. Within each bucket, for any product whose ordered quantity exceeds one warehouse's stock, apply Table 2 to pick the minimal warehouse set. If a bucket contains a companion pair, apply Table 3's precedence rule *before* finalizing which warehouses are used for those specific line items — it either confirms Table 2's pick already colocates them, or overrides it when a tie allows colocation for free.

## Open items needing a business decision

- **Warehouse tie-break key** (Table 2, row 6) — proximity, cost, or something else?
- **Colocation vs. shipment count when they truly conflict** (Table 3, row 4) — assumed shipment count wins; confirm.
- **Pickup location granularity** — assumed out of scope (single store per order); flag if multi-store pickup is real.