All 11 tests collect cleanly and fail as expected against the unimplemented stub (each hits `NotImplementedError` from `fee_for_checked_bag`), confirming imports and collection work correctly.

Wrote `tests/test_fee_for_checked_bag.py` with two parametrized tests covering:
- **Within-limits fee tiers**: boundaries at 23 kg (no fee), 24–32 kg (€75 heavy-bag fee), 33–45 kg (€150 oversize fee), each with an inside-range and boundary case.
- **Rejection**: 46 kg and 60 kg both raise `BagNotAccepted`.

Assumption: weight is an integer in kg as implied by the stub's `int` type hint, so boundary values (23, 32, 45) are exact int cutoffs rather than needing fractional testing.