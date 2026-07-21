All 9 tests ran and failed with `UnsupportedOperationException` — exactly expected since `PermissionChecker.canPerform()` is still a stub. This confirms:

- Both `@TableTest` methods discovered (6 rows in positive, 3 in negative)
- Each row reported as an individual test case (`[1]` through `[9]`)
- `tabletest-junit` integration works correctly

Here's a summary of what was delivered:

### `build.gradle`
Added the required dependency:
```groovy
testImplementation 'org.tabletest:tabletest-junit:1.2.1'
```

### `PermissionCheckerTest.java` — 2 decomposed tables

**Table 1: `shouldGrantPermission`** — positive permissions (6 rows)
| Scenario | Role | Action | Expected? |
|---|---|---|---|
| ADMIN reads/writes/deletes a resource | ADMIN | READ, WRITE, DELETE | true |
| USER reads/writes a resource | USER | READ, WRITE | true |
| GUEST reads a resource | GUEST | READ | true |

**Table 2: `shouldDenyPermission`** — denials (3 rows)
| Scenario | Role | Action | Expected? |
|---|---|---|---|
| USER deletes / GUEST writes or deletes | USER, GUEST | DELETE, WRITE | false |

This decomposition separates **which actions are allowed** from **which are denied**, avoiding mixed concerns in a single table. Both tables use `assertEquals(expected, checker.canPerform(role, action))` — uniform assertions across all rows. The stub implementation remains untouched so the tests fail until the permission logic is implemented.