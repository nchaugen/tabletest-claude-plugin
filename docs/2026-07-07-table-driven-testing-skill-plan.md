# Table-Driven Testing Skill — Plan

Started 2026-07-07. A new `table-driven-testing` skill in this plugin for
ecosystems where TableTest is not available. Swift (Swift Testing
`@Test(arguments:)`) must be covered; pytest `parametrize`, Jest `test.each`,
Go table tests, and xUnit `[Theory]` round out the mapping section.

This file is the cross-session tracker. At the end of each working session:
update the Status line, remove completed items (history lives in git), and
record any decisions in the Decisions section.

## Status

Plan written; no implementation started. Next up: phase 0.

## Approach (agreed before parking, 2026-07-07)

- **Derive, don't extract.** `skills/tabletest/SKILL.md` stays untouched and
  self-contained. The new skill starts as a copy of tabletest's Table Design
  and Workflow sections (SKILL.md lines ~362–838, already mostly
  language-agnostic), de-JVM-ified, and diverges freely from there.
- **Per-ecosystem mechanics.** TableTest mechanics (table syntax, quoting,
  `@TypeConverter`, value sets) are replaced by a mapping section per
  ecosystem. Value sets need per-language emulation notes (stacked
  `parametrize` in pytest; loops/helpers in Swift, Go, Jest).
- **Routing.** The description must defer to `tabletest` on Java/Kotlin
  projects — routing between the two existing skills was a hard-won fix, and
  a third skill re-opens the risk. A routing eval guards it.
- **Suite before skill.** The eval suite is the measuring instrument; it
  lands and is frozen before any skill text is written. Fresh scenario
  domains only — do not reuse tabletest eval scenarios (cross-suite leakage,
  and skill text may never quote eval scenarios).
- **No-skill baseline.** Unlike tabletest, the first question is whether the
  skill beats vanilla Claude at all, so the baseline run is prompt-only.

## Phases

### Phase 0 — Runner groundwork (`feat(evals):` commits, frozen before suite work)

- [ ] Install pytest for the eval environment (machine has Python 3.14 but no
      pytest, no uv; Swift 6.3.3 is present and includes Swift Testing).
- [ ] Extend `runBuildCheck` in `scripts/run-evals.js` beyond Maven/Gradle
      (it currently returns null for anything else, so `compiles` /
      `tests_pass` assertions would silently skip):
      - pytest project (`pyproject.toml` or `test_*.py` present):
        `compiles` → `python3 -m pytest --collect-only -q`,
        `tests_pass` → `python3 -m pytest -q`.
      - Swift package (`Package.swift` present):
        `compiles` → `swift build --build-tests`,
        `tests_pass` → `swift test` (allow a generous first-build timeout).
- [ ] Add a `--no-skill` run mode that deletes `skills/<skill>/` from the
      worktree, for the phase 3 baseline.
- [ ] Update the runner usage text (`--skill` currently enumerates
      `tabletest, spec-by-example`).

Everything else in the runner is already skill-agnostic: `evals/<skill>/` is
resolved generically, plugin skills are auto-discovered from `skills/`, and
per-eval `project/` scaffolding is copied regardless of language.

### Phase 1 — pytest eval suite (3 evals)

Create `evals/table-driven-testing/` following the existing eval layout
(`prompt.md`, `expected_output.md`, `eval.json`, `project/`). Project
scaffolding: minimal `pyproject.toml` plus a small `src` module, mirroring
how tabletest evals ship a Gradle project with main sources and empty test
dirs. Port the assertion families that discriminate hardest in the tabletest
suite, on fresh domains:

- [ ] Eval A — decomposition + thresholds-as-columns (the eval-23-shaped
      pressure: one fat table that should split, with visible thresholds).
- [ ] Eval B — one-row-per-tier enumeration + descriptive scenario naming
      (the eval-15-shaped pressure: a tier ladder models boundary-sample).
- [ ] Eval C — ambiguity policy / deliver-don't-ask + black-box outputs
      (underspecified prompt; the skill should make the agent state
      assumptions and deliver).
- [ ] Deterministic checkers where possible (delivery gate, table-shape
      checks), matching the fixed-suite conventions from the v1.5.0 cycle.

### Phase 2 — Swift eval + routing eval

- [ ] Eval D — Swift Testing `@Test(arguments:)` eval with a `Package.swift`
      scaffold; asserts parameterised structure (zipped argument tuples or
      struct rows, not a for-loop inside one test) plus one ported design
      family.
- [ ] Eval E — routing guard: a Java/Gradle project prompt in this suite
      where the passing behaviour is TableTest output (i.e. the `tabletest`
      skill wins the routing, not this one).

### Phase 3 — Baseline

- [ ] Freeze the suite (evals committed, no further assertion edits during
      skill iteration).
- [ ] Run the suite with `--no-skill` → `iterations/table-driven-testing/`
      baseline. This benchmark is what the first skill draft must beat.

### Phase 4 — Skill v0

- [ ] Draft `skills/table-driven-testing/SKILL.md`. Pre-release, direct
      iteration in `skills/` is fine (nothing published to protect), but
      evals test HEAD — commit before every run.
- [ ] Description scoped to non-JVM parameterised/table/data-driven testing;
      explicitly defers to `tabletest` for Java/Kotlin.
- [ ] Self-contained single file first (the v1.4.0 minimal-variant benchmark
      showed reference-splitting hurts); add references only if a measured
      gap demands it.

### Phase 5 — Iterate

- [ ] Iterate skill text against the no-skill baseline; identify the
      discriminating core once enough runs exist to see which assertions
      move.

### Phase 6 — Ship (plugin v1.6.0)

- [ ] Update plugin.json (version, description now covers three skills,
      keywords already mention swift/pytest? — check), CHANGELOG entry.
- [ ] Full-suite promotion run as regression evidence; trim iteration dirs
      per the standing policy.
- [ ] Tag `v1.6.0`; release workflow handles the rest.
- [ ] Also update README to document the third skill.

## Decisions

- 2026-07-07: Develop skill v0 directly in `skills/table-driven-testing/`
  rather than `skill-variants/` — the variant mechanism swaps an existing
  published skill, which doesn't exist here; "never iterate on `skills/`"
  protects published versions only.
- 2026-07-07: Baseline is a `--no-skill` runner mode (new), not an empty
  variant — an empty SKILL.md would still trigger and distort routing.
- 2026-07-07: pytest-first (3 evals), then Swift, all before any skill text —
  so Swift requirements shape the first draft, but suite mechanics are
  debugged on the cheapest ecosystem.
