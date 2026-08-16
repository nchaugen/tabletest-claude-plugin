# Eval Review — table-driven-testing, Iteration 11

**Model:** sonnet (claude-sonnet-5) · **Grading:** claude-sonnet-5 · **Date:** 2026-08-16 · **Evals:** 5

## Summary

42/44 (95.5%) · 2725487 tokens · 554.0s · $2.7873

_Cost figures are Claude Code list-price estimates; actual billing may differ (e.g. promotional pricing). Timed-out evals score 0 with unrecorded token usage._

> ⚠️ **1 assertion verdict moved** vs iteration 10 (over 4 of 5 evals — the rest were not comparable). These are deltas, not attributions: read each eval's `outputs/` and `narration.md` before explaining any of them, and do not start the next iteration until every entry in `analysis-todo.md` has a cause.

## Delta vs Iteration 10

**Regressions (1):**
- ❌ eval-32-baggage-fees-py: `scenario-names-describe-conditions`

**Not comparable (1) — excluded from the deltas above:**
- ⚠️ eval-31-travel-insurance-py: iteration 10 did not run this eval; nothing to compare against

## Resource Comparison vs Iteration 10

| Eval | Pass Rate | Prev | Tokens | Prev | Time(s) | Prev |
|------|-----------|------|--------|------|---------|------|
| eval-31-travel-insurance-py | 12/13 | — | 521762 | — | 89.1 | — |
| eval-32-baggage-fees-py | 7/8 | 8/8 | 553864 | 493915 | 74.1 | 79.8 |
| eval-33-library-fees-py | 9/9 | 9/9 | 640155 | 660708 | 218.3 | 156.1 |
| eval-34-hotel-cancellation-swift | 8/8 | 8/8 | 405487 | 413144 | 90.8 | 158.5 |
| eval-35-cinema-tickets-routing | 6/6 | 6/6 | 604219 | 509661 | 81.7 | 73.5 |

## Per-Eval Results

### ⚠️ Eval eval-31-travel-insurance-py

**12/13** · 521762 tokens · 89117ms

- ✅ **uses-parametrize**: Tests use @pytest.mark.parametrize (or pytest.param rows) — not one test function per case, and not a loop over cases inside a single test body.
- ✅ **parametrize-has-ids**: Parametrized cases carry scenario identifiers — an ids= argument or pytest.param(..., id=...) — so each case has a readable name in test output.
- ✅ **no-if-in-python-test**: Test function bodies contain no if/elif statements — expected outcomes come from the parametrized data, not branching logic.
- ❌ **threshold-as-column**: Policy thresholds (age 70, 90-day trip limit) appear as explicit values in the test data alongside the applicant's actual values, making the comparison visible to the reader — not only baked invisibly into which cases were chosen.
  > No explicit threshold column (e.g. age_limit=70) exists; only boundary values 69/70 and 90/91 are chosen as actual ages/trip_days, baking the threshold into case selection rather than showing it as an explicit value.
