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
        with_skill/
          outputs/               ← model's actual output files
          grading.json           ← assertion pass/fail with evidence
          timing.json            ← duration_ms and total_tokens
    minimal/                     ← variant results (independent iteration numbering)
      iteration-1/

skill-variants/                  ← experimental skill files (never shipped)
  tabletest/
    minimal/
      SKILL.md                   ← stripped-down skill variant
```

**Running evals:** Use `scripts/run-evals.js`. The `--skill` flag is required.

```bash
node scripts/run-evals.js --skill tabletest --iteration N
node scripts/run-evals.js --skill tabletest --iteration N --evals 1,2
```

**Skill variants:** Test minimized or modified skill files against evals.

```bash
node scripts/run-evals.js --skill tabletest --variant minimal --iteration 1
node scripts/run-evals.js --skill tabletest --variant minimal --iteration 1 --compare-official
```

**Regression detection:** The script compares scores against the previous iteration's benchmark.json (within the same variant) and flags any assertion that regressed. With `--compare-official`, it also compares variant results against the latest official benchmark.

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
