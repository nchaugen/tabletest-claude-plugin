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

**The test is whether the rule can be *stated* about each condition alone — not whether the inputs
are separate.** Several inputs that are each a *contribution to one answer* are one rule, however
separately they arrive: quantities that are weighted and summed, amounts that accumulate into a
total, parts that combine into a whole. There is no claim to make about one of them by itself,
because the answer is the combination. Splitting those gives one table per input, each holding the
others at nothing, and **no table then shows them combining — which is the only interesting case**.
Keep them in one table with a column each, and let some of its {{rows}} carry several contributions
at once. Those {{rows}} belong to that table, which owns the combining rule; they are not a second
table run end to end — see *A Combining Table Needs Its Own Rule*.

Hold the inputs belonging to *other* concerns at one obviously-valid value. An input **this** rule
claims not to affect the outcome is the opposite situation and has to vary — see *Show That an
Input Does Not Change the Outcome*.

Separate tables reduce {{rows}} by avoiding unnecessary permutations, and the table count guides the
implementation: five concern tables suggest five functions.

{{example}}

**Check:** **One rule per table**: every {{row}} and column serves this table's one axis; a behaviour you cannot name without "and" has been split, and a rule that is a conjunction of independent conditions has one table per condition rather than their cross-product — but inputs that are contributions to one combined answer stay in one table, with {{rows}} that show them combining
