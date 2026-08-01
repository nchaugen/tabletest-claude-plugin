```go
tests := []struct {
    name      string
    binBefore Bin
    action    Action
    binAfter  Bin
    message   string
}{
    {"accept a labelled item", Bin{}, Deposit("cardboard"), Bin{"cardboard": 1}, "Accepted"},
    {"fill to the bulk limit", Bin{"cardboard": 1}, Deposit("cardboard"), Bin{"cardboard": 2}, "Accepted"},
    {"reject a mismatched item", Bin{"cardboard": 1}, Deposit("solvent"), Bin{"cardboard": 1}, "Wrong stream"},
}
```
