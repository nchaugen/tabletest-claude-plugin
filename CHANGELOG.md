# Changelog

All notable changes to this project will be documented in this file.

## [1.2.0] - 2026-02-28

### Added
- Planning skill (`/planning`) for capturing software behaviour as example tables before writing any code
  - Elicitation workflow: naming the concern, finding the first example, identifying columns, probing for edge cases and irrelevant inputs
  - Planning table design principles: one concern per table, business language throughout, concrete domain values, traceable outputs, conditions as scenario names
  - Multiple-table guidance: when to split, how to let additional tables emerge naturally
  - Bridge from planning table to `@TableTest`: direct column mapping, value set carry-over, handoff to `/tabletest` skill
  - Quality checklist for planning tables

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
