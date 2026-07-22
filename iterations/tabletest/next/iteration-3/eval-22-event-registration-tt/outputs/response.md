I added two `@TableTest` methods in `src/test/java/com/example/EventRegistrationServiceTest.java`, plus the `tabletest-junit` dependency in `build.gradle`:

1. **`validatesNameAndEmail`** — 9 rows covering name-required and email-format validation (missing/blank name, missing `@`, missing local part, missing domain, missing TLD), with dietary/accessibility needs left blank or filled to confirm they're optional.
2. **`calculatesPriceAndDiscount`** — 6 rows (7 executions, one uses a `{5, 10}` value set for the group-size tier) covering the early-bird cutoff boundary (before/on/after 2025-03-01), the group-size threshold boundary (4 vs 5+), and the no-stacking rule when both discounts would apply.

Key assumptions, documented in `@Description` blocks in the file:
- `price()` is the final charged amount after discount; `discount()` is the currency amount saved (not a percentage).
- Early-bird cutoff is exclusive (2025-03-01 itself does not qualify).
- Exact validation error message strings (`"Name is required"`, `"Invalid email format"`) are assumptions the implementation will need to match — the spec didn't specify wording.

Compiled and ran successfully via `gradle test` — all 16 generated cases fail only with the stub's `UnsupportedOperationException`, confirming table parsing/type conversion is correct and ready for the real implementation to make them pass. `EventRegistrationService` was left untouched (still throwing), per the instruction not to implement logic yet.