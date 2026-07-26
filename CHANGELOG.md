# Changelog

All notable changes to this project will be documented in this file.

## [Unreleased]

### Fixed
- **tabletest**: Corrected the documented behaviour of blank cells and `@TypeConverter`. A blank cell becomes `null` *before* conversion is attempted, so the converter is never called for that row — the skill previously stated the opposite, and its map-column example used a blank "all defaults" row whose null guard could not fire. The all-defaults row is now `[:]`, and dead `value == null` guards are gone from the converter examples

### Changed
- **tabletest**: Collapsing an optional-field parameter object into a map column is now decided from the method signature rather than from how blank the drafted columns look, and the converter returns the domain object — a `Map<String, String>` parameter plus a private construction helper leaves construction in the test, which the map column exists to remove
- **tabletest**: Compound expectations stay native collections — a result that is several items, or items grouped under a key, is a list, set, or map column (`[W1: [camera, lens]]`), not a quoted string assembled by a stringifying helper; use a set where order is not part of the rule

### Added
- **tabletest**: Custom converters claim expectation columns too — conversion is by parameter type, not by column role, so `true` arrives as `false` in a class registering a `Yes/No` boolean converter
- **tabletest**: A collection value cannot hold a null element — `[a, , c]` is a parse error, not a list containing null

## [1.6.0] - 2026-07-07

