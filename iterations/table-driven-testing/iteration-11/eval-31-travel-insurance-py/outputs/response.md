All 9 collect cleanly and fail as expected (stub raises `NotImplementedError`), confirming the tests are wired correctly without implementing any logic.

I wrote `tests/test_travel_policy.py` with three parametrized tests, one per independent rule (the "One Rule, One Axis" design):

1. **`test_declines_restricted_destinations_regardless_of_age_trip_length_and_clearance`** — destination restriction overrides everything; 2 cases vary age/trip length/clearance together to prove the override holds regardless.
2. **`test_sends_trips_over_90_days_to_manual_review_regardless_of_age`** — the 90-day boundary (90 → approved, 91 → manual review), plus a case at 91 days with a senior/cleared applicant to prove age truly doesn't matter once over the limit.
3. **`test_approves_applications_based_on_age_and_medical_clearance`** — the 69/70 age boundary: under 70, clearance is irrelevant (both cases approve); at 70, clearance decides approve vs. decline.

One assumption was necessary and is documented in a comment at the top of the file: the spec doesn't say what happens to a senior applicant without clearance — I inferred **DECLINED**, since `MANUAL_REVIEW` is explicitly reserved for the trip-length rule and "approved only if cleared" implies non-approval otherwise.