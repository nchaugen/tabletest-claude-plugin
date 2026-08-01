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

**Chain re-baseline commands with `;`, not `&&`.** A re-baseline compares against a baseline that is
stale by definition, so the void-comparison guard fires and the run exits **2**. That is correct, but
with `&&` the first command stops the rest. `--rebuild` also needs `--evals` when the iteration
directory holds fewer evals than the suite, because it loads every eval definition by default.

**When a grading run dies part-way, grade the failures then `--rebuild`.** A fatal grading failure
writes no benchmark, stranding the gradings that succeeded. `--rebuild` assembles the benchmark from
the `grading{suffix}.json` files already on disk — no API calls. Do not re-run the full command to
recover; it re-grades everything and discards work you have paid for. The error message prints both
commands in the right order.

## Measuring whether a change worked

**Measure accuracy, not agreement.** Every grader measurement taken before the answer key existed
measured *precision* — whether the grader agrees with itself. Majority-voting a reproducibly wrong
verdict only makes it stable, so **voting fixes variance and can entrench bias**. Check accuracy
before reaching for `--grade-runs 3`. Measured on the current instrument, voting scored 61/68 —
identical to a single pass — so it is reserved, not merely discouraged.

`docs/grader-answer-key.json` holds verdicts established by reading stored artefacts.
`node scripts/score-grader.js --iteration N [--grading-suffix S]` scores a run against it. Re-score
after any assertion-wording change: **a wording fix that does not move the score did not work.**

**The key is bound to one set of outputs.** Its `scored_against.iteration` names them, and entries
hold only while the assertion text *and* that stored output are unchanged. `score-grader.js` does not
check this: pointed at a different iteration it returns a confident, meaningless percentage. Check
`scored_against` before scoring anything but the iteration the key was read from.

**A single accuracy figure carries about ±2 slots** at the current 68-entry key, so a wording change
that moves accuracy by one or two slots has not been shown to do anything. Take three passes before
believing a small gain.

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

**When a probe produced several passes, promote the one whose total is the median.** Not the first,
not the best. A baseline is a single draw from a distribution several slots wide, and whichever draw
takes the plain name anchors every future comparison: `iteration-40`'s 318 sat at the bottom of its
own 318/323/322 spread, quietly flattering every variant measured against it by about three slots.
The median is the least-wrong single draw available, and choosing it is free.

Promote a whole pass, never a slot-by-slot majority. Voting was measured and does not improve
accuracy (61/68, identical to one pass), and a stitched-together benchmark corresponds to no run that
ever happened.

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

## Capturing the grader's reasoning

`grading{suffix}-thinking.json` sits beside each grading file and holds the grader's own reasoning,
one record per API call: the assertion ids in that batch, the run and attempt number, and the
summarized thinking text. It is the material for diagnosing an unstable slot — with two passes over
identical bytes stored, a flip can be read as *the grader looked at a different part of the output*
or *it applied a different clause of the same assertion*, which need opposite fixes.

**Opt in with `--capture-thinking`; it is OFF by default, and that default is load-bearing.**
The summary text is *additional billed output*. On the suite's largest LLM-assertion eval, grading
output went from **15,783 tokens to 28,553** with capture on. Grading posts a **non-streaming**
request at `max_tokens: 32000`, and Anthropic's guidance is that anything above ~16K output must
stream or the connection drops — so the extra volume pushed eval-18 past the line and it began
failing **reproducibly** with `fetch failed` after a ~10-minute hang, while every smaller eval in the
same batch succeeded. Left on globally it would have made the biggest eval flaky *and* changed the
default regime under every future comparison.

**The underlying flaw predates the flag: grading does not stream.** Any eval whose grading output
approaches 16K is near the edge regardless. Fixing it means streaming the grading request, which
changes the grader's transport — a **void comparison** under AGENTS.md § Evolving the eval suite —
so it belongs in its own window, not bolted onto a probe.

**The reasoning was always being generated and billed; the default just hid it.** On this model
family adaptive thinking is on whenever `thinking` is omitted, and `thinking.display` defaults to
`"omitted"` — so thinking blocks were arriving with an empty text field, and `extractGradingText`
discarded them along with everything that was not the first text block. Nothing extra is being paid
for; `GRADING_MAX_TOKENS_WITH_THINKING = 32000` was already sized for it.

**This is a request change, so it is not free of instrument risk.** `display` is documented as
controlling visibility only — thinking happens and bills the same under every setting — which makes
the risk low, but low is not none. It was validated the way any grading change should be: re-grade
stored outputs and compare. On eval-7, **13/13 both ways, no verdict moved**, at $0.0147 for the
call. Re-run that comparison on a wider set before trusting it across the suite.

**Two gates it does not cross.** `extractGradingText` still takes the first text block, so the
response parsing is untouched; and no `grading.json` schema changed, so `--rebuild`, the report and
the answer key all read exactly what they read before.

**A `fetch failed` on one eval while others succeed is this, not the network.** Check the eval's
`grading_usage.output_tokens` before assuming an outage — near 16K and climbing means the
non-streaming request is the cause.

**Not available on every grader model.** The parameter is gated on the same predicate as `effort`:
the older family (haiku-4-5) takes `{type: "enabled", budget_tokens: N}` and rejects `adaptive`. A
haiku regrade therefore captures nothing, and its `grading-*-thinking.json` is simply absent.
