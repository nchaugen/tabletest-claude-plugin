Order splitting has four distinct concerns worth separating: what *forces* a split (three independent rules), how to *minimise warehouse locations* once a group is identified, and how *companions* constrain co-location. Here are example tables for each.

---

## Fulfillment-Type Split

Items with different fulfillment types cannot share a shipment.

| Scenario | Items | Fulfillment Types | Shipments? | Shipment 1? | Shipment 2? |
|---|---|---|---|---|---|
| All home delivery | Shirt, Jacket | Delivery, Delivery | 1 | Shirt + Jacket | — |
| All store pickup | Shirt, Jacket | Pickup, Pickup | 1 | Shirt + Jacket | — |
| One pickup, one delivery | Shirt, Jacket | Delivery, Pickup | 2 | Shirt | Jacket |
| Two delivery, one pickup | Shirt, Jacket, Hat | Delivery, Delivery, Pickup | 2 | Shirt + Jacket | Hat |

---

## Availability-Window Split

Items group together when they share the same availability window; different windows force a split.

| Scenario | Items | Availability | Shipments? | Shipment 1? | Shipment 2? | Shipment 3? |
|---|---|---|---|---|---|---|
| All in stock | Shirt, Jacket | In stock, In stock | 1 | Shirt + Jacket (immediate) | — | — |
| In-stock and pre-order | Shirt, Jacket | In stock, Pre-order 3 weeks | 2 | Shirt (immediate) | Jacket (3 weeks) | — |
| Same backorder date consolidates | Shirt, Jacket | Backorder 2 weeks, Backorder 2 weeks | 1 | Shirt + Jacket (2 weeks) | — | — |
| Different backorder dates split | Shirt, Jacket | Backorder 2 weeks, Backorder 4 weeks | 2 | Shirt (2 weeks) | Jacket (4 weeks) | — |
| In-stock + two different backorders | Shirt, Jacket, Hat | In stock, Backorder 2 weeks, Backorder 4 weeks | 3 | Shirt (immediate) | Jacket (2 weeks) | Hat (4 weeks) |

**Open question:** Does "same window" mean the same calendar date, or is there a tolerance (e.g. same week)? If two items are both back-ordered but one arrives Monday and one arrives Thursday, do they consolidate?

---

## Delivery-Address Split

Items group together when they share a delivery address.

| Scenario | Items | Delivery Addresses | Shipments? | Shipment 1? | Shipment 2? | Shipment 3? |
|---|---|---|---|---|---|---|
| All same address | Shirt, Jacket | Home, Home | 1 | Shirt + Jacket → Home | — | — |
| Two different addresses | Shirt, Book | Home, Gift address | 2 | Shirt → Home | Book → Gift address | — |
| Multiple items group by matching address | Shirt, Jacket, Book, Toy | Home, Home, Gift, Gift | 2 | Shirt + Jacket → Home | Book + Toy → Gift | — |
| Three different addresses | Shirt, Book, Toy | Home, Gift A, Gift B | 3 | Shirt → Home | Book → Gift A | Toy → Gift B | — |

---

## Warehouse Assignment (Minimise Locations)

Once a group of items is established (same fulfillment type, availability window, and address), they must be sourced from as few warehouse locations as possible.

| Scenario | Item + Qty | Warehouse Stock | Locations Used? | Assignment? |
|---|---|---|---|---|
| One warehouse covers all | Shirt ×6 | Chicago: 8 | 1 | Chicago ×6 |
| One warehouse preferred when it can cover all | Shirt ×6 | Chicago: 8, Philadelphia: 4 | 1 | Chicago ×6 |
| Two warehouses needed, none alone sufficient | Shirt ×6 | Philadelphia: 3, Washington: 3 | 2 | Philadelphia ×3, Washington ×3 |
| Two-location option preferred over three | Shirt ×6 | Philadelphia: 3, Washington: 3, Chicago: 1, LA: 1, Houston: 1 | 2 | Philadelphia ×3, Washington ×3 |
| Three locations needed — no two-location option | Shirt ×6 | Chicago: 4, LA: 1, Houston: 1 | 3 | Chicago ×4, LA ×1, Houston ×1 |
| Cannot fulfil | Shirt ×6 | Chicago: 3, LA: 2 | — | Insufficient stock (5 of 6 available) |

**Open question:** When multiple minimum-location combinations exist and are tied (e.g. Philadelphia+Washington vs Chicago+Denver, both 2 locations), what is the tie-breaking rule? Proximity? Cost? Stock freshness?

---

## Companion Co-location

Companions (items that are useless without each other) should ship from the same warehouse location if possible. This is a preference that yields to mandatory splits.

| Scenario | Companion Items | Warehouse Stock | Co-located? | Assignment? |
|---|---|---|---|---|
| One warehouse has both | Camera body + Lens | Chicago: body + lens | yes | Both from Chicago |
| One warehouse has both, others have one each | Camera body + Lens | Chicago: body only, LA: lens only, Houston: both | yes | Both from Houston |
| No single warehouse has both | Camera body + Lens | Chicago: body only, LA: lens only | no | Forced split — body from Chicago, lens from LA |
| Fulfillment type overrides companion preference | Camera body (delivery) + Lens (pickup) | Chicago: both | no | Cannot co-locate across fulfillment types |
| Companions with an independent item, all at same warehouse | Camera body + Lens + Hat | Chicago: body + lens + hat | yes | All three from Chicago |
| Companions together, independent item elsewhere | Camera body + Lens + Hat | Chicago: body + lens, LA: hat | yes (companions) | Camera body + Lens from Chicago, Hat from LA |

**Open question:** If one companion is in stock and the other is backordered, should the in-stock item wait and ship with the backordered one (when it becomes available, if the same warehouse carries it), or do they split? Waiting preserves co-location but delays one item the customer could receive sooner.

---

These four tables cover the main concerns independently. The split rules (fulfillment type, availability, address) combine additively — an order with items differing on all three dimensions would have shipments for each distinct combination of the three. The warehouse assignment and companion rules then apply within each resulting group.