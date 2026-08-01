### Blank Means Absent

Use a blank cell when a value is genuinely absent. Blank means **absent** — not zero, not a default,
and not irrelevant.

Three meanings the notation has to keep apart:

| Meaning | Notation |
|---|---|
| The value is missing | blank cell |
| The value exists but does not affect this {{row}} | value set |
| The value is present and empty | {{empty_value_notation}} |

**The system under test decides what an absent value means — never the test.** That decision is part
of the behaviour being specified, and the {{row}} exists to pin it down. Writing a baseline value into
the cell is a different scenario; converting a blank to a default on the way in deletes the case the
{{row}} was written to show.

Do not fill a genuinely blank cell with filler like `N/A` or `none`.

{{example}}

**Check:** **Blank means absent**: a column whose input is genuinely absent for a {{row}} uses a blank cell, not 0 or a default; an input that is present but irrelevant is a value set instead, and nothing converts a blank to a default on the way in
