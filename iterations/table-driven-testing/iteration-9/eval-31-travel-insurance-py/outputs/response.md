All 10 tests collect and fail as expected (raising `NotImplementedError` from the stub, not an import/collection error). The stub in `src/travel_policy.py` was left untouched.

Wrote `tests/test_travel_policy.py` with three table-driven tests, one per rule:

1. **`test_declines_applications_for_restricted_destinations`** — restricted destination forces `DECLINED`, proven against a case that would otherwise be `APPROVED` and one that would otherwise be `MANUAL_REVIEW`.
2. **`test_sends_overlong_trips_to_manual_review_regardless_of_age`** — trip-length boundary at 90/91 days; covers the boundary itself plus "regardless of age" by showing a >90-day trip goes to `MANUAL_REVIEW` even for an otherwise-approvable (older, cleared) applicant and an otherwise-declinable (older, uncleared) applicant.
3. **`test_approves_applications_by_age_and_medical_clearance`** — age boundary at 69/70; under 70 approves regardless of clearance (value-set pair), at 70 clearance decides the outcome.

**Assumption documented in the file**: the spec doesn't state what happens to an applicant aged 70+ without clearance (destination fine, trip within limits) — I took this as `DECLINED` rather than `MANUAL_REVIEW`, noted in a comment at the top of the test file.