package org.example;

import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TagFilterTest {

    private final TagFilter tagFilter = new TagFilterImpl();

    @Description("""
        "tech" and "business" are the two named categories with special prefix
        sets; any other category value is matched against its own name as the
        prefix. Optional is absent (null) throughout, so it has no effect.
        """)
    @TableTest("""
        Scenario                                    | Tags                                                              | Category | Filtered?
        Tech category keeps tech and dev prefixes   | ["tech:java", "dev:backend", "biz:sales", "ops:widget"]         | tech     | ["tech:java", "dev:backend"]
        Business category keeps biz prefix only     | ["tech:java", "biz:sales", "dev:backend"]                        | business | ["biz:sales"]
        Custom category keeps its own prefix only   | ["urgent:fix", "tech:java", "urgent:review"]                     | urgent   | ["urgent:fix", "urgent:review"]
        Empty tag list yields an empty result        | []                                                                | tech     | []
        """)
    void selectsTagsByCategoryPrefix(List<String> tags, String category, List<String> filtered) {
        assertEquals(filtered, tagFilter.filterTags(tags, category, null));
    }

    @TableTest("""
        Scenario                                     | Tags                                             | Category | Optional         | Filtered?
        Absent optional set adds no extra category   | ["tech:java", "urgent:fix"]                     | tech     |                  | ["tech:java"]
        Empty optional set adds no extra category    | ["tech:java", "urgent:fix"]                     | tech     | {}               | ["tech:java"]
        Optional set adds one extra category         | ["tech:java", "urgent:fix", "biz:sales"]        | tech     | {urgent}         | ["tech:java", "urgent:fix"]
        Optional set adds several extra categories   | ["tech:java", "urgent:fix", "biz:sales"]        | tech     | {urgent, biz}    | ["tech:java", "urgent:fix", "biz:sales"]
        """)
    void appliesOptionalAdditionalCategories(List<String> tags, String category, Set<String> optional, List<String> filtered) {
        assertEquals(filtered, tagFilter.filterTags(tags, category, optional));
    }

    @TableTest("""
        Scenario                                    | Tags                                          | Optional | Filtered?
        Null category returns every tag unfiltered  | ["tech:java", "random", "biz:sales"]         |          | ["tech:java", "random", "biz:sales"]
        Null category ignores the optional set too  | ["tech:java", "random", "biz:sales"]         | {urgent} | ["tech:java", "random", "biz:sales"]
        """)
    void returnsEveryTagWhenCategoryIsNull(List<String> tags, Set<String> optional, List<String> filtered) {
        assertEquals(filtered, tagFilter.filterTags(tags, null, optional));
    }

    @TableTest("""
        Scenario                                       | Tags                        | Category | Filtered?
        Drops an empty tag under a matching category   | ['', "tech:java"]          | tech     | ["tech:java"]
        Drops an empty tag when category is null       | ['', "tech:java"]          |          | ["tech:java"]
        """)
    void dropsEmptyTagStringsRegardlessOfCategory(List<String> tags, String category, List<String> filtered) {
        assertEquals(filtered, tagFilter.filterTags(tags, category, null));
    }

    @TableTest("""
        Scenario                               | Tags                                            | Category | Filtered?
        Pipe and bracket characters kept as-is | ["tech:a|b", "tech:[x]", "biz:skip"]           | tech     | ["tech:a|b", "tech:[x]"]
        Newline within a tag is kept as-is     | ["tech:java\\nEnterprise Edition", "biz:skip"] | tech     | ["tech:java\\nEnterprise Edition"]
        """)
    void preservesSpecialCharactersInTagValues(List<String> tags, String category, List<String> filtered) {
        assertEquals(withRealNewlines(filtered), tagFilter.filterTags(withRealNewlines(tags), category, null));
    }

    private static List<String> withRealNewlines(List<String> values) {
        return values.stream().map(value -> value.replace("\\n", "\n")).toList();
    }
}
