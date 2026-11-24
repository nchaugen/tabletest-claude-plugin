`PlaylistScreenTests.swift` now uses a single `@TableTest`. The package builds and the tests pass.

**Table redesign**
- The old rows each held a whole `PlaylistScreen`, and the scenario name was the only place that said what had changed. The new table has two input columns instead: `Opened From` (`library`, `playlist`, `playlist with an unread rule`) and `Change` (`no change`, `adds a genre rule`, `adds a rating rule`, `adds an unplayed-only rule`, `changes the order`, `removes every rule`).
- The outputs are `Edited?` and `Actions?`, both asserted as before. I dropped the scenario column because the two inputs now say what each row is.
- Test-local enums with `String` raw values (`OpenedFrom` and `Change`) build the fixtures, so the test body only arranges, acts and asserts. The fixture description is in the doc comment.
- Value sets show that the kind of rule added never changes the outcome, and that `no change` and `removes every rule` agree on a library with no rules.

**Change to app code**
- I added `Codable` to `PlaylistScreen.Action` in `Sources/Playlists/PlaylistScreen.swift`. This lets the `Actions?` cells read `[revert, saveAsNew]` directly. The conformance is synthesised, so it adds no behaviour.

**Case count**
- The old test had 10 cases. The new one has 18. Every old case is still covered.
- The extra cases come from two sources. Crossing the two inputs adds `unread rule × changes the order`, `unread rule × removes every rule` and `library × removes every rule`. The value sets add rating-only and unplayed-only rules on playlists, and a rating-only rule on the library.
- Every new case passes against the current code.

**Assumptions**
- The library fixture starts with no rules, most recently added first. The playlist fixture is jazz, most recently added first, as in your old comment.
- `adds a genre rule` adds rock, so a jazz playlist becomes jazz plus rock. I assumed the old "another order" cases map to `changes the order`.

I also ran the table formatter, which reformatted the table's alignment.