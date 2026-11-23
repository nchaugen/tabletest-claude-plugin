I converted `PlaylistScreenTests` to two `@TableTest` functions. `swift build --build-tests` succeeds, and `swift test` passes with 14 cases (6 + 8).

**One change to app code:** I added `Codable` to `PlaylistScreen.Action` in `Sources/Playlists/PlaylistScreen.swift`. It adds no behaviour, and it lets a cell hold `[revert, saveAsNew]` directly.

**Redesign.** The old rows each held two whole queries, and only the scenario name said what had changed. The tables now have a column for each thing the screen reads, and the scenario column is gone:

- **`offersToSaveTheLibraryRulesAsAPlaylist`** has the columns Rules on Screen, Sort Order and `Actions?`. The sort order doesn't affect the outcome, so each row sets it to `{recentlyAdded, artist}`. A row could therefore contradict that claim.
- **`offersTheActionsAnEditedPlaylistAllows`** has the columns Playlist (read in full, or with an unread rule), Change, `Edited?` and `Actions?`. The change is one of no change, adds a rule, changes the order, or removes every rule. Rows that share an outcome are merged with value sets.
- The `isEdited` check moved into the playlist table only, because the library is never edited, so it would be constant there.
- Small test-local enums (`LibraryRules`, `SavedAs`, `Change`) build the fixtures from the words in the cells. The test bodies have no branches.

**Case count: 10 became 14.** The extra cases are not new behaviours:

- **Library:** the sort order is now crossed with each rule set (3 rules × 2 orders = 6, up from 4).
- **Playlist:** the table now includes the unread-rule playlist with a changed order and with every rule removed, which the old list skipped.
- **Playlist:** the old "another order" case on a fully-read playlist is now merged with "adds a rule" in one value-set row. Both still run.

**Assumptions:**

- I treated the library's rule cases as "a genre" and "unplayed songs only". I left out a minimum-rating case, as the old list did.
- The playlist table is "read in full" against "with an unread rule", with the fixture jazz playlist as the base.

I also ran the package's table formatter, which reformatted one file (the test file).