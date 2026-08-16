package com.example;

import org.junit.jupiter.api.DisplayName;
import org.tabletest.junit.TableTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PermissionCheckerTest {

    private final PermissionChecker checker = new PermissionChecker();

    @DisplayName("A role reaches the actions its rule names, and stops there")
    @TableTest("""
        Scenario                  | Role  | Action                | Allowed?
        Admin performs any action | ADMIN | {READ, WRITE, DELETE} | true
        User reads or writes      | USER  | {READ, WRITE}         | true
        User deletes              | USER  | DELETE                | false
        Guest reads               | GUEST | READ                  | true
        Guest writes or deletes   | GUEST | {WRITE, DELETE}       | false
        """)
    void allowsOnlyTheActionsTheRoleNames(Role role, Action action, boolean allowed) {
        assertEquals(allowed, checker.canPerform(role, action));
    }
}
