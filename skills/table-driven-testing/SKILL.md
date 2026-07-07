---
name: table-driven-testing
description: Use when writing or converting parameterised, data-driven, or table-driven tests in any non-JVM ecosystem — pytest parametrize (Python), Swift Testing @Test(arguments:), Jest/Vitest test.each, Go table-driven subtests, xUnit [Theory] — or any framework where several scenarios repeat the same assertion logic over different data. Trigger whenever the user wants to test multiple scenarios of one behaviour, even if they don't say "table". Do not use for Java or Kotlin projects — the tabletest skill owns those, including requests phrased as "table-driven tests".
---

# Table-Driven Testing Skill

Use this skill when several test scenarios exercise the same behaviour with different data, in ecosystems where the TableTest library is not available.

**Java/Kotlin: stop here.** If the project is Java or Kotlin (Maven/Gradle), use the `tabletest` skill instead — the TableTest library is the target idiom on the JVM, even when the request says "table-driven" or "parameterised" tests.

## The Table Model

Whatever the framework, a table-driven test has three parts:

- **Rows**: one scenario per row — its inputs and its expected outputs together, readable left to right as a complete example of the rule.
- **A single test body** that arranges, acts, and asserts. It contains no `if`/`switch`/`guard`, no loops over cases, and no computation of expected values — every decision lives in the data, not the body.
- **A name per row** describing the condition being tested, visible in test output so a failure identifies its scenario.

Everything below applies this model. Framework mechanics differ; the design principles do not.

## Framework Mechanics

### pytest (Python)

Rows are `pytest.param` entries; the id names the scenario:

```python
@pytest.mark.parametrize(
    ("income", "filing_status", "rate"),
    [
        pytest.param(15_000, FilingStatus.SINGLE, 0.10, id="bottom bracket"),
        pytest.param(55_000, FilingStatus.SINGLE, 0.22, id="middle bracket"),
        pytest.param(55_000, FilingStatus.JOINT, 0.12, id="joint middle bracket"),
    ],
)
def test_tax_bracket_by_income_and_status(income, filing_status, rate):
    assert bracket_for(income, filing_status).rate == rate
```

Always name cases — `pytest.param(..., id="...")` or an `ids=` argument. Auto-generated ids like `15000-SINGLE-0.1` force the reader to decode values; a written id states the condition.

**"Regardless of" inputs**: stacking a second `@pytest.mark.parametrize` multiplies the decorators into a cartesian product — use it when one input must not affect the outcome.

**Expected exceptions**: a case list mixing `pytest.raises` cases with return-value cases needs branching in the body — forbidden. Give rejection cases their own parametrized test built around `pytest.raises`.

### Swift Testing (Swift)

Rows are labelled tuples (or a small row struct) passed to `@Test(arguments:)`:

```swift
@Test("Standing by completed credit hours", arguments: [
    (creditHours: 29, standing: Standing.freshman),
    (creditHours: 30, standing: Standing.sophomore),
    (creditHours: 59, standing: Standing.sophomore),
    (creditHours: 60, standing: Standing.junior),
])
func standingByCreditHours(creditHours: Int, standing: Standing) {
    #expect(Standing(creditHours: creditHours) == standing)
}
```

**Cartesian footgun**: passing two collections — `arguments: inputs, expectations` — produces every combination, not paired rows. Pair with labelled tuples in one collection, a row struct, or `zip`. Use the two-collection cartesian form deliberately for "regardless of" inputs.

**Expected exceptions**: a separate `@Test` with `#expect(throws:)` — never sentinel values or branching in a parameterised body.

### Jest / Vitest (JavaScript / TypeScript)

The tagged-template form of `test.each` is a literal table with headers — prefer it:

```javascript
test.each`
  hours | rate  | fee
  ${1}  | ${3}  | ${0}
  ${4}  | ${3}  | ${6}
  ${7}  | ${3}  | ${21}
`('parking fee for $hours hours at rate $rate', ({ hours, rate, fee }) => {
  expect(parkingFee(hours, rate)).toBe(fee);
});
```

The test title interpolates row values — write it so each generated name reads as a condition. Expected rejections use `expect(() => ...).toThrow(...)` in their own `test.each` block.

### Go

Rows are a slice of structs with a `name` field; each runs as a named subtest:

