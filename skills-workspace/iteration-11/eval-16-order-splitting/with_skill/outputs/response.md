Looking at the domain you've described, I see three distinct concerns that each deserve their own table:

1. **Shipment grouping** — what triggers a split (fulfillment type, availability, address)
2. **Warehouse allocation** — which warehouses to pick to minimise shipment count
3. **Companion product constraint** — keeping paired items together

---

## Table 1: Shipment Grouping

| Scenario | Items | Fulfillment Types | Availability | Delivery Addresses | Shipments? | Split reason? |
|---|---|---|---|---|---|---|
| Single in-stock home delivery | Shirt | Home delivery | In stock | 123 Main St | 1 | — |
| All store pickup, one location | Book, Mug | {Store pickup, Store pickup} | {In stock, In stock} | Store A, Store A | 1 | — |
| Mixed fulfillment types | Shirt, Book | Home delivery, Store pickup | {In stock, In stock} | 123 Main St, Store A | 2 | Fulfillment type |
| In-stock and backordered, same address | Shirt, Jacket | {Home delivery, Home delivery} | In stock, Backorder est. 15 Apr | 123 Main St, 123 Main St | 2 | Availability |
| All backordered, same date and address | Shirt, Jacket | {Home delivery, Home delivery} | {Backorder est. 15 Apr, Backorder est. 15 Apr} | 123 Main St, 123 Main St | 1 | — |
| Backordered items with different dates | Shirt, Jacket, Mug | Home delivery | In stock, Backorder est. 15 Apr, Backorder est. 30 Apr | 123 Main St | 3 | Availability date |
| Different delivery addresses, same fulfillment | Shirt, Book | {Home delivery, Home delivery} | {In stock, In stock} | 123 Main St, 456 Oak Ave | 2 | Delivery address |
| Different address and different fulfillment type | Shirt, Book, Mug | Home delivery, Store pickup, Home delivery | {In stock, In stock, In stock} | 123 Main St, Store A, 456 Oak Ave | 3 | Fulfillment + address |
| Same address, same fulfillment, same availability | Shirt, Book, Mug | Home delivery | {In stock, In stock, In stock} | 123 Main St | 1 | — |

**Open questions:**
- If two backordered items have different estimated dates but the later one arrives before the first ships (e.g. stock replenished early), do they consolidate?
- For multiple store pickup locations (Store A and Store B), does each become a separate "shipment" or a separate pickup notification?

---

## Table 2: Warehouse Allocation (Minimising Shipments)

| Scenario | Item | Qty ordered | Warehouse stock | Warehouses selected? | Shipments? |
|---|---|---|---|---|---|
| Single warehouse covers full order | Shirt | 4 | Chicago: 6 | Chicago | 1 |
| Exact match at one warehouse | Shirt | 3 | Chicago: 3 | Chicago | 1 |
| Two warehouses needed, optimal pair exists | Shirt | 6 | Philadelphia: 3, Washington: 3, Chicago: 2, LA: 1 | Philadelphia, Washington | 2 |
| Three warehouses needed, no better combination | Shirt | 6 | Chicago: 4, LA: 1, Houston: 1 | Chicago, LA, Houston | 3 |
| Suboptimal allocation avoided | Shirt | 6 | Philadelphia: 3, Washington: 3, Chicago: 4, LA: 1, Houston: 1 | Philadelphia, Washington | 2 |
| One warehouse has surplus — no split needed | Shirt | 4 | Chicago: 4, LA: 3 | Chicago | 1 |
| Total stock across all warehouses insufficient | Shirt | 10 | Chicago: 4, LA: 3 | — | Unfulfillable |
| Exact stock spread across many warehouses | Shirt | 4 | Chicago: 1, LA: 1, Houston: 1, Dallas: 1 | Chicago, LA, Houston, Dallas | 4 |

**Open question:**
- When two combinations tie on shipment count (e.g. {Chicago+LA} and {Houston+Dallas} both satisfy the order with 2 shipments), what is the tiebreaker — proximity to the delivery address, cost, or arbitrary?

---

## Table 3: Companion Product Grouping

| Scenario | Items | Companion pairs | Warehouse stock | Companions ship together? | Shipments? |
|---|---|---|---|---|---|
| Companions available at same warehouse | Camera body, Camera lens | body + lens | Chicago: body ×1, lens ×1 | yes | 1 (Chicago) |
| Companions only available at separate warehouses | Camera body, Camera lens | body + lens | Chicago: body ×1; LA: lens ×1 | no — unavoidable | 2 |
| Companions and non-companion all at same warehouse | Camera body, Camera lens, Memory card | body + lens | Chicago: body ×1, lens ×1, card ×1 | yes | 1 (Chicago) |
| Non-companion item forces companion to split warehouse | Camera body, Camera lens, Memory card | body + lens | Chicago: body ×1, lens ×1; LA: card ×1 | yes (card ships separately) | 2 |
| Companion available together but would require extra shipment vs. allocation minimum | Camera body, Camera lens, Shirt | body + lens | Chicago: body ×1, lens ×1; LA: shirt ×1, lens ×1 | yes (companion constraint wins over minimisation) | 2 — companions from Chicago, shirt from LA |
| One companion unavailable anywhere | Camera body, Camera lens | body + lens | Chicago: body ×1; nowhere: lens | — | Unfulfillable or hold? |
| Non-companion items, optimal allocation happens to place them together | Shirt, Mug | none | Chicago: shirt ×1, mug ×1 | n/a | 1 |

**Open questions:**
- When a companion pair cannot ship together (unavoidable split), do both items still ship, or is the whole companion pair held until they can go together?
- Can there be companion groups of three or more items, or is it always pairs?
- If companion items have different backorder dates, do they hold for the later date so they arrive together, or ship as soon as each is available?

---

The companion constraint and the minimisation goal can conflict (row 5 of Table 3 shows this). Worth deciding explicitly: **companion grouping takes priority over minimising shipment count** — is that correct?