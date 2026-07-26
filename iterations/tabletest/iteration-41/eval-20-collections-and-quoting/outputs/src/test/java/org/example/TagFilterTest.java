package org.example;

import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TagFilterTest {

    private final TagFilter tagFilter = new TagFilterImpl();

    @Description("""
        Optional is left null in every row here; its effect is covered
        separately in shouldApplyOptionalCategoryFilter.
        """)
    @TableTest("""
        Scenario                                    | Tags                                                     | Category | Filtered?
        Tech category keeps tech and dev prefixes   | ["tech:java", "dev:ci", "biz:sales", "random:stuff"]    | tech     | ["tech:java", "dev:ci"]
        Business category keeps only biz prefix     | ["biz:sales", "tech:java", "random:stuff"]              | business | ["biz:sales"]
        Custom category keeps its own prefix        | ["urgent:ticket1", "tech:java", "biz:sales"]            | urgent   | ["urgent:ticket1"]
        Null category returns all tags unfiltered   | ["tech:java", "biz:sales", "random:stuff"]              |          | ["tech:java", "biz:sales", "random:stuff"]
        Empty list returns empty list               | []                                                       | tech     | []
        Empty tag dropped even with null category   | ["tech:java", '']                                       |          | ["tech:java"]
        Special characters preserved as-is          | ["biz:region|EU", "dev:[legacy]"]                       | business | ["biz:region|EU"]
        Newline within a tag is preserved           | ["tech:java\\nEnterprise Edition", "random:stuff"]      | tech     | ["tech:java\\nEnterprise Edition"]
        """)
    void shouldFilterTagsByCategory(List<String> tags, String category, List<String> filtered) {
        assertEquals(withNewlinesRestored(filtered), tagFilter.filterTags(withNewlinesRestored(tags), category, null));
    }

    @TableTest("""
        Scenario                                         | Tags                                                           | Category | Optional       | Filtered?
        Optional adds an extra prefix on top of category | ["tech:java", "urgent:ticket1", "biz:sales"]                  | tech     | {urgent}       | ["tech:java", "urgent:ticket1"]
        Optional adds several extra prefixes             | ["biz:sales", "tech:java", "urgent:ticket1", "random:stuff"]  | business | {tech, urgent} | ["biz:sales", "tech:java", "urgent:ticket1"]
        Null optional applies no additional filter       | ["tech:java", "biz:sales"]                                    | tech     |                | ["tech:java"]
        Empty optional applies no additional filter      | ["tech:java", "biz:sales"]                                    | tech     | {}             | ["tech:java"]
        Optional prefix works with a custom category     | ["urgent:ticket1", "biz:sales", "random:stuff"]               | urgent   | {biz}          | ["urgent:ticket1", "biz:sales"]
        """)
    void shouldApplyOptionalCategoryFilter(List<String> tags, String category, Set<String> optional, List<String> filtered) {
        assertEquals(filtered, tagFilter.filterTags(tags, category, optional));
    }

    private static List<String> withNewlinesRestored(List<String> values) {
        return values.stream().map(v -> v.replace("\\n", "\n")).toList();
    }
}
