# Eval Review — table-driven-testing variant=next, Iteration 2

**Model:** sonnet (claude-sonnet-5) · **Grading:** claude-sonnet-5 · **Date:** 2026-08-01 · **Evals:** 2

## Summary

16/16 (100.0%) · 965223 tokens · 214.5s · $0.9915

_Cost figures are Claude Code list-price estimates; actual billing may differ (e.g. promotional pricing). Timed-out evals score 0 with unrecorded token usage._

## Delta vs Iteration 1

**Improvements (2):**
- ✅ eval-32-baggage-fees-py: `exception-case-separated`
- ✅ eval-34-hotel-cancellation-swift: `exception-case-separated`

## Resource Comparison vs Iteration 1

| Eval | Pass Rate | Prev | Tokens | Prev | Time(s) | Prev |
|------|-----------|------|--------|------|---------|------|
| eval-32-baggage-fees-py | 8/8 | 7/8 | 469742 | 522480 | 78.1 | 83.9 |
| eval-34-hotel-cancellation-swift | 8/8 | 7/8 | 495481 | 570170 | 136.3 | 164.8 |

## Per-Eval Results

### ✅ Eval eval-32-baggage-fees-py

**8/8** · 469742 tokens · 78133ms

- ✅ **uses-parametrize**: Tests use @pytest.mark.parametrize (or pytest.param rows) — not one test function per case, and not a loop over cases inside a single test body.
- ✅ **parametrize-has-ids**: Parametrized cases carry scenario identifiers — an ids= argument or pytest.param(..., id=...) — so each case has a readable name in test output.
- ✅ **no-if-in-python-test**: Test function bodies contain no if/elif statements — expected outcomes come from the parametrized data, not branching logic.
- ✅ **full-tier-enumeration**: Every fee tier is represented (included, 75-euro heavy, 150-euro oversize) and every boundary is tested from both sides: 23 kg (included) vs 24 kg (heavy), 32 kg (heavy) vs 33 kg (oversize), 45 kg (oversize) vs 46 kg (not accepted). Middle-tier boundaries (23/24 and 32/33) must not be skipped in favour of only the outer edges.
- ✅ **scenario-names-describe-conditions**: Scenario ids name the variation the case exercises, not the result it produces. The decidable test: FAILS when a scenario id states or paraphrases a value that appears in an expectation column of that same case — 'User cannot delete' beside an Allowed? cell of false, 'No discount applies' beside a Discount? cell of 0.00, 'Contractor gets no bonus' beside a Bonus? cell of 0. Also FAILS on generic labels ('Test 1', 'Test case 2'), which name no variation at all. PASSES otherwise. Naming the rule or the situation is correct even when it makes the outcome inferable — 'Delivery and pickup items always split', 'At the standard threshold, not above it', 'EU express, light package', 'Renewal with no claims', 'Missing name' all PASS. The failure this catches is an id echoing its own expectation cell, not an id that describes what the case is about, and not an id from which a reader who knows the rule could predict the outcome. A single offending id fails the assertion. Judge every parametrized test in the file.
- ✅ **exception-case-separated**: The over-45-kg not-accepted case is expressed as its own test (or clearly separated parametrized cases) using pytest.raises(BagNotAccepted) — not mixed into the fee table via sentinel values, None expectations, or branching inside the test body.
- ✅ **concrete-domain-values**: Case values are concrete domain values (weights like 24, fees like 75) — not abstract placeholders or symbolic constants that hide the actual numbers from the reader.
- ✅ **compiles**: pytest can import and collect the test modules against the project scaffolding

### ✅ Eval eval-34-hotel-cancellation-swift

**8/8** · 495481 tokens · 136332ms

- ✅ **uses-test-arguments**: Tests use @Test(arguments:) parameterised cases — not one test function per case, and not a loop over cases inside a single test body.
- ✅ **no-loop-in-swift-test**: Test function bodies contain no for/while loops or forEach — cases are supplied via @Test(arguments:), not iterated manually.
- ✅ **no-if-in-swift-test**: Test function bodies contain no if/switch/guard branching — expected outcomes come from the argument rows, not branching logic.
- ✅ **cases-are-paired-rows**: Each case reads as a coherent row pairing input with expected outcome — labelled tuple elements, a row struct, or zip of collections — not multiple independent collections passed to arguments: (which Swift Testing combines as a cartesian product) and not parallel arrays indexed manually.
- ✅ **full-tier-enumeration**: Every fee tier is represented (free, 50%, 80%, full value) and every boundary is tested from both sides: 30 days (free) vs 29 days (50%), 7 days (50%) vs 6 days (80%), 1 day (80%) vs 0 days (full value). Middle-tier boundaries (7/6 and 1/0) must not be skipped in favour of only the outer edges.
- ✅ **exception-case-separated**: The started-stay case is expressed as its own test using #expect(throws:) — not mixed into the fee table via sentinel values, optional expectations, or branching inside the test body.
- ✅ **concrete-domain-values**: Case values are concrete domain values (days like 29, fees like 100 for a 200-euro booking) — not abstract placeholders, computed expressions like bookingValue / 2, or symbolic constants that hide the actual numbers from the reader.
- ✅ **compiles**: swift build --build-tests succeeds against the package scaffolding

## Variant vs Official (iterations 4, 3 merged)

### Per-Eval Resource Comparison

| Eval | Pass (off) | Pass (var) | Tokens (off) | Tokens (var) | Tok Δ | Cost (off) | Cost (var) | Cost Δ | Time (off) | Time (var) | Time Δ |
|------|------------|------------|--------------|--------------|-------|------------|------------|--------|------------|------------|--------|
| eval-32-baggage-fees-py | 8/8 | 8/8 | 348804 | 469742 | +35% | $0.2792 | $0.4383 | +57% | 44.2s | 78.1s | +77% |
| eval-34-hotel-cancellation-swift | 8/8 | 8/8 | 356855 | 495481 | +39% | $0.4206 | $0.5532 | +32% | 108.7s | 136.3s | +25% |
| **Totals (2 comparable)** | **16/16** | **16/16** | **705659** | **965223** | **+37%** | **$0.6998** | **$0.9915** | **+42%** | **152.9s** | **214.5s** | **+40%** |

**Comparable summary (2 evals in both):**

| Source | Pass Rate | Tokens | Cost | Time |
|--------|-----------|--------|------|------|
| next (iter 2) | 16/16 (100.0%) | 965223 | $0.9915 | 214.5s |
| official | 16/16 (100.0%) | 705659 | $0.6998 | 152.9s |
| **Δ** | | **+37%** | **+42%** | **+40%** |

### Per-Assertion Comparison

| Eval | Assertion | official | next |
|------|-----------|----------|------|

No load-bearing assertions found — variant matches official on all comparable assertions.

