```csharp
[Theory]
[MemberData(nameof(SortingCases))]
public void SortsItemsIntoStreams(string scenario, string[] items, Dictionary<string, string[]> streams)
    => Assert.Equal(streams, Sorter.Sort(items));
```

`streams` stays a real dictionary compared against what the sorter returns — not a joined string.
