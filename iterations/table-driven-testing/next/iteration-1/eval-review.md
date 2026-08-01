# Eval Review — table-driven-testing variant=next, Iteration 1

**Model:** sonnet (claude-sonnet-5) · **Grading:** claude-sonnet-5 · **Date:** 2026-08-01 · **Evals:** 5

## Summary

41/44 (93.2%) · 2731207 tokens · 585.7s · $2.7297

_Cost figures are Claude Code list-price estimates; actual billing may differ (e.g. promotional pricing). Timed-out evals score 0 with unrecorded token usage._

> ⚠️ **3 assertion verdicts moved** vs official (iterations 4, 3 merged). These are deltas, not attributions: read each eval's `outputs/` and `narration.md` before explaining any of them, and do not start the next iteration until every entry in `analysis-todo.md` has a cause.

## Per-Eval Results

### ⚠️ Eval eval-31-travel-insurance-py

**12/13** · 502485 tokens · 144854ms

- ✅ **uses-parametrize**: Tests use @pytest.mark.parametrize (or pytest.param rows) — not one test function per case, and not a loop over cases inside a single test body.
- ✅ **parametrize-has-ids**: Parametrized cases carry scenario identifiers — an ids= argument or pytest.param(..., id=...) — so each case has a readable name in test output.
- ✅ **no-if-in-python-test**: Test function bodies contain no if/elif statements — expected outcomes come from the parametrized data, not branching logic.
- ✅ **threshold-as-column**: Policy thresholds (age 70, 90-day trip limit) appear as explicit values in the test data alongside the applicant's actual values, making the comparison visible to the reader — not only baked invisibly into which cases were chosen.
- ✅ **scenario-names-describe-conditions**: Scenario ids name the variation the case exercises, not the result it produces. The decidable test: FAILS when a scenario id states or paraphrases a value that appears in an expectation column of that same case — 'User cannot delete' beside an Allowed? cell of false, 'No discount applies' beside a Discount? cell of 0.00, 'Contractor gets no bonus' beside a Bonus? cell of 0. Also FAILS on generic labels ('Test 1', 'Test case 2'), which name no variation at all. PASSES otherwise. Naming the rule or the situation is correct even when it makes the outcome inferable — 'Delivery and pickup items always split', 'At the standard threshold, not above it', 'EU express, light package', 'Renewal with no claims', 'Missing name' all PASS. The failure this catches is an id echoing its own expectation cell, not an id that describes what the case is about, and not an id from which a reader who knows the rule could predict the outcome. A single offending id fails the assertion. Judge every parametrized test in the file.
- ✅ **domain-parameter-names**: Parametrize argument names use domain language (age, trip_days, decision) — not generic names like a, b, val1, expected1.
- ✅ **concrete-domain-values**: Case values use concrete domain values (73, 91, Decision.APPROVED) — not abstract placeholders like 'CATEGORY_A' or magic sentinel numbers with no domain meaning.
- ✅ **concerns-decomposed**: Multiple parametrized tests are used, each addressing a distinct concern — not one monolithic table mixing unrelated rules. Acceptable to use a single table only when the domain genuinely has a single concern.
- ❌ **no-duplicate-rows-within-a-table**: Judge each parametrized test in isolation. The assertion FAILS if a case re-covers an obligation an earlier case in the SAME table already discharged (e.g. a full region x speed x weight cross-product where one case per rate band would state the same rule). Judge each table against its concern's coverage obligations — the distinct behaviours the rule must demonstrate. Where the expected output states those obligations for a concern, judge against that list and do not invent a stricter one; where it does not, derive them from the concern itself. A case that discharges an obligation no other case reaches earns its place however simple it looks, and a value set that covers several values in one case is the preferred discharge, never a failure. Cases that walk one input up while everything else is held are excess when the third and later ones only re-show a direction the first two already established — 'standard applicant, no claims' at ages 30, 40 and 50, or a linear premium sampled at 0, 2 and 4 claims: two samples state the effect and the rest exercise the same arithmetic. They are NOT excess when each value is a boundary, a band, or a distinct branch of the rule — 40 against 41 hours, below/at/above a threshold, one case per rate band — because each then discharges an obligation of its own. CASES YOU THINK ARE MISSING ARE NEVER A FAILURE HERE — this assertion judges excess cases only, and coverage is judged by this eval's own coverage assertions. Judge every parametrized test in the file; your evidence must name each parametrized test with PASS or FAIL.
  > test_declines_restricted_destination_regardless_of_other_factors: FAIL (8-case full cross product of age x trip_days x has_medical_clearance mirrors the explicit 'full region x speed x weight cross-product' excess example). test_routes_trips_over_90_days_to_manual_review_regardless_of_age: PASS (boundary 90/91 plus elderly branch). test_requires_medical_clearance_for_applicants_aged_70_or_over: PASS (boundary 69/70 with clearance branches).
