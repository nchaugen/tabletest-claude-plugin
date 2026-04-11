Here's the TableTest:

```java
import org.tabletest.junit.TableTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PermissionCheckTest {

    @TableTest("""
            Scenario                | Role  | Action                | Allowed?
            Admin can perform any   | ADMIN | {READ, WRITE, DELETE} | true
            User can read           | USER  | READ                  | true
            User can write          | USER  | WRITE                 | true
            User cannot delete      | USER  | DELETE                | false
            Guest can read          | GUEST | READ                  | true
            Guest cannot write      | GUEST | WRITE                 | false
            Guest cannot delete     | GUEST | DELETE                | false
            """)
    void permissionsByRoleAndAction(Role role, Action action, boolean allowed) {
        assertEquals(allowed, permissionService.canPerform(role, action));
    }
}
```

**Key decisions:**

- **Value set for ADMIN**: `{READ, WRITE, DELETE}` collapses 3 rows into 1. This is valid because every action yields `true` for ADMIN — the result is identical regardless of which value is chosen.
- **No `@Description` needed**: the table is fully self-explanatory; the rules are visible directly from the scenario names and `Allowed?` column.
- **Enum conversion is built-in**: `Role` and `Action` enum values are converted automatically by JUnit — no `@TypeConverter` required.
- **7 rows cover all 9 combinations**: 3 ADMIN rows via value set + 2 USER + 2 GUEST.