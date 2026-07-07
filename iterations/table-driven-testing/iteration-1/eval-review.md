# Eval Review — table-driven-testing, Iteration 1

**Model:** sonnet (claude-sonnet-5) · **Grading:** claude-haiku-4-5 · **Date:** 2026-07-07 · **Evals:** 5

## Summary

42/44 (95.5%) · 2076001 tokens · 355.9s · $1.5184

_Cost figures are Claude Code list-price estimates; actual billing may differ (e.g. promotional pricing). Timed-out evals score 0 with unrecorded token usage._

## Per-Eval Results

### ✅ Eval eval-31-travel-insurance-py

**13/13** · 384818 tokens · 68523ms

- ✅ **uses-parametrize**: Tests use @pytest.mark.parametrize (or pytest.param rows) — not one test function per case, and not a loop over cases inside a single test body.
- ✅ **parametrize-has-ids**: Parametrized cases carry scenario identifiers — an ids= argument or pytest.param(..., id=...) — so each case has a readable name in test output.
- ✅ **no-if-in-python-test**: Test function bodies contain no if/elif statements — expected outcomes come from the parametrized data, not branching logic.
- ✅ **threshold-as-column**: Policy thresholds (age 70, 90-day trip limit) appear as explicit values in the test data alongside the applicant's actual values, making the comparison visible to the reader — not only baked invisibly into which cases were chosen.
- ✅ **scenario-names-describe-conditions**: Scenario ids describe conditions ('senior without clearance', 'trip just over limit') — not outcomes ('approved', 'declined').
- ✅ **domain-parameter-names**: Parametrize argument names use domain language (age, trip_days, decision) — not generic names like a, b, val1, expected1.
- ✅ **concrete-domain-values**: Case values use concrete domain values (73, 91, Decision.APPROVED) — not abstract placeholders like 'CATEGORY_A' or magic sentinel numbers with no domain meaning.
- ✅ **concerns-decomposed**: Multiple parametrized tests are used, each addressing a distinct concern — not one monolithic table mixing unrelated rules. Acceptable to use a single table only when the domain genuinely has a single concern.
- ✅ **minimal-rows-per-concern**: Each parametrized test has only the cases needed to express its concern's rules — no unnecessary permutations from combining concerns. Fewer cases per test is expected when concerns are properly separated.
- ✅ **separates-age-duration-destination**: Age/clearance policy, trip-duration policy, and destination restriction are separated into distinct parametrized tests, rather than every rule exercised through one combined table.
- ✅ **precedence-visible**: Rule precedence is visible from concrete cases: a restricted destination with an otherwise approvable profile is DECLINED, and a >90-day trip is MANUAL_REVIEW even when the age/clearance rule alone would decline or approve.
- ✅ **clearance-effect-visible**: The effect of medical clearance for applicants aged 70+ is visible from paired cases: the same senior profile appears with clearance (APPROVED) and without (DECLINED). The clearance parameter's effect must be readable from the rows, not just implied.
- ✅ **compiles**: pytest can import and collect the test modules against the project scaffolding

### ⚠️ Eval eval-32-baggage-fees-py

**6/8** · 324013 tokens · 32875ms

- ✅ **uses-parametrize**: Tests use @pytest.mark.parametrize (or pytest.param rows) — not one test function per case, and not a loop over cases inside a single test body.
- ✅ **parametrize-has-ids**: Parametrized cases carry scenario identifiers — an ids= argument or pytest.param(..., id=...) — so each case has a readable name in test output.
- ✅ **no-if-in-python-test**: Test function bodies contain no if/elif statements — expected outcomes come from the parametrized data, not branching logic.
- ✅ **full-tier-enumeration**: Every fee tier is represented (included, 75-euro heavy, 150-euro oversize) and every boundary is tested from both sides: 23 kg (included) vs 24 kg (heavy), 32 kg (heavy) vs 33 kg (oversize), 45 kg (oversize) vs 46 kg (not accepted). Middle-tier boundaries (23/24 and 32/33) must not be skipped in favour of only the outer edges.
- ❌ **scenario-names-describe-conditions**: Scenario ids describe conditions ('at included limit', 'just over heavy limit') — not outcomes ('no fee', 'seventy-five euros').
  > IDs include condition+outcome: 'at_included_limit-no_fee', 'just_over_included_limit-heavy_bag_fee', 'at_heavy_bag_limit-heavy_bag_fee' (outcome suffixes violate spec)
