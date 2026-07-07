All 12 generated test cases pass (row 1's age value set `{25, 70}` expands to 2 cases, giving 7+5=12). Both tables run clean against the actual `InsuranceEvaluator` logic.

Summary of what I did:
- Verified `org.tabletest:tabletest-junit:1.2.1` wasn't yet a dependency and added it to `build.gradle`.
- Created `src/test/java/com/example/InsuranceEvaluatorTest.java` with two `@TableTest` methods, decomposed by concern:
  - `resolvesApplicationOutcome` — the precedence/decision logic: renewal-with-no-claims auto-approval overriding everything (age doesn't matter, tested via a value set), fall-through for renewals-with-claims and new applicants, the rejection threshold boundary (risk score 75 vs 76), and rejection taking precedence even for senior applicants.
  - `calculatesPremiumForApprovedApplications` — isolates the premium arithmetic for approved cases, with applicant type fixed to `"NEW"` (irrelevant to this concern) and a clean boundary pair (age 64 vs 65, same risk score) isolating the senior/standard tier switch.
- Ran `gradle test` — build succeeded, 12/12 generated cases passed.