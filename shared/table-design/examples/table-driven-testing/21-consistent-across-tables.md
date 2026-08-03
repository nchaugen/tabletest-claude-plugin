Two parametrized tests in one file, one notation and one parser behind them:

```python
@pytest.mark.parametrize(("upstream_latency", "response_time"), [
    pytest.param("<10",  "<50",  id="healthy upstream"),
    pytest.param("<400", "<500", id="upstream throttled"),
])
def test_answers_within_the_latency_budget(upstream_latency, response_time):
    assert responder.latency(upstream_latency) <= parse_latency(response_time)


@pytest.mark.parametrize(("upstream_latency", "report_time"), [
    pytest.param("<10",  "<50",  id="healthy upstream"),
    pytest.param("<400", "<500", id="upstream throttled"),
])
def test_publishes_the_report_within_the_latency_budget(upstream_latency, report_time):
    assert reporter.latency(upstream_latency) <= parse_latency(report_time)
```

A bare `50` in the second test would leave the reader deciding whether it means a maximum or an exact
value, and a second copy of `parse_latency` would let the two drift apart without either failing.
