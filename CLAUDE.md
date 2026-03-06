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

Skills are validated via a manual benchmark in `skills-workspace/`:

```
evals/evals.json          ← eval definitions (prompts + assertions) — source of truth
iteration-N/
  benchmark.json          ← aggregated pass rates and analyst notes
  eval-review.html        ← human-readable viewer
  eval-X-name/
    with_skill/           ← run with skill active
    old_skill/            ← run without skill (baseline)
      outputs/            ← model's actual output files
      grading.json        ← assertion pass/fail with evidence
      timing.json         ← duration_ms and total_tokens
```

Running an eval means invoking the skill (or not) with the eval prompt, capturing output to `outputs/`, grading each assertion in `grading.json`, and updating `benchmark.json`. There is no automation script — runs are done manually.

Skill snapshots used as baselines are stored as `snapshot.md` (not `SKILL.md`) to prevent auto-discovery by Claude.
