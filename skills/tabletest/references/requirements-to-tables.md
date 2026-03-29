# From Requirements to Tables

Use this reference when writing tests from natural-language requirements, vague
feature descriptions, or when it is not clear how to decompose the behaviour
into tables. The workflow below helps you derive table structure from the
requirements before writing any `@TableTest` code.

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

**Value sets for tier grouping:** When multiple input values produce the same
output, group them into a value set rather than enumerating each as a separate
row:

```
Scenario        | Tickets in 30 days | Discount?
First tier      | {5, 6, 7, 8, 9}   | 5%
Second tier     | {10, 11, 12, 13, 14} | 10%
```

This expresses the tier structure directly. Enumerating boundaries as separate
rows (5 → 5%, 9 → 5%, 10 → 10%) tests the same thing with more noise and less
clarity about the tier grouping.

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

## 8. Mark What Is Still Open

Not everything needs to be resolved before coding. Mark uncertain cells or add a
notes column for open questions:

```
Scenario           | Customer Age | Has Licence | Eligible? | Open Questions
Senior age limit?  | 75           | yes         | ?         | Is there a max age?
```

Open cells signal decisions that need resolving — through conversation, a domain
expert, or implementation exploration. They are not failures; they are honest
about what is known and unknown.

**Do not silently resolve ambiguities.** When two interpretations of a rule are
plausible, surface both as an open question rather than picking one. The table
should expose the decision, not hide it.

---

## Checklist Before Writing Code

After working through the steps above, verify:

- [ ] Each concern has its own table with a clear verb-phrase name
- [ ] Boundary values are tested at the threshold (not just mid-range values)
- [ ] Value sets group same-outcome values where applicable
- [ ] Rules and arithmetic are in separate tables
- [ ] Stateful features use before/action/after columns
- [ ] Policy thresholds are visible as columns
- [ ] Open questions are marked, not silently resolved
- [ ] Each row is independently executable — no row depends on a prior row

Once the table structure is clear, implement it as `@TableTest` code following
the main skill's Table Design and Quality Checks sections.
