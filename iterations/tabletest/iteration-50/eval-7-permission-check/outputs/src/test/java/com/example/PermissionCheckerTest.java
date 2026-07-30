package com.example;

import org.tabletest.junit.TableTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PermissionCheckerTest {

    private final PermissionChecker checker = new PermissionChecker();

    @TableTest("""
        Scenario                 | Role  | Action                 | Allowed?
        Admin, any action        | ADMIN | {READ, WRITE, DELETE}  | true
        User, read or write      | USER  | {READ, WRITE}          | true
        User, delete             | USER  | DELETE                 | false
        Guest, read              | GUEST | READ                   | true
        Guest, write or delete   | GUEST | {WRITE, DELETE}        | false
        """)
    void decidesWhetherARoleCanPerformAnAction(Role role, Action action, boolean allowed) {
        assertEquals(allowed, checker.canPerform(role, action));
    }
}
