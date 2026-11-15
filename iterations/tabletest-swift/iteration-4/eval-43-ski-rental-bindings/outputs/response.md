An 80 kg skier now gets the heavier class's setting (7.0 instead of 5.5). `swift test` passes.

- **Fix:** in `Sources/SkiRental/Binding.swift`, `weightClassSetting` used `weightKg <= 80` for the 65–79 kg class. It is now `weightKg < 80`, which matches the doc comment ("80 kg and over").
- **Tests:** I added two rows to the existing `releaseSettings` table in `Tests/SkiRentalTests/BindingTests.swift`. One is an intermediate skier at exactly 80 kg, expecting 7.0. The other is an intermediate skier at 79 kg, expecting 5.5, to pin the boundary from the other side.

I read "the setting of the lighter class" as the 65–79 kg class and took the doc comment as the intended rule. I added rows to the existing parameterised test instead of writing a `@TableTest`. `Skier` and `Level` have no string conversion, so a table would have needed a type converter.