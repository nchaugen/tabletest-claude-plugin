All 14 tests collect cleanly and fail with `NotImplementedError` from the stub, as expected. The stub was left untouched.

Wrote `tests/test_library_fees.py` with three parametrized tests decomposing the rule set:

- **`test_on_time_return_has_no_fee`** — 0 days late, both sections, expect 0.
- **`test_late_fee_accrues_per_day_late`** — 50¢/day standard vs. 25¢/day children's, at 1/3/10 days, below the cap.
- **`test_fee_is_capped_at_20_euros`** — boundary rows (just under / at / just over 2000¢) for both rates, with the cap value in the row alongside the expected fee to make the threshold visible.

Stated assumptions (documented in the file's module docstring):
1. `days_late` is always ≥ 0; on-time is `days_late == 0` (no negative/early-return case, since it's unspecified).
2. The 20-euro cap is a single fixed ceiling applied the same way to both sections — the children's discount reduces the per-day rate, not the cap.