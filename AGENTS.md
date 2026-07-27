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

**Run it outside any command sandbox** (in Claude Code, with the sandbox override) — every
invocation, including `--grade-only`. Sandboxed, grading fails with a wall of `fetch failed` that
reads like an API outage, Gradle cannot start so every eval fails `compiles`, and — the damaging
part — the eval agents lose their own Bash tool. Without `ls` they cannot find a provided
`src/main`, so on any eval whose prompt does not name the exact path they conclude the project is
empty and write tests against an invented API. That produces a plausible report in which those
evals answer a different task than the baseline did, and nothing in the output says so. Verify with
`grep -l "Bash is down" iterations/**/narration.md` and by checking each output's package and API
against the eval's `project/src/main`.

**Testing the runner:** `node --test 'scripts/*.test.js'` (~1s, no network, no
dependencies — `node:test` is built in). Covers the machinery a score depends on: grader
response parsing, majority voting, retry/backoff, the abort-on-grading-failure path, the
fingerprint guard, and what a comparison could not cover. Run it after touching `run-evals.js` — a
silent bug there corrupts every measurement downstream, and the failure mode is a plausible-looking
number rather than a crash. The eval definitions in the suite are fabricated, never real ones, so the
file carries no answer keys.

Regression detection compares against the previous iteration's `benchmark.json` (same
variant); `--compare-official` also compares a variant against the latest official baseline.

**Timeouts:** the runner's default is 600s. An eval whose recent runs exceed ~60% of its
budget needs an explicit `timeout_ms`, or it will eventually time out, score 0, and swing the
next two reports — once down, once back up as phantom "improvements" across every assertion
it owns. Suspect this whenever a delta shows `compiles`, `has-tabletest-dependency` or a
format assertion "improving"; those do not improve on their own.

**Spend runs only on questions reading cannot answer.** A run buys exactly one thing: evidence about
what the guidance *causes an agent to do*. Everything else about a change is free to check — whether
it is self-consistent, whether it contradicts a neighbouring passage, whether its illustrations would
pass the assertion they target, whether it covers the artefacts it was drawn from. **Buying a run to
answer a question a read would have answered is the most common way to overspend here**, and it costs
twice: the money, and an iteration spent attributing a result to guidance that was wrong on its face.
Two questions before every run:

- **Has the read-back pass been done?** (§ Developing a variant, step 2.) Minutes against dollars.
  Correctness defects never need a run — a loop cannot tell you a rule contradicts itself, and will
  happily return a plausible number while it does.
- **Does the loop host the target assertion?** If no eval in it carries the assertion the change
  aims at, the run measures nothing about the change, however good its total looks.

The corollary is not "run less" — it is **run for firing evidence and nothing else**. Salience is
real, it is invisible to reading, and it is worth paying for: guidance can be correct, unambiguous,
consistent with its neighbours, and still be skipped by the agent.

**Cost control (tabletest):** almost all run cost is *generation* — solving the eval, not
grading it. Assertion count is close to free (deterministic assertions are code checkers, LLM
assertions share one batched call per eval), so cost is controlled by choosing which evals to
run, never by trimming assertions. Grader *quality* is worth paying for and grader *thrift* is not.

Both costs are recorded per run, so read them from the artefact rather than from memory:
generation as `cost_usd` per eval, grading as `summary.grading` in the same `benchmark.json`
(with the assumed per-token rates stamped alongside). A benchmark predating that record, or one
rebuilt from older gradings, reports `grading: null` — unknown, not free.

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
reusing a fixed list — the discriminating core drifts as failures are fixed. A well-chosen loop is
roughly a quarter of a full suite's cost and time, with strictly more signal. For the absolute
figures read `summary.total_cost_usd` and `summary.total_duration_ms` off a recent `benchmark.json`
rather than a number written down here.

