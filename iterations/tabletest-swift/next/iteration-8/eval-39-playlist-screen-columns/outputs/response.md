I converted `PlaylistScreenTests` to one `@TableTest` and redesigned the table. `swift test` passes with 17 cases, up from 10.

**Redesign.** The old rows each held a whole screen (a `SongQuery` or `PlaylistScreen`), so you had to compare two screens to see what the user had done. The new table uses columns for what decides the result:
- **`Opened From`** is where the screen came from: `library`, `playlist` or `playlist with an unread rule`.
- **`Change`** is what the user does: `no change`, `adds a genre rule`, `adds a rating rule`, `adds an unplayed-only rule`, `changes the order` or `removes every rule`.
- **`Edited?` and `Actions?`** are the outputs.

The fixture (empty library, "Late-night jazz") is described in the doc comment. Value sets show which changes give the same result.

The fixture is built by two test-local enums with `String` raw values, `OpenedFrom` and `Change`. The test function has no branches.

**App source change.** I added `CaseIterable` to `PlaylistScreen.Action` in `Sources/Playlists/PlaylistScreen.swift` so cells can read `[revert, saveAsNew]`. It adds no behaviour.

**Case count.** The 10 old cases are all covered, and one row is new:
- The old "another order" and "unplayed only" rows and the playlist rows are now value-set elements.
- The new case is an unread-rule playlist with every rule removed, which gives `[revert]`. It is the only case that shows the "needs a rule" check comes before the unread-rule check.
- Value sets add cases the old tests lacked, such as a rating rule on the library and an order change on an unread-rule playlist.

**Assumptions:**
- The old library "a rule" case used `rock`, and the playlist "a rule added" case set `minRating: 4` on top of jazz. Both are now `Change` values, applied to either source.
- There is no library row for `removes every rule`, because the library starts with no rule. The doc comment says so.