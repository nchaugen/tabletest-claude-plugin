package org.example;

import org.junit.jupiter.api.DisplayName;
import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TagFilterTest {

    private final TagFilter filter = new TagFilterImpl();

    @DisplayName("Keeps tags matching the category's prefix rule")
    @Description("""
        No optional categories are supplied here (optional is always null) -- additive
        optional-category behavior is covered in a separate table. Tech and business map to
        fixed prefixes (tech:/dev:, biz:); any other category maps to its own name plus ':'.
        """)
    @TableTest("""
        Scenario                                       | Tags                                              | Category | Filtered?
        Tech category with tech, dev, biz and unprefixed tags | ["tech:java", "dev:tools", "biz:sales", random]   | tech     | ["tech:java", "dev:tools"]
        Business category with tech, biz and dev tags        | ["tech:java", "biz:sales", "dev:tools"]           | business | ["biz:sales"]
        Custom category with matching and non-matching tags  | ["urgent:high", "tech:java", "urgent:low"]        | urgent   | ["urgent:high", "urgent:low"]
        Null category with mixed tag formats                  | ["tech:java", random, "biz:sales"]                |          | ["tech:java", random, "biz:sales"]
        Empty tags list                                        | []                                                 | tech     | []
        """)
    void keepsTagsMatchingCategoryPrefix(List<String> tags, String category, List<String> filtered) {
        assertEquals(filtered, filter.filterTags(tags, category, null));
    }

    @DisplayName("Also keeps tags whose category prefix is in the optional set")
    @Description("""
        Tags are fixed at ["tech:java", "biz:sales", "urgent:high"] and category at "tech" across
        every row; only the optional set varies. Assumes an optional entry is a bare category name,
        matched by prefixing it with ':', the same rule an arbitrary primary category follows.
        """)
    @TableTest("""
        Scenario                              | Optional      | Filtered?
        No optional set provided               |               | ["tech:java"]
        Empty optional set provided            | {}            | ["tech:java"]
        Optional set with one extra category   | {urgent}      | ["tech:java", "urgent:high"]
        Optional set with two extra categories | {biz, urgent} | ["tech:java", "biz:sales", "urgent:high"]
        """)
    void alsoKeepsTagsFromOptionalCategories(Set<String> optional, List<String> filtered) {
        List<String> tags = List.of("tech:java", "biz:sales", "urgent:high");
        assertEquals(filtered, filter.filterTags(tags, "tech", optional));
    }

    @DisplayName("Drops empty tag strings regardless of category")
    @TableTest("""
        Scenario                                              | Tags                          | Category                 | Filtered?
        Only an empty tag, category prefix varies              | ['']                          | {tech, business, urgent} | []
        Empty tag alongside tags matching the category prefix  | ['', "tech:java", "dev:tools"] | tech                     | ["tech:java", "dev:tools"]
        Empty tag present while category is null                | ['', "tech:java"]              |                          | ["tech:java"]
        """)
    void dropsEmptyTagRegardlessOfCategory(List<String> tags, String category, List<String> filtered) {
        assertEquals(filtered, filter.filterTags(tags, category, null));
    }

    @DisplayName("Preserves special characters in kept tag content")
    @Description("""
        Category is held at "tech" so the special-content tag always matches the prefix rule; a
        plain decoy tag confirms unrelated tags are still dropped. Newlines are written as literal
        \\n in the table and converted to real newlines before comparison.
        """)
    @TableTest("""
        Scenario                                | Tags                                       | Filtered?
        Tag containing pipe and bracket characters | ["tech:java|core[main]", random]           | ["tech:java|core[main]"]
        Tag containing a newline character         | ["tech:java\\nEnterprise Edition", random] | ["tech:java\\nEnterprise Edition"]
        """)
    void preservesSpecialCharactersInKeptTags(List<String> tags, List<String> filtered) {
        List<String> processedTags = tags.stream().map(t -> t.replace("\\n", "\n")).toList();
        List<String> expected = filtered.stream().map(t -> t.replace("\\n", "\n")).toList();
        assertEquals(expected, filter.filterTags(processedTags, "tech", null));
    }
}
