Write a TableTest for this method:

```java
List<String> filterTags(List<String> tags, Set<String> requiredCategories, String category)
```

The rules are:
- If category is "tech", keep only tags starting with "tech:" or "dev:"
- If category is "business", keep only tags starting with "biz:"
- If requiredCategories is provided, only keep tags whose category prefix is in the required set
- Empty list input returns empty list
- Tags containing special characters like pipes (|) or brackets ([, ]) should be preserved as-is
- Tags may contain newlines in their display text, e.g. "tech:java\nEnterprise Edition"
- Null category returns all tags unfiltered
- Null requiredCategories means no category filter is applied (all categories allowed)

I want to test with inputs like: ["tech:java", "biz:sales", "dev:ci"], ["biz:hr|recruiting", "tech:go"], and empty list [].
For required categories, use sets like {tech, dev} or {biz}.
