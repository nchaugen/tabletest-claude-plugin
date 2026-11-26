I converted `PlaylistScreenTests.swift` to one `@TableTest`. All 10 cases pass under `swift test`, the same count as before.

**What changed in the table**
- **Old shape:** each row held a whole `PlaylistScreen` built by hand, plus `edited` and `actions`. You had to compare two queries to see what the user had done.
- **New inputs:** two columns, `Opened From` and `Change`.
  - `Opened From` is one of `library`, `playlist` or `playlist with an unread rule`.
  - `Change` is one of `no change`, `adds a rating rule`, `restricts to unplayed songs`, `changes the order` or `removes every rule`.
- **Outputs:** `Edited?` and `Actions?`, with the actions written as `[revert, saveAsNew, update]`.
- **Scenario names:** each row gets a name that says which case it is, without repeating its expected result.
- **Fixture:** it moved into the doc comment.
- **Test-local enums:** two enums, `Opening` and `Change`, build the screen from those words. The test function only builds the screen and checks the two outputs.

**Changes outside the test file**
- I added `Codable` to `PlaylistScreen.Action` in `Sources/Playlists/PlaylistScreen.swift` so the cells can read its cases. It is synthesised and changes no behaviour.

**Assumptions and differences**
- **Library rows:** a library screen has no saved query, so its changes apply to an empty rule set. The old "rock genre" row became "adds a rating rule". Both only show that a rule is present, so the result is the same. I did not keep a separate genre row.
- **Other old rows:** "another order" and "unplayed songs only" stay as their own rows. "Every rule removed" is kept for playlists only, because on the library it would be a no-op.
- **Coverage:** the playlist and unread-rule rows cover the same cases as before. There is no new coverage, for example no order change on the unread-rule playlist.