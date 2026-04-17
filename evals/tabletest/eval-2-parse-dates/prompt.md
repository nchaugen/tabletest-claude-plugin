A Gradle Java project has been created for this feature.

Under `src/main/java/com/example`, the project already contains a stub `DateParser` with `parseDate(String input)`. The parsing logic is not implemented yet.

We want to write tests before implementing the parser.

Write TableTests for `DateParser.parseDate(...)`.

The parser should handle:
- ISO format (`2024-01-15`)
- Slash format (`15/01/2024`)
- Short year (`24-01-15`)
- null input should return null
- empty string should throw `IllegalArgumentException`

It is OK to keep a stub implementation for the parser so the tests compile, but do not implement the parsing logic yet.
