# tabletest-claude-plugin

Claude Code plugin with two Java/Kotlin skills:

- `tabletest` (`/tabletest`) — write and convert JUnit tests to TableTest.
- `spec-by-example` (`/spec-by-example`) — clarify behaviour through example tables.

Canonical skill files: `skills/<skill>/SKILL.md`.

## Commits

One-line conventional commits (`feat:`/`fix:`/`docs:`/`chore:`), no body, no attribution footer.

## Release

1. Bump the version in `.claude-plugin/plugin.json`; add a `CHANGELOG.md` entry.
2. Commit and push to `main`.
3. `git tag vX.Y.Z && git push origin vX.Y.Z`.

The tag triggers `.github/workflows/release.yml`, which creates the GitHub release from
`CHANGELOG.md`. Never `gh release create` manually — always use the tag.

## Evals

Per-skill evals with variant comparison validate the skills. Directory roles:

- `evals/<skill>/eval-X-name/` — definitions: `prompt.md`, `expected_output.md`, `eval.json`
  (id, slug, skill, assertions, optional `timeout_ms`).
- `iterations/<skill>/iteration-N/` — results: `benchmark.json` (pass rates, tokens),
  `eval-review.md` (report + regression detection), and per-eval `outputs/`, `grading.json`,
  `timing.json`. Variant results nest under `<variant-name>/` with their own numbering.
- `skill-variants/<skill>/<name>/` — in-development skill copies, never shipped
  (`SKILL.md` + `references/`); absent when nothing is in development.

Results dirs are trimmed each cycle; older ones (summaries + full artefacts) stay in git
history for comparison, audit, and re-grading. Conversation and run logs are never committed.

**Running:** `scripts/run-evals.js`, `--skill` required.

```bash
node scripts/run-evals.js --skill tabletest --iteration N [--evals 1,2] [--compare-iteration M]
```

**Testing the runner:** `node --test scripts/run-evals.test.js` (~1s, no network, no
dependencies — `node:test` is built in). Covers the machinery a score depends on: grader
response parsing, majority voting, retry/backoff, the abort-on-grading-failure path, the
fingerprint guard. Run it after touching `run-evals.js` — a silent bug there corrupts every
measurement downstream, and the failure mode is a plausible-looking number rather than a
crash. The eval definitions in the suite are fabricated, never real ones, so the file carries
no answer keys.

Regression detection compares against the previous iteration's `benchmark.json` (same
variant); `--compare-official` also compares a variant against the latest official baseline.

**Timeouts:** the runner's default is 600s. An eval whose recent runs exceed ~60% of its
budget needs an explicit `timeout_ms`, or it will eventually time out, score 0, and swing the
next two reports — once down, once back up as phantom "improvements" across every assertion
it owns. Suspect this whenever a delta shows `compiles`, `has-tabletest-dependency` or a
format assertion "improving"; those do not improve on their own.

**Cost control (tabletest):** almost all run cost is *generation* — solving the eval, not
grading it. Assertion count is close to free (deterministic assertions are code checkers, LLM
assertions share one batched call per eval), so cost is controlled by choosing which evals to
run, never by trimming assertions. Measured on the full suite: ~$14.90 generation against ~$0.65
grading on sonnet (~$0.33 on haiku) — grading is ~4% of a run, which is why grader *quality* is
worth paying for and grader *thrift* is not.

**An eval earns a place in the iteration loop by having at least one assertion that is unique
to it and has failed in a recent baseline.** Everything else is promotion-time regression
evidence. Two consequences worth stating, because both are easy to get wrong:

- An eval at 100% teaches nothing during iteration, however good it is. Keep it for promotion.
- Near-duplicate evals bill separately for the same finding. The four conversion evals
  (25/26/27/28) are one task from four source frameworks, and their eval-unique assertions
  (`no-*-syntax`, `*-dependency-removed`) rarely move — so a failure they surface is usually in
  an assertion the other three share. Run **one** per iteration cycle, rotating; all four at
  promotion.

Pick the loop per change, from the previous baseline's failing-assertion set, rather than
reusing a fixed list — the discriminating core drifts as failures are fixed. The full suite is
~$15 and ~73 min; a well-chosen loop is a quarter of that with strictly more signal.

### Developing a variant

`skills/` is always the published version — never iterate on it directly.

