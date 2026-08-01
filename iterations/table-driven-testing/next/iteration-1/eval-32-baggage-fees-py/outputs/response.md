No problem — no git action needed here.

## Summary

Created `tests/test_baggage.py` with one table-driven pytest test, `test_charges_checked_bag_fee_by_weight`, covering the fee rule as a single tier ladder with an accept/reject boundary at the top (one rule per the skill's "an accept/reject boundary is one rule and stays in one table" exception):

| weight_kg | fee_eur | error | id |
|---|---|---|---|
| 23 | 0 | — | at the free allowance limit |
| 24 | 75 | — | just over the free allowance limit |
| 32 | 75 | — | at the heavy-bag limit |
| 33 | 150 | — | just over the heavy-bag limit |
| 45 | 150 | — | at the oversize limit |
| 46 | — | `BagNotAccepted` | just over the oversize limit |

A `_fee_or_error` helper captures both the return value and any raised exception as a tuple, so the test body only arranges/acts/asserts with no branching. Every tier and every boundary between adjacent tiers is exercised from both sides.

Assumption (documented in the test's docstring): weights are positive kilogram integers; behavior at 0 kg or below is out of scope since the rules don't define a lower bound.

Verified: `pytest tests/test_baggage.py -v` collects all 6 cases cleanly and fails them against the still-unimplemented stub (raises `NotImplementedError` instead of the expected fee/`BagNotAccepted`) — exactly the expected pre-implementation state. `src/baggage.py` was left untouched.