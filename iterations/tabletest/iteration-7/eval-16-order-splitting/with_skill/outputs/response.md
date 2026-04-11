Four concerns here: what triggers a split, how to select warehouses, how companion constraints interact, and what to do at the edges. I'll give you one table per concern — these don't belong in one mega-table.

---

## 1. Order Shipment Grouping

Two items can share a shipment only when they agree on fulfillment type, availability window, and destination. The grouping cascade applies those three filters in turn.

| Scenario | Order contents | Shipment count? | How grouped? |
|---|---|---|---|
| Single destination, all in stock | 3 shirts — home delivery, in stock, 123 Main St | 1 | All together |
| Pickup alongside home delivery | Shirt (home delivery, in stock) + Mug (store pickup, in stock) | 2 | Shirt → home delivery; Mug → store pickup |
| Two pickups, same store | Shirt + Jeans — store pickup, in stock, Store A | 1 | Together at Store A |
| Two pickups, different stores | Shirt (store pickup, Store A) + Jeans (store pickup, Store B), in stock | 2 | One per pickup location |
| In-stock item + pre-order, same address | Shirt (home delivery, in stock) + Book (home delivery, pre-order Apr 1), 123 Main | 2 | Shirt ships now; Book ships Apr 1 |
| In-stock item + backorder, same address | Shirt (home delivery, in stock) + TV (home delivery, backorder), 123 Main | 2 | Shirt ships now; TV when restocked |
| Personal item + gift, different addresses | Shirt (home, in stock, 123 Main) + Gift set (home, in stock, 456 Elm) | 2 | One per destination |
| All three split triggers at once | Shirt (home, in stock, 123 Main) + Mug (pickup, in stock, Store A) + Book (home, pre-order, 123 Main) | 3 | Each group separate |
| Multiple pre-orders, same release date | Book A (home, pre-order Apr 1) + Book B (home, pre-order Apr 1), 123 Main | 1 | Same availability window |
| Multiple pre-orders, different release dates | Book A (home, pre-order Apr 1) + Book B (home, pre-order May 1), 123 Main | ? | **Open: one deferred group, or split by date?** |
| Pre-order alongside backorder, same address | Book (home, pre-order) + TV (home, backorder), 123 Main | ? | **Open: treated as same deferred group?** |

---

## 2. Warehouse Selection (Minimising Shipments)

Applies once a single logical shipment group has been identified. The goal is to cover the full ordered quantity using as few warehouses as possible.

| Scenario | Quantity ordered | Warehouse stock | Warehouses selected? | Shipments? |
|---|---|---|---|---|
| One warehouse covers full quantity | 6 shirts | Chicago 6, LA 3, Houston 2 | Chicago | 1 |
| Two-warehouse solution exists; avoid three | 6 shirts | Chicago 4, LA 1, Houston 1, Philly 3, DC 3 | Philly + DC | 2 |
| Only a two-warehouse combination works | 6 shirts | Chicago 4, LA 2 | Chicago + LA | 2 |
| Multiple valid two-warehouse pairs | 6 shirts | Chicago 4, LA 2, Philly 3, DC 3 | ? | 2 | **Open: which pair to prefer? (cost, proximity, stock balance?)** |
| No two-warehouse solution; three required | 6 shirts | Chicago 3, LA 2, Houston 2, Atlanta 1 | Chicago + LA + Houston | 3 |
| Total stock across all warehouses falls short | 6 shirts | Chicago 2, LA 2, Houston 1 | — | ? | **Open: partial fulfilment or reject order?** |

---

## 3. Companion Product Co-location

Companions must be considered during warehouse selection. Where possible, companions ship from the same location; the "if possible" qualifier needs to be pinned down for the cases below.

| Scenario | Items | Companion relationship | Warehouse inventory | Co-located? | Notes? |
|---|---|---|---|---|---|
| Both companions at same warehouse | Camera body + 50mm lens | yes | Chicago: body + lens | yes | |
| One warehouse has both; another has partial | Camera body + 50mm lens | yes | Chicago: body + lens, LA: body only | yes | Chicago preferred; splits avoided |
| Companions at different warehouses, no overlap | Camera body + 50mm lens | yes | Chicago: body only, LA: lens only | no | Co-location impossible; ships separately |
| Non-companions split across warehouses | Shirt + Coffee mug | no | Chicago: shirt, LA: mug | no | Two shipments; no companion constraint applies |
| Companion body in stock; lens on backorder globally | Camera body + 50mm lens | yes | Chicago: body; lens: unavailable | ? | **Open: hold body until lens available, or ship body now?** |
| Both companions on backorder | Camera body + 50mm lens | yes | Body: backorder; Lens: backorder | — | **Open: ship together when both available, or allow independent fulfilment?** |
| Three-item companion set, one warehouse has all | Camera body + 50mm lens + battery grip | all three | Chicago: all three, LA: body + lens | yes (Chicago) | Full set available; prefer Chicago |
| Three-item companion set, no warehouse has all | Camera body + 50mm lens + battery grip | all three | Chicago: body + grip, LA: lens + grip | ? | **Open: best-effort two-warehouse, or require full co-location?** |

---

## Open questions to resolve before implementation

| # | Question | Why it matters |
|---|---|---|
| 1 | Are pre-orders with different release dates grouped (single deferred shipment) or split by date? | Determines number of shipments for mixed pre-order carts |
| 2 | Are pre-orders and backorders the same availability group, or distinct groups? | Affects grouping logic and customer messaging |
| 3 | When multiple minimum-shipment warehouse combinations are equally valid, what is the tiebreaker? (cost, proximity, stock balance) | Required to make warehouse selection deterministic |
| 4 | When total stock is insufficient to fill the order, do we partially fulfil or reject? | Affects error handling and cart validation |
| 5 | When a companion item is on backorder, do we hold its in-stock partner or ship it immediately? | Significant UX and inventory impact |
| 6 | Can a companion relationship span more than two items, and does the co-location constraint require all of them together, or a best-effort majority? | Changes companion grouping algorithm |