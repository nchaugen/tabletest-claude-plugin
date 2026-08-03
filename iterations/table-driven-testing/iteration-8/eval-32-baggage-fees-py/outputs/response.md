All 6 tests collect cleanly and fail on `NotImplementedError` as expected (not on import errors), confirming the stub is untouched and tests are correctly wired.

I wrote `tests/test_baggage.py` with two tests:

1. **`test_charges_the_tier_fee_matching_bag_weight`** — a parametrized ladder table covering the fee tiers, with cases at every tier boundary (23/24 kg for included→heavy, 32/33 kg for heavy→oversize, and 45 kg at the top of oversize).
2. **`test_rejects_a_bag_over_the_oversize_limit`** — a separate `pytest.raises(BagNotAccepted)` test for 46 kg, kept apart from the fee ladder per the table-driven-testing convention that a value-returning tier ladder and an exception-raising rejection are different concerns (the ladder still states a full rule with the rejection case removed).

Assumption noted: only boundary weights are used per tier (no extra mid-tier values), since each boundary case already pins down that tier's fee and additional interior values would be redundant.