One `@TableTest` inside `LanePriceTests` that replaces the five price tests, and the zero-hours
test kept as its own plain `@Test`. The tests pass.

## The table

- **Columns:** a scenario column leftmost, then the lane, the hours, the membership and a `Price?`
  expectation. The scenario names come from the original test names.
- **Rows:** exactly five, one per original price test, with each test's values unchanged:
  standard 2 h → 60, deluxe 2 h → 90, kids 1 h → 20, standard 5 h → 90, deluxe 2 h as a member → 72.
  This is a mechanical conversion: adding rows for other lanes or hours is out of scope.
- **Body:** builds the `Booking` from the columns and makes one `#expect`. No `if`, `switch`,
  `guard` or ternary.
- **Price** may be a `Decimal` column, or any column type whose cells state the price as a number.
  A computed expectation (`90 * 0.8`) is not a cell value.

## The lane column

`Lane` is a plain enum in the app, and a plain enum does not convert from a cell. The fix is one
word on the app type: `CaseIterable`, `Codable`, or `String` raw values on `Lane` in
`Sources/Bowling/`. Each is synthesised and adds no behaviour to the app.

These are wrong: a `String` column turned into a `Lane` by a `switch` or a dictionary in the test,
a test-local copy of the enum, a `TableTextDecodable` that maps words to cases by hand, and an
`init(from:)` written into the app.

## The rejection

The zero-hours test expects `BookingError.noHours` with `#expect(throws:)`. It stays a separate
test. It is not a row of the price table with an optional or a sentinel price.

## Equally good

Column names and order, the title text, and whether the membership column is a `Bool` are free.
A doc comment on the table is optional.
