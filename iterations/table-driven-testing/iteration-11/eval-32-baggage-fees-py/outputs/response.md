All 6 tests collect cleanly and fail as expected against the unimplemented stub. `src/baggage.py` was left untouched.

Wrote `tests/test_baggage.py` with two table-driven tests:

- **`test_charges_the_fee_for_the_bags_weight_tier`** — the fee ladder, one case per tier boundary (23/24, 32/33, 45), since the tier rule stands on its own without the rejection case.
- **`test_rejects_a_bag_over_the_oversize_limit`** — the rejection, kept separate per the "strike the rejected case, does the rest still state a rule?" test (it does — the ladder survives), using `pytest.raises(BagNotAccepted)` with the boundary case just past 45 kg (46 kg); the 45 kg accept-side boundary already lives in the first table.

Assumption stated in the module docstring: `weight_kg` is a whole number of kilograms, and each tier's stated upper limit is inclusive.