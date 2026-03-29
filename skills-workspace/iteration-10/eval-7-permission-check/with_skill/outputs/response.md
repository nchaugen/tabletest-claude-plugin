No project files found, so I'll include the dependency info. The logic is straightforward — `Role` and `Action` are enums (JUnit converts them automatically), and the three roles map cleanly to value sets where the same result holds regardless of action.

```java
import org.junit.jupiter.api.Assertions.assertEquals;
import org.tabletest.junit.TableTest;

class PermissionCheckTest {

    @TableTest("""
        Scenario                      | Role  | Action                | Allowed?
        Admin can do anything         | ADMIN | {READ, WRITE, DELETE} | true
        User can read and write       | USER  | {READ, WRITE}         | true
        User cannot delete            | USER  | DELETE                | false
        Guest can only read           | GUEST | READ                  | true
        Guest cannot write or delete  | GUEST | {WRITE, DELETE}       | false
        """)
    void checkPermission(Role role, Action action, boolean allowed) {
        assertEquals(allowed, canPerform(role, action));
    }
}
```

**Design notes:**

- Value sets are used where every value in the set produces the same result — e.g. `{READ, WRITE, DELETE}` for ADMIN all yield `true`, so a value set is correct. The three GUEST/DELETE rows that differ are split into separate rows.
- `Role` and `Action` are enums — JUnit's built-in conversion handles them directly, no `@TypeConverter` needed.
- `boolean` (primitive) works fine here since there are no null/blank cases.

**Dependency** (add to `pom.xml` if not present):
```xml
<dependency>
    <groupId>org.tabletest</groupId>
    <artifactId>tabletest-junit</artifactId>
    <version>VERSION</version>
    <scope>test</scope>
</dependency>
```