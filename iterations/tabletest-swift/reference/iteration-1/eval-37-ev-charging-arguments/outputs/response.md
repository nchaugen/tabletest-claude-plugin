Hand-authored reference answer for eval-37, from
`products/claude-plugin/plans/slice-12-swift-skill.md` step 3. Not agent output. Must never be read
as a skill measurement.

The `sessions` collection becomes a table whose columns are its tuple labels, and the zip of two
inline lists becomes a second table, because it tests a different function. Both keep their rows
and values, and `sessions` is gone.

The `Charger.allCases` test stays a `@Test(arguments:)`: its list comes from the app, so a charger
added there is checked here with no change. A table would freeze the list. The comment above the
test says so. 14 cases before and after.
