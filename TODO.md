# TODO

## Eval improvements
- Tune eval 19 (email validation) to prefer more TableTest features: collection type syntax, type converters, value sets (where semantically appropriate, e.g. grouping invalid emails by failure category)
- Extract inline code to project files for evals working on existing code (e.g. eval 18, 19); update run-evals.js per-eval cwd

## Skill improvements
- Test if skill instructions can be shrunk by replacing elaborate examples with bullet-point heuristics
- Add worked examples for identifying independent axes/concerns in complex domains (partially addressed — "one table per concern" exists but recognition pattern for independent axes is missing)
- Surface tier-grouping value set pattern in main SKILL.md files (pattern exists in requirements-to-tables.md but not in primary skill text or value-sets.md)

## Future infrastructure
- Eval variant comparison system — named skill variants for ablation and model upgrade testing. See [design notes](docs/eval-variant-comparison-design.md)

## Research
- TableTest as agentic spec format — agent-generated example tables as executable specs. See [experiment design](docs/tabletest-as-agentic-spec-format-design.md)
