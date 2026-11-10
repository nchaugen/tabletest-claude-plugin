Hand-authored reference answer for eval-39, from
`products/claude-plugin/plans/slice-12-swift-skill.md` step 3. Not agent output. Must never be read
as a skill measurement.

The original cases held two full queries per playlist case, and their names stated the change that
no column held. The redesign makes the deciding inputs the columns: on the library, the rule set and
the order; on a playlist, the unread rules and the change the user made. The change is a test-local
`String` raw-value enum that builds the query on screen from the fixture.

Value sets show what does not matter: the order on the library, and `{1, 2}` unread rules. Crossing
the unread rules with the changes found two rows the original lacked, both now in the table.

`SmartRules` has defaulted fields, so a synthesised decoder would need every key. A
`TableValueDecodable` conformance in the test target reads the map cell with `readingFields`, with a
`public` initialiser and no `@retroactive`, because this is a Swift package. `CaseIterable` on
`PlaylistScreen.Action` lets the list cell convert.
