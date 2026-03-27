Looking at the domain, I can see at least three distinct concerns here: what triggers a split, how to select warehouses when inventory is distributed, and the companion constraint. Let me work through each as a separate table.

---

## Order Split Triggers

| Scenario | Fulfillment types in order | Availability | Delivery addresses | Shipments? | Split reason? |
|---|---|---|---|---|---|
| Single home delivery, all in stock | all delivery | all in-stock | 1 | 1 | |
| All store pickup, all in stock | all store pickup | all in-stock | 1 | 1 | |
| Store pickup mixed with home delivery | store pickup + delivery | all in-stock | 1 | 2 | Fulfillment type mismatch |
| In-stock and pre-order, same fulfillment | all delivery | in-stock + pre-order | 1 | 2 | Availability mismatch |
| All pre-order | all delivery | all pre-order | 1 | 1 | |
| Two delivery addresses | all delivery | all in-stock | 2 | 2 | Multiple delivery addresses |
| Three delivery addresses | all delivery | all in-stock | 3 | 3 | Multiple delivery addresses |
| Pickup + pre-order alongside in-stock delivery | pickup + delivery | in-stock + pre-order | 1 | ? | Open — does pickup+preorder compound the split count? |
| Multiple pre-order dates | all delivery | pre-order: 2 different dates | 1 | ? | Open — does each distinct date create its own shipment? |
| Backordered vs pre-order distinction | all delivery | in-stock + backordered | 1 | ? | Open — is "backordered" treated the same as "pre-order" for splitting purposes? |

---

## Warehouse Selection (Minimising Shipments)

This table covers the case where a single item (or homogeneous group) needs to be sourced across warehouses.

| Scenario | Qty needed | Warehouse A stock | Warehouse B stock | Warehouse C stock | Shipments? | Source warehouses? |
|---|---|---|---|---|---|---|
| One warehouse has all stock | 6 | 6 | 3 | 2 | 1 | A |
| No single warehouse sufficient, two are | 6 | 4 | 3 | 2 | 2 | A (4), B (2) |
| Three-warehouse combo avoidable by two | 6 | 3 | 3 | 1 | 2 | A (3), B (3) — not A+B+C |
| Three warehouses required | 6 | 2 | 2 | 2 | 3 | A (2), B (2), C (2) |
| Tie between equally good two-warehouse combos | 6 | 4 | 4 | 4 | 2 | ? | Open — A+B, A+C, or B+C? Is there a tiebreaker (proximity, cost)? |
| Total stock insufficient | 6 | 1 | 2 | 1 | ? | ? | Open — reject order, backorder shortfall, or partial fulfillment? |

---

## Companion Product Constraint

| Scenario | Items | Companion group | Warehouse A stock | Warehouse B stock | Shipments? | Notes? |
|---|---|---|---|---|---|---|
| Companions at same warehouse | Camera body + Kit lens | companions | both | neither | 1 | Ship together from A |
| Non-companions sourced from different warehouses | Camera body + Memory card | none | body only | card only | 2 | Independent items — split is fine |
| Companions available at multiple warehouses | Camera body + Kit lens | companions | both | both | 1 | Either warehouse valid; one shipment regardless |
| Companion + standalone, all at one warehouse | Camera body + Kit lens + Bag | body+lens companions, bag standalone | all three | none | 1 | |
| Companion + standalone, companion at A, standalone at B | Camera body + Kit lens + Bag | body+lens companions, bag standalone | body + lens | bag | 2 | Companions must stay at A; bag ships from B |
| Companions split across warehouses — no single location has both | Camera body + Kit lens | companions | body only | lens only | ? | Open — backorder until co-located? Block order? Surface to customer? |
| Companion with fulfillment type mismatch | Camera body (delivery) + Kit lens (pickup) | companions | both at store A | — | ? | Open — can companions span fulfillment types, or is that an input validation error? |

---

A few things worth resolving before implementation starts:

1. **Multiple pre-order dates** — is each distinct release date its own shipment group, or does everything pre-order collapse into one?
2. **Backordered vs pre-order** — do they trigger the same split behaviour?
3. **Warehouse tiebreaking** — when multiple minimum-shipment combinations exist, what decides which to use?
4. **Insufficient total stock** — partial fulfillment, backorder the shortfall, or block the order entirely?
5. **Companions with no shared location** — this is the most consequential open question. The constraint says they *must* ship together, but if no warehouse stocks both, the order can't be fulfilled as-is. What's the policy?
6. **Companions across fulfillment types** — if a companion product is configured for pickup and the other for delivery, is that an order configuration error or does one fulfillment type take precedence?