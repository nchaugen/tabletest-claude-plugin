This all looks correct — 6+2+3+2 rows expanding to 10+2+3+2=17 test invocations, matching the gradle run. Everything compiles and the stub correctly leaves logic unimplemented.

## Summary

Added `org.tabletest:tabletest-junit:1.2.2` to `build.gradle` and wrote `src/test/java/com/example/EventRegistrationServiceTest.java` with four `@TableTest` methods, each isolating one rule:

1. **`registerValidatesEmailFormatAndRequiresName`** — name required, email format required, dietary/accessibility optional (shown via both null and populated rows)
2. **`registerAppliesEarlyBirdDiscountBeforeCutoff`** — 20% off before 2025-03-01, straddling the cutoff boundary; group size held at 1 to isolate the rule
3. **`registerAppliesGroupDiscountAtThreshold`** — 15% off at group size ≥5, straddling the threshold; date held post-cutoff to isolate the rule
4. **`registerUsesHigherOfEarlyBirdOrGroupDiscountWithoutStacking`** — the one combining table, showing the higher discount wins (20%) rather than stacking to 35%, and that group discount applies alone once early-bird has passed

Documented assumptions in each `@Description`: error message wording, that discount is a currency amount (not a percentage), and that "required" rejects both null and blank/whitespace names.

Verified the tables parse (via the skill's format-table.sh, including a deliberate misalignment test to confirm it isn't a no-op), and ran `gradle test` — it compiles cleanly and all 17 expanded row-cases fail identically on the stub's `UnsupportedOperationException`, confirming the tests are correctly wired but the implementation is untouched as requested.