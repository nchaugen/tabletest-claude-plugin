```
Scenario                  | Duty Hours | Extra Rest Required?
At the duty limit         | 13         | false
Just past the duty limit  | 13.5       | true
Well past the duty limit  | 20         | true      <- redundant: 13.5 already proved it
```

Keep the straddling pair; drop the {{row}} further out.
