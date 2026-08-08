# Analysis to-do — tabletest, iteration 79

**The runner's ⛔ VOID COMPARISON is a baseline-default artefact, not a failed run.** It compares
against iteration-78, which ran eval-25 only. The three evals here have three different live
baselines, so no single `--compare-iteration M` re-report works. Comparisons below are by hand
against the baselines `check-baseline.js` resolves. Generation and grading succeeded; nothing errored
or truncated (`failure_kind: none` on all three, eval-30 at 617s against the 900s ceiling).

| Eval | Baseline | Skill commit | Score | Delta |
|---|---|---|---|---|
| 20 collections-and-quoting | it-62 | `857c996` | 17/17 → **17/17** | held |
| 29 shopping-cart-tt | it-65 | `fbb1126` | 26/32 → **28/32** | +3 won, 1 lost |
| 30 order-splitting-tt | it-66 | `fbb1126` | 26/26 → **23/26** | **3 lost** |

Spend: $5.24 notional generation (subscription), $0.89 grading — 1.32x the $3.96 predicted.

## eval-29 — the primary prediction confirmed

**WON `type-converters-for-complex-objects`.** it-65 built the domain object in the method body
(`Cart cart = Cart.empty().withActiveCouponCode(activeCouponBefore);`). it-79 has **five
`@TypeConverter` methods** (Cart, ProductCatalogue, InventoryService, CouponStore, Coupon) and no
construction in any body. This is the slot repairs 7 and 8 target, and **it is the first host outside
eval-25 where their mechanism reaches the artefact.**

**WON `no-duplicate-rows-within-a-table` and `rule-falsifiable-by-a-row`** alongside it.

**LOST `scenario-names-describe-conditions`** — names paraphrasing the outcome column
(*'Rejects a product not in the catalogue'* beside `Success? false`). **This is repair 7's known
mechanical cost** (§ J34, § J37: merged tables fail this slot, split tables pass it), now reproduced
on a second host. The trade is the same one, not a new defect.

**`concern-not-over-split` still fails, but its cause changed.** it-65 failed on success/rejection
split into two methods; it-79 fails on `floorsCartTotalAtZero` duplicating
`computesCartTotalByCouponType`'s fixture and Total? column. **Same pattern as eval-25 in § J38** —
the repairs clear the old cause and a different one takes its place. Two hosts now.

`quantifier-covered-by-rows` and `consistent-quantity-naming` unchanged, as predicted.

## eval-30 — three losses from a perfect baseline, and they are two decisions

All three checked against it-66's `evidence` field first: **none is a grader flip.** The baseline
evidence describes a different structure from the one it-79 wrote, in all three.

### Decision 1 — Table 4 changed job, costing two slots

it-66's four tables ended with `shipsCompanionProductsFromTheSameWarehouseWhenPossible`. it-79's four
end with `keepsCompanionsApartAcrossHigherPrecedenceSplits`, an integration table on the real
`splitOrder` entry point, and companion preference is folded into
`selectsWarehousesMinimisingShipmentsWithCompanionPreference`.

- **LOST `concern-companion-products`** — companion grouping has no table of its own.
- **LOST `no-table-reproves-another`** — the integration table re-proves fulfilment-type and
  availability splitting, both already shown.

**The narration names the cause, and it is the composite-cell constraint** (narration:121):
*"`WarehouseInventory` also needs companion groups, but **a converter only accepts a single
parameter**, so I can't feed two separate table columns into one converter. I'd either need to
combine both the stock map and companion groups into a single composite cell, or treat companion
groups as a separate concern."* It tried the composite cell and rejected it on the type
(narration:167): *"The converter parameter type gets messy with nested generics like
`Map<String, Map<String, Map<String, String>>>`."* It then used two columns and a method-body
helper, rationalising past *Custom Type Converters*: *"this isn't violating the converter rule
because I'm not mapping a single table cell to the domain object — I'm combining two distinct
columns."*

**It also knew the integration table was redundant and shipped it anyway** (narration:117):
*"its main contribution is demonstrating precedence, which is somewhat covered elsewhere … integration
tests should only earn their place when they genuinely show something new."*

### Decision 2 — a value set that was not used

**LOST `no-duplicate-rows-within-a-table`** — `splitsItemsByStockAvailability` writes
*'Backordered item alongside an in-stock item'* and *'Pre-ordered item alongside an in-stock item'*
as separate rows for one obligation. it-66 covered the same three obligations with a value set
("3 rows match 3 obligations via value-set"). The agent knew the two states were one: it added a
`@Description` saying backordered and pre-ordered form a single pending shipment (narration:199),
then gave them a row each.

### What this is evidence for

**A nested composite has no stated route.** The section *Putting a Composite Value in a Cell* covers
an object whose parts are scalars, and names exactly one reason to break it apart (a field needing a
value set). `WarehouseInventory` is an object whose parts are themselves a map and a set of groups —
one level deeper — and the map-cell shape does not scale to it. The agent invented a route, argued
itself past the converter rule, and the table structure degraded around the invention.

**Not repaired here.** Writing new guidance inside a closing run is what § J37 refused for repair 8;
the same applies. Recorded as a candidate repair with its host, its narration lines and its
falsifier in the plan, § J40 RESULT.

**Attribution limit:** all three evals span the entire batch (`fbb1126..HEAD` is ten commits on
`skills/tabletest` and `shared/`). Every line above names a candidate, not a cause.
