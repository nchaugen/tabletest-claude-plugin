# Eval Review — tabletest-swift, Iteration 3

**Model:** sonnet (claude-sonnet-5-5) · **Grading:** claude-sonnet-5-5 · **Date:** 2026-11-26 · **Evals:** 4

## Summary

44/47 (93.6%) · 763247 tokens · 159.6s · $0.8134

_Cost figures are Claude Code list-price estimates; actual billing may differ (e.g. promotional pricing). Timed-out evals score 0 with unrecorded token usage._

> ⛔ **Void comparison — none of the 4 evals could be compared** vs iteration 2. Every delta below is computed over nothing; an absence of movement here is not evidence that nothing moved. See `analysis-todo.md`.

## Delta vs Iteration 2

**Not comparable (4) — excluded from the deltas above:**
- ⚠️ eval-42-dog-daycare-admission: iteration 2 did not run this eval; nothing to compare against
- ⚠️ eval-43-ski-rental-bindings: iteration 2 did not run this eval; nothing to compare against
- ⚠️ eval-44-ballot-box-replacement: iteration 2 did not run this eval; nothing to compare against
- ⚠️ eval-45-train-delay-notice: iteration 2 did not run this eval; nothing to compare against

## Resource Comparison vs Iteration 2

| Eval | Pass Rate | Prev | Tokens | Prev | Time(s) | Prev |
|------|-----------|------|--------|------|---------|------|
| eval-42-dog-daycare-admission | 11/12 | — | 190472 | — | 33.6 | — |
| eval-43-ski-rental-bindings | 13/13 | — | 77670 | — | 17.6 | — |
| eval-44-ballot-box-replacement | 12/12 | — | 329747 | — | 56.8 | — |
| eval-45-train-delay-notice | 8/10 | — | 165358 | — | 51.5 | — |

## Per-Eval Results

### ⚠️ Eval eval-42-dog-daycare-admission

**11/12** · 190472 tokens · 33618ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest attribute
- ✅ **tabletest-function-in-a-type**: Every @TableTest function is declared inside a type (a struct, class, enum or an extension of one), not at file scope.
- ✅ **no-if-switch-in-method**: Test function body contains no if, guard or switch statement and no ternary
- ✅ **no-default-in-test-function**: No @TableTest function body defaults a value with ??, or calls .map, .flatMap or .compactMap on one of its parameters.
- ✅ **has-question-mark-column**: Table has at least one column name ending with '?' (e.g. 'Decision?')
- ✅ **new-cases-are-rows-of-the-existing-table**: The cases of the new rule (a puppy under 16 weeks needs a second vaccination) are rows of the existing admission table, the one that already has the age and vaccination columns. FAILS when a new @TableTest or @Test function holds any of them, or when the admission table is split in two.
- ✅ **untouched-tables-unchanged**: The play-group table and the daily-fee table are byte-equal to the scaffold's.
- ✅ **sixteen-weeks-both-sides**: With one vaccination, the admission table shows both sides of the 16-week line: a dog under 16 weeks gives needsVaccination and a dog of 16 weeks gives admitted. Rows at 15 and 16 weeks pass, and so does a value set whose largest member is 15 beside a row at 16. FAILS when no row is at 16 weeks, or when no row or value-set member is at 15 weeks.
- ❌ **scenario-names-add-to-the-row**: Read each scenario name the answer added or changed in the admission table beside the input cells of its own row. A name passes when it says where its row stands against the rule: a boundary (the youngest age, the 16-week line), a span of weeks, or the dose the rule asks for. FAILS when every added or changed name only repeats the words or values of its own input cells, such as "15 weeks, one vaccination" beside 15 and 1, or when the answer removes the scenario column.
  > Names like "Puppy with a single vaccination" beside {12, 15} and 1 mostly repeat the cells.
- ✅ **no-decoder-in-app-code**: No init(from:) is written into the app code under Sources/.
- ✅ **compiles**: swift build --build-tests succeeds against the package scaffolding
- ✅ **tests-pass**: The tests pass when run against Admission

### ✅ Eval eval-43-ski-rental-bindings

**13/13** · 77670 tokens · 17615ms

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

### ✅ Eval eval-44-ballot-box-replacement

**12/12** · 329747 tokens · 56809ms

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

### ⚠️ Eval eval-45-train-delay-notice

**8/10** · 165358 tokens · 51524ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest attribute
- ✅ **tabletest-function-in-a-type**: Every @TableTest function is declared inside a type (a struct, class, enum or an extension of one), not at file scope.
- ✅ **no-if-switch-in-method**: Test function body contains no if, guard or switch statement and no ternary
- ✅ **no-default-in-test-function**: No @TableTest function body defaults a value with ??, or calls .map, .flatMap or .compactMap on one of its parameters.
- ✅ **has-question-mark-column**: Table has at least one column name ending with '?' (e.g. 'Text?')
- ❌ **rules-tested-on-the-decision**: A @TableTest calls DelayNotice.notice(for:), the function that decides the notice, and its rows hold both sides of the 5-minute and the 60-minute line (4 and 5, 59 and 60). FAILS when the boundaries are tested only through the text of the notice.
  > No @TableTest function calls notice(for:
- ❌ **no-long-text-cell**: No cell of a @TableTest table is longer than 100 characters. The table holds the decision as data, and a sentence is asserted outside the table. FAILS on a single long cell, since one long cell makes the whole table wide.
  > tellsPassengersTheStatusOfADeparture, row 1: a cell of 151 characters; tellsPassengersTheStatusOfADeparture, row 2: a cell of 148 characters; tellsPassengersTheStatusOfADeparture, row 3: a cell of 149 characters
- ✅ **no-decoder-in-app-code**: No init(from:) is written into the app code under Sources/.
- ✅ **compiles**: swift build --build-tests succeeds against the package scaffolding
- ✅ **tests-pass**: The tests pass when run against DelayNotice

