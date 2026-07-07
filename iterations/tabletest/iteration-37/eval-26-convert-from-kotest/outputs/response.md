Clean. The conversion is complete and verified.

**What changed:**
- Added `org.tabletest:tabletest-junit:1.2.1` to `build.gradle.kts` (JUnit Jupiter was already 6.0.3, well above the 5.11 minimum).
- Rewrote `ShippingCostCalculatorTest.kt`, replacing the single Kotest `withData`/`FunSpec` block (17 scenarios in one flat list, including a `ShippingScenario` helper class) with 7 `@TableTest` methods, each isolating one concern:
  1. Base rate by zone/speed/weight — plus a `{DHL, UPS, FEDEX}` value set showing carrier never affects cost (it's an unused parameter in the calculator)
  2. Actual vs. dimensional (volumetric) weight
  3. Oversize dimension surcharge
  4. Fragile multiplier surcharge
  5. Insurance premium with its minimum
  6. Hazmat handling fee
  7. Fragile + insurance combining cumulatively

Each table fixes irrelevant inputs (zone/weight/dimensions/carrier) via `@Description` instead of carrying them as columns, so every table reads as a focused rule. All 24 generated test cases pass (`gradle test`, 0 failures).

One thing worth flagging: the insurance table only exercises the "premium clamped to the 3.00 minimum" branch (insured value 500) — there's no case where the 0.6% rate actually exceeds the minimum. I noted this as an open gap in that table's `@Description` rather than inventing a new scenario, since I didn't want to expand test coverage without your say-so. Let me know if you'd like me to add that case.