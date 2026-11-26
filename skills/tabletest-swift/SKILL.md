---
name: tabletest-swift
description: Use when writing or converting tests in a Swift project whose test target already depends on TableTest (tabletest-swift) — a Package.swift with the tabletest-swift package, or a test file that imports TableTest — or when the user asks to use TableTest in a Swift project. Trigger whenever the user wants several scenarios of one behaviour tested, asks for table-driven or parameterised tests, wants @Test(arguments:) or repetitive @Test functions converted into tables, or asks for tests of a feature — even if they do not say "TableTest". A @TableTest is a Swift Testing test, so a request for "Swift Testing", "table-driven" or "parameterised" tests in such a project belongs here. Also use for questions about @TableTest syntax, cell values, value sets or type conversion in Swift. Do not use for a Swift project without TableTest when the user does not ask for it (use table-driven-testing) or for Java and Kotlin (use tabletest).
---

# TableTest for Swift Skill

Use this skill before you write or convert Swift tests in a project that depends on TableTest.
A `@TableTest` function runs once for each row of a plain-text table, as Swift Testing tests.

## Which Part to Read

| You are about to | Go to |
|---|---|
| add, change or remove rows of a `@TableTest` that exists | this file; go to *Changing an Existing Table* |
| write a new `@TableTest` function, convert a test, or add or remove a column | `references/table-design.md`, in full, before the first row |

The second case is not optional. The design rules decide the columns, and this file does not
hold them. Use the Read tool, not `cat` or `head`: a shell command shows only the start of a
file this long.

## Pre-Check

Before you write a `@TableTest`, check three things. **If one is missing, add it, or in an Xcode
project tell the user the steps.**

1. **The dependency.** TableTest needs Swift 6.2 or later (Xcode 26), and macOS 13 or iOS 16 or
   later.

   In a Swift package, `Package.swift` has the package, and the test target has the `TableTest`
   product. Add what is missing:

   ```swift
   // swift-tools-version: 6.2
   let package = Package(
       name: "WasteSorting",
       platforms: [.macOS(.v14)],
       dependencies: [
           .package(url: "https://github.com/nchaugen/tabletest-swift.git", from: "0.1.2"),
       ],
       targets: [
           .target(name: "WasteSorting"),
           .testTarget(
               name: "WasteSortingTests",
               dependencies: ["WasteSorting", .product(name: "TableTest", package: "tabletest-swift")]
           ),
       ]
   )
   ```

   Write the version as it stands here. `0.1.2` is the lowest release this skill's guidance needs,
   and `from:` takes every later 0.x release, so there is no need to look up the newest one.

   In an Xcode project, the package is in the project, and the `TableTest` library is linked to the
   test target, not to the app target. An agent cannot do this step: tell the user to select
   **File > Add Package Dependencies…**, enter the package URL, and add `TableTest` to the test
   target.

2. **The imports.** A test file has `import TableTest` and `import Testing`, and `@testable import`
   for the module under test.
3. **The macro trust.** Xcode asks once for each TableTest version to trust the `TableTestMacros`
   macro: select **Trust & Enable**. `swift build` and `swift test` do not ask. On a CI machine,
   pass `-skipMacroValidation` to `xcodebuild`.

**Keep a plain `@Test`** when:

- The test has one case, and no table near it has the same shape.
- **The arguments come from the app**, such as `@Test(arguments: Grade.allCases)`. A case added in
  the app is then tested with no change in the test. A table that lists the cases by hand loses
  that. Keep the `@Test(arguments:)`, and say why in a comment or in your answer.
- The test counts or reads its own cases. A test cannot read the rows of a `@TableTest`.
- The setup is complex and specific to the test (a server, a clock, concurrency), and columns would
  hide it.

## Quick Example

```swift
import TableTest
import Testing
@testable import WasteSorting

struct BinTests {
    @TableTest("""
        Scenario       | Items | Bin Capacity | Room Left?
        Below capacity | 4     | 8            | true
        At capacity    | 8     | 8            | false
        """)
    func knowsWhenABinHasRoom(items: Int, binCapacity: Int, roomLeft: Bool) {
        #expect(Bin(capacity: binCapacity, items: items).hasRoom == roomLeft)
    }
}
```

