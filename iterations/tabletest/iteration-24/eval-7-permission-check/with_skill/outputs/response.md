This is a clean decision table — three roles × three actions with uniform boolean output. Value sets will avoid redundant rows where multiple actions produce the same result.

```java
import org.tabletest.junit.TableTest;
import static org.junit.jupiter.api.Assertions.assertEquals;

class PermissionCheckerTest {

    private final PermissionChecker checker = new PermissionChecker();

    @TableTest("""
        Scenario                     | Role  | Action                | Allowed?
        Admin can do anything        | ADMIN | {READ, WRITE, DELETE} | true
        User can read and write      | USER  | {READ, WRITE}         | true
        User cannot delete           | USER  | DELETE                | false
        Guest can read               | GUEST | READ                  | true
        Guest cannot write or delete | GUEST | {WRITE, DELETE}       | false
        """)
    void canPerformAction(Role role, Action action, boolean allowed) {
        assertEquals(allowed, checker.canPerform(role, action));
    }
}
```

Key design choices:

- **Value sets** (`{READ, WRITE, DELETE}`) on rows where all actions share the same result — this generates separate test runs per value while making the "regardless of" relationship explicit. The single `User cannot delete` row stays unset because DELETE is the only denied action for USER.
- **Enum conversion** — JUnit converts `Role` and `Action` enum values automatically from the string names, so no `@TypeConverter` needed.
- **5 rows generate 9 tests** (ADMIN×3 + USER×2 + USER×1 + GUEST×1 + GUEST×2).