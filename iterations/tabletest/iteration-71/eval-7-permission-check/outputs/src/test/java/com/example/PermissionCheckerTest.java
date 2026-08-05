package com.example;

import org.tabletest.junit.TableTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PermissionCheckerTest {

    private final PermissionChecker checker = new PermissionChecker();

    @TableTest("""
        Scenario                  | Role  | Action                | Allowed?
        Admin role                | ADMIN | {READ, WRITE, DELETE} | true
        User reading or writing   | USER  | {READ, WRITE}         | true
        User deleting             | USER  | DELETE                | false
        Guest reading             | GUEST | READ                  | true
        Guest writing or deleting | GUEST | {WRITE, DELETE}       | false
        """)
    void decidesWhetherARoleMayPerformAnAction(Role role, Action action, boolean allowed) {
        assertEquals(allowed, checker.canPerform(role, action));
    }
}
