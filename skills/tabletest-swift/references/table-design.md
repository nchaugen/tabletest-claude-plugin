# Table Design for TableTest in Swift

Read this file in full before you write a new `@TableTest` function, convert a test, or add or
remove a column. `SKILL.md` holds the syntax, the cell conversions and the checks.

## Table Design

The rules below apply to any table notation. The examples show them in `@TableTest` for Swift.

<!-- BEGIN GENERATED table-design — do not edit here; source is shared/table-design/ -->

### One Rule, One Axis

A table is one rule varying along one axis. The axis is what the rows change; everything else is
either held constant or collapsed into a value set. Most decomposition questions are that one
question asked again — *what is this table's axis, and does every column and row serve it?*

**If you cannot name a behaviour without using "and", it is two concerns.** Split them, and give each
its own table.

**The naming test passes on a conjunction, and a conjunction is still several rules.** A rule of the
form *"X holds only if C1 and C2 and C3"*, where the conditions do not mention one another, names
cleanly in one breath — "sorts waste into bins" — while being three independent claims. Give
each condition its own table, holding the others satisfied. Crossing them instead multiplies rows
without adding a claim, and no row then isolates the condition it was meant to show.

**The test is whether the rule can be *stated* about each condition alone — not whether the inputs
are separate.** Several inputs that are each a *contribution to one answer* are one rule, however
separately they arrive: quantities that are weighted and summed, amounts that accumulate into a
total, parts that combine into a whole. There is no claim to make about one of them by itself,
because the answer is the combination. Splitting those gives one table per input, each holding the
others at nothing, and **no table then shows them combining — which is the only interesting case**.
Keep them in one table with a column each, and let some of its rows carry several contributions
at once. Those rows belong to that table, which owns the combining rule; they are not a second
table run end to end — see *A Combining Table Needs Its Own Rule*.

Hold the inputs belonging to *other* concerns at one obviously-valid value. An input **this** rule
claims not to affect the outcome is the opposite situation and has to vary — see *Show That an
Input Does Not Change the Outcome*.

Separate tables reduce rows by avoiding unnecessary permutations, and the table count guides the
implementation: five concern tables suggest five functions.

```swift
// Two concerns, two tables. "Duty eligibility AND rest credit" fails the "and" test.
@TableTest("""
    Scenario       | Hours Since Rest | Max Duty Hours (Policy) | Fit To Fly?
    At the limit   | 13               | 13                      | true
    Past the limit | 14               | 13                      | false
    """)
func decidesFitnessToFly(hoursSinceRest: Int, maxDutyHours: Int, fitToFly: Bool) { ... }
```

### Include All Outputs of a Concern

When an operation produces several observable outputs, include them all as expectation columns in one
table. Each row then gives the complete picture of what happens for that scenario. Splitting the
outputs of one concern forces the reader to cross-reference several tables to understand one
behaviour.

Separate tables are for separate **concerns**, never for separate outputs of the same concern.

**Every expectation column must be exercised by the rows the table varies.** A column that is
constant down every row, or that changes only as a side effect of another column, is not being
tested. Two repairs, and which is right depends on the rule:

- **Give it rows that vary it**, when the column does belong to this table's axis and the rows
  were missing.
- **Move it to the table whose axis varies it**, and drop it here rather than keeping it "for
  completeness".

```swift
// Good — every output of one climate decision in one table
@TableTest("""
    Scenario            | Humidity % | Temp (C) | Vent Position? | Heater? | Alert?
    Warm and damp       | 80         | 28       | open           | false   |
    Cold and damp       | 80         | 8        | closed         | true    | Condensation risk
    Within target range | 55         | 21       | closed         | false   |
    """)
func resolvesClimateResponse(humidity: Int, temp: Int, vent: VentPosition, heater: Bool, alert: String?) { ... }
```

### Decompose When You See These Signs

*One Rule, One Axis* gives the first test — a behaviour you cannot name without "and" is two
concerns. These are the signs that show up later, once the table exists:

- Some rows need columns that other rows leave blank throughout.
- Scenario names need qualifiers — "…for eligibility" against "…for pricing".
- The table has two groups of expectation columns that never both apply in the same row.

**Missing concern:** an input to one rule is itself derived from raw data. The derivation has its own
edge cases and needs boundary rows of its own. The rule's table then takes the *derived value* as
a direct input column, not the raw data. Two tables, not one.

**Ask of every input column where its value comes from.** Either it arrives from outside, or a rule
computes it — and a rule that computes it is a table you have not written yet. Two shapes say you
skipped it, and they look nothing alike: **the raw data is a column and the derived value is nowhere**,
so the derivation happens inside the rows where no row can put a boundary on it; or **both are
columns of the same table**, so the derived one restates a value already present and the rule
connecting them is legible only by reading the rows against each other. Split either way — the
deriving rule takes the raw data and reports the value, and this table takes that value as an input
column and never sees the raw data. **That the value must be visible is not the question**; which
table it is a column *of* is.

**A column blank throughout for most of its rows is a column decision before it is a table
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
row exists, which is why this is decided when you name the table.

**Members of a family compute differently, and that is not a reason to split.** One adjustment is a
flat reduction, another a percentage, another a recalculation. The differing computation is what the
rows show; it is not what makes them separate tables.

**Collapsing a family means one table, not necessarily one column.** Where the members arrive as
*separate inputs* the system reads independently, a single column keyed by member cannot feed them —
routing one value to the right input would put a decision in the test itself, which is never the
answer. **Give each member its own column in the one table, and leave it blank throughout on the
rows where that member does not apply.** The family is still stated as one rule, the members
still sit side by side, and the sparse columns are what shows which member each row exercises.
Reach for the keyed column when the members are values one input takes; reach for a column each when
they are inputs of their own. **Splitting into a table per member is the wrong answer in both
cases** — and it is the tempting one, because it needs no decision.

