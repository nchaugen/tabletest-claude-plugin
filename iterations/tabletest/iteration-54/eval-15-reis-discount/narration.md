# Narration — 15

The agent's visible narration, thinking cost and file writes, in order, distilled from
`conversation.jsonl`. Thinking text appears when the run asked the CLI for a summary
(`thinking_display: "summarized"`); the per-block token counts appear either way.

_[~452 thinking tokens (6 deltas)]_

> (thinking) The user is asking for tests on discount calculation logic with multiple scenarios based on purchase history, which fits the table-driven test pattern perfectly for this Java Gradle project. This is a clear match for the tabletest skill since they want comprehensive test coverage with a discount ladder structure.

I'll first look at the existing project structure to understand what interfaces are already available, then use the TableTest skill since we're writing JUnit tests for this Java project before implementing the logic.

_[~153 thinking tokens (5 deltas)]_

> (thinking) I've got the domain model down, so now I should use the tabletest skill to write JUnit tests with TableTest before diving into implementation. Since the requirements around the discount ladder and decay algorithm are genuinely ambiguous, the tabletest skill's requirements decomposition workflow can help clarify things through concrete examples.

_[~18,780 thinking tokens (386 deltas)]_

> (thinking) I'm breaking down the implementation into separate concerns: tracking ticket counts within a 30-day window based on purchase history, mapping those counts to discount percentages, handling the flat CHILD discount, and determining ADULT/SENIOR eligibility independent of zone. I'll also need to add the tabletest-junit dependency and set up TaskCreate for tracking.
> 
> For the API shape, I'm looking at what the existing PurchaseHistoryRepository provides—a list of past purchases with timestamps, traveler categories, ticket types, and zone validity—and designing a DiscountCalculator that takes the traveler category, ticket type, purchase time, and full history to compute the final discount. Following the principle that tables should drive the API, I'll create a helper function that counts qualifying trips from the history within the trailing 30 days, which becomes a derived input for the discount calculation logic.
> 
> The core structure breaks into separate concerns: a function mapping trip counts to discount percentages via a ladder, a separate rule for child flat discounts, and a top-level combining function that enforces precedence rules—like ensuring only SINGLE ticket types qualify for the Reis discount while weekly and monthly tickets get none.
> 
> I'm sketching out an interface that takes traveler category, ticket type, zone validity, purchase time, and purchase history to determine the applicable discount, though I'm realizing the ticket type constraint (SINGLE only) might be better enforced at the dispatch level rather than coupling it into the core calculation logic.
> 
> Actually, since the feature is specifically about single-ticket purchases, the calculator should only ever be invoked for those—so I can simplify the signature to exclude ticket type and just pass traveler category, zone validity, purchase time, and past purchases. The past purchase history will need filtering logic to count only SINGLE tickets when applying the multi-purchase rules. I'm structuring this with a trip counter function that filters past purchases by matching traveler category and counts recent single-ticket trips, then a discount ladder that maps trip counts to percentages (only for adult/senior tiers, since children get a flat 20%), and finally a top-level calculator that combines these. The entry point needs to handle the special case where children always receive their fixed discount regardless of purchase history. culator's job, not the ladder's). The 40% cap works by taking the floor of trip count divided by 5, multiplying by 5, then capping at 40 — so count=5 gives 5%, count=10 gives 10%, and anything at count=40 or above maxes out at 40%. I'll design boundary test rows to verify this: count=4 should yield 0%, count=5 should yield 5%, count=9 should stay at 5%, count=10 should jump to 10%, count=39 should be 35%, and count=40 should hit the 40% cap.
> 
> For count=45, the formula would naturally produce 45%, but the cap brings it back down to 40%, which is a good explicit test of the capping logic. Since we're always working within a trailing 30-day window, the discount naturally levels off after that period regardless.
> 
> Now I need to define the trip-count table with columns for purchase time, traveler category, and the list of past purchases within that window to calculate the trip count. I should document the key assumption that only single-ticket purchases count toward this metric.
> 
> For the 30-day window boundary, I'm settling on inclusive of the lower bound: a purchase qualifies if it occurred at least 30 days ago but before the current purchase time. This means a ticket bought exactly 30 days prior counts, while one from 30 days and a minute ago doesn't. Zone information doesn't factor into the count itself, though that's validated elsewhere in the calculator.
> 
> Now I need to clarify the discount logic: the new ticket being purchased doesn't count toward its own history, so the fifth purchase in the trailing window is the first one eligible for a non-zero discount. The discount applies starting with that fifth ticket. Days, measured at the time of purchase, includes the current ticket being bought—ticket number five getting the first discount confirms this. So the trip count should be the number of past purchases within the 30-day window plus one for the current purchase, then mapped to the discount ladder where trip 5 gets 5%, trip 10 gets 10%, and so on. Now I'm thinking about the cleanest way to structure this logic.
> 
> I could have a count function return only past purchases and then add one when applying the ladder, or I could have the derivation function directly compute the trip number for this purchase—which is the actual value the system needs to expose for traceability. I'll go with the latter approach and create a function that returns the trip number directly, representing this purchase's ordinal position within the trailing 30-day window.
> 
> Now I'm setting up the test structure: one table to validate the counting and filtering logic against window boundaries and category filters, another to verify the discount ladder mapping at the key thresholds, and a third to test the full calculator that dispatches based on traveler category and uses the trip number to determine discounts for adults and seniors.
> 
> I'm realizing I need to split this into layers to avoid testing too high in the stack—I don't want the calculator tests to require fabricating entire purchase histories just to derive a trip number. So I'll have a pure arithmetic function that maps trip numbers to discounts, a separate analyzer that counts trips from raw history, and then the top-level entry point that ties them together. I could minimize history fabrication by passing trip number directly to the calculator, but that would sidestep the PurchaseHistoryRepository interface that's supposed to be integrated. Instead, I'll keep the top-level combining table lean—just one or two ADULT/SENIOR rows with minimal history to demonstrate the dispatch routing without re-testing the entire ladder logic. For the test table, I'll use minimal purchase history—just a couple of entries—to show the correct dispatch logic and confirm that zone doesn't affect the discount, rather than exhaustively retesting the entire ladder. A small manual list is cleaner than loop-generated data. Now I need to handle the CHILD ticket type, which applies a flat 20% discount only.
> 
> Since the calculator is specifically for single ticket purchases, I don't actually need a ticketType parameter in the DiscountCalculator itself—it's always SINGLE by definition. The ticketType only matters in PastPurchase for filtering history. For the decreasing discount rule, the trip number gets recomputed fresh each time from the trailing window, so I don't need to track previous discount state.
> 
> Now I'm planning the file structure. The tests will call these classes directly, so I'll create concrete utility classes rather than interfaces—something like DiscountCalculator as a concrete class with the methods the tests expect.
> 
> For naming, I'm thinking of three main classes: SingleTicketTripCounter to track how many trips a traveler has made, ReisDiscountLadder to map trip numbers to discount percentages, and ReisDiscountCalculator to compute the final discount based on traveler category and zone validity.
> 
> On the design question of whether ReisDiscountCalculator should accept a PurchaseHistoryRepository or a raw List—I'm leaning toward the list directly since it keeps the calculator decoupled and easier to unit test without mocking, and the caller can handle fetching from the repository.
> 
> Now I need to build the type converter for the PastPurchase list, mapping table fields like purchasedAt (as a LocalDateTime string), ticketType (enum), and travelerCategory (enum, optional). I should include the travelerCategory explicitly in the map so I can properly filter to matching categories when calculating trip counts, with sensible defaults for the common case. The function signature makes sense since the new purchase hasn't been added to history yet, so I need to pass the traveler category separately. For the test table, I'm mapping out scenarios with purchase time, traveler category, past purchases as a list of maps with at/type/category fields, and the expected trip number. The converter needs to map those map entries to PastPurchase objects, handling the field names and defaults appropriately.
> 
> I'll require explicit category values in each row for clarity when testing the filtering logic, though I can default to ADULT elsewhere. Zone will default to ZONE_1 in the converter since it's required by PastPurchase but doesn't affect the trip count logic—it's not part of the black-box contract, so it doesn't need to be a table column.
> 
> Now I'm sketching out the concrete test rows based on the boundary conditions: first scenario has no past purchases expecting trip number 1, another has three past purchases within the window expecting trip number 4.
> 
> Continuing with the remaining test cases: four purchases within the window should yield trip number 5, a purchase exactly at the 30-day boundary should count, one just past the boundary should be excluded, and I need to verify that non-DAILY ticket types like WEEKLY or MONTHLY are filtered out regardless of timing.
> 
> I'm also considering how the traveler category field acts as an identity proxy—the interface doesn't explicitly filter by person, so I'll need to document that assumption clearly. When the history contains a mix of qualifying and non-qualifying purchases, the count should sum only the valid ones.
> 
> For the window boundary, I'm defining it as [purchaseTime - 30 days, purchaseTime), meaning a past purchase counts if it occurred at least 30 days ago up to but not including the current purchase time.
> 
> Now I'm working through test scenarios... Generating test cases... excluded by the 30-day window). So the "Mixed history" case should have a trip number of 2, not 3. Let me also verify the other test cases—the three single tickets scenario correctly yields 4, and I should double-check that rows 3 and 4 properly test the boundary conditions around the 30-day cutoff.
> 
> Now I'm looking at whether rows 5 and 6 are redundant. Both test non-SINGLE ticket types (WEEKLY and MONTHLY), and since they're both excluded for the same reason, I should collapse them into a single row using a value set for the ticket type instead of maintaining two separate rows.
> 
> But wait—I need to check if value sets actually work when nested inside a list-of-maps structure like this. The spec indicates that value sets create cartesian products when used as a whole column value, but here the type field is nested within a map element inside a list. Using curly braces inside that nested field would be interpreted as a collection literal rather than a placeholder for alternatives, which doesn't make sense for a single enum field. So it's cleaner to keep them as two separate rows since the value-set mechanism doesn't apply cleanly to nested fields.
> 
> Actually, I can simplify this—since WEEKLY and MONTHLY both represent "non-SINGLE" ticket types, they're testing the same rule behavior. I should collapse this to just one row with WEEKLY as the representative case and drop the MONTHLY row to avoid redundancy. Let me rebuild the table with this fix and the other corrections I identified.
> 
> For the trip counter test table, I'm settling on seven rows: first-ever ticket, three singles already purchased, a purchase exactly at the 30-day boundary, one just past it (excluded), a non-single ticket type, another traveler's ticket, and mixed history. Now I'm moving to the discount ladder function—I need to test the boundary conditions where discounts kick in. I'm setting up scenarios for trip numbers 1, 4, 5, 9, and continuing into the second tier to verify the percentage increases correctly at each threshold.
> 
> Checking the floor division logic: trip 39 gives 35%, trip 40 hits the cap at 40%, and trip 45 stays capped at 40% rather than climbing higher. One row demonstrating the cap clipping is enough to show it works without being redundant.
> 
> Now moving to the DiscountCalculator table that handles category routing... no recent purchases, while adults and seniors follow a tiered discount structure that activates at four trips within the window. The test cases confirm that seniors use the same ladder-based logic as adults, and that zone selection doesn't affect the discount calculation for any category. Generating test scenarios table... Row 2 effectively demonstrates that child discounts ignore travel history—a distinct constraint from row 1, since it uses the same 20% rate but with substantial purchase history that would trigger tier 1 for adults. Now I need to construct the actual past purchases list for the adult/senior row, ensuring I have at least 4 qualifying entries to reach trip number 5, and I need to verify the category field matches the requirements.
> 
> The issue is that if the past purchase entries are hardcoded with a fixed category (say ADULT) but the row itself uses a value set {ADULT, SENIOR}, then when the row expands to SENIOR, the TripCounter filter won't match those ADULT-categorized purchases, resulting in 0% instead of the expected 5%—which would break the test.
> 
> I'm considering two approaches: either remove the category filter from TripCounter entirely and assume purchase history is already scoped to a single account/person, which would eliminate the need for the TravelerCategory parameter and the test case about other travelers' tickets not counting; or reconsider why the travelerCategory field exists on PastPurchase entries in the first place—maybe it's there for a different reason than filtering, since the spec says purchases are "for yourself." The issue is that value sets in TableTest expand independently per column, so I can't use them to keep category and past-purchase category correlated across different columns or nested structures. I need two explicit rows instead—one for ADULT and one for SENIOR—each with matching category values in their history, even though it means some redundancy. This ensures the data stays consistent rather than trying to force a value set that would break the correlation. I could simplify this whole problem by making the trip counter not filter by traveler category at all, assuming the purchase history passed to the discount calculator is already scoped to a single person. That's more realistic for a typical ticketing app and removes the need for that "another traveler's ticket" test case entirely. I'd keep the travelerCategory field on the PastPurchase table for domain completeness and potential use elsewhere, but document that it's not actually used by the trip counting logic.
> 
> Defining the simplified tripNumberInTrailingWindow method... Days ago excluded from the count, non-single tickets in the window don't qualify, and mixed history only counts the qualifying trips. For the mixed scenario with purchases on 07-20 (single, qualifies), 07-21 (weekly, excluded), and 06-01 (single, outside window), that gives one qualifying trip for trip number 2. So the table has six rows now, which is cleaner and more consistent. For the DiscountCalculator combining table, the ADULT/SENIOR ladder rows don't need the category field in their past purchases.
> 
> The PastPurchase record's category field still needs to be populated in the converter even if it's unused, so I'll just default it to ADULT arbitrarily—it doesn't matter since it won't be referenced. Now the combining table shows scenarios like children getting a flat 20% discount regardless of zone or travel frequency, while adults and seniors with no recent trips get no discount, and those reaching the first threshold get the appropriate rate.
> 
> For the "4 recent single tickets" in the map literal, I need to pick four qualifying SINGLE tickets within the last 30 days from the test date—something like July 5th, 12th, 19th, and 26th all as SINGLE type. The converter will need to supply defaults for fields not explicitly given in the map (defaulting ticketType to SINGLE, category to ADULT, zone to ZONE_1) unless overridden. I'll rename the map keys to "purchasedAt" and "ticketType" for better clarity and alignment with the actual PastPurchase field names.
> 
> Defining the converter method signature... Deciding whether SingleTicketTripCounter should be an interface or a class—leaning toward a class with a public method that throws UnsupportedOperationException for now, though the project's pattern of using interfaces for exposing functionality suggests that might be the better approach. Defining the interfaces and stub implementations... The tests are meant to fail initially since the implementations throw UnsupportedOperationException — that's the expected "red" phase of TDD. I'm reconsidering the design: the main DiscountCalculator should probably be an interface since it's the contract the new code needs to fulfill, but the supporting classes for individual concerns could be concrete implementations rather than interfaces, since they're only used internally and each needs to be separately testable. I'll finalize the three class names—SingleTicketTripCounter, ReisDiscountLadder, and ReisDiscountCalculator as the main entry point—then create the corresponding test files with stub methods throwing UnsupportedOperationException, including a shared @TypeConverter for PastPurchase across the test suite.
> 
> Actually, I need to think through the converters more carefully: TripCounterTest expects plain int trip numbers (no converter needed), but LadderTest and CalculatorTest both work with DiscountPercentage records, so I'll need a @TypeConverter that converts plain int text to DiscountPercentage instances in those test classes.
> 
> Rather than duplicating converters, I'll create a shared TestConverters utility class with static converter methods and reference it via @TypeConverterSources across the test classes that need it.
> 
> Writing converter class... Finishing the converter method for discount percentages, then I need to add the @TypeConverterSources annotation to each test class and make sure everything is public for proper TypeConverter discovery.
> 
> Now I'm setting up the first test file with the proper imports and structure, including the test class annotation and a private instance of the counter being tested.
> 
> Writing test table... Generating test cases... I'm verifying the boundary conditions more carefully: a purchase exactly 30 days ago should be included, while one second earlier gets excluded. The test cases with three single tickets all fall within the window and correctly yield a trip number of 4, and the mixed history scenario properly filters out the weekly ticket and the purchase before the cutoff, leaving just one qualifying purchase. The six test rows cover the essential cases: the base case, accumulation, both window boundaries, type filtering, and mixed scenarios.
> 
> Now moving to the discount ladder test file, which validates how trip numbers map to discount percentages for adult and senior travelers.
> 
> Generating markdown table... I'm realizing the step and cap values are actually fixed business rules that don't vary across test cases, so including them as columns might be unnecessary overhead. But the "Make Thresholds Visible" principle suggests I should include them anyway since the discount logic depends on these thresholds—both the increment per tier (5%) and the maximum cap (40%). Let me add both as columns to make the business rules explicit. The unused parameters feel awkward, but the guidance prioritizes making thresholds visible in the table for reader understanding, so I'll include the policy columns even if the method logic doesn't reference them directly. I'm leaning toward keeping just the Cap % as the key threshold column since that's what the boundary test cases actually exercise. I'm deciding whether to include these policy constants as actual columns in the test table even though they don't vary, since the test method signature requires a parameter per column—I could pass them as unused parameters just for documentation clarity, which is a bit unconventional but would make the policy thresholds explicit in the table structure. The cap of 40 is truly a universal constant in the Reis policy with no variability across different scenarios, unlike the crew-size-dependent limits in the skill guide example — so including it as a separate column feels like over-engineering when the naturally-computed ladder value already demonstrates hitting that cap in the relevant test row.
> 
> But here's the tension: without the cap explicitly shown as a column, a reader can't easily see why 45 trips doesn't produce 45% like the pattern from earlier rows suggests — they'd have to notice the anomaly and infer the cap exists, whereas making it a visible column removes all ambiguity and immediately clarifies why 45 gets capped to 40. if the SUT doesn't accept the cap as a parameter, I can't actually pass it through to the method being tested, so the column would remain purely documentary rather than actively used in the assertion logic.
> 
> I'm leaning toward skipping the explicit threshold columns altogether since the constants are fixed system-wide and not configurable. The test description already conveys the general bounds, and the boundary cases themselves tell the story clearly — the jump from 39→35 following the linear pattern, then 40→40 also fitting, and finally 45→40 breaking the pattern unmistakably signals the cap enforcement without needing an extra column to spell it out.
> 
> I'll keep the table simple but add the cap value explicitly in the description: "Reis discount increases by 5 percentage points for every 5 qualifying trips in the trailing 30-day window and is capped at 40%." This states the business rule at a level a domain expert would recognize, not the implementation mechanics.
> 
> Generating markdown table... Writing test class... Generating test table... The trip number is a primitive int so the default conversion handles it, but for the DiscountPercentage parameter I need a custom TypeConverter that parses the cell text into the right format. Traveler Category uses built-in enum conversion too. So I only need custom converters for PastPurchase and DiscountPercentage. The Purchase Time column with ISO 8601 format also works with built-in LocalDateTime conversion, no custom converter needed there. Inside the PastPurchase map though, I'm manually parsing the "purchasedAt" field string into a LocalDateTime object.
> 
> Since the map converter receives Map<String, String> values, I need to handle the defaults for fields that might not be specified in the test data — travelerCategory defaults to "ADULT" and zoneValidity defaults to "ZONE_1", while ticketType uses getOrDefault to handle missing entries. Now I'm ready to write out all the files.
> 
> I'm also verifying that the method signatures match the column order from each test scenario — the Purchase Time test has purchaseTime, pastPurchases, and tripNumber; the Ladder test has tripNumber and discount; and the Calculator test has travelerCategory, zone, purchaseTime, pastPurchases, and discount.
> 
> Writing stub classes... Completing the interface implementation...
> 
> Now I'm reconsidering the design — should SingleTicketTripCounter and ReisDiscountLadder also be interfaces following the same pattern? I think I'll keep DiscountCalculator as the main public contract interface (similar to how PurchaseHistoryRepository works as the system boundary), while leaving the other two as concrete internal helper classes for simplicity.
> 
> Let me set up the implementation plan: add the tabletest-junit dependency to the build, then create the three table-driven test stubs for the trip counter, discount ladder, and calculator, run them to verify they compile and fail as expected, and finally format everything for consistency.

