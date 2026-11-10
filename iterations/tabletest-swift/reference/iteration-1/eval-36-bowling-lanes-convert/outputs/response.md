Hand-authored reference answer for eval-36, from
`products/claude-plugin/plans/slice-12-swift-skill.md` step 3. Not agent output. Must never be read
as a skill measurement.

The five price tests share one shape, so they become one `@TableTest` with five rows, and the
scenario names come from the test names. The zero-hours test expects an error, so it stays a plain
`@Test` with `#expect(throws:)`.

`Lane` was a plain enum, and a plain enum does not convert from a cell. `CaseIterable` on `Lane` in
the app is one synthesised word that adds no behaviour, so the lane column reads `standard`,
`deluxe` and `kids` with no test code. Two tests with 6 cases before, two tests with 6 cases after.
