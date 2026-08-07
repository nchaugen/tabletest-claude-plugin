Wrote `tests/test_library_fees.py` with one parametrized test, 9 cases covering:

- the on-time/late boundary (day 0 → day 1),
- ordinary accrual at 50¢/day,
- the €20 cap boundary at 40 days (and one day past it),
- the same ladder for children's-section books at half rate, with its own cap boundary at 80 days (since the discount shifts where the cap kicks in).

Assumptions are documented in the module docstring: `days_late >= 0` (0 = on/before due date), and the cap applies after the children's discount. The stub is untouched — all 9 cases collect cleanly and fail on `NotImplementedError` as expected.