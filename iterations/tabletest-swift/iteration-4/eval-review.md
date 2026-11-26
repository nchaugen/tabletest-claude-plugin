# Eval Review — tabletest-swift, Iteration 4

**Model:** sonnet (claude-sonnet-5-5) · **Grading:** claude-sonnet-5-5 · **Date:** 2026-11-26 · **Evals:** 4

## Summary

43/47 (91.5%) · 759243 tokens · 155.0s · $0.7932

_Cost figures are Claude Code list-price estimates; actual billing may differ (e.g. promotional pricing). Timed-out evals score 0 with unrecorded token usage._

> ⚠️ **1 assertion verdict moved** vs iteration 3. These are deltas, not attributions: read each eval's `outputs/` and `narration.md` before explaining any of them, and do not start the next iteration until every entry in `analysis-todo.md` has a cause.

## Delta vs Iteration 3

**Regressions (1):**
- ❌ eval-44-ballot-box-replacement: `no-branch-on-table-enum-outside-it`

## Resource Comparison vs Iteration 3

| Eval | Pass Rate | Prev | Tokens | Prev | Time(s) | Prev |
|------|-----------|------|--------|------|---------|------|
| eval-42-dog-daycare-admission | 11/12 | 11/12 | 190282 | 190472 | 32.8 | 33.6 |
| eval-43-ski-rental-bindings | 13/13 | 13/13 | 121262 | 77670 | 22.6 | 17.6 |
| eval-44-ballot-box-replacement | 11/12 | 12/12 | 286015 | 329747 | 58.7 | 56.8 |
| eval-45-train-delay-notice | 8/10 | 8/10 | 161684 | 165358 | 40.9 | 51.5 |

## Per-Eval Results

### ⚠️ Eval eval-42-dog-daycare-admission

**11/12** · 190282 tokens · 32836ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest attribute
- ✅ **tabletest-function-in-a-type**: Every @TableTest function is declared inside a type (a struct, class, enum or an extension of one), not at file scope.
- ✅ **no-if-switch-in-method**: Test function body contains no if, guard or switch statement and no ternary
- ✅ **no-default-in-test-function**: No @TableTest function body defaults a value with ??, or calls .map, .flatMap or .compactMap on one of its parameters.
- ✅ **has-question-mark-column**: Table has at least one column name ending with '?' (e.g. 'Decision?')
- ✅ **new-cases-are-rows-of-the-existing-table**: The cases of the new rule (a puppy under 16 weeks needs a second vaccination) are rows of the existing admission table, the one that already has the age and vaccination columns. FAILS when a new @TableTest or @Test function holds any of them, or when the admission table is split in two.
- ✅ **untouched-tables-unchanged**: The play-group table and the daily-fee table are byte-equal to the scaffold's.
- ✅ **sixteen-weeks-both-sides**: With one vaccination, the admission table shows both sides of the 16-week line: a dog under 16 weeks gives needsVaccination and a dog of 16 weeks gives admitted. Rows at 15 and 16 weeks pass, and so does a value set whose largest member is 15 beside a row at 16. FAILS when no row is at 16 weeks, or when no row or value-set member is at 15 weeks.
- ❌ **scenario-names-add-to-the-row**: Read each scenario name the answer added or changed in the admission table beside the input cells of its own row. A name passes when it says where its row stands against the rule: a boundary (the youngest age, the 16-week line), a span of weeks, or the dose the rule asks for. FAILS when every added or changed name only repeats the words or values of its own input cells, such as "15 weeks, one vaccination" beside 15 and 1, or when the answer removes the scenario column.
  > Names like "Puppy with one vaccination" / "Puppy with two vaccinations" just repeat input cells; "At the age where one is enough" is the only boundary-ish name.
- ✅ **no-decoder-in-app-code**: No init(from:) is written into the app code under Sources/.
- ✅ **compiles**: swift build --build-tests succeeds against the package scaffolding
- ✅ **tests-pass**: The tests pass when run against Admission

### ✅ Eval eval-43-ski-rental-bindings

**13/13** · 121262 tokens · 22584ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest attribute
- ✅ **tabletest-function-in-a-type**: Every @TableTest function is declared inside a type (a struct, class, enum or an extension of one), not at file scope.
- ✅ **no-if-switch-in-method**: Test function body contains no if, guard or switch statement and no ternary
- ✅ **no-default-in-test-function**: No @TableTest function body defaults a value with ??, or calls .map, .flatMap or .compactMap on one of its parameters.
- ✅ **has-question-mark-column**: Table has at least one column name ending with '?' (e.g. 'Fits?')
- ✅ **fix-made**: Binding.releaseSetting(for:) now gives a skier of exactly 80 kg the setting of the 80 kg-and-over class (7.0 for an intermediate, 6.5 for a beginner, 8.0 for an expert), and a test case at exactly 80 kg asserts it. FAILS when the code still gives 80 kg the lighter class, or when no test case is at 80 kg.
- ✅ **no-lone-test-beside-the-table**: The 80 kg case is an element of the releaseSettings collection that the @Test(arguments:) reads, or a row of a @TableTest that replaces that test. FAILS when a new plain @Test, or a second @Test(arguments:) or @TableTest, holds the 80 kg case while the release-setting test over releaseSettings still exists.
- ✅ **unconverted-table-reported**: The answer either converts the release-setting @Test(arguments:) to a @TableTest, or says in its response that this test stays a @Test(arguments:). FAILS when the test stays unconverted and the response does not name it.
- ✅ **conversion-kept-apart**: When the answer converts the release-setting test to a @TableTest, the response states the conversion as a step apart from the fix (its own step, commit or paragraph), not as part of the fix. PASSES when the answer does not convert it.
- ✅ **original-cases-kept**: The six original release-setting cases remain with the same weights, levels and settings (35 kg beginner 2.5, 55 kg intermediate 4.5, 70 kg intermediate 5.5, 72 kg expert 6.5, 90 kg intermediate 7.0, 95 kg beginner 6.5), beside the new case or cases at 80 kg. FAILS when an original case is dropped or one of its values changes.
- ✅ **no-decoder-in-app-code**: No init(from:) is written into the app code under Sources/.
- ✅ **compiles**: swift build --build-tests succeeds against the package scaffolding
- ✅ **tests-pass**: The tests pass when run against Binding

