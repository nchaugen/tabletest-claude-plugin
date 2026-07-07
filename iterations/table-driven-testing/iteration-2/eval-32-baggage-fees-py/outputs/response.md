All 8 tests collect cleanly and fail as expected (`NotImplementedError` from the stub), confirming they're valid, well-formed tests that will pass once the fee logic is implemented.

I wrote `tests/test_fee_for_checked_bag.py` with two table-driven tests:

1. **`test_fee_for_checked_bag_by_weight`** — 6 rows covering all three fee tiers with both sides of each boundary (22/23, 24/32, 33/45 kg → 0/75/150 euros).
2. **`test_fee_for_checked_bag_rejects_over_oversize_limit`** — 2 rows in a separate `pytest.raises(BagNotAccepted)` test (46 kg at the boundary, 80 kg well beyond), kept apart from the value-returning cases per the skill's exception-separation rule.

The stub in `src/baggage.py` was left untouched (`raise NotImplementedError`), and all 8 tests fail on that as intended rather than erroring on import/collection.