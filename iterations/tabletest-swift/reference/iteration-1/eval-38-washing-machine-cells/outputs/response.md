Hand-authored reference answer for eval-38, from
`products/claude-plugin/plans/slice-12-swift-skill.md` step 3. Not agent output. Must never be read
as a skill measurement.

One table per rule of the doc comment. Each table varies only the settings its rule reads, as
columns, and the body builds the `WashProgram` with the other settings at their defaults.

Two app types gained one word each: `CaseIterable` on `Fabric`, so a cell reads `wool`, and
`Codable` on `WashOutcome`, so the expectation cell reads the case as Swift spells it, such as
`runs(minutes: 140)` or `tooHot(limit: 60)`. Nothing else in the app changed.
