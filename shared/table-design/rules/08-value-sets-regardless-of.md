### Use Value Sets for "Regardless Of" Relationships

When an input exists but does not affect the outcome of a {{row}}, say so with data rather than prose:
put every value the rule ignores in the cell.

A blank would wrongly suggest the field is absent. The value set makes the claim explicit — *this rule
holds for all these values* — and one {{row}} states it more precisely than two near-identical ones.

**The clearest sign you want one: a column that could carry every one of its values on every
{{row}} without changing anything.** That is the rule saying, in data, that it does not read the
column.

**A value set cannot vary an expectation.** It expands the {{row}} into one case per value, and every
expanded case keeps the same expectation cells. Where the answer differs per value, those are
ordinary distinct {{rows}}.

**Every value in the set must produce the same result.** If the results differ, the input does matter
and belongs as ordinary distinct {{rows}}. Never use a value set as shorthand for "test several
values".

**Two different situations, two different treatments.** An input that *another* rule owns is held at
one obviously-valid value. An input that *this* rule claims not to affect has to vary across the
values it ignores — otherwise no {{row}} could ever contradict the claim.

**Value sets work on two axes — check both.** *Within* a {{row}}, group input values that produce the
same outcome. *Across* {{rows}}, collapse duplicates: when two input values produce the same
expectation cells **in this table**, one {{row}} carrying both replaces two identical ones. It is
easy to apply one axis and miss the other.

**Judge that per table, not across the whole class.** Two values that this rule treats alike collapse
here even if a neighbouring rule tells them apart — grouping them says *this* rule does not
distinguish them, which is exactly what the neighbouring table then contradicts, on the record. Ask
only whether the expectation cells match in the {{rows}} in front of you. A category you have named
as a catch-all is the easy case and gets collapsed almost automatically; **the one that gets missed
is two values you think of as distinct that this particular rule happens to treat the same.**

{{example}}

**Check:** **Value set semantics**: value sets appear only where every value produces the same result, never as shorthand for "test several values"; an input this rule claims not to affect the outcome varies across the values it ignores, while an input another rule owns is held at one valid value
