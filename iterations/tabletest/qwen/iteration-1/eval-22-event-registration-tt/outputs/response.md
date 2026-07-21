Done. Here's a summary of what was written:

**Files modified:**
- `build.gradle` — added `tabletest-junit:1.2.1` dependency
- `EventRegistrationService.java` — changed `int groupSize` → `Integer groupSize` (so null is valid for the stub)
- **New:** `EventRegistrationServiceTest.java` — 7 `@TableTest` methods, 19 test rows

**Tests decomposed by concern (one table per rule):**

| # | TableTest method | Concern | Rows |
|---|---|---|---|
| 1 | `rejectsWhenNameIsEmpty` | name validation — empty/blank | 2 |
| 2 | `rejectsWhenNameIsNull` | name validation — null missing | 1 |
| 3 | `rejectsWhenEmailFormatIsInvalid` | email format — invalid patterns | 4 |
| 4 | `acceptsValidRegistration` | baseline acceptance with null optional fields | 1 |
| 5 | `earlyBirdGetsTwentyPercentOff` | 20% discount before cutoff (Jan, day-before-Mar-1) | 2 |
| 6 | `noEarlyBirdOnOrAfterCutoff` | no early-bird on/after 2025-03-01 | 2 |
| 7 | `groupOfFiveGetsFifteenPercentOff` | 15% discount at/below/above threshold (4, 5, 10) | 3 |
| 8 | `noGroupDiscountForSmallerGroups` | no discount for 1 or null group size | 2 |
| 9 | `higherDiscountWinsWhenBothApply` | precedence: 20% early-bird beats 15% group (doesn't stack) | 2 |

All 19 tests compile, discover, and execute — each fails at `UnsupportedOperationException` as expected from the stub. To implement the logic, make all assertions pass.