**Collapse on a family, never on a bag.** A family is a domain category, not "everything that affects
the answer". The check: the family name works as a column header with the members as its values.
Where no such name exists the tables are genuinely distinct and belong apart — and so they do where
collapsing would cross-multiply, or leave rows whose purpose is no longer legible.

One family, one table with an adjustment column — not three tables each fixing the same setup:
```
Scenario                   | Adjustments                        | Daily Dose?
No adjustment              | [:]                                | 500
Renal impairment           | [renal: severe]                    | 250
Low body weight            | [weightKg: 20]                     | 200
Interacting drug           | [interaction: true]                | 400
Renal and interacting drug | [renal: severe, interaction: true] | 200
```

A map that names only the fields it sets converts with `readingFields` (*Structs* in `SKILL.md`).

### A Combining Table Needs Its Own Rule

Once every rule has a table, the pull is to add one more that runs the whole feature end to end. It
re-proves what the single-rule tables already established, and it reads as redundant however clean
those tables are.

**A table that combines concerns earns its place only where the combination behaves in a way neither
concern shows alone** — a precedence, an ordering, an interaction whose result neither parent table
produces — and then it carries only the rows that show it. A table proving that a weight-based
dose is computed *before* the daily maximum caps it is a real table: the question is which rule
applies first, and its expected values appear in no other table. A table whose rows re-run each
dose band through the public entry point is not.

**Collapsing several same-fixture tables into one is not a combining table**, and *Decompose When You
See These Signs* requires it. The difference is what the merged table states: a family table states
one rule with its members as a column, while a combining table re-runs rules other tables have
already established. The first has a rule of its own; the second is a second pass over the ladder.

Two symptoms:

- **The description gives it away.** If the description you would write is "end-to-end scenarios
  combining the rules from the tables above", the table has no rule of its own. Delete it.
- **Wiring is not a rule.** Reaching a rule through the public API rather than the unit under test
  does not make it a new rule. If the wiring genuinely needs showing, that is one row, not a
  second pass over the ladder.

**Salvage its rows before you delete it — and then delete it.** One or two rows of an
end-to-end table often reach a case no single-rule table does. Deleting the table takes those with it
and nothing reports the loss, so list the obligations only its rows discharge and move each into
the table that owns its rule.

**This is a salvage step, not a reprieve — no outcome of it keeps the table.** A row worth
keeping is worth keeping *somewhere else*. Nor does shrinking the table save it: a single test that
runs the whole feature to re-prove one already-proven total is the same combining table with fewer
rows.

Earns its place — the answer appears in no other table:
```
Scenario                        | Body Weight (kg) | Daily Max (mg) | Daily Dose?
Weight-based below the cap      | 40               | 400            | 200
Weight-based above the cap      | 120              | 400            | 400
```

Does not — re-runs each band through the front door:
```
Scenario                | Body Weight (kg) | Daily Dose?
Low weight end to end   | 20               | 100
Adult end to end        | 70               | 350
```

### Separate Rules from Arithmetic

Tables specify the interesting decisions — classifications, eligibility rules, tier lookups, state
transitions — not that multiplication works.

**The symptom is an expectation cell you cannot predict in one step.** If reading a row means
classifying first and then computing, the table has fused two rules and states neither.

Give the classification its own table, whose expectation columns *are* the classification. Give the
arithmetic its own, taking the classification as an input. Each table then states one rule, and every
cell is predictable from its row.

This usually needs a narrower function to call. A table that can only reach the fused result means
the seam is missing, not that the table must fuse.

**Putting the classification in a column of the fused table satisfies this test without splitting
anything.** With the classified value beside the raw data, every cell is predictable in one step
again — and the rule that produces it has still not been stated anywhere. One-step predictability is
necessary, not sufficient; *Decompose When You See These Signs* asks the second question.

**Where you may not add the seam, name it.** Code you cannot change still has the boundary in its
behaviour, and a table that fuses two rules without saying why reads as a design choice. One sentence
on a published surface fixes that — *"the intermediate score is not observable, so the decision and
the amount are verified together; an accessor for it would allow two tables."* Whether the gap gets
closed in the code or bridged here is then the reader's decision to make, which it cannot be while
the gap is invisible.

Table 1 — the classification (how do these duty hours divide?):
```
Scenario           | Duty Hours | Max Duty Hours (Policy) | Normal Hours? | Extended Hours?
Below the limit    | 8          | 13                      | 8             | 0
At the limit       | 13         | 13                      | 13            | 0
Past the limit     | 14         | 13                      | 13            | 1
```

Table 2 — the arithmetic (extended hours earn rest credit at double rate):
```
Scenario            | Normal Hours | Extended Hours | Rest Credit?
Ordinary duty       | 13           | 0              | 13.0
Duty ran long       | 13           | 1              | 15.0
```

### Give Each Obligation Exactly One Row

**The obligation list comes from the inputs, and producing it is the step most often skipped.**
Before counting rows, take each input the rule reads and ask three questions of it. Where an
answer is not obvious from the requirement, it names rows nothing else will.

- **Counted, or read in bands?** A rule reading an input in bands gives two different values the same
  answer, and the only way to state that is two rows differing in that input and agreeing in the
  expectation. A table whose every value of it carries a different expectation has stated "the answer
  rises with this input", which is a different rule from the one you meant.
- **Per unit of it, or once for having any?** Two rows — none and one — show a difference and are
  equally consistent with both readings, because a flat charge for having any at all is an ordinary
  rule. **A third consecutive value is what decides between them.** Without it the table leaves
  ambiguous the very rule it was written to state.
- **Does its effect depend on another input?** Where it does, the pair showing that effect has to
  appear on **both sides** of the other input's boundary. One pair, however well chosen, states an
  effect that is wrong wherever the other input differs.

Answer all three before writing rows. The questions cost nothing, and the rows they produce
are exactly the ones a reader cannot infer from the others.

The right number of rows is a covering problem. List the concern's **obligations** — the distinct
behaviours the rule must demonstrate — then write the smallest set of rows that covers all of
them. Both errors are real and they do not read alike: a missing obligation lets a wrong
implementation pass, while a repeated one costs the reader time and suggests a distinction that is
not there.

