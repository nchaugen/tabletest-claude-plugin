Five @TableTest methods, one per splitting concern: fulfillment type, delivery address, availability,
warehouse allocation, companion grouping. Each isolates its own rule by fixing the other facets, and
carries only the rows that rule needs — no cross-multiplication. The substance of this eval is that
decomposition. A secondary, and weaker, axis is *representation*: how order items and the resulting
shipments are rendered in the table. Both an order item and a shipment are compound values, and the
rewarded choice is to model them as TableTest's native collections, not as hand-rolled strings the
test then has to encode and decode.

## The concerns

`splitOrder(order, inventory)` applies five rules that compound in production but must be pulled apart
for testing. Each gets its own `@TableTest`, with the other four facets held constant so a row varies
exactly one thing.

| Concern | Governs | Held fixed to isolate it |
|---|---|---|
| **Fulfillment type** | delivery vs pickup items never share a shipment | all IN_STOCK, one warehouse, no companions |
| **Delivery address** | different addresses split; same address groups | all DELIVERY, IN_STOCK, one warehouse |
| **Availability** | in-stock ships now; backordered/pre-ordered ship when available, not held back | one address, one warehouse |
| **Warehouse allocation** | fewest-shipments cover of the order across warehouses | all DELIVERY to one address, all IN_STOCK |
| **Companion grouping** | companions ship from one warehouse *when possible* (a tie-breaker, not an override) | all DELIVERY to one address, all IN_STOCK |