1. Copy the **full** published skill dir (`SKILL.md` + `references/`) to
   `skill-variants/<skill>/next/`. The runner swaps the whole skill dir for the variant, so a
   partial copy silently drops references.
2. Benchmark against the baseline:
   `node scripts/run-evals.js --skill tabletest --variant next --iteration N --compare-official`.
   The `eval-review.md` "Load-Bearing Assertions" and resource-comparison sections show what
   the change wins and loses — but read them alongside the artefacts (see below), never alone.

**HEAD gotcha:** official-skill runs test the last *commit* (the worktree is created from
HEAD), while variant files, prompts, and scaffolding come from the live working tree. Variant
edits apply immediately; `skills/` changes only after committing.

### Analysing a variant outcome

**Gate: do not start the next iteration until every entry in the run's `analysis-todo.md` has a
cause filled in from an artefact.** Each run writes that file next to `eval-review.md`, listing
every assertion whose verdict moved against the comparison baseline, with the grader's words and
the paths that explain it. It exists because the loss list *reads* like evidence — it arrives
compact and causal-sounding — so opening the artefacts feels like confirming what you already
know. That is the trap, and skipping the step has produced a confident wrong fix list and a wasted
iteration.

**The report tells you which assertions moved. It does not tell you why, and its grader
justifications are not evidence of cause.** Before attributing a delta to a specific edit, read
the artefacts under `iterations/<skill>/<variant>/iteration-N/<eval>/`:

- `outputs/` — the generated test code. This is the primary evidence: what the guidance actually
  produced. Read it for every assertion you intend to explain — not one eval and then a
  generalisation to the rest.
- `narration.md` — the agent's own account: its visible narration plus the order in which it wrote
  files. Committed, so it survives trimming. This is where you see whether the guidance *fired*
  and in what words ("a combining table for the one genuine precedence question"), and the write
  order shows drafts the final output no longer contains.
- `conversation.jsonl` — the raw transcript `narration.md` is distilled from. Gitignored and
  trimmed each cycle. **Thinking text is unavailable for Claude 5-family models** — they return it
  encrypted (empty `thinking`, signature only), which no runner flag changes. Narration and tool
  calls are all you get, and they are usually enough.

**Two verdicts that look like findings and are not.** A grader can fail an assertion whose own
wording the output satisfies — check the assertion text against the artefact before believing a
loss (`rule-statable-from-table` once failed an output for showing a threshold as a column, which
is what the skill mandates and what its sibling assertion rewards). And a holistic assertion
flipping on two structurally identical outputs is noise, not signal: compare the *structure* of
the two runs' outputs before attributing a flip to wording.

**Check every grader justification against the artefact.** A justification names the right
assertion and can still name the wrong cause — a `description-*` failure whose real trigger is a
scenario name, say. Graders also misfire outright, failing an assertion whose own wording the
output satisfies. An analysis built on justifications alone will produce a confident, wrong fix
list.

**Compare against the right baseline.** `--compare-official` uses the latest *official* benchmark,
which is stale for any eval a mid-batch promotion has already improved — its wins reappear as if
the current variant had earned them. For those evals the previous variant iteration is the true
comparison; reserve the official one for evals no promotion in the open batch has touched.

### Promoting a variant

**A full run per promotion is not required — a full run per *release* is.** The two questions a
run answers have very different prices: "did the change do what I aimed it at?" needs only the
discriminating loop (~$4), while "did it break something else?" needs all 17 evals ($15). Only
the second is expensive, and it does not have to be asked once per promotion.

1. Replace `skills/<skill>/` with the variant; reconcile `references/` (drop any the new
   SKILL.md subsumes); delete `skill-variants/<skill>/next/`.
2. Add a user-facing `CHANGELOG.md` entry under `## [Unreleased]` (no variant/development
   terminology). **Do not bump `.claude-plugin/plugin.json`** — see below.
3. Commit (`feat:`). The variant's own loop result — graded in the standard regime, against the
   same loop run on the previous skill version — is the promotion evidence. **No full run yet.**
4. Repeat 1–3 for further variants, accumulating entries under `## [Unreleased]`. Nothing is
   released mid-batch.
5. **Close the batch with one official full iteration.** Its regression report is the evidence
   for every promotion in the batch, and its `benchmark.json` becomes the new baseline.
6. Trim results: keep only the latest official baseline's iteration dir (commit its
   `benchmark.json`, `eval-review.md`, `outputs/`, `grading.json`, `timing.json`); delete the
   rest, which git history retains. The next run reads the baseline from disk.