### ⚠️ Eval eval-44-ballot-box-replacement

**11/12** · 286015 tokens · 58746ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest attribute
- ✅ **tabletest-function-in-a-type**: Every @TableTest function is declared inside a type (a struct, class, enum or an extension of one), not at file scope.
- ✅ **no-if-switch-in-method**: Test function body contains no if, guard or switch statement and no ternary
- ✅ **no-default-in-test-function**: No @TableTest function body defaults a value with ??, or calls .map, .flatMap or .compactMap on one of its parameters.
- ✅ **has-question-mark-column**: Table has at least one column name ending with '?' (e.g. 'Result?')
- ❌ **no-branch-on-table-enum-outside-it**: A switch, an if or guard that compares with a case, or a ternary on a case, of a test-local enum with String raw values is in a member of that enum (the enum itself or an extension of it), never in another function of the test type that takes the enum or reads a property of its type.
  > Tests/PollingTests/BallotBoxTests.swift: box branches on the test-local enum Poll (if/guard on poll) outside enum Poll; Tests/PollingTests/BallotBoxTests.swift: box branches on the test-local enum Ballot (if/guard on ballot) outside enum Ballot
- ✅ **each-refusal-has-a-row**: Each of the three refusals has its own row, or its own value-set member, with its reason in an expectation cell: the poll is closed (pollClosed), the voter has no vote in the box (noVoteToReplace), the voter's ballot is sealed (ballotSealed). FAILS when a reason has no case.
- ✅ **unchanged-state-asserted**: A refused case asserts the state of the box after the call — the voter's vote (vote(of:)) and the tally — read from the box, in addition to the returned refusal. FAILS when a refused case asserts only the returned value.
- ✅ **unchanged-state-has-a-contrast**: The state after a refusal is asserted in one of three forms: a table that also has a row where the state changes (the accepted replacement); one row with a value set of the refusal reasons; or a before-state column whose cells differ between the refused rows, so that the after-state column differs too. FAILS when an after-state column (the voter's vote or the tally after the call) has the same cell in every row of a table with two or more rows, and when the unchanged state is asserted with a constant in the test body.
- ✅ **refusal-as-data**: The refusal reason is an expectation cell (such as refused(pollClosed), or a Refusal column), and the replaced and refused rows share one assertion. FAILS when the body chooses what to assert by a branch, or when the refusal is a constant in the body rather than a cell.
- ✅ **no-decoder-in-app-code**: No init(from:) is written into the app code under Sources/.
- ✅ **compiles**: swift build --build-tests succeeds against the package scaffolding

### ⚠️ Eval eval-45-train-delay-notice

**8/10** · 161684 tokens · 40861ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest attribute
- ✅ **tabletest-function-in-a-type**: Every @TableTest function is declared inside a type (a struct, class, enum or an extension of one), not at file scope.
- ✅ **no-if-switch-in-method**: Test function body contains no if, guard or switch statement and no ternary
- ✅ **no-default-in-test-function**: No @TableTest function body defaults a value with ??, or calls .map, .flatMap or .compactMap on one of its parameters.
- ✅ **has-question-mark-column**: Table has at least one column name ending with '?' (e.g. 'Text?')
- ❌ **rules-tested-on-the-decision**: A @TableTest calls DelayNotice.notice(for:), the function that decides the notice, and its rows hold both sides of the 5-minute and the 60-minute line (4 and 5, 59 and 60). FAILS when the boundaries are tested only through the text of the notice.
  > No @TableTest function calls notice(for:
- ❌ **no-long-text-cell**: No cell of a @TableTest table is longer than 100 characters. The table holds the decision as data, and a sentence is asserted outside the table. FAILS on a single long cell, since one long cell makes the whole table wide.
  > wordsTheNoticeFromTheDeparturesStatus, row 1: a cell of 153 characters; wordsTheNoticeFromTheDeparturesStatus, row 2: a cell of 150 characters; wordsTheNoticeFromTheDeparturesStatus, row 3: a cell of 151 characters
- ✅ **no-decoder-in-app-code**: No init(from:) is written into the app code under Sources/.
- ✅ **compiles**: swift build --build-tests succeeds against the package scaffolding
- ✅ **tests-pass**: The tests pass when run against DelayNotice

