# Eval Review — tabletest-swift variant=next, Iteration 7

**Model:** sonnet (claude-sonnet-5-5) · **Grading:** claude-sonnet-5-5 · **Date:** 2026-11-26 · **Evals:** 2

## Summary

24/24 (100.0%) · 613567 tokens · 129.9s · $0.6472

_Cost figures are Claude Code list-price estimates; actual billing may differ (e.g. promotional pricing). Timed-out evals score 0 with unrecorded token usage._

> ⚠️ **2 assertion verdicts moved** vs official (iterations 9, 8, 7, 6, 5, 4, 3, 2, 1 merged). These are deltas, not attributions: read each eval's `outputs/` and `narration.md` before explaining any of them, and do not start the next iteration until every entry in `analysis-todo.md` has a cause.

## Delta vs Iteration 6

**Not comparable (1) — excluded from the deltas above:**
- ⚠️ eval-39-playlist-screen-columns: iteration 6 did not run this eval; nothing to compare against

## Resource Comparison vs Iteration 6

| Eval | Pass Rate | Prev | Tokens | Prev | Time(s) | Prev |
|------|-----------|------|--------|------|---------|------|
| eval-39-playlist-screen-columns | 12/12 | — | 313368 | — | 70.0 | — |
| eval-44-ballot-box-replacement | 12/12 | 12/12 | 300199 | 441147 | 59.9 | 91.8 |

## Per-Eval Results

### ✅ Eval eval-39-playlist-screen-columns

**12/12** · 313368 tokens · 70011ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest attribute
- ✅ **tabletest-function-in-a-type**: Every @TableTest function is declared inside a type (a struct, class, enum or an extension of one), not at file scope.
- ✅ **no-if-switch-in-method**: Test function body contains no if, guard or switch statement and no ternary
- ✅ **has-question-mark-column**: Table has at least one column name ending with '?' (e.g. 'Actions?')
- ✅ **edit-columns-are-the-deciding-inputs**: For a saved playlist, the input columns are the inputs the screen reads to decide the actions: the unread rules and the change the user made to the rules (such as 'adds a rule' or 'removes every rule'), with test code that builds the query on screen from the change. FAILS when the columns hold full rule sets or queries, saved and on screen, that the reader must compare to find the change, or when a scenario name states a deciding input that no column holds.
- ✅ **irrelevant-input-is-a-value-set**: An input that does not change the actions is shown once, as a value set ({...}) or left out of the table with the fixture stating it: the order on the library, and the count of unread rules beyond zero. FAILS when two rows differ only in such an input and have the same expectations.
- ✅ **crossing-the-inputs-finds-missing-rows**: The playlist table has a row for a playlist with an unread rule whose every rule is removed (actions: revert), which the original cases lacked.
- ✅ **rule-cell-names-only-what-it-sets**: Where a cell holds a rule set, it names only the rules it sets (such as [:] or [genres: {rock}]) and a TableValueDecodable conformance on SmartRules in the test target reads it, for example with readingFields. PASSES when no cell holds a rule set (the rules are columns and the body builds SmartRules). FAILS when every rule cell must name all three fields, or when the app was changed so a synthesised decoder accepts the cell.
- ✅ **action-list-converts-on-the-app-type**: The actions list cell converts because PlaylistScreen.Action gained a one-word conformance in Sources/ (CaseIterable, Codable, Decodable, or String raw values). FAILS when the test target turns words into actions itself (a switch, a dictionary, a test-local copy of the enum), or when the expectation is not a list of actions.
- ✅ **no-decoder-in-app-code**: No init(from:) is written into the app code under Sources/.
- ✅ **compiles**: swift build --build-tests succeeds against the package scaffolding
- ✅ **tests-pass**: The tests pass when run against PlaylistScreen

### ✅ Eval eval-44-ballot-box-replacement

