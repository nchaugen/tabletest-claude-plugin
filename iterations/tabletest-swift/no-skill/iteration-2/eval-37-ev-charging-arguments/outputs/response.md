I converted all three tests in `ChargingFeeTests.swift` to `@TableTest`, and `swift test` passes: 8, 3 and 3 cases.

- **Fee per session:** the eight tuples are now rows with `Scenario | Charger | Minutes | Fee?`. I gave each row a scenario name describing its condition, such as "Last free minute" and "Start of the second quarter". Scenario names weren't part of your original tuples, so the wording is mine.
- **Unread meter:** the `zip([...], [...])` pairing is now a three-row `Charger | Fee?` table, so each charger sits next to its maximum.
- **Free for five minutes:** `Charger.allCases` became one row with the value set `{ac, fast, ultrafast}` and `Minutes = 5`. This keeps one case per charger, but a charger added later would no longer be covered automatically. Add it to the set by hand.

I kept the three tests separate because they test different rules. The value-set row could have replaced the `fast | 5 | 0` row in the first table, but I left both so the first table still shows the 5/6-minute boundary on its own.

I also renamed the second and third test titles to action phrases, for example "Charges the session maximum for an unread meter". I renamed the `euros` parameter to `fee` to match the `Fee?` column header.