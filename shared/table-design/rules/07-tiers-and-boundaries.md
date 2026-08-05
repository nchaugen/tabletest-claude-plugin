### Cover Every Tier and Both Sides of Every Boundary

When inputs map to tiers — rate bands, size categories, standings — every tier appears in the
{{rows}}, and every boundary is exercised from both sides: the last value inside a tier and the first
value of the next.

**Middle-tier boundaries are the ones most often skipped.** Outer edges alone do not pin down where
the middle tiers change.

**Pick the pair's unit from the finest distinction the rule draws, before writing either value.**
Where the rule separates 29 days 23 hours from 30 days 1 hour, whole-day {{rows}} of 30 and 31
straddle nothing — the column is `Hours Ago` and not `Days Ago`. A boundary drawn in a unit coarser
than the rule is not drawn at all, however many {{rows}} surround it.

**And express it as an offset from the reference point, not as an absolute value restated in every
{{row}}.** `Hours Ago` is the whole example: it fixes the unit *and* keeps the {{row}} readable, where
absolute instants pin the same boundary while making the reader subtract before the rule is visible.
*Assume the Table Is Published* sends the reference point itself to a column; this rule owns the unit,
and one choice satisfies both. A boundary win bought with an unreadable cell has been paid for twice.

**A formula behind the tiers does not reduce the tiers.** If you find yourself arguing that two tiers
and the delta between them determine the rest, that is the formula talking: the table pins the tiers
the rule names, and identifying the formula is the implementation's job. Nine tiers stay nine
{{rows}}.

**And it does not reduce the boundaries.** Where the tier is decided by a formula over several
inputs, every input still has a value at which the outcome flips, and that pair is what the {{rows}}
have to straddle — one just below it, one just above, the other inputs held. Sampling that input at
two comfortable values instead exercises the arithmetic and leaves the boundary untested.

This is the coverage half of *Give Each Obligation Exactly One {{Row}}*, and the two meet at a
boundary: the straddling pair is required here and earns both its {{rows}} there. A third {{row}}
further past the same boundary is what the other rule removes.

Where a tier is a range rather than a single value, a value set spanning it carries its own
boundaries — **provided its first and last members are the tier's own first and last values.** The
straddling pair is then already written: the last member of one {{row}}'s set and the first member of
the next {{row}}'s. **State the tier's edges, not two comfortable values inside it** — a set of middle
values straddles nothing and the explicit pair is still owed. Done that way a separate "tier begins"
{{row}} discharges nothing the "tier holds" {{row}} has not, and **one {{row}} per tier covers the
whole ladder and every boundary in it**. That is economy inside a {{row}} and buys no licence to drop
{{rows}}: shortening each tier to one cell makes the ladder look repetitive long before it is
complete.

{{example}}

**Check:** **Every tier once**: a tier ladder has one {{row}} per tier — all of them, none twice — and every boundary is exercised from both sides at the finest unit the rule distinguishes, whether by two {{rows}} or by a value set whose end members are the tier's own edges, middle tiers included, and a boundary an input reaches through a formula straddled like any other
