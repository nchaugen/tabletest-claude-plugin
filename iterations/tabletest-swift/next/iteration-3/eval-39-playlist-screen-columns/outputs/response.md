I converted `PlaylistScreenTests.swift` to a `@TableTest` and redesigned the table. `swift test` passes with 17 cases, and the formatter only re-aligned the table columns.

**Redesign**
- The old rows held two whole screens, so you had to compare them to see what the user had done. The table now has a `Screen` column (`library`, `playlist`, `playlist with an unread rule`) and a `Change` column (`no change`, `adds a genre rule`, `changes the order`, `removes every rule` and so on). Each is a test-local enum that builds the fixture.
- The expectation columns are `Edited?` and `Actions?`. The old test asserted both, so I kept both.
- The old 10 rows are now 6. Rows that reached the same answer share a row, and a value set in a cell means every value in it gives that result. For example, an unread rule doesn't matter once every rule is removed.
- The scenario names say the situation, not the answer.

**App change:** I added `CaseIterable` to `PlaylistScreen.Action` in `Sources/Playlists/PlaylistScreen.swift`, so the `Actions?` cells can read `[revert, saveAsNew, update]`. It adds no behaviour.

**Assumptions**
- Case count: the old test ran 10 cases and the new one runs 17, because value sets expand. The new cases include the three rule kinds (genre, rating, unplayed-only) on both the library and the playlist.
- The old "library, rock" and "library, unplayed only" cases are now members of the "adds a rule" value set. Adding a genre rule to the jazz playlist adds folk.
- I dropped two combinations that can't happen: removing every rule from the library, which starts with none, and a saved playlist that has no rules.
- I couldn't tell whether the old "another order" case on the library was meant as its own rule. It now shares a row with "no change", since both give `Edited? = false` and no actions.