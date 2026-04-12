No build file exists, so I'll write a self-contained test. The `canPerform` rules map cleanly to a single table — role × action with a boolean result:

```java
import org.tabletest.junit.TableTest;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PermissionCheckerTest {

    @TableTest("""
        Scenario              | Role  | Action | Allowed?
        Admin reads           | ADMIN | READ   | true
        Admin writes          | ADMIN | WRITE  | true
        Admin deletes         | ADMIN | DELETE | true
        User reads            | USER  | READ   | true
        User writes           | USER  | WRITE  | true
        User cannot delete    | USER  | DELETE | false
        Guest reads           | GUEST | READ   | true
        Guest cannot write    | GUEST | WRITE  | false
        Guest cannot delete   | GUEST | DELETE | false
        """)
    void checkPermission(Role role, Action action, boolean allowed) {
        assertEquals(allowed, checker.canPerform(role, action));
    }
}
```

**Design notes:**

- `Role` and `Action` are enums — JUnit converts them by name automatically, no `@TypeConverter` needed.
- Each row covers one role/action combination with a direct boolean expectation. The pattern is clear: ADMIN rows are all `true`, USER rows split on DELETE, GUEST rows split on anything but READ.
- Value sets could compress ADMIN's rows into one (`ADMIN | {READ, WRITE, DELETE} | true`), but explicit rows are more readable here and make failures easier to diagnose.
- No `@Description` needed — the table is self-explanatory.