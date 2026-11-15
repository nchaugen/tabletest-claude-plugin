The fault is fixed in `Binding.swift`, and the 80 kg case is a case of the release-setting test
that already exists. The tests pass.

## The fix

`weightClassSetting` reads `weightKg <= 80` where the class of 80 kg and over starts. It becomes
`weightKg < 80`, so a skier of exactly 80 kg gets 7.0 (an intermediate), 6.5 (a beginner) or 8.0
(an expert).

## The test

The release-setting test is a `@Test(arguments:)` over `releaseSettings`, a collection of
`(Skier, Double)` tuples. The 80 kg case belongs to it, so either:

- **One tuple is added to `releaseSettings`**, such as
  `(Skier(heightCm: 178, weightKg: 80, level: .intermediate), 7.0)`. The table stays a
  `@Test(arguments:)`, and the answer says so: converting it to a `@TableTest` needs a conformance
  on `Level` in the app (a column per field of `Skier`, by *Structs*), which is work apart from the
  fix. A case at 79 kg beside it is good too.
- **Or the test is converted to a `@TableTest`** — one column for each field of `Skier`, with
  `Level` given one conformance word (`CaseIterable`) in the app — and the answer states the
  conversion as a step of its own, apart from the fix. The six original cases stay, with the same
  values, and the 80 kg case is a row.

## Wrong

- A new plain `@Test` for 80 kg beside the `@Test(arguments:)` that tests the same function.
- A conversion mixed into the fix with no word that it happened.
- An original case dropped or changed.

## Equally good

The height of the new skier, its level, and a second case at 79 kg. The boot-sole table does not
take part.
