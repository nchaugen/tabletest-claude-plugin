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

Skills are validated via a benchmark in `skills-workspace/`:

```
evals/evals.json          ← eval definitions (prompts + assertions) — source of truth
iteration-N/
  benchmark.json          ← aggregated pass rates and token counts
  eval-review.md          ← human-readable report with regression detection
  eval-X-name/
    with_skill/           ← run with current skill active
      outputs/            ← model's actual output files
      grading.json        ← assertion pass/fail with evidence
      timing.json         ← duration_ms and total_tokens
    no_skill/             ← baseline without skill (--baseline or --baseline-only)
      ...same structure
```

**Running evals:** Use `scripts/run-evals.js`. See `docs/superpowers/specs/2026-03-22-eval-automation-design.md` for the full design.

```bash
node scripts/run-evals.js --iteration N              # run all evals
node scripts/run-evals.js --iteration N --evals 1,2  # run specific evals
node scripts/run-evals.js --iteration N --baseline      # run both with and without skill
node scripts/run-evals.js --iteration N --baseline-only  # run without skill only
```

**Regression detection:** The script compares with_skill scores against the previous iteration's benchmark.json and flags any assertion that passed before but fails now.

### Contamination Protocol

`docs/` contains ideal answer tables and spec documents describing eval strategy and known weaknesses. `skills-workspace/iteration-*/` directories contain prior model responses to the same eval prompts. `skills-workspace/evals/evals.json` contains assertions that describe the exact expected output structure. `README.md`, `CLAUDE.md`, and `CHANGELOG.md` describe eval strategy and project context. Agents exploring the codebase during eval runs could discover and use any of these, invalidating results.

**The `run-evals.js` script enforces this automatically** — it creates a git worktree and removes contaminating files. Git tools are disallowed at runtime (`--disallowedTools "Bash(git:*)"`) so agents cannot recover deleted files from history.

**If running evals manually** (without the script), you must:
1. Create a worktree: `git worktree add /tmp/eval-run`
2. Remove docs: `rm -rf /tmp/eval-run/docs`
3. Remove project files: `rm -f /tmp/eval-run/{README,CLAUDE,CHANGELOG}.md`
4. Remove prior iterations: `rm -rf /tmp/eval-run/skills-workspace/iteration-*`
5. Remove eval definitions: `rm -f /tmp/eval-run/skills-workspace/evals/evals.json`
6. Run evals from the worktree with `--disallowedTools "Bash(git:*)"`
7. Clean up: `git worktree remove /tmp/eval-run`
