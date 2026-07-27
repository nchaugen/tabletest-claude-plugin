# Narration — 18

The agent's visible narration and file writes, in order, distilled from
`conversation.jsonl`. Thinking text is absent unless the model returns it in the
clear (Claude 5-family models encrypt it).

I'll invoke the tabletest skill for this Java/Kotlin TableTest request, but first I need to locate the `InsuranceEvaluator` class.

Now let me invoke the tabletest skill to write these tests properly.

JUnit Jupiter is present but the `tabletest-junit` dependency is missing. I'll add it before writing the test.

**Edit ./build.gradle (edit)**

Now I'll write the test class with two `@TableTest` methods: one for the decision/routing logic, one for premium calculation by age tier.

**Write ./src/test/java/com/example/InsuranceEvaluatorTest.java (53 lines)**

Let me verify the tests compile and pass.

No gradle wrapper available. I'll check if gradle is installed globally to run the tests.

Build succeeded. Let me confirm the actual test count and results via the HTML/XML report.

All 11 generated test cases pass (7 from the decision table's 5 rows, one expanding to 3 via a value set; 4 from the premium table).

## Summary

Added `org.tabletest:tabletest-junit:1.2.2` to `build.gradle` and created `src/test/java/com/example/InsuranceEvaluatorTest.java` with two `@TableTest` methods, splitting the decision-routing rule from the premium-arithmetic rule (per "Separate Rules from Arithmetic"):

1. **`routesApplicationToDecision`** — covers auto-approval precedence (a `{25, 70, 900}` value set proves it wins regardless of age/risk), both halves of the `RENEWAL AND claimCount==0` condition failing independently, and the exact risk-score boundary (75 approved / 76 rejected). A `Risk Score?` traceability column exposes the internal `age/10 + claimCount*15` score since it isn't otherwise visible to the reader.
2. **`pricesApprovedApplication`** — covers the senior/standard premium formulas, including the age 64→65 boundary where risk score is held identical (6) to isolate the tier switch from the arithmetic.

Assumptions documented in `@Description`:
- Ages 9/10 in the risk-limit rows are synthetic, chosen only to land exactly on the 75/76 boundary — not realistic applicant ages.
- `Applicant Type` is fixed to `"NEW"` in the premium table to bypass auto-approval, since it doesn't affect the premium formula.
- `checkPolicyDatabase`/`hasActivePolicy` is dead state — set but never read in the return value — so it isn't covered; worth flagging to you as a possible bug in the source.

