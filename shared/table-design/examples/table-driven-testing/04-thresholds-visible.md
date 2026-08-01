```go
tests := []struct {
    name          string
    daysSinceLast int
    minInterval   int
    eligible      bool
}{
    {"long-standing donor", 120, 90, true},
    {"exactly at the interval", 90, 90, true},
    {"one day short", 89, 90, false},
}
```
