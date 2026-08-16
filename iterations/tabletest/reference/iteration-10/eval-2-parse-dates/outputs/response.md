Hand-authored reference answer for eval-2, from
`products/claude-plugin/plans/slice-10-reference-answers-remaining-evals.md` section Findings.
Not agent output. Must never be read as a skill measurement.

Two `@TableTest` methods — the formats that parse, and the input that is rejected. Compiled and run
against the real scaffolding: 7 cases, all failing against the unimplemented stub, which is what the
prompt asks for.

**The century window is a row, not a sentence.** The requirement fixes `24` as 2024 and says nothing
about any other two-digit year, so `99-01-15 -> 2099-01-15` sits directly beneath the `24` row: one
input cell apart, so the rule the pair states is the century, and one cell for a reviewer who wants
1999 to change. Every stored solution that scored full marks stated this choice in an
`@Description` and left it untested.

**The first three rows carry one date written three ways.** Format equivalence is the rule, and
three rows landing on the same `LocalDate` state it in a way three unrelated dates cannot.

**Absent input and unparseable input are different rows in different tables.** A blank cell is null
and returns null; `''` is an empty string and is rejected. The second rejection row settles an open
point — text in none of the three formats is rejected rather than returned as a null the caller
cannot tell from an absent input.

`assertThrows` takes the expected type straight from the column, so no helper stands between the
table and the assertion.
