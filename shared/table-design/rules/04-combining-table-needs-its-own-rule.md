### A Combining Table Needs Its Own Rule

Once every rule has a table, the pull is to add one more that runs the whole feature end to end. It
re-proves what the single-rule tables already established, and it reads as redundant however clean
those tables are.

**A table that combines concerns earns its place only where the combination behaves in a way neither
concern shows alone** — a precedence, an ordering, an interaction whose result neither parent table
produces — and then it carries only the {{rows}} that show it. A table proving that a weight-based
dose is computed *before* the daily maximum caps it is a real table: the question is which rule
applies first, and its expected values appear in no other table. A table whose {{rows}} re-run each
dose band through the public entry point is not.

**Collapsing several same-fixture tables into one is not a combining table**, and *Decompose When You
See These Signs* requires it. The difference is what the merged table states: a family table states
one rule with its members as a column, while a combining table re-runs rules other tables have
already established. The first has a rule of its own; the second is a second pass over the ladder.

Two symptoms:

- **The description gives it away.** If the description you would write is "end-to-end scenarios
  combining the rules from the tables above", the table has no rule of its own. Delete it.
- **Wiring is not a rule.** Reaching a rule through the public API rather than the unit under test
  does not make it a new rule. If the wiring genuinely needs showing, that is one {{row}}, not a
  second pass over the ladder.

**Salvage its {{rows}} before you delete it — and then delete it.** One or two {{rows}} of an
end-to-end table often reach a case no single-rule table does. Deleting the table takes those with it
and nothing reports the loss, so list the obligations only its {{rows}} discharge and move each into
the table that owns its rule.

**This is a salvage step, not a reprieve — no outcome of it keeps the table.** A {{row}} worth
keeping is worth keeping *somewhere else*. Nor does shrinking the table save it: a single test that
runs the whole feature to re-prove one already-proven total is the same combining table with fewer
{{rows}}.

{{example}}

**Check:** **Combining tables prove an interaction**: any table exercising several rules together shows behaviour the single-rule tables cannot (a precedence, an ordering), not the earlier rules re-run end to end
