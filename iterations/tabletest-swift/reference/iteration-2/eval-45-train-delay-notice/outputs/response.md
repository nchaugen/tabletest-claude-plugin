Hand-authored reference answer for eval-45, from `products/claude-plugin/plans/slice-13-swift-skill-field-feedback.md` step 1. Not agent output. Must never be read
as a skill measurement. Eval-45 has no target assertion yet (step 5 decides it from the baseline),
so this answer shows one conforming form only.

Two tables at the seam the code has: the notice a departure gets (`notice(for:)`, with both sides of
the 5-minute and the 60-minute lines), and the wording of each notice through `text(for:)`, one
row for each notice. `Codable` on `Notice` lets a cell read `delayed(minutes: 5)`.