```go
tests := []struct {
    name        string
    creditHours int
    standing    Standing
}{
    {"top of freshman range", 29, Freshman},
    {"bottom of sophomore range", 30, Sophomore},
}
for _, tt := range tests {
    t.Run(tt.name, func(t *testing.T) {
        if got := StandingFor(tt.creditHours); got != tt.standing {
            t.Errorf("StandingFor(%d) = %v, want %v", tt.creditHours, got, tt.standing)
        }
    })
}
```

The loop over the case slice is the framework mechanic here — the rule against loops applies inside the subtest body. Error-returning cases go in a separate table whose rows expect a specific error, not a mixed table with `wantErr bool` alongside unrelated expected values.

### xUnit (C#)

`[Theory]` with `[InlineData]` rows, or `TheoryData<...>` when rows need real types. `[InlineData]` has no per-row name — put the condition in a leading string argument or use `MemberData` with self-describing row objects. Expected exceptions use `Assert.Throws<T>` in their own theory.

## Table Design

### Name Scenarios by Condition, Not Outcome

Good scenario names answer "under what circumstances?" — not "what happens?". The outcome is already in the expectation values; naming it twice adds nothing, and when the expectation changes the name silently lies.

| Good                         | Bad             |
|------------------------------|-----------------|
| `negative input`             | `returns error` |
| `empty list`                 | `sum is zero`   |
| `user without licence`       | `cannot rent`   |
| `divisible by 4 but not 100` | `is leap year`  |

### Use Domain Terminology and Concrete Values

Parameter and field names use domain language (`credit_hours`, `filing_status`, `decision`) — not `a`, `b`, `val1`, `expected1`. Case values are concrete domain data (`29`, `0.22`, `Standing.FRESHMAN`) — not abstract codes, sentinel numbers, or expressions computed from other values. If an expected value is `base_rate * 2`, write the number and let the base rate appear as its own input so the reader can trace the derivation.

### Decompose Concerns into Separate Tests

