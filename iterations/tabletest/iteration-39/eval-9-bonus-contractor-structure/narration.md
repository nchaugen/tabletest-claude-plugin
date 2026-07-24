# Narration — eval-9-bonus-contractor-structure

The agent's visible narration and file writes, in order, distilled from
`conversation.jsonl`. Thinking text is absent unless the model returns it in the
clear (Claude 5-family models encrypt it).

This is a TableTest-writing task in Java, so I'll invoke the tabletest skill to follow its workflow and conventions.

**Edit /private/var/folders/fz/zxx_qbvj49gbql83cc5l47pr0000gn/T/eval-skill-1783416908758/eval-9-work/build.gradle (edit)**

Now I'll write the TableTest class.

**Write /private/var/folders/fz/zxx_qbvj49gbql83cc5l47pr0000gn/T/eval-skill-1783416908758/eval-9-work/src/test/java/com/example/BonusCalculatorTest.java (34 lines)**

Compiles cleanly. Let's also run the test to confirm it fails only due to the stub throwing `UnsupportedOperationException` (expected at this stage), not due to any structural/table issue.

All 6 generated test cases (4 rules + 2 from the contractor value-set) compile and run correctly, failing only because the stub throws `UnsupportedOperationException` as intended — bonus logic is untouched.

Summary of changes:
- `build.gradle`: added `testImplementation 'org.tabletest:tabletest-junit:1.2.1'` (JUnit Jupiter was already 6.0.3, well above the 5.11 minimum).
- `src/test/java/com/example/BonusCalculatorTest.java` (new): one `@TableTest` covering all four level/department rules plus the contractor override, using a `{SALES, ENGINEERING}` value set to express "department doesn't matter for contractors."
- `BonusCalculator` left as the stub throwing `UnsupportedOperationException` — no bonus logic implemented, per instructions.

Assumption (documented in the test's `@Description`): `calculateBonusPercentage` returns the percentage as a plain number (e.g. `15.0` for 15%), not a fraction (`0.15`) — the spec didn't state which representation to use.