- ✅ **separates-age-duration-destination**: Age/clearance policy, trip-duration policy, and destination restriction are separated into distinct parametrized tests, rather than every rule exercised through one combined table.
- ✅ **precedence-visible**: Rule precedence is visible from concrete cases: a restricted destination with an otherwise approvable profile is DECLINED, and a >90-day trip is MANUAL_REVIEW even when the age/clearance rule alone would decline or approve.
- ✅ **clearance-effect-visible**: The effect of medical clearance for applicants aged 70+ is visible from paired cases: the same senior profile appears with clearance (APPROVED) and without (DECLINED). The clearance parameter's effect must be readable from the rows, not just implied.
- ✅ **compiles**: pytest can import and collect the test modules against the project scaffolding

### ⚠️ Eval eval-32-baggage-fees-py

**7/8** · 522480 tokens · 83851ms

- ✅ **uses-parametrize**: Tests use @pytest.mark.parametrize (or pytest.param rows) — not one test function per case, and not a loop over cases inside a single test body.
- ✅ **parametrize-has-ids**: Parametrized cases carry scenario identifiers — an ids= argument or pytest.param(..., id=...) — so each case has a readable name in test output.
- ✅ **no-if-in-python-test**: Test function bodies contain no if/elif statements — expected outcomes come from the parametrized data, not branching logic.
- ✅ **full-tier-enumeration**: Every fee tier is represented (included, 75-euro heavy, 150-euro oversize) and every boundary is tested from both sides: 23 kg (included) vs 24 kg (heavy), 32 kg (heavy) vs 33 kg (oversize), 45 kg (oversize) vs 46 kg (not accepted). Middle-tier boundaries (23/24 and 32/33) must not be skipped in favour of only the outer edges.
- ✅ **scenario-names-describe-conditions**: Scenario ids name the variation the case exercises, not the result it produces. The decidable test: FAILS when a scenario id states or paraphrases a value that appears in an expectation column of that same case — 'User cannot delete' beside an Allowed? cell of false, 'No discount applies' beside a Discount? cell of 0.00, 'Contractor gets no bonus' beside a Bonus? cell of 0. Also FAILS on generic labels ('Test 1', 'Test case 2'), which name no variation at all. PASSES otherwise. Naming the rule or the situation is correct even when it makes the outcome inferable — 'Delivery and pickup items always split', 'At the standard threshold, not above it', 'EU express, light package', 'Renewal with no claims', 'Missing name' all PASS. The failure this catches is an id echoing its own expectation cell, not an id that describes what the case is about, and not an id from which a reader who knows the rule could predict the outcome. A single offending id fails the assertion. Judge every parametrized test in the file.
- ❌ **exception-case-separated**: The over-45-kg not-accepted case is expressed as its own test (or clearly separated parametrized cases) using pytest.raises(BagNotAccepted) — not mixed into the fee table via sentinel values, None expectations, or branching inside the test body.
  > pytest.param(46, None, BagNotAccepted, id=...) mixed into same table; def _fee_or_error(weight_kg): try: ... except Exception as error: return None, type(error) — branching, not a separate pytest.raises test
- ✅ **concrete-domain-values**: Case values are concrete domain values (weights like 24, fees like 75) — not abstract placeholders or symbolic constants that hide the actual numbers from the reader.
- ✅ **compiles**: pytest can import and collect the test modules against the project scaffolding

### ✅ Eval eval-33-library-fees-py

**9/9** · 460707 tokens · 77091ms

