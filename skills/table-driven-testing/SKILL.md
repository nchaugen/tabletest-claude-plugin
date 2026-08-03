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

Always name cases — `pytest.param(..., id="...")` or an `ids=` argument. Auto-generated ids like `15000-SINGLE-0.1` force the reader to decode values; a written id states the condition — only the condition: `at_the_limit`, never `at_the_limit-surcharge_applied`.

**"Regardless of" inputs**: put them in the *same* case list, varying together. Stacking a second `@pytest.mark.parametrize` multiplies the decorators into a cartesian product, which is four visible cases for one claim — see *Generating "Regardless Of" Combinations*.

**Expected exceptions**: a case list mixing `pytest.raises` cases with return-value cases needs branching in the body — forbidden. Give rejection cases their own parametrized test built around `pytest.raises`. Exception: an accept/reject boundary is one rule and stays in one table — see *Separate Expected-Error Cases*.

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

**Cartesian footgun**: passing two collections — `arguments: inputs, expectations` — produces every combination, not paired rows. Pair with labelled tuples in one collection, a row struct, or `zip` — including for "regardless of" inputs, which vary together in one collection rather than crossed; see *Generating "Regardless Of" Combinations*.

**Expected exceptions**: a separate `@Test` with `#expect(throws:)` — never sentinel values or branching in a parameterised body. Exception: an accept/reject boundary is one rule and stays in one table — see *Separate Expected-Error Cases*.

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

The test title interpolates row values — write it so each generated name reads as a condition. Expected rejections use `expect(() => ...).toThrow(...)` in their own `test.each` block. Exception: an accept/reject boundary is one rule and stays in one table — see *Separate Expected-Error Cases*.

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

The loop over the case slice is the framework mechanic here — the rule against loops applies inside the subtest body. Error-returning cases go in a separate table whose rows expect a specific error, not a mixed table with `wantErr bool` alongside unrelated expected values. A `wantErr error` field on an accept/reject boundary table is the sanctioned exception, and is idiomatic Go — see *Separate Expected-Error Cases*.

### xUnit (C#)

`[Theory]` with `[InlineData]` rows, or `TheoryData<...>` when rows need real types. `[InlineData]` has no per-row name — put the condition in a leading string argument or use `MemberData` with self-describing row objects. Expected exceptions use `Assert.Throws<T>` in their own theory. Exception: an accept/reject boundary is one rule and stays in one table — see *Separate Expected-Error Cases*.

## Table Design

<!-- BEGIN GENERATED table-design — do not edit here; source is shared/table-design/ -->

### One Rule, One Axis

A table is one rule varying along one axis. The axis is what the cases change; everything else is
either held constant or collapsed into a value set. Most decomposition questions are that one
question asked again — *what is this table's axis, and does every column and case serve it?*

**If you cannot name a behaviour without using "and", it is two concerns.** Split them, and give each
its own table.

**The naming test passes on a conjunction, and a conjunction is still several rules.** A rule of the
form *"X holds only if C1 and C2 and C3"*, where the conditions do not mention one another, names
cleanly in one breath — "sorts waste into bins" — while being three independent claims. Give
each condition its own table, holding the others satisfied. Crossing them instead multiplies cases
without adding a claim, and no case then isolates the condition it was meant to show.

**The test is whether the rule can be *stated* about each condition alone — not whether the inputs
are separate.** Several inputs that are each a *contribution to one answer* are one rule, however
separately they arrive: quantities that are weighted and summed, amounts that accumulate into a
total, parts that combine into a whole. There is no claim to make about one of them by itself,
because the answer is the combination. Splitting those gives one table per input, each holding the
others at nothing, and **no table then shows them combining — which is the only interesting case**.
Keep them in one table with a column each, and let some of its cases carry several contributions
at once. Those cases belong to that table, which owns the combining rule; they are not a second
table run end to end — see *A Combining Table Needs Its Own Rule*.

Hold the inputs belonging to *other* concerns at one obviously-valid value. An input **this** rule
claims not to affect the outcome is the opposite situation and has to vary — see *Value Sets for
"Regardless Of" Relationships*.

Separate tables reduce cases by avoiding unnecessary permutations, and the table count guides the
implementation: five concern tables suggest five functions.

```python
# Two concerns, two tests. "Duty eligibility AND rest credit" fails the "and" test.
@pytest.mark.parametrize(("hours_since_rest", "max_duty_hours", "fit_to_fly"), [
    pytest.param(13, 13, True,  id="at the limit"),
    pytest.param(14, 13, False, id="past the limit"),
])
def test_fitness_to_fly(hours_since_rest, max_duty_hours, fit_to_fly):
    assert is_fit_to_fly(hours_since_rest, max_duty_hours) == fit_to_fly
```

### Include All Outputs of a Concern

When an operation produces several observable outputs, include them all as expectation columns in one
table. Each case then gives the complete picture of what happens for that scenario. Splitting the
outputs of one concern forces the reader to cross-reference several tables to understand one
behaviour.

Separate tables are for separate **concerns**, never for separate outputs of the same concern.

**Every expectation column must be exercised by the cases the table varies.** A column that is
constant down every case, or that changes only as a side effect of another column, is not being
tested. Two repairs, and which is right depends on the rule:

- **Give it cases that vary it**, when the column does belong to this table's axis and the cases
  were missing.
- **Move it to the table whose axis varies it**, and drop it here rather than keeping it "for
  completeness".

