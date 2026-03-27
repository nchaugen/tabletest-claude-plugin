---
name: spec-by-example
description: Use to pin down behaviour through concrete examples before or during implementation. Trigger when requirements use vague terms ("eligible customers", "valid input", "appropriate discount"), when behaviour depends on multiple conditions whose combinations aren't fully worked out, or when an edge case surfaces mid-implementation that calls the assumed behaviour into question. Use this even when the user doesn't say "spec" or "example" — if they're describing conditional logic that needs examples to clarify, this skill applies.
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

Use this skill when:

- The behaviour depends on multiple conditions and the combinations are not fully
  worked out ("it depends on the customer's age and whether they hold a licence")
- Requirements use vague language that concrete examples would sharpen ("eligible
  customers", "appropriate discount", "valid input")
- Reading existing code reveals that a rule is more complex than it initially
  appeared, and examples would clarify the intended behaviour
- An edge case surfaces mid-implementation that calls the assumed behaviour into
  question

This skill is useful at any point — before implementation begins, mid-way through,
or when revisiting a feature. The trigger is encountering conditional logic or
variation that needs examples to pin down, not the phase of development.

When you already have working tests to consolidate or refine, use `/tabletest`
instead.

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

### 1. Name the Behaviour

Start by agreeing on what the table will describe. Use a verb phrase from the domain:

- "Car Rental Eligibility"
- "Loan Approval Decision"
- "Discount Calculation"
- "Order Status Transition"

Getting the name right focuses the examples and later becomes the test method name.
If you cannot name it cleanly, the behaviour may be two concerns — keep that in mind.

### 2. Find the First Example

Ask for the simplest, most obvious case where the behaviour works as intended:

- "What does a typical successful case look like?"
- "Give me one concrete example — with real values — where this works."
- "Walk me through the default situation."

Write this as the first data row. Column naming can be rough at this stage; refine
it as more examples arrive.

### 3. Identify the Columns

**Input columns** — what varies between examples:

- Ask: "What information does the system need to make this decision?"
- Ask: "What changes between one example and the next?"
- Each distinct piece of information becomes a column.
- Use the domain's own words (`Customer Age`, not `age`, `userAge`, or `int`).

**Output columns** — what the system produces or decides:

- Ask: "What is the system's response or decision?"
- Ask: "What do we verify to know the behaviour is correct?"
- Suffix output column names with `?` (`Eligible?`, `Discount?`, `Error Message?`).

**The `?` suffix is reserved for output columns only.** Do not use `?` on input columns,
even when the input is a yes/no condition. Input columns describe the given state; output
columns describe what the system decides or produces.

| Good (Input)         | Bad (Input)           | Why bad                        |
|----------------------|-----------------------|--------------------------------|
| `Loyalty Member`     | `Loyalty Member?`     | `?` implies this is an output  |
| `Within 24h`         | `Within 24h?`         | This is a given condition      |
| `Trial Active`       | `Trial?`              | This is an input state         |
| `Has Licence`        | `Has Licence?`        | This is a given fact           |

### 4. Add More Examples

Work through variations systematically:

**Different outcomes** — what causes the decision to go the other way?

- "What makes the answer change from yes to no?"
- "What other rules apply?"

**Boundary conditions** — where exactly do rules trigger?

- "At what exact value does this rule kick in?"
- "What happens just at the threshold, just above, and just below?"
- Include rows at exact boundaries (e.g., 40 hours, 41 hours for an overtime threshold)
- Boundaries are where misunderstandings live — a table that only shows mid-range values illustrates rule *types* but does not *specify* the behaviour

**Special cases** — important situations that may surprise people:

- "Is there a case that surprises new team members?"
- "What's a common misunderstanding about this behaviour?"

**Missing or absent inputs** — what happens when information is not provided?

- "What if this field is empty or not given?"
- "Is there a sensible default, or does absence cause a rejection?"

### 5. Probe for Irrelevant Inputs

When one input clearly does not affect the outcome, put multiple values in a single
cell — surrounded by `{...}` — to express "regardless of":

| Scenario                        | Customer Age | Has Licence | Car Category        | Eligible? | Reason?  |
|---------------------------------|--------------|-------------|---------------------|-----------|----------|
| Underage regardless of category | 17           | yes         | {Economy, Premium}  | no        | Under 18 |

This single row documents that the age rule takes precedence over category — without
listing every category separately. It also prevents implementation assumptions where
the category is left unspecified.

The `{...}` notation reads as "any of these values". It maps directly to the value-set
syntax in `@TableTest`, so the spec carries over into code without translation. Prefer
it over listing near-identical rows — two rows that differ only in one irrelevant
column are harder to read and easier to get out of sync than one row with a value set.

**State and status values** — value sets are especially useful when a rule holds
regardless of which status or state an entity is in:

| Scenario                              | Current Status       | Target Status | Allowed? | Reason?            |
|---------------------------------------|----------------------|---------------|----------|--------------------|
| Cancellation from pre-shipment states | {PENDING, CONFIRMED} | CANCELLED     | yes      |                    |
| Cancellation after shipment           | SHIPPED              | CANCELLED     | no       | Already dispatched |

Without the value set, you would need two almost-identical rows. One row with
`{PENDING, CONFIRMED}` states the rule more precisely: cancellation is allowed
from any pre-shipment state, not just the two listed.

### 6. Focus Tables on Rules, Not Arithmetic

When a feature involves both domain rules and arithmetic, separate them. Tables should
specify the interesting decisions — classifications, eligibility rules, tier lookups,
state transitions — not test that multiplication works.

**Good decomposition** — tables focus on the rule:

| Scenario              | Package weight | Destination zone | Size category? | Surcharge? |
|-----------------------|----------------|-----------------|----------------|------------|
| Light domestic parcel | 0.5 kg         | Domestic         | Standard       | none       |
| Heavy domestic parcel | 12 kg          | Domestic         | Oversize       | 5.00       |
| Light international   | 0.5 kg         | International    | Standard       | none       |
| Heavy international   | 12 kg          | International    | Oversize       | 8.50       |

The interesting rule is how weight and destination determine the size category and
surcharge. Once the category is known, shipping cost = base rate + surcharge is
arithmetic — a separate, minimal table.

**Signs you are testing arithmetic, not rules:**
- Every row has the same decision outcome but different numbers
- The table is testing that `a × b = c` for various values of a and b
- Removing rows would not lose any rule coverage

When arithmetic is the only thing left to test, a small table (2–3 rows) verifying
the formula is sufficient. The bulk of the table rows should cover the domain rules.

### 7. Frame Stateful Features as Rules

When a feature involves state (carts, orders, workflows), frame each row as a
**state transition rule** — not a step in a sequential path:

| Scenario                | Playlist before      | Action           | Message?       | Playlist after?      |
|-------------------------|----------------------|------------------|----------------|----------------------|
| Add to empty playlist   | []                   | add "Hey Jude"   | Added          | [Hey Jude]           |
| Remove last song        | [Hey Jude]           | remove Hey Jude  | Removed        | []                   |
| Remove unknown song     | [Hey Jude]           | remove Yesterday | Not in list    | [Hey Jude]           |

Each row is independent: given this state, when this action happens, expect this result.
No row depends on a previous row's outcome. This matches TableTest's execution model
where rows run independently.

**Sequential paths** ("Step 1: add song, Step 2: add another, Step 3: shuffle") create
row dependencies and belong in `@Test` methods with `@DisplayName`, not in tables.

The distinction: tables define **what the rules are**; `@Test` methods verify **that
the rules compose correctly in a real scenario**.

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

## Example Table Design

### One Table per Concern

A single table should describe one decision, transformation, or rule. Signs that
you have two concerns mixed together:

- Some rows need columns that other rows leave blank throughout
- Scenario names require qualifiers like "...for eligibility" vs "...for pricing"
- You cannot name the table without using "and"

Split the table at that point. Let additional tables emerge naturally — do not
design them upfront.

### Use Business Language Throughout

Column headers and cell values should use the language of the domain. The test:
could a domain expert read the table and confirm or challenge every row without
asking what the column names mean?

| Good (Business Language) | Bad (Implementation Language)  |
|--------------------------|-------------------------------|
| `Customer Age`           | `age`, `userAge`, `int age`   |
| `Has Licence`            | `licencePresent`, `boolean`   |
| `Eligible?`              | `isEligible`, `result`        |
| `Rejection Reason?`      | `errorCode`, `exception`      |
| `Economy`, `Premium`     | `CATEGORY_A`, `CATEGORY_B`    |
| `30 days`                | `MEDIUM`, `2`                 |

Business language is stable. Technical terms change as the implementation evolves.

### Use Concrete Domain Values

Prefer real, recognisable values over abstract codes and flags:

| Good (Concrete)      | Bad (Abstract)          |
|----------------------|-------------------------|
| `Economy`, `Premium` | `true`, `false`         |
| `No licence`         | `REJECT_1`              |
| `EUR`                | `1`                     |
| `30 days`            | `MEDIUM`                |
| `2026-01-15`         | `some date`             |

If you find yourself writing abstract placeholders, ask: "What would this actually
be in a real case?" Use that answer.

### Make Outputs Traceable

Where possible, output values should be derivable from the input values by a reader
with no additional knowledge:

| Scenario               | Base Price | Discount | Final Price? |
|------------------------|------------|----------|--------------|
| 10% loyalty discount   | 100        | 10%      | 90           |
| 20% bulk order         | 200        | 20%      | 160          |

`90` is traceable: `100 − 10% = 90`. Prefer this over a symbolic placeholder like
`discounted`. When an output is not mathematically derivable, make it a real domain
value the reader recognises (`Economy`, `Rejected`, `EUR 99.00`).

### Make Thresholds and Limits Visible

When a rule depends on a threshold or limit, include it as a column — even when its
value is constant across every row of the table.

Without a `Max Age (Policy)` column:

| Scenario            | Customer Age | Eligible? | Rejection Reason? |
|---------------------|--------------|-----------|-------------------|
| Standard customer   | 30           | yes       |                   |
| Over age limit      | 80           | no        | Over age 75       |
| At the limit        | 75           | yes       |                   |
| Just over the limit | 76           | no        | Over age 75       |

The number 75 is buried in the `Rejection Reason?` column — or in the code. A
reader cannot tell from the table where the threshold is, or that the rule is
strictly greater than rather than greater than or equal to.

With the threshold explicit:

| Scenario            | Customer Age | Max Age (Policy) | Eligible? | Rejection Reason? |
|---------------------|--------------|-----------------|-----------|-------------------|
| Standard customer   | 30           | 75              | yes       |                   |
| Over age limit      | 80           | 75              | no        | Over age 75       |
| At the limit        | 75           | 75              | yes       |                   |
| Just over the limit | 76           | 75              | no        | Over age 75       |

The rule is now legible from the table alone. The boundary rows (75 and 76) also
become natural to add once the threshold column is visible.

**A constant column often signals configuration, not code.** When a column holds
the same value in every row, ask: "Under what circumstances would this value
differ?" The answer often reveals a second axis — the limit might vary by car
category, by hire company, or by market. That variation may become new rows in the
same table, or it may surface a separate concern that belongs in its own table:

| Scenario                     | Customer Age | Car Category | Max Age (Policy) | Eligible? |
|------------------------------|--------------|--------------|-----------------|-----------|
| Economy, within limit        | 30           | Economy      | 75              | yes       |
| Economy, over limit          | 80           | Economy      | 75              | no        |
| Premium allows older drivers | 80           | Premium      | 85              | yes       |
| Premium, over extended limit | 90           | Premium      | 85              | no        |

**Different columns can live at different levels of change.** Customer age is per
transaction. The max age policy might be reviewed annually. Limits per car category
might be set per product launch. Recognising that `Max Age (Policy)` is a policy
setting — not a customer attribute — communicates where the value comes from and at
what pace it changes. Policy limits typically live in configuration rather than in
the incoming request, and they may need to be loaded or injected differently from
transactional inputs.

In an example table, naming the layer — `Max Age (Policy)` rather than just
`Max Age` — signals to the implementer that this is a configurable threshold, not
a constant to be hardcoded. When columns at different levels of change accumulate,
that is also a signal to consider splitting into separate tables — one for the
transactional concern, one for the policy or configuration (see "Multiple Tables").

### Name Scenarios as Conditions

Scenario names describe **when** — the conditions under test — not **then** — the
expected outcome:

| Good (Condition)                 | Bad (Outcome)              |
|----------------------------------|----------------------------|
| `Underage customer`              | `Rejected`                 |
| `Premium car, driver under 25`   | `Not eligible`             |
| `Full licence holder`            | `Approved`                 |
| `Missing licence information`    | `Error case`               |
| `Divisible by 4 but not 100`     | `Is a leap year`           |

Good scenario names read as failure messages: "Underage customer — row 3 failed."
They tell you exactly what to investigate.

### Leave Blanks Intentionally

Blank cells and value sets mean different things — using the wrong one hides information. A blank means "this field is absent"; a value set like `{yes, no}` means "this field exists but doesn't affect the outcome". Mixing them up creates tables that silently mislead readers about what the rule actually is.

Use blank cells for values that are genuinely absent, not for values that exist but
happen not to be the deciding factor in a row.

**Blank output cells** — when an output does not apply in a scenario:

| Scenario                    | Customer Age | Has Licence | Car Category | Eligible? | Rejection Reason? |
|-----------------------------|--------------|-------------|--------------|-----------|-------------------|
| Standard adult customer     | 30           | yes         | Economy      | yes       |                   |
| Underage applicant          | 17           | no          | Economy      | no        | Under 18          |
| No driving licence          | 25           | no          | Economy      | no        | No licence        |

`Rejection Reason?` is blank for the approved row because there is no rejection
reason — the value is genuinely absent, not merely irrelevant.

**Blank input cells** — when an optional field is not provided:

| Scenario                   | Base Price | Promo Code | Final Price? |
|----------------------------|------------|------------|--------------|
| No promotion applied       | 100        |            | 100          |
| With 10% promo code        | 100        | SAVE10     | 90           |

`Promo Code` is blank when the customer provides none — the field is optional and
genuinely not present.

**Blank input cells for optional or defaulting inputs** — when an input exists but
defaults to zero (or another baseline) and is not relevant to a particular scenario,
use a blank cell to signal "not part of this scenario":

| Scenario              | Base fare | Peak surcharge | Airport fee | Total fare? |
|-----------------------|-----------|---------------|-------------|-------------|
| Off-peak city ride    | 12.00     |               |             | 12.00       |
| Peak-hour city ride   | 12.00     | 5.00          |             | 17.00       |
| Airport pickup        | 12.00     |               | 8.00        | 20.00       |
| Peak airport pickup   | 12.00     | 5.00          | 8.00        | 25.00       |

The blank cells for Peak surcharge and Airport fee make it immediately clear which
cost components apply to each scenario. Filling them with `0` obscures this — the
reader must scan every cell to understand the scenario. In a `@TableTest`, blank
cells translate to `null`; the test method handles null-to-default conversion.

**Use value sets, not blanks, for "regardless of" relationships.** When an input
exists but simply does not affect the outcome of a particular row, show that
explicitly with multiple values rather than hiding it behind a blank:

| Scenario                              | Customer Age | Has Licence   | Car Category | Eligible? | Rejection Reason? |
|---------------------------------------|--------------|---------------|--------------|-----------|-------------------|
| Underage regardless of licence status | 17           | {yes, no}     | Economy      | no        | Under 18          |
| Over age limit regardless of category | 80           | yes           | {Economy, Premium} | no  | Over age 75       |

A blank would wrongly suggest the field is absent. The value set makes the claim
explicit: "this rule holds for all values of this input."

Do not fill genuinely blank cells with filler values like `N/A` or `none` unless
those are real domain values.

---

## Multiple Tables

A feature often needs more than one table. Common splits:

**Different outcome types**: One table for what the system decides, another for
what it stores, another for what it returns to the caller.

**Different logical concerns**: "Who is eligible to rent" and "what they are charged"
are separate concerns even when they involve the same customer data.

**Different input populations**: If happy-path examples and error/rejection examples
need different columns, they may belong in separate tables.

**Different levels of change**: When some columns change per transaction and others
change per policy review or configuration cycle, the data belongs to different
concerns. A table for customer eligibility requests and a table for the rental
policies that govern them live at different tempos — mixing them inflates the
transactional table with rows that only change when someone updates a policy.
The signal is a constant-valued column that, on reflection, comes from a different
source than the rest of the inputs (see "Make Thresholds and Limits Visible").

Start with the table that is clearest and most central. Let additional tables emerge
as rows that do not fit appear — that is the signal.

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
| Business-language column names   | Kept as-is or refined during the refine phase |

A few things to keep in mind for the handoff:
- Column names should stay close to business language; avoid reverting to technical terms during translation
- Custom types and enums will need type converters — `/tabletest` handles this
- Open cells marked `?` become the first decisions to resolve during implementation

The example table is not a throwaway artefact — it is the first draft of the living specification. Treat it accordingly.

---

## Quality Checks

Before handing off to implementation, verify the example table:

- [ ] **Named concern**: The table has a clear name describing the single behaviour it specifies
- [ ] **Multiple rows**: At least 2–3 rows — enough to reveal the column structure and decision boundaries
- [ ] **Business language**: All column headers use domain terms, not implementation terms
- [ ] **Concrete values**: Cell values are recognisable domain data, not abstract codes or boolean flags
- [ ] **Traceable outputs**: Expected output values are derivable from input values where possible
- [ ] **Conditions as scenarios**: Scenario names describe the situation, not the expected result
- [ ] **Domain-expert readable**: A domain expert could confirm or challenge every row without reading code
- [ ] **One concern per table**: All rows belong to the same decision, transformation, or rule
- [ ] **Edge cases considered**: Boundary values, absent inputs, and rejection cases are represented
- [ ] **Open questions noted**: Uncertain cells or unresolved decisions are marked, not silently left blank
- [ ] **Irrelevant inputs shown**: When a rule holds regardless of an input, that is expressed with multiple values in one cell, not omitted
