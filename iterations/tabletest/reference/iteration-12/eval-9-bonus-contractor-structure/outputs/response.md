Hand-authored reference answer for eval-9, from
`products/claude-plugin/plans/slice-10-reference-answers-remaining-evals.md` section Findings.
Not agent output. Must never be read as a skill measurement.

One `@TableTest`, five rows. Compiled and run: 6 cases, all failing against the unimplemented stub.

**This answer is very close to the stored ones, and that is the result.** eval-9's domain is a closed
6-cell product of two enums with a rate stated for every cell and nothing left open, so a correct
answer is nearly unique: four rows for the four distinct outcomes, one value-set row for the level
whose outcome does not read the department. Both stored solutions reach it.

The one deliberate difference is the `@Description`. It carries only what the table cannot show —
that the rate is a whole percentage of base salary with no salary in scope, and why level and
department are separate columns rather than one `Employee` cell (a value set varies a whole cell, so
the contractor claim needs department in a cell of its own). It restates none of the rates, which
eval-9's ground truth calls the failure mode of `description-if-present-adds-information`.
