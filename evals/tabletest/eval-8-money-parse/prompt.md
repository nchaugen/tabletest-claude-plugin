A Gradle Java project has been created for this feature.

Under `src/main/java/com/example`, the project already contains a stub `MoneyParser` and a simple `Money` type. The parsing logic is not implemented yet.

We want to write tests before implementing the parser.

Write TableTests for `MoneyParser.parse(...)`.

The method should:
- Return a Money object for valid inputs like '10.00' and '0.01'
- Return null for null input
- Throw IllegalArgumentException for: empty string, letters-only input (e.g. 'abc'), and negative values (e.g. '-5.00')

It is OK to keep a stub implementation for the parser so the tests compile, but do not implement the money parsing logic yet.
