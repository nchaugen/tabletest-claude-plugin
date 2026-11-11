Two tables in one suite, one notation for the concept they share:

```swift
@TableTest("""
    Scenario           | Upstream Latency | Response Time?
    Healthy upstream   | <10              | <50
    Upstream throttled | <400             | <500
    """)
func answersWithinTheLatencyBudget(upstreamLatency: Latency, responseTime: Latency) { ... }

@TableTest("""
    Scenario           | Upstream Latency | Report Time?
    Healthy upstream   | <10              | <50
    Upstream throttled | <400             | <500
    """)
func publishesTheReportWithinTheLatencyBudget(upstreamLatency: Latency, reportTime: Latency) { ... }

extension Latency: TableTextDecodable {
    public init?(tableText: String) { ... }
}
```

A bare `50` in the second table would leave the reader deciding whether it means a maximum or an
exact value. **A type converts itself, once**: `Latency` has one `TableTextDecodable` conformance,
so every column of that type reads the same notation, and a second notation for it cannot exist.
