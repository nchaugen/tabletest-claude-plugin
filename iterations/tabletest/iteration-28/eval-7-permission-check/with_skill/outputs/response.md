The rules are explicit so I can go straight to table design. The key design choice is using value sets to express "ADMIN can perform any action" and "GUEST cannot do anything but READ" — each value set asserts the result is identical regardless of which value is chosen.

```java
@TableTest("""
    Scenario               | Role  | Action                | Allowed?
    Admin, any action      | ADMIN | {READ, WRITE, DELETE} | true
    User, read or write    | USER  | {READ, WRITE}         | true
    User, delete           | USER  | DELETE                | false
    Guest, read            | GUEST | READ                  | true
    Guest, write or delete | GUEST | {WRITE, DELETE}       | false
    """)
void canPerform(Role role, Action action, boolean allowed) {
    assertEquals(allowed, permissionChecker.canPerform(role, action));
}
```

**Design notes:**

- `Role` and `Action` are enums — JUnit's built-in conversion handles them with no `@TypeConverter` needed.
- The 5 rows expand to 9 test cases via value sets (`{READ, WRITE, DELETE}` → 3, `{READ, WRITE}` → 2, `{WRITE, DELETE}` → 2).
- Value set semantics are satisfied: every value in each set produces the same `Allowed?` result.
- No `@Description` needed — the table fully expresses the rules.
- Scenario names describe the condition ("GUEST, write or delete"), not the outcome ("returns false").