**The test for a redundant row, and it is decidable inside the table in front of you: where two
rows share an expectation, ask whether swapping one's differing input for the other's would
change an expectation cell *in this table*.** If it would not, they are one row — and a value set
is how you say so.

**"Exactly one" is a floor as well as a ceiling, and consolidating is where the floor gets broken.**
Trimming a table is the moment to re-read the obligation list, because the rows that look most
redundant are often the ones carrying an obligation of their own. Three shapes account for nearly
every obligation dropped that way:

- **A second input in a different *state*, mistaken for a larger value of the same one.** Acting on
  something already populated is not a bigger version of acting on something fresh — it is the case
  where existing content has to survive, and nothing else shows it.
- **The transition that empties or fills.** Removing the last member, filling the final slot: the
  row looks like the ordinary case with smaller numbers, and it is the only one that reaches the
  boundary of the container.
- **A distinct branch that shares its expectation with a neighbour.** Two rows agreeing on the
  answer are not redundant when they reach it by different routes — but **the routes have to differ
  in what this table expects, not in what its rule mentions**, which is what the test above decides.
  **A value the rule names is not thereby a branch:** enumerating the members is how a rule gets
  stated, and the table's job is to show which of them the answer turns on. Kinds of a thing that
  another rule tells apart are the usual false positive: three rows for three kinds, where the
  rule under test reads only whether the thing was valid.
  **Collapsing means the value set, not the delete key.** Put every kind in the surviving cell —
  `{percentage, fixed, product-specific}` — because the description will still claim the kind makes
  no difference, and deleting the rows leaves that claim with nothing behind it.

When you cut a row, say which surviving row discharges its obligation. If none does, keep it —
but **a value set discharges every obligation its members carried**, because it expands into one case
per value. Collapsing rows into a value set is not cutting them, and the floor is not in play.

**Two closed sets of inputs are where the floor gets misread.** With m values of one input and n of
another, every one of the m×n combinations is a case the rule names, so every one looks like an
obligation of its own and the rows grow to the full cross-product. The obligations are the
distinct *answers*, not the combinations: group the combinations that share an expectation, give
each group one row, and let the value sets carry the members. This is a row count, not a
table count — one rule still means one table, however its inputs multiply.

And three shapes account for nearly every genuinely redundant row:

- **Further past the same boundary.** A pair that *straddles* a boundary earns both its rows: the
  outcomes differ, and that is the rule. A second row on the same side does not, and the same
  holds for rejections — one row just past a limit rejects, and a row further past it rejects
  for no new reason. It earns its place only where the point *is* that two inputs collapse to one
  behaviour, and then a value set says that in one row; keep two and the scenario names have to carry
  why.
- **A larger n in the same direction.** If two incompatible items force a batch into separate streams,
  three incompatible items force it for the same reason. One obligation, one row.
- **A value the rule ignores.** The redundancy test above, applied directly: one row carrying a
  value set. **Where the value that differs sits inside a composite cell, the collapse needs the
  column reshaped first** — a value set varies a whole cell, never one part of one. Ask what else in
  that cell this table reads. If nothing does, the object does not belong in the cell and the value
  does: give it a column of its own and fix the object's other parts outside the table — bar any
  part a surface makes a claim about, which has to stay visible (*Assume the Table Is Published*).
  If other parts are read too, add a further element instead, so one row carries every state and
  the near-duplicate pair never arises.

**One value can carry two obligations, in two different tables.** A value that is a boundary for one
rule is often the subject of another. A zero duty period is both the accepted end of "duty hours
cannot be negative" *and* the input that should produce no rest requirement whatever the crew size —
two rules, two questions, two rows in two tables. Showing the value once, in whichever table you
reached first, feels like coverage and is not. **Count obligations per rule, never per value.**

```
Scenario                  | Duty Hours | Extra Rest Required?
At the duty limit         | 13         | false
Just past the duty limit  | 13.5       | true
// Well past the duty limit | 20       | true    <- redundant: 13.5 already proved it
```

Keep the straddling pair; drop the row further out.

### Cover Every Tier and Both Sides of Every Boundary

When inputs map to tiers — rate bands, size categories, standings — every tier appears in the
rows, and every boundary is exercised from both sides: the last value inside a tier and the first
value of the next.

**Middle-tier boundaries are the ones most often skipped.** Outer edges alone do not pin down where
the middle tiers change.

**Pick the pair's unit from the finest distinction the rule draws, before writing either value.**
Where the rule separates 29 days 23 hours from 30 days 1 hour, whole-day rows of 30 and 31
straddle nothing — the column is `Hours Ago` and not `Days Ago`. A boundary drawn in a unit coarser
than the rule is not drawn at all, however many rows surround it.

**And express it as an offset from the reference point, not as an absolute value restated in every
row.** `Hours Ago` is the whole example: it fixes the unit *and* keeps the row readable, where
absolute instants pin the same boundary while making the reader subtract before the rule is visible.
*Assume the Table Is Published* sends the reference point itself to a column; this rule owns the unit,
and one choice satisfies both. A boundary win bought with an unreadable cell has been paid for twice.

**A formula behind the tiers does not reduce the tiers.** If you find yourself arguing that two tiers
and the delta between them determine the rest, that is the formula talking: the table pins the tiers
the rule names, and identifying the formula is the implementation's job. Nine tiers stay nine
rows.

**And it does not reduce the boundaries.** Where the tier is decided by a formula over several
inputs, every input still has a value at which the outcome flips, and that pair is what the rows
have to straddle — one just below it, one just above, the other inputs held. Sampling that input at
two comfortable values instead exercises the arithmetic and leaves the boundary untested.

**A ladder repeated across several classes states its boundaries once.** Where one ladder is priced
or graded differently per class — the same usage bands on every tariff, the same age brackets in
every region — exercise both sides of every boundary in **one** class, and give each other class one
row per tier:

