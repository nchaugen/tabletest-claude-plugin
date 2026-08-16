Hand-authored reference answer for eval-7, from
`products/claude-plugin/plans/slice-10-reference-answers-remaining-evals.md` section Findings.
Not agent output. Must never be read as a skill measurement.

One `@TableTest`, five rows expanding to nine cases — every role/action pair — all failing against
the unimplemented stub, which is what the prompt asks for.

**Each role shows both where its permission holds and where it stops.** ADMIN has no denial, so it
is one all-true row; USER and GUEST each carry a true group and a false group. A table listing only
what each role can do never states where the rule ends, and a reader could not tell GUEST from
ADMIN.

Value sets group only actions that share an outcome for that role, so no two rows repeat a role with
the same verdict. No `@Description`: the prompt carries no context the table cannot show, so adding
one would restate the rows.

**The first draft of this answer failed `scenario-names-describe-conditions` and the grader was
right.** Its names read "User, the action it is refused" and "Guest, the actions it is refused",
which paraphrase the `Allowed?` cell of `false` beside them. The names now say which action the row
exercises and leave the verdict to its column.
