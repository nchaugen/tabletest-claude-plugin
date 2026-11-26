# Eval Review — tabletest-swift, Iteration 21

**Model:** sonnet (claude-sonnet-5-5) · **Grading:** claude-sonnet-5-5 · **Date:** 2026-11-26 · **Evals:** 10

## Summary

113/114 (99.1%) · 2390214 tokens · 495.7s · $1.9231

_Cost figures are Claude Code list-price estimates; actual billing may differ (e.g. promotional pricing). Timed-out evals score 0 with unrecorded token usage._

> ⛔ **Void comparison — none of the 10 evals could be compared** vs iteration 20. Every delta below is computed over nothing; an absence of movement here is not evidence that nothing moved. See `analysis-todo.md`.

## Delta vs Iteration 20

**Not comparable (10) — excluded from the deltas above:**
- ⚠️ eval-36-bowling-lanes-convert: iteration 20 did not run this eval; nothing to compare against
- ⚠️ eval-37-ev-charging-arguments: iteration 20 did not run this eval; nothing to compare against
- ⚠️ eval-38-washing-machine-cells: fingerprint differs from iteration 20; re-baseline to compare
- ⚠️ eval-39-playlist-screen-columns: iteration 20 did not run this eval; nothing to compare against
- ⚠️ eval-40-parcel-locker-rules: iteration 20 did not run this eval; nothing to compare against
- ⚠️ eval-41-hotel-cancellation-routing: iteration 20 did not run this eval; nothing to compare against
- ⚠️ eval-42-dog-daycare-admission: iteration 20 did not run this eval; nothing to compare against
- ⚠️ eval-44-ballot-box-replacement: iteration 20 did not run this eval; nothing to compare against
- ⚠️ eval-45-train-delay-notice: iteration 20 did not run this eval; nothing to compare against
- ⚠️ eval-46-library-loan-reminder: iteration 20 did not run this eval; nothing to compare against

## Resource Comparison vs Iteration 20

| Eval | Pass Rate | Prev | Tokens | Prev | Time(s) | Prev |
|------|-----------|------|--------|------|---------|------|
| eval-36-bowling-lanes-convert | 11/11 | — | 224500 | — | 31.9 | — |
| eval-37-ev-charging-arguments | 10/10 | — | 167396 | — | 34.2 | — |
| eval-38-washing-machine-cells | 12/12 | 12/12 | 174948 | 173623 | 64.5 | 56.2 |
| eval-39-playlist-screen-columns | 11/12 | — | 233187 | — | 59.2 | — |
| eval-40-parcel-locker-rules | 13/13 | — | 354769 | — | 82.9 | — |
| eval-41-hotel-cancellation-routing | 9/9 | — | 283065 | — | 34.1 | — |
| eval-42-dog-daycare-admission | 12/12 | — | 106362 | — | 21.4 | — |
| eval-44-ballot-box-replacement | 12/12 | — | 424597 | — | 86.9 | — |
| eval-45-train-delay-notice | 11/11 | — | 209884 | — | 38.4 | — |
| eval-46-library-loan-reminder | 12/12 | — | 211506 | — | 42.2 | — |

## Per-Eval Results

### ✅ Eval eval-36-bowling-lanes-convert