**Then weigh the loop by price too — evals are not interchangeable units of spend.**
`node scripts/eval-costs.js --skill <skill>` prints mean generation cost, grading cost and wall-clock
per eval across every stored run, cheapest first. **The spread is about sevenfold**, so two loops
with equal signal can differ several-fold in cost, and "five evals" says almost nothing about what a
run will cost. Two consequences:

- **A smoke loop is affordable in a way a full loop is not.** The cheapest hosts of an assertion run
  in about a minute for small change; the dearest take ten minutes and dollars. When a targeted
  assertion has a cheap host, an early probe on it costs a fraction of the loop and answers "does
  this fire at all?" before the expensive evals are committed.
- **Signal still wins.** Price is a tiebreak between evals that carry the target, never a reason to
  drop the eval that carries it. A cheap loop measuring nothing is the most expensive kind.

Costs drift with the suite and the model, so read the script rather than a figure quoted in a plan.

### Developing a variant

`skills/` is always the published version — never iterate on it directly.

1. Copy the **full** published skill dir (`SKILL.md` + `references/`) to
   `skill-variants/<skill>/next/`. The runner swaps the whole skill dir for the variant, so a
   partial copy silently drops references.
2. **Read the draft back before paying for it.** Three checks, all free, all against text you already
   have. They catch a different class of defect from a run, and the class they catch is common:
   every cluster so far has turned out to be a *conflict* in the skill rather than a gap.

   - **Run each targeted assertion's own decidable test against your own illustration.** The
     assertion texts in `evals/<skill>/*/eval.json` are written as mechanical tests, so they work as
     a linter on the examples you just wrote. A drafted title example once failed
     `title-states-system-behaviour`'s own strike-out test — the illustration could not have passed
     the assertion it existed to fix.
   - **Grep `SKILL.md` for every term the edit introduces, and read what already says something
     about it.** Guidance that contradicts an adjacent passage loses to the adjacent passage. One
     draft licensed keeping a combining table three lines below *Wiring is not a rule*, which
     rejects it.
   - **Apply the draft rule by hand to the offending values in the stored artefacts.** They are
     already paid for, and they are the exact inputs the guidance has to catch.

   **What this cannot tell you is whether the guidance fires.** A rule can be correct, unambiguous,
   consistent with its neighbours, and still be skipped: cluster 3 taught the "force N" naming shape
   explicitly and two evals wrote it anyway. Reading tests correctness; only generation tests
   salience. Do both, in this order — the free one first.
3. Benchmark against the baseline:
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

**Read the coverage line before the deltas.** The to-do opens with `N of M evals comparable`. An
empty moved list satisfies the gate vacuously if nothing was comparable, so the two cases are now
stated apart: a void comparison gets a `⛔ Nothing was compared` section and the run exits **2**
(the run itself succeeded and its results are saved — exit 2 is never a reason to re-run and pay
again). A partial comparison names each excluded eval and why. Silence about exclusions used to read
as agreement; it no longer is silent, but the number is still yours to read.

**One run does not settle a flip-prone slot.** Measured on the current instrument 2026-07-26:
**17 of 365 slots flip** across three identical re-grades, and `minimal-rows-per-concern` (×4) plus
`rule-statable-from-table` (×3) are seven of them. `docs/assertion-triage.md` holds the per-slot list
and the diagnosis — read it rather than guessing which of your moved verdicts are real.

When a cluster's moved verdicts land on any of those slots, confirm with a **re-grade of the same
stored outputs** (`--grade-only --grading-suffix`) before attributing — generation is the expensive
half and you already have it. **Do not reach for `--grade-runs 3`:** majority-of-3 was measured
against the answer key and scored 61/68, exactly matching a single pass, because the split slots lean
wrong rather than wobbling symmetrically. Voting costs 3× and buys nothing here.

**Judge a cluster by whether its targeted slots moved, never by the net.** The whole-suite score
spread is 318/323/322 across identical re-grades — the size of a typical cluster's entire effect. Two
consequences: a net figure is not evidence, and **the stored baseline is the low draw of its own
distribution** (318 against a mean of 321), so every comparison against it flatters the variant by
roughly 3 slots.

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