```swift
@Test("Climate response by humidity and temperature", arguments: [
    (humidity: 80, temp: 28, vent: Vent.open,   heater: false, alert: String?.none),
    (humidity: 80, temp: 8,  vent: Vent.closed, heater: true,  alert: "Condensation risk"),
    (humidity: 55, temp: 21, vent: Vent.closed, heater: false, alert: String?.none),
])
func climateResponse(humidity: Int, temp: Int, vent: Vent, heater: Bool, alert: String?) {
    let response = ClimateController().respond(humidity: humidity, temp: temp)
    #expect(response.vent == vent)
    #expect(response.heaterOn == heater)
    #expect(response.alert == alert)
}
```

### Decompose When You See These Signs

*One Rule, One Axis* gives the first test — a behaviour you cannot name without "and" is two
concerns. These are the signs that show up later, once the table exists:

- Some cases need columns that other cases leave at a placeholder value throughout.
- Scenario ids need qualifiers — "…for eligibility" against "…for pricing".
- The table has two groups of expectation columns that never both apply in the same case.

**Missing concern:** an input to one rule is itself derived from raw data. The derivation has its own
edge cases and needs boundary cases of its own. The rule's table then takes the *derived value* as
a direct input column, not the raw data. Two tables, not one.

**A column at a placeholder value throughout for most of its cases is a column decision before it is a table
decision.** Ask what the sparse columns feed. Several feeding the *same* expectation column are one
family: collapse them into one column keyed by member, and the table stays whole. Feeding
*different* expectation columns, they are different concerns and split into separate tables.

**Do not over-split either.** Several tables that fix the same setup, each vary one sub-rule, and all
report the same expectation column are one concern scattered — one table per adjustment, per option,
per flag. That shape is the symptom; the cause is a family you did not name.

**If you can name what several tables have in common in one term, they are one concern — that term is
the table, and its members are a column.** This is the mirror of the "and" test. Renal impairment,
low body weight and an interacting drug all *adjust the standard dose*: three rules, one family, one
table with an adjustment column. Naming the members instead commits to the split before a single
case exists, which is why this is decided when you name the table.

**Members of a family compute differently, and that is not a reason to split.** One adjustment is a
flat reduction, another a percentage, another a recalculation. The differing computation is what the
cases show; it is not what makes them separate tables.

**Collapsing a family means one table, not necessarily one column.** Where the members arrive as
*separate inputs* the system reads independently, a single column keyed by member cannot feed them —
routing one value to the right input would put a decision in the test itself, which is never the
answer. **Give each member its own column in the one table, and leave it at a placeholder value throughout on the
cases where that member does not apply.** The family is still stated as one rule, the members
still sit side by side, and the sparse columns are what shows which member each case exercises.
Reach for the keyed column when the members are values one input takes; reach for a column each when
they are inputs of their own. **Splitting into a table per member is the wrong answer in both
cases** — and it is the tempting one, because it needs no decision.

**Collapse on a family, never on a bag.** A family is a domain category, not "everything that affects
the answer". The check: the family name works as a column header with the members as its values.
Where no such name exists the tables are genuinely distinct and belong apart — and so they do where
collapsing would cross-multiply, or leave cases whose purpose is no longer legible.

```csharp
public static TheoryData<string, Adjustments, int> DoseCases => new() {
    { "no adjustment",              new(),                                    500 },
    { "renal impairment",           new(Renal: Severe),                       250 },
    { "low body weight",            new(WeightKg: 20),                        200 },
    { "interacting drug",           new(Interaction: true),                   400 },
    { "renal and interacting drug", new(Renal: Severe, Interaction: true),    200 },
};
```

One family, one theory. Three separate theories fixing the same setup would be the over-split.

### A Combining Table Needs Its Own Rule

Once every rule has a table, the pull is to add one more that runs the whole feature end to end. It
re-proves what the single-rule tables already established, and it reads as redundant however clean
those tables are.

**A table that combines concerns earns its place only where the combination behaves in a way neither
concern shows alone** — a precedence, an ordering, an interaction whose result neither parent table
produces — and then it carries only the cases that show it. A table proving that a weight-based
dose is computed *before* the daily maximum caps it is a real table: the question is which rule
applies first, and its expected values appear in no other table. A table whose cases re-run each
dose band through the public entry point is not.

**Collapsing several same-fixture tables into one is not a combining table**, and *Decompose When You
See These Signs* requires it. The difference is what the merged table states: a family table states
one rule with its members as a column, while a combining table re-runs rules other tables have
already established. The first has a rule of its own; the second is a second pass over the ladder.

Two symptoms:

- **The description gives it away.** If the description you would write is "end-to-end scenarios
  combining the rules from the tables above", the table has no rule of its own. Delete it.
- **Wiring is not a rule.** Reaching a rule through the public API rather than the unit under test
  does not make it a new rule. If the wiring genuinely needs showing, that is one case, not a
  second pass over the ladder.

**Salvage its cases before you delete it — and then delete it.** One or two cases of an
end-to-end table often reach a case no single-rule table does. Deleting the table takes those with it
and nothing reports the loss, so list the obligations only its cases discharge and move each into
the table that owns its rule.

**This is a salvage step, not a reprieve — no outcome of it keeps the table.** A case worth
keeping is worth keeping *somewhere else*. Nor does shrinking the table save it: a single test that
runs the whole feature to re-prove one already-proven total is the same combining table with fewer
cases.

```swift
// Earns its place: the cap-versus-weight precedence appears in no other test.
@Test("Daily maximum caps the weight-based dose", arguments: [
    (weightKg: 40,  dailyMax: 400, dose: 200),
    (weightKg: 120, dailyMax: 400, dose: 400),
])
func dailyMaximumCaps(weightKg: Int, dailyMax: Int, dose: Int) {
    #expect(Dosing.daily(weightKg: weightKg, dailyMax: dailyMax) == dose)
}
```

