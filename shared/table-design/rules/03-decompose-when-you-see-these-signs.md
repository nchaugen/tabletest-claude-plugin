### Decompose When You See These Signs

*One Rule, One Axis* gives the first test — a behaviour you cannot name without "and" is two
concerns. These are the signs that show up later, once the table exists:

- Some {{rows}} need columns that other {{rows}} leave {{unused_cell}}.
- {{Names}} need qualifiers — "…for eligibility" against "…for pricing".
- The table has two groups of expectation columns that never both apply in the same {{row}}.

**Missing concern:** an input to one rule is itself derived from raw data. The derivation has its own
edge cases and needs boundary {{rows}} of its own. The rule's table then takes the *derived value* as
a direct input column, not the raw data. Two tables, not one.

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

**Collapse on a family, never on a bag.** A family is a domain category, not "everything that affects
the answer". The check: the family name works as a column header with the members as its values.
Where no such name exists the tables are genuinely distinct and belong apart — and so they do where
collapsing would cross-multiply, or leave {{rows}} whose purpose is no longer legible.

{{example}}

**Check:** **Decomposed, not over-split**: no table mixes concerns (blank-throughout columns, qualified {{names}}, two groups of expectation columns), and no set of same-fixture tables reports one expectation column that a family column would collapse
