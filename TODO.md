# TODO

## Eval improvements
- Tune eval 19 (email validation) to prefer more TableTest features: collection type syntax, type converters, value sets (where semantically appropriate, e.g. grouping invalid emails by failure category)
- Extract inline code to project files for evals working on existing code (e.g. eval 18, 19); update run-evals.js per-eval cwd

### Coverage gaps — TableTest (see [goals & coverage](docs/tabletest-goals-and-coverage.md))
- Add output correctness/traceability assertions beyond eval 14
- Improve `concerns-decomposed` pass rates — fails in evals 14,18,23,25,27,28,30
- Improve `options-as-map` pass rates — fails in evals 25,26,28
- Improve `special-chars-quoted` — eval 20 still fails pipe/bracket quoting

### Coverage gaps — Spec-by-Example (see [goals & coverage](docs/spec-by-example-goals-and-coverage.md))
- Improve stateful features coverage — eval 17 before/after framing passes but eval 5 is the only other stateful eval
- Strengthen value-set-over-duplicate-rows — `express-uses-value-sets` fails in eval 13
- Add open-question format (marked cells) assertion beyond eval 4
- Improve `blank-output-for-na` pass rate — assertion exists in eval 13 but fails
- Add output traceability assertions beyond evals 10, 17
- Improve `concerns-decomposed` pass rates — present in 9 evals but fails in 4 (4,10,13,24)
- Improve `rules-separate-from-arithmetic` — exists in eval 10 but currently fails

## Skill improvements
- Add worked examples for identifying independent axes/concerns in complex domains (partially addressed — "one table per concern" exists but recognition pattern for independent axes is missing)
- Surface tier-grouping value set pattern in main SKILL.md files (pattern exists in requirements-to-tables.md but not in primary skill text or value-sets.md)

## Research
- TableTest as agentic spec format — agent-generated example tables as executable specs. See [experiment design](docs/tabletest-as-agentic-spec-format-design.md)
