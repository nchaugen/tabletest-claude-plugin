# Changelog

All notable changes to this project will be documented in this file.

## [1.9.0] - 2026-08-18

The three skills now share one table-design core — the same rules, in the same words, illustrated in each skill's own notation. **Every entry below applies to all three skills unless it names one.**

### Added

- Tables that sit together are read together. Across one set, a concept takes one column name, a kind of value one notation, a failure one spelling, and the tables share their helpers instead of each carrying a copy
- A compound cell value is judged by whether a reader can name each part, not by which punctuation it uses. `2 x 5 mg tablet` explains itself; `G3/HEAT/zone-2` needs a key that lives outside the table
- A field no surface makes a claim about can be left out of a cell and supplied by a fixture — what keeps a cell readable when the object has twelve properties and the rule reads two
- A seam you cannot add is named rather than passed over. Where a table fuses two rules because the intermediate value is not observable, one sentence says so, and it says whether the gap belongs in the code or in the test
- **tabletest**: A shape for composite cell values, chosen by what a reader can name — a map where the parts need their keys, a domain notation where the values name themselves, the value alone for a single part, and a list of any of those for several objects
- **tabletest**: The bundled `scripts/format-table.sh` is part of the workflow — run it to align columns before finishing. It doubles as a parse check: `--check` exits 1 when the table parses and 0 when it does not
- **tabletest**: A list of what the notation cannot express, so the limits are read rather than met as a failing build — a value set cannot vary an expectation, its members split on commas, one converter serves a target type per class (matched on the erased type), and a collection cell cannot hold a null element
- **tabletest**: A reference for two shapes a table cannot express directly — work handed to another thread that the assertions depend on, and positional output fields whose occupant depends on configuration
- **tabletest**: Where the code under test has no type to pin a converter to, an object's parts may go in separate columns and be assembled in the method — named as the last resort it is, and declared in `@Description`

### Changed

- Table design is stated once and shared — one rule per table, the signs a table should be decomposed, value sets for inputs a rule ignores, covering every tier and both sides of every boundary, what the published surfaces carry, scenario and column naming, rejection as an expected column, blank meaning absent, black-box columns. Each skill previously stated an overlapping subset in its own wording, and the wordings had drifted, so the same table could be judged well designed by one skill and badly designed by another. `spec-by-example` and `table-driven-testing` gain the rules they never carried
- **tabletest**: *Collapse Sparse Columns into a Map* is folded into *Putting a Composite Value in a Cell*, which stated the same rule; two checklist lines the shared rules had already replaced are gone
- **spec-by-example**: Worked examples no longer illustrate with loan approval, discounts, order-status transitions, shipping zones, or subscription trial and loyalty columns

### Fixed

**Which rows a table owes**

- The obligation list is derived from the inputs before any rows are counted. Take each input the rule reads and ask whether it is counted or read in bands, whether it charges per unit or once for having any, and whether its effect depends on another input. Each question names rows nothing else will
- *Give each obligation exactly one row* reads as a floor as well as a ceiling. It listed only ways to spot a redundant row, so used as a checklist it could only ever remove
- A rule that reads as one behaviour can still be several. "X holds only if C1 and C2 and C3" names cleanly in a single breath and is three independent claims, each owing its own table
- A tier ladder shows every tier, and a formula behind it reduces neither the tiers nor the boundaries — nine tiers stay nine rows. A value set spanning a tier carries that tier's boundaries only when its first and last members are the tier's own edges
- A boundary pair is written in the finest unit the rule distinguishes. Rows of 30 and 31 days straddle nothing where the rule turns on hours, so the column is `Hours Ago`
- Where one ladder repeats across classes that share its boundary positions, the straddling pairs are written in one class and every other class carries one row per tier. The obligation previously read as boundaries × classes, and a four-class ladder had no stated way to be discharged
- Two rows sharing an answer are one row when swapping one's differing input for the other's would not change an expectation cell *in this table*. The old test asked whether the values behaved alike everywhere, which blocked the collapse whenever any neighbouring rule told them apart
- Collapsing means putting every collapsed value in the surviving cell, never deleting rows — a value set discharges every obligation its members carried, because it expands into one case per value
- A row kept because it "reaches the answer by a different route" has to name a route this table's rule cares about. Three rows for three kinds of coupon, in a table whose rule reads only whether the code was valid, was the common false positive

**Which values become columns**