7. Tag and release (see Release) — that is where the single version bump for the whole batch
   happens, and where `## [Unreleased]` becomes `## [X.Y.Z] - <date>`.

**One version bump per release, not per promotion.** A version is a publication fact: it names
something a user can install. Numbering intermediate promoted-but-unreleased states burns
versions on things that were never published, and buys nothing — `run-evals.js` never reads
`plugin.json`, so the version is invisible to every comparison. What identifies a skill state to
the instrument is the git commit (official runs build their worktree from HEAD) and the
`skill_commit`/`skill_digest` provenance stamped into each `benchmark.json`.

**When the batch run shows a regression, bisect — don't re-run the suite.** Re-run *only the
regressed evals* against each promotion commit in the batch. That is per-eval money (~$1 each),
so attributing a two-eval regression across four promotions costs a few dollars, not $60. The
saving over a full run per promotion grows with batch size and is only spent when a regression
actually appears.

**Batching does not cost you isolation when the variants are eval-disjoint.** Check which evals
carry each variant's target assertions before sequencing; when two variants' target sets do not
overlap, both can be developed and validated in parallel, each on its own loop, and the eval
partition supplies the isolation that serialised full runs were meant to buy. Two caveats:
disjoint *targets* are not disjoint *effects* — every variant edits the same SKILL.md and added
length perturbs everything, which is exactly what the batch-closing run is for — and two variants
editing SKILL.md must be reconciled before that run. Targets overlap more often than the failing
sets suggest: two variants editing the same *section* collide even when their assertions differ.

**Optional per-promotion regression tier.** If a batch is long enough that deferring all
regression signal feels risky, add the evals sitting at zero failures in the current baseline
(read them off its `benchmark.json`). They teach nothing during iteration, which is exactly why
they are good sentinels: they have nowhere to go but down. Loop + sentinels is roughly half a
full run, with per-promotion attribution preserved.

**Three things that do not save money.** Trimming assertions (grading is a rounding error
against generation). A cheaper generation model (the model must match or every delta is
meaningless). Reusing stored outputs across promotions — `--grade-only` is valid across
*grading* changes only; any skill edit invalidates every stored output. If wall-clock rather
than cost is the constraint, generation parallelism is the lever, and it does not reduce spend.

### Evolving the eval suite

The suite is the instrument, the skill the subject — a score delta means something only when
exactly one of them changed between the compared iterations.

- **Sequence, don't interleave.** Freeze the suite within a comparison cycle. Land suite
  changes as their own commits (`feat(evals):`, `fix(evals):`), re-baseline the published
  skill, then resume skill iteration.
- **Re-baseline cheaply.** Assertions/checkers only changed → re-grade stored outputs with
  `--grade-only` (no generation cost). Prompts/scaffolding changed → re-run the published skill
  for the affected `--evals`; `--compare-official` merges the newest result per eval.
- **Cross-suite regression reports are noise** — added/renamed assertions show as spurious
  regressions. Re-baseline instead of interpreting them.
