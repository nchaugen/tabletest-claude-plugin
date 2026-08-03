All 15 tests collect cleanly and fail exactly on `NotImplementedError` from the stub, as expected. The stub was left untouched.

Wrote `tests/test_travel_policy.py` with three parametrized tests, one per rule:

- **`test_approves_based_on_age_and_medical_clearance`** — age boundary at 70, showing clearance only matters at/over the threshold (trip length and destination held at valid constants, since they're other rules' concerns).
- **`test_sends_long_trips_to_manual_review_regardless_of_age`** — trip-length boundary at 90 days, with an extra case at 91 days/age 80 showing age doesn't affect the outcome once the trip is too long.
- **`test_declines_restricted_destinations_regardless_of_age_trip_length_and_clearance`** — stacked `@pytest.mark.parametrize` producing the full 2×2×2 combination of age/trip length/clearance, all expected to decline, to demonstrate the destination rule overrides everything else.

Assumption stated in the design (not literally in code comments beyond docstrings): when an applicant is 70+ without medical clearance, the outcome is `DECLINED` (the rules only say approval requires clearance at that age; declining is the natural complement since no other outcome fits). Let me know if manual review was intended instead for that case.