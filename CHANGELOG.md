# Changelog

All notable changes to this project will be documented in this file.

## [Unreleased]

### Fixed
- **tabletest**: Removed a second, contradictory statement about blank cells and converters. "Blank cells for irrelevant inputs" still told readers to handle null-to-default conversion *in a `@TypeConverter`* — the very thing a blank cell makes impossible, and the claim corrected elsewhere in the same file. Blank now explicitly means *absent*, with the empty value (`[:]`, `[]`, `{}`, `''`) as the way to reach a converter for defaults
- **tabletest**: An input that exists but does not affect the outcome is a value set, not a blank cell. The guidance said both, in adjacent paragraphs
- **tabletest**: Worked examples no longer name a row after its own answer. The tier-ladder table named each row for the standing it expects (`Freshman` beside `Standing?` `Freshman`), and the priority-resolution tables named theirs for the source they resolve to (`Configured wins` beside `Source?` `CONFIGURED`) — both contradicting the rule that scenario names describe the condition, not the outcome. The advice to name decision rows "X wins over Y" went with them: a priority table almost always publishes the winner as an expectation column
- **tabletest**: Corrected the documented behaviour of blank cells and `@TypeConverter`. A blank cell becomes `null` *before* conversion is attempted, so the converter is never called for that row — the skill previously stated the opposite, and its map-column example used a blank "all defaults" row whose null guard could not fire. The all-defaults row is now `[:]`, and dead `value == null` guards are gone from the converter examples

### Changed
- **tabletest**: Scenario-name guidance now opens with the check instead of ending with it. The decidable test — read each name beside its own expectation cells, and cut whatever repeats one — is the first line of the section, followed by a single transform table covering every shape: the compound name, the bare outcome, the verdict-led prefix, and the name that says nothing changed. Two explanatory paragraphs and a second table became rows in it
- **tabletest**: The rule about inputs a rule ignores is now written as an instruction with the trigger first — *if a title or description says an input doesn't affect the result, vary that input in the rows* — with the value-set syntax to copy and the prohibition (not in a converter, a field, or the test body) stated plainly rather than argued for
- **tabletest**: Table Design guidance is now grouped under the three questions that decide almost every table — what is this table's axis, what does a reader see, and what is left in the method body. The sections themselves are unchanged; they were previously in an order that interleaved all three, so advice that answers one question arrived in three separate places
- **tabletest**: Collapsing an optional-field parameter object into a map column is now decided from the method signature rather than from how blank the drafted columns look, and the converter returns the domain object — a `Map<String, String>` parameter plus a private construction helper leaves construction in the test, which the map column exists to remove
- **tabletest**: Compound expectations stay native collections — a result that is several items, or items grouped under a key, is a list, set, or map column (`[W1: [camera, lens]]`), not a quoted string assembled by a stringifying helper; use a set where order is not part of the rule
- **tabletest**: A map column is a column decision, not a table decision. Choosing a map for one parameter does not make that parameter's fields the concern boundary — another input that drives the same rule to the same output column is another column in the same table, not a table of its own
- **tabletest**: Separating rules from arithmetic now names the symptom: if reading a row means classifying first and then computing, the table has fused two rules and states neither. The classification gets its own table whose expectation columns *are* the classification, and the calculation table takes those as input columns
- **tabletest**: The rule for when a second expectation column belongs elsewhere is now about axes, not response shape. A response carrying two fields usually keeps both columns; what matters is whether each column moves for its own reason along the axis the table varies. A column constant down every row, or changing only as a side effect of another, is not being tested — give it rows that vary it, or move it to the table whose axis does. The previous wording ("a second rule's output does not belong") would have split any two-field response, causing the over-decomposition the skill warns against two sections earlier
- **tabletest**: Decomposition guidance now states the opposite failure as well — several tables that fix the same setup, each varying one sub-rule and reporting the same output column, are one concern scattered across methods, and belong in one table with a column for the varying input. That shape is now given as the *symptom*: the cause is a family of rules you did not name. If you can name what several tables have in common in one term, that term is the table and its members are a column — and members of a family computing differently (a flat reduction, a percentage, a weight-based recalculation) is not a reason to split them. Guarded against collapsing on a bag rather than a family: the family name must work as a column header with the members as its values
- **tabletest**: Tier ladders get one row per tier — all of them, none twice. Do not sample the ladder and trust the reader to interpolate, and do not split a tier into a "tier begins" row beside a "tier holds" row: a value set spanning the tier already carries its boundaries
- **tabletest**: Null, empty and blank variants of an input are one row per distinct *outcome*, not one per representation. Where all three produce the same rejection that is a single row, or `{'', '   '}` as a value set with a blank-cell row only where the null case must be visible on its own
- **tabletest**: Whether a value set is right is decided by the rule's own granularity, not by how different the inputs look. Where the rule answers differently for each — distinct reason codes — those are separate outcomes and separate rows. Where it returns one undifferentiated rejection however the input fails, that is one obligation: a representative case or two, and a value set for the rest. Enumerating every way an input can be malformed is coverage of the *format*, not of the rule. This replaces guidance that said the opposite by example, asserting that malformed-input variants "each test a different structural rule" — which licensed the enumeration the rest of the section forbids

