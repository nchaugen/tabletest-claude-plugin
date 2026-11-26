I converted `PlaylistScreenTests.swift` to a single `@TableTest` and redesigned the table. It builds, and `swift test` passes all 17 cases.

**Redesign.** The old rows each held two whole `PlaylistScreen` values, so you had to compare them to see what the user had changed. The table now has the columns that decide the result:
- **Screen**: `library`, `playlist`, or `playlist with an unread rule`.
- **Change**: what the user does, for example `adds a genre rule`, `changes the order` or `removes every rule`.
- **Edited?** and **Actions?**: the outputs. Both are still checked, as before.

Two small enums in the test, `Screen` and `Change`, build the fixture from those words. The test body only builds the screen, applies the change and asserts.

I used value sets where the outcome is the same, so six rows cover every combination:
- Library: no change or a new order gives no actions. Adding any kind of rule gives `saveAsPlaylist`.
- Playlist: unchanged gives no actions, with or without an unread rule. Removing every rule gives only `revert`.
- Edited playlist: any added rule or a new order gives `revert, saveAsNew, update`. With an unread rule it gives `revert, saveAsNew`.

**App change.** I added `CaseIterable` to `PlaylistScreen.Action` in `Sources/Playlists/PlaylistScreen.swift`. This lets the `Actions?` column read the case names directly. It adds no behaviour.

**Case count: 10 became 17.** The value sets add checks the old table didn't have:
- A rating rule and the unplayed-only rule are now tried on the library, and a genre rule on a playlist.
- A playlist with an unread rule is now tested with a changed order.
- A playlist with an unread rule is now tested with every rule removed.

All the original scenarios are still covered.

I assumed the library starts with no rules and `recentlyAdded` order, and that "adds a genre rule" adds `rock`. The fixture comment above the table says this.