Now I understand the domain model. I'll add the TableTest dependency, then write three focused test tables — one for the 30-day trip-count derivation, one for the discount ladder arithmetic, and one combining table for category dispatch (child flat vs. adult/senior ladder) — plus compilable stub classes.

**Edit ./build.gradle (edit)**

Now the stub main classes and the shared test converters, then each TableTest file.

**Write ./src/main/java/com/example/SingleTicketTripCounter.java (12 lines)**

**Write ./src/main/java/com/example/ReisDiscountLadder.java (9 lines)**

**Write ./src/main/java/com/example/DiscountCalculator.java (9 lines)**

**Write ./src/main/java/com/example/ReisDiscountCalculator.java (13 lines)**

**Write ./src/test/java/com/example/TestConverters.java (25 lines)**

Now the trip counter table test.

**Write ./src/test/java/com/example/SingleTicketTripCounterTest.java (38 lines)**

**Write ./src/test/java/com/example/ReisDiscountLadderTest.java (35 lines)**

The interface and stub for `DiscountCalculator` are already in place from the earlier batch. Now the combining test.

**Write ./src/test/java/com/example/ReisDiscountCalculatorTest.java (34 lines)**

Now let's verify the tables parse correctly and the project compiles.

Tables parsed fine — the script just wants to realign columns. Let me apply the formatting.

