### Use Value Sets for "Regardless Of" Relationships

When an input exists but does not affect the outcome of a {{row}}, say so with data rather than prose:
put every value the rule ignores in the cell.

A blank would wrongly suggest the field is absent. The value set makes the claim explicit — *this rule
holds for all these values* — and one {{row}} states it more precisely than two near-identical ones.

**Every value in the set must produce the same result.** If the results differ, the input does matter
and belongs as ordinary distinct {{rows}}. Never use a value set as shorthand for "test several
values".

**Two different situations, two different treatments.** An input that *another* rule owns is held at
one obviously-valid value. An input that *this* rule claims not to affect has to vary across the
values it ignores — otherwise no {{row}} could ever contradict the claim.

**Value sets work on two axes — check both.** *Within* a {{row}}, group input values that produce the
same outcome. *Across* {{rows}}, collapse duplicates: when two input kinds follow identical rules
everywhere, one {{row}} with both values replaces two identical ones. It is easy to apply one axis
and miss the other.

{{example}}

**Check:** **Value set semantics**: value sets appear only where every value produces the same result, never as shorthand for "test several values"; an input this rule claims not to affect the outcome varies across the values it ignores, while an input another rule owns is held at one valid value