## Table Syntax

### The Function and Its Table

- **Put `@TableTest` on a function inside a type**: a struct, a class, an enum or an extension.
  At file scope it does not compile.
- **The columns map to the parameters by order, not by name.** Give each data column one
  parameter, in the same order. Keep the header and the parameter names close.
- **Give every table a scenario column, as its first column.** Its names let a reader find a row
  and talk about it, and they name each row in the test results. When the table has one more column
  than the function has parameters, the first column is the scenario column. It maps to no
  parameter. There is no `@Scenario` parameter in Swift.
- **A scenario name says where its row stands against the rule**: a boundary, a tier, a reason, a
  situation. Read each name beside its row. A name has a fault in three cases:
  - a word of the name is, or paraphrases, a value in an expectation cell of its row (`Bin full`
    beside `Room Left?` = `false`), also when the word describes the state the row starts in;
  - the name is a generic label (`Case 2`);
  - the name only repeats the row's input cells (`8 items, capacity 8` beside `8` and `8`).

  `At capacity` passes all three. Repair a fault with a better name, never by removing the column.
- **A scenario name is unique in its table.** Xcode's result bundle merges two rows with one name.
- **End each expectation header with `?`** (`Room Left?`). Never put `?` on an input header.
- The function can be `static`, `async` and `throws`. The type's `init` can be `async` and `throws`.
  For main-actor code, put `@MainActor` on the type.

`@TableTest` has three forms:

| Form | Use |
|---|---|
| `@TableTest("""…""")` | The test has the name of the function. |
| `@TableTest("Title", traits…, table: """…""")` | A display name and Swift Testing traits, as `@Test` takes them. |
| `@TableTest(traits…, table: """…""")` | Traits and no display name. |

Two titles in one type must differ. A title cannot hold a backtick or a backslash.

**A doc comment (`///`) above the function is the table's description.** Use it for the fixture,
the domain context and the open questions. It must not publish the algorithm.

### Single Values and Quoting

Text needs no quotes. The whitespace around a value is removed. Quote with `'…'` or `"…"` when the
text has a `|`, has whitespace at its ends, is empty, or starts with `[`, `{`, `'` or `"`.

- **There is no escape character.** To put `'` in a value, quote it with `"`, and the other way
  round: `"d'Yquem"`, `'"Vides"'`. Unquoted, `"Vides"` loses its quotes.
- **The table is a Swift string literal**, so a backslash in a cell is `\\` in the source.
- **A blank cell is `nil`.** Its parameter must be optional (`Int?`).
- **`''` is an empty string.** It is not `nil`, and it does not convert to a number.
- **A JSON text is one quoted cell**: `'{"rooms": 3}'`. Unquoted, it reads as a map.
- **A cell cannot hold a new line.**

### Lists, Sets and Maps

- A list is `[1, 2, 3]`; `[]` is empty. It converts to an `Array`.
- A set is `{a, b}`; `{}` is empty. It converts to a `Set` — or it is a value set (below).
- A map is `[early: 2, late: 1]`; `[:]` is empty. It converts to a `Dictionary` or to a
  `Decodable` struct.
- Collections nest: `[[a, b], [c]]`.
- **Inside a collection, an element ends at `,` and at `:`.** Quote an element that has a comma
  or a colon: `['restRequired(hours: 10)', fit]`, `['12:30']`.
- **A collection cannot hold a `nil` element.** Write absence in domain words (see *Values in
  Cells*), or blank the whole cell.

### Value Sets

A set in the column of a parameter that does not take a set is a **value set**. The row runs once
for each element, and each case shows its element: `Below the minimum (Age = 16)`. With value sets
in two columns, the row runs once for each combination.

```swift
@TableTest("""
    Scenario       | Bin Capacity | Items  | Room Left?
    Below capacity | 8            | {0, 7} | true
    """)
func hasRoomBelowCapacity(binCapacity: Int, items: Int, roomLeft: Bool) {
    #expect(Bin(capacity: binCapacity, items: items).hasRoom == roomLeft)
}
```