- A constant the outcome depends on is a column wherever it can be one, and a threshold or limit the rule turns on always can be. The title and description carry only what a column cannot — where the data came from, what the fixture fixes
- A column blank for most of its rows is a column decision before it is a table decision. Sparse columns feeding one expectation column are a family and collapse into one column; columns feeding different expectation columns are different concerns and split the table
- Where another rule derives an input, that rule keeps its own table and this one takes the derived value as an input column. Making a value visible is not a reason to absorb the rule that produces it
- Inputs that are contributions to one combined answer stay in one table with a column each. Splitting them gives one table per contribution, each holding the others at nothing
- The `?` suffix marks outputs only. An input column never takes it, however yes/no it looks — `Repeat Donor`, not `Repeat Donor?`
- A reference point in its own column leaves the values measured from it readable as offsets, instead of absolutes restated in every row
- The rule for an input a table's rule ignores is named for the property rather than the notation — *Show That an Input Does Not Change the Outcome* — and it names the two ways of losing that conclusion. Leaving the column out states nothing: it reads exactly like having forgotten the input. And where the operation does not take the input at all, that is the thing to fix before the table — a value a caller hands over with the request is an input, so it stays in the signature and the table varies it, even where the code will not read it
- Value sets gained the sign that you want one — a column that could carry every one of its values on every row without changing anything is the rule saying, in data, that it does not read that column — and the limit that goes with it: a value set cannot vary an expectation
- Collapsing a family of rules means one table, not necessarily one column. Where the family's members are separate inputs the system reads independently, each takes its own column and stays blank on the rows where it plays no part
- A held constant declared as "and it makes no difference" is a claim, not apparatus. Vary the value across what it ignores rather than writing the sentence, because no row can contradict the sentence
- **tabletest**: A policy constant the API does not expose still gets a column. The column binds to the *test method's* parameter list, not to the arguments of the code under test, so a hardcoded cutoff date or tier threshold takes a parameter like any other column and the body simply does not pass it on
- **tabletest**: A parameter that is one object or one collection keeps one column even where the table moves only part of it — one map column in every table of the class, rather than a field-per-column spread blank in most cells

**What a cell holds**

- A compound expectation stays a native collection down to its keys. Nest one level per part, or give the key a type with a name; a key pasted together from several fields leaves the row one token whose parts the reader separates by eye
- A shorthand cell keeps a slot for every field the description makes a claim about. Packing only the fields the rule reads pins the rest for every row with no column saying so
- Cell values prefer what the system really produces — a sentinel, enum constant or error string that is part of the observable contract, rather than a tidier test-only label. Where a value is too long to scan, shorten the value and never the vocabulary
- Identity and status varying together in one output position is named as the exception it is. `Primary OK` against `Secondary ERROR` is one domain value where both parts vary, and splitting it into two columns makes the reader join the halves back up
- A value the rule ignores that sits inside a composite cell has two routes out, and the cheaper one is stated first: where the cell holds a list, vary the ignored value across its elements — one row, nothing reshaped
- **tabletest**: An object with two collection-shaped parts is not the last-resort case for spreading a value across columns. A converter takes one cell, so an object holding a map and a set cannot be built from two columns — the table varies the one part the converter builds from, and the parts no row varies are fixed in the method and declared in `@Description`
- **tabletest**: A custom `@TypeConverter` is reached for collection elements, at any depth, exactly as a built-in converter is. The skill said this of built-in conversion only, which left a list of domain objects looking unsupported

**Rejected cases**

- Whether accepted and rejected rows belong in one table is decidable rather than a matter of taste. Strike the rejected rows: if what remains still states a rule, the rejection was a separate concern and takes its own table
- **table-driven-testing**: An accept/reject boundary is one rule and stays in one test, so the last accepted value sits beside the first rejected one. Five references to a section that had stopped existing now resolve
- **tabletest**: The exception column in the rejection example holds `java.lang.IllegalArgumentException`. The bare name it carried cannot be converted to a `Class<?>` and fails every row with `ClassNotFoundException`

**Titles and descriptions**

- A value already shown as a column is fully declared and no other surface owes it anything. A column that never varies is declared as well as one that does, so being constant is not on its own a reason to add a row varying it
- **tabletest**: `@Description` no longer offers "fixed values shared by all rows" as a reason to write one. A value that can be a column belongs in a column; the description carries what cannot
- **tabletest** and **spec-by-example**: Column and scenario names are written in domain terms in the first draft, not deferred to a refinement pass after the tests go green. The table someone reads is the one you hand over

**tabletest notation and tooling**

- The table formatter installs and runs. It asked Maven Central for a `shaded` classifier that has never been published, so the download always failed and the script exited without formatting anything — including the bundled auto-formatting hook
- The formatter is referenced by a path that resolves: `${CLAUDE_PLUGIN_ROOT}/skills/tabletest/scripts/format-table.sh`. It was written as if relative to your working directory, where it has never existed
- The quoting rules were wrong about colons and are now decidable without running anything. A colon in a whole cell needs no quotes — `Alert: condensation risk` is a plain value — while a colon inside `[…]` or `{…}` does, because the parser reads brackets before it knows the parameter type
- The one-converter-per-target-type rule says what it actually is: per *class*, matched on the *erased* type, so `Optional<String>` and `Optional<Boolean>` collide. That is also what forces several tables in one class onto one cell format
- The from-existing-code workflow no longer asks for a mockup to be confirmed before implementing, which contradicted the ambiguity policy two branches above — choose the reasonable reading, record it, deliver
- "A lone error case belongs in the table rather than a separate `@Test`" no longer reads as licence to merge two tables. It is about `@Test` methods, and never overrides the test that decides whether accepted and rejected rows share a table

**spec-by-example**

- A blank cell means one thing: the value is genuinely absent and the system under test decides what missing means. `0` says the value is present and is zero, and a test method must never convert a blank to a default on the way in
- A `@Test` method is for a sequential path, not for re-running the rules end to end. A combination that behaves in a way neither rule shows alone earns a table of its own

