`Tests/PlaylistsTests/PlaylistScreenTests.swift` tests which actions the playlist screen offers, with `@Test(arguments:)`. The package already depends on TableTest.

Convert it to `@TableTest`. The cases are hard to read today, so redesign the tables where that makes them easier to read.
