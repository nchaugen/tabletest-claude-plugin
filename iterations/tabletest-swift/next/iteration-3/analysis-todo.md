# Analysis to-do — tabletest-swift variant=next, iteration 3

Compared against **official (iterations 9, 8, 7, 6, 5, 4, 3, 2, 1 merged)**, grading claude-sonnet-5-5/default.

**1 of 1 evals comparable.**

**1 assertion verdict moved.**

These are deltas, not attributions. Before explaining any of them, read the generated
output for that eval and its narration — a grader justification can name the right
assertion and still name the wrong cause, and graders do misfire outright. Fill in the
cause line from the artefact, not from the justification.

Do not start the next iteration until every line below has a cause.

## WON `irrelevant-input-is-a-value-set` — eval-39-playlist-screen-columns

Grader said: _`{no change, changes the order}` on the library row; `{playlist, playlist with an unread rule}` for as-opened_

- Output: `eval-39-playlist-screen-columns/outputs/`
- Narration: `eval-39-playlist-screen-columns/narration.md`
- Raw transcript: `eval-39-playlist-screen-columns/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): the restored crossing operation fired, and it groups the combinations by answer (narration line 42). That puts the library's order in `{no change, changes the order}` and the unread rule in `{playlist, playlist with an unread rule}`, which is what this slot asks. Before, the slot was P F F. The crossing slot also passes again: `Every rule removed from a playlist` covers the combination that iteration-2 left out. The reach clause fired as well (line 50): the agent dropped `library × removes every rule` as a combination that cannot happen, although the code reaches it (a no-op on a library with no rule). Watch that clause in the next draws.
