`PlaylistScreenTests.swift` now uses two `@TableTest` functions instead of the single `@Test(arguments:)`. `swift build --build-tests` and `swift test` both pass: 2 tests, 12 cases.

**One change outside the test file:** I added `Codable` to `PlaylistScreen.Action` in `Sources/Playlists/PlaylistScreen.swift`. The `Actions?` cells need it to convert, and it is synthesised, so it adds no behaviour. You may want to review it.

**New tables**
- **`Offers saving the library search as a playlist once it has a rule`** has these columns: `Genres`, `Minimum Rating`, `Unplayed Only`, `Sort Order`, `Actions?`. The library has no saved state to compare with, so only the rules on screen decide the result. The sort-order claim is now a value set `{recentlyAdded, artist}` on the "Whole library" row. This replaces the old "another order" row.
- **`Offers revert, save and update once a playlist is edited`** has these columns: `Unread Rules`, `Change`, `Edited?`, `Actions?`. The old rows held two whole `PlaylistScreen` values each, and you had to compare them to see what the user did. `Change` is now a column, backed by a test-local enum (`no change`, `adds a rule`, `changes the order`, `removes every rule`). `Unread Rules` is a number column. The fixture playlist is built in the test body.

**Differences from the old test (10 cases before, 12 now)**
- I added a "Narrowed by rating" row. The old test never exercised `minRating` in `hasRules`.
- "Playlist left without a rule" now runs with 0 and 1 unread rules. The old test never crossed unread rules with removing every rule. Both give `[revert]`.
- I did not add a row for changing the order on a playlist with an unread rule. It would repeat the "Rule added, one unread" row's expectation.
- `Edited?` is only in the playlist table. In the library table it would be `false` on every row, so the library "never edited" assertion is no longer made. You can put it back if you want it.

Row names say which case each row is, and don't repeat the expected values.