**table-driven-testing**

- Inputs a rule ignores vary together in one case list instead of being stacked into a cartesian product. Two stacked `parametrize` decorators turn one claim into four visible cases, and a third input turns it into eight

**Worked examples and wording**

- Eighteen defects in the illustrations themselves, found by reading every example against the rules beside it — `[EMPTY]` where the empty map is `[:]`, boundary examples using comfortable values where the boundary value belongs, a hand-rolled separator inside the rule that forbids one, and a TDD example demonstrating the `ERROR+1` encoding the guidance names as the thing not to write. Illustrations also moved off borrowed business domains — the legend now reads `2 x 5 mg tablet` against `G3/HEAT/zone-2`
- **spec-by-example** and **table-driven-testing**: Two sentences in *Assume the Table Is Published* rendered with a duplicated article — "A the note beneath the table sentence naming a value" — because the shared source was built around a bare noun for a surface both skills name with an article

### Removed

- **tabletest**: Five of the eight references — `column-design.md`, `common-patterns.md`, `table-design-advanced.md`, `pair-programming.md` and `testing-reveals-bugs.md`, about 1,300 of 1,746 lines. A reference is for a corner case whose trigger you can see in your own task before opening the file; "torn between maps and separate columns" is a judgement, not a trigger, and the table design behind it is in the skill file, which is always read. The patterns that did have a trigger moved into `type-converters.md` and a new short file
- **tabletest**: The "iterative column evolution" and "progressive refinement" phases went with them. They described starting from parameter names and fixing them after the tests go green, which contradicts naming columns in domain terms in the first draft

## [1.8.0] - 2026-07-31

### Fixed
- **tabletest**: Corrected the rule for multiple type converters. The skill claimed two `@TypeConverter` methods returning the same wrapper type are selected by matching the parameter name — they are not, and the published example fails every row with `TableTestException: Multiple type converters found`. Selection is by return type alone, and the match is on the *erased* type, so `Optional<String>` and `Optional<Boolean>` collide with each other. Several parameters of one wrapper type share a single converter
- **tabletest**: "Irrelevant input" meant two opposite things in two places. An input **another** rule owns is held at one obviously-valid value; an input **this** rule claims not to affect the outcome has to vary across the values it ignores, or the claim cannot be contradicted by any row. The distinction is now stated once, as a question to ask of your own table, and both misuses are corrected. Same fix applied to the table-driven-testing skill, which carried the identical conflict in its own checklist
- **tabletest**: A converter is no longer described as being "for formatting only". Any domain object built from a table value belongs in one, whatever the construction idiom. What a regex inside a converter signals is a *cell* carrying two values — the repair is a column, never moving construction back into the test body
- **tabletest**: A column blank for most of its rows now collapses into a map column before any table is split. Splitting first produced several tables fixing the same setup and reporting the same output column, which the skill elsewhere calls an over-split
- **tabletest**: The worked example for annotation order no longer breaks three rules while demonstrating a fourth — its description published the whole fee algorithm, it claimed an input did not matter while never varying it, and it fused a classification with the arithmetic that follows it
- **tabletest**: The comments-and-grouping example tested no system — it asserted `output == input * input`, recomputing its own expectation. It now calls a policy object, and its two comment groups each straddle a real band boundary instead of labelling rows "basic" and "edge"
- **tabletest**: The last worked examples naming a row after its own answer are fixed (`Alice succeeds` beside `Result?` `SUCCESS`, `Primary master, both ok` beside `Primary OK`). Two of them could not be repaired by renaming: the row's real input was named only in the scenario text and appeared in no column, so the tables now carry that input and the names describe it

### Removed
- **tabletest**: The async-and-performance reference. Almost all of it was general advice on testing asynchronous code — latches, thread-safe collections for recording call order, timing assertions — rather than anything about expressing those tests as a table. The parts that were TableTest-specific already lived elsewhere: the `<50` upper-bound cell convention and its converter, waiting for off-thread work before asserting, and map columns for composite request data. One distinction was kept on the way out: a range assertion still beats an upper bound when the rule is "the duration the system reported matches the real one"

### Changed
- **tabletest**: Guidance that encoded two values into one cell (`ERROR+1`, `TIMEOUT+3`, `OK in 10ms`) is removed. A flattened cell has to be parsed back in the method body, which tests the format rather than the rule; a pair that is really one value is a domain type, and a pair that is two values is two columns
- **tabletest**: Patterns that kept a table short by moving its meaning into the test body are replaced. An unshowable expected value (an ANSI escape, Base64) gets a type whose constants carry it, so the table names the constant and built-in enum conversion does the rest — not a lookup map resolved in the body. Composite keys use short real values (`acme:search:v2`), not single-letter placeholders needing a legend the table does not contain
- **tabletest**: Worked examples no longer put `if`, `switch` or a ternary in a `@TableTest` method body, no longer leave an expectation column holding one value in every row, and are named for the action the code performs rather than `test…`

## [1.7.0] - 2026-07-30

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
