```go
tests := []struct {
    name              string
    dutyHours         float64
    extraRestRequired bool
}{
    {"at the duty limit", 13, false},
    {"just past the duty limit", 13.5, true},
    // {"well past the duty limit", 20, true},  <- redundant: 13.5 already proved it
}
```
