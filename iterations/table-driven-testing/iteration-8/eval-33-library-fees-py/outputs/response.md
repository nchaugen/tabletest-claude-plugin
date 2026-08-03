All 12 tests collect cleanly and fail as expected against the `NotImplementedError` stub (the stub itself is untouched).

I wrote `tests/test_library_fees.py` with three parametrized tests, one per concern:

1. **`test_returned_on_the_due_date_incurs_no_fee`** — the on-time boundary (`days_late=0`), checked regardless of section (value set).
2. **`test_computes_late_fee_from_days_late_and_section`** — the per-day rate (50¢, or 25¢ for children's), below the cap, at 1 day (just past the on-time boundary) and 10 days for each section.
3. **`test_caps_late_fee_at_twenty_euros`** — the cap as an interaction with the rate: for each section, one case just below the cap boundary, one exactly at it, and one just past it (day counts differ by section since the boundary point shifts with the rate).

Two assumptions I made explicit in a comment at the top of the file, since the spec didn't cover them:
- `days_late` is never negative; `0` is the earliest value and means "on time."
- The 2000-cent cap is a flat ceiling applied after the per-day rate — the children's half-rate discount doesn't also halve the cap.