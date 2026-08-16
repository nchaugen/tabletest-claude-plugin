### Give Each Obligation Exactly One {{Row}}

**The obligation list comes from the inputs, and producing it is the step most often skipped.**
Before counting {{rows}}, take each input the rule reads and ask three questions of it. Where an
answer is not obvious from the requirement, it names {{rows}} nothing else will.

- **Counted, or read in bands?** A rule reading an input in bands gives two different values the same
  answer, and the only way to state that is two {{rows}} differing in that input and agreeing in the
  expectation. A table whose every value of it carries a different expectation has stated "the answer
  rises with this input", which is a different rule from the one you meant.
- **Per unit of it, or once for having any?** Two {{rows}} — none and one — show a difference and are
  equally consistent with both readings, because a flat charge for having any at all is an ordinary
  rule. **A third consecutive value is what decides between them.** Without it the table leaves
  ambiguous the very rule it was written to state.
- **Does its effect depend on another input?** Where it does, the pair showing that effect has to
  appear on **both sides** of the other input's boundary. One pair, however well chosen, states an
  effect that is wrong wherever the other input differs.

Answer all three before writing {{rows}}. The questions cost nothing, and the {{rows}} they produce
are exactly the ones a reader cannot infer from the others.

The right number of {{rows}} is a covering problem. List the concern's **obligations** — the distinct
behaviours the rule must demonstrate — then write the smallest set of {{rows}} that covers all of
them. Both errors are real and they do not read alike: a missing obligation lets a wrong
implementation pass, while a repeated one costs the reader time and suggests a distinction that is
not there.

**The test for a redundant {{row}}, and it is decidable inside the table in front of you: where two
{{rows}} share an expectation, ask whether swapping one's differing input for the other's would
change an expectation cell *in this table*.** If it would not, they are one {{row}} — and a value set
is how you say so.

**"Exactly one" is a floor as well as a ceiling, and consolidating is where the floor gets broken.**
Trimming a table is the moment to re-read the obligation list, because the {{rows}} that look most
redundant are often the ones carrying an obligation of their own. Three shapes account for nearly
every obligation dropped that way:

- **A second input in a different *state*, mistaken for a larger value of the same one.** Acting on
  something already populated is not a bigger version of acting on something fresh — it is the case
  where existing content has to survive, and nothing else shows it.
- **The transition that empties or fills.** Removing the last member, filling the final slot: the
  {{row}} looks like the ordinary case with smaller numbers, and it is the only one that reaches the
  boundary of the container.
- **A distinct branch that shares its expectation with a neighbour.** Two {{rows}} agreeing on the
  answer are not redundant when they reach it by different routes — but **the routes have to differ
  in what this table expects, not in what its rule mentions**, which is what the test above decides.
  **A value the rule names is not thereby a branch:** enumerating the members is how a rule gets
  stated, and the table's job is to show which of them the answer turns on. Kinds of a thing that
  another rule tells apart are the usual false positive: three {{rows}} for three kinds, where the
  rule under test reads only whether the thing was valid.
  **Collapsing means the value set, not the delete key.** Put every kind in the surviving cell —
  `{percentage, fixed, product-specific}` — because the description will still claim the kind makes
  no difference, and deleting the {{rows}} leaves that claim with nothing behind it.

When you cut a {{row}}, say which surviving {{row}} discharges its obligation. If none does, keep it —
but **a value set discharges every obligation its members carried**, because it expands into one case
per value. Collapsing {{rows}} into a value set is not cutting them, and the floor is not in play.

**Two closed sets of inputs are where the floor gets misread.** With m values of one input and n of
another, every one of the m×n combinations is a case the rule names, so every one looks like an
obligation of its own and the {{rows}} grow to the full cross-product. The obligations are the
distinct *answers*, not the combinations: group the combinations that share an expectation, give
each group one {{row}}, and let the value sets carry the members. This is a {{row}} count, not a
table count — one rule still means one table, however its inputs multiply.

And three shapes account for nearly every genuinely redundant {{row}}:

- **Further past the same boundary.** A pair that *straddles* a boundary earns both its {{rows}}: the
  outcomes differ, and that is the rule. A second {{row}} on the same side does not, and the same
  holds for rejections — one {{row}} just past a limit rejects, and a {{row}} further past it rejects
  for no new reason. It earns its place only where the point *is* that two inputs collapse to one
  behaviour, and then a value set says that in one {{row}}; keep two and the {{names}} have to carry
  why.
- **A larger n in the same direction.** If two incompatible items force a batch into separate streams,
  three incompatible items force it for the same reason. One obligation, one {{row}}.
- **A value the rule ignores.** The redundancy test above, applied directly: one {{row}} carrying a
  value set. **Where the value that differs sits inside a composite cell, the collapse needs the
  column reshaped first** — a value set varies a whole cell, never one part of one. Ask what else in
  that cell this table reads. If nothing does, the object does not belong in the cell and the value
  does: give it a column of its own and fix the object's other parts outside the table — bar any
  part a surface makes a claim about, which has to stay visible (*Assume the Table Is Published*).
  If other parts are read too, add a further element instead, so one {{row}} carries every state and
  the near-duplicate pair never arises.

**One value can carry two obligations, in two different tables.** A value that is a boundary for one
rule is often the subject of another. A zero duty period is both the accepted end of "duty hours
cannot be negative" *and* the input that should produce no rest requirement whatever the crew size —
two rules, two questions, two {{rows}} in two tables. Showing the value once, in whichever table you
reached first, feels like coverage and is not. **Count obligations per rule, never per value.**

{{example}}

**Check:** **One {{row}} per obligation**: every obligation of the concern is discharged by some {{row}}, and every {{row}} discharges one no other {{row}} in that table reaches; where two {{rows}} share an expectation, swapping what differs between them would change an expectation cell in that table — not a value further past the same boundary, a larger n in the same direction, or an input the rule ignores even though it names it
