Hand-authored reference answer for eval-45, from `products/claude-plugin/plans/slice-13-swift-skill-field-feedback.md` step 5. Not agent output. Must never be read
as a skill measurement. It replaces `reference/iteration-3`, which asserted each of the four
sentences in a plain test (`decisions/test-the-key-not-the-wording.md`: a person reads the wording,
so a copy of a constant sentence checks only that the two copies agree).

One table at the seam the code has: the notice a departure gets (`notice(for:)`, with both sides of
the 5-minute and the 60-minute lines). The rules are in that table. One plain test shows that
`text(for:)` renders the decided notice for the departure, with the sentence split over source
lines. The wording itself is for a reader to review. `Codable` on `Notice` lets a cell read
`delayed(minutes: 5)`.
