"TableTest" isn't a name I can map to a specific, well-known Java library with confidence — I want to avoid giving you incorrect Maven coordinates. Could you clarify which library you mean?

A few possibilities:

- **JUnit 5 parameterized tests** (`@ParameterizedTest` + `@CsvSource`/`@MethodSource`) — already included in `junit-jupiter`, no extra dependency needed
- **JUnit Pioneer** (`org.junit-pioneer:junit-pioneer`) — extends JUnit 5 with extras like `@CartesianTest`
- A specific third-party "table test" library you've seen referenced somewhere

If you can share a link or the group/artifact ID you're targeting, I can give you accurate guidance on what to add and any compatibility gotchas with JUnit 5.10.0.