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

So a constant the outcome depends on is either a column, or declared in the title or the description.
It is **not** declared when it sits in the test body, in a field, in a conversion helper, or in a
comment — a comment reaches no published surface at all. The helper is the easiest hiding place
because it looks like plumbing: one that builds every entry with the same zone has pinned zone for
the whole table, and no column says so.

{{example}}

**Check:** **Held constants declared**: every value the outcome depends on that the table fixes for all {{rows}} is a column, or is named in the title or description as held fixed — never left only in the test body, a field, a conversion helper, or a comment
