All 6 cases collect cleanly and fail exactly as expected — `NotImplementedError` from the stub, not an import/collection error.

## Summary

Wrote `tests/test_baggage.py` with two table-driven pytest tests, split per the "tier ladder + rejection" pattern (removing the rejection case still leaves a coherent fee-schedule rule, so it's a separate concern):

1. **`test_charges_fee_by_checked_bag_weight`** — 5 cases covering every tier boundary from both sides: 23/24 (no-fee → heavy-bag), 32/33 (heavy-bag → oversize), and 45 (top of oversize, last accepted weight).
2. **`test_rejects_bags_over_the_oversize_limit`** — 1 case (46 kg, just past the last accepted weight) asserting `BagNotAccepted` via `pytest.raises`.

**Assumption stated in the design:** the stub types `weight_kg` as `int`, so I treated kilograms as the finest distinguishable unit (boundaries are whole-kg pairs like 23/24) rather than assuming fractional weights.

Both tests currently fail with `NotImplementedError` from the stub — collection is clean, so they're ready to drive the implementation.