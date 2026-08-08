---
name: spec-by-example
description: Clarify behaviour through concrete example tables before or during implementation. Trigger when requirements use vague terms ("eligible customers", "valid input", "appropriate discount"), when behaviour depends on multiple conditions whose combinations aren't fully worked out, when an edge case surfaces mid-implementation, or when the user asks to work through examples of a feature. Do NOT use when the user asks to write tests, convert tests, or create a @TableTest — use the tabletest skill instead, which has its own requirements decomposition workflow and will produce executable @TableTest code directly.
---

# Spec by Example

Use this skill when behaviour has multiple cases, conditions, or rules that are not
yet pinned down by concrete examples. The table becomes a shared specification —
readable and verifiable by domain experts and developers alike — and a natural
starting point for a TableTest once coding starts.

The approach is inspired by the FIT (Framework for Integrated Tests) workflow,
where teams work out business rules collaboratively by filling in tables of
examples. The goal is not a finished test, but enough agreed-upon examples to
implement with confidence.

## When to Use This Skill

This skill is useful at any point — before implementation begins, mid-way through,
or when revisiting a feature. The trigger is encountering conditional logic or
variation that needs examples to pin down, not the phase of development.

When you already have working tests to consolidate or refine and the requirements
are already clear, use `/tabletest` directly — it will produce executable
`@TableTest` code.

---

## Quick Example

An example table for car rental eligibility:

| Scenario                          | Customer Age | Has Licence | Car Category      | Eligible? | Reason?              |
|-----------------------------------|--------------|-------------|-------------------|-----------|----------------------|
| Standard adult customer           | 30           | yes         | Economy           | yes       |                      |
| Underage applicant                | 17           | no          | Economy           | no        | Under 18             |
| No driving licence                | 25           | no          | Economy           | no        | No licence           |
| Young driver in premium car       | 22           | yes         | Premium           | no        | Under 25 for Premium |
| Underage regardless of category   | 17           | yes         | {Economy, Premium}  | no        | Under 18             |
| Senior with valid licence         | 72           | yes         | Economy           | yes       |                      |

Key properties of this table:

- Written entirely in business language — no code, no types, no variable names
- Each row is a complete, verifiable example a domain expert can confirm or challenge
- Outputs (suffixed `?`) are traceable to input values
- One row uses multiple values (`Economy, Premium`) to show a rule holds regardless of category
- The table maps directly to a future `@TableTest` implementation

---

## Elicitation Workflow

**Start with the table that is clearest and most central, and let the others emerge.** Additional
tables announce themselves as rows that do not fit — that is the signal to split, and it arrives
during the conversation rather than before it. Do not design a set of tables upfront.

### 1. Name the Behaviour

Start by agreeing on what the table will describe. Use a verb phrase from the domain:

- "Car Rental Eligibility"
- "Blood Donation Deferral"
- "Medication Dose Calculation"
- "Waste Sorting Classification"

Getting the name right focuses the examples and later becomes the test method name.
If you cannot name it cleanly, the behaviour may be two concerns — keep that in mind.

### 2. Find the First Example

Ask for the simplest, most obvious case where the behaviour works as intended:

- "What does a typical successful case look like?"
- "Give me one concrete example — with real values — where this works."
- "Walk me through the default situation."

Write this as the first data row. The *set* of columns will change as more examples
arrive — but name each one in the domain's words as you write it, never in technical
ones you mean to fix later. There may be no later.

### 3. Identify the Columns

**Input columns** — what varies between examples:

- Ask: "What information does the system need to make this decision?"
- Ask: "What changes between one example and the next?"
- Each distinct piece of information becomes a column.
- Use the domain's own words (`Customer Age`, not `age`, `userAge`, or `int`).

**Output columns** — what the system produces or decides:

- Ask: "What is the system's response or decision?"
- Ask: "What do we verify to know the behaviour is correct?"
- Suffix output column names with `?` (`Eligible?`, `Deferral Period?`, `Error Message?`).

The `?` suffix is reserved for output columns — see *Name Expectation Columns Clearly* below.

### 4. Add More Examples

Work through variations systematically:

**Different outcomes** — what causes the decision to go the other way?

- "What makes the answer change from yes to no?"
- "What other rules apply?"

