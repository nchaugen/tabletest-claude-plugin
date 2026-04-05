Working through the analysis:

**Concern**: "Check action permission by role" — one concern, one table.

**Variations and value sets**:
- ADMIN → any action is irrelevant: value set `{READ, WRITE, DELETE}` → `true`
- USER → READ and WRITE both allowed, DELETE denied
- GUEST → only READ allowed, WRITE and DELETE both denied → value set `{WRITE, DELETE}` → `false`

Here's the TableTest:

```java
import org.junit.jupiter.api.DisplayName;
import org.tabletest.junit.TableTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PermissionCheckTest {

    @DisplayName("Check action permission by role")
    @TableTest("""
        Scenario                     | Role  | Action                | Allowed?
        Admin can do anything        | ADMIN | {READ, WRITE, DELETE} | true
        User can read and write      | USER  | {READ, WRITE}         | true
        User cannot delete           | USER  | DELETE                | false
        Guest can only read          | GUEST | READ                  | true
        Guest cannot write or delete | GUEST | {WRITE, DELETE}       | false
        """)
    void checkPermission(Role role, Action action, boolean allowed) {
        assertEquals(allowed, canPerform(role, action));
    }
}
```

Key design decisions:
- **ADMIN row uses a value set** `{READ, WRITE, DELETE}` — this explicitly asserts "any action is allowed", rather than silently omitting the column or listing three separate rows. The value set makes the "regardless of action" assertion visible.
- **USER and GUEST denied cases** group same-outcome actions into value sets for the same reason.
- JUnit converts `Role`, `Action` (enums), and `boolean` automatically — no `@TypeConverter` needed.
- 5 rows become 9 test invocations (3 from ADMIN's value set + 2 from USER's + 1 + 2 from GUEST's).