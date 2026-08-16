package org.example;

import org.junit.jupiter.api.DisplayName;
import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TagFilterTest {

    private final TagFilter filter = new StubTagFilter();

    @DisplayName("Category keeps the tags carrying its prefix")
    @Description("""
        No optional set on any row here — which categories one adds is the next table's subject.
        A prefix is the text before a colon, so a category matches a tag only up to that colon:
        'tech' does not reach 'technology:node', and the two named categories keep the prefixes
        the requirement gives them rather than their own names.
        """)
    @TableTest("""
        Scenario                                   | Category | Tags                                    | Kept?
        Tech keeps both the prefixes it names      | tech     | ["tech:java", "dev:ci", "biz:sales"]    | ["tech:java", "dev:ci"]
        A longer name sharing the first letters    | tech     | ["tech:java", "technology:node"]        | ["tech:java"]
        Business keeps biz, not its own name       | business | ["biz:sales", "business:plan"]          | ["biz:sales"]
        Any other category keeps its own name      | sports   | ["sports:football", "biz:sales"]        | ["sports:football"]
        No category at all keeps every tag         |          | ["tech:java", "biz:sales"]              | ["tech:java", "biz:sales"]
        A category name with no colon after it     | tech     | ["dev", "dev:ci"]                       | ["dev:ci"]
        A colon with no category before it         | tech     | [":java", "tech:java"]                  | ["tech:java"]
        """)
    void keepsTheTagsCarryingTheCategoryPrefix(String category, List<String> tags, List<String> kept) {
        assertEquals(kept, filter.filterTags(tags, category, null));
    }

    @DisplayName("An optional set adds categories to the one asked for")
    @Description("""
        The same three tags on every row, category held at 'tech', so the optional set is the only
        thing that moves. An absent set and an empty set are written differently — a blank cell is
        null, {} is an empty Set — and the requirement gives them the same effect.
        """)
    @TableTest("""
        Scenario                              | Tags                                          | Optional      | Kept?
        No optional set given                 | ["tech:java", "biz:sales", "sports:football"] |               | ["tech:java"]
        An empty optional set adds nothing    | ["tech:java", "biz:sales", "sports:football"] | {}            | ["tech:java"]
        One optional category is added        | ["tech:java", "biz:sales", "sports:football"] | {biz}         | ["tech:java", "biz:sales"]
        Both optional categories are added    | ["tech:java", "biz:sales", "sports:football"] | {biz, sports} | ["tech:java", "biz:sales", "sports:football"]
        An optional category no tag carries   | ["tech:java", "biz:sales", "sports:football"] | {music}       | ["tech:java"]
        """)
    void addsTheOptionalCategoriesToTheOneAskedFor(
            List<String> tags, Set<String> optional, List<String> kept) {
        assertEquals(kept, filter.filterTags(tags, "tech", optional));
    }

    @DisplayName("Tag text survives the punctuation the table itself reserves")
    @Description("""
        A tag routinely carries the characters this table's own syntax reserves, so each is quoted
        inside the list and reaches the filter unchanged. A newline is written \\n and turned into a
        real one on both the input and the expectation before the call, since a line break written
        into a row would end the row. The last row settles a collision the requirement leaves open:
        an empty tag is never kept beats returning every tag when no category is given.
        """)
    @TableTest("""
        Scenario                                | Category | Tags                                | Kept?
        An empty list of tags stays empty       | tech     | []                                  | []
        A pipe inside a tag                     | tech     | ["tech:a|b"]                        | ["tech:a|b"]
        Brackets inside a tag                   | tech     | ["tech:array[]"]                    | ["tech:array[]"]
        A newline inside a tag                  | tech     | ["tech:java\\nEnterprise Edition"]  | ["tech:java\\nEnterprise Edition"]
        An empty tag is never kept              | tech     | ["tech:java", '']                   | ["tech:java"]
        An empty tag goes even with no category |          | ["tech:java", '']                   | ["tech:java"]
        """)
    void keepsTagTextExactlyAsGiven(String category, List<String> tags, List<String> kept) {
        assertEquals(withRealNewlines(kept), filter.filterTags(withRealNewlines(tags), category, null));
    }

    private static List<String> withRealNewlines(List<String> values) {
        return values.stream().map(value -> value.replace("\\n", "\n")).toList();
    }
}

/** Stands in for the unwritten filter so the tests compile; it filters nothing. */
class StubTagFilter implements TagFilter {

    @Override
    public List<String> filterTags(List<String> tags, String category, Set<String> optional) {
        return List.of();
    }
}
