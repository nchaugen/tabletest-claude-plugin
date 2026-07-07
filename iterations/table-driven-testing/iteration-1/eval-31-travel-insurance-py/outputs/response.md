All 17 tests collect cleanly and fail red against the unimplemented stub, as expected. Also confirmed the stub in `src/travel_policy.py` is untouched.

Wrote `tests/test_travel_policy.py` with three parametrized tests, one per rule:
- **Age/clearance within trip limit** — under-70 approved, 70+ approved only with medical clearance (boundary at age 70 tested both ways).
- **Long trip → manual review** — stacked parametrize over trip length × (age, clearance) combos, showing the outcome holds regardless of age/clearance.
- **Restricted destination → declined** — stacked over age/trip/clearance combos, including a restricted + long-trip case to pin down that it overrides the manual-review rule.

Documented assumption (in the module docstring): a 70+ traveler *without* medical clearance is treated as `DECLINED`, since the rules only carve out a `MANUAL_REVIEW` outcome for trip length, not for missing clearance.