**Boundary conditions** — where exactly do rules trigger?

- "At what exact value does this rule kick in?"
- "What happens just at the threshold, just above, and just below?"
- Include rows at exact boundaries (e.g., 13 hours, 14 hours for a duty-time limit)
- Boundaries are where misunderstandings live — a table that only shows mid-range values illustrates rule *types* but does not *specify* the behaviour

**Special cases** — important situations that may surprise people:

- "Is there a case that surprises new team members?"
- "What's a common misunderstanding about this behaviour?"

**Missing or absent inputs** — what happens when information is not provided?

- "What if this field is empty or not given?"
- "Is there a sensible default, or does absence cause a rejection?"

### 5. Probe for Irrelevant Inputs

Ask which inputs the rule is indifferent to, and record the answer as data:

- "Does this rule still hold whatever the category is?"
- "Which of these inputs could I change without changing the answer?"

An input the expert says does not matter is a claim, and a claim needs a row that could
contradict it — see *Use Value Sets for "Regardless Of" Relationships* below.

### 6. Ask for the Decision and the Calculation Separately

When the conversation mixes a decision with a calculation, split the questions:

- "How do you decide which band this falls into?"
- "Once you know the band, how is the number worked out?"

Two questions, two tables — see *Separate Rules from Arithmetic* below.

### 7. Ask for State as Before and After

When the behaviour involves state, ask for it as before-and-after rather than as a sequence:

- "What state is it in before this happens, and what state after?"
- "What does the system say when the action is not allowed from that state?"

A described sequence is not a table — see *Frame Stateful Features as Transition Rules* below.

### 8. Review the Table

Show the table to a domain expert (or read it as one) and ask:

- "Does every row describe a situation that can actually happen?"
- "Is the outcome in every row what you would expect?"
- "Is there an important case missing from this table?"
- "Are any two rows testing the same thing?"
- "Could a new team member understand each row without asking questions?"

### 9. Note What Is Still Open

Not everything needs to be resolved before coding starts. Mark uncertain cells or
add a notes column for open questions:

| Scenario             | Customer Age | Has Licence | Car Category | Eligible? | Open Questions         |
|----------------------|--------------|-------------|--------------|-----------|------------------------|
| Senior age limit?    | 75           | yes         | Economy      | ?         | Is there a maximum age? |

Open cells signal decisions that need resolving — through conversation, a domain
decision, or implementation exploration. They are not failures; they are honest
about what is known and unknown.

---

## Table Design

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
claims not to affect the outcome is the opposite situation and has to vary — see *Value Sets for
"Regardless Of" Relationships*.

Separate tables reduce rows by avoiding unnecessary permutations, and the table count guides the
implementation: five concern tables suggest five functions.

| Scenario       | Hours Since Rest | Max Duty Hours (Policy) | Fit To Fly? |
|----------------|------------------|-------------------------|-------------|
| At the limit   | 13               | 13                      | yes         |
| Past the limit | 14               | 13                      | no          |

"Duty eligibility **and** rest credit" fails the "and" test — rest credit gets its own table.

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

| Scenario            | Humidity % | Temp (C) | Vent Position? | Heater? | Alert?            |
|---------------------|------------|----------|----------------|---------|-------------------|
| Warm and damp       | 80         | 28       | OPEN           | off     |                   |
| Cold and damp       | 80         | 8        | CLOSED         | on      | Condensation risk |
| Within target range | 55         | 21       | CLOSED         | off     |                   |

All three outputs of one climate decision, so a reader sees the whole response per scenario.

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

| Scenario                   | Adjustments                        | Daily Dose? |
|----------------------------|------------------------------------|-------------|
| No adjustment              | [:]                                | 500         |
| Renal impairment           | [renal: severe]                    | 250         |
| Low body weight            | [weightKg: 20]                     | 200         |
| Interacting drug           | [interaction: true]                | 400         |
| Renal and interacting drug | [renal: severe, interaction: true] | 200         |

`Adjustment` works as a column header with those as its values, which is what makes it a family and
not a bag.

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

Earns its place — which rule applies first is a question no other table answers:

| Scenario                   | Body Weight (kg) | Daily Max (mg) | Daily Dose? |
|----------------------------|------------------|----------------|-------------|
| Weight-based below the cap | 40               | 400            | 200         |
| Weight-based above the cap | 120              | 400            | 400         |