- **A value set replaces a whole cell.** `{a, b}` inside a list or a map is a `Set` in that
  position and adds no cases. To vary one part of a composite value, split it into columns first.
- **A member can be a collection.** `{[], [2020], [2020, 2020]}` in an `[Int]` column is three
  cases.
- A parameter of type `Set`, an `OptionSet` or an optional of one takes the whole set.
- An empty value set is an error.

**Count the cases, not the rows.** A row with a value set of three elements is three cases. When
you convert tests, the number of cases stays the same, or your answer explains the change.

### Comments and Formatting

A line that starts with `//` is a comment, and a blank line is skipped.

Align the columns with the formatter when it is available:

```sh
swift package --allow-writing-to-package-directory tabletest-format
```

The macro checks the table when the tests compile. A malformed row is a compile error at that row,
so `swift build --build-tests` is the parse check.

## Values in Cells

### How a Cell Converts

Each cell converts to the type of its parameter. The same rules apply to the elements of a list or a
set, the keys and values of a map, and the fields of a `Decodable` struct. The first rule that
applies decides:

1. The type's own conversion: `TableValueDecodable` or `TableTextDecodable`.
2. A built-in conversion: `String`, the number types, `Bool`, `Decimal`, `URL`, `Date`
   (`2025-03-14`, or a date and time with its zone), `ClosedRange` and `Range` (`2028...2030`),
   `Character`, `Duration` (`1.5 seconds`), `TimeZone`, `Locale`, `Locale.Currency`,
   `DateComponents`.
3. An enum with `String` raw values reads its **raw value**.
4. Another `CaseIterable` enum reads its **case name**.
5. `Decodable`. An enum with associated values also reads its **Swift spelling**.
6. `LosslessStringConvertible` reads the text of the cell.
7. Another enum with raw values reads its raw value.

**An enum with `String` raw values reads the raw value, not the case name.** A raw value is often the
word of the domain (`extra hot`). A case name in its cell is an error that names the raw value.

### Where Conversion Code Goes

**Decide this before you write a column.** Take the first step that works:

1. **The app type gets one word.** An app enum or struct that a cell holds gets `Codable`, or
   `CaseIterable` for a plain enum, in the app's own source. The conformance is synthesised, so it
   adds no behaviour. Then the cell reads the value as Swift spells it. An enum that already has
   `String` raw values needs nothing.
2. **A situation or a user action is a test-local enum with `String` raw values.** The enum builds
   the fixture from the word in the cell (see *Choose the Input Columns by What Decides the Result*
   in `references/table-design.md`). This models the test's input. It is not a conversion of an app
   type.
3. **A form that the app never decodes is a `TableValueDecodable` conformance in the test target.**
   For example: a map cell that names only the fields it sets, or one word that stands for a
   catalogue entry. Read a map with `readingFields`.
4. **A text form that you invent (`250 g`, `<50`) is a `TableTextDecodable` conformance in the
   test target.**

**Never add an `init(from:)` to an app type for a test.** The app's decoding would change with it.
Steps 3 and 4 are the place for hand-written conversion code, also for a cell type of the test
target: a conformance is shorter than `init(from:)`, and a bad cell is reported by its value.

**Never convert an app type by hand in the test.** A `String` column that a `switch` or a dictionary
turns into the app's enum, a test-local copy of the app's enum, or a conformance that lists the
cases by hand — each is step 1 not taken. Conversion code in the test body is worse: the body must
only arrange, act and assert.

**Code that reads a cell lives with the type of the cell.** A branch on a cell's value, a loop over
its elements, a default for it, or a reshape of it into the form an app API takes is in a conversion
conformance (steps 3 and 4) or in a member of that cell's own type: a branch on a `Ward` cell is in
`Ward`, never in a member of another enum. The test function has none of these. A helper of the test
type that takes a cell value has none either: it calls the member of each cell.

Swift facts that decide where the code can go:

- **Swift cannot synthesise `Codable`, `Decodable` or `CaseIterable` in the test target**, also not
  with `@retroactive`. That is why the one word goes on the app type.
- **Under `@testable import`, the witness of an app type must be `public`**:
  `public init(tableValue:)` or `public init?(tableText:)`. Without it the build fails.
- **`@retroactive` only in an Xcode app project.** In a Swift package, the app and its tests are one
  package, and `@retroactive` is an error.
- A type that conforms to `TableTextDecodable` cannot be a field of a synthesised `Decodable`
  struct.
- **In an app target with default main-actor isolation**, the synthesised conformances of an app
  type are main-actor-isolated. A test type that is not `@MainActor` cannot convert a cell to it:
  the error names an "isolated conformance". Mark the app type `nonisolated`.
- **A nested type does not inherit `@MainActor` from the test type.** A member of a test-local
  enum that builds a main-actor fixture needs `@MainActor` itself.

### Enums with Associated Values

A `Codable` enum with associated values reads a case as Swift spells it. **An outcome with data is one
cell**, never a case-name column and number columns that the body puts back together.

```swift
// In the app, Sources/Crew/Clearance.swift — Codable is the only addition:
public enum Clearance: Codable, Equatable, Sendable {
    case fit
    case restRequired(hours: Int)
    case grounded(reason: String)
}
```

```swift
@TableTest("""
    Scenario                     | Hours Since Rest | Sectors Flown | Clearance?
    Inside the duty limit        | 9                | 3             | fit
    Past the duty limit          | 14               | 3             | restRequired(hours: 10)
    Past the sector limit        | 9                | 7             | grounded(reason: sector limit)
    """)
func clearsAPilotForTheNextSector(hoursSinceRest: Int, sectorsFlown: Int, clearance: Clearance) {
    #expect(DutyRules.clearance(hoursSinceRest: hoursSinceRest, sectorsFlown: sectorsFlown) == clearance)
}
```

A value with no label is given by its position: `between(8, 12)`. In a list, quote a spelling that
has a label or two values: `['restRequired(hours: 10)', fit]`. Spellings nest as Swift writes them,
with no quotes: `period(month(year: 2025, month: 3))`, `grape([key: chardonnay])`.

### Structs

A map converts to a `Decodable` struct. **When the struct only gathers the inputs of one call, give
each field its own column** and build the struct in the test body with the app's initialiser. That is
arranging, not the construction that *Design Black-Box Tables* (in `references/table-design.md`)
moves out of the body: it has no branch, no parsing and no default. A map cell is for a struct that
the rule reads as one value.

- An optional field can be left out of the map.
- A list, set or map field that the map leaves out is empty.
- **Any other field needs a key in every map, also a field with a default value.** Synthesised
  `Decodable` ignores the default. When the cell must name only the fields it sets, write a
  `TableValueDecodable` conformance in the test target with `readingFields`:

```swift
// In the test target. The witness is public, because ClimateZone is an app type.
extension ClimateZone: TableValueDecodable {
    public init(tableValue: TableValue) throws {
        self = try tableValue.readingFields { fields in
            var zone = ClimateZone()
            zone.targetHumidity = try fields.value("targetHumidity") ?? zone.targetHumidity
            zone.ventLimit = try fields.value("ventLimit") ?? zone.ventLimit
            return zone
        }
    }
}

@TableTest("""
    Scenario                 | Climate Settings     | Humidity % | Vent Position?
    Default target, damp air | [:]                  | 85         | open
    Raised target, same air  | [targetHumidity: 90] | 85         | closed
    """)
func opensTheVentAboveTheTargetHumidity(settings: ClimateZone, humidity: Int, vent: VentPosition) {
    #expect(settings.ventPosition(atHumidity: humidity) == vent)
}
```

A key that the conversion does not read fails the cell, so a misspelt key cannot be left out
silently. `fields.required("key")` fails the cell when the map leaves the key out.

### Your Own Text Form

For a text form that you invent, conform the type to `TableTextDecodable`. Return `nil` when the text
does not convert, and TableTest reports the cell:

