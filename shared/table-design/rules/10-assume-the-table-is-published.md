### Assume the Table Is Published

Write every table as if a reader will meet it in a published report, never having seen the code. Only
three surfaces reach that reader, and they divide the work:

| Surface | Carries |
|---|---|
| {{title_surface}} | the rule, as an action the code performs |
| {{description_surface}} | the apparatus that cannot be a column — what is held constant, where the data came from |
| the table | the variations the rule ranges over |

**Whatever the table holds constant is silently promoted into the rule.** Readers generalise from
what varies, so a value that never varies is read as part of the rule: a duty-limit table whose every
{{row}} assumes a two-pilot crew states, to its reader, a rule about two-pilot crews.

So a constant the outcome depends on is a **column** wherever it can be one — and a value the rule
turns on, such as a threshold or a limit, always can be. The other two surfaces carry what a column
cannot: where the data came from, what the fixture fixes, an assumption the {{rows}} cannot state.

**If the declaration says the value does not matter, declaring it is not enough.** *"Held empty
throughout, and it makes no difference"* is not apparatus — it is a claim about the rule, and a claim
no {{row}} can contradict is not stated in the table at all. Vary it instead, across the values it
ignores; see *Value Sets for "Regardless Of" Relationships*. Write a fixture into the
{{description_surface}} only for what the rule genuinely reads and the {{rows}} cannot show.

**Making a value a column does not force everything measured from it into the same form.** Once a
reference point is declared — a clock, an origin, a baseline — the columns measured *from* it read
better as offsets against it than as restatements of it. Both are then visible, and the offsets stay
short enough to scan.

**Choose that unit before shortening anything, and choose it from the boundary rather than from the
offset.** A shorter cell that cannot state the rule has bought nothing — see *Cover Every Tier and
Both Sides of Every Boundary*, which owns the choice.

**Keep a slot in the cell for every field the {{description_surface}} makes a claim about.** A compact
cell carries the fields the rule reads and drops the rest, and a dropped field is pinned exactly as it
would be in a conversion helper, with nothing on any surface to say so. When the claim is that the
outcome does not turn on that field, dropping it is what makes the claim uncontradictable — put the
field back as a key or a column, or stop making the claim.

It is **not** declared when it sits in the test body, in a field, in a conversion helper, or in a
comment — a comment reaches no published surface at all. The helper is the easiest hiding place
because it looks like plumbing: one that builds every entry with the same zone has pinned zone for
the whole table, and no column says so.

**What the assertion tolerates is part of the rule too.** A comparison that sorts either side before
comparing, accepts a subset, matches "contains" rather than equals, or normalises case or whitespace
is *enforcing a rule*: it changes which behaviours the test would accept, and none of it reaches the
reader. Ordering is the usual one, and a shared helper is where it hides — written once, then
invisible at every call site, so a reader cannot tell whether order is part of the behaviour or an
artefact of the comparison. Two repairs, and the second is better where it fits:

- **Name it** — one sentence in the description, or a column that makes it evident.
- **Remove the need for it** — an unordered collection as the expectation says order does not matter
  *in the table itself*, which beats saying so in prose; an ordered one with a canonical sort says it
  does.

Numeric tolerance is not a criterion: a conventional epsilon on a decimal column is exempt. Nor is
constructing the objects the columns name.

{{example}}

**Check:** **Held constants declared**: every value the outcome depends on that the table fixes for all {{rows}} is a column where it can be one — always so for a threshold or limit the rule turns on — and otherwise named in the title or description as held fixed; never left only in the test body, a field, a conversion helper, or a comment