```
Standard, at band 1 limit | standard | 100    | 12.00
Standard, band 2 begins   | standard | 100.01 | 18.00
Standard, at band 2 limit | standard | 500    | 18.00
Standard, band 3 begins   | standard | 500.01 | 30.00
Economy, band 1           | economy  | 50     |  9.00
Economy, band 2           | economy  | 300    | 13.50
Economy, band 3           | economy  | 800    | 22.50
```

**The check is whether the boundary positions differ between classes.** If they differ, each class
has its own ladder and owes its own straddling pairs. If they do not, the positions belong to the
ladder and the classes differ only in their values — which is what their one-row-per-tier lines
state.

**Duplicated implementation is not a reason to repeat the pairs.** That the same comparison is
written out once per class is a property of the code, and *Design Black-Box Tables* is where that
argument stops: the table states the rule the specification gives, and a specification with shared
bands declares one ladder. Without this, the obligation above reads as *boundaries × classes* and
the row count multiplies with nothing added.

Where a tier is a range rather than a single value, a value set spanning it carries its own
boundaries — **provided its first and last members are the tier's own first and last values.** The
straddling pair is then already written: the last member of one row's set and the first member of
the next row's. **State the tier's edges, not two comfortable values inside it** — a set of middle
values straddles nothing and the explicit pair is still owed. Done that way a separate "tier begins"
row discharges nothing the "tier holds" row has not, and **one row per tier covers the
whole ladder and every boundary in it**. That is economy inside a row and buys no licence to drop
rows: shortening each tier to one cell makes the ladder look repetitive long before it is
complete.

This is the coverage half of *Give Each Obligation Exactly One Row*, and the two meet at a
boundary: the straddling pair is required here and earns both its rows there. A third row
further past the same boundary is what the other rule removes.

```
Scenario                       | Haemoglobin | Donation Band?
Below the minimum              | 124         | deferred
At the minimum                 | 125         | standard
Top of the standard band       | 159         | standard
First value of the high band   | 160         | review
```

Both middle boundaries appear from both sides; outer edges alone would not locate them.

### Show That an Input Does Not Change the Outcome

**You have just concluded that some input does not affect this rule. That conclusion is a rule too,
and it needs a row that could contradict it**: put every value the rule ignores in the cell, as
a value set.

**Dropping the input states nothing.** Leaving the column out reads exactly like having forgotten it
— nothing on the page tells a reader which happened, and no row can contradict a claim the table
never makes. A blank is no better and wrongly suggests the field is absent. The value set makes the
claim explicit — *this rule holds for all these values* — and one row states it more precisely
than two near-identical ones.

**The clearest sign you want one: a column that could carry every one of its values on every
row without changing anything.** That is the rule saying, in data, that it does not read the
column.

**If the operation does not take the input, fix that before the table.** A rule cannot say it ignores
what never reaches it, and leaving the parameter out publishes nothing: the reader sees an operation
that was never offered the value, which is silence rather than a claim. **A column the code never
receives will not do instead** — that route is open to a threshold, which only has to be readable
(*Make Thresholds Visible*), and closed to this rule, whose claim has to be exercised to be
contradictable. Decide it by asking who supplies the value: **if a caller hands it over with the
request it is an input, so keep it and let the table vary it**, even where the code will not read it.

**If the ignored value sits inside a composite cell, the value set is the wrong tool — and it is not
the only one.** `{a, b}` written inside a cell adds no rows: expansion varies a whole cell, never
one part of one. Two routes remain, and the cheap one is easy to miss. **Where the cell holds a list,
vary the ignored value across its elements** — one row, three entries, three different values,
nothing reshaped. Where it holds a single object, give the value its own column if this table reads
nothing else in that cell (*Give Each Obligation Exactly One Row*).

**Fixing the value in the converter is the move to catch yourself making.** It is the cheapest thing
to write and it drops the claim altogether: what reaches the reader is one hard-coded value they
cannot tell from an oversight, and a sentence about a rule that no row exercises. The reshape is
worth it because the claim is; if the claim is not worth a column, it was not worth the sentence
either.

**A value set cannot vary an expectation.** It expands the row into one case per value, and every
expanded case keeps the same expectation cells. Where the answer differs per value, those are
ordinary distinct rows.

**Every value in the set must produce the same result.** If the results differ, the input does matter
and belongs as ordinary distinct rows. Never use a value set as shorthand for "test several
values".

**Two different situations, two different treatments.** An input that *another* rule owns is held at
one obviously-valid value. An input that *this* rule claims not to affect has to vary across the
values it ignores — otherwise no row could ever contradict the claim.

**Value sets work on two axes — check both.** *Within* a row, group input values that produce the
same outcome. *Across* rows, collapse duplicates: when two input values produce the same
expectation cells **in this table**, one row carrying both replaces two identical ones. It is
easy to apply one axis and miss the other.

**Judge that per table, not across the whole class.** Two values that this rule treats alike collapse
here even if a neighbouring rule tells them apart — grouping them says *this* rule does not
distinguish them, which is exactly what the neighbouring table then contradicts, on the record. Ask
only whether the expectation cells match in the rows in front of you. A category you have named
as a catch-all is the easy case and gets collapsed almost automatically; **the one that gets missed
is two values you think of as distinct that this particular rule happens to treat the same.**

```swift
@TableTest("""
    Scenario                           | Donor Age | Haemoglobin | Recent Travel | Eligible?
    Below the minimum age              | 16        | {125, 140}  | {true, false} | false
    Eligible adult donor               | 35        | 140         | false         | true
    Travelled recently, otherwise fine | 35        | 140         | true          | false
    """)
func decidesDonorEligibility(age: Int, haemoglobin: Int, recentTravel: Bool, eligible: Bool) { ... }
```

The first row claims age alone decides it, and varies the two inputs it ignores so a row could
contradict the claim.

### Frame Stateful Features as Transition Rules

When a feature involves state — queues, workflows, inventories — frame each row as a state
transition rule: the state before, the action, the state after, and any message or result.

