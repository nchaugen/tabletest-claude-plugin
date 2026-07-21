A Gradle Java project has been created for this feature.

Under `src/main/java/org/example`, the project already contains a stub `TagFilter`. The filtering logic is not implemented yet.

We want to write tests before implementing the filter.

Write TableTests for an implementation of `TagFilter`.

It filters the given tags according to these rules:
- If category is "tech", keep only tags starting with "tech:" or "dev:"
- If category is "business", keep only tags starting with "biz:"
- If category is anything else, keep only tags starting with that prefix followed by ":"
- If optional is provided, also keep tags whose category prefix is in the optional set
- Empty list input returns empty list

Tags follow this format:
- Tag strings containing special characters like pipes (|) or brackets ([, ]) should be preserved as-is
- Tag strings may contain newlines, e.g. "tech:java\nEnterprise Edition"
- An empty tag string is never kept, whatever the category
- Null category returns all tags unfiltered
- Null or empty optional means no optional category filter is applied

The tests should cover relevant variations of input.

It is OK to keep a stub implementation for the filter so the tests compile, but do not implement the filtering logic yet.
