Hand-authored reference answer for eval-44, from `products/claude-plugin/plans/slice-13-swift-skill-field-feedback.md` step 1. Not agent output. Must never be read
as a skill measurement.

One table: the accepted replacement and the three refusals. A test-local enum reads the situation
from its cell and builds the box in a member of the enum. `Codable` on `Replacement` and on
`Refusal` lets the result cell read `refused(pollClosed)`; the stub stays unimplemented, so the
tests compile and fail.

The open poll is the contrast row: Ann's vote and the tally change there, and in no refused row,
so the after-state columns vary and the table shows that a refusal changes nothing.
