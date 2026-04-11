Five distinct concerns are at play here, each warranting its own table. I'll work through them in order of clarity, leaving open questions visible where the rules aren't fully specified yet.

---

## 1. Fulfillment Method Splitting

Items must be grouped by how they are fulfilled — store pickup and home delivery cannot share a shipment.

| Scenario | Cart items | Shipments? | Groups? |
|---|---|---|---|
| All home delivery | Shirt (delivery), Jeans (delivery) | 1 | {Shirt, Jeans} |
| All store pickup | Camera (pickup), Bag (pickup) | 1 | {Camera, Bag} |
| One pickup, one delivery | Camera (pickup), Tripod (delivery) | 2 | {Camera} , {Tripod} |
| Multiple per method | Shirt (delivery), Jeans (delivery), Camera (pickup), Lens (pickup) | 2 | {Shirt, Jeans} , {Camera, Lens} |
| Majority delivery, one pickup | Shirt (delivery), Jeans (delivery), Hat (pickup) | 2 | {Shirt, Jeans} , {Hat} |

---

## 2. Delivery Address Splitting

Items destined for different addresses must be in separate shipments, regardless of fulfillment method.

| Scenario | Cart items | Shipments? | Groups? |
|---|---|---|---|
| All to same address | Shirt (123 Main St), Book (123 Main St) | 1 | {Shirt, Book} |
| Gift to a different address | Shirt (123 Main St), Toy (456 Oak Ave) | 2 | {Shirt} , {Toy} |
| Two items to self, one gift | Shirt (123 Main St), Jeans (123 Main St), Toy (456 Oak Ave) | 2 | {Shirt, Jeans} , {Toy} |
| Three separate addresses | Gadget (123 Main St), Book (456 Oak Ave), Toy (789 Elm St) | 3 | {Gadget} , {Book} , {Toy} |

---

## 3. Availability-Based Splitting

In-stock items ship immediately; backordered and pre-order items ship when available. Items sharing the same availability date can ship together.

| Scenario | Cart items | Shipments? | Groups? |
|---|---|---|---|
| All in stock | Shirt (in stock), Jeans (in stock) | 1 | {Shirt, Jeans} — immediately |
| One backordered | Shirt (in stock), Jeans (backordered May 1) | 2 | {Shirt} immediately , {Jeans} May 1 |
| Both backordered, same date | Shirt (backordered May 1), Jeans (backordered May 1) | 1 | {Shirt, Jeans} — May 1 |
| Both backordered, different dates | Shirt (backordered May 1), Jeans (backordered Jun 1) | 2 | {Shirt} May 1 , {Jeans} Jun 1 |
| In stock + pre-order | Shirt (in stock), Limited Edition (pre-order Jun 15) | 2 | {Shirt} immediately , {Limited Edition} Jun 15 |
| All pre-order, same date | Item A (pre-order Jun 15), Item B (pre-order Jun 15) | 1 | {Item A, Item B} — Jun 15 |
| In stock + two backorders, same date | Shirt (in stock), Hoodie (backordered May 1), Cap (backordered May 1) | 2 | {Shirt} immediately , {Hoodie, Cap} May 1 |

**Open questions:**
- If a backordered item has no confirmed availability date, does it form its own shipment group or hold until a date is known?
- Can customers opt out of early shipment and wait for the complete order? (changes whether in-stock items ship ahead)

---

## 4. Warehouse Allocation — Minimising Shipments

When no single warehouse holds the full quantity, distribute across warehouses. Among all valid combinations, prefer the one using the fewest warehouses.

| Scenario | Qty ordered | Warehouse stocks | Shipments? | Allocation? |
|---|---|---|---|---|
| Single warehouse has full qty | 6 | Chicago: 6 | 1 | Chicago: 6 |
| Single warehouse has more than needed | 4 | Chicago: 6 | 1 | Chicago: 4 (uses 4 of 6) |
| Two warehouses needed, clean split | 6 | Chicago: 3, LA: 3 | 2 | Chicago: 3, LA: 3 |
| Two warehouses needed, uneven split | 6 | Chicago: 4, LA: 4 | 2 | Chicago: 4, LA: 2 |
| Two warehouses preferred over three | 6 | Chicago: 4, LA: 1, Houston: 1, Philadelphia: 3, Washington: 3 | 2 | Philadelphia: 3, Washington: 3 |
| Three warehouses required | 6 | Chicago: 3, LA: 2, Houston: 1 | 3 | Chicago: 3, LA: 2, Houston: 1 |
| Total stock insufficient | 6 | Chicago: 2, LA: 2, Houston: 1 | — | Cannot fulfill |

**Open questions:**
- When multiple minimum-shipment combinations exist (e.g., Philadelphia + Washington vs Chicago + Philadelphia both satisfy with 2 shipments), which wins? Is there a secondary criterion such as geography, cost, or warehouse load?
- Does this rule apply per line item (one SKU at a time) or across the whole order?

---

## 5. Companion Product Co-location

Companion items — products that are useless without each other — should ship from the same warehouse if any warehouse stocks the complete set. This takes priority over the default warehouse selection but not over shipment count.

| Scenario | Order items | Companion pair | Warehouse stocks | Shipments? | Allocation? | Companions together? |
|---|---|---|---|---|---|---|
| Single warehouse stocks both companions | Camera body, Lens | Body ↔ Lens | Chicago: body + lens | 1 | Chicago: body + lens | yes |
| One warehouse has both; another only one | Camera body, Lens | Body ↔ Lens | Chicago: body only; LA: body + lens | 1 | LA: body + lens | yes |
| Co-location is a tie-breaker between equal-shipment options | Camera body, Lens, Shirt | Body ↔ Lens | Chicago: body + shirt; LA: lens; Houston: body + lens | 2 | Houston: body + lens, Chicago: shirt | yes — Houston chosen over Chicago + LA split |
| No warehouse stocks both companions | Camera body, Lens | Body ↔ Lens | Chicago: body only; LA: lens only | 2 | Chicago: body, LA: lens | no — unavoidable |
| Companions with a non-companion item, all at one warehouse | Camera body, Lens, Memory card | Body ↔ Lens | Chicago: body + lens + card | 1 | Chicago: all | yes |
| Non-companion items, split by inventory | Shirt, Jeans | (none) | Chicago: shirt; LA: jeans | 2 | Chicago: shirt, LA: jeans | n/a |

**Open questions:**
- If companion products have different availabilities (e.g., body in stock, lens backordered), should the in-stock item be held to ship with the companion, or ship ahead? Holding together conflicts with the "ship when available" rule.
- Can there be companion sets of three or more items, or are companions always pairwise?
- Does the companion preference apply across fulfillment method boundaries — e.g., if a companion pair straddles pickup and delivery, does co-location still apply?

---

The five tables map onto fairly clean implementation boundaries. The two most likely sources of surprises in implementation are the tie-breaking rule in **table 4** (when multiple minimum-shipment allocations exist) and the conflict in **table 5** when companions have different availability dates — both of those open questions should be resolved before implementation touches those paths.