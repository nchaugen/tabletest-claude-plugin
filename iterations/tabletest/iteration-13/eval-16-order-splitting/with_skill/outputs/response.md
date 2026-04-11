Five distinct splitting rules are at play here, each worth its own table. The companion constraint cuts across several of them, so I'll flag interaction questions as open items at the end.

---

## Order Splitting Specification

### Table 1 — Fulfillment Method Split

| Scenario | Home delivery items | Store pickup items | Shipment count? | Shipment groups? |
|---|---|---|---|---|
| All items home delivery | Shirt, Pants | — | 1 | [Shirt, Pants] → home delivery |
| All items store pickup | — | Mug, Book | 1 | [Mug, Book] → store pickup |
| One home delivery, one pickup | Shirt | Mug | 2 | [Shirt] → home delivery; [Mug] → store pickup |
| Multiple home delivery, one pickup | Shirt, Pants | Mug | 2 | [Shirt, Pants] → home delivery; [Mug] → store pickup |
| Multiple of each method | Shirt, Pants | Mug, Book | 2 | [Shirt, Pants] → home delivery; [Mug, Book] → store pickup |

---

### Table 2 — Availability Split

| Scenario | In-stock items | Backordered items | Immediate shipment? | Deferred shipment? |
|---|---|---|---|---|
| All items in stock | Shirt, Pants | — | Shirt, Pants | — |
| All items backordered | — | Camera | — | Camera |
| One in stock, one backordered | Shirt | Camera | Shirt | Camera |
| Multiple in stock, one backordered | Shirt, Pants | Camera | Shirt, Pants | Camera |
| One in stock, multiple backordered | Shirt | Camera, Lens | Shirt | Camera, Lens |
| Multiple backordered, same available date | — | Camera, Tripod (both April) | — | Camera, Tripod |
| Multiple backordered, different available dates | — | Camera (April), Tripod (June) | — | ? | ← open: one deferred shipment per ETA, or hold all until June? |

---

### Table 3 — Inventory Distribution (Warehouse Selection)

Minimum shipments rule: find the fewest warehouses whose combined stock covers the order quantity.

| Scenario | Qty needed | Chicago | LA | Houston | Philadelphia | Washington | Shipments? | From? |
|---|---|---|---|---|---|---|---|---|
| One warehouse covers all | 4 | 6 | 2 | 0 | 0 | 0 | 1 | Chicago: 4 |
| Two warehouses needed | 4 | 2 | 3 | 0 | 0 | 0 | 2 | Chicago: 2, LA: 2 |
| Fewer warehouses preferred over largest single stock | 6 | 4 | 1 | 1 | 3 | 3 | 2 | Philadelphia: 3, Washington: 3 |
| Three warehouses required | 5 | 2 | 0 | 0 | 2 | 1 | 3 | Chicago: 2, Philadelphia: 2, Washington: 1 |
| Total available stock is insufficient | 6 | 2 | 1 | 0 | 0 | 0 | — | ? | ← open: reject order, or partially fulfil and back-order remainder?

The Chicago/LA/Houston vs Philadelphia/Washington case above is the key scenario from your description. Even though Chicago has the most stock (4), using Philadelphia + Washington produces only 2 shipments compared to 3.

---

### Table 4 — Multiple Delivery Addresses

| Scenario | Items | Delivery address | Shipments? | Shipment groups? |
|---|---|---|---|---|
| All items, same address | Shirt, Pants | 123 Main St (both) | 1 | [Shirt, Pants] → 123 Main St |
| Two items, two addresses | Shirt, Gift book | Shirt → 123 Main St; Gift book → 456 Oak Ave | 2 | [Shirt] → 123 Main St; [Gift book] → 456 Oak Ave |
| Three items, two addresses | Shirt, Pants, Gift book | Shirt + Pants → 123 Main St; Gift book → 456 Oak Ave | 2 | [Shirt, Pants] → 123 Main St; [Gift book] → 456 Oak Ave |
| Three items, three addresses | Shirt, Pants, Gift book | one each | 3 | one item per shipment |

---

### Table 5 — Companion Products

Companion pairs should ship together from the same warehouse *if possible*. If no single warehouse stocks both, they ship separately.

| Scenario | Item A | Item B | Companion pair | WH East: A qty | WH East: B qty | WH West: A qty | WH West: B qty | Shipped together? | From? |
|---|---|---|---|---|---|---|---|---|---|
| Only one warehouse stocks both | Camera body | Camera lens | yes | 5 | 3 | 2 | 0 | yes | WH East |
| Both warehouses stock both | Camera body | Camera lens | yes | 5 | 3 | 4 | 2 | yes | {WH East, WH West} |
| No warehouse stocks both (unavoidable split) | Camera body | Camera lens | yes | 5 | 0 | 0 | 4 | no | WH East: Camera body; WH West: Camera lens |
| Non-companion items follow normal distribution | Shirt | Hat | no | 3 | 0 | 0 | 5 | no | WH East: Shirt; WH West: Hat |
| Companions: one warehouse has both, other has more stock | Camera body | Camera lens | yes | 5 | 3 | 8 | 0 | yes | WH East | ← companion constraint overrides stock optimisation for WH West

---

### Open Questions

These are the cases where the rules interact and the expected behaviour is not yet pinned down:

| Question | Scenario | What needs deciding |
|---|---|---|
| Backorder ETAs | Two backordered items with different available dates | One deferred shipment per ETA, or hold both until the later date? |
| Companion + availability | Camera body in stock, companion lens backordered | Does the body wait for the lens, or ship immediately (breaking the companion constraint)? |
| Companion + address | Companions ordered to different delivery addresses | Does address split override the companion rule, or is this a validation error at order entry? |
| Companion + warehouse selection | Only WH East has both companions, but WH West minimises total shipments for the rest of the order | Does the companion constraint force WH East even if it increases overall shipment count? |
| Unfulfillable quantity | Total stock across all warehouses is less than ordered | Reject order outright, or fulfil what's available and back-order the rest as a new deferred shipment? |

The companion + availability question is probably the most consequential — resolving it will determine whether companions need to be tracked as a constraint in the availability split logic as well as the warehouse selection logic.