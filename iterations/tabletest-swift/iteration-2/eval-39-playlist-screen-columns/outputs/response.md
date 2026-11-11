I converted `PlaylistScreenTests` to a single `@TableTest` and redesigned the table. `swift test` passes with 12 cases.

**The old table.** Each row held two whole `PlaylistScreen` values built from `SongQuery` and `SmartRules`. You had to compare them to see what the user had changed. The scenario names held the real inputs: where the screen opened, and what the user did.

**The new table.** The columns are the deciding inputs and the outputs:

| Screen | Change | Edited? | Actions? |
|---|---|---|---|
| the library / a playlist / a playlist with an unread rule | no change, adds a rating rule, and so on | false/true | `[revert, saveAsNew, update]` and so on |

- **Fixtures:** `Screen` and `Change` are test-local enums with `String` raw values. The `switch` that builds each fixture lives in them, so the test function has no branches.
- **Outputs:** `isEdited` stays as a second expectation column, since it is an output of the same rule.
- **Value sets:** Rows that share an expected result are collapsed into one row with a value set. For example, `{adds a rating rule, changes the order}` on a playlist gives `[revert, saveAsNew, update]` either way.
- **Doc comment:** It states the fixtures and why the library is not crossed with "removes every rule".

**The one app change.** I added `CaseIterable` to `PlaylistScreen.Action`. That is what lets a cell read `[revert, saveAsNew]`. It adds no behaviour.

**Case count: 10 became 12.** I crossed Screen with Change and found two missing combinations: a playlist with an unread rule combined with "changes the order", and with "removes every rule". Both now have coverage. All 10 original cases are still covered.

Two assumptions:
- "Adds a rating rule" (`minRating = 4`) replaces the old library rock-genre case. It makes the same point, that a library with a rule offers `saveAsPlaylist`.
- The old "a playlist, another order" case is now covered by the `changes the order` element of the value set.