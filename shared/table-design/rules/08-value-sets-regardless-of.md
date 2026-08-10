### Show That an Input Does Not Change the Outcome

**You have just concluded that some input does not affect this rule. That conclusion is a rule too,
and it needs a {{row}} that could contradict it**: put every value the rule ignores in the cell, as
a value set.

**Dropping the input states nothing.** Leaving the column out reads exactly like having forgotten it
— nothing on the page tells a reader which happened, and no {{row}} can contradict a claim the table
never makes. A blank is no better and wrongly suggests the field is absent. The value set makes the
claim explicit — *this rule holds for all these values* — and one {{row}} states it more precisely
than two near-identical ones.

**The clearest sign you want one: a column that could carry every one of its values on every
{{row}} without changing anything.** That is the rule saying, in data, that it does not read the
column.

**If the operation does not take the input, fix that before the table.** A rule cannot say it ignores
what never reaches it, and leaving the parameter out publishes nothing: the reader sees an operation
that was never offered the value, which is silence rather than a claim. **A column the code never
receives will not do instead** — that route is open to a threshold, which only has to be readable
(*Make Thresholds Visible*), and closed to this rule, whose claim has to be exercised to be
contradictable. Decide it by asking who supplies the value: **if a caller hands it over with the
request it is an input, so keep it and let the table vary it**, even where the code will not read it.

**If the ignored value sits inside a composite cell, reshape the column first** — a value set varies
a whole cell, never one part of one, so `{a, b}` written inside a cell adds no {{rows}}. Give the
value its own column when this table reads nothing else in that cell (*Give Each Obligation Exactly
One {{Row}}*). Holding it at one value instead abandons the claim.

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
