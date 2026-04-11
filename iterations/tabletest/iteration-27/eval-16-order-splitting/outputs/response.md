Here are five tables, one per concern. I'll work through each splitting rule, then warehouse allocation, then the companion constraint — which is softer than the rest.

---

## Order Splitting — Fulfillment Method

Items for different fulfillment methods cannot share a shipment.

| Scenario | Items | Fulfillment Methods | Shipments? | Contents? |
|---|---|---|---|---|
| All home delivery | Shirt, Jeans | Home Delivery, Home Delivery | 1 | [Shirt, Jeans] |
| All store pickup | Book, Mug | Store Pickup, Store Pickup | 1 | [Book, Mug] |
| One delivery, one pickup | Shirt, Book | Home Delivery, Store Pickup | 2 | [Shirt] · [Book] |
| Two delivery items, one pickup | Shirt, Jeans, Book | Home Delivery, Home Delivery, Store Pickup | 2 | [Shirt, Jeans] · [Book] |

---

## Order Splitting — Delivery Address

Items going to different addresses cannot share a shipment.

| Scenario | Items | Delivery Addresses | Shipments? | Contents? |
|---|---|---|---|---|
| All to same address | Shirt, Jeans | 42 Oak Lane, 42 Oak Lane | 1 | [Shirt, Jeans] |
| Personal item + gift | Shirt, Book | 42 Oak Lane, 7 Elm St | 2 | [Shirt] · [Book] |
| Three different addresses | Shirt, Book, Toy | 42 Oak Lane, 7 Elm St, 99 Pine Rd | 3 | [Shirt] · [Book] · [Toy] |
| Two items same address, one different | Shirt, Jeans, Book | 42 Oak Lane, 42 Oak Lane, 7 Elm St | 2 | [Shirt, Jeans] · [Book] |

---

## Order Splitting — Availability Window

Items with different availability should ship when ready rather than waiting for the last item.

| Scenario | Items | Availability | Shipments? | Shipment Timing? |
|---|---|---|---|---|
| All in stock | Shirt, Jeans | In Stock, In Stock | 1 | Now |
| All pre-order, same release date | Book A, Book B | Pre-order 2026-06-01, Pre-order 2026-06-01 | 1 | 2026-06-01 |
| In stock + pre-order | Shirt, Book | In Stock, Pre-order 2026-06-01 | 2 | Shirt: now · Book: 2026-06-01 |
| Two different pre-order dates | Book A, Toy | Pre-order 2026-05-01, Pre-order 2026-06-01 | 2 | Book A: 2026-05-01 · Toy: 2026-06-01 |
| In stock + backorder (date unknown) | Shirt, Widget | In Stock, Backorder | 2 | Shirt: now · Widget: when available |
| In stock + two pre-order dates | Shirt, Book, Toy | In Stock, Pre-order 2026-05-01, Pre-order 2026-06-01 | 3 | Shirt: now · Book: 2026-05-01 · Toy: 2026-06-01 |

---

## Warehouse Allocation — Minimising Shipments

When no single warehouse covers the full quantity, use the fewest warehouses that together cover it.

| Scenario | Quantity | Available Stock | Shipments? | Warehouses Used? |
|---|---|---|---|---|
| One warehouse covers full order | 6 shirts | Chicago: 6 | 1 | Chicago |
| One warehouse has surplus | 6 shirts | Chicago: 10 | 1 | Chicago |
| Two warehouses required, exact fit | 6 shirts | Chicago: 4, LA: 2 | 2 | Chicago + LA |
| Three warehouses minimum | 6 shirts | Chicago: 4, LA: 1, Houston: 1 | 3 | Chicago + LA + Houston |
| Two-warehouse solution preferred over three | 6 shirts | Philadelphia: 3, Washington: 3, Chicago: 2, LA: 2, Houston: 2 | 2 | Philadelphia + Washington |
| Total stock insufficient | 6 shirts | Chicago: 4, LA: 1 | — | Unfulfillable |

> **Open question — tie-breaking:** When multiple warehouse combinations share the same minimum count (e.g. Philadelphia+Washington and Chicago+LA+Houston both happen to be 2-warehouse solutions), which do we prefer? Likely lowest shipping cost, but needs a defined tiebreaker.

---

## Companion Product Co-location

Companions should ship together from the same warehouse where possible. This is a preference, not an absolute rule.

| Scenario | Items | Companion Pair | Stock | Shipments? | Companion Grouping? |
|---|---|---|---|---|---|
| Shared warehouse available | Camera body, Lens | Camera body ↔ Lens | Chicago: Camera body + Lens | 1 | Together |
| Prefer shared warehouse over individual availability | Camera body, Lens | Camera body ↔ Lens | Chicago: Camera body + Lens; LA: Lens only | 1 | Both from Chicago (not split to LA) |
| Companions with non-companion, all at same warehouse | Camera body, Lens, Tripod | Camera body ↔ Lens | Chicago: all three | 1 | All together |
| Companions at same warehouse, non-companion elsewhere | Camera body, Lens, Tripod | Camera body ↔ Lens | Chicago: Camera body + Lens; LA: Tripod | 2 | Camera body + Lens from Chicago · Tripod from LA |
| No warehouse stocks both — forced split | Camera body, Lens | Camera body ↔ Lens | Chicago: Camera body; LA: Lens | 2 | Unavoidable split |

---

## Open Questions

A few things worth resolving before implementation:

1. **Availability + companion interaction** — if one companion is in stock and the other is on pre-order, do they wait and ship together, or ship the available item immediately? The "prefer together" rule and the "ship when available" rule conflict here.

2. **Address + companion interaction** — if a customer orders companion items to different delivery addresses, the address constraint presumably overrides the co-location preference. Is that correct?

3. **Customer preference to hold** — does the customer ever get to say "hold everything until it can all ship together"? If so, that opt-in likely affects how the availability splitting table behaves.

4. **Mixed splitting rules** — when an order triggers multiple splitting rules simultaneously (e.g. mixed fulfillment methods *and* different availability windows), what is the rule for grouping? The cleanest model is: two items can share a shipment only if they have the same fulfillment method, the same delivery address, and the same availability window.

5. **Partial backorder** — if a customer orders 3 units of an item and only 2 are in stock, does the in-stock quantity ship immediately and 1 unit ship later, or does the order wait until all 3 are available?

Which of these should we pin down now before moving to `@TableTest`?