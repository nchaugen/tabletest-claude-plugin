Three distinct concerns here, each deserving its own table: what triggers a logical split, how to allocate across warehouses to minimise sub-shipments, and how companion items constrain that allocation. An order first gets divided into logical groups (Table 1), then each group gets warehouse-allocated (Table 2), with companion constraints layered on top (Table 3).

---

### Shipment Grouping

Items sharing the same fulfillment method, destination, and availability window belong to one shipment — any difference forces a split.

| Scenario | Order Contents | Shipments? | Groups? |
|---|---|---|---|
| All items identical attributes | Shirt + jacket, home delivery to 123 Main St, both in stock | 1 | [shirt, jacket] |
| Home delivery alongside store pickup | Shirt (home delivery, 123 Main St, in stock) + tripod (store pickup, Downtown, in stock) | 2 | [shirt → 123 Main St], [tripod → Downtown pickup] |
| Personal item and gift to different address | Shirt (home delivery, 123 Main St, in stock) + book (home delivery, 456 Oak Ave, in stock) | 2 | [shirt → 123 Main St], [book → 456 Oak Ave] |
| In-stock item alongside pre-order | Shirt (home delivery, 123 Main St, in stock) + jacket (home delivery, 123 Main St, pre-order) | 2 | [shirt ships now], [jacket when available] |
| In-stock item alongside backorder | Shirt (home delivery, 123 Main St, in stock) + jacket (home delivery, 123 Main St, backorder) | 2 | [shirt ships now], [jacket when restocked] |
| Two pickup items at same store | Camera + lens, both store pickup at Downtown, in stock | 1 | [camera, lens at Downtown] |
| Two pickup items at different stores | Camera (store pickup, Downtown, in stock) + lens (store pickup, Eastside, in stock) | 2 | [camera at Downtown], [lens at Eastside] |
| Two addresses plus a pre-order | Shirt (home delivery, 123 Main St, in stock) + book (home delivery, 456 Oak Ave, in stock) + jacket (home delivery, 123 Main St, pre-order) | 3 | [shirt → 123 Main St], [book → 456 Oak Ave], [jacket later → 123 Main St] |
| Pre-order alongside backorder, same address | Jacket A (home delivery, 123 Main St, pre-order) + jacket B (home delivery, 123 Main St, backorder) | ? | Open: one combined delayed shipment, or split by expected availability date? |

---

### Warehouse Allocation

For a single shipment group, select the fewest warehouses whose combined stock covers the quantity needed.

| Scenario | Units Needed | Warehouse Stock | Warehouses Selected? | Sub-shipments? |
|---|---|---|---|---|
| One warehouse has sufficient stock | 3 | Chicago: 6, LA: 2 | Chicago | 1 |
| Multiple single-warehouse options available | 3 | Chicago: 5, LA: 4 | {Chicago, LA} | 1 |
| No single warehouse covers the quantity | 6 | Chicago: 4, LA: 3 | Chicago + LA | 2 |
| Stock split across five warehouses; balanced pair available | 6 | Chicago: 4, LA: 1, Houston: 1, Philadelphia: 3, Washington: 3 | Philadelphia + Washington | 2 |
| Two warehouses each hold exactly half | 6 | Chicago: 3, LA: 3 | Chicago + LA | 2 |
| No single or two-warehouse combination covers the quantity | 5 | Chicago: 2, LA: 2, Houston: 1 | Chicago + LA + Houston | 3 |
| Total stock across all warehouses falls short | 10 | Chicago: 3, LA: 2, Houston: 2 | — | Open: partial fulfilment or reject outright? |

**Open:** When multiple minimum-warehouse combinations tie (e.g. Chicago + LA vs. Philadelphia + Washington both give 2 sub-shipments), what is the tiebreaker — shipping cost, proximity to customer, or arbitrary?

---

### Companion Product Co-shipment

Companion items should ship from a single shared warehouse where one exists. No shared location is a forced split requiring explicit resolution.

| Scenario | Item A | Item B | Companion Pair | Item A Stock Locations | Item B Stock Locations | Ship Together? | Resolution? |
|---|---|---|---|---|---|---|---|
| Shared location available | Camera body | Camera lens | yes | Chicago, LA | Chicago, Houston | yes | Both from Chicago |
| Only one location carries both | Camera body | Camera lens | yes | Chicago | Chicago | yes | Both from Chicago |
| Non-companion items at different locations | Shirt | Jacket | no | Chicago | LA | no | Normal warehouse split applies |
| No shared location exists | Camera body | Camera lens | yes | Chicago | LA | no (forced) | Open: ship separately, hold order, or alert customer? |
| Companion co-shipment conflicts with warehouse minimisation | Camera body | Camera lens | yes | Chicago, Philadelphia | Philadelphia only | yes (companions stay together) | Open: accept extra sub-shipment for remaining items in the group to honour companion constraint, or override? |

---

A few things worth resolving before implementation:

1. **Pre-order + backorder grouping** — are these the same "delayed" bucket or do they split further by expected date?
2. **Insufficient inventory** — partial fulfilment or hard reject?
3. **Warehouse tiebreaker** — what decides between equally good warehouse combinations?
4. **Forced companion split** — does the system hold, warn, or just ship anyway?
5. **Companion constraint priority** — does it always beat warehouse minimisation, or only when cost-neutral?