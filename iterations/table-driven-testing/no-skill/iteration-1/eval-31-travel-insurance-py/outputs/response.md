All 16 tests fail cleanly with `NotImplementedError` (no collection/import errors), as expected for tests written ahead of the implementation.

I wrote `tests/test_travel_policy.py` with a `pytest.mark.parametrize` table of 16 cases covering:

- **Base rule**: under 70 + trip ≤90 days → `APPROVED` (clearance irrelevant)
- **Senior rule**: age ≥70 → `APPROVED` only with clearance, else `DECLINED` (including exact boundary at age 70)
- **Trip-length rule**: trip >90 days → `MANUAL_REVIEW` regardless of age/clearance, including boundary at 91 days and cases where age+clearance would otherwise approve
- **Restricted destination**: always `DECLINED`, overriding otherwise-approved, otherwise-manual-review, and otherwise-approved-senior-with-clearance cases

One assumption I made explicit via test cases: the spec doesn't state the outcome for age ≥70 without clearance (only that clearance is required for approval) — I assumed that resolves to `DECLINED` rather than `MANUAL_REVIEW`, since manual review is only triggered by trip length per the stated rules.