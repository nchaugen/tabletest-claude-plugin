# TODO

## Eval improvements
- Tune eval 19 (email validation) to prefer more TableTest features: collection type syntax, type converters, value sets (where semantically appropriate, e.g. grouping invalid emails by failure category)
- Extract inline code to project files for evals working on existing code (e.g. eval 18, 19); update run-evals.js per-eval cwd

### Coverage gaps — TableTest (see [goals & coverage](docs/tabletest-goals-and-coverage.md))
- Add "all outputs of same concern in one table" assertion — needs eval with multi-output operation (T5.5, zero assertions)
- Add traceability column assertions beyond eval 14 (e.g. to evals 22, 23, or conversion evals)
- Add set syntax `{}` and newline `\\n` assertions — extend eval 20 or create new syntax eval
- Add `single-assertion-in-method` to more evals (currently only eval 1)
- Add output correctness/traceability assertions beyond eval 14
- New tabletest eval with stateful domain (before/after pattern)
- Improve `concerns-decomposed` pass rates — present in 11 evals but fails in 5 (14,18,23,25,27,28)
- Improve `options-as-map` pass rates — fails in evals 25,26,28

### Coverage gaps — Spec-by-Example (see [goals & coverage](docs/spec-by-example-goals-and-coverage.md))
- Improve stateful features coverage — eval 17 before/after framing passes but eval 5 is the only other stateful eval
- Strengthen value-set-over-duplicate-rows — `express-uses-value-sets` fails in eval 13
- Add open-question format (marked cells) assertion beyond eval 4
- Add blank-output-for-N/A assertion (blank output cell for N/A outcomes, not filler text)
- Add output traceability assertions beyond evals 10, 17
- Improve `concerns-decomposed` pass rates — present in 9 evals but fails in 4 (4,10,13,24)
- Improve `rules-separate-from-arithmetic` — exists in eval 10 but currently fails

## Skill improvements
- Test if skill instructions can be shrunk by replacing elaborate examples with bullet-point heuristics
- Add worked examples for identifying independent axes/concerns in complex domains (partially addressed — "one table per concern" exists but recognition pattern for independent axes is missing)
- Surface tier-grouping value set pattern in main SKILL.md files (pattern exists in requirements-to-tables.md but not in primary skill text or value-sets.md)

## Future infrastructure
- Eval variant comparison system — named skill variants for ablation and model upgrade testing. See [design notes](docs/eval-variant-comparison-design.md)

## Research
- TableTest as agentic spec format — agent-generated example tables as executable specs. See [experiment design](docs/tabletest-as-agentic-spec-format-design.md)
