`@TableTest` tables inside `PlaylistScreenTests` whose input columns are the inputs that decide the
actions. The tests pass.

## What decides the actions

`PlaylistScreen.actions` reads where the screen comes from (the library or a saved playlist), how
the playlist was read (its unread rules), and, for a playlist, what the user did to its rules. None
of these is a whole `SongQuery`. The original cases hold two full queries — the saved one and the
one on screen — and their names ("a rule added") state the input that no column holds.

## The library table

The library offers Save as playlist once a rule shows. The input is the rule set itself, because
the rule is about what counts as a rule: no rule, a genre, a minimum rating, unplayed songs only.
The order does not change the actions, so it is a value set such as `{recentlyAdded, artist}`, or
it is left out with the fixture stating it. A row for the minimum rating is new and equally good.

The rule set is given either as one column per rule with the body building `SmartRules`, or as a
map cell that names only the rules it sets (`[:]`, `[genres: {rock}]`). A synthesised `Decodable`
needs a key for `unplayedOnly`, so the map cell needs a `TableValueDecodable` conformance on
`SmartRules` in the test target — for example with `tableValue.readingFields` — with a `public`
initialiser and no `@retroactive` in a Swift package.

## The playlist table

The inputs are the unread rules and the change the user made: none, adds a rule, changes the order,
removes every rule. The change is a test-local `String` raw-value enum (or an equal test-side value)
that builds the query on screen from the fixture. The expectations are `Edited?` and `Actions?`.

- The count of unread rules matters only as zero or not: `{1, 2}` as a value set shows that.
- Crossing the inputs finds two rows the original lacked: a playlist with an unread rule whose
  order changed (`[revert, saveAsNew]`), and one whose every rule was removed (`[revert]`).

## Cells

`PlaylistScreen.Action` is a plain enum in a list cell. Add `CaseIterable` (or `Codable`) to it in
the app — one synthesised word — so that `[revert, saveAsNew, update]` converts. No `init(from:)`
in the app, and no field of an app type changes.

## Equally good

The scenario column may go: when the columns state the inputs, a name often has nothing to add.
When present, a name states the situation, not the actions. Titles, column names and the split
into tables are free, provided no table mixes the library rule with the playlist rule.