Each row is independent: given this state, when this action happens, expect this result. No row
depends on a previous one having run.

**Include the before and after columns** even when the description states the operation procedurally.

A sequential path — step 1, then step 2, then step 3 — creates row dependencies and is not a table
at all.

```
Scenario                  | Bin Before     | Action            | Bin After?     | Message?
Labelled item, empty bin  | [:]            | deposit cardboard | [cardboard: 1] | Accepted
Fill to the bulk limit    | [cardboard: 1] | deposit cardboard | [cardboard: 2] | Accepted
Mismatched item           | [cardboard: 1] | deposit solvent   | [cardboard: 1] | Wrong stream
```

`deposit cardboard` is a text form that you invent: a `TableTextDecodable` conformance (*Your Own
Text Form* in `SKILL.md`), or a test-local enum when the action builds the fixture.

### Assume the Table Is Published

Write every table as if a reader will meet it in a published report, never having seen the code. Only
three surfaces reach that reader, and they divide the work:

| Surface | Carries |
|---|---|
| the `@TableTest` title, or the function name when there is none | the rule, as an action the code performs |
| the doc comment above the function | the apparatus that cannot be a column — what is held constant, where the data came from |
| the table | the variations the rule ranges over |

**Whatever the table holds constant is silently promoted into the rule.** Readers generalise from
what varies, so a value that never varies is read as part of the rule: a duty-limit table whose every
row assumes a two-pilot crew states, to its reader, a rule about two-pilot crews.

So a constant the outcome depends on is a **column** wherever it can be one — and a value the rule
turns on, such as a threshold or a limit, always can be. The other two surfaces carry what a column
cannot: where the data came from, what the fixture fixes, an assumption the rows cannot state.

**Once a value is a column, it is declared — check the table before writing about it or adding to
it.** A sentence in the doc comment above the function naming a value the rows already show tells the reader
nothing they cannot read off the table, and a column that does not vary is declared just as well as
one that does. Being constant is not on its own a reason to add a row varying it: where a constant
column hides a second axis, *Make Thresholds Visible* owns that question — and when the rows are
given to you, by a conversion or a supplied set of examples, adding one *to vary the constant* changes
the question you were asked. Boundary and tier rows are a separate obligation and are never what
this paragraph is about.

**Which table it is a column of is a separate question, and this rule does not answer it.** Where
another rule derives the value, it is an input column here and an expectation column there — see
*Decompose When You See These Signs*, which owns that split. Making a value visible is never a reason
to absorb the rule that produces it.

**If the declaration says the value does not matter, declaring it is not enough.** *"Held empty
throughout, and it makes no difference"* is not apparatus — it is a claim about the rule, and a claim
no row can contradict is not stated in the table at all. Vary it instead, across the values it
ignores; see *Show That an Input Does Not Change the Outcome*. Write a fixture into the
the doc comment above the function only for what the rule genuinely reads and the rows cannot show.

**Making a value a column does not force everything measured from it into the same form.** Once a
reference point is declared — a clock, an origin, a baseline — the columns measured *from* it read
better as offsets against it than as restatements of it. Both are then visible, and the offsets stay
short enough to scan.

**Choose that unit before shortening anything, and choose it from the boundary rather than from the
offset.** A shorter cell that cannot state the rule has bought nothing — see *Cover Every Tier and
Both Sides of Every Boundary*, which owns the choice.

**Keep a slot in the cell for every field the doc comment above the function makes a claim about.** A compact
cell carries the fields the rule reads and drops the rest, and a dropped field is pinned exactly as it
would be in a conversion helper, with nothing on any surface to say so. When the claim is that the
outcome does not turn on that field, dropping it is what makes the claim uncontradictable — put the
field back as a key or a column, or stop making the claim.

**A field no surface says anything about is the opposite case, and leaving it out is what keeps the
cell readable.** An object with twelve properties whose rule reads two belongs in the table as those
two; a fixture supplies valid values for the rest. The rule above is the whole limit on that — what
must be visible is what something claims about, not everything the object happens to hold.

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

Crew size never varies, so a reader takes the rule to be about two-pilot crews. Make it a column:
```
Scenario                 | Duty Hours | Crew Size | Max Duty Hours (Policy) | Fit To Fly?
Two-pilot crew, inside   | 12         | 2         | 13                      | true
Two-pilot crew, past     | 14         | 2         | 13                      | false
Augmented crew, past 13  | 14         | 3         | 17                      | true
```

### Write Titles That Form an Index

The `@TableTest` title, or the function name when there is none is the line a reader scans in the report index. **Judge titles as a set, never one
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

**This action voice is the title's alone.** Scenario names stay condition phrases naming the row's
variation — see *Name Scenarios by Condition, Not Outcome*. A title says what the rule does; a
scenario name says which case this row is. Writing rows as little sentences is how outcome-echoing
scenario names get in.

| Scans as an index                                    | Does not                           |
|------------------------------------------------------|------------------------------------|
| `Sets the deferral interval from donation type`      | `shouldApplyDeferralInterval`      |
| `Rejects a reading below the haemoglobin minimum`    | `Haemoglobin minimum by donor sex` |
| `Defers a donor returning from a listed destination` | `shouldDeferForTravelDestination`  |

Three distinct verbs, each carrying information, and the subject arrives immediately after.

### Name Scenarios by Condition, Not Outcome

Good scenario names answer "under what circumstances?" — not "what happens?". The outcome is already in the
expectation columns; naming it twice adds nothing, and when the expectation changes the name
silently lies.

Appending the outcome to a condition is still naming the outcome. The name only needs to say
*when*; the row's expectation values say *what*.

Naming the rule or the situation is correct even when it makes the outcome inferable. The failure to
avoid is a name echoing its own expectation cell, and a generic label that names no variation
at all.

**A priority or decision table is where this goes wrong most often**, because such a table almost
always publishes the winner as an expectation column. "Configured wins" beside a `Source?` of
`CONFIGURED` restates its own answer; "Both sources set" and "Input dir absent" say which case the
row is.

