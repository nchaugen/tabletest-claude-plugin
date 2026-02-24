# Testing Can Reveal Implementation Bugs

The process of designing test scenarios can reveal bugs in the implementation. If test rows feel awkward, impossible to express, or inconsistent with black-box expectations, investigate the implementation.

## Real Example: Suppressed Exceptions Bug

**Scenario:** Writing test for router that falls back to secondary system when primary fails.

**Test design challenge:**
```java
// Trying to write test for "both routes fail with fallback"
@TableTest("""
    Scenario          | Primary | Secondary | Fallback? | Response?
    Primary fails     | ERROR   | OK        | true      | OK
    Both fail         | ERROR   | ERROR     | true      | ERROR
    """)
```

Expected behavior: When both fail, primary exception should have secondary exception as suppressed (observable via `thrown.getSuppressed()`).

**Problem discovered:** Test couldn't be written cleanly because suppressed exception behavior varied based on `reportEvents` flag:
- `reportEvents=true` -> suppressed exception added
- `reportEvents=false` -> suppressed exception NOT added

**Investigation revealed the bug:**
```java
// MdcRouter.java:100 - WRONG
} catch (Throwable suppressed) {
    if (routerContext.reportEvents()) {
        reporter.report(...);
        t.addSuppressed(suppressed);  // Inside if block!
    }
    throw t;
}
```

**The bug:** `addSuppressed()` was inside the `if (reportEvents)` block, but these are orthogonal concerns:
- **Exception chaining** (`addSuppressed`) - preserves error context for the **caller**
- **Reporting** - sends telemetry to monitoring systems

A caller catching the exception should **always** see both failures when fallback fails, regardless of whether we're sending reports.

**Fix:**
```java
// Move addSuppressed outside the if block
} catch (Throwable suppressed) {
    if (routerContext.reportEvents()) {
        reporter.report(...);
    }
    t.addSuppressed(suppressed);  // ALWAYS add
    throw t;
}
```

## TDD Approach

1. **Red:** Write failing test expecting suppressed exception
   ```java
   @TableTest("""
       Scenario          | Response?
       Both fail         | ERROR+1    // Expects 1 suppressed exception
       """)
   ```
   Test fails: expected 1 suppressed, got 0

2. **Green:** Fix implementation to always add suppressed exceptions

3. **Refactor:** Update all related tests to reflect corrected behavior

## Lessons

**When test design feels wrong:**
- Can't write clean test rows -> investigate implementation
- Expectations vary based on unrelated flags -> likely bug (orthogonal concerns mixed)
- Observable behavior inconsistent with black-box expectations -> check implementation logic

**Don't blame the test:**
If you find yourself thinking "the test is too hard to write," first investigate whether the implementation has a bug. Good test design often exposes coupling or logic errors.

**TableTest advantage:**
Writing comprehensive scenarios in table format makes inconsistencies obvious. When you can't express expected behavior cleanly in a table, that's a signal to investigate the code, not compromise the test.
