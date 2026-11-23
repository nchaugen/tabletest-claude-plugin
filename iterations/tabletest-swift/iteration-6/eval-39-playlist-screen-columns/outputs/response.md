`PlaylistScreenTests.swift` now uses `@TableTest`. `swift build --build-tests` and `swift test` both pass. The tabletest formatter only realigned the table whitespace.

**Changes to the code**
- **App change:** I added `CaseIterable` to `PlaylistScreen.Action` in `Sources/Playlists/PlaylistScreen.swift`. That lets a cell read `[revert, saveAsNew]`. It adds no behaviour.
- **Test-local enum:** `Change` holds what the user does to a saved playlist's rules: "as saved", "adds a rule", "changes the order" and "removes every rule". It builds the changed query.
- **Test function bodies:** They contain no branches. They build the screen and assert.

**New tables**
- **Old shape:** The old table held two whole queries or screens per row, so only the scenario name showed what differed. It also put the library and the playlist rules in one table.
- **Library table, "Offers saving a library query that has a rule":** It has columns for Genres, Minimum Rating, Unplayed Only and Order, with `Actions?` as the result. Order is a value set `{recentlyAdded, artist}` on every row, which states that order doesn't count as a rule.
- **Playlist table, "Offers actions for a playlist changed on screen":** It has the columns Unread Rules, Change, `Edited?` and `Actions?`. The fixture, "Late-night jazz" (jazz, most recently added first), is in the doc comment. "Nothing changed" and "Every rule removed" use `{0, 1}` for Unread Rules, because the unread-rule count doesn't change their result.
- **Plain test:** "Never counts a library query as edited" is a small `@Test`. The library has no stored query, so `Edited?` would be `false` on every library row.

**Case counts: 10 before, 19 now**
- **Library:** The original had 4 library cases. There are now 8, from 4 rows with the order value set.
- **New library row:** I added a "Rating only" row, since `minRating` is one of the three things `hasRules` checks and the original never covered it.
- **Playlist:** The original had 6 cases, and there are now 8. The new cases are "as saved" and "every rule removed" crossed with the unread-rule count, and the order change crossed with an unread rule.
- **Never edited:** The "library is never edited" check is 1 case.

I read "a rule" as genres, minimum rating or unplayed-only, matching `hasRules`. I chose rating 4 for "adds a rule" because the original fixture did.