| Good (condition)             | Bad (outcome)      |
|------------------------------|--------------------|
| `Unlabelled item`            | `Rejected`         |
| `Solvent in a sealed drum`   | `Goes to hazardous`|
| `Cardboard over the bulk limit` | `Bulky handling` |

`Solvent in a sealed drum` PASSES even though a reader who knows the rule can predict the outcome —
naming the situation is not naming the answer.

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
holds only where **both** parts vary in the same position. Where identity is fixed for the row and
only the status varies, the ordinary column design applies.

**A compound result stays a collection.** When the value under test is several items — or items
grouped under a key — the expectation is a native list, set or map, nested where needed, compared
against what the system returns. Do not flatten it into a string assembled by a formatting helper:
that tests the formatter rather than the rule, hides the structure from the reader, and puts
formatting logic back into the test body. Use a set where order is not part of the rule, and a list
with a canonical sort where it is.

**A key is part of the value, so the same holds one level down.** Grouping the result under a key
pasted together from several fields — `HEAT/G3/zone-2` standing for a mode, a unit and a zone — has
flattened the structure into the key instead of the value, and the row still reads as one token
whose parts the reader separates by eye. Nest one level per part, or give the key a type with a name.
**The test is the one that governs any cell: can a reader name each part?** A delimited string is not
the alternative to nesting, at any level.

```
Scenario                  | Items         | Streams?
Mixed recyclables         | [paper, card] | [recycling: [paper, card]]
Recyclable and residual   | [paper, foil] | [recycling: [paper], landfill: [foil]]
```

`Streams?` stays a native map, `[String: [String]]`. Flattening it to `"recycling:[paper]"` would
test the formatter.

The `?` marks outputs only — never an input, however yes/no it looks:

| Good (input)         | Bad (input)           | Why bad                       |
|----------------------|-----------------------|-------------------------------|
| `Repeat Donor`       | `Repeat Donor?`       | `?` implies this is an output |
| `Within Rest Period` | `Within Rest Period?` | This is a given condition     |
| `Vent Open`          | `Vent?`               | This is an input state        |

### Model Rejection as an Expected Column

When a table covers cases the system rejects, the rejection is an **expectation column** — the error
type, or the reason — never a decision taken in the test body. Each row then states its own
outcome where the reader can see it.

**Whether accepted and rejected rows share a table is decided by what the table is about, and
there is a decidable test for it: remove the rejected rows.** If what remains still states a rule,
the rejection was a separate concern — split it out. If what remains says nothing on its own, the
table is about acceptance and stays whole.

A tier ladder with one rejection row at the end **fails that test**: strike the rejection and the
ladder still states the tiers. It is two concerns, however tempting the last-accepted-beside-
first-rejected pair looks. A validation boundary passes it: strike the rejected row and a single
accepted value is left, which states nothing by itself.

- **The table's whole expectation is whether the call is rejected** — a boundary straddling a
  validation limit, the last accepted value beside the first rejected one. That is *one rule*, and
  splitting it puts the two halves of a single boundary where no reader sees them together. Keep one
  table, leave the rejection column blank where nothing is rejected, and compare the outcome as a
  value.
- **Rejection is one outcome among several** — a parser returning values for good input and rejecting
  malformed input. Those are two concerns and belong in two tables.

**Never branch in the body to choose how to assert.** Picking between a rejection assertion and a
value assertion per row puts the rule back where the table cannot show it, and it is the failure
both shapes above exist to avoid.

One rule — the whole table asks whether the dose is accepted:
```
Scenario                | Dose (mg) | Rejected?
At the minimum dose     | 0         |
Just below the minimum  | -0.01     | belowMinimum
```

Two concerns — parsing returns values, rejection is its own table:
```
Scenario          | Input      | Parsed?
ISO date          | 2026-07-30 | 2026-07-30
Short year        | 30/07/26   | 2026-07-30
```

### Use Concrete Domain Values

Cell values are concrete, meaningful domain data — not abstract flags, codes, or placeholders. An
expectation value is traceable to the input values in its own row.

**An expectation naming something that appears nowhere in the row is a value hardcoded in the
test, not a value the table states.** The reader then cannot understand the table without reading the
code, which is the one thing the table exists to prevent.

When a value is derived from an input, include the source column so the derivation is visible.

**Prefer the value the system really produces.** Where a sentinel, enum constant or error string is
part of the observable contract, put that in the cell rather than a tidier test-only label — the
row then states what a reader would actually see. Shorten a value only when it is too long to
scan, and shorten the **value**, never the vocabulary: `acme:search:v2` scans as well as a
placeholder and still says what each part is. Single letters cost more than they save, because the
legend that decodes them lives outside the table.

**Where a cell carries several parts, the test is whether a reader can name each one.** That is the
same legend question asked of a compound value: `2 x 5 mg tablet` explains itself, while
`G3/HEAT/zone-2` needs a key that lives somewhere else. It decides how much structure the cell
has to show — spell the parts out where the values alone do not identify them, and let them stand
bare where they do.

Write literal values even when they repeat across rows. Extracting them into named constants
forces the reader to look up every number, which is exactly the indirection the rows exist to
remove.

**Good** — every expectation traceable to the row's own inputs:
```
Scenario                    | Body Weight (kg) | Dose Per Kg (mg) | Daily Dose (mg)?
Standard adult              | 70               | 5                | 350
Paediatric                  | 20               | 5                | 100
```

**Bad** — `standard` and `reduced` appear nowhere in the row:
```
Scenario                    | Heavy | Impaired | Dose?
Normal function             | true  | false    | standard
Impaired function           | true  | true     | reduced
```

### Use Domain Terminology

Column names use domain or feature terminology that readers understand without knowing the
implementation. Avoid parameter names, variable names, and internal API terms.

The table should read as a specification a domain expert could review.

