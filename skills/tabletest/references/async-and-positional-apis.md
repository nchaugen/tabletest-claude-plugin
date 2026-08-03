# Off-Thread Work and Positional APIs

Two shapes in the code under test that a table cannot express directly. Both are visible in the
signature before you write anything: work handed to another thread, and output fields whose meaning
depends on configuration.

## Waiting for Async Work Before Asserting

The system finishes its async work after the assertions have already run, so the table fails
intermittently or passes for the wrong reason.

Gate the assertions on a `CountDownLatch` sized for the row. **Sizing it is arrangement, so it
belongs in a helper** — a `?:` in the method body is a rule the table cannot show, and this one is
not even about the behaviour under test.

```java
@TableTest("""
    Scenario           | Dual Dispatch | Master | Reports Sent?
    Single dispatch    | false         | OK     | 0
    Dual dispatch      | true          | OK     | 1
    Master failed      | true          | ERROR  | 0
    """)
void reportsEventsWhenDispatchedToBoth(boolean dualDispatch, String masterStatus, int reportsSent) {
    CountDownLatch asyncLatch = latchFor(dualDispatch, masterStatus);
    Executor asyncExecutor = task -> new Thread(() -> {
        task.run();
        asyncLatch.countDown();
    }).start();

    // ... execute system

    assertTrue(asyncLatch.await(5, TimeUnit.SECONDS));
    verify(reporter, times(reportsSent)).report(any());
}

// with the other helpers, at the bottom of the class — the secondary is skipped when the
// master fails without fallback, so waiting on a latch it will never count down times out
private static CountDownLatch latchFor(boolean dualDispatch, String masterStatus) {
    boolean asyncWillExecute = dualDispatch && !"ERROR".equals(masterStatus);
    return new CountDownLatch(asyncWillExecute ? 1 : 0);
}
```

**A count, not a flag.** `verify(reporter, times(reportsSent))` asserts every row with one call;
branching between `verifyNoInteractions` and `verify(times(1))` puts the rule back in the body.

**One concern still means one table.** This is a technique for waiting, not a licence to test routing
and reporting together — if you cannot name the behaviour without "and", it is two tables (main skill
file, *Decompose When You See These Signs*).

## Positional Output Fields

The system reports into positional fields — `masterResponse` and `otherResponse`, first and second —
and which system occupies which position depends on a configuration flag.

Keep fixed identities in the input columns, relative roles in the expectation columns, and let the
test map between them:

```java
@TableTest("""
    Scenario            | Primary Is Master | Primary | Secondary | Master Response? | Other Response?
    Primary is master   | true              | OK      | ERROR     | Primary OK       | Secondary ERROR
    Secondary is master | false             | OK      | ERROR     | Secondary OK     | Primary ERROR
    """)
void reportsEachSystemInItsConfiguredPosition(boolean primaryIsMaster, String primaryStatus,
                                              String secondaryStatus,
                                              ServiceResponse masterResponse,
                                              ServiceResponse otherResponse) {
    // ... execute system

    assertEquals(masterResponse, ServiceResponse.of(report.masterResponse(), report.masterError()));
    assertEquals(otherResponse, ServiceResponse.of(report.otherResponse(), report.otherError()));
}
```

The expectation cells hold identity and status as one value, which is the narrow exception under
*Name Expectation Columns Clearly* — both parts vary in the same position, so `Primary OK` is one
domain value with its own type rather than an encoding of two.

Without this, the input columns have to be renamed per row ("whichever system is master"), and the
scenario names stop saying which system is which.
