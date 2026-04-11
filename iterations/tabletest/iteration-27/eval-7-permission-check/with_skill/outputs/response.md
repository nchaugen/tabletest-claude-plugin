Working through the analysis:

**Concern:** Check whether a role can perform an action — single concern, one table.

**Variations with value sets:**
- ADMIN → action is irrelevant, always `true` → value set `{READ, WRITE, DELETE}`
- USER → READ/WRITE allowed (value set), DELETE not
- GUEST → only READ allowed, WRITE/DELETE not (value set)

`Role` and `Action` are enums — JUnit converts them automatically. No custom type converters needed.

```java
import org.junit.jupiter.api.DisplayName;
import org.tabletest.junit.TableTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PermissionCheckTest {

    private final PermissionService permissions = new PermissionService();

    @DisplayName("Role permission check")
    @TableTest("""
            Scenario                     | Role  | Action                | Allowed?
            ADMIN can do anything        | ADMIN | {READ, WRITE, DELETE} | true
            USER can read and write      | USER  | {READ, WRITE}         | true
            USER cannot delete           | USER  | DELETE                | false
            GUEST can only read          | GUEST | READ                  | true
            GUEST cannot write or delete | GUEST | {WRITE, DELETE}       | false
            """)
    void checkPermission(Role role, Action action, boolean allowed) {
        assertEquals(allowed, permissions.canPerform(role, action));
    }
}
```

Key design choices:
- **ADMIN row uses `{READ, WRITE, DELETE}`** — explicitly asserts "regardless of action", rather than three separate rows that merely happen to show the same result
- **USER and GUEST non-allowed actions use value sets** — `{WRITE, DELETE}` and `{READ, WRITE}` group same-outcome values, making the tier structure visible
- No `@Description` needed — the table is self-explanatory

Replace `PermissionService` with whatever class holds `canPerform`.