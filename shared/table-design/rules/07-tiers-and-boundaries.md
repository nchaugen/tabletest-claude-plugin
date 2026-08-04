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

**A formula behind the tiers does not reduce the tiers.** If you find yourself arguing that two tiers
and the delta between them determine the rest, that is the formula talking: the table pins the tiers
the rule names, and identifying the formula is the implementation's job. Nine tiers stay nine
{{rows}}.

This is the coverage half of *Give Each Obligation Exactly One {{Row}}*, and the two meet at a
boundary: the straddling pair is required here and earns both its {{rows}} there. A third {{row}}
further past the same boundary is what the other rule removes.

Where a tier is a range rather than a single value, a value set spanning it carries its own
boundaries — a separate "tier begins" {{row}} then discharges nothing the "tier holds" {{row}} has
not. **That is economy inside a {{row}} and buys no licence to drop {{rows}}:** shortening each tier
to one cell makes the ladder look repetitive long before it is complete.

{{example}}

**Check:** **Every tier once**: a tier ladder has one {{row}} per tier — all of them, none twice — and every boundary is exercised from both sides at the finest unit the rule distinguishes, middle tiers included