### Added
- **tabletest**: What the assertion tolerates is part of the rule. A comparison that sorts either side before comparing, accepts a subset, matches "contains" rather than equals, or normalises case or whitespace changes which behaviours the test would accept — and none of it reaches a reader of the table. Ordering is the usual case and a helper is where it hides, written once and invisible at every call site. Either name the criterion in the `@Description`, or remove the need for it: a `Set` expectation column says order does not matter in the table itself. A conventional numeric epsilon, and constructing the objects the columns name, are exempt
- **tabletest**: Any domain object built from a table value belongs in a `@TypeConverter`, whatever the construction idiom — constructor, builder, static factory, or a chain of `with…` calls are all construction, and construction in the test body puts the arrangement between the reader and the rule. Previously this was stated only for objects with several optional fields, as part of the map-column guidance, so a composed object assembled by a factory or wither chain fell outside it
- **tabletest**: How to write a table where some rows throw and some do not — the shape any boundary straddling a validation limit produces. Leave `Throws?` blank where nothing is thrown and compare the thrown type against it, so the method keeps one assertion; branching between `assertThrows` and `assertDoesNotThrow` puts the rule back in the body where the table cannot show it
- **tabletest**: One value can carry two obligations in two different tables. A value that is a boundary for one rule is often the subject of another — showing it once, in whichever table you reached first, feels like coverage and is not. Count obligations per rule, never per value
- **tabletest**: A new frame — assume the table is published. Only three surfaces reach a reader who never sees the test body, and they divide the work: the title carries the rule, the description carries the apparatus that cannot be a column, the table carries the variations. Whatever the table holds constant is silently promoted into the rule, so a constant the outcome depends on is either a column or declared in the title or description — never left in the method body, a field, a `@TypeConverter`, or a `//` comment, which reaches no published surface at all. An input the rule is claimed to ignore must still be shown varying, as a value set; pinning it makes the independence unfalsifiable
- **tabletest**: Guidance on titles. `@DisplayName` — or the method name when there is none — is the line a reader scans in a report index, so titles are judged as a set. Each states an action the code performs rather than labelling a topic (`Sets the deferral interval from donation type`, not `Deferral interval by donation type`), no three share an uninformative opener, and one grammatical shape runs across the family. That action voice belongs to the title alone: scenario names stay condition phrases
- **tabletest**: `@Description` must not publish the algorithm. Restating an internal formula turns a black-box table into a white-box one; the test is whether a reader could recompute the expectation cells from the description alone. A threshold the rule *is about* stays welcome — better still as a column
- **tabletest**: Before deleting a combining table, salvage the obligations only its rows discharge into the table that owns their rule — then delete. It is a salvage step, not a reprieve: a single `@Test` re-proving one already-proven total is the same combining table with fewer rows
- **tabletest**: Scenario-name guidance now covers the mistake people actually make. The published good/bad pairs only showed bare outcomes (`Returns error`, `Cannot rent`); the common failure is a name that states the condition *and then adds the outcome* — `Contractor gets no bonus` beside a bonus column of `0.0`. Such a name looks right, because a condition really is in it. Added the repair as a transform (point at the expectation cell the name restates, cut that clause, keep the rest), the verdict-led prefix (`Deferred: …`) as the same mistake, and the case where a name says nothing changed, which publishes the answer just as surely
- **tabletest**: Custom converters claim expectation columns too — conversion is by parameter type, not by column role, so `true` arrives as `false` in a class registering a `Yes/No` boolean converter
- **tabletest**: A collection value cannot hold a null element — `[a, , c]` is a parse error, not a list containing null
- **tabletest**: Guidance on how many rows a table needs. List the concern's obligations — the distinct behaviours the rule must demonstrate — then write the smallest set of rows covering all of them. Where two rows share an expectation, the difference between them must be the thing the rule is about; three shapes of redundant row are named: a value further past a boundary an earlier row already crossed, a larger n in the same direction, and an input the rule is indifferent to (one row with a value set)
- **tabletest**: Guidance on combining tables. A table exercising several rules together earns its place only where the combination behaves in a way neither rule shows alone — a precedence, an ordering, an interaction whose result neither single-rule table produces. A final table that runs the whole feature end to end re-proves what those tables established; if the description you would write for it is "end-to-end scenarios combining the rules above", it has no rule of its own
- **tabletest**: Four Quality Checks — one row per obligation, every tier exactly once, combining tables prove an interaction, and expectation columns are all outputs of the same rule

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
