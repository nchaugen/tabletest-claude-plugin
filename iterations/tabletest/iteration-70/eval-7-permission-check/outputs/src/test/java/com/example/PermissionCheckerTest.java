package com.example;

import org.tabletest.junit.TableTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PermissionCheckerTest {

    @TableTest("""
        Scenario      | Role  | Action | Allowed?
        Admin reads   | ADMIN | READ   | true
        Admin writes  | ADMIN | WRITE  | true
        Admin deletes | ADMIN | DELETE | true
        User reads    | USER  | READ   | true
        User writes   | USER  | WRITE  | true
        User deletes  | USER  | DELETE | false
        Guest reads   | GUEST | READ   | true
        Guest writes  | GUEST | WRITE  | false
        Guest deletes | GUEST | DELETE | false
        """)
    void decidesWhetherARoleMayPerformAnAction(Role role, Action action, boolean allowed) {
        assertEquals(allowed, new PermissionChecker().canPerform(role, action));
    }
}
