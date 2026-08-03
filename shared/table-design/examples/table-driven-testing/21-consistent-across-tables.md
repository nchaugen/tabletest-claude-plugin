Two parametrized tests in one file, one notation and one parser behind them:

```python
@pytest.mark.parametrize(("response_time"), [pytest.param("<50", id="healthy upstream")])
def test_answers_within_the_latency_budget(response_time):
    assert responder.latency() <= parse_latency(response_time)


@pytest.mark.parametrize(("report_time"), [pytest.param("<50", id="healthy upstream")])
def test_publishes_the_report_within_the_latency_budget(report_time):
    assert reporter.latency() <= parse_latency(report_time)
```

A bare `50` in the second test would leave the reader deciding whether it means a maximum or an exact
value, and a second copy of `parse_latency` would let the two drift apart without either failing.
