# Large Tables: Comments, Grouping, and External Files

## Comments and Blank Lines

Use comment lines starting with `//` to document sections. Use blank lines to group related cases visually.

```java
@TableTest("""
    Scenario        | Input | Output?

    // Basic cases
    Zero            | 0     | 0
    Positive        | 5     | 25

    // Edge cases
    Negative        | -3    | 9
    """)
void squaresItsInput(int input, int output) {
    assertEquals(output, input * input);
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
