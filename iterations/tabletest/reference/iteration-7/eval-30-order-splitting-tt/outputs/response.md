Hand-authored reference answer for eval-30, from
`products/claude-plugin/plans/slice-10-reference-answers-remaining-evals.md` section Findings.
Not agent output. Must never be read as a skill measurement.

Four `@TableTest` methods, one per splitting concern, with fulfillment type and delivery address
sharing one table because both answer "same shipment iff same type and address". Every table holds
the other facets fixed so a row varies exactly one thing.

Outputs are native TableTest collections throughout:

- unkeyed shipments as a set of sets — `{{camera, lens}, {watch}}`, so neither shipment order nor
  within-shipment order is part of the rule;
- keyed shipments as a map from the tag to a product set — `[W1: {camera, lens}]`,
  `[IMMEDIATE: {camera}, WHEN_AVAILABLE: {lens}]`, the availability map keyed by the enum itself.

Items use a compact `product/type/address` shorthand through a `@TypeConverter`, with a slash rather
than a colon so no cell needs quoting; a pickup carries no address at all. Warehouse stock is one
column, `[W1: {camera, lens}, W2: {mic}]`, converted to a `WarehouseInventory` — the same notation the
result column uses, so a reader compares what was available against what was chosen without
translating. A converter reads one cell, so this only works because the whole inventory is one
column; three per-warehouse columns would have to be assembled in the test instead.

The availability table keeps its two `StockStatus` columns and arranges its fixture in the body,
because **a class may hold only one converter per target type**: a second one returning
`WarehouseInventory` from a differently shaped cell would collide on the erased type.

Row counts come from the coverage obligations rather than from permutation. The warehouse table
carries the four-product row where `{W1, W3}` is the only two-warehouse cover, which a greedy picker
fails; the companion table carries a genuine tie between `{W1, W2}` and `{W2, W3}` that only the
companion rule breaks, plus the case where the preference yields and the case where it costs nothing.
Companions are declared on the `Order`, not on the inventory — one source of truth, stated.

The splitter stays the supplied stub, per the prompt, so the tests compile and fail at run time.
