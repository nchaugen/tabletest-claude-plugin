package com.example;

import org.tabletest.junit.TableTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PermissionCheckerTest {

    private final PermissionChecker checker = new PermissionChecker();

    @TableTest("""
        Scenario                       | Role  | Action                   | Allowed?
        Admin can perform any action   | ADMIN | {READ, WRITE, DELETE}    | true
        User can read and write        | USER  | {READ, WRITE}            | true
        User cannot delete             | USER  | DELETE                   | false
        Guest can only read            | GUEST | READ                     | true
        Guest cannot write or delete   | GUEST | {WRITE, DELETE}          | false
        """)
    void determinesWhetherARoleCanPerformAnAction(Role role, Action action, boolean allowed) {
        assertEquals(allowed, checker.canPerform(role, action));
    }
}