Does not earn its place — re-runs each band end to end and answers nothing new:

| Scenario              | Body Weight (kg) | Daily Dose? |
|-----------------------|------------------|-------------|
| Low weight end to end | 20               | 100         |
| Adult end to end      | 70               | 350         |

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

| Scenario        | Duty Hours | Normal Hours? | Extended Hours? |
|-----------------|------------|---------------|-----------------|
| Below the limit | 8          | 8             | 0               |
| At the limit    | 13         | 13            | 0               |
| Past the limit  | 14         | 13            | 1               |

Table 2 — the arithmetic (extended hours earn rest credit at double rate):

| Scenario      | Normal Hours | Extended Hours | Rest Credit? |
|---------------|--------------|----------------|--------------|
| Ordinary duty | 13           | 0              | 13.0         |
| Duty ran long | 13           | 1              | 15.0         |

### Give Each Obligation Exactly One Row

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

| Scenario                 | Duty Hours | Extra Rest Required? |
|--------------------------|------------|----------------------|
| At the duty limit        | 13         | no                   |
| Just past the duty limit | 13.5       | yes                  |
| Well past the duty limit | 20         | yes                  |

The third row is redundant — 13.5 already discharged "past the limit requires extra rest". Keep the
straddling pair, drop the one further out.

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

| Scenario                     | Haemoglobin | Donation Band? |
|------------------------------|-------------|----------------|
| Below the minimum            | 124         | DEFER          |
| At the minimum               | 125         | STANDARD       |
| Top of the standard band     | 159         | STANDARD       |
| First value of the high band | 160         | REVIEW         |

Every band appears, and each boundary is shown from both sides.

### Use Value Sets for "Regardless Of" Relationships

When an input exists but does not affect the outcome of a row, say so with data rather than prose:
put every value the rule ignores in the cell.

A blank would wrongly suggest the field is absent. The value set makes the claim explicit — *this rule
holds for all these values* — and one row states it more precisely than two near-identical ones.

**The clearest sign you want one: a column that could carry every one of its values on every
row without changing anything.** That is the rule saying, in data, that it does not read the
column.

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

| Scenario                           | Donor Age | Haemoglobin | Recent Travel | Eligible? |
|------------------------------------|-----------|-------------|---------------|-----------|
| Below the minimum age              | 16        | {125, 140}  | {yes, no}     | no        |
| Eligible adult donor               | 35        | 140         | no            | yes       |
| Travelled recently, otherwise fine | 35        | 140         | yes           | no        |

The first row claims age alone decides it. A blank in those cells would have said the values were
*absent*, which is a different statement.

### Frame Stateful Features as Transition Rules

When a feature involves state — queues, workflows, inventories — frame each row as a state
transition rule: the state before, the action, the state after, and any message or result.

Each row is independent: given this state, when this action happens, expect this result. No row
depends on a previous one having run.

**Include the before and after columns** even when the description states the operation procedurally.

A sequential path — step 1, then step 2, then step 3 — creates row dependencies and is not a table
at all.

| Scenario                 | Bin Before     | Action            | Bin After?     | Message?     |
|--------------------------|----------------|-------------------|----------------|--------------|
| Accept a labelled item   | [:]            | deposit cardboard | [CARDBOARD: 1] | Accepted     |
| Fill to the bulk limit   | [CARDBOARD: 1] | deposit cardboard | [CARDBOARD: 2] | Accepted     |
| Reject a mismatched item | [CARDBOARD: 1] | deposit solvent   | [CARDBOARD: 1] | Wrong stream |

Each row stands alone — none of them assumes the row above ran first.

### Assume the Table Is Published

Write every table as if a reader will meet it in a published report, never having seen the code. Only
three surfaces reach that reader, and they divide the work:

| Surface | Carries |
|---|---|
| the table's heading | the rule, as an action the code performs |
| the note beneath the table | the apparatus that cannot be a column — what is held constant, where the data came from |
| the table | the variations the rule ranges over |

**Whatever the table holds constant is silently promoted into the rule.** Readers generalise from
what varies, so a value that never varies is read as part of the rule: a duty-limit table whose every
row assumes a two-pilot crew states, to its reader, a rule about two-pilot crews.

