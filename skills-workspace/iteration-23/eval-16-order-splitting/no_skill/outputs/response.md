Since the write was blocked, here are the test cases as a structured overview you can use directly or adapt to any language/framework:

---

## Order splitting test cases

### 1. No split required
| # | Scenario | Expected shipments |
|---|----------|--------------------|
| 1.1 | Single item, one warehouse, in-stock | 1 |
| 1.2 | Multiple items, all at same warehouse, same address | 1 |

---

### 2. Fulfillment type split
| # | Scenario | Expected shipments |
|---|----------|--------------------|
| 2.1 | One SHIP\_TO\_HOME + one STORE\_PICKUP | 2 — always, even if same warehouse |
| 2.2 | Pickups at two different stores | 2 — one per store |
| 2.3 | Multiple items, all pickup at same store | 1 — grouped together |

---

### 3. Availability split
| # | Scenario | Expected shipments |
|---|----------|--------------------|
| 3.1 | In-stock + pre-order | 2 — in-stock ships now, pre-order ships on available date |
| 3.2 | In-stock + backordered | 2 |
| 3.3 | Two pre-orders, same availability date | 1 — can travel together |
| 3.4 | Two pre-orders, different availability dates | 2 — earlier must not be held |
| 3.5 | Three windows: now / soon / later | 3 — one per window |

---

### 4. Inventory distribution — minimise shipments
| # | Scenario | Expected shipments |
|---|----------|--------------------|
| 4.1 | One warehouse has full quantity | 1 — no unnecessary split |
| 4.2 | Need 6 shirts: CHI=4, LA=1, HOU=1, PHL=3, DCA=3 | 2 — PHL+DCA preferred over CHI+LA+HOU |
| 4.3 | Multiple equally-good two-warehouse pairs | 2 — any optimal pair acceptable |
| 4.4 | Total inventory across all warehouses < quantity ordered | Error / `InsufficientInventoryException` |
| 4.5 | Two different products each needing distribution | Minimised globally, not per-product greedily |

The key assertion for 4.2 is that the algorithm does **not** greedily take the warehouse with the highest individual stock and then fill the remainder — it finds the combination with the fewest warehouses.

---

### 5. Multiple delivery addresses
| # | Scenario | Expected shipments |
|---|----------|--------------------|
| 5.1 | Two items, different addresses, same warehouse | 2 — one per address |
| 5.2 | Multiple items, all to same address | 1 — grouped |

---

### 6. Companion products
| # | Scenario | Expected shipments | Notes |
|---|----------|--------------------|-------|
| 6.1 | Body + lens, same warehouse has both | 1 — co-located |  |
| 6.2 | Body + lens, PHL has both, CHI has body, LA has lens | 1 — must pick PHL | Prefer warehouse that stocks the group |
| 6.3 | Body + lens, only CHI has body, only LA has lens | 2 — unavoidable split; order still fulfilled | Not rejected |
| 6.4 | 2 bodies + 2 lenses, co-location would cost an extra shipment | 2 — minimise total shipments, companions may split | Soft constraint loses to hard minimisation constraint |
| 6.5 | Companions + non-companion item, all at same warehouse | 1 — non-companions unaffected by grouping logic |  |

---

### 7. Compound scenarios
| # | Split reasons combined | Expected shipments |
|---|------------------------|--------------------|
| 7.1 | Fulfillment type + availability | 3 (in-stock home / pre-order home / pickup at store) |
| 7.2 | Different addresses + different availability dates | 3 |
| 7.3 | Inventory distribution + different addresses | 2 (global minimisation still applies) |
| 7.4 | Companions + availability split (body in-stock, lens pre-order) | 2 — availability wins; advisory flag on both shipments noting companion split |

---

### Key constraints encoded by the tests

1. **Hard constraints** (always respected): fulfillment type, delivery address, availability window, single warehouse per shipment.
2. **Minimisation** (hard): fewest shipments overall — applies across all products and warehouses jointly, not per-product.
3. **Companion co-location** (soft): respected only when it does not increase shipment count. When availability forces a split, co-location is dropped and the shipments should carry an advisory flag so downstream notifications can warn the customer.

---

Want me to write this as a runnable JUnit 5 file, or in a different language/framework?