### Separate Rules from Arithmetic

Tables specify the interesting decisions — classifications, eligibility rules, tier lookups, state
transitions — not that multiplication works.

**The symptom is an expectation cell you cannot predict in one step.** If reading a case means
classifying first and then computing, the table has fused two rules and states neither.

Give the classification its own table, whose expectation columns *are* the classification. Give the
arithmetic its own, taking the classification as an input. Each table then states one rule, and every
cell is predictable from its case.

This usually needs a narrower function to call. A table that can only reach the fused result means
the seam is missing, not that the table must fuse.

```javascript
// Test 1 — the classification
test.each`
  dutyHours | normalHours | extendedHours
  ${8}      | ${8}        | ${0}
  ${13}     | ${13}       | ${0}
  ${14}     | ${13}       | ${1}
`('$dutyHours duty hours divide into normal and extended', ({ dutyHours, normalHours, extendedHours }) => {
  expect(splitDutyHours(dutyHours)).toEqual({ normalHours, extendedHours });
});

// Test 2 — the arithmetic, taking the classification as input
test.each`
  normalHours | extendedHours | restCredit
  ${13}       | ${0}          | ${13.0}
  ${13}       | ${1}          | ${15.0}
`('rest credit for $normalHours normal and $extendedHours extended', ({ normalHours, extendedHours, restCredit }) => {
  expect(restCreditFor(normalHours, extendedHours)).toBe(restCredit);
});
```

### Give Each Obligation Exactly One Case

The right number of cases is a covering problem. List the concern's **obligations** — the distinct
behaviours the rule must demonstrate — then write the smallest set of cases that covers all of
them. Both errors are real and they do not read alike: a missing obligation lets a wrong
implementation pass, while a repeated one costs the reader time and suggests a distinction that is
not there.

**The test for a redundant case: if two cases share an expectation, the difference between them
must be the thing the rule is about.** If it is not, they are one case — and a value set is how you
say so.

**"Exactly one" is a floor as well as a ceiling, and consolidating is where the floor gets broken.**
Trimming a table is the moment to re-read the obligation list, because the cases that look most
redundant are often the ones carrying an obligation of their own. Three shapes account for nearly
every obligation dropped that way:

- **A second input in a different *state*, mistaken for a larger value of the same one.** Acting on
  something already populated is not a bigger version of acting on something fresh — it is the case
  where existing content has to survive, and nothing else shows it.
- **The transition that empties or fills.** Removing the last member, filling the final slot: the
  case looks like the ordinary case with smaller numbers, and it is the only one that reaches the
  boundary of the container.
- **A distinct branch that shares its expectation with a neighbour.** Two cases agreeing on the
  answer are not redundant when they reach it by different routes **this table's rule names**. Ask
  which rule names the branch. If the answer is a neighbouring table's, the difference is a value
  *this* rule ignores, and it collapses into a value set — see *Use Value Sets for "Regardless Of"
  Relationships*. Kinds of a thing that another rule tells apart are the usual false positive: three
  cases for three kinds, where the rule under test reads only whether the thing was valid.
  **Collapsing means the value set, not the delete key.** Put every kind in the surviving cell —
  `{percentage, fixed, product-specific}` — because the description will still claim the kind makes
  no difference, and deleting the cases leaves that claim with nothing behind it.

When you cut a case, say which surviving case discharges its obligation. If none does, keep it.

Three shapes account for nearly every redundant case:

- **Further past the same boundary.** A pair that *straddles* a boundary earns both its cases: the
  outcomes differ, and that is the rule. A second case on the same side does not. This holds for
  rejections too — one case just past a limit rejects, and a case further past it rejects for no
  new reason.
- **A larger n in the same direction.** If two incompatible items force a batch into separate streams,
  three incompatible items force it for the same reason. One obligation, one case.
- **A value the rule ignores.** Two cases differing only in it are one case. Merge them with a
  value set: same outcome either way means the difference between the cases is not the rule.

A second case on the same side of a boundary earns its place in one case: when the point *is* that
two inputs collapse to one behaviour. Then say so — a value set says it in one case, and if you
keep two the scenario ids have to carry why.

**One value can carry two obligations, in two different tables.** A value that is a boundary for one
rule is often the subject of another. A zero duty period is both the accepted end of "duty hours
cannot be negative" *and* the input that should produce no rest requirement whatever the crew size —
two rules, two questions, two cases in two tables. Showing the value once, in whichever table you
reached first, feels like coverage and is not. **Count obligations per rule, never per value.**

```go
tests := []struct {
    name              string
    dutyHours         float64
    extraRestRequired bool
}{
    {"at the duty limit", 13, false},
    {"just past the duty limit", 13.5, true},
    // {"well past the duty limit", 20, true},  <- redundant: 13.5 already proved it
}
```

### Cover Every Tier and Both Sides of Every Boundary

When inputs map to tiers — rate bands, size categories, standings — every tier appears in the
cases, and every boundary is exercised from both sides: the last value inside a tier and the first
value of the next.

**Middle-tier boundaries are the ones most often skipped.** Outer edges alone do not pin down where
the middle tiers change.

This is the coverage half of *Give Each Obligation Exactly One Case*, and the two meet at a
boundary: the straddling pair is required here and earns both its cases there. A third case
further past the same boundary is what the other rule removes.

Where a tier is a range rather than a single value, a value set spanning it carries its own
boundaries — a separate "tier begins" case then discharges nothing the "tier holds" case has
not.

