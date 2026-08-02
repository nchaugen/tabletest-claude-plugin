All 12 tests collect cleanly and fail on the expected `NotImplementedError` from the stub — exactly the intended state.

**Design summary** (3 tables, one per rule/axis):

1. `test_approves_by_age_and_medical_clearance` — age/clearance decision, trip and destination held at safe values. Covers under-70 regardless of clearance, the 69/70 age boundary, and clearance splitting the outcome at 70+.
2. `test_sends_trips_over_90_days_to_manual_review_regardless_of_age` — the 90/91-day boundary, plus a case showing an older applicant without clearance (who'd otherwise be `DECLINED`) gets `MANUAL_REVIEW` instead once the trip exceeds 90 days — proving trip-length precedence over the age rule.
3. `test_declines_restricted_destinations_regardless_of_other_factors` — the destination override, with cases that would otherwise be `APPROVED` or `MANUAL_REVIEW`, all forced to `DECLINED`.

**Assumption made explicit**: the spec doesn't say what happens for age ≥ 70, no medical clearance, trip ≤ 90 days, unrestricted destination. Since trips ≤ 90 days aren't manual review and the destination isn't restricted, `DECLINED` is the only remaining outcome — this is encoded in the "at the age threshold without medical clearance" case and stated in the test name/docstring.