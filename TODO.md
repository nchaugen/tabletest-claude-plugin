# TODO

## Eval improvements
- Tune eval 19 (email validation) to prefer more TableTest features: collection type syntax, type converters, and value sets where semantically appropriate — multiple example emails within one failure category, not grouping across categories, which the skill warns against

### Coverage gaps — TableTest (see [goals & coverage](docs/tabletest-goals-and-coverage.md))
Failure lists are from iteration 35 (pre-1.4.0 skill); re-verify after iteration 36.
- Add output correctness/traceability assertions beyond eval 14
- Improve `concerns-decomposed` pass rates — fails in evals 15,18,23,25,26,27
- Improve `options-as-map` pass rates — fails in evals 25,26,28

### Coverage gaps — Spec-by-Example (see [goals & coverage](docs/spec-by-example-goals-and-coverage.md))
- Improve stateful features coverage — eval 17 before/after framing passes but eval 5 is the only other stateful eval
- Strengthen value-set-over-duplicate-rows — `express-uses-value-sets` fails in eval 13
- Add open-question format (marked cells) assertion beyond eval 4
- Add output traceability assertions beyond evals 10, 17
- Improve `concerns-decomposed` pass rates — fails in evals 4, 16, 24

## Skill improvements
- Add worked examples for identifying independent axes/concerns in complex domains (decomposition signs are now in the tabletest SKILL.md, but the recognition pattern for independent axes is still missing and `concerns-decomposed` remains the dominant failure theme in both suites)
- Surface tier-grouping value set pattern in spec-by-example SKILL.md (added to tabletest SKILL.md in v1.4.0)

## Research
- TableTest as agentic spec format — agent-generated example tables as executable specs. See [experiment design](docs/tabletest-as-agentic-spec-format-design.md)