```javascript
test.each`
  haemoglobin | band
  ${124}      | ${'DEFER'}
  ${125}      | ${'STANDARD'}
  ${159}      | ${'STANDARD'}
  ${160}      | ${'REVIEW'}
`('haemoglobin $haemoglobin falls in the $band band', ({ haemoglobin, band }) => {
  expect(donationBand(haemoglobin)).toBe(band);
});
```

### Use Value Sets for "Regardless Of" Relationships

When an input exists but does not affect the outcome of a case, say so with data rather than prose:
put every value the rule ignores in the cell.

A blank would wrongly suggest the field is absent. The value set makes the claim explicit — *this rule
holds for all these values* — and one case states it more precisely than two near-identical ones.

**The clearest sign you want one: a column that could carry every one of its values on every
case without changing anything.** That is the rule saying, in data, that it does not read the
column.

**A value set cannot vary an expectation.** It expands the case into one case per value, and every
expanded case keeps the same expectation cells. Where the answer differs per value, those are
ordinary distinct cases.

**Every value in the set must produce the same result.** If the results differ, the input does matter
and belongs as ordinary distinct cases. Never use a value set as shorthand for "test several
values".

**Two different situations, two different treatments.** An input that *another* rule owns is held at
one obviously-valid value. An input that *this* rule claims not to affect has to vary across the
values it ignores — otherwise no case could ever contradict the claim.

**Value sets work on two axes — check both.** *Within* a case, group input values that produce the
same outcome. *Across* cases, collapse duplicates: when two input values produce the same
expectation cells **in this table**, one case carrying both replaces two identical ones. It is
easy to apply one axis and miss the other.

**Judge that per table, not across the whole class.** Two values that this rule treats alike collapse
here even if a neighbouring rule tells them apart — grouping them says *this* rule does not
distinguish them, which is exactly what the neighbouring table then contradicts, on the record. Ask
only whether the expectation cells match in the cases in front of you. A category you have named
as a catch-all is the easy case and gets collapsed almost automatically; **the one that gets missed
is two values you think of as distinct that this particular rule happens to treat the same.**

```python
# One case per ignored value, varying together — not stacked generators, which cross them.
@pytest.mark.parametrize(("haemoglobin", "recent_travel"), [(125, True), (140, False)])
def test_under_age_donor_is_ineligible_regardless(haemoglobin, recent_travel):
    assert not is_eligible(age=16, haemoglobin=haemoglobin, recent_travel=recent_travel)
```

Age alone decides it, and the two inputs it ignores each take both their values, so a case could
contradict the claim.

### Frame Stateful Features as Transition Rules

When a feature involves state — queues, workflows, inventories — frame each case as a state
transition rule: the state before, the action, the state after, and any message or result.

Each case is independent: given this state, when this action happens, expect this result. No case
depends on a previous one having run.

**Include the before and after columns** even when the description states the operation procedurally.

A sequential path — step 1, then step 2, then step 3 — creates case dependencies and is not a table
at all.

```go
tests := []struct {
    name      string
    binBefore Bin
    action    Action
    binAfter  Bin
    message   string
}{
    {"accept a labelled item", Bin{}, Deposit("cardboard"), Bin{"cardboard": 1}, "Accepted"},
    {"fill to the bulk limit", Bin{"cardboard": 1}, Deposit("cardboard"), Bin{"cardboard": 2}, "Accepted"},
    {"reject a mismatched item", Bin{"cardboard": 1}, Deposit("solvent"), Bin{"cardboard": 1}, "Wrong stream"},
}
```

### Assume the Table Is Published

Write every table as if a reader will meet it in a published report, never having seen the code. Only
three surfaces reach that reader, and they divide the work:

| Surface | Carries |
|---|---|
| the test function name, or the display name the framework shows | the rule, as an action the code performs |
| the test's docstring or leading comment | the apparatus that cannot be a column — what is held constant, where the data came from |
| the table | the variations the rule ranges over |

**Whatever the table holds constant is silently promoted into the rule.** Readers generalise from
what varies, so a value that never varies is read as part of the rule: a duty-limit table whose every
case assumes a two-pilot crew states, to its reader, a rule about two-pilot crews.

So a constant the outcome depends on is a **column** wherever it can be one — and a value the rule
turns on, such as a threshold or a limit, always can be. The other two surfaces carry what a column
cannot: where the data came from, what the fixture fixes, an assumption the cases cannot state.

**If the declaration says the value does not matter, declaring it is not enough.** *"Held empty
throughout, and it makes no difference"* is not apparatus — it is a claim about the rule, and a claim
no case can contradict is not stated in the table at all. Vary it instead, across the values it
ignores; see *Value Sets for "Regardless Of" Relationships*. Write a fixture into the
the test's docstring or leading comment only for what the rule genuinely reads and the cases cannot show.

**Making a value a column does not force everything measured from it into the same form.** Once a
reference point is declared — a clock, an origin, a baseline — the columns measured *from* it read
better as offsets against it than as restatements of it. Both are then visible, and the offsets stay
short enough to scan.

**Pick the offset's unit from the finest distinction the rule has to draw.** Where the rule separates
29 days 23 hours from 30 days 1 hour, the column is `Hours Ago` and not `Days Ago` — whole days cannot
state that boundary at all. Read the boundary cases first and choose the unit second; a shorter
cell that cannot state the rule has bought nothing.

