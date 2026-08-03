Order splitting decomposes into six distinct rules rather than one big table — each of your four scenarios plus the minimisation goal plus the companion-product constraint reads as a separate "and," so each gets its own table (per spec-by-example's one-rule-per-table guidance), with one small table at the end to show how they interact when they conflict.

**Assumption stated up front:** a "shipment" = one warehouse (or pickup location) fulfilling one fulfillment-method + delivery-address + availability-date group. This is what makes "minimise shipments" the same thing as "minimise distinct warehouses used per group." Flagging this because it's load-bearing for every table below.

---

### 1. Separates pickup items from delivery items

| Scenario | Items (Item: Fulfillment Method) | Shipments? |
|---|---|---|
| All items for pickup | A: Pickup, B: Pickup | {A, B} |
| All items for delivery | A: Delivery, B: Delivery | {A, B} |
| One pickup, one delivery | A: Pickup, B: Delivery | {A}, {B} |
| Two pickup, one delivery | A: Pickup, B: Pickup, C: Delivery | {A, B}, {C} |

---

### 2. Separates items by delivery address

*Held constant: all items are Delivery (pickup has no address to split on).*

| Scenario | Items (Item: Delivery Address) | Shipments? |
|---|---|---|
| All items to one address | A: 12 Main St, B: 12 Main St | {A, B} |
| Two items, two addresses | A: 12 Main St, B: 9 Oak Ave | {A}, {B} |
| Gift plus items for self | A: 12 Main St (self), B: 9 Oak Ave (gift recipient) | {A}, {B} |
| Three items, two addresses | A: 12 Main St, B: 12 Main St, C: 9 Oak Ave | {A, B}, {C} |

---

### 3. Separates available items from backordered items

*Held constant: single delivery address, all items Delivery.*

| Scenario | Items (Item: Availability) | Shipments? |
|---|---|---|
| All items in stock | Shirt: In Stock, Hat: In Stock | {Shirt, Hat} ships now |
| In-stock plus backorder | Shirt: In Stock, Jacket: Backorder (ETA 2026-09-01) | {Shirt} now; {Jacket} 2026-09-01 |
| In-stock plus pre-order | Shirt: In Stock, Console: Pre-order (release 2026-11-20) | {Shirt} now; {Console} 2026-11-20 |
| Two backorders, same ETA | Jacket: Backorder (2026-09-01), Boots: Backorder (2026-09-01) | {Jacket, Boots} 2026-09-01 |
| Two backorders, different ETAs | Jacket: Backorder (2026-09-01), Boots: Backorder (2026-10-15) | {Jacket} 2026-09-01; {Boots} 2026-10-15 |
| Three items, one now, two different future dates | Shirt: In Stock, Jacket: Backorder (2026-09-01), Boots: Backorder (2026-10-15) | {Shirt} now; {Jacket} 2026-09-01; {Boots} 2026-10-15 |

Pre-order and backorder are treated as the same "future-dated availability" category — the rule only cares about the date, not the label.

---

### 4. Minimises warehouse count when allocating stock

*Applies within a single fulfillment/address/availability group already established by Tables 1–3.*

| Scenario | Order Requirements | Warehouse Stock | Fulfilling Warehouses? | Shipment Count? |
|---|---|---|---|---|
| One warehouse covers it | Shirt: 6 | Chicago{Shirt:10} | Chicago{Shirt:6} | 1 |
| Only a 3-way combination exists | Shirt: 6 | Chicago{Shirt:4}, LA{Shirt:1}, Houston{Shirt:1} | Chicago{Shirt:4}, LA{Shirt:1}, Houston{Shirt:1} | 3 |
| 2-warehouse combination preferred over available 3-way | Shirt: 6 | Chicago{Shirt:4}, LA{Shirt:1}, Houston{Shirt:1}, Philadelphia{Shirt:3}, Washington{Shirt:3} | Philadelphia{Shirt:3}, Washington{Shirt:3} | 2 |
| Two unrelated items, one warehouse has both | Camera Body: 1, Tripod: 1 | Denver{Body:5, Tripod:5}, Atlanta{Body:5} | Denver{Body:1, Tripod:1} | 1 |
| Two unrelated items, no overlap | Camera Body: 1, Tripod: 1 | Denver{Body:5}, Atlanta{Tripod:5} | Denver{Body:1}, Atlanta{Tripod:1} | 2 |
| Stock insufficient to fully cover demand | Shirt: 6 | Chicago{Shirt:2}, LA{Shirt:1} | Chicago{Shirt:2}, LA{Shirt:1} | 2 (Unfulfilled: 3 → routes to Table 3 as backorder) |
| Two equally-minimal combinations exist | Shirt: 6 | Austin{Shirt:3}, Dallas{Shirt:3}, Reno{Shirt:2}, Boise{Shirt:4} | **? — open question** | 2 |

**Open question:** the last row has two valid 2-warehouse solutions (Austin+Dallas, Reno+Boise) with identical shipment count. No tie-break rule is defined yet — candidates would be customer proximity, warehouse priority/cost, or least excess inventory shipped. Needs a decision before this table can be finalised.

---

### 5. Keeps companion products together when a shared warehouse exists

| Scenario | Companion Items | Other Items | Warehouse Stock | Fulfilling Warehouses? | Shipment Count? |
|---|---|---|---|---|---|
| Shared warehouse has full stock of both | Body: 1, Lens: 1 | — | Denver{Body:5, Lens:5}, Seattle{Body:5} | Denver{Body:1, Lens:1} | 1 |
| No warehouse carries both | Body: 1, Lens: 1 | — | Denver{Body:5}, Seattle{Lens:5} | Denver{Body:1}, Seattle{Lens:1} | 2 |
| Companion affinity breaks a tie between equal-count options | Body: 1, Lens: 1 | Z: 1 | WH1{Body:1, Z:1}, WH3{Body:1, Lens:1} (Lens only at WH3) | WH3{Body:1, Lens:1}, WH1{Z:1} | 2 |
| Shared warehouse only partially covers the pair | Body: 2, Lens: 2 | — | WH1{Body:2, Lens:1}, WH2{Lens:1} | **? — open question** | ? |

**Open question:** when a shared warehouse can supply all of one companion item but only part of the other, it's undefined whether to (a) split by quantity — ship 1 Lens with the Body from WH1 and the remaining 1 Lens from WH2, or (b) keep Lens whole and source it entirely from WH2, splitting the pair completely. Needs a business decision.

Note: companion affinity never *increases* shipment count in these examples — it only chooses among equal-count options. Whether the business wants it to be allowed to cost one extra shipment in rarer cases is itself worth confirming; I didn't find a clean small example where it must, which suggests in practice it's a tie-breaker, not a hard override — worth validating that assumption with the domain owner.

---

### 6. Overrides companion grouping when a hard constraint conflicts

*This shows an interaction none of the tables above can show alone: precedence between the hard partition rules (Tables 1–3) and the soft companion preference (Table 5).*

| Scenario | Companion Items | Conflicting Constraint | Shipments? |
|---|---|---|---|
| Companions split by fulfillment method | Body: Pickup, Lens: Delivery | Fulfillment method differs | {Body} pickup; {Lens} delivery |
| Companions split by delivery address | Body: 12 Main St, Lens: 9 Oak Ave (gift) | Delivery address differs | {Body}; {Lens} |
| Companions split by availability | Body: In Stock, Lens: Backorder (2026-09-01) | Availability differs | {Body} now; {Lens} 2026-09-01 |
| No hard constraint conflicts | Body: Delivery/12 Main St/In Stock, Lens: Delivery/12 Main St/In Stock, both at Denver | none | {Body, Lens} from Denver |

**Precedence order:** fulfillment method → delivery address → availability date → warehouse minimisation → companion affinity. The first three are hard partitions items simply cannot share a shipment across; companion affinity only gets a say once you're down to allocating warehouses within one already-partitioned group.

---

### Open questions to resolve before implementation
1. Tie-break rule when multiple warehouse combinations achieve the same minimal shipment count (Table 4).
2. Behaviour when a shared warehouse can only partially co-locate a companion pair (Table 5) — partial split vs. full split.
3. Whether companion affinity is allowed to cost strictly more shipments than the minimal solution, or is purely a tie-breaker (noted under Table 5).

These six tables and the open-question list are ready to hand to `/tabletest` for executable test generation once you pick a language/framework — the column structure carries over directly.