All 8 test cases pass. Summary:

Added `org.tabletest:tabletest-junit:1.2.2` to `build.gradle` and created `src/test/java/com/example/InsuranceEvaluatorTest.java` with one `@TableTest` covering `evaluateApplication`'s full decision logic:

- **Auto-approval precedence** — renewal + zero claims short-circuits, including an edge case (age 760) proving it overrides an otherwise-rejectable risk score
- **Auto-approval guards** — renewal with a prior claim, and a non-renewal type with zero claims, both fall through to normal evaluation
- **Rejection threshold** — boundary pair at risk score 75 (approved) vs. 76 (rejected)
- **Senior vs. standard pricing** — boundary pair at age 64 vs. 65, held at the same risk score (6) to isolate the age effect

One judgment call worth flagging: the internal risk-score formula (`age/10 + claimCount*15`) isn't exposed by the public API, so I documented it in `@Description` rather than as a column — needed so a reader can verify the premium values, even though it means the description states a formula. All 8 rows pass against the current implementation.