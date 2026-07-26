# Grader tuning — changing the instrument, and cleaning up after

Two workstreams run against this eval suite, and confusing them has produced wrong answers.

- **Skill iteration** — the common one. The instrument is fixed and the skill changes. `AGENTS.md`
  is the whole procedure; you should not need this file.
- **Grader tuning** — rare. The *instrument* changes: grading model, effort, `--grade-runs`,
  `LLM_GRADING_BATCH_SIZE`, the grader's response schema, or assertion wording. This file is that
  procedure, including the cleanup that hands a single unambiguous baseline back to the common
  workstream.

**Why this is written down at all:** a tuning effort leaves nineteen benchmarks in one iteration
directory, only one of them live. On 2026-07-26 the leftovers silently voided a skill-iteration run —
`--compare-official` resolved to a plain-named file from a superseded regime, the fingerprint guard
correctly excluded every eval in the loop, and `analysis-todo.md` reported "no verdicts moved" over a
comparison that had measured nothing. The tooling now says when a comparison covered nothing
(exit code 2, and a `⛔ Nothing was compared` section in the to-do). This file is the other half:
not leaving the trap set.

## A comparison is void across a change of regime

Model, `--grade-runs`, `LLM_GRADING_BATCH_SIZE`, and the response schema are all part of the
instrument. **Re-baselining, not interpreting, is the sanctioned response to a regime change.** So is
a change of assertion wording: it re-fingerprints the affected evals, and the guard will exclude them
from every subsequent comparison against an older benchmark.

Grading effort is per-eval and deliberately part of the *definition* (`grading_effort` in
`eval.json`), so it feeds the fingerprint. An eval always grades at the same effort, which is what
keeps a mixed-effort suite internally comparable. Choosing effort per run instead would silently make
two runs incomparable.

## Suffixes: what they name

`--grade-only --grading-suffix S` re-grades stored outputs and writes `grading-S.json` per eval, plus
`benchmark-S.json`, `eval-review-S.md`, and `analysis-todo-S.md` in the iteration directory. The
original files are untouched.

**A suffix names one regime, not one attempt.** `-t5` means "tranche-5 assertion texts", not "my
fifth try". Two runs that differ in nothing an analysis will ever care about should not get two
suffixes; two runs whose verdicts are not commensurable must never share one.

Re-grading costs no generation. A full 17-eval regrade is roughly **13 minutes and $1.83** — check
`summary.total_duration_ms` and `summary.grading` on a recent benchmark rather than trusting that
figure. Always background it, and price a three-pass variance probe in hours, not minutes.

**When a grading run dies part-way, grade the failures then `--rebuild`.** A fatal grading failure
writes no benchmark, stranding the gradings that succeeded. `--rebuild` assembles the benchmark from
the `grading{suffix}.json` files already on disk — no API calls. Do not re-run the full command to
recover; it re-grades everything and discards work you have paid for. The error message prints both
commands in the right order.

## Measuring whether a change worked

**Measure accuracy, not agreement.** Every grader measurement taken before the answer key existed
measured *precision* — whether the grader agrees with itself. Majority-voting a reproducibly wrong
verdict only makes it stable, so **voting fixes variance and can entrench bias**. Check accuracy
before reaching for `--grade-runs 3`.

`docs/grader-answer-key.json` holds verdicts established by reading stored artefacts.
`node scripts/score-grader.js --iteration N [--grading-suffix S]` scores a run against it. Re-score
after any assertion-wording change: **a wording fix that does not move the score did not work.**

**A disagreement is not automatically the grader's fault.** Key entries have been wrong and the
grader has disproved them. Re-read the artefact before changing an assertion.

**Validate against *level*, not only variance.** Does a known-good eval hold its score? The batch-size
experiment that motivated `LLM_GRADING_BATCH_SIZE = 1` measured flip-rate only, never level, so it
could not catch that grading assertions in isolation is *systematically stricter* — it collapsed
every eval uniformly. A uniform drop across all seventeen is a calibration break, not solutions
getting worse.

**Two things that do not fix grader disagreement**, both tried more than once: sharper assertion
*wording* (the disagreement is response-level, so it is a sampling problem — **check call parameters
before rewriting prompts**), and grading assertions one at a time. What *does* work is naming the
surface an assertion judges: "judge only the column headers", "judge every `@TableTest` in the class".

**Two API facts about the current model family**, both structural rather than tunable. There is **no
`temperature`** — a non-default value is a 400, so `acceptsTemperature()` omits it and grading runs at
model-default sampling. And **thinking is on by default and billed against `max_tokens`**, so the
verdict JSON is not `content[0]` (`extractGradingText` finds the text block) and the budget must cover
thinking *and* the JSON.

## Variance probes

Two or more gradings of the *same* outputs under one regime. This is the only artefact that can
answer "does this assertion flip?", and it is cheap — regrade only, no generation.

`benchmark-v1/v2/v3.json` on iteration-40 are one such probe: 322 / 320 / 327, with **18 of 365 slots
flipping and 33 failing in all three**. That is the noise floor, and it has two consequences the
common workstream depends on:

- A whole-suite score has a **±3–4 slot run-to-run spread**, so no single run's *net* is evidence.
- A moved verdict on a flip-prone assertion needs a confirming re-grade before it is attributed.

