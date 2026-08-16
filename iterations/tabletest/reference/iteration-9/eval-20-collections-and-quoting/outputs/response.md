Hand-authored reference answer for eval-20, from
`products/claude-plugin/plans/slice-10-reference-answers-remaining-evals.md` section Findings.
Not agent output. Must never be read as a skill measurement.

Three `@TableTest` methods — category prefixes, the optional set, and tag text against the table's
own punctuation. Compiled and run against the real scaffolding: 18 cases, 17 failing against the
stub and one (`[] → []`) vacuously passing, which is what the prompt asks for — tests before the
implementation.

**The stub lives in the test file.** Every stored solution refers to a `TagFilterImpl` that the
run's stored `outputs/` does not contain, because the runner snapshots `src/test` and the build file
only. A package-private `StubTagFilter` beside the test class makes the artefact self-contained and
keeps the unwritten filter out of `src/main`, where a later implementation would collide with it.

**Rows are chosen to kill a specific wrong filter, not only to cover a rule.** `technology:node`
under category `tech` kills `startsWith("tech")` — the prefix bug a reader would actually write.
`business:plan` under category `business` kills `startsWith(category + ":")` applied to the two
named categories, which is the requirement's own point that the filter name and the prefix differ.
`dev` without a colon and `:java` without a category settle the two readings the requirement leaves
open.

**The optional table holds its tag list constant across all five rows,** so the set is the only
thing that moves and each `Kept?` cell is readable as its effect. The absent set and the empty set
are written differently — a blank cell is null, `{}` is an empty `Set` — and the requirement gives
them the same effect, which is a claim only two rows can make.

**The last table's final row settles a collision the requirement leaves open.** "Null category
returns all tags unfiltered" and "an empty tag string is never kept, whatever the category" both
apply to it, and the row commits to the empty-tag rule winning. A reviewer who disagrees has one
cell to change.

Every tag carrying a colon, pipe or bracket is quoted inside the list, so the element is a string
and not a map entry. The newline is written `\\n` in the text block — Java would turn a single
`\n` into a real line break before TableTest saw it, ending the row — and restored on both the
input and the expectation before the call.