_[~156 thinking tokens (6 deltas)]_

> (thinking) The network access issue isn't a blocker since the formatter just returns the table unchanged if it can't parse anyway. Rather than trying to verify the parsing through that tool, I'll compile the project with gradle to validate everything works correctly—that's the definitive test.

_[~125 thinking tokens (6 deltas)]_

> (thinking) I don't have a Gradle wrapper available and the network seems limited since the format-table script failed to download, but let me try running a Gradle build anyway—the JUnit and tabletest dependencies might already be cached locally.

_[~134 thinking tokens (6 deltas)]_

> (thinking) The compilation succeeded, but I'm seeing test failures in SingleTicketTripCounterTest and ReisDiscountCalculatorTest due to parsing errors in the test tables themselves. The ladder test failures are expected since that functionality isn't implemented yet. I need to dig into what's causing those table parse exceptions.

Compiles cleanly. Two tables have a parse error (`TableTestParseException`) — let's get details and fix them.

_[~199 thinking tokens (5 deltas)]_

> (thinking) The problem is that the datetime value inside the map contains colons from the time component, which interferes with the map's colon-based parsing syntax. I need to quote the datetime string to escape those colons and allow the map to parse correctly.

Found it — the datetime values inside the map cells (`2026-07-05T08:00:00`) contain colons, which conflicts with the map `key: value` syntax. Those values need to be quoted.