**Keep a slot in the cell for every field the the test's docstring or leading comment makes a claim about.** A compact
cell carries the fields the rule reads and drops the rest, and a dropped field is pinned exactly as it
would be in a conversion helper, with nothing on any surface to say so. When the claim is that the
outcome does not turn on that field, dropping it is what makes the claim uncontradictable — put the
field back as a key or a column, or stop making the claim.

It is **not** declared when it sits in the test body, in a field, in a conversion helper, or in a
comment — a comment reaches no published surface at all. The helper is the easiest hiding place
because it looks like plumbing: one that builds every entry with the same zone has pinned zone for
the whole table, and no column says so.

**What the assertion tolerates is part of the rule too.** A comparison that sorts either side before
comparing, accepts a subset, matches "contains" rather than equals, or normalises case or whitespace
is *enforcing a rule*: it changes which behaviours the test would accept, and none of it reaches the
reader. Ordering is the usual one, and a shared helper is where it hides — written once, then
invisible at every call site, so a reader cannot tell whether order is part of the behaviour or an
artefact of the comparison. Two repairs, and the second is better where it fits:

- **Name it** — one sentence in the description, or a column that makes it evident.
- **Remove the need for it** — an unordered collection as the expectation says order does not matter
  *in the table itself*, which beats saying so in prose; an ordered one with a canonical sort says it
  does.

Numeric tolerance is not a criterion: a conventional epsilon on a decimal column is exempt. Nor is
constructing the objects the columns name.

```swift
// Crew size was held at 2 in every case, so the test silently claimed a two-pilot rule.
@Test("Fitness to fly by duty hours and crew size", arguments: [
    (dutyHours: 12, crewSize: 2, maxDuty: 13, fit: true),
    (dutyHours: 14, crewSize: 2, maxDuty: 13, fit: false),
    (dutyHours: 14, crewSize: 3, maxDuty: 17, fit: true),
])
func fitnessToFly(dutyHours: Int, crewSize: Int, maxDuty: Int, fit: Bool) { ... }
```

### Write Titles That Form an Index

The test function name, or the display name the framework shows is the line a reader scans in the report index. **Judge titles as a set, never one
at a time:** a title that reads well on its own page can still be an unscannable entry in the list.

**Open each title with something that distinguishes it, and keep one grammatical shape across the
family.** When every title starts with the same word the index becomes a column of identical openers,
and the distinguishing part arrives last, where scanning cannot reach it. Three titles sharing an
uninformative opener is enough to make the list unscannable.

**Write an action the code performs, not a label for a topic.** This is the half that is easy to
miss: a noun phrase can front the varying subject and still say nothing about what the code *does*
with it. "Deferral interval by donation type" names a topic; "Sets the deferral interval from the
donation type" names behaviour. The label form is the more tempting mistake, because it looks tidy in
a list.

One outlier does not break a family — a negative or invariant claim often reads best subject-first.

**A title states what your system does, not an external fact it depends on.** Strike the system under
test from the sentence: if it still reads as true, the title is restating a regulation, a format or a
domain fact instead of naming behaviour.

**This action voice is the title's alone.** Scenario ids stay condition phrases naming the case's
variation — see *Name Scenarios by Condition, Not Outcome*. A title says what the rule does; a
scenario id says which case this case is. Writing cases as little sentences is how outcome-echoing
scenario ids get in.

```javascript
describe('Sets the deferral interval from donation type', () => { /* ... */ });
describe('Rejects a reading below the haemoglobin minimum', () => { /* ... */ });

// Not an index: identical openers push the distinguishing word out of scanning range.
describe('should apply deferral interval', () => { /* ... */ });
describe('should apply haemoglobin minimum', () => { /* ... */ });
```

### Name Scenarios by Condition, Not Outcome

Good scenario ids answer "under what circumstances?" — not "what happens?". The outcome is already in the
expectation columns; naming it twice adds nothing, and when the expectation changes the id
silently lies.

Appending the outcome to a condition is still naming the outcome. The id only needs to say
*when*; the case's expectation values say *what*.

Naming the rule or the situation is correct even when it makes the outcome inferable. The failure to
avoid is a id echoing its own expectation cell, and a generic label that names no variation
at all.

**A priority or decision table is where this goes wrong most often**, because such a table almost
always publishes the winner as an expectation column. "Configured wins" beside a `Source?` of
`CONFIGURED` restates its own answer; "Both sources set" and "Input dir absent" say which case the
case is.

```python
pytest.param("solvent", "sealed drum", Route.HAZARDOUS, id="solvent in a sealed drum"),   # condition
pytest.param("solvent", "sealed drum", Route.HAZARDOUS, id="goes to hazardous"),          # outcome — no
pytest.param("solvent", "sealed drum", Route.HAZARDOUS, id="sealed_drum-hazardous"),      # both — still no
```

### Name Expectation Columns Clearly

End every expectation column with a `?` **suffix**, so a reader can tell at a glance which columns are
outputs being verified and which are inputs being provided. Input columns never take `?` — including
yes/no columns that describe the state a scenario starts in.

**Prefer the rule's direct output.** `Fee?` beats `Total?`: the fee is what the rule decides, while
verifying the total also requires knowing the base amount. If you do expect a derived value, include
its inputs as columns so a reader can trace it.

**One exception, and it is narrow: identity and status varying together in the same output
position.** Where a column answers *which* one and *how it went* at once — the winner of a pair and
whether it succeeded — the two are one value and the cell names it as one, `Primary OK` against
`Secondary ERROR`. Splitting that into "which?" and "did it succeed?" columns doubles them and makes
the reader join the halves back up. This is a domain value with its own type, not an encoding: it
holds only where **both** parts vary in the same position. Where identity is fixed for the case and
only the status varies, the ordinary column design applies.

