Written `tests/test_library_fees.py` with one parametrized test (10 cases) driving `late_fee_cents(days_late, childrens_section)`:

- On-time boundary (day 0 → 0 fee) for both sections
- One-day-late rate check (50¢ general, 25¢ children's — confirms the half rate)
- Cap boundary straddled from both sides for each rate (general: 39/40/41 days around the 2000¢ cap; children's: 79/80/81 days, since the halved rate reaches the same cap twice as slowly)

Stated assumptions in the file's header comment: `days_late <= 0` counts as on-time, and the 20-euro cap is a flat ceiling applied after the children's discount (not itself halved). Verified in a throwaway venv (pytest wasn't installed) that all 10 cases collect and fail with `NotImplementedError` against the stub — no import/collection errors — then removed that venv.