- ✅ **exception-case-separated**: The over-45-kg not-accepted case is expressed as its own test (or clearly separated parametrized cases) using pytest.raises(BagNotAccepted) — not mixed into the fee table via sentinel values, None expectations, or branching inside the test body.
- ❌ **concrete-domain-values**: Case values are concrete domain values (weights like 24, fees like 75) — not abstract placeholders or symbolic constants that hide the actual numbers from the reader.
  > Uses symbolic constants: NO_FEE = 0, HEAVY_BAG_FEE = 75, OVERSIZE_FEE = 150 in test parameters instead of literal values
- ✅ **compiles**: pytest can import and collect the test modules against the project scaffolding

### ✅ Eval eval-33-library-fees-py

**9/9** · 490869 tokens · 94799ms

- ✅ **delivers-without-asking**: The response delivers a complete test file. Open questions may be noted as assumptions alongside the delivered tests, but the response must not stop at clarifying questions instead of delivering.
- ✅ **assumptions-stated**: The ambiguity in the rules (how the 20-euro cap interacts with the children's half rate) is resolved to an explicit, stated interpretation — in the response text, a comment, or scenario ids — not silently embedded in expected values.
- ✅ **cap-interaction-covered**: Cases cover the interaction between the cap and the children's half rate: a children's-section book late enough to reach the cap appears with a concrete expected fee — not just each rule tested in isolation.
- ✅ **boundary-cases-covered**: Cases include zero days late (no fee), an ordinary per-day fee, and a return late enough to hit the cap for a regular book — the rule set's boundaries, not just one happy-path row.
- ✅ **uses-parametrize**: Tests use @pytest.mark.parametrize (or pytest.param rows) — not one test function per case, and not a loop over cases inside a single test body.
- ✅ **parametrize-has-ids**: Parametrized cases carry scenario identifiers — an ids= argument or pytest.param(..., id=...) — so each case has a readable name in test output.
- ✅ **no-if-in-python-test**: Test function bodies contain no if/elif statements — expected outcomes come from the parametrized data, not branching logic.
- ✅ **concrete-domain-values**: Expected fees are concrete cent values (e.g. 150, 2000) readable directly from the cases — not computed inside the test body from the rate and days.
- ✅ **compiles**: pytest can import and collect the test modules against the project scaffolding

### ✅ Eval eval-34-hotel-cancellation-swift

**8/8** · 337579 tokens · 70370ms

- ✅ **uses-test-arguments**: Tests use @Test(arguments:) parameterised cases — not one test function per case, and not a loop over cases inside a single test body.
- ✅ **no-loop-in-swift-test**: Test function bodies contain no for/while loops or forEach — cases are supplied via @Test(arguments:), not iterated manually.
- ✅ **no-if-in-swift-test**: Test function bodies contain no if/switch/guard branching — expected outcomes come from the argument rows, not branching logic.
- ✅ **cases-are-paired-rows**: Each case reads as a coherent row pairing input with expected outcome — labelled tuple elements, a row struct, or zip of collections — not multiple independent collections passed to arguments
- ✅ **full-tier-enumeration**: Every fee tier is represented and every boundary tested from both sides: 30 days (free) vs 29 days (50%), 7 days (50%) vs 6 days (80%), 1 day (80%) vs 0 days (full value)
- ✅ **exception-case-separated**: The started-stay case is expressed as its own test using #expect(throws:) — not mixed into the fee table
- ✅ **concrete-domain-values**: Case values are concrete domain values (days like 29, fees like 100) — not abstract placeholders or computed expressions
- ✅ **compiles**: swift build --build-tests succeeds against the package scaffolding

### ✅ Eval eval-35-cinema-tickets-routing

**6/6** · 538722 tokens · 89327ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation — on a Java project the tabletest skill handles the table-driven request, not a generic parameterised-test approach.
- ✅ **no-parameterized-test**: Test source does not use JUnit @ParameterizedTest — the request must route to TableTest, not generic JUnit parameterisation.
- ✅ **has-tabletest-dependency**: The build file (build.gradle) includes org.tabletest:tabletest-junit:1.2.1 as a test dependency
- ✅ **scenario-column-present**: Table has a scenario/description column as the leftmost column
- ✅ **has-question-mark-column**: At least one output column name ends with '?' (e.g. 'Price?')
- ✅ **compiles**: The generated test code compiles successfully against the project scaffolding

