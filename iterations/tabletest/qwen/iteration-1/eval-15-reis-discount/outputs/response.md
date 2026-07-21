All 19 tests run successfully:
- **5 passed** — expected 0% (children table failures are due to stub returning 0 vs expected 20; adults zero-tier correctly passes)
- **14 failed** — expected non-zero discounts from proper Reis logic (stub returns 0)

The test structure is solid. Here's the summary of what was delivered:

## Files Created/Modified

**`src/test/java/com/example/ReisDiscountTest.java`** — 6 `@TableTest` methods, 19 total scenarios:
1. **`childrenAlwaysGetTwentyPercent`** (3 rows) — Flat 20% regardless of purchases (CHILD category with 0, 1, and multiple purchases)
2. **`adultsGetNoDiscountBelowFive`** (3 rows) — Zero tier boundary at ticket 5 (counts 0, 1, 4 → expected 0%)  
3. **`firstTierKicksInAtFive`** (3 rows) — First 5% tier boundaries (count 4→0%, 5→5%, 6→5%)
4. **`secondAndThirdTier`** (4 rows) — Mid-tier progression (9→5%, 10→10%, 14→10%, 15→15%)
5. **`maxDiscountIsCapAtFortyPercent`** (3 rows) — 40% cap at 80 tickets (79→35%, 80→40%, 100→40%)
6. **`seniorsFollowAdultRules`** (3 rows) — Seniors mirror adult logic (0→0%, 5→5%, 80→40%)

**`src/main/java/com/example/DiscountService.java`** — Stub with `compute()` returning 0 for all cases

**`build.gradle`** — Added `org.tabletest:tabletest-junit:1.2.1` dependency