All 15 tests collect cleanly and fail with `NotImplementedError` as expected, and the stub is untouched.

Wrote `tests/test_evaluate_application.py` with three tables, one per rule:

1. **`test_declines_restricted_destination_regardless_of_other_factors`** — restricted destination always declines; stacked `parametrize` proves age, trip length (short and long), and medical clearance are all irrelevant (8 cases).
2. **`test_routes_trips_over_90_days_to_manual_review_regardless_of_age`** — boundary at 90 vs. 91 days, plus a value-set case showing an elderly applicant hits the same manual-review outcome.
3. **`test_requires_medical_clearance_for_applicants_aged_70_or_over`** — boundary at age 69 vs. 70, with clearance shown to be irrelevant under 70 and decisive at/over 70.

One assumption, stated in the module docstring since the rules don't say: a 70+ applicant *without* clearance is **DECLINED** (not manual review), since manual review is explicitly reserved for the trip-length rule.