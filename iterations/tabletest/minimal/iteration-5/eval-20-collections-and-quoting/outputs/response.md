Here's the TableTest for `filterTags`, organized across three `@TableTest` methods by concern:

```java
import org.tabletest.junit.TableTest;
import org.tabletest.junit.TypeConverter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FilterTagsTest {

    private final TagService subject = new TagService(); // adjust to actual class name

    @TableTest("""
        Scenario                        | Tags                            | Required Categories | Category | Filtered Tags?
        tech keeps tech and dev tags    | [tech:java, biz:sales, dev:ci]  |                     | tech     | [tech:java, dev:ci]
        business keeps biz tags only    | [tech:java, biz:sales, dev:ci]  |                     | business | [biz:sales]
        null category keeps all tags    | [tech:java, biz:sales, dev:ci]  |                     |          | [tech:java, biz:sales, dev:ci]
        tech matches no tags            | [biz:sales, biz:hr]             |                     | tech     | []
        business matches no tags        | [tech:java, dev:ci]             |                     | business | []
        """)
    void filtersByCategory(List<String> tags, Set<String> requiredCategories, String category, List<String> filteredTags) {
        assertEquals(filteredTags, subject.filterTags(tags, requiredCategories, category));
    }

    @TableTest("""
        Scenario                          | Tags                            | Required Categories | Category | Filtered Tags?
        tech and dev required             | [tech:java, biz:sales, dev:ci]  | {tech, dev}         |          | [tech:java, dev:ci]
        biz required                      | [tech:java, biz:sales, dev:ci]  | {biz}               |          | [biz:sales]
        null required keeps all           | [tech:java, biz:sales, dev:ci]  |                     |          | [tech:java, biz:sales, dev:ci]
        required narrows within category  | [tech:java, biz:sales, dev:ci]  | {tech}              | tech     | [tech:java]
        disjoint category and required    | [tech:java, dev:ci]             | {biz}               | tech     | []
        """)
    void filtersByRequiredCategories(List<String> tags, Set<String> requiredCategories, String category, List<String> filteredTags) {
        assertEquals(filteredTags, subject.filterTags(tags, requiredCategories, category));
    }

    @TableTest("""
        Scenario                    | Tags                                           | Required Categories | Category | Filtered Tags?
        empty list                  | []                                             |                     | tech     | []
        tag with pipe preserved     | "[biz:hr|recruiting, tech:go]"                 |                     | business | "[biz:hr|recruiting]"
        tag with brackets preserved | ["tech:[framework]", biz:sales]                |                     | tech     | ["tech:[framework]"]
        tag with newline preserved  | ["tech:java\\nEnterprise Edition", biz:sales]  |                     | tech     | ["tech:java\\nEnterprise Edition"]
        """)
    void handlesSpecialCharactersAndEdgeCases(List<String> tags, Set<String> requiredCategories, String category, List<String> filteredTags) {
        assertEquals(filteredTags, subject.filterTags(tags, requiredCategories, category));
    }

    @TypeConverter
    public static List<String> parseTagList(String value) {
        if (value == null) return null;
        String trimmed = value.trim();
        if ("[]".equals(trimmed)) return List.of();
        String inner = trimmed.substring(1, trimmed.length() - 1);
        List<String> elements = new ArrayList<>();
        int i = 0;
        while (i < inner.length()) {
            if (inner.charAt(i) == '"') {
                int end = inner.indexOf('"', i + 1);
                elements.add(inner.substring(i + 1, end).replace("\\n", "\n"));
                i = end + 1;
                if (i < inner.length() && inner.charAt(i) == ',') i++;
                if (i < inner.length() && inner.charAt(i) == ' ') i++;
            } else {
                int comma = inner.indexOf(',', i);
                if (comma == -1) {
                    elements.add(inner.substring(i).trim().replace("\\n", "\n"));
                    break;
                }
                elements.add(inner.substring(i, comma).trim().replace("\\n", "\n"));
                i = comma + 1;
                if (i < inner.length() && inner.charAt(i) == ' ') i++;
            }
        }
        return elements;
    }
}
```

**Key decisions:**

- **Three concerns, three tables**: category filtering, required-categories filtering (including combined), and special characters/edge cases.
- **Combined rows in `filtersByRequiredCategories`**: rows 4 and 5 cover the interaction between both parameters — "required narrows within category" shows `{tech}` cutting out `dev:ci` even though category="tech" would allow it, and "disjoint" shows both filters producing nothing when they target different prefixes.
- **`@TypeConverter` for `List<String>`**: needed because tags can contain `\n` sequences that built-in conversion doesn't process. The converter handles unquoted elements (simple tags), quoted elements (those containing `[` or `]`), and `\\n` → newline substitution.
- **Pipe cells quoted at cell level**: `"[biz:hr|recruiting, ...]"` — the outer double quotes protect the `|` from the table column-separator parser. The TypeConverter then receives the raw `[biz:hr|recruiting, ...]` string and splits on `,` normally.
- **Bracket elements quoted inside the list**: `["tech:[framework]", ...]` — the inner double quotes around `tech:[framework]` prevent the `]` from confusing the TypeConverter's bracket-counting logic.
- **`\\n` in text block**: Java text blocks process `\\n` into literal `\n` (two chars), which the TypeConverter's `.replace("\\n", "\n")` then converts to an actual newline.