- **Grading regime is part of the instrument too.** The standard grading model is **sonnet**
  (`claude-sonnet-5`), switched from haiku on 2026-07-25 on measured grader *accuracy* — see
  "Grader accuracy" below. A comparison across a change of grading model, of `--grade-runs`, or of
  `LLM_GRADING_BATCH_SIZE` is not a comparison.
  **There is no `temperature` on the current grading model.** Sampling parameters were removed from
  the newer model families: a non-default `temperature` is a 400, so `acceptsTemperature()` omits it
  and grading runs at model-default sampling. "Temperature 0" was a haiku-era regime and cannot be
  restored on sonnet. **Measured 2026-07-25 over three `--grade-only` passes on identical stored
  outputs: 18 of 365 slots unstable — 4.9%, level ranging 320–327.** A 7-slot spread on byte-identical
  inputs, so the **single-run MDE is ~7–8 slots** — wider than the entire headroom most skill changes
  have (slice 1's whole realistic ceiling was estimated at +5 to +7). Practical consequence:
  **`--grade-runs 3` is required for any comparison, not reserved for adjudicating one inside the MDE.**
  Majority voting took haiku from ~4% to 1.4% and should do the same here; without it no skill delta
  this project can produce is attributable. Read the failing *set*, never the headline rate.
  **44% of the instability is two assertions** — `rule-statable-from-table` (4 flips) and
  `minimal-rows-per-concern` (4) — so fixing or voting just those two roughly halves the rate. See
  `docs/assertion-triage.md`.
  Those models also run **adaptive thinking by default**, so the verdict JSON is not `content[0]`
  (`extractGradingText` selects the text block) and thinking bills against `max_tokens` — hence the
  larger budget for them. Grading is ~4× slower than haiku. **Measured 2026-07-25 over four full-suite
  `--grade-only` passes: 22, 35 and 83 minutes wall clock** (the 83 included retries after a transient
  `fetch failed` late in the run). Budget **20–90 minutes per full regrade**, not "a bit over ten" —
  the earlier "exceeds ten minutes" was a floor and reads as an estimate, which is how a session ends
  up planning three regrades into an afternoon that cannot hold them. Always background it, and price
  a variance probe (three passes) at **1–4 hours**, not one.
  Sharper assertion *wording* does not fix grader disagreement and has been tried twice: the
  disagreement is response-level, so it is a sampling problem, not a prompt problem. **Check
  call parameters before rewriting prompts.**
  `LLM_GRADING_BATCH_SIZE` (10) is part of the regime as well: grading assertions one at a time
  is systematically *stricter*, not merely less cross-contaminated, and drops every eval's score
  uniformly. Validate any regime change against *level* — does a known-good eval hold its score —
  and not only against variance.

- **Grader accuracy — measure it, don't infer it from agreement.** Every earlier grader measurement
  (temperature, `--grade-runs`, majority voting) measured *precision*: whether the grader agrees with
  itself. None measured whether it is *right*. Majority-voting a reproducibly wrong verdict just
  makes it stable. `docs/grader-answer-key.json` holds verdicts established by reading the stored
  iteration-40 artefacts; `node scripts/score-grader.js --iteration N [--grading-suffix S]` scores a
  grading run against it. Measured 2026-07-25 on identical stored outputs and identical assertion
  text: **haiku 16/21, sonnet 20/21** — the switch is worth ~19 accuracy points for about +$0.32 per
  full run. Re-score after any assertion-wording change; a wording fix that does not move the score
  did not work. **A disagreement is not automatically the grader's fault** — one key entry was itself
  wrong and the sonnet grader disproved it (kept in the file as a worked example). Re-read the
  artefact before changing an assertion.
- **Fingerprint guard (automatic).** Each `benchmark.json` result is stamped with a content
  fingerprint of its definition (`prompt.md`, `eval.json`, `expected_output.md`, `project/`);
  reports compare only matching evals and exclude changed ones as "not comparable". Pre-guard
  benchmarks (no fingerprint) count as comparable.
  **The guard protects comparison, not re-grade validity** — it excludes changed evals when
  comparing two iterations, but a fresh `--grade-only` writes a benchmark stamped with the
  *current* fingerprint, so re-grading a stored output whose prompt has since changed scores a
  stale response as if it were valid, silently. Check what changed before re-grading.
- **Skill provenance (automatic).** Each `benchmark.json` records `skill_commit` (repo HEAD at
  run time) and `skill_digest` (content hash of the skill directory the agent was actually
  handed, after any variant was applied), so a stored result still answers "which skill state
  produced this?" once the iteration dirs around it are trimmed. A `--grade-only` run *inherits*
  these from the benchmark already in the iteration dir rather than restamping — provenance
  belongs to the generation, and today's skill did not write yesterday's answers. Runs predating
  this record `unknown`.

### Contamination protocol

`docs/`, `iterations/`, `skill-variants/`, each `eval.json`/`expected_output.md`, and the
project markdown (`README.md`, `AGENTS.md`, `CHANGELOG.md`) hold answer keys, prior responses,
and eval strategy — an agent exploring during a run could use them and invalidate results.

`run-evals.js` isolates automatically: it runs in a git worktree with those files removed and
`--disallowedTools "Bash(git:*)"` so history can't recover them.

Running manually, replicate it: create a worktree, `rm -rf docs iterations skill-variants`,
`rm -f {README,CLAUDE,AGENTS,CHANGELOG}.md`, remove eval answer keys
(`find evals -name eval.json -o -name expected_output.md | xargs rm -f`), run with
`--disallowedTools "Bash(git:*)"`, then remove the worktree.
