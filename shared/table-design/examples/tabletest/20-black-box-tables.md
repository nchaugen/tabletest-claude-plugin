```
Scenario                | Humidity % | Temp (C) | Vent Position?
Warm and damp           | 80         | 28       | OPEN
Within target range     | 55         | 21       | CLOSED
```

Observable readings in, observable position out. A `sensorPollCount` or `controllerInitialised`
column would be internal state, not the contract.
