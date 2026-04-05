Write a TableTest for this method:

```java
List<String> filterTags(List<String> tags, String category)
```

The rules are:
- If category is "tech", keep only tags starting with "tech:" or "dev:"
- If category is "business", keep only tags starting with "biz:"
- Empty list input returns empty list
- Tags containing special characters like pipes (|) or brackets ([, ]) should be preserved as-is
- Null category returns all tags unfiltered

I want to test with inputs like: ["tech:java", "biz:sales", "dev:ci"], ["biz:hr|recruiting", "tech:go"], and empty list [].