**Compare against the right baseline.** `--compare-official` merges the newest official result per
eval, so it is correct **provided each mid-batch promotion wrote its loop result into the official
tree** (step 4 of Promoting a variant). Skip that step and the official benchmark is stale for every
eval an earlier promotion already improved, and those wins reappear as if the current variant had
earned them. `node scripts/check-baseline.js --skill <skill>` tells you which benchmark will be used
and whether its definitions are current.

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
4. **Copy the promoted loop result into the official tree as a partial iteration** —
   `iterations/<skill>/next/iteration-N/` becomes `iterations/<skill>/iteration-M/`, next number up.
   `loadOfficialBenchmark` merges the newest result *per eval* across official iterations, so
   `--compare-official` then returns the promoted numbers for the evals in the loop and the old
   baseline for everything else. Without this step the baseline is a per-eval fact recorded only in a
   plan's prose, and the next cluster compares against results the promotion already superseded.
   The copy is honest: the variant's `skill_digest` is the digest of the skill you just promoted —
   check it matches if you want to be sure. Note `skill_commit` names the commit HEAD was at during
   generation, which is the commit *before* the promotion.
5. Repeat 1–4 for further variants, accumulating entries under `## [Unreleased]`. Nothing is
   released mid-batch.
6. **Close the batch with one official full iteration.** Its regression report is the evidence
   for every promotion in the batch, and its `benchmark.json` becomes the new baseline. Then trim:
   keep only that iteration dir (commit its `benchmark.json`, `eval-review.md`, `outputs/`,
   `grading.json`, `timing.json`) and the partial iterations it supersedes go; git history retains
   them. The next run reads the baseline from disk. **Before deleting anything, check
   `docs/grader-tuning.md` § The sweep** — outputs whose fingerprints still match are re-gradable and
   must survive, and a variance probe must be distilled before it is swept.
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
- **When a grading run dies part-way, grade the failures then `--rebuild`.** A fatal grading
  failure deliberately writes no benchmark, which strands the gradings that succeeded. `--rebuild`
  assembles the benchmark from the `grading{suffix}.json` files already on disk — no API calls, no
  key needed, seconds rather than a full pass. **Do not re-run the full command to recover**: it
  re-grades every eval and throws away the work you already paid for. The error message prints both
  commands in the right order.
- **Cross-suite regression reports are noise** — added/renamed assertions show as spurious
  regressions. Re-baseline instead of interpreting them.
- **The grading regime is part of the instrument.** Standard model is **sonnet**
  (`claude-sonnet-5`). **A comparison is void across a change of grading model, `--grade-runs`,
  `LLM_GRADING_BATCH_SIZE`, or the grader's response schema.** Re-baseline instead. Current
  measurements — instability, accuracy, level, what each batch of assertion edits moved — live in
  `docs/assertion-triage.md`; read that rather than trusting a figure remembered from a past session.
- **Changing the grader itself is a separate workstream with its own procedure:
  `docs/grader-tuning.md`.** Read it before any `--grading-suffix` work. It covers what a suffix
  names, measuring accuracy against the answer key rather than agreement, variance probes, promoting
  a regrade to the plain name, and the sweep that hands a single unambiguous baseline back. Skipping
  it is what left nineteen benchmarks in iteration-40 and voided a skill-iteration run.
- **Check the baseline is live before spending a loop against it.**
  `node scripts/check-baseline.js --skill tabletest` names every eval whose definition has moved since
  the benchmark `--compare-official` would pick. It is a second, and the failure it catches otherwise
  surfaces only after the run is paid for.
- **Every run appends a row to `docs/measurement-ledger.md`.** You do not append; you **annotate** the
  `Note` column when the analysis gate closes, and you **never delete a row**. The ledger outlives the
  iteration directories, so once results are trimmed it is the only record that a measurement
  happened — and the only durable source for what a run costs and takes. Read its header before
  comparing two rows: a score column is not a trend line, because the instrument moves under it.
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
