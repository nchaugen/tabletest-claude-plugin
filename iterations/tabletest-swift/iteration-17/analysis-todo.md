# Analysis to-do — tabletest-swift variant=next, iteration 1

Compared against **official (iterations 16, 15, 14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1 merged)**, grading claude-sonnet-5-5/default.

**2 of 2 evals comparable.**

**4 assertion verdicts moved.**

These are deltas, not attributions. Before explaining any of them, read the generated
output for that eval and its narration — a grader justification can name the right
assertion and still name the wrong cause, and graders do misfire outright. Fill in the
cause line from the artefact, not from the justification.

Do not start the next iteration until every line below has a cause.

## WON `no-long-text-cell` — eval-45-train-delay-notice

Grader said: _1 table(s); no cell over 100 characters_

- Output: `eval-45-train-delay-notice/outputs/`
- Narration: `eval-45-train-delay-notice/narration.md`
- Raw transcript: `eval-45-train-delay-notice/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): the table is over `notice(for:)` with a `Notice` cell (`Codable` added); the wording is one wiring check that compares with `text(of:service:)`, so no cell holds a sentence. Narration line 13: "the decision already lives in `notice(for:)`, so I'll put the table there". Baseline draws (iterations 3–7) put the sentences in cells.

## WON `rules-tested-on-the-decision` — eval-45-train-delay-notice

Grader said: _decidesWhichNoticeADepartureGets calls notice(for: and holds 4/5, 59/60_

- Output: `eval-45-train-delay-notice/outputs/`
- Narration: `eval-45-train-delay-notice/narration.md`
- Raw transcript: `eval-45-train-delay-notice/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): same table as the width win: `notice(for:)` with rows 0/4, 5, 59, 60 and a cancelled value set. The variant's first line fired at design (narration 13); the baseline draws declined `notice(for:)` as "not the function under test".

## WON `no-long-text-cell` — eval-46-library-loan-reminder

Grader said: _1 table(s); no cell over 100 characters_

- Output: `eval-46-library-loan-reminder/outputs/`
- Narration: `eval-46-library-loan-reminder/narration.md`
- Raw transcript: `eval-46-library-loan-reminder/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): the app gained `Kind` + `kind(for:)`, which `text(for:)` now switches over; the table holds the kind, one cell. The wording is one wiring check with one sentence split over lines. Narration 9: weighs pulling "the decision logic into a small function that returns an enum". Both baseline draws (iterations 15, 16) put the sentences in cells.

## WON `rules-tested-on-the-decision` — eval-46-library-loan-reminder

Grader said: _choosesTheReminderForALoan calls kind(for: and holds 4/3, 0/-1, -29/-30_

- Output: `eval-46-library-loan-reminder/outputs/`
- Narration: `eval-46-library-loan-reminder/narration.md`
- Raw transcript: `eval-46-library-loan-reminder/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): the table calls the new `kind(for:)` with value sets {4, 60}, {0, 3}, {-1, -29}, {-30, -90}; `verify-tests-pass` P, so no text or boundary moved. The answer names the source change. The baseline draws never weighed a seam (discovery gap); here the variant's seam bullet fired at design.
