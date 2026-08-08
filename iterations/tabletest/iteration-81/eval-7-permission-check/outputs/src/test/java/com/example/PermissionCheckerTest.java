package com.example;

import org.tabletest.junit.TableTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PermissionCheckerTest {

    private final PermissionChecker checker = new PermissionChecker();

    @TableTest("""
        Scenario                  | Role  | Action                | Can Perform?
        Admin performs any action | ADMIN | {READ, WRITE, DELETE} | true
        User reads or writes      | USER  | {READ, WRITE}         | true
        User deletes              | USER  | DELETE                | false
        Guest reads               | GUEST | READ                  | true
        Guest writes or deletes   | GUEST | {WRITE, DELETE}       | false
        """)
    void decidesWhetherARoleCanPerformAnAction(Role role, Action action, boolean canPerform) {
        assertEquals(canPerform, checker.canPerform(role, action));
    }
}
