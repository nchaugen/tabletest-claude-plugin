# Eval Variant Comparison — Design Notes

Status: draft / not yet implemented

## Problem

The current eval framework compares iterations (current vs previous) with a binary with_skill/no_skill split. This is too coarse for two key use cases:

1. **Skill ablation** — understanding which parts of the skill instructions actually matter vs. what the model figures out on its own
2. **Model upgrade assessment** — when a new model version ships, understanding whether parts of the skill are now redundant

## Current State

- `--baseline` runs with_skill + no_skill in the same iteration
- Regression detection compares current vs previous iteration (same config type)
- No structured comparison of with_skill vs no_skill within an iteration
- No mechanism to test partial skills (individual sections or references)

## Proposed Direction

### Named skill variants

Replace the binary with_skill/no_skill with arbitrary named configs (e.g., `full`, `minimal`, `no-references`, `none`). Each variant specifies which skill file(s) to use or omit. The eval script runs all requested variants and compares them in eval-review.md.

### Open design questions

**How to define variants:**
- **Separate skill files** per variant (e.g., `variants/minimal.md`) — most explicit, manual to maintain
- **Section tags + exclusion flags** — tag sections in SKILL.md with markers, specify exclusions per variant in config. One source file but adds markup
- **Reference-level granularity** — since `references/` are already separate files, control which are included. Simpler but can't ablate core SKILL.md content

**Scope:**
- Minimum: add cross-config comparison table in eval-review.md (assertion-level diff between configs within same iteration)
- Medium: named variants + cross-config comparison
- Full: variants + cross-config comparison + model version tracking + historical trend analysis

### Workflow envisioned

1. **Normal iteration**: run `full` variant, compare to previous iteration (existing behavior)
2. **Ablation run**: run `full` + one or more stripped variants, compare across variants in one report
3. **Model upgrade**: run `full` + `none`, compare to see if the model caught up

### Cross-config comparison in eval-review.md

For each assertion, show which variants pass/fail:

```
| Assertion                    | full | no-refs | minimal | none |
|------------------------------|------|---------|---------|------|
| has-tabletest-annotation     |  ✅  |   ✅    |   ✅    |  ✅  |
| uses-value-set-syntax        |  ✅  |   ❌    |   ❌    |  ❌  |
| column-names-business-lang   |  ✅  |   ✅    |   ❌    |  ❌  |
```

This immediately shows which skill sections are load-bearing for which assertions.
