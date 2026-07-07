# Table-Driven Testing Skill — Plan

Started 2026-07-07. A new `table-driven-testing` skill in this plugin for
ecosystems where TableTest is not available. Swift (Swift Testing
`@Test(arguments:)`) must be covered; pytest `parametrize`, Jest `test.each`,
Go table tests, and xUnit `[Theory]` round out the mapping section.

This file is the cross-session tracker. At the end of each working session:
update the Status line, remove completed items (history lives in git), and
record any decisions in the Decisions section.

## Status

Phases 0 and 1 done. Runner groundwork (commit `d1d5144`): pytest 9.1.1 via
Homebrew; per-language profiles (`language` field in eval.json — `jvm`
default, `python`, `swift`) driving build checks, output collection, grading
file loading, and the delivery gate; `--no-skill` baseline mode; both new
build checks verified against scratch projects. The three pytest evals are
committed (`b99cc39`, `086fa16`) with three new deterministic Python checkers
(`uses-parametrize`, `parametrize-has-ids`, `no-if-in-python-test`) unit-
tested against good and bad samples; all scaffolds collect cleanly and the
runner loads the suite. Next up: phase 2 (Swift eval + routing eval).

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

### Phase 2 — Swift eval + routing eval

The three pytest evals now in `evals/table-driven-testing/` set the
template: eval-31 `travel-insurance-py` (decomposition +
thresholds-as-columns, the eval-23-shaped pressure), eval-32
`baggage-fees-py` (full-tier enumeration + exception-case separation, the
eval-15-shaped pressure), eval-33 `library-fees-py` (ambiguity policy /
deliver-don't-ask; the cap-vs-half-rate interaction is the planted
ambiguity). Eval ids continue the global id space (tabletest and
spec-by-example interleave 1–30, so this suite starts at 31).

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
- 2026-07-07: Language handling in the runner is a per-eval `language` field
  in eval.json (`jvm` default, `python`, `swift`) mapped to a profile (build
  check, output collection, delivery-gate pattern) — not filesystem
  autodetection at grade time, so grading is deterministic per definition.
  Existing eval.json files are untouched (fingerprints preserved).
- 2026-07-07: `--no-skill` reuses the variant plumbing under the reserved
  label `no-skill` (results in `iterations/<skill>/no-skill/`); the worktree
  deletes `skills/<skill>/` while other skills stay installed, so the
  baseline matches what current plugin users experience.
- 2026-07-07: pytest installed via `brew install pytest` (9.1.1) — Homebrew
  Python is externally managed, so no pip install; the `pytest` binary on
  PATH is what the runner and eval agents invoke.
