I converted `PlaylistScreenTests.swift` to one `@TableTest`, and `swift test` passes with 18 cases.

**Table redesign**
- The old rows held two whole `PlaylistScreen` values each, so you had to compare them to see what had changed. The scenario name was the only place that said what the user did.
- The inputs are now two columns. `Screen` is where it was opened from: the library, a playlist, or a playlist with an unread rule. `Change` is what the user does to the rules. The scenario column is gone because those two columns already say it.
- `Edited?` and `Actions?` are both outputs of the same rule, so they share the table.
- The three ways of adding a rule (genre, minimum rating, unplayed-only) all give the same result on each screen, so each is a value set in a single row.
- Library rows never count as edited.
- `Screen` and `Change` are test-local enums with string raw values that build the screen and the changed query, so the test function has no branches.

**Changes outside the test file**
- I added `CaseIterable` to `PlaylistScreen.Action` in `Sources/Playlists/PlaylistScreen.swift`. Without it a cell can't read `[revert, saveAsNew]`. It adds no behaviour.
- I ran the table formatter, which realigned the columns.

**Case count: 10 before, 18 now.** Crossing every screen with every change filled in combinations the old list skipped, and it covers the old cases as well.
- The old two library rows with a rule (a genre and unplayed-only) are now the value-set row for the library.
- The "another order" library case is now a library row under `changes the order`.
- The old "rating rule added" playlist row is now one element of the playlist value set; the genre and unplayed-only elements are new cases.
- New cases:
  - Unread-rule playlist, order changed: `[revert, saveAsNew]`.
  - Unread-rule playlist, every rule removed: `[revert]`.
  - Library, every rule removed: `[]`.

I assumed the library opens with no rules, most recently added first. That, the Late-night jazz fixture and the meaning of "unread rule" are in the doc comment.

I didn't touch git.