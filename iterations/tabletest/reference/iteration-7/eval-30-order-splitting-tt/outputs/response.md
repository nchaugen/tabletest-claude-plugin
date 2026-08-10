Hand-authored reference answer for eval-30, from
`products/claude-plugin/plans/slice-10-reference-answers-remaining-evals.md` section Findings.
Not agent output. Must never be read as a skill measurement.

Four `@TableTest` methods, one per splitting concern, with fulfillment type and delivery address
sharing one table because both answer "same shipment iff same type and address". Every table holds
the other facets fixed so a row varies exactly one thing.

Outputs are native TableTest collections throughout:

every result column is a map from the tag that caused the split to the products in that shipment —
`[DELIVERY@Addr-A: {camera}, PICKUP: {mug}]`, `[IMMEDIATE: {camera}, WHEN_AVAILABLE: {lens}]`,
`[W1: {camera, lens}]`. Keying the fulfilment table the same way makes the result say *why* it split
rather than only that it did, and the key is written exactly as the Items column writes it. Note the
one asymmetry: the warehouse and availability keys are read off the `Shipment` the splitter produced,
while the fulfilment key's address is reconstructed from the shipment's items, so that half of it
cannot fail independently. Kept deliberately — a published table is read without the input beside it.

Items are a map keyed by product, each value naming how that item is fulfilled —
`[camera: DELIVERY@Addr-A, mug: PICKUP]`. The product/fulfilment split is the parser's own `k: v`, so
only the address needs a separator, and a pickup has no slot for an address rather than an empty one.

Warehouse stock stays **one column per warehouse**, so the coverage a row sets up can be read down
the column — which is what those two tables are for, and an empty `{}` says that warehouse holds
nothing rather than padding the row. That shape rules a `@TypeConverter` out: a converter reads one
cell and the inventory spans three, so the three sets are assembled by a named helper instead. The
one-cell alternative was tried and rejected on legibility.

Row counts come from the coverage obligations rather than from permutation. The warehouse table
carries the four-product row where `{W1, W3}` is the only two-warehouse cover, which a greedy picker
fails; the companion table carries a genuine tie between `{W1, W2}` and `{W2, W3}` that only the
companion rule breaks, plus the case where the preference yields and the case where it costs nothing.
Companions are declared on the `Order`, not on the inventory — one source of truth, stated.

The splitter stays the supplied stub, per the prompt, so the tests compile and fail at run time.
