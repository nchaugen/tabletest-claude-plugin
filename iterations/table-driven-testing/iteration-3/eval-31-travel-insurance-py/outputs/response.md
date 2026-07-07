All 21 tests collect cleanly and fail with `NotImplementedError`, exactly as expected before the logic is written. The stub is untouched.

Wrote `tests/test_travel_policy.py` with 4 parametrized tests decomposed by concern:
- **Restricted destination → DECLINED**, regardless of age/trip length/clearance (stacked parametrize, 8 combos)
- **Trip > 90 days → MANUAL_REVIEW**, regardless of age/clearance (stacked parametrize, 4 combos)
- **Trip-length boundary** (90 vs 91 days) for an under-70 applicant
- **Age/clearance decision table** for trips within the limit, covering under/at/over the age-70 boundary crossed with clearance

**Assumption stated in the file docstring:** the rules never specify an outcome for a 70+ applicant without medical clearance on a short, unrestricted trip. I treated `MANUAL_REVIEW` as the catch-all for cases not explicitly covered by an APPROVED or DECLINED rule, reserving `DECLINED` exclusively for restricted destinations (the only rule that explicitly says "declined").