**A compound result stays a collection.** When the value under test is several items — or items
grouped under a key — the expectation is a native list, set or map, nested where needed, compared
against what the system returns. Do not flatten it into a string assembled by a formatting helper:
that tests the formatter rather than the rule, hides the structure from the reader, and puts
formatting logic back into the test body. Use a set where order is not part of the rule, and a list
with a canonical sort where it is.

```csharp
[Theory]
[MemberData(nameof(SortingCases))]
public void SortsItemsIntoStreams(string scenario, string[] items, Dictionary<string, string[]> streams)
    => Assert.Equal(streams, Sorter.Sort(items));
```

`streams` stays a real dictionary compared against what the sorter returns — not a joined string.

The `?` marks outputs only — never an input, however yes/no it looks. In a field or parameter name
the same rule applies to the expectation: `repeat_donor` in, `fee_eur` expected — not
`repeat_donor_q`.

### Model Rejection as an Expected Column

When a table covers cases the system rejects, the rejection is an **expectation column** — the error
type, or the reason — never a decision taken in the test body. Each case then states its own
outcome where the reader can see it.

**Whether accepted and rejected cases share a table is decided by what the table is about, and
there is a decidable test for it: remove the rejected cases.** If what remains still states a rule,
the rejection was a separate concern — split it out. If what remains says nothing on its own, the
table is about acceptance and stays whole.

A tier ladder with one rejection case at the end **fails that test**: strike the rejection and the
ladder still states the tiers. It is two concerns, however tempting the last-accepted-beside-
first-rejected pair looks. A validation boundary passes it: strike the rejected case and a single
accepted value is left, which states nothing by itself.

- **The table's whole expectation is whether the call is rejected** — a boundary straddling a
  validation limit, the last accepted value beside the first rejected one. That is *one rule*, and
  splitting it puts the two halves of a single boundary where no reader sees them together. Keep one
  table, leave the rejection column blank where nothing is rejected, and compare the outcome as a
  value.
- **Rejection is one outcome among several** — a parser returning values for good input and rejecting
  malformed input. Those are two concerns and belong in two tables.

**Never branch in the body to choose how to assert.** Picking between a rejection assertion and a
value assertion per case puts the rule back where the table cannot show it, and it is the failure
both shapes above exist to avoid.

```python
# One rule: the whole test asks whether the dose is accepted.
@pytest.mark.parametrize(("dose_mg", "expected_error"), [
    pytest.param(0,     None,       id="at the minimum dose"),
    pytest.param(-0.01, ValueError, id="just below the minimum dose"),
])
def test_rejects_dose_below_minimum(dose_mg, expected_error):
    assert thrown_by(lambda: validate_dose(dose_mg)) == expected_error
```

Go's `wantErr` field is this same shape and is already idiomatic there. Where rejection is one
outcome among several, split instead — two concerns, two tests.

### Use Concrete Domain Values

Cell values are concrete, meaningful domain data — not abstract flags, codes, or placeholders. An
expectation value is traceable to the input values in its own case.

**An expectation naming something that appears nowhere in the case is a value hardcoded in the
test, not a value the table states.** The reader then cannot understand the table without reading the
code, which is the one thing the table exists to prevent.

When a value is derived from an input, include the source column so the derivation is visible.

**Prefer the value the system really produces.** Where a sentinel, enum constant or error string is
part of the observable contract, put that in the cell rather than a tidier test-only label — the
case then states what a reader would actually see. Shorten a value only when it is too long to
scan, and shorten the **value**, never the vocabulary: `acme:search:v2` scans as well as a
placeholder and still says what each part is. Single letters cost more than they save, because the
legend that decodes them lives outside the table.

Write literal values even when they repeat across cases. Extracting them into named constants
forces the reader to look up every number, which is exactly the indirection the cases exist to
remove.

```csharp
[Theory]
[InlineData("standard adult", 70, 5, 350)]
[InlineData("paediatric",     20, 5, 100)]
public void DailyDoseByWeight(string scenario, int bodyWeightKg, int dosePerKgMg, int dailyDoseMg)
    => Assert.Equal(dailyDoseMg, Dosing.DailyDose(bodyWeightKg, dosePerKgMg));
```

`350` is traceable to `70 x 5` in its own row; a `"standard"` expectation would not be.

### Use Domain Terminology

Column names use domain or feature terminology that readers understand without knowing the
implementation. Avoid parameter names, variable names, and internal API terms.

The table should read as a specification a domain expert could review.

```go
// Good                          // Bad
bodyWeightKg int                 w int
renalFunction Function           flag bool
doseBand Band                    result string
```

### Make Thresholds Visible

When a rule depends on a threshold or limit, include it as a column — even when the value is constant
across every case.

Without the threshold column the number is buried in the code: the reader cannot tell from the table
where the boundary is, or whether the rule is strictly greater than. Boundary cases — at the limit,
just over it — become natural to add once the threshold is visible.

**A constant column often signals configuration.** Ask under what circumstances the value would
differ. The answer may reveal a second axis that belongs as new cases or as a separate table.

```go
tests := []struct {
    name          string
    daysSinceLast int
    minInterval   int
    eligible      bool
}{
    {"exactly at the interval", 90, 90, true},
    {"one day short", 89, 90, false},
}
```

### Include Traceability Columns

When a table exercises a pipeline — input, then an intermediate result, then a final result — include
the intermediate as an expectation column. A reader can then trace the logic step by step, and when a
case fails the intermediate column shows where in the pipeline it broke.

The intermediate is usually not strictly necessary: the test could verify only the final value. It
earns its place by making the derivation legible in the case.

