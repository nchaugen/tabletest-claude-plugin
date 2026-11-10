Hand-authored reference answer for eval-40, from
`products/claude-plugin/plans/slice-12-swift-skill.md` step 3. Not agent output. Must never be read
as a skill measurement.

Two tables: the code and the count of wrong codes, within the pickup window; and the 72-hour
window. The parcel's pickup code is a fixture. `Codable` on `PickupResult` lets an expectation cell
read `wrongCode(attemptsLeft: 2)` as Swift spells it; the stub stays unimplemented, so the tests
compile and fail.

The rules do not say what a blocked parcel past 72 hours gives. This answer takes
`returnedToDepot`, and the row "Three wrong codes, past its time" states that reading; the doc comment
says the rules leave it open.
