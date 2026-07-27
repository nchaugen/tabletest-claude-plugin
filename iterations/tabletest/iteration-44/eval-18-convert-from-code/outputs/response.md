No failures. Here's a summary of what I did.

**Read `InsuranceEvaluator.evaluateApplication`** and split its logic into two concerns, per the tabletest skill's "separate rules from arithmetic" guidance:

1. **`decidesApplicationOutcome`** (6 rows) — which decision (`AUTO_APPROVED`/`REJECTED`/`APPROVED`) an application gets. Covers: the renewal+zero-claims auto-approval, its precedence over the senior-age branch, that renewal alone or zero-claims alone isn't sufficient, and the risk-score rejection threshold straddled on both sides (using value sets to show applicant type doesn't matter there).
2. **`calculatesPremiumByAgeTier`** (5 rows) — the premium formula for approved applications, isolating the senior-vs-standard rate switch at ages 64/65 (which produce the *same* internal risk score, isolating age as the deciding variable) plus claim-history variation for both tiers.

Two assumptions, documented in `@Description`:
- No minimum applicant age is enforced by the code, so ages 9/10 are used to land exactly on the internal risk-score rejection threshold (75/76) — the only realistic pairing this coarse formula allows.
- Claim-count-0 rows in the premium table fix applicant type to `NEW` to avoid triggering the auto-approval override (already covered in the other table).

I added the `tabletest-junit:1.2.2` dependency to `build.gradle` and created `src/test/java/com/example/InsuranceEvaluatorTest.java`. Ran `gradle test` — build succeeded, all 15 generated cases (rows × value-set expansion) passed.