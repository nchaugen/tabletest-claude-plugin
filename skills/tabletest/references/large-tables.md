# Large Tables: Comments, Grouping, and External Files

## Comments and Blank Lines

Use comment lines starting with `//` to document sections. Use blank lines to group related cases visually.

```java
@TableTest("""
    Scenario                   | Weight (kg) | Dose (mg)?

    // Middle band boundary
    Just below the middle band | 39          | 100
    At the middle band         | 40          | 250

    // High band boundary
    Just below the high band   | 69          | 250
    At the high band           | 70          | 500
    """)
void setsDoseFromWeight(int weightKg, int doseMg) {
    assertEquals(doseMg, dosePolicy.doseFor(weightKg));
}
```

## External Table Files

Load large tables from resource files to keep test classes readable:

```java
@TableTest(resource = "/test-data/donor-deferral.table")
void defersDonorByComponentAndInterval(String component, int daysSinceLast, boolean deferred) {
    assertEquals(deferred, deferralPolicy.isDeferred(component, daysSinceLast));
}
```

The table file uses the same syntax as inline tables:

```
Scenario                  | Component   | Days Since Last | Deferred?
Inside whole-blood window | whole blood | 30              | true
Past whole-blood window   | whole blood | 60              | false
...
```

Place table files in `src/test/resources/`.
