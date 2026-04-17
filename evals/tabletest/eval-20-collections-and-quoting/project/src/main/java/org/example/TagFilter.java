package org.example;

import java.util.List;
import java.util.Set;

public interface TagFilter {
    List<String> filterTags(List<String> tags, String category, Set<String> optional);
}
