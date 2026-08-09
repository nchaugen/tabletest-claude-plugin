# Eval Review — table-driven-testing, Iteration 11

**Model:** sonnet (claude-sonnet-5) · **Grading:** claude-sonnet-5 · **Date:** 2026-08-09 · **Evals:** 1

## Summary

12/13 (92.3%) · 521762 tokens · 89.1s · $0.5136

_Cost figures are Claude Code list-price estimates; actual billing may differ (e.g. promotional pricing). Timed-out evals score 0 with unrecorded token usage._

> ⛔ **Void comparison — none of the 1 evals could be compared** vs iteration 10. Every delta below is computed over nothing; an absence of movement here is not evidence that nothing moved. See `analysis-todo.md`.

## Delta vs Iteration 10

**Not comparable (1) — excluded from the deltas above:**
- ⚠️ eval-31-travel-insurance-py: iteration 10 did not run this eval; nothing to compare against

## Resource Comparison vs Iteration 10

| Eval | Pass Rate | Prev | Tokens | Prev | Time(s) | Prev |
|------|-----------|------|--------|------|---------|------|
| eval-31-travel-insurance-py | 12/13 | — | 521762 | — | 89.1 | — |

## Per-Eval Results

### ⚠️ Eval eval-31-travel-insurance-py

**12/13** · 521762 tokens · 89117ms

- ✅ **uses-parametrize**: Tests use @pytest.mark.parametrize (or pytest.param rows) — not one test function per case, and not a loop over cases inside a single test body.
- ✅ **parametrize-has-ids**: Parametrized cases carry scenario identifiers — an ids= argument or pytest.param(..., id=...) — so each case has a readable name in test output.
- ✅ **no-if-in-python-test**: Test function bodies contain no if/elif statements — expected outcomes come from the parametrized data, not branching logic.
- ❌ **threshold-as-column**: Policy thresholds (age 70, 90-day trip limit) appear as explicit values in the test data alongside the applicant's actual values, making the comparison visible to the reader — not only baked invisibly into which cases were chosen.
  > Parametrize columns are only ('trip_days','age','has_medical_clearance','decision') etc; no explicit threshold value column, thresholds only implied via chosen boundary values like 90/91 or 69/70.
- ✅ **scenario-names-describe-conditions**: Scenario ids name the variation the case exercises, not the result it produces. The decidable test: FAILS when a scenario id states or paraphrases a value that appears in an expectation column of that same case — 'User cannot delete' beside an Allowed? cell of false, 'No discount applies' beside a Discount? cell of 0.00, 'Contractor gets no bonus' beside a Bonus? cell of 0. Also FAILS on generic labels ('Test 1', 'Test case 2'), which name no variation at all. PASSES otherwise. Naming the rule or the situation is correct even when it makes the outcome inferable — 'Delivery and pickup items always split', 'At the standard threshold, not above it', 'EU express, light package', 'Renewal with no claims', 'Missing name' all PASS. The failure this catches is an id echoing its own expectation cell, not an id that describes what the case is about, and not an id from which a reader who knows the rule could predict the outcome. A single offending id fails the assertion. Judge every parametrized test in the file.
- ✅ **domain-parameter-names**: Parametrize argument names use domain language (age, trip_days, decision) — not generic names like a, b, val1, expected1.
- ✅ **concrete-domain-values**: Case values use concrete domain values (73, 91, Decision.APPROVED) — not abstract placeholders like 'CATEGORY_A' or magic sentinel numbers with no domain meaning.
- ✅ **concerns-decomposed**: Multiple parametrized tests are used, each addressing a distinct concern — not one monolithic table mixing unrelated rules. Acceptable to use a single table only when the domain genuinely has a single concern.
- ✅ **no-duplicate-rows-within-a-table**: Judge each parametrized test in isolation. The assertion FAILS if a case re-covers an obligation an earlier case in the SAME table already discharged (e.g. a full region x speed x weight cross-product where one case per rate band would state the same rule). Judge each table against its concern's coverage obligations — the distinct behaviours the rule must demonstrate. Where the expected output states those obligations for a concern, judge against that list and do not invent a stricter one; where it does not, derive them from the concern itself. A case that discharges an obligation no other case reaches earns its place however simple it looks, and a value set that covers several values in one case is the preferred discharge, never a failure. Cases that walk one input up while everything else is held are excess when the third and later ones only re-show a direction the first two already established — 'standard applicant, no claims' at ages 30, 40 and 50, or a linear premium sampled at 0, 2 and 4 claims: two samples state the effect and the rest exercise the same arithmetic. They are NOT excess when each value is a boundary, a band, or a distinct branch of the rule — 40 against 41 hours, below/at/above a threshold, one case per rate band — because each then discharges an obligation of its own. CASES YOU THINK ARE MISSING ARE NEVER A FAILURE HERE — this assertion judges excess cases only, and coverage is judged by this eval's own coverage assertions. Judge every parametrized test in the file; your evidence must name each parametrized test with PASS or FAIL.
- ✅ **separates-age-duration-destination**: Age/clearance policy, trip-duration policy, and destination restriction are separated into distinct parametrized tests, rather than every rule exercised through one combined table.
- ❌ **precedence-visible**: Rule precedence is visible from concrete cases: a restricted destination with an otherwise approvable profile is DECLINED, and a >90-day trip is MANUAL_REVIEW even when the age/clearance rule alone would decline or approve.
  > Only shows >90-day override when age/clearance would approve (id='just over the limit, senior applicant with clearance'); no case shows override when age/clearance would decline (senior without clearance, trip>90).
- ✅ **clearance-effect-visible**: The effect of medical clearance for applicants aged 70+ is visible from paired cases: the same senior profile appears with clearance (APPROVED) and without (DECLINED). The clearance parameter's effect must be readable from the rows, not just implied.
- ✅ **compiles**: pytest can import and collect the test modules against the project scaffolding