I'll quote the datetime values wherever they appear as map entries (they contain colons, which collide with map key:value syntax).

**Edit ./src/test/java/com/example/SingleTicketTripCounterTest.java (edit)**

_[~320 thinking tokens (8 deltas)]_

> (thinking) I'm realizing that the scenario name "Child's flat discount ignores travel frequency" contains an apostrophe in "Child's", which means it needs to be quoted according to the rules for cells containing special characters like single quotes.
> 
> Actually, let me just simplify this by removing the apostrophe altogether and renaming it to "Child flat discount ignores travel frequency" to avoid any parsing complications.

I'll also avoid the apostrophe in the scenario name (which needs quoting per the same special-char rule) and quote the datetime values.

**Edit ./src/test/java/com/example/ReisDiscountCalculatorTest.java (edit)**

Now let's re-run the tests.

All tables now parse correctly — 26 tests total, all failing with the expected `UnsupportedOperationException` from the unimplemented stubs (the "red" state before implementation). This confirms the tables, converters, and stub API all compile and wire together correctly.

## Summary

Wrote three focused `@TableTest` classes for the Reis discount, each covering one rule, plus compilable stub implementations under `src/main/java/com/example`:

1. **`SingleTicketTripCounterTest`** (6 rows) — derives the trip's ordinal position from raw purchase history: 30-day window boundary (inclusive), SINGLE-only filtering, and combined filtering. Assumes history is already scoped to the purchasing traveler.
2. **`ReisDiscountLadderTest`** (8 rows) — pure arithmetic: 0% below tier 5, +5% per 5 trips, capped at 40% (boundary at 40 vs. 45 to prove clipping).
3. **`ReisDiscountCalculatorTest`** (4 rows, expanded to 12 via value sets) — the dispatch/combining rule: CHILD gets a flat 20% regardless of zone or travel frequency; ADULT/SENIOR route through the ladder; zone never matters.

Stub classes (`SingleTicketTripCounter`, `ReisDiscountLadder`, `ReisDiscountCalculator` implementing new `DiscountCalculator` interface) throw `UnsupportedOperationException` — no logic implemented yet, per your request. Added `tabletest-junit:1.2.2` to `build.gradle`.

Key assumptions (documented in each table's `@Description`):
- Purchase history passed in is already scoped to the purchasing traveler (no cross-traveler filtering).
- Only `SINGLE`-ticket past purchases count toward the trailing-window trip number.
- The 30-day window is inclusive of the instant exactly 30 days before purchase time.
- "Trip number" includes the ticket currently being purchased (so the 5th ticket = first discount, matching the spec).
- The "levels off" / "decreases if you travel less" behavior falls out naturally from recomputing the trailing count fresh each purchase — no extra state or table needed.

Ran `gradle test`: all 26 generated test cases compile and fail with `UnsupportedOperationException` as expected (red state, ready for implementation). One gotcha hit and fixed along the way: raw `LocalDateTime` values inside map cells (e.g. `[purchasedAt: 2026-07-05T08:00:00, ...]`) need quoting because the embedded colons collide with map `key: value` syntax.

