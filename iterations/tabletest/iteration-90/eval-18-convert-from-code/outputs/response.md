All 11 rows across the 4 tables pass. Summary:

Wrote `src/test/java/com/example/InsuranceEvaluatorTest.java` with 4 `@TableTest` methods covering `evaluateApplication`, decomposed by rule rather than by branch:

1. **`autoApprovesARenewalApplicantWithNoClaims`** — proves the AUTO_APPROVED short-circuit needs *both* `RENEWAL` and zero claims (each condition alone falls through to a normal APPROVED).
2. **`prioritizesAutoApprovalOverRiskBasedRejection`** — a combining table showing the AUTO_APPROVED rule overrides what would otherwise be a REJECTED (high-risk) outcome; same age, only claim count differs.
3. **`rejectsAnApplicationWhoseRiskScoreExceedsTheThreshold`** — boundary pair straddling the risk-score-75 cutoff.
4. **`selectsThePremiumFormulaByTheApplicantsAge`** — age-65 boundary for standard vs. senior premium, plus two rows showing the premium scales correctly with risk score in each band.

Key assumptions (documented in each `@Description` since the internal risk score is private and never exposed):
- Applicant type is held at a non-renewal value (`NEW`) in tables 3–4 so the auto-approval rule doesn't interfere — that interaction is proven separately.
- Age/claim-count values in tables 3–4 were chosen using the known internal formula to land exactly on the boundaries being tested, since there's no public seam to assert the intermediate directly.

Added `org.tabletest:tabletest-junit:1.2.2` to `build.gradle` (was missing). Ran the suite with the system `gradle` (no wrapper present) — **all 11 generated cases pass**.