**If you cannot name a behaviour without using "and", it is two concerns** — each gets its own parameterised test. One monolithic case list mixing unrelated rules multiplies rows (every rule × every other rule's values) and hides which rule any row is about.

Signs that concerns are mixed:
- Some rows need inputs that other rows leave at a placeholder value throughout
- Scenario names need qualifiers like "...for eligibility" vs "...for pricing"
- Two groups of expected outputs never both apply in the same row

Fewer cases per test is the expected result of separating concerns: each test carries only the rows that express its own rule, holding other inputs at a fixed obviously-valid value. The test count also guides implementation — five concern tables suggest five functions.

When rules interact by precedence (rule A overrides rule B), add a focused test whose rows show the precedence directly: an input that triggers both rules, expecting A's outcome.

### Separate Rules from Arithmetic

Tables should specify the interesting decisions — classifications, eligibility rules, tier lookups, state transitions — not prove that multiplication works. Split "which bracket applies?" (a decision table over incomes and statuses) from "what is owed at a given rate?" (a small arithmetic table over rate and deductions). Each table stays small and each failure points at one kind of mistake.

### Make Thresholds Visible

When a rule depends on a threshold or limit, include it in the row alongside the actual value — even when it is constant across every row:

```python
pytest.param(75, 75, True,  id="at the age limit"),
pytest.param(76, 75, False, id="just over the age limit"),
```

With `(customer_age, policy_age_limit, eligible)` fields, the reader sees both the value and the boundary it is compared against, and can tell whether the rule is strict or inclusive. Without it, the number is buried in the implementation. A constant column often signals configuration — ask under what circumstances the value would differ; the answer may reveal a second axis of rows.

### Cover Every Tier and Both Sides of Every Boundary

When inputs map to tiers (rate bands, size categories, standings), every tier appears in the rows, and every boundary is tested from both sides — the last value inside a tier and the first value of the next. Middle-tier boundaries are the ones most often skipped; outer edges alone do not pin down where the middle tiers change.

### Separate Expected-Error Cases

Cases that expect an exception/error get their own test using the framework's throw assertion (`pytest.raises`, `#expect(throws:)`, `.toThrow`, `Assert.Throws`, expected-error table in Go). Mixing them into a value-expectation table forces sentinel values (`None`, `-1`) or branching in the body — both hide the contract. The split also mirrors the API: callers handle the error path separately too.

### Include All Outputs of a Concern in One Test

When an operation produces multiple observable outputs, expect them all in the same rows. Splitting outputs of one behavioural concern across tests forces the reader to cross-reference several tables to understand one behaviour. Separate tests are for separate concerns, not separate outputs of the same concern.

### Frame Stateful Features as Transition Rules

For queues, workflows, inventories: each row is `state before → action → state after (+ message/result)`. Rows stay independent — no row depends on a previous row having run. Include the before **and** after state in the row even when the prompt describes the operation procedurally.

### Express "Regardless Of" Relationships

When one input must not affect the outcome, say so with data rather than prose: generate the combinations (stacked `parametrize` in pytest, two-collection `arguments:` in Swift Testing, `flatMap` over value lists in Jest, nested loops building the case slice in Go, `MemberData` generators in xUnit). Every generated combination must expect the same result — if results differ, the input does matter and belongs as ordinary distinct rows. Don't use combination generation as shorthand for "test several values".

### Absent Inputs Are Absent

Represent a genuinely missing input as the language's absence value (`None`, `nil`, `null`) — not `0`, `""`, or a made-up default. Defaulting logic the code under test applies belongs in the expectations, not pre-applied in the row data.

## Workflow

**Budget your reasoning.** If concerns are already listed in the prompt, use them directly — don't re-derive what's already stated. Working code you can revise beats perfect analysis that times out.

**Write incrementally.** For multi-concern features, write one parameterised test at a time. Each test written is a checkpoint.

### Writing Tests from a Feature Description

When tests come before the implementation:

1. **Read the feature description** and identify the rules/concerns — each becomes one parameterised test.
2. **Write the tests** following the design principles above.
3. **Keep or create a stub** for the code under test — signatures only, raising/throwing "not implemented" or returning nothing meaningful. Do not implement the logic unless asked: the user may want to iterate on test design first. Tests must compile/collect cleanly; failing on the stub is expected.

**Let tables drive the API decomposition.** Each concern's test should call a function whose parameters are exactly that concern's inputs. If a test needs a helper to fabricate raw data so a derived value reaches a target (e.g. generating n records so a count equals n), the test is aimed too high in the stack — test a narrower function that takes the derived value directly, and cover the derivation separately. Cheap rows are the sign of a well-placed table.

**Ambiguity policy — deliver, don't ask.** Feature descriptions rarely answer every question. Choose the most reasonable interpretation, record it — in the response, a comment, or the scenario ids — and deliver complete tests. Never end the task with clarifying questions in place of tests; documented assumptions in delivered tests are how you raise them. Where two rules interact ambiguously (a cap and a discount, two applicable rates), pick an interpretation, state it explicitly, and add rows that pin the interaction down with concrete expected values.

### Converting Existing Tests

1. Identify tests with identical structure but different data.
2. Extract the varying parts as row fields (inputs and expectations); name each row by its condition.
3. Verify all rows use the same assertion logic — split into separate parameterised tests if logic differs per row.
4. Remove the original one-off tests the rows now cover — run the tests before and after removal to confirm coverage is preserved.

## Quality Checks

After writing, verify:

- [ ] **Named rows**: every case carries a condition-describing name (`id=` in pytest, subtest name in Go, interpolated title in Jest); names state conditions, not outcomes
- [ ] **Straightforward body**: the test body only arranges, acts, and asserts — no `if`/`switch`/`guard`/ternary, no loops over cases, no defaulting or parsing
- [ ] **Rows are paired**: each case binds inputs to their expected outputs in one row — no parallel arrays, no accidental cartesian products (Swift `arguments:` with multiple collections)
- [ ] **Concerns decomposed**: one parameterised test per rule; no monolithic case list mixing unrelated rules; precedence between rules shown by dedicated rows
- [ ] **Minimal rows per concern**: each test has only the cases its rule needs; irrelevant inputs held at a fixed valid value
- [ ] **Thresholds visible**: rules that compare against a limit show the limit in the row, with boundary cases at and just past it
- [ ] **Tiers fully enumerated**: every tier represented; every boundary tested from both sides, including middle tiers
- [ ] **Errors separated**: expected-exception cases in their own test using the framework's throw assertion — no sentinel expectations
- [ ] **Concrete values**: expected values are literal domain values traceable to the inputs — not computed in the row or the body
- [ ] **Domain language**: parameter/field names come from the domain, not generic placeholders
- [ ] **Complete outputs**: all observable outputs of the concern asserted in the same rows
- [ ] **Stateful rows independent**: transition rows carry their own before-state; no row depends on another having run
- [ ] **Assumptions stated**: any interpretation chosen for an ambiguous rule is recorded where the reader will see it