- ✅ **delivers-without-asking**: The response delivers a complete test file. Open questions may be noted as assumptions alongside the delivered tests, but the response must not stop at clarifying questions instead of delivering.
- ✅ **assumptions-stated**: The ambiguity in the rules (how the 20-euro cap interacts with the children's half rate) is resolved to an explicit, stated interpretation — in the response text, a comment, or scenario ids — not silently embedded in expected values.
- ✅ **cap-interaction-covered**: Cases cover the interaction between the cap and the children's half rate: a children's-section book late enough to reach the cap appears with a concrete expected fee — not just each rule tested in isolation.
- ✅ **boundary-cases-covered**: Cases include zero days late (no fee), an ordinary per-day fee, and a return late enough to hit the cap for a regular book — the rule set's boundaries, not just one happy-path row.
- ✅ **uses-parametrize**: Tests use @pytest.mark.parametrize (or pytest.param rows) — not one test function per case, and not a loop over cases inside a single test body.
- ✅ **parametrize-has-ids**: Parametrized cases carry scenario identifiers — an ids= argument or pytest.param(..., id=...) — so each case has a readable name in test output.
- ✅ **no-if-in-python-test**: Test function bodies contain no if/elif statements — expected outcomes come from the parametrized data, not branching logic.
- ✅ **concrete-domain-values**: Expected fees are concrete cent values (e.g. 150, 2000) readable directly from the cases — not computed inside the test body from the rate and days.
- ✅ **compiles**: pytest can import and collect the test modules against the project scaffolding

### ⚠️ Eval eval-34-hotel-cancellation-swift

**7/8** · 570170 tokens · 164828ms

- ✅ **uses-test-arguments**: Tests use @Test(arguments:) parameterised cases — not one test function per case, and not a loop over cases inside a single test body.
- ✅ **no-loop-in-swift-test**: Test function bodies contain no for/while loops or forEach — cases are supplied via @Test(arguments:), not iterated manually.
- ✅ **no-if-in-swift-test**: Test function bodies contain no if/switch/guard branching — expected outcomes come from the argument rows, not branching logic.
- ✅ **cases-are-paired-rows**: Each case reads as a coherent row pairing input with expected outcome — labelled tuple elements, a row struct, or zip of collections — not multiple independent collections passed to arguments: (which Swift Testing combines as a cartesian product) and not parallel arrays indexed manually.
- ✅ **full-tier-enumeration**: Every fee tier is represented (free, 50%, 80%, full value) and every boundary is tested from both sides: 30 days (free) vs 29 days (50%), 7 days (50%) vs 6 days (80%), 1 day (80%) vs 0 days (full value). Middle-tier boundaries (7/6 and 1/0) must not be skipped in favour of only the outer edges.
- ❌ **exception-case-separated**: The started-stay case is expressed as its own test using #expect(throws:) — not mixed into the fee table via sentinel values, optional expectations, or branching inside the test body.
  > plus the accept→reject boundary (0 vs -1 days) folded into the same table ... using a fee/error column pair rather than a separate throwing test.
- ✅ **concrete-domain-values**: Case values are concrete domain values (days like 29, fees like 100 for a 200-euro booking) — not abstract placeholders, computed expressions like bookingValue / 2, or symbolic constants that hide the actual numbers from the reader.
- ✅ **compiles**: swift build --build-tests succeeds against the package scaffolding

### ✅ Eval eval-35-cinema-tickets-routing

**6/6** · 675365 tokens · 115063ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation — on a Java project the tabletest skill handles the table-driven request, not a generic parameterised-test approach.
- ✅ **no-parameterized-test**: Test source does not use JUnit @ParameterizedTest — the request must route to TableTest, not generic JUnit parameterisation.
- ✅ **has-tabletest-dependency**: The build file (build.gradle) includes org.tabletest:tabletest-junit:1.2.1 as a test dependency
- ✅ **scenario-column-present**: Table has a scenario/description column as the leftmost column
- ✅ **has-question-mark-column**: At least one output column name ends with '?' (e.g. 'Price?')
- ✅ **compiles**: The generated test code compiles successfully against the project scaffolding

## Variant vs Official (iterations 4, 3 merged)

### Per-Eval Resource Comparison

| Eval | Pass (off) | Pass (var) | Tokens (off) | Tokens (var) | Tok Δ | Cost (off) | Cost (var) | Cost Δ | Time (off) | Time (var) | Time Δ |
|------|------------|------------|--------------|--------------|-------|------------|------------|--------|------------|------------|--------|
| eval-31-travel-insurance-py | 11/13 | 12/13 | 346051 | 502485 | +45% | $0.3881 | $0.5841 | +51% | 101.4s | 144.9s | +43% |
| eval-32-baggage-fees-py | 8/8 | 7/8 | 348804 | 522480 | +50% | $0.2792 | $0.4714 | +69% | 44.2s | 83.9s | +90% |
| eval-33-library-fees-py | 9/9 | 9/9 | 361100 | 460707 | +28% | $0.3499 | $0.4369 | +25% | 73.2s | 77.1s | +5% |
| eval-34-hotel-cancellation-swift | 8/8 | 7/8 | 356855 | 570170 | +60% | $0.4206 | $0.6399 | +52% | 108.7s | 164.8s | +52% |
| eval-35-cinema-tickets-routing | 6/6 | 6/6 | 659458 | 675365 | +2% | $0.6998 | $0.5974 | -15% | 175.7s | 115.1s | -35% |
| **Totals (5 comparable)** | **42/44** | **41/44** | **2072268** | **2731207** | **+32%** | **$2.1376** | **$2.7297** | **+28%** | **503.2s** | **585.7s** | **+16%** |

**Comparable summary (5 evals in both):**

| Source | Pass Rate | Tokens | Cost | Time |
|--------|-----------|--------|------|------|
| next (iter 1) | 41/44 (93.2%) | 2731207 | $2.7297 | 585.7s |
| official | 42/44 (95.5%) | 2072268 | $2.1376 | 503.2s |
| **Δ** | | **+32%** | **+28%** | **+16%** |

### Per-Assertion Comparison

| Eval | Assertion | official | next |
|------|-----------|----------|------|
| eval-31-travel-insurance-py | threshold-as-column | ❌ | ✅ |
| eval-32-baggage-fees-py | exception-case-separated | ✅ | ❌ |
| eval-34-hotel-cancellation-swift | exception-case-separated | ✅ | ❌ |

### Load-Bearing Assertions

Assertions that pass with official skill but fail with next variant:

- eval-32-baggage-fees-py: `exception-case-separated`
- eval-34-hotel-cancellation-swift: `exception-case-separated`

### Attention Needed

Evals with failed assertions or disproportionate resource usage (>2x official):

| Eval | Issue | Details |
|------|-------|---------|
| eval-32-baggage-fees-py | Failed assertions | `exception-case-separated` |
| eval-34-hotel-cancellation-swift | Failed assertions | `exception-case-separated` |

