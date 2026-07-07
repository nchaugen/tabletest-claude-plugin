All 12 tests collect cleanly and fail on `NotImplementedError`, exactly as expected for a pre-implementation test suite.

Wrote `tests/test_library_fees.py` with a `pytest.mark.parametrize`-based table covering:
- on-time/early returns → no fee (including negative `days_late` for early returns)
- standard-rate late fees (50¢/day)
- children's-section half-rate late fees (25¢/day)
- the 20€ (2000¢) cap at, just past, and well past the threshold, for both rate tiers

Assumption stated in the test data: `days_late` can be ≤ 0 for on-time/early returns (not just exactly 0), and the cap applies identically regardless of section rate.