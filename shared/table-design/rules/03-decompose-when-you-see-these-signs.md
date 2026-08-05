### Decompose When You See These Signs

*One Rule, One Axis* gives the first test — a behaviour you cannot name without "and" is two
concerns. These are the signs that show up later, once the table exists:

- Some {{rows}} need columns that other {{rows}} leave {{unused_cell}}.
- {{Names}} need qualifiers — "…for eligibility" against "…for pricing".
- The table has two groups of expectation columns that never both apply in the same {{row}}.

**Missing concern:** an input to one rule is itself derived from raw data. The derivation has its own
edge cases and needs boundary {{rows}} of its own. The rule's table then takes the *derived value* as
a direct input column, not the raw data. Two tables, not one.

**Ask of every input column where its value comes from.** Either it arrives from outside, or a rule
computes it — and a rule that computes it is a table you have not written yet. Two shapes say you
skipped it, and they look nothing alike: **the raw data is a column and the derived value is nowhere**,
so the derivation happens inside the {{rows}} where no {{row}} can put a boundary on it; or **both are
columns of the same table**, so the derived one restates a value already present and the rule
connecting them is legible only by reading the {{rows}} against each other. Split either way — the
deriving rule takes the raw data and reports the value, and this table takes that value as an input
column and never sees the raw data. **That the value must be visible is not the question**; which
table it is a column *of* is.

**A column {{unused_cell}} for most of its {{rows}} is a column decision before it is a table
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
{{row}} exists, which is why this is decided when you name the table.

**Members of a family compute differently, and that is not a reason to split.** One adjustment is a
flat reduction, another a percentage, another a recalculation. The differing computation is what the
{{rows}} show; it is not what makes them separate tables.

**Collapsing a family means one table, not necessarily one column.** Where the members arrive as
*separate inputs* the system reads independently, a single column keyed by member cannot feed them —
routing one value to the right input would put a decision in the test itself, which is never the
answer. **Give each member its own column in the one table, and leave it {{unused_cell}} on the
{{rows}} where that member does not apply.** The family is still stated as one rule, the members
still sit side by side, and the sparse columns are what shows which member each {{row}} exercises.
Reach for the keyed column when the members are values one input takes; reach for a column each when
they are inputs of their own. **Splitting into a table per member is the wrong answer in both
cases** — and it is the tempting one, because it needs no decision.

**Collapse on a family, never on a bag.** A family is a domain category, not "everything that affects
the answer". The check: the family name works as a column header with the members as its values.
Where no such name exists the tables are genuinely distinct and belong apart — and so they do where
collapsing would cross-multiply, or leave {{rows}} whose purpose is no longer legible.

{{example}}

**Check:** **Decomposed, not over-split**: no table mixes concerns (blank-throughout columns, qualified {{names}}, two groups of expectation columns), no table carries both a value and the raw data another rule derives it from, and no set of same-fixture tables reports one expectation column that a family column would collapse
