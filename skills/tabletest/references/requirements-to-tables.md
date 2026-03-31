# From Requirements to Tables

Use this reference when writing tests from natural-language requirements, vague
feature descriptions, or when it is not clear how to decompose the behaviour
into tables.

**Your deliverable is a Java test class with `@TableTest` methods.**
Do not output markdown tables. Do not stop for user feedback between steps.
Work through every step below, then write the Java class directly as your
final output.

Each step below is an analysis step that shapes the table structure. As you
work through them, you are building up a mental model of the columns, rows,
and concerns — but you do not output anything until step 9, where you write
the complete Java `@TableTest` class.

---

## 1. Name Each Concern

Start by identifying the distinct behaviours in the requirement. Name each one
as a verb phrase:

- "Calculate weekly pay"
- "Determine discount tier"
- "Check traveller eligibility"
- "Validate coupon"

**If you cannot name a behaviour without using "and", it is two concerns** —
split them. Each concern becomes a candidate for its own `@TableTest` method.

Signs that concerns are mixed:
- Some rows need columns that other rows leave blank throughout
- Scenario names require qualifiers like "...for eligibility" vs "...for pricing"
- The table has two groups of output columns that never both apply in the same row

**Signs of a missing concern:**
- An input to one rule is itself derived from raw data. The recognisable
  shape: a rule takes a value that is computed via a time window, rolling
  count, aggregation, or filter over raw records (e.g. "average response
  time over the last 7 days" feeds into an SLA compliance check). The
  derivation has its own edge cases — what falls inside vs outside the
  window, how the boundary instant is handled, what records are included —
  and these need boundary testing in a separate table. The rule table then
  takes the derived value as a direct input column, not the raw data.

  Two tables, not one: *Concern 1 — derive the value* (which raw records
  count, window boundaries). *Concern 2 — apply the rule* (given the
  derived value, what is the outcome?).

---

## 2. Find the First Example

For each concern, start with the simplest, most obvious case:

- What does a typical successful case look like?
- What are the concrete values — not abstractions, but real numbers, names, dates?

Write this as the first data row. Column naming can be rough at this stage.

---

## 3. Probe for Variations

Work through these systematically:

**Different outcomes** — what makes the decision go the other way?
- "What makes the answer change from yes to no?"
- "What other rules apply?"

**Boundary conditions** — where exactly do rules trigger?
- "At what exact value does this rule kick in?"
- Include rows at the threshold, just above, and just below
- When a boundary is ambiguous (inclusive vs exclusive?), add rows on both sides
  AND mark the expected output as an open question — the rows should exist even
  if the outcome is uncertain

**Special cases** — situations that may surprise:
- "Is there a case that surprises new team members?"
- "What is a common misunderstanding about this behaviour?"

**Absent inputs** — what happens when information is not provided?
- "What if this field is empty or not given?"
- "Is there a sensible default, or does absence cause rejection?"

---

## 4. Probe for Irrelevant Inputs

When one input clearly does not affect the outcome, use a value set to express
"regardless of":

```
Scenario                        | Customer Age | Car Category       | Eligible?
Underage regardless of category | 17           | {Economy, Premium} | no
```

This is more precise than a blank (which means absent) and more concise than
separate rows for each category.

Check every input dimension mentioned in the requirements. If the requirement
says "regardless of X", X must appear as a column with a value set — omitting
it silently hides the assertion that X is irrelevant.

**Common mistake — omitting the irrelevant column entirely.** If a requirement
says "regardless of membership level", the table must include a Membership
Level column with a value set like `{Gold, Silver, Bronze}`. Without it, a
reader cannot tell whether the irrelevance was tested or simply overlooked.
The column makes the "regardless of" assertion visible and verifiable.

**Value sets for tier grouping:** When multiple input values produce the same
output (a tier), group them into a value set rather than enumerating each as
a separate row:

```
Scenario   | Credit Hours         | Standing?
Freshman   | {0, 10, 20, 29}     | Freshman
Sophomore  | {30, 45, 59}        | Sophomore
Junior     | {60, 75, 89}        | Junior
Senior     | {90, 100, 120}      | Senior
```

This makes the tier structure a first-class concept — each row IS a tier. The
alternative — separate rows for boundaries (29 → Freshman, 30 → Sophomore) —
tests boundary detection but obscures which values belong to the same tier. Use
value sets for tier membership; add boundary rows (29 vs 30) only as additional
rows alongside the tier rows, not instead of them.

**Value sets for identical entity types:** When two categories follow
identical rules (e.g. `{Manager, Director}` both have the same expense
approval limit), express them as a value set in one row rather than separate
rows. This is more concise AND more expressive — it asserts the rules are
identical, whereas separate rows merely happen to show the same output.

---

## 5. Separate Rules from Arithmetic

Tables should specify the interesting decisions — classifications, eligibility
rules, tier lookups, state transitions — not test that multiplication works.

**Signs you are testing arithmetic, not rules:**
- Every row has the same decision outcome but different numbers
- The table is testing that `a * b = c` for various values of a and b
- Removing rows would not lose any rule coverage

When arithmetic is the only thing left to test, a small table (2-3 rows)
verifying the formula is sufficient. The bulk of the table rows should cover the
domain rules.

**Good decomposition** — separate the rule from the calculation:

Table 1 — the rule (which tier?):
```
Scenario     | Weight | Zone          | Category?  | Surcharge?
Light domestic | 0.5  | Domestic      | Standard   | none
Heavy domestic | 12   | Domestic      | Oversize   | 5.00
```

Table 2 — the arithmetic (what does it cost?):
```
Scenario    | Base rate | Surcharge | Total?
No surcharge | 10.00    |           | 10.00
With surcharge | 10.00  | 5.00      | 15.00
```

---

## 6. Frame Stateful Features as Rules

When a feature involves state (carts, orders, workflows), frame each row as a
state transition rule — not a step in a sequential path:

```
Scenario              | Cart before    | Action         | Cart after?    | Message?
Add to empty cart     | []             | add Widget     | [Widget x1]    | Added
Remove last item      | [Widget x1]    | remove Widget  | []             | Removed
Remove unknown item   | [Widget x1]    | remove Gadget  | [Widget x1]    | Not found
```

Each row is independent: given this state, when this action happens, expect this
result. No row depends on a previous row's outcome.

**Include before and after columns** — even when the prompt describes the
operation procedurally ("customers can enter a coupon code") rather than as a
state transition. Whenever an operation changes system state, the reader needs to
see what changed.

---

## 7. Make Thresholds Visible

When a rule depends on a threshold or limit, include it as a column — even when
the value is constant across every row:

```
Scenario            | Customer Age | Max Age (Policy) | Eligible?
Standard customer   | 30           | 75               | yes
At the limit        | 75           | 75               | yes
Just over the limit | 76           | 75               | no
```

A constant column often signals configuration. Ask: "Under what circumstances
would this value differ?" The answer may reveal a second axis (e.g., max age
varies by car category) that belongs as new rows or a separate table.

