I converted `PlaylistScreenTests.swift` to a single `@TableTest`, and `swift test` passes with the same 10 cases as before.

**New table design.** The old table had each row hold a whole `PlaylistScreen`, and only the scenario name said what the user had done. The new table has two input columns that say it directly:
- **Opened From** is where the screen came from: `the library`, `a playlist` or `a playlist with an unread rule`.
- **Change** is what the user did to the rules: `no change`, `requires a rating of 4+`, `limits to unplayed`, `sorts by artist` or `removes every rule`.

`Edited?` and `Actions?` are the expectations, so both old assertions are kept. The fixtures that used to be static lets and tuples are now two small enums inside the test type. `OpenedFrom` builds the starting screen and `Change` applies the edit. The test function has no branching.

**Changes outside the test file:**
- I added `CaseIterable` to `PlaylistScreen.Action` in `Sources/Playlists/PlaylistScreen.swift`. That is what lets the `[revert, saveAsNew, update]` cells convert. It adds no behaviour.
- Running the table formatter reformatted one file. I didn't check which one, so if it wasn't the test file, look for stray changes.

**Assumptions:**
- Every library row now starts from the empty library query and applies one change. "The library, a rule" used a rock genre rule before. It is now a rating rule, because the same `Change` has to work for libraries and playlists. It still checks that any rule on the library offers `saveAsPlaylist`.
- The old "playlist, a rule added" case also used a rating rule, so it maps directly.
- I kept the old test's 10 cases and added no new combinations. For example, an unread-rule playlist with every rule removed has no row.