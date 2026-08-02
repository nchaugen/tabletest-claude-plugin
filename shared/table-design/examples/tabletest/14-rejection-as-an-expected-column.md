One rule — the whole table asks whether the dose is accepted:
```
Scenario                | Dose (mg) | Throws?
At the minimum dose     | 0         |
Just below the minimum  | -0.01     | java.lang.IllegalArgumentException
```

Two concerns — parsing returns values, rejection is its own table:
```
Scenario          | Input      | Parsed?
ISO date          | 2026-07-30 | 2026-07-30
Short year        | 30/07/26   | 2026-07-30
```
