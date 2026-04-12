Looking at the rules, I can identify four concerns worth separating:
1. Category → prefix group mapping
2. `requiredCategories` set filtering
3. Both filters active simultaneously
4. Special character preservation (pipes, brackets) — plus a single `@Test` for newlines since those can't be expressed inline in a list column without reimplementing list parsing

```java
import org.junit.jupiter.api.Test;
import org.tabletest.junit.TableTest;
import java.util.List;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class FilterTagsTest {

    private final TagService subject = new TagService(); // adjust to actual class

    @TableTest("""
        Scenario                      | Tags                                 | Required Categories | Category | Filtered Tags?
        tech keeps tech: and dev:     | ["tech:java", "biz:sales", "dev:ci"] |                     | tech     | ["tech:java", "dev:ci"]
        business keeps only biz:      | ["tech:java", "biz:sales", "dev:ci"] |                     | business | ["biz:sales"]
        null category keeps all       | ["tech:java", "biz:sales", "dev:ci"] |                     |          | ["tech:java", "biz:sales", "dev:ci"]
        empty list returns empty      | []                                   |                     |          | []
        """)
    void filtersByCategory(List<String> tags, Set<String> requiredCategories, String category, List<String> filteredTags) {
        assertEquals(filteredTags, subject.filterTags(tags, requiredCategories, category));
    }

    @TableTest("""
        Scenario                               | Tags                                 | Required Categories | Category | Filtered Tags?
        {tech,dev} keeps tech: and dev: tags   | ["tech:java", "biz:sales", "dev:ci"] | {tech, dev}         |          | ["tech:java", "dev:ci"]
        {biz} keeps only biz: tags             | ["tech:java", "biz:sales", "dev:ci"] | {biz}               |          | ["biz:sales"]
        null required applies no prefix filter | ["tech:java", "biz:sales", "dev:ci"] |                     |          | ["tech:java", "biz:sales", "dev:ci"]
        empty required set excludes all        | ["tech:java", "biz:sales", "dev:ci"] | {}                  |          | []
        """)
    void filtersByRequiredCategories(List<String> tags, Set<String> requiredCategories, String category, List<String> filteredTags) {
        assertEquals(filteredTags, subject.filterTags(tags, requiredCategories, category));
    }

    @TableTest("""
        Scenario                           | Tags                                 | Required Categories | Category | Filtered Tags?
        required narrows tech to tech:     | ["tech:java", "biz:sales", "dev:ci"] | {tech}              | tech     | ["tech:java"]
        required narrows tech to dev:      | ["tech:java", "biz:sales", "dev:ci"] | {dev}               | tech     | ["dev:ci"]
        no overlap between filters         | ["tech:java", "biz:sales", "dev:ci"] | {biz}               | tech     | []
        """)
    void filtersByCategoryAndRequiredCategories(List<String> tags, Set<String> requiredCategories, String category, List<String> filteredTags) {
        assertEquals(filteredTags, subject.filterTags(tags, requiredCategories, category));
    }

    @TableTest("""
        Scenario                           | Tags                             | Required Categories | Category | Filtered Tags?
        pipe in matching tag preserved     | ["biz:hr|recruiting", "tech:go"] | {biz}               |          | ["biz:hr|recruiting"]
        pipe in non-matching tag excluded  | ["biz:hr|recruiting", "tech:go"] | {tech}              |          | ["tech:go"]
        brackets in matching tag preserved | ["tech:[beta]", "biz:sales"]     | {tech}              |          | ["tech:[beta]"]
        """)
    void preservesSpecialCharactersInMatchingTags(List<String> tags, Set<String> requiredCategories, String category, List<String> filteredTags) {
        assertEquals(filteredTags, subject.filterTags(tags, requiredCategories, category));
    }

    @Test
    void preservesNewlineInMatchingTag() {
        String tagWithNewline = "tech:java\nEnterprise Edition";
        assertEquals(
            List.of(tagWithNewline),
            subject.filterTags(List.of(tagWithNewline, "biz:sales"), null, "tech")
        );
    }
}
```

A few design notes:

**Why four tables, not one?** Each table tests a distinct rule. `filtersByCategory` tests the category→prefix mapping. `filtersByRequiredCategories` tests the set membership check. `filtersByCategoryAndRequiredCategories` tests that both filters stack (their intersection, not union). `preservesSpecialCharacters` tests that content is not corrupted by filtering. Mixing them would require many columns that are blank in most rows.

**Why a separate `@Test` for newlines?** A `\\n` in a list element (e.g. `["tech:java\\nEnterprise Edition"]`) would arrive at the test method as the literal two-character sequence `\n`, not a newline — because TableTest would need to parse the list itself to do the replacement. That requires reimplementing list-parsing in a converter, which is more complex than it's worth for a single scenario. A plain `@Test` is cleaner here.

**Quoting in lists:** All elements containing `:` are quoted (e.g. `"tech:java"`) to avoid being parsed as map key:value syntax. Elements with `|` (pipe) or `[` are quoted for the same reason.