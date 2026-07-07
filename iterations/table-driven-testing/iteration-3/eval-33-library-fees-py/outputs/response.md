All 13 tests collect cleanly and fail red as expected (on `NotImplementedError`, not an import/collection error). The stub was left untouched.

Wrote `tests/test_library_fees.py` with four parametrized tests decomposing the rules:
- `test_late_fee_by_days_late` — base per-day fee (on-time, 1 day late, 5 days late)
- `test_late_fee_capped_at_20_euros` — cap boundary at 40 days (both sides + well past)
- `test_late_fee_childrens_section_half_rate` — half-rate discount
- `test_late_fee_cap_applies_regardless_of_section` — precedence: cap still hits at 2000 cents even at the halved rate

Assumptions are documented in the file's module docstring: `days_late` is non-negative (0 = on time), the 2000-cent cap is absolute and not itself halved for children's section, and "half rate" means 25¢/day against that same cap.