So a constant the outcome depends on is a **column** wherever it can be one — and a value the rule
turns on, such as a threshold or a limit, always can be. The other two surfaces carry what a column
cannot: where the data came from, what the fixture fixes, an assumption the rows cannot state.

**Once a value is a column, it is declared — check the table before writing about it or adding to
it.** A the note beneath the table sentence naming a value the rows already show tells the reader
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
ignores; see *Use Value Sets for "Regardless Of" Relationships*. Write a fixture into the
the note beneath the table only for what the rule genuinely reads and the rows cannot show.

**Making a value a column does not force everything measured from it into the same form.** Once a
reference point is declared — a clock, an origin, a baseline — the columns measured *from* it read
better as offsets against it than as restatements of it. Both are then visible, and the offsets stay
short enough to scan.

**Choose that unit before shortening anything, and choose it from the boundary rather than from the
offset.** A shorter cell that cannot state the rule has bought nothing — see *Cover Every Tier and
Both Sides of Every Boundary*, which owns the choice.

**Keep a slot in the cell for every field the the note beneath the table makes a claim about.** A compact
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

| Scenario                | Duty Hours | Crew Size | Max Duty Hours (Policy) | Fit To Fly? |
|-------------------------|------------|-----------|-------------------------|-------------|
| Two-pilot crew, inside  | 12         | 2         | 13                      | yes         |
| Two-pilot crew, past    | 14         | 2         | 13                      | no          |
| Augmented crew, past 13 | 14         | 3         | 17                      | yes         |

### Write Titles That Form an Index

The table's heading is the line a reader scans in the report index. **Judge titles as a set, never one
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

| Scans as an index                                  | Does not                         |
|----------------------------------------------------|----------------------------------|
| Sets the deferral interval from donation type      | Deferral interval                |
| Rejects a reading below the haemoglobin minimum    | Haemoglobin minimum by donor sex |
| Defers a donor returning from a listed destination | Travel deferral rules            |

The right column names topics; the left names what the system does with them.

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

| Good (condition)                | Bad (outcome)       |
|---------------------------------|---------------------|
| Unlabelled item                 | Rejected            |
| Solvent in a sealed drum        | Goes to hazardous   |
| Cardboard over the bulk limit   | Bulky handling      |

Naming the situation is not naming the answer — the first column is right even when the outcome is
inferable from it.

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

| Scenario                | Items         | Streams?                                |
|-------------------------|---------------|-----------------------------------------|
| Mixed recyclables       | [paper, card] | [recycling: [paper, card]]              |
| Recyclable and residual | [paper, foil] | [recycling: [paper], landfill: [foil]]  |

`Streams?` carries the `?`; `Items` does not. Only the column being verified takes the suffix.

The `?` marks outputs only — never an input, however yes/no it looks:

| Good (input)       | Bad (input)         | Why bad                       |
|--------------------|---------------------|-------------------------------|
| Repeat Donor       | Repeat Donor?       | `?` implies this is an output |
| Within Rest Period | Within Rest Period? | This is a given condition     |
| Vent Open          | Vent?               | This is an input state        |

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

One rule — the whole table asks whether the registration is accepted:

| Scenario              | Name  | Email           | Valid? | Rejection Reason?  |
|-----------------------|-------|-----------------|--------|--------------------|
| Complete registration | Ada   | ada@example.com | yes    |                    |
| Missing name          |       | ada@example.com | no     | Name is required   |
| Email without @       | Ada   | ada.example.com | no     | Email is malformed |

`Rejection Reason?` is blank where nothing is rejected — the same column, not a second table.

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

| Scenario       | Body Weight (kg) | Dose Per Kg (mg) | Daily Dose (mg)? |
|----------------|------------------|------------------|------------------|
| Standard adult | 70               | 5                | 350              |
| Paediatric     | 20               | 5                | 100              |

**Bad** — `standard` and `reduced` appear nowhere in the row, so the table cannot be read on its own:

| Scenario          | Heavy | Impaired | Dose?    |
|-------------------|-------|----------|----------|
| Normal function   | true  | false    | standard |
| Impaired function | true  | true     | reduced  |

### Use Domain Terminology

