I converted `PlaylistScreenTests.swift` to `@TableTest`, and both tables pass under `swift test`. The library table runs 6 cases because its order column is a value set (3 rows × 2 orders). The playlist table runs 6.

**What changed**
- **Two tables instead of one.** The old table mixed two rules, so it is now split into the library screen and the playlist screen.
  - **Library:** the screen offers `saveAsPlaylist` once it has a rule. The order of the songs doesn't matter, so the order column is the value set `{recentlyAdded, artist}`. That is what lets the old "another order" row go.
  - **Playlist:** the columns are the saved playlist's unread rules and the rules and order now on screen (genres, minimum rating, order). The expected columns are `Edited?` and `Actions?`.
- **No more `SongQuery` or `SavedPlaylist` in the cells.** The rows were hard to read because each cell held a constructed object. Each case is now plain values, and the test body just builds the objects from them.
- **Saved playlist fixture.** It is fixed at jazz, no minimum rating, recently added first. A doc comment states that, because the cases only show what is on screen.
- **Action conversion.** `PlaylistScreen.Action` has no raw values, so I added a small `TableTextDecodable` conformance in the test file. It lets the tables write `[revert, saveAsNew]`. I left out `@retroactive` because the compiler rejected it for a type in the same package.

**Assumptions**
- Edited-ness is checked in both tables, as it was before, even though it is always `false` for the library. That keeps the "library is never edited" assertion.
- I didn't add a library case for a minimum-rating rule, since the original didn't have one. It is a one-row addition if you want it.
- I kept the original cases otherwise, and didn't add any for the unread-rule playlist with every rule removed or with another order.