**Guard: only for values the system exposes, or that are observable domain concepts.** If populating
the column would mean reimplementing an internal calculation in the test, it does not belong — the
intermediate is pointing at a separate concern that needs its own table. Decompose instead, and the
intermediate becomes an expectation in one table and an input in the next.

```swift
@Test("Daily dose by weight and renal function", arguments: [
    (weightKg: 70, renal: Renal.normal,   band: Band.standard, dose: 500),
    (weightKg: 70, renal: Renal.impaired, band: Band.reduced,  dose: 250),
    (weightKg: 40, renal: Renal.normal,   band: Band.low,      dose: 300),
])
func dailyDose(weightKg: Int, renal: Renal, band: Band, dose: Int) {
    let result = Dosing.resolve(weightKg: weightKg, renal: renal)
    #expect(result.band == band)   // the intermediate, so a failure says which step broke
    #expect(result.dailyDose == dose)
}
```

### Blank Means Absent

Use a blank cell when a value is genuinely absent. Blank means **absent** — not zero, not a default,
and not irrelevant.

Three meanings the notation has to keep apart:

| Meaning | Notation |
|---|---|
| The value is missing | blank cell |
| The value exists but does not affect this case | value set |
| The value is present and empty | the language's own empty literal — `''`, `[]`, `{}` |

**The system under test decides what an absent value means — never the test.** That decision is part
of the behaviour being specified, and the case exists to pin it down. Writing a baseline value into
the cell is a different scenario; converting a blank to a default on the way in deletes the case the
case was written to show.

Do not fill a genuinely blank cell with filler like `N/A` or `none`.

```python
@pytest.mark.parametrize(("humidity", "override_setpoint", "vent"), [
    pytest.param(80, None, Vent.OPEN,   id="no override configured"),
    pytest.param(80, 90,   Vent.CLOSED, id="override supplied"),
])
def test_vent_position(humidity, override_setpoint, vent):
    assert resolve_vent(humidity, override_setpoint) == vent
```

`None` states the absence. A `0` would state a different scenario, and defaulting it inside the test
body would state nothing.

### Design Black-Box Tables

Model observable inputs and outputs. Avoid internal flags and setup-only columns unless they are part
of the public contract.

Anything the test does beyond arranging, acting and asserting is a rule the table cannot show.
Construction belongs in a conversion helper, the expected error in a column, defaulting and
normalisation outside the body entirely. When you find yourself writing logic in the test, ask which
column or helper it should have been.

```javascript
test.each`
  humidity | temp  | vent
  ${80}    | ${28} | ${'OPEN'}
  ${55}    | ${21} | ${'CLOSED'}
`('humidity $humidity at $temp C sets the vent $vent', ({ humidity, temp, vent }) => {
  expect(new ClimateController().respond(humidity, temp).vent).toBe(vent);
});
```

### Keep the Tables of One Concern Consistent

Tables that sit together are read together. Within one parametrized test set, the same concept takes the
same column name, the same kind of value takes the same notation, and the same failure takes the same
spelling. `Response Time?` in one table and `Timing?` in the next reads as two different things; `<50`
beside a bare `50` leaves the reader deciding whether the second is a maximum or an exact value.

**Decide the shared notation before writing the first table, not while writing the third.** The choice
is cheapest at the start and gets more expensive with every table that fixes it differently.

Four things to hold steady across the set:

- **Column names** — one name per concept, everywhere it appears.
- **Value notation** — one way of writing a bound, a duration, an absent value.
- **Failure vocabulary** — one spelling of an error, not `ERROR` here and `FAIL` there.
- **The helpers behind them** — one parser or fixture shared, rather than a copy per table.

Two parametrized tests in one file, one notation and one parser behind them:

```python
@pytest.mark.parametrize(("response_time"), [pytest.param("<50", id="healthy upstream")])
def test_answers_within_the_latency_budget(response_time):
    assert responder.latency() <= parse_latency(response_time)


@pytest.mark.parametrize(("report_time"), [pytest.param("<50", id="healthy upstream")])
def test_publishes_the_report_within_the_latency_budget(report_time):
    assert reporter.latency() <= parse_latency(report_time)
```

A bare `50` in the second test would leave the reader deciding whether it means a maximum or an exact
value, and a second copy of `parse_latency` would let the two drift apart without either failing.

<!-- END GENERATED table-design -->

---

## Expressing These Rules in a Parameterised Framework

The rules above are notation-independent. These are the mechanics they need here.

### Expressing an Absent Input

Represent a genuinely missing input as the language's own absence value — `None`, `nil`, `null` — not
`0`, `""`, or a made-up default. Any defaulting the code under test applies belongs in the
expectations, never pre-applied in the row data.

### Generating "Regardless Of" Combinations

**Vary the ignored inputs together in one case list. Do not stack generators that cross them.**

```python
@pytest.mark.parametrize(("haemoglobin", "recent_travel"), [(125, True), (140, False)])
def test_under_age_donor_is_ineligible_regardless(haemoglobin, recent_travel):
    assert not is_eligible(age=16, haemoglobin=haemoglobin, recent_travel=recent_travel)
```

Each ignored input takes both its values, so a case could still contradict the claim — which is the
whole job. Every case must expect the same result.

A row-based notation writes several value sets on one row and the reader still sees **one** row. A
case list has no such collapse, so the generators that produce a product — stacked `parametrize` in
pytest, a two-collection `arguments:` in Swift Testing, `flatMap` over value lists in Jest, nested
loops in Go, `MemberData` in xUnit — turn one claim into four visible cases, and a third input turns
it into eight. Reach for them only when the rule under test *is* the combination.