```swift
extension Latency: TableTextDecodable {
    public init?(tableText: String) {
        guard tableText.hasPrefix("<"), let milliseconds = Int(tableText.dropFirst()) else { return nil }
        self.init(maximumMilliseconds: milliseconds)
    }
}
```

The `guard` is in the conversion, not in the test function. The test function stays free of
branches.

**A conversion that reads more than one part of the text is code that every row depends on.** Give it
its own small `@TableTest`: the text in, the value out.

### Absent Values

- **Blank is `nil`.** The system under test decides what `nil` means, never the test.
- **Absence inside a collection is a domain word**, such as a `none` case of the app enum, or the
  element left out. A collection has no `nil` element.
- **An empty collection is `[]`, `{}` or `[:]`**, not a blank cell.
- **A value set cannot hold a blank.** Where a blank and a value give the same result, write two rows,
  and name them by absence and presence. `''` is never a stand-in for `nil`.

## Expressing These Rules in TableTest for Swift

### Expressing a Rejection

*Model Rejection as an Expected Column* (in `references/table-design.md`) decides **whether**
accepted and rejected rows share a table. When they share one, the column holds the error, and is
blank where nothing is rejected.

**Where the call returns the rejection as a case of its result, not as a thrown error,** there is no
separate rejection column. The result column holds the outcome of every row, and the rejection is one
cell with its data, as *Enums with Associated Values* writes it: `restRequired(hours: 10)` beside
`fit`. Never a column of the rejection's data alone, read out of the result by a helper.

For a thrown error, the error enum gets `String` raw values or `Codable`, and `Equatable`:

```swift
@TableTest("""
    Scenario               | Dose (mg) | Rejected?
    At the minimum dose    | 0         |
    Just below the minimum | -1        | belowMinimum
    """)
func rejectsADoseBelowTheMinimum(dose: Int, rejected: DoseError?) throws {
    #expect(try errorThrown(as: DoseError.self) { try DoseCheck.validate(milligrams: dose) } == rejected)
}

// With the other helpers, at the bottom of the type
private func errorThrown<E: Error>(as type: E.Type, by action: () throws -> Void) throws -> E? {
    do {
        try action()
        return nil
    } catch let error as E {
        return error
    }
}
```

**When the rejection is a separate concern, it is its own test.** One rejected case is a plain `@Test`
with `#expect(throws: DoseError.belowMinimum) { … }`. Several are a table with a `Rejected?` column,
never one `#expect(throws:)` that every row shares.

**A rejection that leaves the state unchanged needs a row where the state changes.** Show the state
before and after the call as columns, and keep the accepted case as a row of the table. Without that
row, the after-state column is the same in every row, and no row shows that the test can see a
change. Where the accepted case is its own table, the unchanged state is one row, with a value set of
the reasons.

### A Threshold the Code Holds

A threshold column holds the one limit that the row's boundary straddles. **A ladder is not a
threshold:** each step of a ladder is a row, and no column lists the steps.

Where the code reads the limit from an app constant, the body asserts the constant against the column
first. The table then fails when the constant moves:

```swift
@TableTest("""
    Scenario       | Hours Since Rest | Max Duty Hours (Policy) | Fit To Fly?
    At the limit   | 13               | 13                      | true
    Past the limit | 14               | 13                      | false
    """)
func decidesFitnessToFly(hoursSinceRest: Int, maxDutyHours: Int, fitToFly: Bool) {
    #expect(DutyRules.maximumDutyHours == maxDutyHours)
    #expect(DutyRules.isFitToFly(hoursSinceRest: hoursSinceRest) == fitToFly)
}
```

### Converting `@Test(arguments:)`

- **A named collection of tuples, or inline literals, becomes one table.** The tuple labels, or the
  fields of a row struct, become the columns. A value the old test built from fields (an app struct,
  an enum case with data) becomes one cell, by *Where Conversion Code Goes*.
- **Two argument collections are a cross product.** Write the combinations as rows, or as value sets
  in one row when every combination has the same result.
