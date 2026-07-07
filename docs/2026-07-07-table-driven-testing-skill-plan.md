# Table-Driven Testing Skill — Plan

Started 2026-07-07. A new `table-driven-testing` skill in this plugin for
ecosystems where TableTest is not available. Swift (Swift Testing
`@Test(arguments:)`) must be covered; pytest `parametrize`, Jest `test.each`,
Go table tests, and xUnit `[Theory]` round out the mapping section.

This file is the cross-session tracker. At the end of each working session:
update the Status line, remove completed items (history lives in git), and
record any decisions in the Decisions section.

## Status

Phases 0–2 done. Runner groundwork (commit `d1d5144`): pytest 9.1.1 via
Homebrew; per-language profiles (`language` field in eval.json — `jvm`
default, `python`, `swift`) driving build checks, output collection, grading
file loading, and the delivery gate; `--no-skill` baseline mode. The suite is
now five evals: three pytest (`b99cc39`, `086fa16`), eval-34
`hotel-cancellation-swift` (Swift Testing, ports the full-tier-enumeration +
exception-separation family; three new Swift checkers `uses-test-arguments`,
`no-loop-in-swift-test`, `no-if-in-swift-test`), and eval-35
`cinema-tickets-routing` (Java/Gradle prompt saying "table-driven tests";
deterministic-only — delivery gate plus `has-tabletest-annotation` and a new
`no-parameterized-test` checker make mis-routing score zero). All checkers
verified against good/bad samples; both new scaffolds build cleanly; the
runner loads all five evals and every deterministic assertion resolves to a
checker. Phase 3 done: suite frozen and the `--no-skill` baseline recorded
(`iterations/table-driven-testing/no-skill/iteration-1`, commit `e57b586`) —
**30/44 (68.2%)**, Sonnet 5, graded by Haiku 4.5. What the skill draft must
win: eval-31's decomposition family (0/3: monolithic 16-case table),
thresholds-as-columns, parametrize ids (missing in 32 and 33),
condition-describing scenario names (0/2), and eval-35 routing (0/6 — see
below). eval-34 (Swift) is saturated at baseline (8/8); it guards against
the skill hurting Swift output rather than showing a positive delta.
Baseline surprise: eval-35 exposed a routing gap in the *published* tabletest
skill — with tabletest installed, "write table-driven tests" on a Java/Gradle
project produced generic @ParameterizedTest/@CsvSource; the skill never
triggered (tabletest's own eval prompts all say "TableTest" explicitly). The
new skill's description mentioning "table-driven" and deferring to tabletest
for Java/Kotlin may itself close this gap — eval-35 measures exactly that.
Phase 4 done: skill v0 committed (`d12b170`) as a single self-contained
`skills/table-driven-testing/SKILL.md` (~220 lines) — table model, framework
mechanics for pytest/Swift Testing/Jest/Go/xUnit (with per-framework
"regardless of" emulation and the Swift cartesian footgun), de-JVM-ified
table-design principles targeting the baseline gaps (decomposition,
thresholds visible, tier/boundary enumeration, condition-named rows/ids,
error-case separation, ambiguity policy), and the description defers to
tabletest for Java/Kotlin. Next up: phase 5 — run the suite with the skill
(`node scripts/run-evals.js --skill table-driven-testing --iteration 1`,
user's shell) and compare against the 30/44 no-skill baseline.

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

### Phase 5 — Iterate

Evals test HEAD — commit skill edits before every run. Keep the skill a
self-contained single file (the v1.4.0 minimal-variant benchmark showed
reference-splitting hurts); add references only if a measured gap demands it.

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