- ✅ **scenario-names-describe-conditions**: Scenario ids name the variation the case exercises, not the result it produces. The decidable test: FAILS when a scenario id states or paraphrases a value that appears in an expectation column of that same case — 'User cannot delete' beside an Allowed? cell of false, 'No discount applies' beside a Discount? cell of 0.00, 'Contractor gets no bonus' beside a Bonus? cell of 0. Also FAILS on generic labels ('Test 1', 'Test case 2'), which name no variation at all. PASSES otherwise. Naming the rule or the situation is correct even when it makes the outcome inferable — 'Delivery and pickup items always split', 'At the standard threshold, not above it', 'EU express, light package', 'Renewal with no claims', 'Missing name' all PASS. The failure this catches is an id echoing its own expectation cell, not an id that describes what the case is about, and not an id from which a reader who knows the rule could predict the outcome. A single offending id fails the assertion. Judge every parametrized test in the file.
- ✅ **domain-parameter-names**: Parametrize argument names use domain language (age, trip_days, decision) — not generic names like a, b, val1, expected1.
- ✅ **concrete-domain-values**: Case values use concrete domain values (73, 91, Decision.APPROVED) — not abstract placeholders like 'CATEGORY_A' or magic sentinel numbers with no domain meaning.
- ✅ **concerns-decomposed**: Distinct concerns are addressed by distinct parametrized tests, rather than crammed into one monolithic parametrized test mixing unrelated rules. **What counts as a distinct concern is the Expected Output Description's own list wherever it gives one** — take its dimensions as written and do not regroup them into something you find more cohesive; where it gives no list, derive the concerns from the requirement. FAILS when one parametrized test carries concerns that answer different rules — whether it is the only parametrized test delivered or one of several, since a class can decompose most of its concerns correctly and still cram three unrelated ones into a fourth parametrized test. PASSES when every parametrized test delivered is focused on one concern, or on concerns related as the exemptions below describe. Three things are NOT failures here, and each is commonly mistaken for one. **Closely-related concerns may share one parametrized test** where they answer the same rule — a fulfillment type and a delivery address both deciding "same shipment iff same type and address" are two facets of one question, so four parametrized tests covering five named concerns is correct, not under-decomposed. **A single parametrized test is right** where the concerns share the same input columns and each is discharged by one case or two: the compact one-parametrized test form and a multi-parametrized test split are then equally correct, and a domain with genuinely one concern needs only one. **Repeated or redundant cases are not this assertion's subject at all** — whether one case re-covers ground an earlier one already discharged, and whether a whole parametrized test re-proves another, are judged elsewhere; do not fail decomposition for them. Judge the delivered parametrized tests and nothing else. Any claim the response's prose makes about how many concerns or parametrized tests it produced is irrelevant — an inaccurate summary standing beside correctly decomposed parametrized tests PASSES, and an accurate summary standing beside one monolithic parametrized test FAILS.
- ✅ **no-duplicate-rows-within-a-table**: Judge each parametrized test in isolation. The assertion FAILS if a case re-covers an obligation an earlier case in the SAME table already discharged (e.g. a full region x speed x weight cross-product where one case per rate band would state the same rule). Judge each table against its concern's coverage obligations — the distinct behaviours the rule must demonstrate. Where the expected output states those obligations for a concern, judge against that list and do not invent a stricter one; where it does not, derive them from the concern itself. A case that discharges an obligation no other case reaches earns its place however simple it looks, and a value set that covers several values in one case is the preferred discharge, never a failure. Cases that walk one input up while everything else is held are excess when the third and later ones only re-show a direction the first two already established — 'standard applicant, no claims' at ages 30, 40 and 50, or a premium sampled at 0, 2, 4 and 6 claims once the charge is already known to be per claim: two samples state a difference, a third states its shape, and the rest exercise the same arithmetic. They are NOT excess when each value is a boundary, a band, or a distinct branch of the rule — 40 against 41 hours, below/at/above a threshold, one case per rate band — because each then discharges an obligation of its own. Two further cases are NOT excess, and both are commonly mistaken for it. A third consecutive value of a count, where two would leave a per-unit rate and a one-off charge indistinguishable: 0, 1 and 2 prior claims, because 0 and 1 alone are equally consistent with a flat penalty for having any claim history, and the third case is what decides between them. And a case whose point is that an input does NOT move the outcome — two ages priced the same, a value set over every carrier against one cost — because an invariance is a rule of its own and the only way to state it is to vary the input and hold the expectation. Neither is a repeat of a direction: the first establishes a shape, the second establishes that there is no direction at all. CASES YOU THINK ARE MISSING ARE NEVER A FAILURE HERE — this assertion judges excess cases only, and coverage is judged by this eval's own coverage assertions. Judge every parametrized test in the file; your evidence must name each parametrized test with PASS or FAIL.
- ✅ **separates-age-duration-destination**: Age/clearance policy, trip-duration policy, and destination restriction are separated into distinct parametrized tests, rather than every rule exercised through one combined table.
- ✅ **precedence-visible**: Rule precedence is visible from concrete cases: a restricted destination with an otherwise approvable profile is DECLINED, and a >90-day trip is MANUAL_REVIEW even when the age/clearance rule alone would decline or approve.
- ✅ **clearance-effect-visible**: The effect of medical clearance for applicants aged 70+ is visible from paired cases: the same senior profile appears with clearance (APPROVED) and without (DECLINED). The clearance parameter's effect must be readable from the rows, not just implied.
- ✅ **compiles**: pytest can import and collect the test modules against the project scaffolding

### ⚠️ Eval eval-32-baggage-fees-py

**7/8** · 553864 tokens · 74067ms

