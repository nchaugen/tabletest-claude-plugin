All 15 tests collect cleanly and fail as expected (stub raises `NotImplementedError`), confirming the tests are well-formed before implementation exists.

Wrote `tests/test_baggage.py` with two parametrized tables covering `fee_for_checked_bag`:
- 12 cases for the fee tiers (0/1/22/23 → 0, 24/25/31/32 → 75, 33/34/44/45 → 150), each boundary and just-inside value covered
- 3 cases (46/50/100) asserting `BagNotAccepted` is raised

Assumption: `weight_kg` is always a non-negative integer per the existing stub's type hint, so no fractional-kg or negative-weight cases were added.