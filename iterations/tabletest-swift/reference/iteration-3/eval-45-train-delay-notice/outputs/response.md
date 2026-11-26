Hand-authored reference answer for eval-45, from `products/claude-plugin/plans/slice-13-swift-skill-field-feedback.md` step 5. Not agent output. Must never be read
as a skill measurement. It replaces `reference/iteration-2`, whose wording table had four rows of
about 200 characters.

One table at the seam the code has: the notice a departure gets (`notice(for:)`, with both sides of
the 5-minute and the 60-minute lines). The rules are in that table. The wording of each notice is
asserted once, in a plain test, with the sentence split over source lines, so no line is wide.
`Codable` on `Notice` lets a cell read `delayed(minutes: 5)`.