- ✅ **uses-parametrize**: Tests use @pytest.mark.parametrize (or pytest.param rows) — not one test function per case, and not a loop over cases inside a single test body.
- ✅ **parametrize-has-ids**: Parametrized cases carry scenario identifiers — an ids= argument or pytest.param(..., id=...) — so each case has a readable name in test output.
- ✅ **no-if-in-python-test**: Test function bodies contain no if/elif statements — expected outcomes come from the parametrized data, not branching logic.
- ✅ **full-tier-enumeration**: Every fee tier is represented (included, 75-euro heavy, 150-euro oversize) and every boundary is tested from both sides: 23 kg (included) vs 24 kg (heavy), 32 kg (heavy) vs 33 kg (oversize), 45 kg (oversize) vs 46 kg (not accepted). Middle-tier boundaries (23/24 and 32/33) must not be skipped in favour of only the outer edges.
- ❌ **scenario-names-describe-conditions**: Scenario ids name the variation the case exercises, not the result it produces. The decidable test: FAILS when a scenario id states or paraphrases a value that appears in an expectation column of that same case — 'User cannot delete' beside an Allowed? cell of false, 'No discount applies' beside a Discount? cell of 0.00, 'Contractor gets no bonus' beside a Bonus? cell of 0. Also FAILS on generic labels ('Test 1', 'Test case 2'), which name no variation at all. PASSES otherwise. Naming the rule or the situation is correct even when it makes the outcome inferable — 'Delivery and pickup items always split', 'At the standard threshold, not above it', 'EU express, light package', 'Renewal with no claims', 'Missing name' all PASS. The failure this catches is an id echoing its own expectation cell, not an id that describes what the case is about, and not an id from which a reader who knows the rule could predict the outcome. A single offending id fails the assertion. Judge every parametrized test in the file.
  > pytest.param(23, 0, id="at the no-fee limit") paraphrases the fee_eur=0 expectation as 'no-fee'
- ✅ **exception-case-separated**: The over-45-kg not-accepted case is expressed as its own test (or clearly separated parametrized cases) using pytest.raises(BagNotAccepted) — not mixed into the fee table via sentinel values, None expectations, or branching inside the test body.
- ✅ **concrete-domain-values**: Case values are concrete domain values (weights like 24, fees like 75) — not abstract placeholders or symbolic constants that hide the actual numbers from the reader.
- ✅ **compiles**: pytest can import and collect the test modules against the project scaffolding

### ✅ Eval eval-33-library-fees-py

**9/9** · 640155 tokens · 218324ms

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

**8/8** · 405487 tokens · 90832ms

- ✅ **uses-test-arguments**: Tests use @Test(arguments:) parameterised cases — not one test function per case, and not a loop over cases inside a single test body.
- ✅ **no-loop-in-swift-test**: Test function bodies contain no for/while loops or forEach — cases are supplied via @Test(arguments:), not iterated manually.
- ✅ **no-if-in-swift-test**: Test function bodies contain no if/switch/guard branching — expected outcomes come from the argument rows, not branching logic.
- ✅ **cases-are-paired-rows**: Each case reads as a coherent row pairing input with expected outcome — labelled tuple elements, a row struct, or zip of collections — not multiple independent collections passed to arguments: (which Swift Testing combines as a cartesian product) and not parallel arrays indexed manually.
- ✅ **full-tier-enumeration**: Every fee tier is represented (free, 50%, 80%, full value) and every boundary is tested from both sides: 30 days (free) vs 29 days (50%), 7 days (50%) vs 6 days (80%), 1 day (80%) vs 0 days (full value). Middle-tier boundaries (7/6 and 1/0) must not be skipped in favour of only the outer edges.
- ✅ **exception-case-separated**: The started-stay case is expressed as its own test using #expect(throws:) — not mixed into the fee table via sentinel values, optional expectations, or branching inside the test body.
- ✅ **concrete-domain-values**: Case values are concrete domain values (days like 29, fees like 100 for a 200-euro booking) — not abstract placeholders, computed expressions like bookingValue / 2, or symbolic constants that hide the actual numbers from the reader.
- ✅ **compiles**: swift build --build-tests succeeds against the package scaffolding

### ✅ Eval eval-35-cinema-tickets-routing

**6/6** · 604219 tokens · 81677ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation — on a Java project the tabletest skill handles the table-driven request, not a generic parameterised-test approach.
- ✅ **no-parameterized-test**: Test source does not use JUnit @ParameterizedTest — the request must route to TableTest, not generic JUnit parameterisation.
- ✅ **has-tabletest-dependency**: The build file (build.gradle) includes org.tabletest:tabletest-junit:1.2.1 as a test dependency
- ✅ **scenario-column-present**: Table has a scenario/description column as the leftmost column
- ✅ **has-question-mark-column**: At least one output column name ends with '?' (e.g. 'Price?')
- ✅ **compiles**: The generated test code compiles successfully against the project scaffolding

