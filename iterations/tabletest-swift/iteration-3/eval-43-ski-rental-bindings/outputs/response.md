I fixed the bug, and all tests pass, including the two new boundary cases.

**Fix:** In `Sources/SkiRental/Binding.swift`, `weightClassSetting` checked `weightKg <= 80`, so an 80 kg skier landed in the 65–79 kg class (5.5). It now checks `weightKg < 80`, which matches the doc comment ("80 kg and over"), so 80 kg gets the heavier class (7.0).

**Tests:** I added two rows to the `releaseSettings` table in `Tests/SkiRentalTests/BindingTests.swift`:
- 79 kg, intermediate gives 5.5. This is the top of the lighter class.
- 80 kg, intermediate gives 7.0. This is the case you reported.

I assumed the doc comment states the intended rule and the code was wrong. I used an intermediate level so the level adjustment doesn't affect the result.

I added the rows to the existing array-based test, not a `@TableTest`, because `Level` is not a string-backed enum, so I wasn't sure TableTest could convert it.