| Good (domain)        | Bad (implementation)   |
|----------------------|------------------------|
| `Body Weight (kg)`   | `weightKg`             |
| `Renal Function`     | `renalFlag`            |
| `Dose Band?`         | `result`               |

### Make Thresholds Visible

When a rule depends on a threshold or limit, include it as a column — even when the value is constant
across every row.

Without the threshold column the number is buried in the code: the reader cannot tell from the table
where the boundary is, or whether the rule is strictly greater than. Boundary rows — at the limit,
just over it — become natural to add once the threshold is visible.

**A constant column often signals configuration.** Ask under what circumstances the value would
differ. The answer may reveal a second axis that belongs as new rows or as a separate table.

**A threshold another rule computes stays a column here, and that rule keeps its table.** Never carry
both the threshold and the input it is derived from — see *Decompose When You See These Signs*.

```
Scenario                | Days Since Last Donation | Min Interval (Policy) | Eligible?
Exactly at the interval | 90                       | 90                    | true
One day short           | 89                       | 90                    | false
```

### Include Traceability Columns

When a table exercises a pipeline — input, then an intermediate result, then a final result — include
the intermediate as an expectation column. A reader can then trace the logic step by step, and when a
row fails the intermediate column shows where in the pipeline it broke.

The intermediate is usually not strictly necessary: the test could verify only the final value. It
earns its place by making the derivation legible in the row.

**Guard: only for values the system exposes, or that are observable domain concepts.** If populating
the column would mean reimplementing an internal calculation in the test, it does not belong — the
intermediate is pointing at a separate concern that needs its own table. Decompose instead, and the
intermediate becomes an expectation in one table and an input in the next.

```swift
@TableTest("""
    Scenario                    | Body Weight (kg) | Renal Function | Dose Band? | Daily Dose (mg)?
    Adult, normal function      | 70               | normal         | standard   | 500
    Adult, impaired function    | 70               | impaired       | reduced    | 250
    Low weight, normal function | 40               | normal         | low        | 300
    """)
func setsTheDailyDose(bodyWeight: Int, renalFunction: RenalFunction, doseBand: DoseBand, dailyDose: Int) { ... }
```

`Dose Band?` is not strictly necessary, but it lets a reader trace weight + renal function -> band ->
daily dose, and a failure shows which step broke.

### Blank Means Absent

Use a blank cell when a value is genuinely absent. Blank means **absent** — not zero, not a default,
and not irrelevant.

Three meanings the notation has to keep apart:

| Meaning | Notation |
|---|---|
| The value is missing | blank cell |
| The value exists but does not affect this row | value set |
| The value is present and empty | `''` for a string, `[]` `{}` `[:]` for a collection |

**The system under test decides what an absent value means — never the test.** That decision is part
of the behaviour being specified, and the row exists to pin it down. Writing a baseline value into
the cell is a different scenario; converting a blank to a default on the way in deletes the case the
row was written to show.

Do not fill a genuinely blank cell with filler like `N/A` or `none`.

```swift
@TableTest("""
    Scenario               | Humidity % | Override Setpoint | Vent Position?
    No override configured | 80         |                   | open
    Override supplied      | 80         | 90                | closed
    """)
func resolvesVentPosition(humidity: Int, overrideSetpoint: Int?, vent: VentPosition) { ... }
```

The blank row specifies what the controller does with *no* override. Writing `0` there would specify
something else, and defaulting it in the function body would specify nothing at all. A blank cell is
`nil`, so its parameter is optional; `''` is an empty **string** and fails to convert to a number.

### Design Black-Box Tables

Model observable inputs and outputs. Avoid internal flags and setup-only columns unless they are part
of the public contract.

Anything the test does beyond arranging, acting and asserting is a rule the table cannot show.
Construction belongs in a conversion helper, the expected error in a column, defaulting and
normalisation outside the body entirely. When you find yourself writing logic in the test, ask which
column or helper it should have been.

```
Scenario                | Humidity % | Temp (C) | Vent Position?
Warm and damp           | 80         | 28       | open
Within target range     | 55         | 21       | closed
```

Observable readings in, observable position out. A `sensorPollCount` or `controllerInitialised`
column would be internal state, not the contract.

In Swift, the conversion helper of this rule is a `TableValueDecodable` or `TableTextDecodable`
conformance, or a member of the cell's own type (*Where Conversion Code Goes* in `SKILL.md`).

### Keep the Tables of One Concern Consistent

Tables that sit together are read together. Within one table set, the same concept takes the
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

Two tables in one suite, one notation for the concept they share:

```swift
@TableTest("""
    Scenario           | Upstream Latency | Response Time?
    Healthy upstream   | <10              | <50
    Upstream throttled | <400             | <500
    """)
func answersWithinTheLatencyBudget(upstreamLatency: Latency, responseTime: Latency) { ... }

@TableTest("""
    Scenario           | Upstream Latency | Report Time?
    Healthy upstream   | <10              | <50
    Upstream throttled | <400             | <500
    """)
func publishesTheReportWithinTheLatencyBudget(upstreamLatency: Latency, reportTime: Latency) { ... }

extension Latency: TableTextDecodable {
    public init?(tableText: String) { ... }
}
```

A bare `50` in the second table would leave the reader deciding whether it means a maximum or an
exact value. **A type converts itself, once**: `Latency` has one `TableTextDecodable` conformance,
so every column of that type reads the same notation, and a second notation for it cannot exist.

<!-- END GENERATED table-design -->

---

## Choose the Input Columns by What Decides the Result

**Before you write an input column, ask what the code reads to decide the result.** Each such input
is a column. An input is one of three kinds:

- **the situation** the row starts in — where a screen came from, the state of a record;
- **the action** the user takes — adds an item, stops a drug, changes nothing;
- **the data** that the rule judges.

A column that only holds the data that builds the situation is not an input.

**The sign of a wrong column: the scenario name states what decides the row, and no column does.**
The columns hold two whole records, and the reader must compare them to find the difference that the
name states. Make that difference the column. The scenario column stays: its name now says in a
reader's words what the columns hold.