Fulfillment type and delivery address can share one method (both are "same shipment iff same
(type,address)"), or be two — either reads as a clean split. The other three are genuinely separate:
availability, warehouse selection, and companion preference each need their own inputs.

## Modelling decisions this eval fixes

- **Warehouse allocation is set-cover over named products, not quantity-splitting.** `WarehouseInventory`
  records, per warehouse, *which products* it stocks (with a status) — there are no per-warehouse
  quantities. So the warehouse table's columns are product **sets** per warehouse, and product identity
  is the whole logic. (This is why a "scalar quantity" column is the wrong shape here and its assertion
  was removed.)
- **Companion grouping is a tie-breaker.** It only changes the outcome when two warehouse combinations
  tie for fewest shipments; it never forces an *extra* shipment to keep companions together, and it
  yields entirely when no single warehouse stocks both. The rows must prove all three: tie broken,
  preference honoured-for-free, preference abandoned when infeasible.
- **The model has two availability states, not three.** BACKORDERED and PRE_ORDERED both map to
  `WHEN_AVAILABLE`, so they combine into one shipment once the other facets match — there is no
  "ready at different times" split. This is not in the prompt; state it in an `@Description` and let a
  row (`Backordered and pre-ordered items combine`) make it falsifiable.

## Representation — items and shipments as native collections

Both compound values in this feature map naturally onto TableTest collections. Encoding them as strings
that the test parses and re-joins is the anti-pattern (the same one as eval-20's `[tech:java]` map trap
and eval-29's coupon column): it forces quoting, needs bespoke describe-helpers, and renders in the HTML
report as opaque quoted blobs rather than structured values a reviewer can read.

- **Order item** — a small fixed set of properties (product, fulfillment type, address). A compact
  per-item shorthand via a `@TypeConverter` is fine and preferable to a wide list-of-maps; the pitfall is
  the *separator*. A colon (`camera:DELIVERY:Addr-A`) collides with TableTest's map `k: v` syntax, so
  every element must be quoted. A non-colliding separator (`camera/DELIVERY/Addr-A`) drops the quotes and
  the column reads cleanly.
- **Shipments (the output)** — a shipment is an unordered *set of products*; the result is an unordered
  *set of shipments*, tagged for the keyed concerns by warehouse or availability. The honest shapes:
  - **Unkeyed** (fulfillment/address): a set of sets — `{{camera, lens}, {watch}}`. TableTest preserves
    the declared order for rendering, while `Set` equality compares by membership, so shipment order and
    within-shipment order are both irrelevant to the assertion with no sorting needed.
  - **Keyed** (availability, warehouse, companion): a map from the tag to a product set —
    `[W1: {camera, lens, mic}]`, `[IMMEDIATE: {camera, lens}]`. The map key is unique per shipment
    *within each isolated table* (one shipment per warehouse when all items are in stock; one per
    availability state at one warehouse), which is what lets a map represent the result.

  List-of-lists / map-of-lists is an equally acceptable ordered alternative; it just needs the helper to
  impose a canonical order (sort inner lists, and the outer list for the unkeyed table) so the
  order-sensitive `List` comparison is stable. Sets avoid that entirely and suit the set-semantics better.

  What the iteration-40 solution did instead — `["IMMEDIATE:[camera,lens]"]`, `["W1:[camera,lens]"]`,
  strings built by `describeByWarehouse`/`describeByAvailability` and canonicalised by sorting — passes
  every current assertion, but it is the inferior choice: structure smuggled into strings, opaque in the
  report. It is tolerated, not rewarded.

## Reference decomposition

Values verified. Shipments are shown set-style; a shipment is written as its product set.

**Fulfillment type & address** — items vary type and address; output is the set of shipments.

| Scenario | Items | Shipments? |
|---|---|---|
| Same type and address group together | camera (DELIVERY, Addr-A), lens (DELIVERY, Addr-A) | {{camera, lens}} |
| Different delivery addresses split | camera (DELIVERY, Addr-A), watch (DELIVERY, Addr-B) | {{camera}, {watch}} |
| Delivery and pickup always split | camera (DELIVERY, Addr-A), mug (PICKUP) | {{camera}, {mug}} |
| Two pickups share a shipment | mug (PICKUP), candle (PICKUP) | {{candle, mug}} |
| Address groups, others split | camera (DELIVERY, Addr-A), lens (DELIVERY, Addr-A), watch (DELIVERY, Addr-B) | {{camera, lens}, {watch}} |

**Availability** — one address, one warehouse; only stock status varies.

| Scenario | Items | Shipments? |
|---|---|---|
| All in stock ship together | camera (IN_STOCK), lens (IN_STOCK) | [IMMEDIATE: {camera, lens}] |
| In-stock not held for backordered | camera (IN_STOCK), lens (BACKORDERED) | [IMMEDIATE: {camera}, WHEN_AVAILABLE: {lens}] |
| Backordered and pre-ordered combine | camera (BACKORDERED), lens (PRE_ORDERED) | [WHEN_AVAILABLE: {camera, lens}] |
| Mixed across all states | camera (IN_STOCK), lens (BACKORDERED), mic (PRE_ORDERED) | [IMMEDIATE: {camera}, WHEN_AVAILABLE: {lens, mic}] |

**Warehouse allocation** — each warehouse column is the product set it stocks; choose the fewest-shipment cover.

| Scenario | Order | W1 stock | W2 stock | W3 stock | Shipments? |
|---|---|---|---|---|---|
| One warehouse covers everything | {camera, lens, mic} | {camera, lens, mic} | {camera} | {} | [W1: {camera, lens, mic}] |
| No single cover — split forced | {camera, lens, mic} | {camera, lens} | {mic} | {} | [W1: {camera, lens}, W2: {mic}] |
| Overlap must not cause a needless split | {camera, lens, mic} | {camera} | {camera, lens, mic} | {} | [W2: {camera, lens, mic}] |
| The *correct* pair, not just any pair | {camera, lens, mic, battery} | {camera, lens} | {lens, mic} | {mic, battery} | [W1: {camera, lens}, W3: {battery, mic}] |

The last row is the load-bearing one: `{W1, W3}` is the only two-warehouse cover of all four products
(`W1∪W2` misses battery, `W2∪W3` misses camera), and a greedy "W1 then W2" picker fails. It proves the
rule is a real minimisation, not an approximation.

**Companion grouping** — companions passed via `WarehouseInventory.addCompanionGroup`; a tie-breaker only.

| Scenario | Order | W1 stock | W2 stock | W3 stock | Companions | Shipments? |
|---|---|---|---|---|---|---|
| Companions win a tie | {camera, lens, mic} | {camera, lens} | {camera, mic} | {lens} | {camera, lens} | [W1: {camera, lens}, W2: {mic}] |
| Companions split when unavoidable | {camera, lens} | {camera} | {lens} | {} | {camera, lens} | [W1: {camera}, W2: {lens}] |
| Already together — no trade-off | {camera, lens} | {camera, lens} | {} | {} | {camera, lens} | [W1: {camera, lens}] |

In row 1 both `{W1, W2}` and `{W2, W3}` give two shipments, but only `{W1, W2}` keeps camera+lens in one
warehouse — the companion rule breaks the tie. Row 2 shows the preference yielding to feasibility (no
warehouse stocks both), and it must not manufacture a third shipment to satisfy the preference.

## Scenario coverage — the obligations each concern must hit

The "right number of rows" is a covering problem: each concern has a set of **coverage obligations**
(distinct behaviours the rule must demonstrate), each row covers some subset, and the optimal table is
the smallest row set whose union covers them all. Too few rows leaves an obligation uncovered — a wrong
implementation passes (a falsifiability gap); too many repeats an already-covered obligation or
cross-multiplies concerns (a `minimal-rows-per-concern` failure). This list is the universe to check a
solution's rows against; it is not a demand for exactly these rows.

- **Fulfillment type & address** — (a) same type + same address groups into one shipment; (b) different
  address splits; (c) different fulfillment type (delivery vs pickup) splits; (d) pickup items with no
  address still group (a null address is not a splitting key). One interaction case — grouping and
  splitting in the same order — is valuable but optional; it composes (a)+(b), it does not add a rule.
- **Availability** — (a) all in-stock → one immediate shipment; (b) an in-stock item is *not held* for a
  delayed item (the two ship separately); (c) delayed items group together, establishing that
  BACKORDERED and PRE_ORDERED are one state (the deliberately-surfaced assumption). A three-state
  composition row is optional. Note: a row proving "in-stock not held for pre-ordered" *separately* from
  "…for backordered" is **redundant** under the two-state model — it re-covers (b). Iteration-40 spends
  6 rows here where 4 obligations exist; the extra two demonstrate the state-collapse explicitly rather
  than carelessly, so it is mild over-coverage, not a decomposition error — but it is what the
  obligation lens flags.
- **Warehouse allocation** — (a) a single warehouse covers the order → 1 shipment; (b) no single cover →
  minimal forced split; (c) overlapping stock must not cause a *needless* split; (d) the correct minimal
  pair is found where a greedy pick fails. Four obligations, four rows — minimal and complete.
- **Companion grouping** — (a) companions break a tie between equally-minimal warehouse sets (the "when
  possible" as tie-breaker); (b) companions yield when no warehouse stocks both, without adding a
  shipment; (c) companions already together need no trade-off (the baseline). Three obligations, three
  rows.

## Underspecified → make it an explicit row

- **Backordered vs pre-ordered.** The model can't distinguish their ship times, so they combine; the
  `combine` row commits to that reading rather than silently assuming a third split.
- **Companion source of truth.** Both `Order` (a `companionGroups` constructor arg) and
  `WarehouseInventory` (`addCompanionGroup`) can carry companions. Pick one, state it in `@Description`,
  and be consistent — either is defensible; using both ambiguously is not.
- **Allocation ties within a chosen warehouse set.** When a product is stocked by two chosen warehouses
  (e.g. lens in W1 and W2 in the four-product row), the assignment is forced by which warehouses are in
  the minimal set; rows are constructed so the assignment is unique, avoiding an unspecified tiebreak.

## Out of scope, but foreseeable

- **Quantity-based partial fulfilment** (splitting one line across warehouses by count). The model has no
  per-warehouse quantities; inventing it contradicts the scaffolding.
- **Companion grouping across fulfillment types or addresses.** Companions are exercised only within the
  warehouse concern; whether a companion preference could ever override an address split is unspecified
  and need not be tested.
- **Exact warehouse identifiers.** `W1/W2/W3` are test data, not column names; naming them `East`/`West`
  changes nothing. The `business-language-columns` assertion is about column *names*, which are already
  domain language (`Items`, `W1 stock`, `Companions`, `Shipments?`).

## Judging

Score the decomposition first: five concerns in separate `@TableTest` methods, each holding the other
facets fixed and carrying only its own minimal rows, with all outputs of a concern in one table. The
warehouse and companion tables are where a weak solution over- or under-specifies — reward the unique
minimal-cover row and the genuine companion tie, penalise cross-multiplication.

Representation is the secondary axis and carries latitude. The rewarded shape models items and shipments
as native TableTest collections (sets for the unordered shipment/product semantics, or ordered
lists/maps with a canonical sort), with a non-colliding item separator so nothing needs quoting. A
string-encoded output (structure packed into `"W1:[...]"` strings with describe-helpers) is a real but
inferior choice: it passes today because no assertion rewards native collections, and it reads poorly in
the report. Do not treat its use as a decomposition failure — the concern separation is what this eval
scores first.

Two grading notes for this eval. `business-language-columns` judges column *names*, not cell values, and
the `?` suffix on output columns is the expected convention (see `has-question-mark-column`) — `Shipments?`
is correct, not a violation. And `scalar-quantity-for-warehouse` has been removed: product identity is the
warehouse rule here, so a scalar-quantity column would lose the logic, not clarify it.
