Two tables in one response, one notation for the concept they share:

| Scenario           | Upstream Latency | Response Time? |
|--------------------|------------------|----------------|
| Healthy upstream   | <10              | <50            |
| Upstream throttled | <400             | <500           |

| Scenario           | Upstream Latency | Report Time? |
|--------------------|------------------|--------------|
| Healthy upstream   | <10              | <50          |
| Upstream throttled | <400             | <500         |

A bare `50` in the second table would leave the reader deciding whether it means a maximum or an
exact value.
