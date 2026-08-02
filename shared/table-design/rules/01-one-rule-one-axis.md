### One Rule, One Axis

A table is one rule varying along one axis. The axis is what the {{rows}} change; everything else is
either held constant or collapsed into a value set. Most decomposition questions are that one
question asked again — *what is this table's axis, and does every column and {{row}} serve it?*

**If you cannot name a behaviour without using "and", it is two concerns.** Split them, and give each
its own table.

**The naming test passes on a conjunction, and a conjunction is still several rules.** A rule of the
form *"X holds only if C1 and C2 and C3"*, where the conditions do not mention one another, names
cleanly in one breath — "sorts waste into bins" — while being three independent claims. Give
each condition its own table, holding the others satisfied. Crossing them instead multiplies {{rows}}
without adding a claim, and no {{row}} then isolates the condition it was meant to show.

Hold the inputs belonging to *other* concerns at one obviously-valid value. An input **this** rule
claims not to affect the outcome is the opposite situation and has to vary — see *Value Sets for
"Regardless Of" Relationships*.

Separate tables reduce {{rows}} by avoiding unnecessary permutations, and the table count guides the
implementation: five concern tables suggest five functions.

{{example}}

**Check:** **One rule per table**: every {{row}} and column serves this table's one axis; a behaviour you cannot name without "and" has been split, and a rule that is a conjunction of independent conditions has one table per condition rather than their cross-product
