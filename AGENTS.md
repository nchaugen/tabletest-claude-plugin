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
run, never by trimming assertions.

**An eval earns a place in the iteration loop by having at least one assertion that is unique
to it and has failed in a recent baseline.** Everything else is promotion-time regression
evidence. Two consequences worth stating, because both are easy to get wrong:

- An eval at 100% teaches nothing during iteration, however good it is. Keep it for promotion.
- Near-duplicate evals bill separately for the same finding. The four conversion evals
  (25/26/27/28) are one task from four source frameworks; their eval-unique assertions
  (`no-*-syntax`, `*-dependency-removed`) pass consistently, so every failure they surface is
  in a shared assertion. Run **one** per iteration cycle, rotating; run all four at promotion.

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
   the change wins and loses.

**HEAD gotcha:** official-skill runs test the last *commit* (the worktree is created from
HEAD), while variant files, prompts, and scaffolding come from the live working tree. Variant
edits apply immediately; `skills/` changes only after committing.

### Promoting a variant

1. Replace `skills/<skill>/` with the variant; reconcile `references/` (drop any the new
   SKILL.md subsumes); delete `skill-variants/<skill>/next/`.
2. Bump `.claude-plugin/plugin.json` and write a user-facing `CHANGELOG.md` entry (no
   variant/development terminology).
3. Commit (`feat:`), then run one official iteration — its regression report vs the previous
   baseline is the promotion evidence, and its `benchmark.json` becomes the new baseline.
4. Trim results: keep only the latest official baseline's iteration dir (commit its
   `benchmark.json`, `eval-review.md`, `outputs/`, `grading.json`, `timing.json`); delete the
   rest, which git history retains. The next run reads the baseline from disk.
5. Tag and release (see Release).

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
- **Fingerprint guard (automatic).** Each `benchmark.json` result is stamped with a content
  fingerprint of its definition (`prompt.md`, `eval.json`, `expected_output.md`, `project/`);
  reports compare only matching evals and exclude changed ones as "not comparable". Pre-guard
  benchmarks (no fingerprint) count as comparable.

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
