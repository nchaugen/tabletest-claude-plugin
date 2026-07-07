# Table-Driven Testing Skill — Plan

Started 2026-07-07. A new `table-driven-testing` skill in this plugin for
ecosystems where TableTest is not available. Swift (Swift Testing
`@Test(arguments:)`) must be covered; pytest `parametrize`, Jest `test.each`,
Go table tests, and xUnit `[Theory]` round out the mapping section.

This file is the cross-session tracker. At the end of each working session:
update the Status line, remove completed items (history lives in git), and
record any decisions in the Decisions section.

## Status

**Shipped as plugin v1.6.0 on 2026-07-07.** All phases complete. The suite
is five frozen evals (31–35: three pytest, one Swift Testing, one routing
guard) with per-language runner profiles, Swift/Python deterministic
checkers, and a `--no-skill` baseline mode. Results: no-skill baseline
30/44 (68.2%); skill iterations 1–2 reached 42/44 + eval-32 8/8 after
counter-text for two skill-induced over-applications (outcome-suffixed ids,
named constants for expected values); promotion iteration 3 scored **43/44
(97.7%)** — the one miss is a borderline LLM-grader flip on eval-34's
`concrete-domain-values` (varying booking values mid-table made fees less
traceable; passed in earlier runs). Official baseline on disk:
`iterations/table-driven-testing/iteration-3`; earlier iterations trimmed
(git history has them).

Notable findings for future cycles:
- eval-35 exposed a routing gap in the published tabletest skill: with
  tabletest installed but no third skill, "write table-driven tests" on a
  Java/Gradle project produced generic @ParameterizedTest (baseline 0/6).
  The new skill's deferring description closes it (6/6) — the router needs
  a skill whose description owns the phrase "table-driven".
- eval-34 (Swift) is saturated with and without the skill (vanilla Sonnet
  writes idiomatic `@Test(arguments:)`); it is a regression guard, not a
  discriminator.

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

## The Suite

The five evals in `evals/table-driven-testing/`: eval-31
`travel-insurance-py` (decomposition + thresholds-as-columns, the
eval-23-shaped pressure), eval-32 `baggage-fees-py` (full-tier enumeration +
exception-case separation, the eval-15-shaped pressure), eval-33
`library-fees-py` (ambiguity policy / deliver-don't-ask; the
cap-vs-half-rate interaction is the planted ambiguity), eval-34
`hotel-cancellation-swift` (Swift Testing `@Test(arguments:)`; the eval-32
family ported), eval-35 `cinema-tickets-routing` (routing guard: Java/Gradle
prompt where TableTest output is the passing behaviour). Eval ids continue
the global id space (tabletest and spec-by-example interleave 1–30, so this
suite starts at 31).

Evals test HEAD — commit skill edits before every run. Keep the skill a
self-contained single file (the v1.4.0 minimal-variant benchmark showed
reference-splitting hurts); add references only if a measured gap demands it.

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
- 2026-07-07: the Swift eval stub throws a package-private `NotImplemented`
  error rather than `fatalError()` — `swift test` fails gracefully instead
  of crashing the test process, and the started-stay test cannot spuriously
  pass because `NotImplemented` is not a `CancellationError`.
- 2026-07-07: the routing eval (35) is deterministic-only (no LLM
  assertions) — the jvm delivery gate zeroes everything when no `@TableTest`
  source is delivered, so mis-routing is measured for free; design depth is
  the other evals' job.
- 2026-07-07: `no-if-in-swift-test` also rejects `guard` — in a
  table-driven Swift test, `guard` on the row data is the same
  mixed-outcome branching the checker exists to catch (Swift Testing's
  `try #require(...)` is the idiomatic alternative).
