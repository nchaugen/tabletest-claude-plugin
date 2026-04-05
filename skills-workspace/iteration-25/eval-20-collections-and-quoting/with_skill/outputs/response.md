Working through the analysis steps internally (one concern: "filter tags by category prefix"), here's the complete test:

```java
import org.junit.jupiter.api.DisplayName;
import org.tabletest.junit.TableTest;
import org.tabletest.junit.Description;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TagFilterTest {

    private final TagFilter filter = new TagFilter();

    @DisplayName("Tag filtering by category")
    @Description("""
        Open: behaviour for unknown category values (not "tech" or "business")
        is unspecified. Prefix matching is assumed to be case-sensitive.
        """)
    @TableTest("""
        Scenario                               | Tags                               | Category         | Filtered?
        tech keeps tech: and dev: prefixes     | [tech:java, biz:sales, dev:ci]     | tech             | [tech:java, dev:ci]
        business keeps only biz: prefix        | [tech:java, biz:sales, dev:ci]     | business         | [biz:sales]
        no tags match category                 | [biz:sales, biz:hr]                | tech             | []
        pipe in biz tag preserved for business | ["biz:hr|recruiting", tech:go]     | business         | ["biz:hr|recruiting"]
        pipe in biz tag excluded for tech      | ["biz:hr|recruiting", tech:go]     | tech             | [tech:go]
        brackets in tech tag preserved         | ["tech:[ci]", biz:sales]           | tech             | ["tech:[ci]"]
        null category returns all tags         | [tech:java, biz:sales, dev:ci]     |                  | [tech:java, biz:sales, dev:ci]
        empty list returns empty list          | []                                 | {tech, business} | []
        """)
    void shouldFilterTagsByCategory(List<String> tags, String category, List<String> filtered) {
        assertEquals(filtered, filter.filterTags(tags, category));
    }
}
```

A few design decisions worth noting:

**Special character quoting** — elements containing `|` or `[`/`]` are quoted individually within the list (e.g., `["biz:hr|recruiting", tech:go]`), not the whole collection. The last row's expected `["tech:[ci]"]` uses the same quoting.

**Value set on the empty list row** — `{tech, business}` expresses "empty input returns empty regardless of category" as a single declarative row rather than two identical rows. Null category + empty list isn't a separate row since it produces the same trivially-empty result covered by the null-category row.

**`@Description` for the open question** — unknown category behavior (e.g., category `"marketing"`) isn't specified in the requirements. Worth flagging rather than silently picking an interpretation.