**12/12** · 300199 tokens · 59875ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest attribute
- ✅ **tabletest-function-in-a-type**: Every @TableTest function is declared inside a type (a struct, class, enum or an extension of one), not at file scope.
- ✅ **no-if-switch-in-method**: Test function body contains no if, guard or switch statement and no ternary
- ✅ **no-default-in-test-function**: No @TableTest function body defaults a value with ??, or calls .map, .flatMap or .compactMap on one of its parameters.
- ✅ **has-question-mark-column**: Table has at least one column name ending with '?' (e.g. 'Result?')
- ✅ **no-branch-on-table-enum-outside-it**: A switch, an if or guard that compares with a case, or a ternary on a case, of a test-local enum with String raw values is in a member of that enum (the enum itself or an extension of it), never in another function of the test type that takes the enum or reads a property of its type.
- ✅ **each-refusal-has-a-row**: Each of the three refusals has its own row, or its own value-set member, with its reason in an expectation cell: the poll is closed (pollClosed), the voter has no vote in the box (noVoteToReplace), the voter's ballot is sealed (ballotSealed). FAILS when a reason has no case.
- ✅ **unchanged-state-asserted**: A refused case asserts the state of the box after the call — the voter's vote (vote(of:)) and the tally — read from the box, in addition to the returned refusal. FAILS when a refused case asserts only the returned value.
- ✅ **unchanged-state-has-a-contrast**: The state after a refusal is asserted in one of three forms: a table that also has a row where the state changes (the accepted replacement); one row with a value set of the refusal reasons; or a before-state column whose cells differ between the refused rows, so that the after-state column differs too. FAILS when an after-state column (the voter's vote or the tally after the call) has the same cell in every row of a table with two or more rows, and when the unchanged state is asserted with a constant in the test body.
- ✅ **refusal-as-data**: The refusal reason is an expectation cell (such as refused(pollClosed), or a Refusal column), and the replaced and refused rows share one assertion. FAILS when the body chooses what to assert by a branch, or when the refusal is a constant in the body rather than a cell.
- ✅ **no-decoder-in-app-code**: No init(from:) is written into the app code under Sources/.
- ✅ **compiles**: swift build --build-tests succeeds against the package scaffolding

## Variant vs Official (iterations 9, 8, 7, 6, 5, 4, 3, 2, 1 merged)

### Per-Eval Resource Comparison

| Eval | Pass (off) | Pass (var) | Tokens (off) | Tokens (var) | Tok Δ | Cost (off) | Cost (var) | Cost Δ | Time (off) | Time (var) | Time Δ |
|------|------------|------------|--------------|--------------|-------|------------|------------|--------|------------|------------|--------|
| eval-39-playlist-screen-columns | 11/12 | 12/12 | 356426 | 313368 | -12% | $0.3062 | $0.3355 | +10% | 61.4s | 70.0s | +14% |
| eval-44-ballot-box-replacement | 11/12 | 12/12 | 279005 | 300199 | +8% | $0.2693 | $0.3118 | +16% | 46.5s | 59.9s | +29% |
| **Totals (2 comparable)** | **22/24** | **24/24** | **635431** | **613567** | **-3%** | **$0.5755** | **$0.6472** | **+12%** | **107.9s** | **129.9s** | **+20%** |

**Comparable summary (2 evals in both):**

| Source | Pass Rate | Tokens | Cost | Time |
|--------|-----------|--------|------|------|
| next (iter 7) | 24/24 (100.0%) | 613567 | $0.6472 | 129.9s |
| official | 22/24 (91.7%) | 635431 | $0.5755 | 107.9s |
| **Δ** | | **-3%** | **+12%** | **+20%** |

### Per-Assertion Comparison

| Eval | Assertion | official | next |
|------|-----------|----------|------|
| eval-39-playlist-screen-columns | irrelevant-input-is-a-value-set | ❌ | ✅ |
| eval-44-ballot-box-replacement | no-branch-on-table-enum-outside-it | ❌ | ✅ |

No load-bearing assertions found — variant matches official on all comparable assertions.

