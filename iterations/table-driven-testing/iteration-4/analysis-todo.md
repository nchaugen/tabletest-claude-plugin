# Analysis to-do — table-driven-testing, iteration 4

Compared against **iteration 3**, grading claude-sonnet-5/default.

> ⚠️ **The baseline was graded under a different regime** (claude-haiku-4-5/default vs claude-sonnet-5/default).
> A comparison is void across a change of grading regime — re-baseline rather than interpret this.

## ⚠️ Partial comparison

**3 of 5 evals comparable.** The rest were excluded from them:

- `eval-31-travel-insurance-py` — definition changed since the baseline; the two verdicts are not commensurable
- `eval-32-baggage-fees-py` — definition changed since the baseline; the two verdicts are not commensurable

**1 assertion verdict moved.**

These are deltas, not attributions. Before explaining any of them, read the generated
output for that eval and its narration — a grader justification can name the right
assertion and still name the wrong cause, and graders do misfire outright. Fill in the
cause line from the artefact, not from the justification.

Do not start the next iteration until every line below has a cause.

---

## ✅ Gate closed 2026-08-01 — read this before the list

**The comparison this report made is void.** It compares against `iteration-3`, which was graded on
`claude-haiku-4-5`; a comparison is void across a change of grading model, and § 0 already closed
that gate by annotation.

**The comparison that means something is inside this directory:** `benchmark.json` against
`benchmark-s6b.json` — same stored outputs, same grader, same prompts, only the § 0b assertion texts
moved. It reads **42/44 → 42/44 with nothing moved.** The port of
`no-duplicate-rows-within-a-table` and `scenario-names-describe-conditions` cost this suite nothing,
which is the right behaviour for a one-slot-of-headroom tripwire. Write-up:
`docs/assertion-triage.md` § *Porting the repaired texts to the two thin suites*.

---

## WON `concrete-domain-values` — eval-34-hotel-cancellation-swift

Grader said: _(daysBeforeCheckIn: 15, bookingValue: 730, expectedFee: 365) — concrete integers, no computed expressions_

- Output: `eval-34-hotel-cancellation-swift/outputs/`
- Narration: `eval-34-hotel-cancellation-swift/narration.md`
- Raw transcript: `eval-34-hotel-cancellation-swift/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): void — this is the haiku baseline showing through, not the § 0b edit. The in-iteration pair moved nothing on this eval, and its assertion texts did not change.
