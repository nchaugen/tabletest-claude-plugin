`PlaylistScreenTests.swift` is now a single `@TableTest`. `swift test` passes with 17 cases, and the formatter has aligned the table.

**New table design.** The old table gave each row two whole screens, `open` and `edited`, so you had to compare the screens to see what the user had done. It now has two input columns:
- **`Opened From`** is `library`, `playlist` or `playlist with an unread rule`.
- **`Change`** is what the user did: `no change`, `sorts by artist`, `removes every rule`, or one of three rule additions. A test-local `Source` enum builds the fixture, and `Change.applied(to:)` applies the change to its query.
- **`Edited?` and `Actions?`** are the outputs, so both old assertions are kept.

The fixture description is now a doc comment on `Source`, and each scenario name states a situation rather than its answer. The three rule additions (genre, rating, unplayed-only) share one value-set cell, since they all give the same result.

**One change in `Sources/`.** I added `CaseIterable` to `PlaylistScreen.Action`, so `[revert, saveAsNew, update]` can be read from a cell. It adds no behaviour.

**Case count: 10 before, 17 now.** The extra cases come from three things:
- The value sets expand the "rule added" rows into three cases each.
- The crossed columns exposed missing combinations: sorting a playlist with an unread rule, and removing every rule from one.
- I left out the old "library, a rule" and "library, unplayed only" rows as separate scenarios. The value set on "Rule added to the library" now covers them, with the old rock rule replaced by a genre rule that adds folk.

**Assumptions.**
- **Library change:** the library always starts with no rules, so "removes every rule" has no library row, because it would repeat "no change".
- **Folk as the added genre:** I picked folk because the playlist already holds jazz.
- **Test name:** I kept the old name, `offersTheActions…`, as the function name rather than adding a title.