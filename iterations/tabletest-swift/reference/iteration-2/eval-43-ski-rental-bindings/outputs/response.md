Hand-authored reference answer for eval-43, from `products/claude-plugin/plans/slice-13-swift-skill-field-feedback.md` step 1. Not agent output. Must never be read
as a skill measurement.

**The fix.** `weightClassSetting` read `weightKg <= 80` for the 65–79 kg class, so a skier of
exactly 80 kg got 5.5 in place of 7.0. It now reads `weightKg < 80`.

**The test.** One case is added to `releaseSettings`: an intermediate skier of 80 kg, setting 7.0.
It fails against the old code and passes against the fix.

**Not converted.** The release-setting test stays a `@Test(arguments:)` over `(Skier, Double)`
tuples. Converting it to a `@TableTest` needs a column for each field of `Skier` and a conformance
on `Level` in the app, which is a change apart from this fix. It can be its own commit.
