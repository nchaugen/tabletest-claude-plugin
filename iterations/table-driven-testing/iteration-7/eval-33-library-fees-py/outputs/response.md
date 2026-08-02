All 9 cases collect cleanly and fail (via `NotImplementedError`, not a collection/import error) as expected, and the stub was left untouched.

Wrote `tests/test_late_fee_cents.py` — one table-driven test, 9 cases covering: on-time boundary (both sections), one-day-late rate (50¢ regular / 25¢ children's), mid-range linear scaling, and the 2000¢ cap approached and exceeded from both sides for each section.

Assumptions (documented in the test docstring):
- `days_late` is never negative — a due-date-or-earlier return is represented as `0`.
- The 2000¢ (20€) cap is absolute and doesn't itself get halved for the children's section — only the per-day rate does.