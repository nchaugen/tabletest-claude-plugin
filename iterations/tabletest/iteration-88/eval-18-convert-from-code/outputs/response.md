All 14 generated test cases pass (4 + 4 + 4 + 2 rows across the four tables), confirming the table data matches the implementation.

## Summary

Added `tabletest-junit:1.2.2` to `build.gradle` and wrote `src/test/java/com/example/InsuranceEvaluatorTest.java` with four `@TableTest` methods, one per concern in `evaluateApplication`:

1. **`autoApprovesRenewalApplicantsWithNoClaims`** — the renewal + zero-claims short-circuit, including a row proving it overrides the rejection check even at a risk score above threshold (uses an intentionally unrealistic age to expose that ordering).
2. **`rejectsApplicantsWhoseRiskScoreExceedsTheThreshold`** — straddles the internal `>75` cutoff at ages 9/10 (claim count fixed at 5), with applicant type varied as a value set to show it's irrelevant here.
3. **`computesPremiumForApprovedApplicantsByAgeBand`** — straddles the senior-rate cutoff at ages 64/65 (chosen so the age-derived risk contribution is identical on both sides, isolating just the formula switch), plus two rows showing claims scaling the premium in each band.
4. **`throwsForANullApplicantType`** — the unguarded `NullPointerException` when `applicantType` is null (`checkPolicyDatabase` calls `.equals` on it unconditionally).

Assumption worth flagging: `hasActivePolicy`/`internalRiskScore` are private fields with no accessor, so they're not testable black-box and aren't referenced in the tables — this is noted implicitly by using age/claim-count combinations rather than a risk-score column, with the reasoning documented in each table's `@Description`.