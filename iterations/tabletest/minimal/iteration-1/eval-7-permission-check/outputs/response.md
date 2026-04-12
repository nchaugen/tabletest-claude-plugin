Here's the test class:

```java
import org.junit.jupiter.api.DisplayName;
import org.tabletest.junit.TableTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PermissionCheckTest {

    @DisplayName("Role permission check")
    @TableTest("""
            Scenario                       | Role  | Action                 | Allowed?
            ADMIN can perform any action   | ADMIN | {READ, WRITE, DELETE}  | true
            USER can READ                  | USER  | READ                   | true
            USER can WRITE                 | USER  | WRITE                  | true
            USER cannot DELETE             | USER  | DELETE                 | false
            GUEST can READ                 | GUEST | READ                   | true
            GUEST cannot WRITE or DELETE   | GUEST | {WRITE, DELETE}        | false
            """)
    void checkPermission(Role role, Action action, boolean allowed) {
        assertEquals(allowed, canPerform(role, action));
    }
}
```

**Key design decisions:**

- `{READ, WRITE, DELETE}` value set for ADMIN — asserts that the action is irrelevant, ADMIN is always allowed. This generates 3 tests from one row.
- `{WRITE, DELETE}` value set for GUEST's denied row — both are denied for the same reason (GUEST can only READ), so they belong together.
- USER's READ and WRITE are kept as separate rows rather than a value set — they're individually meaningful permission grants worth naming explicitly.
- `Role` and `Action` are enums; JUnit converts them by name automatically, no `@TypeConverter` needed.
- No `@Description` needed — the table is self-explanatory and there are no fixed values or open questions to add.