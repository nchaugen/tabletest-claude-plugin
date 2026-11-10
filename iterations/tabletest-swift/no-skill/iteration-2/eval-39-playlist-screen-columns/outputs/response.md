I converted `Tests/PlaylistsTests/PlaylistScreenTests.swift` to `@TableTest`, and `swift test` passes: 3 tests, with 8 cases for the library table and 6 for the playlist table.

**What changed in the design**
- **Two tables instead of one.** The old table mixed library screens and playlist screens, each built with a long `SongQuery(...)` expression. Library screens are now one table, and playlist screens another.
- **Plain columns instead of constructed values.** Each table has columns for `Genres`, `Minimum rating`, `Unplayed only` and `Order`. The test body builds the query from them. A blank cell means no minimum rating.
- **Sort order shown as irrelevant.** The library rule doesn't depend on sort order. Every library row has `Order` as `{recentlyAdded, artist}`, so each row runs once per order. That replaces the old pair of rows that differed only in order.
- **The playlist fixture stays in a comment.** "Late-night jazz" holds jazz, most recently added first. The playlist table's columns describe what is on screen, plus the `Unread rules` count. Scenarios are named by condition, such as `Unread rule, a rule added`.
- **Expected values in the table.** `Edited?` and `Actions?` are columns, and actions are written as lists like `[revert, saveAsNew, update]`.

**Assumptions to check**
- **Retroactive conformance in the test file.** `PlaylistScreen.Action` has no raw values and isn't `Codable`, so I added a small `TableTextDecodable` conformance in the test file. I did not touch the app source. `@retroactive` isn't allowed there because the type is in the same package.
- **Added row.** I added a `Minimum rating` row to the library table (genre, minimum rating and unplayed-only each count as a rule). The old tests covered only genre and unplayed-only.
- **Library "never edited" check split out.** `Edited?` would have been `false` on every library row, so it isn't a column. The old check is now a small separate `@Test`, "Never marks the library as edited".

The tables were aligned with `tabletest-format`.