### Asserting a Rejection

*Model Rejection as an Expected Column* decides **whether** accepted and rejected cases share a test.
This is how each framework expresses it.

Where rejection is its own concern, use the framework's throw assertion: `pytest.raises`,
`#expect(throws:)`, `.toThrow`, `Assert.Throws`, or an expected-error table in Go. Mixing those cases
into a value-expectation list forces sentinel values (`None`, `-1`) or branching in the body.

Where the whole expectation is *whether the call is rejected*, keep one table and compare the raised
type as a value. Go's `wantErr error` field is already this shape; elsewhere it needs a helper that
returns the thrown type or nothing:

```python
def thrown_by(action):
    try:
        action()
    except Exception as error:
        return type(error)
    return None
```

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

**Table design** — the shared rules above, in checklist form:

<!-- BEGIN GENERATED table-design-checks — do not edit here; source is shared/table-design/ -->

- [ ] **One rule per table**: every case and column serves this table's one axis; a behaviour you cannot name without "and" has been split, and a rule that is a conjunction of independent conditions has one table per condition rather than their cross-product — but inputs that are contributions to one combined answer stay in one table, with cases that show them combining
- [ ] **Complete outputs**: all observable outputs of the same rule sit in one table, and every expectation column there is exercised by the cases that table varies — one constant down all cases, or moving only as a side effect of another, belongs to a different rule's table
- [ ] **Decomposed, not over-split**: no table mixes concerns (blank-throughout columns, qualified scenario ids, two groups of expectation columns), and no set of same-fixture tables reports one expectation column that a family column would collapse
- [ ] **Combining tables prove an interaction**: any table exercising several rules together shows behaviour the single-rule tables cannot (a precedence, an ordering), not the earlier rules re-run end to end
- [ ] **Rules separated from arithmetic**: every expectation cell is predictable from its case in one step; a classification and the calculation that follows it are two tables
- [ ] **One case per obligation**: every obligation of the concern is discharged by some case, and every case discharges one no other case in that table reaches; where two cases share an expectation, what differs between them is what the rule is about — not a value further past the same boundary, a larger n in the same direction, or an input the rule ignores
- [ ] **Every tier once**: a tier ladder has one case per tier — all of them, none twice — and every boundary is exercised from both sides, middle tiers included
- [ ] **Value set semantics**: value sets appear only where every value produces the same result, never as shorthand for "test several values"; an input this rule claims not to affect the outcome varies across the values it ignores, while an input another rule owns is held at one valid value
- [ ] **Stateful cases independent**: transition cases carry their own before-state and after-state; no case depends on another having run
- [ ] **Held constants declared**: every value the outcome depends on that the table fixes for all cases is a column where it can be one — always so for a threshold or limit the rule turns on — and otherwise named in the title or description as held fixed; never left only in the test body, a field, a conversion helper, or a comment
- [ ] **Titles form an index**: read the titles as a sorted list — each states an action the code performs (not a label for a topic), one grammatical shape runs across them, and no three share an uninformative opener
- [ ] **No scenario id restates its own case's answer**: read each scenario id beside the expectation cells of that case — none states or paraphrases one of them, and none is a generic label
- [ ] **Expectation columns marked**: at least one column uses the `?` suffix (never a prefix), no input column does, and a compound result stays a native collection rather than a flattened string
- [ ] **Rejection expressed as data**: rejected cases carry the error type or reason in an expectation column, never a hardcoded outcome in the body; accepted and rejected cases share a table only where striking the rejected cases would leave a table stating nothing, and no case branches the assertion
- [ ] **Concrete values**: expectation values are literal domain values traceable to the input columns of their own case — not abstract codes, and not hidden behind named constants
- [ ] **Domain language**: column names use the business vocabulary, not parameter names, field names or internal API terms
- [ ] **Thresholds visible**: a rule that depends on a threshold or limit shows it as a column, with boundary cases at and just past it
- [ ] **Traceability columns**: an intermediate expectation appears only where the value is observable from the public API — never reimplemented from internal logic; if a formula would have to be reimplemented to fill it, decompose instead
- [ ] **Blank means absent**: a column whose input is genuinely absent for a case uses a blank cell, not 0 or a default; an input that is present but irrelevant is a value set instead, and nothing converts a blank to a default on the way in
- [ ] **Black-box design**: columns represent observable inputs and outputs, not internal flags or implementation details
- [ ] **Consistent across tables**: within one parametrized test set, one concept has one column name, one kind of value has one notation, one failure has one spelling, and the cases are served by shared helpers rather than per-table copies

<!-- END GENERATED table-design-checks -->

**This skill's own checks** — what a parameterised test needs beyond a well-designed table:

- [ ] **Straightforward body**: the test body only arranges, acts, and asserts — no `if`/`switch`/`guard`/ternary, no loops over cases, no defaulting or parsing
- [ ] **Rows are paired**: each case binds inputs to their expected outputs in one row — no parallel arrays, no accidental cartesian products (Swift `arguments:` with multiple collections)
- [ ] **Errors separated**: expected-exception cases in their own test using the framework's throw assertion — no sentinel expectations. The one exception: an accept/reject boundary keeps both sides in one table with an expected-error column, blank where nothing is thrown
- [ ] **Named rows carry the framework's own mechanism**: `id=` in pytest, subtest name in Go, interpolated title in Jest, leading string argument or `MemberData` in xUnit
- [ ] **Assumptions stated**: any interpretation chosen for an ambiguous rule is recorded where the reader will see it