Column names use domain or feature terminology that readers understand without knowing the
implementation. Avoid parameter names, variable names, and internal API terms.

The table should read as a specification a domain expert could review.

| Good (domain)      | Bad (implementation) |
|--------------------|----------------------|
| Body Weight (kg)   | weightKg             |
| Renal Function     | renalFlag            |
| Dose Band?         | result               |

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

| Scenario                | Days Since Last Donation | Min Interval (Policy) | Eligible? |
|-------------------------|--------------------------|-----------------------|-----------|
| Exactly at the interval | 90                       | 90                    | yes       |
| One day short           | 89                       | 90                    | no        |

Without `Min Interval (Policy)`, 90 is invisible and no reader can tell whether the boundary is
inclusive.

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

| Scenario                    | Body Weight (kg) | Renal Function | Dose Band? | Daily Dose (mg)? |
|-----------------------------|------------------|----------------|------------|------------------|
| Adult, normal function      | 70               | Normal         | Standard   | 500              |
| Adult, impaired function    | 70               | Impaired       | Reduced    | 250              |
| Low weight, normal function | 40               | Normal         | Low        | 300              |

`Dose Band?` is not strictly necessary — it is there so a reader can trace weight and renal function
to the band, and the band to the dose.

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

| Scenario                  | Humidity % | Override Setpoint | Vent Position? |
|---------------------------|------------|-------------------|----------------|
| No override configured    | 80         |                   | OPEN           |
| Override supplied         | 80         | 90                | CLOSED         |

The blank row specifies what the controller does with *no* override. Writing `0` there would specify
something else.

### Design Black-Box Tables

Model observable inputs and outputs. Avoid internal flags and setup-only columns unless they are part
of the public contract.

Anything the test does beyond arranging, acting and asserting is a rule the table cannot show.
Construction belongs in a conversion helper, the expected error in a column, defaulting and
normalisation outside the body entirely. When you find yourself writing logic in the test, ask which
column or helper it should have been.

| Scenario            | Humidity % | Temp (C) | Vent Position? |
|---------------------|------------|----------|----------------|
| Warm and damp       | 80         | 28       | OPEN           |
| Within target range | 55         | 21       | CLOSED         |

Observable readings in, observable position out. "Controller initialised" would be internal state,
not something a domain expert can review.

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

Two tables in one response, one notation for the concept they share:

| Scenario           | Upstream Latency | Response Time? |
|--------------------|------------------|----------------|
| Healthy upstream   | <10              | <50            |
| Upstream throttled | <400             | <500           |

| Scenario           | Upstream Latency | Report Time? |
|--------------------|------------------|--------------|
| Healthy upstream   | <10              | <50          |
| Upstream throttled | <400             | <500         |

A bare `50` in the second table would leave the reader deciding whether it means a maximum or an
exact value.

<!-- END GENERATED table-design -->

---

## From Example Table to TableTest

When implementation begins, hand the table to `/tabletest` — the column structure carries over directly. An example table maps to `@TableTest` like this:

| Example Table                    | TableTest                               |
|----------------------------------|-----------------------------------------|
| Plain markdown table             | `@TableTest("""...""")` annotation      |
| Input columns                    | Method parameters                       |
| Output columns (`?` suffix)      | Parameters used in assertions           |
| Scenario column                  | Optional `@Scenario` parameter          |
| Multiple values in a cell        | Value set `{Economy, Premium}`          |
| Business-language column names   | Kept as-is — they are already the domain terms |

A few things to keep in mind for the handoff:
- Custom types and enums will need type converters — `/tabletest` handles this
- Open cells marked `?` become the first decisions to resolve during implementation

The example table is not a throwaway artefact — it is the first draft of the living specification. Treat it accordingly.

---

## Quality Checks

Before handing off to implementation, verify the example table.

**Table design** — the shared rules above, in checklist form:

<!-- BEGIN GENERATED table-design-checks — do not edit here; source is shared/table-design/ -->