- **`zip(a, b)` pairs its arguments.** Each pair is one row.
- **Arguments computed from the app stay**, with their reason — see *Pre-Check*.
- **An old case description becomes the scenario name.** Make each name unique in its table.


## Workflow

**Budget your reasoning.** When the prompt lists the concerns, use them. When you find yourself
analysing one concern again, stop and write code.

**Write incrementally.** Write one `@TableTest` function at a time. Each function written is a
checkpoint that a timeout cannot take away.

**Read the tests once more before you build.** Read each test function and each helper that takes a
cell value: no branch, loop, `??` or `.map` on a cell outside a conversion or a member of that
cell's own type. Read each scenario name beside its row: no name has one of the three faults in
*The Function and Its Table*.

**Build and test before you finish.** Run `swift build --build-tests`, then `swift test`. The macro
reports a malformed table at compile time.

### Changing an Existing Table

1. Read the table and its doc comment. The new case takes the form of the rows beside it.
2. Add the row where its neighbours are: a boundary beside its other side, a tier in tier order.
3. Give it a scenario name that is unique in the table, in the form of the other names, and that
   says where the row stands against the rule (see *The Function and Its Table*). Where the
   table has no scenario column, add one, and give every row a name.
4. Where an input does not change the outcome of the new case, use a value set.
5. **Where the case needs a column that the table does not have, or an assertion that the body
   does not make, this is a design change: read `references/table-design.md` first.**
6. **Where the case belongs to a `@Test(arguments:)` test**, never add a plain `@Test` beside it for
   a case of the same rule. Add the case to the collection that the test reads. Then:
   - where the project asks you to convert the tests you change, and the conversion needs no new
     conversion code, convert first, as a step of its own, then make the change;
   - where the conversion needs conversion code (an app struct in a tuple, a conformance on an app
     type), make the change in the present form, and name the unconverted test in your answer.
7. Run `swift build --build-tests`, then `swift test`.

To write a new table or convert a test, read `references/table-design.md`: it holds the design
workflows.

## Quality Checks

**Table design** — the shared rules of `references/table-design.md`, in checklist form:

<!-- BEGIN GENERATED table-design-checks — do not edit here; source is shared/table-design/ -->

