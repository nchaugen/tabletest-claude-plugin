# Eval Review — table-driven-testing, Iteration 10

**Model:** sonnet (claude-sonnet-5) · **Grading:** claude-sonnet-5 · **Date:** 2026-08-04 · **Evals:** 4

## Summary

31/31 (100.0%) · 2077428 tokens · 467.9s · $2.2114

_Cost figures are Claude Code list-price estimates; actual billing may differ (e.g. promotional pricing). Timed-out evals score 0 with unrecorded token usage._

## Delta vs Iteration 8

No changes.

## Resource Comparison vs Iteration 8

| Eval | Pass Rate | Prev | Tokens | Prev | Time(s) | Prev |
|------|-----------|------|--------|------|---------|------|
| eval-32-baggage-fees-py | 8/8 | 8/8 | 493915 | 411871 | 79.8 | 93.9 |
| eval-33-library-fees-py | 9/9 | 9/9 | 660708 | 428489 | 156.1 | 105.2 |
| eval-34-hotel-cancellation-swift | 8/8 | 8/8 | 413144 | 366132 | 158.5 | 82.9 |
| eval-35-cinema-tickets-routing | 6/6 | 6/6 | 509661 | 619687 | 73.5 | 91.8 |

## Per-Eval Results

### ✅ Eval eval-32-baggage-fees-py

**8/8** · 493915 tokens · 79836ms

- ✅ **uses-parametrize**: Tests use @pytest.mark.parametrize (or pytest.param rows) — not one test function per case, and not a loop over cases inside a single test body.
- ✅ **parametrize-has-ids**: Parametrized cases carry scenario identifiers — an ids= argument or pytest.param(..., id=...) — so each case has a readable name in test output.
- ✅ **no-if-in-python-test**: Test function bodies contain no if/elif statements — expected outcomes come from the parametrized data, not branching logic.
- ✅ **full-tier-enumeration**: Every fee tier is represented (included, 75-euro heavy, 150-euro oversize) and every boundary is tested from both sides: 23 kg (included) vs 24 kg (heavy), 32 kg (heavy) vs 33 kg (oversize), 45 kg (oversize) vs 46 kg (not accepted). Middle-tier boundaries (23/24 and 32/33) must not be skipped in favour of only the outer edges.
- ✅ **scenario-names-describe-conditions**: Scenario ids name the variation the case exercises, not the result it produces. The decidable test: FAILS when a scenario id states or paraphrases a value that appears in an expectation column of that same case — 'User cannot delete' beside an Allowed? cell of false, 'No discount applies' beside a Discount? cell of 0.00, 'Contractor gets no bonus' beside a Bonus? cell of 0. Also FAILS on generic labels ('Test 1', 'Test case 2'), which name no variation at all. PASSES otherwise. Naming the rule or the situation is correct even when it makes the outcome inferable — 'Delivery and pickup items always split', 'At the standard threshold, not above it', 'EU express, light package', 'Renewal with no claims', 'Missing name' all PASS. The failure this catches is an id echoing its own expectation cell, not an id that describes what the case is about, and not an id from which a reader who knows the rule could predict the outcome. A single offending id fails the assertion. Judge every parametrized test in the file.
- ✅ **exception-case-separated**: The over-45-kg not-accepted case is expressed as its own test (or clearly separated parametrized cases) using pytest.raises(BagNotAccepted) — not mixed into the fee table via sentinel values, None expectations, or branching inside the test body.
- ✅ **concrete-domain-values**: Case values are concrete domain values (weights like 24, fees like 75) — not abstract placeholders or symbolic constants that hide the actual numbers from the reader.
- ✅ **compiles**: pytest can import and collect the test modules against the project scaffolding

### ✅ Eval eval-33-library-fees-py

**9/9** · 660708 tokens · 156120ms

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

**8/8** · 413144 tokens · 158455ms

- ✅ **uses-test-arguments**: Tests use @Test(arguments:) parameterised cases — not one test function per case, and not a loop over cases inside a single test body.
- ✅ **no-loop-in-swift-test**: Test function bodies contain no for/while loops or forEach — cases are supplied via @Test(arguments:), not iterated manually.
- ✅ **no-if-in-swift-test**: Test function bodies contain no if/switch/guard branching — expected outcomes come from the argument rows, not branching logic.
- ✅ **cases-are-paired-rows**: Each case reads as a coherent row pairing input with expected outcome — labelled tuple elements, a row struct, or zip of collections — not multiple independent collections passed to arguments: (which Swift Testing combines as a cartesian product) and not parallel arrays indexed manually.
- ✅ **full-tier-enumeration**: Every fee tier is represented (free, 50%, 80%, full value) and every boundary is tested from both sides: 30 days (free) vs 29 days (50%), 7 days (50%) vs 6 days (80%), 1 day (80%) vs 0 days (full value). Middle-tier boundaries (7/6 and 1/0) must not be skipped in favour of only the outer edges.
- ✅ **exception-case-separated**: The started-stay case is expressed as its own test using #expect(throws:) — not mixed into the fee table via sentinel values, optional expectations, or branching inside the test body.
- ✅ **concrete-domain-values**: Case values are concrete domain values (days like 29, fees like 100 for a 200-euro booking) — not abstract placeholders, computed expressions like bookingValue / 2, or symbolic constants that hide the actual numbers from the reader.
- ✅ **compiles**: swift build --build-tests succeeds against the package scaffolding

### ✅ Eval eval-35-cinema-tickets-routing

**6/6** · 509661 tokens · 73525ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation — on a Java project the tabletest skill handles the table-driven request, not a generic parameterised-test approach.
- ✅ **no-parameterized-test**: Test source does not use JUnit @ParameterizedTest — the request must route to TableTest, not generic JUnit parameterisation.
- ✅ **has-tabletest-dependency**: The build file (build.gradle) includes org.tabletest:tabletest-junit:1.2.1 as a test dependency
- ✅ **scenario-column-present**: Table has a scenario/description column as the leftmost column
- ✅ **has-question-mark-column**: At least one output column name ends with '?' (e.g. 'Price?')
- ✅ **compiles**: The generated test code compiles successfully against the project scaffolding

