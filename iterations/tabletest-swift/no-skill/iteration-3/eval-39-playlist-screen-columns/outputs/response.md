`Tests/PlaylistsTests/PlaylistScreenTests.swift` now uses one `@TableTest` with 10 rows, one per original case. It builds and all 10 rows pass.

**What changed in the design**
- **Rule cells:** each query is written out in the row, for example `[genres: {jazz}, minRating: 4, order: recentlyAdded]`. Before, the rules were hidden behind the `jazz`, `wellRatedJazz` and `lateNightJazz…` fixtures and inline `SongQuery(...)` calls.
- **Saved query and unread rules:** these are now columns, so you can see what a row is compared against. The saved query is the same in every playlist row, but it's visible instead of buried in a fixture.
- **Library rows:** a blank Saved Query and a blank Unread Rules mean the library, because nothing is saved. This is the "blank means absent" convention.
- **Expectations:** `Edited?` and `Actions?` are marked with `?`, and `Actions?` is a real list such as `[revert, saveAsNew]`.
- **Scenario names:** they describe the situation, for example "Playlist with every rule removed", and the test title says what the code does.
- **Fixture description:** it is in the doc comment above the struct.

**Things to check**
- **Conversions:** to read the cells I added two small conversions at the bottom of the test file. One reads a query map, where `order` is required and the other rules are optional and default to "chooses every song". The other reads an `Action` by case name. Neither touches the `Playlists` sources.
- **`showing(...)` helper:** a small helper picks library or playlist depending on whether Saved Query is blank. That keeps the test body free of branches.
- **`@retroactive`:** I dropped it because the compiler rejects it for types in the same package.
- **Cases:** I didn't add or remove any. One gap: removing every rule on a playlist that has an unread rule isn't covered. That case should offer `[revert]` only, so it's a good row to add.