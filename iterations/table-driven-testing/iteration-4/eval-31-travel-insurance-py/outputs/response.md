All 22 tests fail as expected (raising `NotImplementedError` from the stub), and collection is clean.

I wrote `tests/test_travel_policy.py` with four parametrized tests, one per concern:

1. **`test_eligibility_by_age_and_medical_clearance`** — age/clearance rule, boundary at 69/70/71, trip length and destination held at valid fixed values.
2. **`test_trip_length_threshold`** — 90 vs 91 day boundary, age/clearance held fixed.
3. **`test_long_trip_goes_to_manual_review_regardless_of_age_and_clearance`** — stacked `parametrize` over age × clearance, trip fixed at 120, proving the manual-review trigger overrides the age/clearance decline case.
4. **`test_restricted_destination_declines_regardless_of_age_trip_length_and_clearance`** — stacked `parametrize` over age × trip_days × clearance, destination fixed restricted, proving the decline overrides both approval and manual-review outcomes.

**Assumption documented in the file docstring**: the spec never states the outcome for a 70+ applicant without medical clearance on a trip ≤90 days — I assumed **DECLINED**, since none of the stated approval or manual-review conditions apply. The stub remains unimplemented; all 22 cases currently fail on `NotImplementedError` as intended.