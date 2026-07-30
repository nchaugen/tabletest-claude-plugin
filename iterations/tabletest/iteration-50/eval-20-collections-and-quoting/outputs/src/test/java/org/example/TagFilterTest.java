package org.example;

import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TagFilterTest {

    private final TagFilter tagFilter = new TagFilterImpl();

    @TableTest("""
        Scenario                          | Tags                                               | Category | Filtered?
        Tech category keeps tech and dev  | ["tech:java", "dev:backend", "biz:sales"]          | tech     | ["tech:java", "dev:backend"]
        Business category keeps biz only  | ["tech:java", "biz:sales", "urgent:review"]        | business | ["biz:sales"]
        Custom category keeps its prefix  | ["urgent:review", "tech:java"]                     | urgent   | ["urgent:review"]
        Prefix must be followed by colon  | ["tech:java", "technology:x", "techdebt:y"]        | tech     | ["tech:java"]
        """)
    void filtersTagsByCategoryPrefix(List<String> tags, String category, List<String> filtered) {
        assertEquals(filtered, tagFilter.filterTags(tags, category, null));
    }

    @Description("""
        Category is held at "tech" for every row; filtersTagsByCategoryPrefix covers how the
        category value selects the base prefix set. "marketing:promo" is never in the optional
        set, so it stays excluded throughout - the optional set only adds categories, it does
        not replace the category rule.
        """)
    @TableTest("""
        Scenario                          | Optional      | Filtered?
        No optional set (null)            |               | ["tech:java", "dev:backend"]
        Empty optional set                | {}            | ["tech:java", "dev:backend"]
        Optional adds one category        | {biz}         | ["tech:java", "dev:backend", "biz:sales"]
        Optional adds multiple categories | {biz, urgent} | ["tech:java", "dev:backend", "biz:sales", "urgent:review"]
        """)
    void appliesOptionalCategoryFilter(Set<String> optional, List<String> filtered) {
        List<String> tags = List.of("tech:java", "dev:backend", "biz:sales", "urgent:review", "marketing:promo");
        assertEquals(filtered, tagFilter.filterTags(tags, "tech", optional));
    }

    @Description("""
        Category is null for every row in this table.
        """)
    @TableTest("""
        Scenario                                          | Tags                                | Filtered?
        Passes through tags with any prefix unfiltered    | ["tech:java", "biz:sales", "urgent:review"] | ["tech:java", "biz:sales", "urgent:review"]
        Passes through tags without any prefix            | [random, notes]                     | [random, notes]
        Drops an empty tag string even when category is null | ['', "tech:java", random]        | ["tech:java", random]
        """)
    void passesTagsThroughWhenCategoryIsNull(List<String> tags, List<String> filtered) {
        assertEquals(filtered, tagFilter.filterTags(tags, null, null));
    }

    @TableTest("""
        Scenario                                        | Tags | Category                 | Filtered?
        Empty list input returns empty list             | []   | tech                     | []
        Empty tag string alone is never kept, any category | [''] | {tech, business, urgent} | []
        """)
    void returnsEmptyListForDegenerateInput(List<String> tags, String category, List<String> filtered) {
        assertEquals(filtered, tagFilter.filterTags(tags, category, null));
    }

    @TableTest("""
        Scenario                              | Tags                                              | Category | Filtered?
        Tag containing a pipe is preserved     | ["tech:milestone|v2", "biz:sales"]                | tech     | ["tech:milestone|v2"]
        Tag containing brackets is preserved   | ["tech:[urgent]", "biz:sales"]                    | tech     | ["tech:[urgent]"]
        Tag containing a newline is preserved  | ["tech:java\\nEnterprise Edition", "biz:sales"]   | tech     | ["tech:java\\nEnterprise Edition"]
        """)
    void preservesSpecialCharactersInKeptTags(List<String> tags, String category, List<String> filtered) {
        assertEquals(withRealNewlines(filtered), tagFilter.filterTags(withRealNewlines(tags), category, null));
    }

    private static List<String> withRealNewlines(List<String> values) {
        return values.stream().map(value -> value.replace("\\n", "\n")).toList();
    }
}