### Added
- **table-driven-testing**: New skill for writing table-driven tests outside the JVM — pytest `parametrize` (Python), Swift Testing `@Test(arguments:)`, Jest/Vitest `test.each`, Go table-driven subtests, and xUnit `[Theory]` (C#)
  - Framework mechanics per ecosystem: named cases everywhere (`pytest.param(id=...)`, Go subtest names, Jest interpolated titles), per-framework "regardless of" combination patterns, and the Swift Testing cartesian-product footgun (multiple collections passed to `arguments:` combine, they don't pair)
  - The same table-design principles as the tabletest skill, applied language-agnostically: decompose concerns into separate parameterised tests, make thresholds visible in the rows, cover every tier and both sides of every boundary, name scenarios by condition (never with the outcome appended), keep expected values literal (no named constants), and give expected-error cases their own test
  - Deliver-don't-ask ambiguity policy: choose the most reasonable interpretation, state it, and deliver complete tests
- **Plugin**: description and keywords updated to cover the third skill

### Changed
- **Routing**: requests for "table-driven tests" on Java/Kotlin projects now route reliably to the tabletest skill — the new skill's description explicitly defers to it

## [1.5.0] - 2026-07-07

### Added
- **tabletest**: Ambiguity policy for writing tests from a feature description — proceed with the most reasonable interpretation and record assumptions and open questions in `@Description`, rather than stopping to ask clarifying questions
- **tabletest**: Worked example for collapsing an optional-field parameter object (constructor, setters, or builder) into a single map column with a `@TypeConverter` supplying defaults
- **tabletest**: "Let tables drive the API decomposition" — if a row needs a helper fabricating raw data to reach a derived input value, target a narrower function; cheap rows signal a well-placed table
- **tabletest**: Conversion workflow now finishes the migration — replace the old framework's matchers, remove its imports and build-file dependencies (with matching quality check)
- **tabletest**: Value-set guidance covers both axes — grouping same-outcome values within a row and collapsing duplicate rows for interchangeable inputs; every tier expressed as one row including both boundary values

### Changed
- **tabletest**: Quality checks tightened — no `if`/`switch`/ternary in test methods including null-guards (defaulting belongs in a `@TypeConverter` or helper); a lone error/null/empty case belongs as a table row with a `Throws?` column, not a separate `@Test`; date cutoffs prefer descriptive relative values or a cutoff column

## [1.4.0] - 2026-07-06

### Changed
- **tabletest**: SKILL.md rewritten as a self-contained core file using ~20% fewer tokens
  - Dependency coordinates, imports, custom type converters, non-obvious built-in conversions, and value-set patterns (cartesian product, "doesn't matter", tier grouping) now inlined — no reference reads needed on the standard path
  - Table design guidance moved into the core file: separate rules from arithmetic, frame stateful features as rules, decomposition signs, colon quoting to avoid map syntax
  - Workflow guidance for greenfield features: write tests first with stub implementations, write one `@TableTest` at a time
  - Examples mirroring eval scenarios replaced (weekly pay → parking fee, loyalty discount → insurance premium)
- **tabletest**: "Make Thresholds Visible" added to Table Design with matching quality check (previously only in `references/requirements-to-tables.md`)
- **tabletest**: References pruned to niche topics with a stricter read-on-condition table; removed `dependency-setup`, `value-sets`, `requirements-to-tables`, `example-patterns`, `incremental-development`, `consolidating-tests` (content inlined or subsumed by SKILL.md); `type-converters` trimmed to search strategy, defaults, and wrapper types

## [1.3.0] - 2026-03-06

### Changed
- **tabletest**: Simplified pre-check — dependency and shape checks rewritten as readable prose rather than a prescriptive checklist
- **tabletest**: Improved skill trigger description so the skill activates on value-set, type-converter, and column-design questions even when the user doesn't say "TableTest" explicitly
- **tabletest**: Pair programming guidance extracted to `references/pair-programming.md`; SKILL.md retains the key habit (show a mockup first) with a pointer to the full cadence
- **spec-by-example**: Improved skill trigger description — now activates on vague requirements and mid-implementation edge cases, not just upfront spec work
- **spec-by-example**: Expanded value-set guidance with a dedicated state/status example (`{PENDING, CONFIRMED}`) and an explicit callout that blank and value-set mean different things and must not be conflated
- **spec-by-example**: Clearer handoff section linking to `/tabletest` with column-translation notes

### Added
- **tabletest**: Date format limitation warning — built-in `LocalDate`/`LocalDateTime` conversion handles ISO 8601 only; non-standard formats require a `@TypeConverter`
- **Plugin**: Updated description to cover both skills; keywords updated (`spec-by-example`, `example mapping` added; `fit`, `acceptance testing` removed)

## [1.2.0] - 2026-02-28

### Added
- Spec-by-example skill (`/spec-by-example`) for clarifying behaviour with multiple cases or rules through concrete example tables
  - Elicitation workflow: naming the concern, finding the first example, identifying columns, probing for edge cases and irrelevant inputs
  - Example table design principles: one concern per table, business language throughout, concrete domain values, traceable outputs, thresholds visible as columns, conditions as scenario names
  - Multiple-table guidance: when to split, how to let additional tables emerge naturally
  - Bridge from example table to `@TableTest`: direct column mapping, value set carry-over, handoff to `/tabletest` skill
  - Quality checklist for example tables

## [1.1.0] - 2026-02-25

### Added
- Non-obvious built-in type conversions reference table (enums, hex/octal integers, `Class<?>` variants, `Duration`, `Period`, `Currency`, `Locale`)
- Minimal quoting strategy: start without quotes, add only where needed
- Guidance on quoting inside collection elements rather than wrapping the whole collection
- Newline handling in table values (`\\n` + manual replace in test method)
- Set `{}` vs List `[]` common mistake callout
- Single-scenario `@TableTest` exception in pre-check
- Guidance on when NOT to use TableTest (trivial implementations, complex setup, already covered by integration tests)
- New advanced design pattern: separate tables when column sets diverge
- New common pattern: static constants for readable expected values (e.g. ANSI codes)

## [1.0.0] - 2026-02-24

### Added
- TableTest skill for writing and converting JUnit tests to TableTest format
- Reference guides: dependency setup, value sets, type converters, column design, common patterns, large tables, example patterns, async and performance, provided parameters, advanced table design, incremental development, consolidating tests, testing reveals bugs
