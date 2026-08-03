```go
tests := []struct {
    name          string
    daysSinceLast int
    minInterval   int
    eligible      bool
}{
    {"exactly at the interval", 90, 90, true},
    {"one day short", 89, 90, false},
}
```
