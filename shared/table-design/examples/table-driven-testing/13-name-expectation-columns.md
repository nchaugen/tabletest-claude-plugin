```csharp
[Theory]
[MemberData(nameof(SortingCases))]
public void SortsItemsIntoStreams(string scenario, string[] items, Dictionary<string, string[]> streams)
    => Assert.Equal(streams, Sorter.Sort(items));
```

`streams` stays a real dictionary compared against what the sorter returns — not a joined string.

The `?` marks outputs only — never an input, however yes/no it looks. In a field or parameter name
the same rule applies to the expectation: `repeat_donor` in, `fee_eur` expected — not
`repeat_donor_q`.
