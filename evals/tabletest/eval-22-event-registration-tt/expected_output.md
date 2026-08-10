Two @TableTest methods, one per concern: form acceptance (validation) and pricing/discount. The
validation table varies name, email, and the two optional fields in their own columns, with blank
cells for the absent optionals and output columns asserting acceptance and, preferably, the reason a
rejected registration gives. The pricing table varies
registration date (descriptive values via a @TypeConverter) and group size, with a Discount? output
(and optionally a per-attendee Price?). Column names are business language; each method applies one
uniform assertion to every row; scenario names and @Description restate nothing the columns show.

## The two concerns

`register(name, email, dietaryRequirements, accessibilityNeeds, registrationDate, groupSize)` mixes
two unrelated decisions, and the substance of this eval is pulling them apart:

| Concern | Governs | Irrelevant to it |
|---|---|---|
| **Validation** | whether the registration is accepted at all | date, group size, price |
| **Pricing / discount** | the discount and resulting price, *given* a valid registration | name, email, dietary, accessibility |

They belong in separate `@TableTest` methods. Cross-multiplying them (valid/invalid × every
date/group combination) is the anti-pattern the row-count assertions guard against.

## Modelling decisions this eval fixes

- **Per-attendee price, not a group total.** The interface conflates one attendee's registration
  with the size of the group they register within. The reading that keeps the table honest:
  `groupSize` is the size of the group this attendee is part of, and it only *informs the discount*
  for this attendee. Price and discount are therefore **per attendee** — no multiplication by group
  size. `Discount?` is the rule's direct output; a `Price per attendee?` column may accompany it.
- **Base price £100 is a fixed constant**, stated once in the `@Description`, not a column (it never
  varies). Everything the rule *does* — the discount — is in the rows.
- **Relative dates, not literals.** The prompt's cutoff (`2025-03-01`) is policy, not test data. Rows
  use `before cutoff` / `on cutoff` / `after cutoff` through a `@TypeConverter`; the reader never
  needs to know the literal date to predict the outcome, and `on cutoff` proves the boundary is
  exclusive (`before` means strictly before).
- **Optional fields are separate columns**, not a map. There are only two, so two columns keep the
  table narrow; a blank cell is the null (absent) value — never `N/A` or `none`.

## Reference decomposition

**Validation** — name, email, and both optionals in columns; date and group size are irrelevant, so
they are fixed at valid values in the method body rather than shown. One assertion: `Accepted?`.

| Scenario | Name | Email | Dietary requirements | Accessibility needs | Accepted? | Error message? |
|---|---|---|---|---|---|---|
| All fields provided | Ann | ann@example.com | Vegan | Wheelchair | true | |
| Optionals absent | Ben | ben@example.com | | | true | |
| One optional only | Cara | cara@example.com | Halal | | true | |
| Invalid email — no @ | Dan | dan.example.com | | | false | Email format is invalid |
| Invalid email — no domain | Eve | eve@ | | | false | Email format is invalid |
| Missing name | | fay@example.com | | | false | Name is required |

The first three rows carry the point that the optionals are genuinely optional: present, absent, and
half-present all accepted. The blank cells are the absent optionals (null), and are the reason the
`blank-for-absent-optional` and `validation-includes-optional-fields` assertions exist.

**The `Error message?` column is the same blank notation doing a second job**: blank on every
accepted row means no message, and the two rejected rules carry different text while the two
invalid-email rows share it. That is what stops the column being a restatement of `Accepted?`. The
exact wording is latitude — any consistent phrasing passes, and asserting acceptance alone is
acceptable. Note also that a blank `Name` cell and a blank optional cell mean the same thing (not
supplied) and land on opposite verdicts, which is the table's sharpest single property.

**Pricing** — a valid name/email fixed in the body; dietary/accessibility irrelevant to price and
omitted; date and group size in columns. One assertion pattern (`Discount?`, optionally `Price?`):

| Scenario | Registration date | Group size | Discount? | Price per attendee? |
|---|---|---|---|---|
| Early-bird, individual | before cutoff | 1 | 20% | £80 |
| Standard, individual | after cutoff | 1 | 0% | £100 |
| On cutoff — not early-bird | on cutoff | 1 | 0% | £100 |
| Group rate | after cutoff | 5 | 15% | £85 |
| Below group threshold | after cutoff | 4 | 0% | £100 |
| Both apply — higher wins | before cutoff | 5 | 20% | £80 |

Arithmetic (base £100): early-bird 20% → £80; group 15% → £85; neither → £100; both → the higher
20% → £80 (the discounts do not stack). The `group size 4` row proves the ≥5 threshold; the
`on cutoff` row proves the exclusive boundary; the `both apply` row proves the max-not-sum rule.

## Underspecified → make it an explicit row

- **Both discounts eligible.** The prompt says only the higher is used; the `Both apply` row commits
  to 20% (early-bird) beating 15% (group) and lets a reviewer challenge it.
- **The boundary date.** "before 2025-03-01" is read as strictly before; the `on cutoff` row makes
  that explicit rather than leaving the reader to guess whether the cutoff day is included.
- **Error message text is unspecified, so committing to wording is one of the readings this eval
  asks for.** The prompt fixes that a rejected registration carries a message and never says what it
  says. A table that publishes the reason — one message per rule, blank on every accepted row — is
  the preferred answer: it tells the two rejection rules apart, where a boolean `Error Reported?` is
  the exact complement of `Accepted?` and states nothing the acceptance column has not. The wording
  is an assumption, and naming it as one in the `@Description` is what makes it reviewable. Asserting
  only acceptance, or only that *some* message is present, is weaker but not wrong.
  **What is wrong is inventing a reason the rules do not distinguish** — two different messages for
  the same rule, or a message naming a field the prompt never validates.

## Out of scope, but foreseeable

- **Group-total price.** If a solution instead models price as `groupSize × £100 × (1 − discount)`,
  that is a defensible alternative reading — acceptable only if the column name makes the total
  explicit. It is not the rewarded reading (this eval is not about multiplication), but it is not a
  bug.
- **Email-format depth.** How strict "valid email format" is (sub-addressing, unicode, multiple dots)
  is unspecified; two representative invalids (no `@`, no domain) are enough. Enumerating an RFC is
  neither expected nor rewarded.

## Judging

Score the decomposition first: two methods, validation and pricing separated, each with one uniform
assertion and only the rows its concern needs. Then the representation choices the assertions
encode: blank = absent optional, descriptive dates, `Discount?` as the falsifiable output,
business-language columns, and scenario names / descriptions that add context rather than echo cells.
Whether a per-attendee `Price?` column accompanies `Discount?` is latitude; a group-total reading is
tolerated if labelled, not preferred. Whether the validation table publishes the rejection reason is
also latitude — preferred, not required — and the wording it chooses is never scored.