**11/11** · 224500 tokens · 31889ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest attribute
- ✅ **tabletest-function-in-a-type**: Every @TableTest function is declared inside a type (a struct, class, enum or an extension of one), not at file scope.
- ✅ **no-if-switch-in-method**: Test function body contains no if, guard or switch statement and no ternary
- ✅ **scenario-column-present**: Table has a scenario/description column as the leftmost column
- ✅ **has-question-mark-column**: Table has at least one column name ending with '?' (e.g. 'Price?')
- ✅ **price-tests-become-one-table**: The five price tests become one @TableTest with exactly five rows, one per original test, each keeping that test's lane, hours, membership and price (standard 2 h → 60, deluxe 2 h → 90, kids 1 h → 20, standard 5 h → 90, deluxe 2 h as a member → 72). FAILS when a row is added or dropped, when a value changes, or when the five original price tests are kept beside the table.
- ✅ **rejection-stays-its-own-test**: The zero-hours case stays its own test that expects BookingError.noHours (for example with #expect(throws:)). FAILS when it becomes a row of the price table with an optional, sentinel or error-text price, or when it is deleted.
- ✅ **lane-converts-on-the-app-type**: The lane cell converts because the app's Lane enum gained a one-word conformance in Sources/ (CaseIterable, Codable, Decodable, or String raw values). FAILS when the test target turns a word into a Lane itself: a String column mapped by a switch or a dictionary, a test-local copy of the enum, or a TableTextDecodable / TableValueDecodable that lists the cases by hand.
- ✅ **no-decoder-in-app-code**: No init(from:) is written into the app code under Sources/.
- ✅ **compiles**: swift build --build-tests succeeds against the package scaffolding
- ✅ **tests-pass**: The converted tests pass when run against LanePrice

### ✅ Eval eval-37-ev-charging-arguments

**10/10** · 167396 tokens · 34170ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest attribute
- ✅ **tabletest-function-in-a-type**: Every @TableTest function is declared inside a type (a struct, class, enum or an extension of one), not at file scope.
- ✅ **no-if-switch-in-method**: Test function body contains no if, guard or switch statement and no ternary
- ✅ **has-question-mark-column**: Table has at least one column name ending with '?' (e.g. 'Euros?')
- ✅ **named-collection-becomes-a-table**: The `sessions` collection becomes one @TableTest whose columns are its tuple labels (charger, minutes, euros, with the euros as the expectation) and whose eight rows keep the original values. The `sessions` property is removed. FAILS when the collection is kept and passed to the table, when rows are added or dropped, or when a value changes.
- ✅ **zipped-literals-become-a-table**: The unread-meter test's zip of two inline lists becomes its own @TableTest with one row per pair: ac → 10, fast → 25, ultrafast → 40. FAILS when the pairs stay in a zip or in two parallel lists, or when they are merged into the sessions table.
- ✅ **computed-arguments-stay**: The test whose arguments are Charger.allCases stays a @Test(arguments: Charger.allCases), and the response or a comment gives the reason: the list comes from the app, so a charger added there is tested with no change here. FAILS when it becomes a table that lists the chargers by hand, or when it is kept with no reason given.
- ✅ **case-count-kept**: The converted tests run the same 14 cases as before: 8 sessions, 3 unread meters and 3 chargers free for five minutes. PASSES when the count differs only for a change the response names and explains. FAILS on an unexplained added, dropped or merged case.
- ✅ **compiles**: swift build --build-tests succeeds against the package scaffolding
- ✅ **tests-pass**: The converted tests pass when run against ChargingFee

### ✅ Eval eval-38-washing-machine-cells

**12/12** · 174948 tokens · 64522ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest attribute
- ✅ **tabletest-function-in-a-type**: Every @TableTest function is declared inside a type (a struct, class, enum or an extension of one), not at file scope.
- ✅ **no-if-switch-in-method**: Test function body contains no if, guard or switch statement and no ternary
- ✅ **has-question-mark-column**: Table has at least one column name ending with '?' (e.g. 'Outcome?')
- ✅ **outcome-cell-spelled-as-swift**: The expected outcome is one cell that spells the WashOutcome case as Swift code does, such as runs(minutes: 120) or tooHot(limit: 40), because WashOutcome gained Codable (or Decodable) in Sources/. FAILS when the outcome is split into a case-name column and number columns that the body puts back together, when the body builds the expected outcome from other columns, or when the test target maps words to cases by hand.
- ✅ **fabric-converts-on-the-app-type**: The fabric converts from a cell because the app's Fabric enum gained a one-word conformance in Sources/ (CaseIterable, Codable, Decodable, or String raw values). FAILS when the test target turns a word into a Fabric itself: a switch, a dictionary, a test-local copy of the enum, or a hand-written conversion.
- ✅ **app-types-change-by-one-word**: The only changes to the app code under Sources/ are conformances added to type declarations; no field of WashProgram is made optional, loses its default, or changes type, and no initialiser or method is added. PASSES when Sources/ is unchanged apart from such conformances.
- ✅ **no-decoder-in-app-code**: No init(from:) is written into the app code under Sources/.
- ✅ **program-rows-state-only-what-they-set**: Each row states only the program settings that the rule under test varies; the other settings keep the app's defaults. Either the settings are columns and the body builds the WashProgram, or a map cell names only the settings it sets and a TableValueDecodable conformance in the test target reads it (for example with readingFields). FAILS when every row must spell all five settings to decode, or when a map cell is read by an init(from:) or a synthesised Decodable that the app was changed to allow.
- ✅ **each-rule-has-its-rows**: Each of the four rules of the doc comment has rows that show it: each fabric at its temperature limit and one step above it; the base time of each fabric and the 10-minute step for 10 °C; a prewash, an extra rinse, and both together; and a delayed start against no delay. FAILS when a rule has no row, or when a limit is shown on one side only.
- ✅ **compiles**: swift build --build-tests succeeds against the package scaffolding
- ✅ **tests-pass**: The tests pass when run against WashPlanner

### ⚠️ Eval eval-39-playlist-screen-columns

**11/12** · 233187 tokens · 59232ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest attribute
- ✅ **tabletest-function-in-a-type**: Every @TableTest function is declared inside a type (a struct, class, enum or an extension of one), not at file scope.
- ✅ **no-if-switch-in-method**: Test function body contains no if, guard or switch statement and no ternary
- ✅ **has-question-mark-column**: Table has at least one column name ending with '?' (e.g. 'Actions?')
- ✅ **edit-columns-are-the-deciding-inputs**: For a saved playlist, the input columns are the inputs the screen reads to decide the actions: the unread rules and the change the user made to the rules (such as 'adds a rule' or 'removes every rule'), with test code that builds the query on screen from the change. FAILS when the columns hold full rule sets or queries, saved and on screen, that the reader must compare to find the change, or when a scenario name states a deciding input that no column holds.
- ❌ **irrelevant-input-is-a-value-set**: An input that does not change the actions is shown once, as a value set ({...}) or left out of the table with the fixture stating it: the order on the library, and the count of unread rules beyond zero. FAILS when two rows differ only in such an input and have the same expectations.
  > `Library as opened | no change | false | []` and `Library sorted by artist | sorts by artist | false | []` differ only in library order.
- ✅ **crossing-the-inputs-finds-missing-rows**: The playlist table has a row for a playlist with an unread rule whose every rule is removed (actions: revert), which the original cases lacked.
- ✅ **rule-cell-names-only-what-it-sets**: Where a cell holds a rule set, it names only the rules it sets (such as [:] or [genres: {rock}]) and a TableValueDecodable conformance on SmartRules in the test target reads it, for example with readingFields. PASSES when no cell holds a rule set (the rules are columns and the body builds SmartRules). FAILS when every rule cell must name all three fields, or when the app was changed so a synthesised decoder accepts the cell.
- ✅ **action-list-converts-on-the-app-type**: The actions list cell converts because PlaylistScreen.Action gained a one-word conformance in Sources/ (CaseIterable, Codable, Decodable, or String raw values). FAILS when the test target turns words into actions itself (a switch, a dictionary, a test-local copy of the enum), or when the expectation is not a list of actions.
- ✅ **no-decoder-in-app-code**: No init(from:) is written into the app code under Sources/.
- ✅ **compiles**: swift build --build-tests succeeds against the package scaffolding
- ✅ **tests-pass**: The tests pass when run against PlaylistScreen

### ✅ Eval eval-40-parcel-locker-rules

**13/13** · 354769 tokens · 82872ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest attribute
- ✅ **tabletest-function-in-a-type**: Every @TableTest function is declared inside a type (a struct, class, enum or an extension of one), not at file scope.
- ✅ **no-if-switch-in-method**: Test function body contains no if, guard or switch statement and no ternary
- ✅ **has-question-mark-column**: Table has at least one column name ending with '?' (e.g. 'Result?')
- ✅ **result-cell-spelled-as-swift**: The expected result is one cell that spells the PickupResult case as Swift code does, such as wrongCode(attemptsLeft: 2) or returnedToDepot, because PickupResult gained Codable (or Decodable) in Sources/. FAILS when the result is split into a case-name column and an attempts column that the body puts back together, when the body builds the expected result from other columns, or when the test target maps words to cases by hand.
- ✅ **no-decoder-in-app-code**: No init(from:) is written into the app code under Sources/.
- ✅ **countdown-and-block-shown**: The rows show the countdown of wrong codes — 2 attempts left after the first, 1 after the second, blocked at the third — and that a blocked parcel stays blocked for the right code. FAILS when a step of the countdown has no row, or when no row gives the right code to a blocked parcel.
- ✅ **pickup-window-both-sides**: The 72-hour limit is shown from both sides: the pickup code opens at 72 hours after delivery, and the result is returnedToDepot at 73 (or the first hour after 72 the table uses). FAILS when only one side is shown, or when the boundary sits at another value without a reason the rules give.
- ✅ **assumption-surfaced-as-row**: Where the requirement leaves a point open and the answer must commit to a reading anyway, a row states the commitment. Judge ONLY the points the Expected Output Description names as underspecified — normally a section headed "Underspecified" — and of those, only the ones it says should become a row. **If it names no such point, or says the requirement leaves nothing open, this assertion PASSES**, and your evidence must quote the sentence that says so. Do not go looking for open points of your own: that description is the only list, and a point it does not name is not judged here. Enumerate before you decide: list each named point, and beside it the row that fixes it — its table, its scenario name, and the cells carrying the choice — or write "no row". A verdict reached without that list is not a verdict. The decidable test for a named point is whether the reading NOT taken would move a cell: name that reading and the cell it would change. PASSES when every named point has such a row. One row can fix several points, and one point can need a pair — the row taking the reading and the row just past it. FAILS when a named point is fixed only in prose: in the doc comment above the function, in a comment, in the response text, or in a name with no cell behind it. A sentence saying which reading was taken is not the row this asks for, and publishing it neither creates nor discharges the failure — whether an assumption is *published* is another assertion's subject, and whether the reading chosen is the better one is nobody's. FAILS when a named point is fixed nowhere. Follow the Expected Output Description's own words on what each point needs, never your own sense of what would be worth testing. Where it prescribes something other than a row — stating the choice in the doc comment above the function, or constructing the rows so the case cannot arise — that is the resolution, and the point PASSES when the answer does it. Where it marks a point out of scope, or foreseeable-but-omitted, or says omitting it is not penalised, the point is NOT judged. Where it makes a point conditional on a choice the answer did not make — "if the answer rejects these, a row should show it" — the point is NOT judged. And rows you think are missing for any other reason are never a failure here: this assertion judges the named points and nothing else.
- ✅ **row-kills-a-wrong-implementation**: A row that exists to defeat a specific wrong implementation must actually defeat it. Judge ONLY the coverage obligations the Expected Output Description states in that form — an obligation whose point is that a plausible but wrong version of the rule gets this case wrong, such as "the correct minimal pair is found where a greedy pick fails" or "companions break a tie between equally-minimal sets". **If it states no such obligation, this assertion PASSES**, and your evidence must quote the sentence you judged that on. Do not invent wrong implementations of your own: the Expected Output Description names them, or states the obligation clearly enough to imply one, and nothing else is judged here. Enumerate before you decide: for each such obligation, name the row meant to discharge it, name the wrong rule it is aimed at, and work out what that wrong rule would produce **from that row's own input cells**. A verdict reached without that list is not a verdict. FAILS when the wrong rule would produce the row's expected output too — the row cannot tell the two apart, whatever it is called. FAILS when no row targets the obligation at all. PASSES when every such obligation has a row whose expected output the named wrong rule gets wrong. Judge cells, never scenario names: a name like "the correct pair, not just any pair" claims the property and does not confer it, and a row an approximating rule would answer the same way fails here under any scenario name. Two things are NOT failures here. A row satisfiable by some contrived lookup of the very answers it states — every finite table is, and that is not what this asks. And rows you think are missing for an obligation the Expected Output Description does not put in this form: coverage in general is not this assertion's subject, only the obligations that name a wrong rule to defeat.
- ✅ **row-values-traceable-to-requirement**: Every input value is there for a reason the table could state. Enumerate before you decide: list every row carrying an input outside the range the domain admits, and every pair of rows that differ in one input and straddle a change of outcome. Write the reason each value is there. A verdict reached without that list is not a verdict. FAILS when a row carries an input the domain does not admit — an age of 800, a claim count of 999 — and the rule that table states is not itself about values in that range. Such a value is there to move some quantity the table is not about, and the row then demonstrates nothing the requirement asks for. Publishing the reason does not rescue it: "age 760 is used only to force an internal risk score above the rejection threshold" is a clear statement of the defect, not a defence. FAILS when a boundary pair's values can only be justified by arithmetic the test is NOT PERMITTED to publish. The check in one question: could a column, a title or the doc comment above the function say why these two values and not others, without naming something this eval forbids the test to name? Approving at age 9 and rejecting at age 10 fails it — nothing publishable separates 9 from 10, and the only thing that does is a private formula. PASSES when an out-of-range value is the subject of the rule the table states — a value set of {-1, -10, -100} in a table whose rule is that negative hours floor to zero — or when it demonstrates a precedence the requirement states and the row says which, as "the oldest applicant still under the claim limit" does at age 100 with 4 claims. PASSES when a boundary pair brackets a value the requirement names (650 against 651 where the requirement says "above 650"; 40 against 41 hours) or a boundary of a rule the table's own columns show, such as a weight of 1.0 against 1.01 beside the rate each produces. A threshold the solution found in the code and then PUBLISHED as a column or a description PASSES here; whether publishing it was allowed is another assertion's subject. A value that is merely unstated is also another assertion's subject, not this one. Judge every @TableTest function in the file.
- ✅ **scenario-names-describe-conditions**: Scenario names name the variation the row exercises, not the result it produces. The decidable test: FAILS when a scenario name states or paraphrases a value that appears in an expectation column of that same row — 'User cannot delete' beside an Allowed? cell of false, 'No discount applies' beside a Discount? cell of 0.00, 'Contractor gets no bonus' beside a Bonus? cell of 0. Also FAILS on generic labels ('Test 1', 'Test case 2'), which name no variation at all. PASSES otherwise. Naming the rule or the situation is correct even when it makes the outcome inferable — 'Delivery and pickup items always split', 'At the standard threshold, not above it', 'EU express, light package', 'Renewal with no claims', 'Missing name' all PASS. The failure this catches is a name echoing its own expectation cell, not a name that describes what the row is about, and not a name from which a reader who knows the rule could predict the outcome. A single offending name fails the assertion. Judge every @TableTest function in the file.
- ✅ **compiles**: swift build --build-tests succeeds against the package scaffolding

### ✅ Eval eval-41-hotel-cancellation-routing

**9/9** · 283065 tokens · 34089ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest attribute — in a Swift package that already depends on TableTest, the table-driven request is answered with @TableTest.
- ✅ **fee-cases-are-table-rows**: The fee cases are the rows of a @TableTest. FAILS when they are supplied with @Test(arguments:), a loop, or one test function per case, also when a @TableTest exists elsewhere in the answer.
- ✅ **tabletest-function-in-a-type**: Every @TableTest function is declared inside a type (a struct, class, enum or an extension of one), not at file scope.
- ✅ **no-if-switch-in-method**: Test function body contains no if, guard or switch statement and no ternary
- ✅ **has-question-mark-column**: Table has at least one column name ending with '?' (e.g. 'Fee?')
- ✅ **full-tier-enumeration**: Every fee tier is represented (free, 50%, 80%, full value) and every boundary is tested from both sides: 30 days (free) vs 29 days (50%), 7 days (50%) vs 6 days (80%), 1 day (80%) vs 0 days (full value). Middle-tier boundaries (7/6 and 1/0) must not be skipped in favour of only the outer edges.
- ✅ **exception-case-separated**: The started-stay case is expressed as its own test using #expect(throws:) — not mixed into the fee table via sentinel values, optional expectations, or branching inside the test body.
- ✅ **concrete-domain-values**: Row values are concrete domain values (days like 29, fees like 100 for a 200-euro booking) — not abstract placeholders, computed expressions like bookingValue / 2, or symbolic constants that hide the actual numbers from the reader.
- ✅ **compiles**: swift build --build-tests succeeds against the package scaffolding

### ✅ Eval eval-42-dog-daycare-admission

**12/12** · 106362 tokens · 21352ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest attribute
- ✅ **tabletest-function-in-a-type**: Every @TableTest function is declared inside a type (a struct, class, enum or an extension of one), not at file scope.
- ✅ **no-if-switch-in-method**: Test function body contains no if, guard or switch statement and no ternary
- ✅ **no-default-in-test-function**: No @TableTest function body defaults a value with ??, or calls .map, .flatMap or .compactMap on one of its parameters.
- ✅ **has-question-mark-column**: Table has at least one column name ending with '?' (e.g. 'Decision?')
- ✅ **new-cases-are-rows-of-the-existing-table**: The cases of the new rule (a puppy under 16 weeks needs a second vaccination) are rows of the existing admission table, the one that already has the age and vaccination columns. FAILS when a new @TableTest or @Test function holds any of them, or when the admission table is split in two.
- ✅ **untouched-tables-unchanged**: The play-group table and the daily-fee table are byte-equal to the scaffold's.
- ✅ **sixteen-weeks-both-sides**: With one vaccination, the admission table shows both sides of the 16-week line: a dog under 16 weeks gives needsVaccination and a dog of 16 weeks gives admitted. Rows at 15 and 16 weeks pass, and so does a value set whose largest member is 15 beside a row at 16. FAILS when no row is at 16 weeks, or when no row or value-set member is at 15 weeks.
- ✅ **scenario-names-add-to-the-row**: Read each scenario name the answer added or changed in the admission table beside the input cells of its own row. A name passes when it says where its row stands against the rule: a boundary (the youngest age, the 16-week line), a span of weeks, or the dose the rule asks for. FAILS when every added or changed name only repeats the words or values of its own input cells, such as "15 weeks, one vaccination" beside 15 and 1, or when the answer removes the scenario column.
- ✅ **no-decoder-in-app-code**: No init(from:) is written into the app code under Sources/.
- ✅ **compiles**: swift build --build-tests succeeds against the package scaffolding
- ✅ **tests-pass**: The tests pass when run against Admission

### ✅ Eval eval-44-ballot-box-replacement

**12/12** · 424597 tokens · 86922ms

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

### ✅ Eval eval-45-train-delay-notice

**11/11** · 209884 tokens · 38440ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest attribute
- ✅ **tabletest-function-in-a-type**: Every @TableTest function is declared inside a type (a struct, class, enum or an extension of one), not at file scope.
- ✅ **no-if-switch-in-method**: Test function body contains no if, guard or switch statement and no ternary
- ✅ **no-default-in-test-function**: No @TableTest function body defaults a value with ??, or calls .map, .flatMap or .compactMap on one of its parameters.
- ✅ **has-question-mark-column**: Table has at least one column name ending with '?' (e.g. 'Text?')
- ✅ **rules-tested-on-the-decision**: A @TableTest calls DelayNotice.notice(for:), the function that decides the notice, and its rows hold both sides of the 5-minute and the 60-minute line (4 and 5, 59 and 60). FAILS when the boundaries are tested only through the text of the notice.
- ✅ **no-long-text-cell**: No cell of a @TableTest table is longer than 100 characters. The table holds the decision as data, and a sentence is asserted outside the table. FAILS on a single long cell, since one long cell makes the whole table wide.
- ✅ **constant-text-not-restated**: Outside the @TableTest tables, the tests hold at most one string literal longer than 100 characters. A person reads the wording, so a copy of a constant sentence only checks that the two copies agree. One sentence can be the wiring check. FAILS on a test for each sentence.
- ✅ **no-decoder-in-app-code**: No init(from:) is written into the app code under Sources/.
- ✅ **compiles**: swift build --build-tests succeeds against the package scaffolding
- ✅ **tests-pass**: The tests pass when run against DelayNotice

### ✅ Eval eval-46-library-loan-reminder

**12/12** · 211506 tokens · 42220ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest attribute
- ✅ **tabletest-function-in-a-type**: Every @TableTest function is declared inside a type (a struct, class, enum or an extension of one), not at file scope.
- ✅ **no-if-switch-in-method**: Test function body contains no if, guard or switch statement and no ternary
- ✅ **no-default-in-test-function**: No @TableTest function body defaults a value with ??, or calls .map, .flatMap or .compactMap on one of its parameters.
- ✅ **has-question-mark-column**: Table has at least one column name ending with '?' (e.g. 'Text?')
- ✅ **rules-tested-on-the-decision**: LoanReminder.text(for:) calls a function or a computed property of the app that decides the reminder, and a @TableTest calls that function with rows on both sides of the 3-day, the due-date and the 30-day line (4 and 3, 0 and -1, -29 and -30). FAILS when the decision stays inside text(for:), and when the new function is a copy that text(for:) does not call.
- ✅ **no-long-text-cell**: No cell of a @TableTest table is longer than 100 characters. The table holds the decision as data, and a reader reviews the wording. FAILS on a single long cell, since one long cell makes the whole table wide.
- ✅ **constant-text-not-restated**: Outside the @TableTest tables, the tests hold at most one string literal longer than 100 characters. A person reads the wording, so a copy of a constant sentence only checks that the two copies agree. One sentence can be the wiring check. FAILS on a test for each sentence.
- ✅ **no-decoder-in-app-code**: No init(from:) is written into the app code under Sources/.
- ✅ **compiles**: swift build --build-tests succeeds against the package scaffolding
- ✅ **tests-pass**: The tests pass when run against LoanReminder
- ✅ **verify-tests-pass**: The eval's own tests pass against the changed app code: text(for:) gives the same text as before on both sides of each line, also for a reserved item.