**Distil the per-slot flip list, never just the summary number.** "18/365 flip" is useless later; the
list of *which* slots is what makes a future delta readable. It goes in `docs/assertion-triage.md`,
and this produces it in the right shape:

```bash
node scripts/flip-report.js --skill tabletest --iteration 40 --suffixes ,p1,p2
```

An empty entry means the plain-named `benchmark.json` — **a live baseline graded under the current
regime is already one pass of the probe**, so N passes cost N−1 regrades. The script refuses (exit 2)
when the runs do not share an instrument and a regime, because a flip between those is a change of
instrument rather than grader noise.

**Every regrade appends its own row to `docs/measurement-ledger.md`**, labelled `iteration-N [suffix]`
and marked `regrade` — generation cost and duration read `—` because it spent neither. A probe is
therefore visible in the ledger as *two or more rows sharing an `Instrument` and a `Regime`*, which is
precisely the condition for their scores being comparable. Annotate one of them with the flip finding
and point at the triage section; when the probe is later swept, those rows are what remains of it.

## Declaring a winner

When a regrade becomes the reference, promote it to the plain name **in one commit**. Half a rename
is worse than none.

```
git mv benchmark-t5.json      benchmark.json
git mv eval-review-t5.md      eval-review.md
git mv analysis-todo-t5.md    analysis-todo.md
for d in eval-*/; do git mv "$d/grading-t5.json" "$d/grading.json"; done
```

**All four, or none.** `--rebuild` and the grader-evidence lookup both read `grading{suffix}.json`, so
promoting the benchmark while leaving the gradings suffixed means a later no-suffix `--rebuild`
reassembles from the stale files and **silently resurrects the superseded score**.

The plain name is load-bearing: `--compare-official` resolves to `benchmark.json` and has no way to be
told otherwise. **At most one benchmark per official iteration directory is plain-named, and it is the
live one.**

## The sweep

Cleaning up is part of the workstream, not an optional tidy. Three classes, and only one of them goes.

1. **The live baseline** — plain-named, whole directory. Keep.
2. **Re-gradable outputs** — `outputs/` and `timing.json` for any iteration whose fingerprints still
   match the current definitions. Generation is the only expensive thing here, and these are the raw
   material for every future probe and cheap re-baseline. **Never sweep these while they still
   match.**
3. **Everything else** — dead regrades' `benchmark-S.json`, `eval-review-S.md`, `analysis-todo-S.md`,
   and `eval-*/grading-S.json`. Sweep, once distilled.

**Distil before deleting.** A regrade's finding must be in `docs/assertion-triage.md` first — and for
a variance probe that means the per-slot flip list, not a summary. Git history is recoverable in
principle; nobody reconstructs a measurement from it in practice. Note that a *plan* is not a valid
home for a distillate: plans are deleted when their slice completes.

For a probe you keep, keep the `grading-S.json` files too, not just the benchmarks. The benchmark
carries only failed-assertion ids; the grader's reasoning — the *why* behind a flip — is in the
grading files.

**Delete an iteration directory only when it is triply superseded**: obsolete skill state, obsolete
eval definitions, obsolete regime. The test is *"could any run still be cheaply derived from these
outputs?"* If the fingerprints still match, the answer is yes and it stays. Deleting also loses
`conversation.jsonl` permanently — it is gitignored, so it is not in history. The committed
`narration.md` distillate is what survives, which is why it exists.

**`git rm -r` does not empty an iteration directory.** `conversation.jsonl` and `run.log` are
gitignored, so they survive on disk along with the directory tree holding them — leaving what looks
like a live iteration (3.9 MB of it, in the 2026-07-26 sweep) that every directory listing still
shows. It has no `benchmark.json`, so baseline resolution skips it and nothing breaks quietly, but
finish the job with `rm -rf` and confirm with `ls iterations/<skill>/`.

## Hazards

- **`loadOfficialBenchmark` merges across iterations.** It takes the newest result *per eval* across
  every official iteration directory, reading only plain-named `benchmark.json`. Deleting the newest
  one does not "reset" anything — it silently exposes an older, possibly differently-graded iteration.
  Sweep an old iteration directory entirely, or not at all.
- **`--grade-only` does not respect the fingerprint guard.** The guard protects *comparison*: it
  excludes changed evals when comparing two benchmarks. A fresh `--grade-only` stamps the *current*
  fingerprint onto a stale response, so re-grading an output whose prompt has since changed scores it
  as if it were valid, silently. Check what changed before re-grading.
- **A `--grade-only` run inherits `skill_commit` and `skill_digest`** from the benchmark already in
  the directory rather than restamping. Provenance belongs to the generation; today's skill did not
  write yesterday's answers.

## Handing back

The common workstream resumes when all of these hold:

- Exactly one plain-named benchmark per official iteration directory, and it is the live regime.
- Its fingerprints match the current eval definitions. **Verify, don't assume** — this is the check
  that would have caught the 2026-07-26 failure before it cost a loop:

  ```bash
  node scripts/check-baseline.js --skill tabletest
  ```

  It resolves the benchmark `--compare-official` would reach for and names every eval that no longer
  matches. Exit 0 means live; exit 2 names the stale ones. Pass `--benchmark PATH` to check a
  candidate regrade before promoting it.
- Every dead suffix is distilled into `docs/assertion-triage.md` and swept.
- A `--compare-official` run of any loop reports `N of N evals comparable` rather than exit code 2.
