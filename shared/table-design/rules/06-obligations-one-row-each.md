## Give Each Obligation Exactly One {{Row}}

The right number of {{rows}} is a covering problem. List the concern's **obligations** — the distinct
behaviours the rule must demonstrate — then write the smallest set of {{rows}} that covers all of
them. Both errors are real and they do not read alike: a missing obligation lets a wrong
implementation pass, while a repeated one costs the reader time and suggests a distinction that is
not there.

**The test for a redundant {{row}}: if two {{rows}} share an expectation, the difference between them
must be the thing the rule is about.** If it is not, they are one {{row}} — and a value set is how you
say so.

Three shapes account for nearly every redundant {{row}}:

- **Further past the same boundary.** A pair that *straddles* a boundary earns both its {{rows}}: the
  outcomes differ, and that is the rule. A second {{row}} on the same side does not. This holds for
  rejections too — one {{row}} just past a limit rejects, and a {{row}} further past it rejects for no
  new reason.
- **A larger n in the same direction.** If two incompatible items force a batch into separate streams,
  three incompatible items force it for the same reason. One obligation, one {{row}}.
- **A value the rule ignores.** Two {{rows}} differing only in it are one {{row}}. Merge them with a
  value set: same outcome either way means the difference between the {{rows}} is not the rule.

A second {{row}} on the same side of a boundary earns its place in one case: when the point *is* that
two inputs collapse to one behaviour. Then say so — a value set says it in one {{row}}, and if you
keep two the {{names}} have to carry why.

**One value can carry two obligations, in two different tables.** A value that is a boundary for one
rule is often the subject of another. A zero duty period is both the accepted end of "duty hours
cannot be negative" *and* the input that should produce no rest requirement whatever the crew size —
two rules, two questions, two {{rows}} in two tables. Showing the value once, in whichever table you
reached first, feels like coverage and is not. **Count obligations per rule, never per value.**

{{example}}