---

## 8. Note What Is Still Open

Not everything needs to be resolved before coding. Note open questions
internally — they will go in `@Description` annotations in the Java code
(step 9), not in a separate markdown section.

**Do not silently resolve ambiguities.** When two interpretations of a rule are
plausible, note both as an open question rather than picking one.

---

## 9. Write the `@TableTest` Code

This is the only step that produces output. Everything above was analysis.
Write the complete Java test class now — each concern becomes a `@TableTest`
method. Follow the main skill's Table Design section for syntax, and its
Quality Checks for verification.

For each `@TableTest` method:
- Add `@DisplayName` with a clear title derived from the concern name (step 1)
- Add `@Description` with context the table alone cannot convey — fixed values,
  formulas, domain context, or open questions from step 8
- Use `@TypeConverter` methods for any human-readable values that need conversion
- Follow annotation order: `@DisplayName` → `@Description` → `@TableTest`

**Do not present markdown tables for review.** Do not add a notes section or
open questions list after the code. Go directly to the Java code. Open
questions from step 8 belong in `@Description` annotations inside the code.

### Final checklist

- [ ] Each concern has its own `@TableTest` method with a clear name
- [ ] Boundary values are tested at the threshold (not just mid-range values)
- [ ] Value sets group same-outcome values where applicable
- [ ] Rules and arithmetic are in separate `@TableTest` methods
- [ ] Stateful features use before/action/after columns
- [ ] Policy thresholds are visible as columns
- [ ] Open questions are in `@Description`, not silently resolved
- [ ] Each row is independently executable — no row depends on a prior row
- [ ] `@DisplayName`, `@Description`, `@TableTest` annotations in correct order
- [ ] `@TypeConverter` methods handle any non-standard type conversions
