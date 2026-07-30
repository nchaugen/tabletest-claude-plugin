package org.example;

import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TagFilterTest {

    private final TagFilter tagFilter = new DefaultTagFilter();

    @Description("""
        Optional is fixed to null throughout this table; the optional-category axis
        is covered separately by addsOptionalCategoryPrefixesToTheKeptTags.
        """)
    @TableTest("""
        Scenario                                | Category | Tags                                                                 | Filtered?
        Tech category keeps tech: and dev: tags  | tech     | ["tech:alpha", "dev:beta", "biz:gamma", "urgent:delta"]              | ["tech:alpha", "dev:beta"]
        Business category keeps only biz: tags   | business | ["biz:alpha", "business:beta", "tech:gamma"]                         | ["biz:alpha"]
        Custom category keeps its own prefix     | urgent   | ["urgent:alpha", "tech:beta", "biz:gamma"]                           | ["urgent:alpha"]
        Null category returns tags unfiltered    |          | ["tech:alpha", "biz:beta", "whatever"]                               | ["tech:alpha", "biz:beta", "whatever"]
        """)
    void keepsTagsMatchingTheCategoryPrefix(String category, List<String> tags, List<String> filtered) {
        assertEquals(filtered, tagFilter.filterTags(tags, category, null));
    }

    @TableTest("""
        Scenario                                   | Category | Optional      | Tags                                          | Filtered?
        Optional category adds its own prefix      | tech     | {urgent}      | ["tech:a", "urgent:b", "biz:c"]              | ["tech:a", "urgent:b"]
        Multiple optional categories are included  | business | {urgent, vip} | ["biz:a", "urgent:b", "vip:c", "tech:d"]     | ["biz:a", "urgent:b", "vip:c"]
        Null optional applies no extra filter      | tech     |               | ["tech:a", "urgent:b", "biz:c"]              | ["tech:a"]
        Empty optional applies no extra filter     | tech     | {}            | ["tech:a", "urgent:b", "biz:c"]              | ["tech:a"]
        """)
    void addsOptionalCategoryPrefixesToTheKeptTags(String category, Set<String> optional, List<String> tags, List<String> filtered) {
        assertEquals(filtered, tagFilter.filterTags(tags, category, optional));
    }

    @Description("""
        Optional is fixed to null throughout this table; it is not a factor in what
        counts as an empty result.
        """)
    @TableTest("""
        Scenario                                        | Category                 | Tags               | Filtered?
        Empty tag list, any category                    | {tech, business, urgent} | []                 | []
        List containing only an empty tag, any category | {tech, business, urgent} | ['']               | []
        Empty tag dropped alongside a valid tag          | tech                     | ['', "tech:a"]     | ["tech:a"]
        """)
    void returnsNoTagsWhenNothingQualifies(String category, List<String> tags, List<String> filtered) {
        assertEquals(filtered, tagFilter.filterTags(tags, category, null));
    }

    @Description("""
        Category is fixed to "tech" and optional to null throughout; this table is
        only about whether kept tag content survives unmodified. The escaped newline
        marker in a cell is unescaped to a real newline before the call, matching the
        literal newline described in the feature.
        """)
    @TableTest("""
        Scenario                       | Category | Tags                                          | Filtered?
        Preserves pipe characters      | tech     | ["tech:a|b", "biz:x"]                        | ["tech:a|b"]
        Preserves bracket characters   | tech     | ["tech:[x]", "biz:y"]                        | ["tech:[x]"]
        Preserves an embedded newline  | tech     | ["tech:java\\nEnterprise Edition", "biz:z"]  | ["tech:java\\nEnterprise Edition"]
        """)
    void preservesTagContentForKeptTags(String category, List<String> tags, List<String> filtered) {
        assertEquals(unescapeNewlines(filtered), tagFilter.filterTags(unescapeNewlines(tags), category, null));
    }

    private static List<String> unescapeNewlines(List<String> values) {
        return values.stream().map(value -> value.replace("\\n", "\n")).toList();
    }
}
