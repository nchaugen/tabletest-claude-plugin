# tabletest-claude-plugin

A Claude Code plugin providing two skills for Java/Kotlin developers:
- `tabletest` (`/tabletest`) — write and convert JUnit tests to TableTest format
- `spec-by-example` (`/spec-by-example`) — clarify behaviour through concrete example tables

Canonical skill files: `skills/tabletest/SKILL.md`, `skills/spec-by-example/SKILL.md`

## Commit Messages

Use one-line conventional commits: `feat:`, `fix:`, `docs:`, `chore:`. No body, no attribution footer.

## Release Process

1. Update version in `.claude-plugin/plugin.json`
2. Add entry to `CHANGELOG.md`
3. Commit and push to `main`
4. Tag and push: `git tag vX.Y.Z && git push origin vX.Y.Z`

The `.github/workflows/release.yml` workflow triggers on version tags and creates the GitHub release automatically, extracting notes from `CHANGELOG.md`. Do NOT create releases manually with `gh release create` — use the tag-triggered workflow.

## Eval Framework

Skills are validated via per-skill evals with variant comparison support:

```
evals/                           ← test definitions, per skill
  tabletest/
    eval-X-name/
      prompt.md                  ← user prompt sent to Claude
      expected_output.md         ← criteria for grading
      eval.json                  ← metadata: id, slug, skill, assertions, timeout_ms?
  spec-by-example/
    eval-X-name/

iterations/                      ← results, per skill
  tabletest/
    iteration-N/
      benchmark.json             ← aggregated pass rates and token counts
      eval-review.md             ← human-readable report with regression detection
      eval-X-name/
        outputs/               ← model's actual output files
        grading.json           ← assertion pass/fail with evidence
        timing.json            ← duration_ms and total_tokens
    <variant-name>/              ← variant results (independent iteration numbering)
      iteration-1/

skill-variants/                  ← in-development skill versions (never shipped)
  tabletest/
    <variant-name>/
      SKILL.md                   ← complete skill copy (dir is absent when no version is in development)
      references/
```

Results directories are trimmed after each development cycle; older results live in git history — summaries plus the full artefacts (`outputs/`, `grading.json`, `timing.json`), so past iterations can be compared, audited, or re-graded from history. Conversation and run logs are never committed.

**Running evals:** Use `scripts/run-evals.js`. The `--skill` flag is required.

```bash
node scripts/run-evals.js --skill tabletest --iteration N
node scripts/run-evals.js --skill tabletest --iteration N --evals 1,2
node scripts/run-evals.js --skill tabletest --iteration N --compare-iteration M
```

**Regression detection:** The script compares scores against the previous iteration's benchmark.json (within the same variant) and flags any assertion that regressed. With `--compare-official`, it also compares variant results against the latest official benchmark.

**Two-tier cost control (tabletest):** during variant iteration, run only the discriminating core — `--evals 2,14,15,22,23,26,28,30` (about 40% of full-suite cost; contains every assertion family that has failed in recent baselines). The full suite runs once at promotion as the regression evidence. Full-suite conversion coverage can be rotated (26+28 one cycle, 25+27 the next) if promotion cost matters.

### Developing a New Skill Version

`skills/` is always the published version — never iterate on it directly. Develop the next version as a variant:

1. Copy the **full** published skill directory (`SKILL.md` and `references/`) to `skill-variants/<skill>/next/`. The runner replaces the entire skill directory with the variant at eval time, so a partial copy silently drops the references.
2. Iterate on the variant and benchmark it against the published baseline:

```bash
node scripts/run-evals.js --skill tabletest --variant next --iteration N --compare-official
```

The eval-review.md's "Load-Bearing Assertions" and resource-comparison sections show what the change wins and loses versus published.

**HEAD gotcha:** official-skill runs test the last *commit* (the eval worktree is created from HEAD), while variant files, prompts, and project scaffolding are copied from the live working tree. Variant edits take effect immediately; changes to `skills/` only after committing.

### Promoting a Development Version to Published

1. Replace the contents of `skills/<skill>/` with the variant; reconcile the `references/` set (delete references whose content the new SKILL.md subsumes); delete `skill-variants/<skill>/next/`.
2. Bump the version in `.claude-plugin/plugin.json` and write a user-facing `CHANGELOG.md` entry — describe what changed for users, no variant/development terminology.
3. Commit (`feat:`), then run one official eval iteration. Its regression report against the previous official baseline is the promotion evidence, and its `benchmark.json` becomes the new baseline.
4. Trim results: commit each recorded iteration's `benchmark.json`, `eval-review.md`, `outputs/`, `grading.json`, and `timing.json`, then delete all iteration directories except the latest official baseline — the next run's regression comparison reads it from disk, and everything else remains available in git history for comparison and re-grading (see below). Conversation and run logs are never committed.
5. Tag and release per the Release Process above.

### Evolving the Eval Suite

The eval suite is the measuring instrument; the skill is the subject. A score delta is only meaningful when exactly one of them changed between the two iterations being compared.

- **Sequence, don't interleave.** Freeze the suite within a comparison cycle. Land eval-suite changes as their own commits (`feat(evals):`, `fix(evals):`), re-baseline the published skill against the new suite, then resume skill iteration.
- **Re-baseline cheaply.** If only assertions or checkers changed, re-grade the baseline iteration's stored outputs with `--grade-only` — no generation cost. If prompts or project scaffolding changed, re-run the published skill for just the affected evals (`--evals ...`); `--compare-official` merges the newest result per eval across official iterations.
- **Cross-suite regression reports are noise.** Added or renamed assertions appear as spurious regressions/improvements. Re-baseline instead of interpreting them.
- **Fingerprint guard (automatic).** Each eval result in `benchmark.json` is stamped with a content fingerprint of its definition (`prompt.md`, `eval.json`, `expected_output.md`, `project/`). Reports compare only fingerprint-matching evals; changed evals are listed as "not comparable" and excluded from variant-vs-official totals. Benchmarks from before the guard have no fingerprints and are treated as comparable.

### Contamination Protocol

`docs/` contains ideal answer tables and spec documents describing eval strategy and known weaknesses. `iterations/` directories contain prior model responses to the same eval prompts. Each eval's `eval.json` and `expected_output.md` contain assertions and grading criteria. `README.md`, `CLAUDE.md`, and `CHANGELOG.md` describe eval strategy and project context. Agents exploring the codebase during eval runs could discover and use any of these, invalidating results.

**The `run-evals.js` script enforces this automatically** — it creates a git worktree and removes contaminating files (`docs/`, `iterations/`, `skill-variants/`, project markdown files, eval answer keys). Git tools are disallowed at runtime (`--disallowedTools "Bash(git:*)"`) so agents cannot recover deleted files from history.

**If running evals manually** (without the script), you must:
1. Create a worktree: `git worktree add /tmp/eval-run`
2. Remove docs: `rm -rf /tmp/eval-run/docs`
3. Remove project files: `rm -f /tmp/eval-run/{README,CLAUDE,CHANGELOG}.md`
4. Remove prior iterations: `rm -rf /tmp/eval-run/iterations`
5. Remove skill variants: `rm -rf /tmp/eval-run/skill-variants`
6. Remove eval answer keys: `find /tmp/eval-run/evals -name eval.json -o -name expected_output.md | xargs rm -f`
7. Run evals from the worktree with `--disallowedTools "Bash(git:*)"`
8. Clean up: `git worktree remove /tmp/eval-run`