- [ ] **One rule per table**: every row and column serves this table's one axis; a behaviour you cannot name without "and" has been split, and a rule that is a conjunction of independent conditions has one table per condition rather than their cross-product — but inputs that are contributions to one combined answer stay in one table, with rows that show them combining
- [ ] **Complete outputs**: all observable outputs of the same rule sit in one table, and every expectation column there is exercised by the rows that table varies — one constant down all rows, or moving only as a side effect of another, belongs to a different rule's table
- [ ] **Decomposed, not over-split**: no table mixes concerns (blank-throughout columns, qualified scenario names, two groups of expectation columns), no table carries both a value and the raw data another rule derives it from, and no set of same-fixture tables reports one expectation column that a family column would collapse
- [ ] **Combining tables prove an interaction**: any table exercising several rules together shows behaviour the single-rule tables cannot (a precedence, an ordering), not the earlier rules re-run end to end
- [ ] **Rules separated from arithmetic**: every expectation cell is predictable from its row in one step; a classification and the calculation that follows it are two tables
- [ ] **One row per obligation**: every obligation of the concern is discharged by some row, and every row discharges one no other row in that table reaches; where two rows share an expectation, swapping what differs between them would change an expectation cell in that table — not a value further past the same boundary, a larger n in the same direction, or an input the rule ignores even though it names it
- [ ] **Every tier once**: a tier ladder has one row per tier — all of them, none twice — and every boundary is exercised from both sides at the finest unit the rule distinguishes, whether by two rows or by a value set whose end members are the tier's own edges, middle tiers included, and a boundary an input reaches through a formula straddled like any other; where one ladder repeats across classes that share its boundary positions, the straddling pairs appear in one class and the rest carry one row per tier
- [ ] **Value set semantics**: value sets appear only where every value produces the same result, never as shorthand for "test several values"; an input this rule claims not to affect the outcome varies across the values it ignores, while an input another rule owns is held at one valid value
- [ ] **Stateful rows independent**: transition rows carry their own before-state and after-state; no row depends on another having run
- [ ] **Held constants declared**: every value the outcome depends on that the table fixes for all rows is a column where it can be one — always so for a threshold or limit the rule turns on — and otherwise named in the title or description as held fixed; never left only in the test body, a field, a conversion helper, or a comment; a value already shown as a column needs nothing further on any surface
- [ ] **Titles form an index**: read the titles as a sorted list — each states an action the code performs (not a label for a topic), one grammatical shape runs across them, and no three share an uninformative opener
- [ ] **No scenario name restates its own row's answer**: read each scenario name beside the expectation cells of that row — none states or paraphrases one of them, and none is a generic label
- [ ] **Expectation columns marked**: at least one column uses the `?` suffix (never a prefix), no input column does, and a compound result stays a native collection rather than a flattened string
- [ ] **Rejection expressed as data**: rejected rows carry the error type or reason in an expectation column, never a hardcoded outcome in the body; accepted and rejected rows share a table only where striking the rejected rows would leave a table stating nothing, and no row branches the assertion
- [ ] **Concrete values**: expectation values are literal domain values traceable to the input columns of their own row — not abstract codes, and not hidden behind named constants
- [ ] **Domain language**: column names use the business vocabulary, not parameter names, field names or internal API terms
- [ ] **Thresholds visible**: a rule that depends on a threshold or limit shows it as a column, with boundary rows at and just past it
- [ ] **Traceability columns**: an intermediate expectation appears only where the value is observable from the public API — never reimplemented from internal logic; if a formula would have to be reimplemented to fill it, decompose instead
- [ ] **Blank means absent**: a column whose input is genuinely absent for a row uses a blank cell, not 0 or a default; an input that is present but irrelevant is a value set instead, and nothing converts a blank to a default on the way in
- [ ] **Black-box design**: columns represent observable inputs and outputs, not internal flags or implementation details
- [ ] **Consistent across tables**: within one table set, one concept has one column name, one kind of value has one notation, one failure has one spelling, and the rows are served by shared helpers rather than per-table copies

<!-- END GENERATED table-design-checks -->

**Input columns:**

- [ ] **Deciding inputs**: each input column is something the code reads to decide the result — a
  situation, an action or judged data — and no scenario name states a deciding input that no column
  holds
- [ ] **Crossed inputs**: every combination of the input columns' values has a row, or a value set
  shows that it does not matter

**TableTest for Swift mechanics:**

- [ ] **In a type**: every `@TableTest` function is inside a struct, class, enum or extension
- [ ] **Parameter alignment**: one parameter for each data column, in column order; the scenario
  column has none
- [ ] **Straightforward function**: no `if`, `guard`, `switch`, ternary or loop in the test function,
  and no `??` or `.map` on a parameter; a helper or an enum member has none of these on a cell of
  another type, and calls that cell's own member
- [ ] **One word on the app type**: an app type in a cell converts through `Codable`, `CaseIterable`
  or `String` raw values on the app type — not a `switch`, a dictionary or a copy of the enum in the
  test target
- [ ] **No decoder in the app**: no `init(from:)` added to an app type for a test, or written for
  a cell type of the test target; hand-written conversion is a `TableValueDecodable` or
  `TableTextDecodable` conformance in the test target, with a `public` witness on an app type, and
  `@retroactive` only in an Xcode app project
- [ ] **Outcomes as Swift spells them**: an enum result with data is one cell
  (`restRequired(hours: 10)`), not columns that the body puts back together
- [ ] **Unique scenario names**: no two rows in one table have the same scenario name
- [ ] **Optional for blank**: a column that can be blank has an optional parameter
- [ ] **Computed arguments kept**: a `@Test(arguments:)` over a collection from the app stays, with
  its reason
- [ ] **Counts kept** (conversions only): the number of cases is the same as before, or the answer
  explains the change
- [ ] **Old framework removed** (XCTest only): no `XCTAssert…`, `XCTestCase` or unused `import XCTest`
- [ ] **It builds**: `swift build --build-tests` succeeds, so every table parses