**Data is the input only when the rule is about that data.** A rule about what a record contains
takes the record. A rule about what the user did to the record takes the action.

**Name a situation or an action in domain words.** The word is the concrete value of that input. It
is not a placeholder. A test-local enum with `String` raw values reads the word, and builds the
fixture from it.

**An output is a projection of the actual result** — the property of the result that the rule
decides, such as `review.signOffs`. Never compute the expectation from the inputs in the test. **An
enum result is not split into projections**: compare the whole case, written as Swift spells it (see
*Enums with Associated Values*).

**Cross the input columns to find the missing rows.** List the values of each input column, and look
at each combination. A combination with no row is a missing row, or a value set on a row that shows
the input does not matter.

Before — two whole prescriptions per row, and only the scenario name says what the nurse did:

```
Scenario                      | Ward           | Prescription On File   | Prescription On Screen | Sign-offs?
Dose raised on the ward       | general ward   | [paracetamol: 1 g x 4] | [paracetamol: 2 g x 4] | [secondNurse]
Dose raised in intensive care | intensive care | [paracetamol: 1 g x 4] | [paracetamol: 2 g x 4] | [secondNurse, pharmacist]
Drug stopped                  | general ward   | [paracetamol: 1 g x 4] |                        | [secondNurse]
```

After — the situation and the action are the columns:

```swift
/// Fixture: the standing order is paracetamol 1 g, four times a day.
struct MedicationChartReviewTests {
    enum Ward: String {
        case general = "general ward"
        case intensiveCare = "intensive care"

        var isIntensiveCare: Bool { self == .intensiveCare }
    }

    enum Change: String {
        case none = "no change"
        case raisesTheDose = "raises the dose"
        case lowersTheDose = "lowers the dose"
        case stopsTheDrug = "stops the drug"

        func applied(to order: MedicationOrder) -> MedicationOrder? {
            switch self {
            case .none: order
            case .raisesTheDose: order.withDose(grams: order.doseGrams * 2)
            case .lowersTheDose: order.withDose(grams: order.doseGrams / 2)
            case .stopsTheDrug: nil
            }
        }
    }

    static let standingOrder = MedicationOrder(drug: "paracetamol", doseGrams: 1, timesPerDay: 4)

    @TableTest("""
        Scenario                       | Ward                           | Change          | Sign-offs?
        Prescription unchanged         | {general ward, intensive care} | no change       | []
        Dose raised on a general ward  | general ward                   | raises the dose | [secondNurse]
        Dose raised in intensive care  | intensive care                 | raises the dose | [secondNurse, pharmacist]
        Dose lowered                   | {general ward, intensive care} | lowers the dose | [secondNurse]
        Drug stopped                   | {general ward, intensive care} | stops the drug  | [secondNurse]
        """)
    func asksForTheSignOffsAChangeNeeds(ward: Ward, change: Change, signOffs: [SignOff]) {
        let review = ChartReview(onFile: Self.standingOrder, onScreen: change.applied(to: Self.standingOrder),
                                 intensiveCare: ward.isIntensiveCare)
        #expect(review.signOffs == signOffs)
    }
}
```

The `switch` and the comparison with a case are members of the test-local enums, which build the
fixture. The test function has no branch, and no helper that takes the enum branches on it.
The value sets show that the ward does not change three of the outcomes. Crossing the two columns
finds every combination, so no row is missing.

## Design Workflows

### Converting Existing Tests

1. **Find the function that decides the result before you convert a test of a function that calls
   it.** Where another test already has a table of the deciding function, do not convert into a
   second copy of that table. Add the cases that the table lacks to it as rows. Keep one test of the
   calling function, which shows that it uses the decision. Remove the rest, and name the covering
   rows in your answer. The number of cases then falls, as expected.
2. Find the tests with one structure and different data: plain `@Test` functions, `@Test(arguments:)`
   or XCTest methods.
3. Make the varying parts columns: the inputs and the expected values. **Where the old test builds an
   app value, decide by what the value is:**
   - a struct that only gathers the inputs of the call gets one column for each field, and the body
     builds it with its initialiser (see *Structs*);
   - an enum, or a value that the rule reads as one thing, is one cell. It converts by *Where
     Conversion Code Goes*.

   Conversion code — a `switch`, a dictionary, parsing — never stays in the function body.
4. Write the table: the scenario column first, then the inputs, then the expectations with `?`.
5. Give the function one parameter for each data column, in column order.
6. Check that every row uses the same assertion.
7. Remove the old tests that the table covers. Run the tests before and after: the number of cases
   stays the same, or your answer explains the change.
8. **From XCTest, finish the migration to Swift Testing.** Replace `XCTAssert…` with `#expect`, and
   `XCTestCase` subclasses with a struct. Remove `import XCTest` where nothing uses it.

### Writing New Tests from a Feature Description

When the logic does not exist yet, write the tests first. The table design drives the API.

1. **Read the description** and find the rules.
2. **Write the tests**, one `@TableTest` for each rule, by the design rules above.
3. **Add a stub** for each type and function the tests call, with signatures only. Do not implement
   the logic unless the user asks for it. The tests must compile.

**Let the tables drive the API.** Each table calls a function whose parameters are the table's input
columns. When a test needs a loop or a helper to make raw data that gives a derived input its value,
the table is too high in the stack. Stub a narrower function that takes the derived value, and test
the derivation in its own table.

**Ambiguity policy — deliver, do not ask.** Choose the most reasonable reading, write it and any open
question in the function's doc comment, and deliver complete tests. Never end the task with questions
in place of tests.

### Writing New Tests from Existing Code

1. **Understand**: read the code, and find what varies.
2. **Design**: decide the concerns and each table's axis before you write a row.
3. **Implement**: write the full table.
4. **Run**: build and test at once, for fast feedback on the conversions.
5. **Refine**: read the table again and fix what the rules catch. Write domain names in the first
   draft; do not leave them to this step.

The ambiguity policy applies here too.
