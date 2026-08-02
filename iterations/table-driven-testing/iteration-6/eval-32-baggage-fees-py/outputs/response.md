All 6 tests collect cleanly and fail (as expected) against the unimplemented stub, and the stub in `src/baggage.py` was left untouched.

I wrote `tests/test_baggage.py` with two tables, split per the rejection rule (a fee ladder with a rejection case at the end is two concerns — striking the reject case still leaves a meaningful ladder):

- `test_computes_checked_bag_fee_from_weight` — the fee ladder, boundaries at each tier transition: 23/24 (included→heavy) and 32/33 (heavy→oversize), plus 45 (top of oversize).
- `test_rejects_bags_over_the_oversize_limit` — `pytest.raises(BagNotAccepted)` at 46kg (just past the 45kg limit).

Assumption: the lower/open end of the included tier (0–23kg) needed no extra boundary case, since only the 23kg upper edge is meaningful to the rule — mirrored on the accepted side (45kg) already exercised in the fee table, so the reject test only needed the first rejected value.