- [ ] **One rule per table**: every row and column serves this table's one axis; a behaviour you cannot name without "and" has been split, and a rule that is a conjunction of independent conditions has one table per condition rather than their cross-product — but inputs that are contributions to one combined answer stay in one table, with rows that show them combining
- [ ] **Complete outputs**: all observable outputs of the same rule sit in one table, and every expectation column there is exercised by the rows that table varies — one constant down all rows, or moving only as a side effect of another, belongs to a different rule's table
- [ ] **Decomposed, not over-split**: no table mixes concerns (blank-throughout columns, qualified scenario names, two groups of expectation columns), no table carries both a value and the raw data another rule derives it from, and no set of same-fixture tables reports one expectation column that a family column would collapse
- [ ] **Combining tables prove an interaction**: any table exercising several rules together shows behaviour the single-rule tables cannot (a precedence, an ordering), not the earlier rules re-run end to end
- [ ] **Rules separated from arithmetic**: every expectation cell is predictable from its row in one step; a classification and the calculation that follows it are two tables
- [ ] **One row per obligation**: every obligation of the concern is discharged by some row, and every row discharges one no other row in that table reaches; where two rows share an expectation, swapping what differs between them would change an expectation cell in that table — not a value further past the same boundary, a larger n in the same direction, or an input the rule ignores even though it names it
- [ ] **Every tier once**: a tier ladder has one row per tier — all of them, none twice — and every boundary is exercised from both sides at the finest unit the rule distinguishes, whether by two rows or by a value set whose end members are the tier's own edges, middle tiers included, and a boundary an input reaches through a formula straddled like any other; where one ladder repeats across classes that share its boundary positions, the straddling pairs appear in one class and the rest carry one row per tier
- [ ] **Value set semantics**: value sets appear only where every value produces the same result, never as shorthand for "test several values"; an input this rule claims not to affect the outcome varies across the values it ignores, while an input another rule owns is held at one valid value
- [ ] **Stateful rows independent**: transition rows carry their own before-state and after-state; no row depends on another having run
- [ ] **Held constants declared**: every value the outcome depends on that the table fixes for all rows is a column where it can be one — always so for a threshold or limit the rule turns on — and otherwise named in the title or description as held fixed; never left only in the test body, a field, a conversion helper, or a comment; a value already shown as a column needs nothing further on any surface
- [ ] **Titles form an index**: read the titles as a sorted list — each states an action the code performs (not a label for a topic), one grammatical shape runs across them, and no three share an uninformative opener
- [ ] **No scenario name restates its own row's answer**: read each scenario name beside the expectation cells of that row — none states or paraphrases one of them, and none is a generic label
- [ ] **Expectation columns marked**: at least one column uses the `?` suffix (never a prefix), no input column does, and a compound result stays a native collection rather than a flattened string
- [ ] **Rejection expressed as data**: rejected rows carry the error type or reason in an expectation column, never a hardcoded outcome in the body; accepted and rejected rows share a table only where striking the rejected rows would leave a table stating nothing, and no row branches the assertion
- [ ] **Concrete values**: expectation values are literal domain values traceable to the input columns of their own row — not abstract codes, and not hidden behind named constants
- [ ] **Domain language**: column names use the business vocabulary, not parameter names, field names or internal API terms
- [ ] **Thresholds visible**: a rule that depends on a threshold or limit shows it as a column, with boundary rows at and just past it
- [ ] **Traceability columns**: an intermediate expectation appears only where the value is observable from the public API — never reimplemented from internal logic; if a formula would have to be reimplemented to fill it, decompose instead
- [ ] **Blank means absent**: a column whose input is genuinely absent for a row uses a blank cell, not 0 or a default; an input that is present but irrelevant is a value set instead, and nothing converts a blank to a default on the way in
- [ ] **Black-box design**: columns represent observable inputs and outputs, not internal flags or implementation details
- [ ] **Consistent across tables**: within one table set, one concept has one column name, one kind of value has one notation, one failure has one spelling, and the rows are served by shared helpers rather than per-table copies

<!-- END GENERATED table-design-checks -->

**This skill's own checks** — what a facilitated example table needs beyond a well-designed one:

- [ ] **Multiple rows**: at least 2–3 rows — enough to reveal the column structure and decision boundaries
- [ ] **Domain-expert readable**: a domain expert could confirm or challenge every row without reading code
- [ ] **Open questions noted**: uncertain cells or unresolved decisions are marked, not silently left blank
- [ ] **Edge cases raised in the conversation**: absent inputs and rejection cases were asked about, not assumed away
