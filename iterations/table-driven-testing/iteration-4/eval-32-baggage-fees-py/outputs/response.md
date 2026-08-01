All 10 collect and fail as expected against the unimplemented stub — no collection errors, `src/baggage.py` untouched.

Wrote `tests/test_baggage.py` with two parametrized tests:
- `test_fee_for_checked_bag_by_weight` — the fee-tier table, covering all three tiers and both sides of each boundary (23/24, 32/33), plus the inclusive upper edge (45kg → 150).
- `test_fee_for_checked_bag_rejects_over_oversize_limit` — a separate `pytest.raises(BagNotAccepted)` test for weights over 45kg, kept apart from the value table per the skill's rule against mixing exception cases with return-value cases.

Assumption stated in the row choice: boundaries are inclusive on the lower side of each tier (23 → no fee, 32 → 75, 45 → 150), with rejection starting strictly above 45kg, matching the "over X and up to Y" phrasing in the rules.